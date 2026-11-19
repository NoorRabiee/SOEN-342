package main.java.ats.domain;

import main.java.ats.util.TextUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

// Repetition pattern used by "Make Availability": daily, weekly on days, or monthly on days
public class RepetitionPattern {
    private final String description;
    private final Predicate<LocalDate> rule;

    private RepetitionPattern(String description, Predicate<LocalDate> rule) {
        this.description = description;
        this.rule = rule;
    }

    public static RepetitionPattern daily() {
        return new RepetitionPattern("every day", d -> true);
    }

    public static RepetitionPattern weekly(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("at least one day of the week is required");
        }
        Set<DayOfWeek> copy = EnumSet.copyOf(days);
        return new RepetitionPattern("weekly on " + copy, d -> copy.contains(d.getDayOfWeek()));
    }

    // Months that do not have the requested day (e.g. the 31st) are simply skipped
    public static RepetitionPattern monthly(Set<Integer> daysOfMonth) {
        if (daysOfMonth == null || daysOfMonth.isEmpty()) {
            throw new IllegalArgumentException("at least one day of the month is required");
        }
        for (int d : daysOfMonth) {
            if (d < 1 || d > 31) {
                throw new IllegalArgumentException("day of month must be between 1 and 31");
            }
        }
        Set<Integer> copy = new HashSet<>(daysOfMonth);
        return new RepetitionPattern("monthly on day(s) " + copy, d -> copy.contains(d.getDayOfMonth()));
    }

    public boolean matches(LocalDate date) {
        return rule.test(date);
    }

    // Parses "MON,WED" / "monday, friday"
    public static Set<DayOfWeek> parseDays(String text) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        for (String raw : text.split("[,; ]+")) {
            String t = raw.trim().toUpperCase(Locale.ROOT);
            if (t.isEmpty()) {
                continue;
            }
            boolean found = false;
            for (DayOfWeek d : DayOfWeek.values()) {
                if (d.name().equals(t) || (t.length() >= 3 && d.name().startsWith(t))) {
                    days.add(d);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("unknown day: " + raw);
            }
        }
        if (days.isEmpty()) {
            throw new IllegalArgumentException("at least one day of the week is required");
        }
        return days;
    }

    // Parses "1,15"
    public static Set<Integer> parseDaysOfMonth(String text) {
        Set<Integer> days = new HashSet<>();
        for (String raw : text.split("[,; ]+")) {
            if (TextUtil.isBlank(raw)) {
                continue;
            }
            try {
                days.add(Integer.parseInt(raw.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("not a number: " + raw);
            }
        }
        if (days.isEmpty()) {
            throw new IllegalArgumentException("at least one day of the month is required");
        }
        return days;
    }

    @Override
    public String toString() {
        return description;
    }
}