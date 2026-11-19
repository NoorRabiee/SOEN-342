package main.java.ats.controller;

import main.java.ats.catalog.*;
import main.java.ats.domain.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Handles the system operations from the SSDs (GRASP controller)
 * The Console only talks to this class
 * Design class: it is not part of the domain model
 */
public class SystemController {

    private final HealthcareProfessionalCatalog professionals = new HealthcareProfessionalCatalog();
    private final MedicalSpecialtyCatalog specialties = new MedicalSpecialtyCatalog();
    private final PatientCatalog patients = new PatientCatalog();
    private final ScheduleCatalog schedules = new ScheduleCatalog();
    private final AppointmentCatalog appointments = new AppointmentCatalog();

    private final AvailabilityCsvImporter importer = new AvailabilityCsvImporter();
    private final AvailabilityCsvExporter exporter = new AvailabilityCsvExporter();

    // set-up
    // helpers (PoC convenience)

    // Returns the existing professional (adding the specialty if new) or creates one
    public HealthcareProfessional registerProfessional(String first, String last, String title, String specialtyName) {
        MedicalSpecialty specialty = (specialtyName == null || specialtyName.trim().isEmpty())
                ? null
                : specialties.getOrCreate(specialtyName);
        HealthcareProfessional hp = professionals.findByFullName(first, last);
        if (hp == null) {
            hp = professionals.create(first, last, title, specialty);
            schedules.register(hp.getSchedule());
        } else {
            hp.addSpecialty(specialty);
        }
        return hp;
    }

    public Patient registerPatient(String first, String last, String insuranceNum, LocalDate dob, String phone) {
        return patients.add(new Patient(first, last, insuranceNum, dob, phone));
    }

    // Make Availability

    /**
     * Creates one availability slot of {@code slotMinutes} for every day in [from,
     * to] that matches the pattern,
     * from {@code start} to {@code end}. Slots that overlap an existing one are
     * skipped.
     */
    public BatchResult makeAvailability(HealthcareProfessional hp, LocalDate from, LocalDate to,
            RepetitionPattern pattern, LocalTime start, LocalTime end, int slotMinutes) {
        if (hp == null || from == null || to == null || pattern == null || start == null || end == null) {
            throw new IllegalArgumentException("professional, dates, pattern and times are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("start date must not be after end date");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start time must be before end time");
        }
        if (slotMinutes <= 0) {
            throw new IllegalArgumentException("slot length must be positive");
        }
        BatchResult result = new BatchResult();
        int startMin = start.getHour() * 60 + start.getMinute();
        int endMin = end.getHour() * 60 + end.getMinute();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (!pattern.matches(d)) {
                continue;
            }
            for (int m = startMin; m + slotMinutes <= endMin; m += slotMinutes) {
                try {
                    hp.getSchedule().addAvailability(d, LocalTime.of(m / 60, m % 60),
                            LocalTime.of((m + slotMinutes) / 60, (m + slotMinutes) % 60));
                    result.created();
                } catch (IllegalStateException overlap) {
                    result.skipped(null);
                }
            }
        }
        return result;
    }

    // Search
    // Availability

    public List<Availability> searchAvailability(String professionalNamePart, String specialtyPart,
            LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("start date must not be after end date");
        }
        return schedules.searchFree(professionalNamePart, specialtyPart, from, to);
    }

    // Book
    // Appointment (SSD operations)

    public List<Patient> searchPatient(String insuranceNum, LocalDate dob, String phone, String namePart) {
        return patients.search(insuranceNum, dob, phone, namePart);
    }

    // requestAppointment: status REQUESTED, slot occupied. Fails with "Time slot unavailable"
    public Appointment requestAppointment(Patient patient, Availability availability) {
        return appointments.add(new Appointment(patient, availability));
    }

    /** confirmAppointment: REQUESTED -> CONFIRMED. */
    public Appointment confirmAppointment(Appointment appointment) {
        appointment.confirm();
        return appointment;
    }

    // CSV

    public BatchResult importAvailability(Path file) throws IOException {
        return importer.importFrom(file, this);
    }

    public int exportAvailability(Path file) throws IOException {
        return exporter.exportTo(file, schedules);
    }

    // read access for the UI

    public List<HealthcareProfessional> getProfessionals() {
        return professionals.getAll();
    }

    public List<Patient> getPatients() {
        return patients.getAll();
    }

    public List<Appointment> getAppointments() {
        return appointments.getAll();
    }

    public List<Availability> getAllAvailabilities() {
        return schedules.getAllAvailabilities();
    }
}
