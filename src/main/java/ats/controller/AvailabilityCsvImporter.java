package main.java.ats.controller;

import main.java.ats.domain.HealthcareProfessional;
import main.java.ats.domain.Schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AvailabilityCsvImporter {

    /**
     * Imports availability rows with columns First Name, Last Name, Title,
     * Specialty, Date, Start and End. Invalid or overlapping rows are skipped
     * and reported in the returned result; an invalid header fails the import.
     */
    public BatchResult importFrom(Path file, SystemController controller) throws IOException {
        if (file == null || controller == null) {
            throw new IllegalArgumentException("file and controller are required");
        }

        BatchResult result = new BatchResult();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty");
            }

            Map<String, Integer> columns = indexColumns(parseLine(headerLine));
            String[] required = {"first name", "last name", "title", "specialty", "date", "start", "end"};
            for (String name : required) {
                if (!columns.containsKey(name)) {
                    throw new IOException("CSV header is missing column: " + name);
                }
            }

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    String[] fields = parseLine(line);
                    String first = value(fields, columns, "first name");
                    String last = value(fields, columns, "last name");
                    String title = value(fields, columns, "title");
                    String specialty = value(fields, columns, "specialty");
                    if (first.isEmpty() || last.isEmpty()) {
                        throw new IllegalArgumentException("first and last name are required");
                    }

                    LocalDate date = LocalDate.parse(value(fields, columns, "date"));
                    LocalTime start = LocalTime.parse(value(fields, columns, "start"));
                    LocalTime end = LocalTime.parse(value(fields, columns, "end"));

                    HealthcareProfessional professional = controller.registerProfessional(
                            first, last, title, specialty);
                    Schedule schedule = professional.getSchedule();
                    // Use the same schedule catalog as normal registration/search flows.
                    // registerProfessional has already registered this schedule when new.
                    schedule.addAvailability(date, start, end);
                    result.created();
                } catch (RuntimeException e) {
                    result.skipped("Row " + rowNumber + ": " + message(e));
                }
            }
        }
        return result;
    }

    private static Map<String, Integer> indexColumns(String[] header) {
        Map<String, Integer> columns = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String name = header[i].trim().replace("\uFEFF", "").toLowerCase(Locale.ROOT);
            columns.put(name, i);
        }
        return columns;
    }

    private static String value(String[] fields, Map<String, Integer> columns, String name) {
        int index = columns.get(name);
        if (index >= fields.length) {
            throw new IllegalArgumentException("missing value for " + name);
        }
        return fields[index].trim();
    }

    private static String message(RuntimeException e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    /** Parses one comma-separated record, including quoted commas and escaped quotes. */
    private static String[] parseLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("unterminated quoted field");
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
}
