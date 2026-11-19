package main.java.ats.controller;

import main.java.ats.catalog.ScheduleCatalog;
import main.java.ats.domain.Availability;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AvailabilityCsvExporter {

    /** Exports every availability row using the same columns accepted by the importer. */
    public int exportTo(Path file, ScheduleCatalog schedules) throws IOException {
        if (file == null || schedules == null) {
            throw new IllegalArgumentException("file and schedules are required");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            writer.write("First Name,Last Name,Title,Specialty,Date,Start,End");
            writer.newLine();

            int count = 0;
            for (Availability availability : schedules.getAllAvailabilities()) {
                writer.write(csv(availability.getProfessional().getFirstName()));
                writer.write(',');
                writer.write(csv(availability.getProfessional().getLastName()));
                writer.write(',');
                writer.write(csv(availability.getProfessional().getTitle()));
                writer.write(',');
                writer.write(csv(availability.getProfessional().getSpecialtyLabel().equals("-")
                        ? "" : availability.getProfessional().getSpecialtyLabel()));
                writer.write(',');
                writer.write(availability.getDate().toString());
                writer.write(',');
                writer.write(availability.getStart().toString());
                writer.write(',');
                writer.write(availability.getEnd().toString());
                writer.newLine();
                count++;
            }
            return count;
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
