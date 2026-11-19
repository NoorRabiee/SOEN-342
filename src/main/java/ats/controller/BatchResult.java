package main.java.ats.controller;

import java.util.ArrayList;
import java.util.List;

// Outcome of an operation that creates many availabilities (make availability, CSV import)
public class BatchResult {
    private int created;
    private int skipped;
    private final List<String> messages = new ArrayList<>();

    public void created() {
        created++;
    }

    public void skipped(String message) {
        skipped++;
        if (message != null) {
            messages.add(message);
        }
    }

    public void error(String message) {
        messages.add(message);
    }

    public int getCreated() {
        return created;
    }

    public int getSkipped() {
        return skipped;
    }

    public List<String> getMessages() {
        return messages;
    }
}