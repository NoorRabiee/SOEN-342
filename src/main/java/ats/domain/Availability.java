package main.java.ats.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Availability implements Comparable<Availability> {
    private final Schedule schedule;
    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private Appointment appointment;

    Availability(Schedule schedule, LocalDate date, LocalTime start, LocalTime end) {
        this.schedule = schedule;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public HealthcareProfessional getProfessional() {
        return schedule.getProfessional();
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public boolean isBooked() {
        return appointment != null;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    // Search Availability diagram: free = within the date range and not occupied by an Appointment
    public boolean isFreeWithin(LocalDate startDate, LocalDate endDate) {
        return !isBooked() && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // Make Availability diagram
    public boolean overlaps(LocalDate otherDate, LocalTime otherStart, LocalTime otherEnd) {
        return date.equals(otherDate) && otherStart.isBefore(end) && start.isBefore(otherEnd);
    }

    void occupy(Appointment a) {
        if (isBooked()) {
            throw new IllegalStateException("Time slot unavailable");
        }
        this.appointment = a;
    }

    void release() {
        this.appointment = null;
    }

    @Override
    public int compareTo(Availability o) {
        int c = date.compareTo(o.date);
        if (c == 0) {
            c = start.compareTo(o.start);
        }
        if (c == 0) {
            c = getProfessional().getFullName().compareToIgnoreCase(o.getProfessional().getFullName());
        }
        return c;
    }
}
