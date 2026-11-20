package main.java.ats.ui;

import main.java.ats.controller.BatchResult;
import main.java.ats.controller.SystemController;
import main.java.ats.domain.*;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

// Command line menu: reads input, calls the SystemController, prints the result
public class Console {
    private final SystemController controller;
    private final Scanner in;
    private final PrintStream out;

    public Console(SystemController controller, Scanner in, PrintStream out) {
        this.controller = controller;
        this.in = in;
        this.out = out;
    }

    public void printBanner() {
        out.println("=== Appointment Tracking System ===");
    }

    public void preload(String csvPath) {
        importCsv(csvPath);
    }

    public void run() {
        while (true) {
            out.println("\n1. Import CSV  2. Export CSV  3. Make availability  4. Search availability");
            out.println("5. Add patient  6. Book appointment  7. Register arrival  0. Exit");
            String choice = ask("Choice");
            try {
                switch (choice) {
                    case "1": importCsv(ask("CSV file")); break;
                    case "2": exportCsv(); break;
                    case "3": makeAvailability(); break;
                    case "4": searchAvailability(); break;
                    case "5": addPatient(); break;
                    case "6": bookAppointment(); break;
                    case "7": out.println("Status: " + controller.registerArrival(askInt("Appointment id")).getStatus()); break;
                    case "0": return;
                    default: out.println("Unknown choice");
                }
            } catch (Exception e) {
                out.println("error: " + e.getMessage());
            }
        }
    }

    private void importCsv(String path) {
        try {
            BatchResult r = controller.importAvailability(Paths.get(path));
            out.println("Imported " + r.getCreated() + ", skipped " + r.getSkipped());
            for (String m : r.getMessages()) {
                out.println("  " + m);
            }
        } catch (Exception e) {
            out.println("error: " + e.getMessage());
        }
    }

    private void exportCsv() throws Exception {
        int n = controller.exportAvailability(Paths.get(ask("Output file")));
        out.println("Exported " + n + " availabilities");
    }

    private void listProfessionals() {
        for (HealthcareProfessional hp : controller.getProfessionals()) {
            out.println(hp.getId() + ". " + hp);
        }
    }

    private void makeAvailability() {
        listProfessionals();
        controller.selectProfessional(askInt("Professional id"));
        LocalDate from = LocalDate.parse(ask("From (YYYY-MM-DD)"));
        LocalDate to = LocalDate.parse(ask("To (YYYY-MM-DD)"));
        String type = ask("Repeat: daily / weekly / monthly");
        RepetitionPattern pattern;
        if (type.equals("weekly")) {
            pattern = RepetitionPattern.weekly(RepetitionPattern.parseDays(ask("Days (e.g. MON,WED)")));
        } else if (type.equals("monthly")) {
            pattern = RepetitionPattern.monthly(RepetitionPattern.parseDaysOfMonth(ask("Days of month (e.g. 1,15)")));
        } else {
            pattern = RepetitionPattern.daily();
        }
        LocalTime start = LocalTime.parse(ask("Start time (HH:MM)"));
        LocalTime end = LocalTime.parse(ask("End time (HH:MM)"));
        out.println("Preview: " + controller.defineAvailability(from, to, pattern, start, end));
        if (ask("Confirm? (y/n)").equals("y")) {
            controller.confirmAvailability();
            out.println("Availability created");
        }
    }

    private List<Availability> searchAvailability() {
        listProfessionals();
        int id = askInt("Professional id");
        LocalDate from = LocalDate.parse(ask("From (YYYY-MM-DD)"));
        LocalDate to = LocalDate.parse(ask("To (YYYY-MM-DD)"));
        List<Availability> slots = controller.searchAvailability(id, from, to);
        if (slots.isEmpty()) {
            out.println("No availability found");
        }
        for (int i = 0; i < slots.size(); i++) {
            Availability a = slots.get(i);
            out.println((i + 1) + ". " + a.getDate() + " " + a.getStart() + "-" + a.getEnd() + " " + a.getProfessional());
        }
        return slots;
    }

    private void addPatient() {
        String first = ask("First name");
        String last = ask("Last name");
        String ramq = ask("RAMQ (blank if none)");
        String phone = ask("Phone (blank if none)");
        Patient p = controller.registerPatient(first, last, ramq, null, phone);
        out.println("Added " + p);
    }

    private void bookAppointment() {
        List<Patient> patients = controller.searchPatient(ask("RAMQ (blank to skip)"), null,
                ask("Phone (blank to skip)"), ask("Name (blank to skip)"));
        if (patients.isEmpty()) {
            out.println("No patient found");
            return;
        }
        for (int i = 0; i < patients.size(); i++) {
            out.println((i + 1) + ". " + patients.get(i));
        }
        Patient patient = patients.get(askInt("Patient #") - 1);

        List<Availability> slots = searchAvailability();
        if (slots.isEmpty()) {
            return;
        }
        Availability slot = slots.get(askInt("Slot #") - 1);

        Appointment appt = controller.requestAppointment(patient, slot);
        controller.confirmAppointment(appt);
        out.println("Booked appointment #" + appt.getId() + " (" + appt.getStatus() + ")");
    }

    private String ask(String prompt) {
        out.print(prompt + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private int askInt(String prompt) {
        return Integer.parseInt(ask(prompt));
    }
}
