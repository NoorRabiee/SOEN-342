package main.java.ats.domain;

import java.time.LocalDate;

public class Appointment {
    private static int nextId = 1;

    private final int id;
    private final Patient patient;
    private final Availability availability;
    private AppointmentStatus status;

    // requestAppointment: creates the appointment (REQUESTED) and occupies the slot
    public Appointment(Patient patient, Availability availability) {
        if (patient == null || availability == null) {
            throw new IllegalArgumentException("patient and availability are required");
        }
        if (availability.isBooked()) {
            throw new IllegalStateException("Time slot unavailable");
        }
        this.patient = patient;
        this.availability = availability;
        this.status = AppointmentStatus.REQUESTED;
        this.id = nextId++;
        availability.occupy(this);
    }

    public int getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Availability getAvailability() {
        return availability;
    }

    public HealthcareProfessional getProfessional() {
        return availability.getProfessional();
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void confirm() {
        if (status != AppointmentStatus.REQUESTED) {
            throw new IllegalStateException("only a REQUESTED appointment can be confirmed (is " + status + ")");
        }
        status = AppointmentStatus.CONFIRMED;
    }

    /** Records the patient's arrival for an appointment scheduled today. */
    public void registerArrival() {
        if (status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("only a CONFIRMED appointment can be marked ARRIVED (is " + status + ")");
        }
        if (!availability.getDate().equals(LocalDate.now())) {
            throw new IllegalStateException("appointment is not scheduled for today");
        }
        status = AppointmentStatus.ARRIVED;
    }

    /** Completes an appointment after its consultation has taken place. */
    public void complete() {
        if (status != AppointmentStatus.ARRIVED) {
            throw new IllegalStateException("only an ARRIVED appointment can be completed (is " + status + ")");
        }
        status = AppointmentStatus.COMPLETED;
    }

    public void cancel() {
        if (status != AppointmentStatus.REQUESTED && status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("only a REQUESTED or CONFIRMED appointment can be cancelled (is " + status + ")");
        }
        status = AppointmentStatus.CANCELLED;
        availability.release();
    }
}
