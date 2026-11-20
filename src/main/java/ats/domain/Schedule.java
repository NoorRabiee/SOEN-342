package main.java.ats.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
    private final HealthcareProfessional professional;
    private final List<Availability> availabilities = new ArrayList<>();

    public Schedule(HealthcareProfessional professional) {
        this.professional = professional;
    }

    public HealthcareProfessional getProfessional() {
        return professional;
    }

    /**
     * Creates one bookable slot
     * 
     * @throws IllegalArgumentException if the data is invalid
     * @throws IllegalStateException    if the slot overlaps an existing one
     */
    public Availability addAvailability(LocalDate date, LocalTime start, LocalTime end) {
        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("date, start and end are required");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start time must be before end time");
        }
        if (overlaps(date, start, end)) {
            throw new IllegalStateException("overlaps an existing availability");
        }
        Availability a = new Availability(this, date, start, end);
        availabilities.add(a);
        return a;
    }

    public boolean overlaps(LocalDate date, LocalTime start, LocalTime end) {
        for (Availability existing : availabilities) {
            if (existing.overlaps(date, start, end)) {
                return true;
            }
        }
        return false;
    }

    // Search Availability diagram: loop over av[j], isFreeWithin(startDate, endDate)
    public List<Availability> getFreeAvailabilities(LocalDate startDate, LocalDate endDate) {
        List<Availability> slots = new ArrayList<>();
        for (Availability av : getAvailabilities()) {
            if (av.isFreeWithin(startDate, endDate)) {
                slots.add(av);
            }
        }
        return slots;
    }

    public List<Availability> getAvailabilities() {
        List<Availability> copy = new ArrayList<>(availabilities);
        Collections.sort(copy);
        return copy;
    }
}
