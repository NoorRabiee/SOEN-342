package main.java.ats.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Make Availability diagram: generates the dates that match the repetition pattern
public class RecurrenceRule {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final RepetitionPattern repetitionPattern;

    public RecurrenceRule(LocalDate startDate, LocalDate endDate, RepetitionPattern repetitionPattern) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.repetitionPattern = repetitionPattern;
    }

    public List<LocalDate> generateDates() {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (repetitionPattern.matches(d)) {
                dates.add(d);
            }
        }
        return dates;
    }
}
