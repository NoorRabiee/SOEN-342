package main.java.ats.catalog;

import main.java.ats.domain.Availability;
import main.java.ats.domain.HealthcareProfessional;
import main.java.ats.domain.Schedule;
import main.java.ats.util.TextUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleCatalog {
    private final List<Schedule> schedules = new ArrayList<>();

    public void register(Schedule schedule) {
        schedules.add(schedule);
    }

    // The entire availability database (free and booked), sorted by date, start time, professional
    public List<Availability> getAllAvailabilities() {
        List<Availability> all = new ArrayList<>();
        for (Schedule s : schedules) {
            all.addAll(s.getAvailabilities());
        }
        Collections.sort(all);
        return all;
    }

    // Free slots only, any filter may be blank/null, and dates are inclusive
    public List<Availability> searchFree(String professionalNamePart, String specialtyPart,
            LocalDate from, LocalDate to) {
        List<Availability> out = new ArrayList<>();
        for (Availability a : getAllAvailabilities()) {
            HealthcareProfessional hp = a.getProfessional();
            if (a.isBooked()) {
                continue;
            }
            if (!TextUtil.isBlank(professionalNamePart)
                    && !TextUtil.containsAllTokens(hp.getDisplayName(), professionalNamePart)) {
                continue;
            }
            if (!TextUtil.isBlank(specialtyPart) && !hp.hasSpecialtyLike(specialtyPart)) {
                continue;
            }
            if (from != null && a.getDate().isBefore(from)) {
                continue;
            }
            if (to != null && a.getDate().isAfter(to)) {
                continue;
            }
            out.add(a);
        }
        return out;
    }
}
