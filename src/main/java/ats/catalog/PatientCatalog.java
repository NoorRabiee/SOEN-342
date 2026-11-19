package main.java.ats.catalog;

import main.java.ats.domain.Patient;
import main.java.ats.util.TextUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientCatalog {
    private final List<Patient> patients = new ArrayList<>();

    public Patient add(Patient p) {
        patients.add(p);
        return p;
    }

    // At least one criterion is required, all supplied criteria must match
    public List<Patient> search(String insuranceNum, LocalDate dateOfBirth, String phoneNum, String namePart) {
        if (TextUtil.isBlank(insuranceNum) && dateOfBirth == null
                && TextUtil.isBlank(phoneNum) && TextUtil.isBlank(namePart)) {
            throw new IllegalArgumentException("provide at least one search criterion");
        }
        List<Patient> out = new ArrayList<>();
        for (Patient p : patients) {
            if (p.matches(insuranceNum, dateOfBirth, phoneNum, namePart)) {
                out.add(p);
            }
        }
        return out;
    }

    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }
}
