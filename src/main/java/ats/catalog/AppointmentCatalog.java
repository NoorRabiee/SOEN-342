package main.java.ats.catalog;

import main.java.ats.domain.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentCatalog {
    private final List<Appointment> appointments = new ArrayList<>();

    public Appointment add(Appointment a) {
        appointments.add(a);
        return a;
    }

    // Register Arrival diagram: appt := getAppointment(appointmentId)
    public Appointment getAppointment(int appointmentId) {
        for (Appointment a : appointments) {
            if (a.getId() == appointmentId) {
                return a;
            }
        }
        return null;
    }

    public List<Appointment> getAll() {
        return new ArrayList<>(appointments);
    }
}
