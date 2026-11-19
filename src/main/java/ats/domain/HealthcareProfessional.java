package main.java.ats.domain;

import main.java.ats.util.TextUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class HealthcareProfessional {
    private final String firstName;
    private final String lastName;
    private final String title;
    private final Set<MedicalSpecialty> specialties = new LinkedHashSet<>();
    private Schedule schedule;

    protected HealthcareProfessional(String firstName, String lastName, String title) {
        if (TextUtil.isBlank(firstName) || TextUtil.isBlank(lastName)) {
            throw new IllegalArgumentException("first and last name are required");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.title = title == null ? "" : title.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        return title.isEmpty() ? getFullName() : title + " " + getFullName();
    }

    public Schedule getSchedule() {
        if (schedule == null) {
            schedule = new Schedule(this); // created on first use and one Schedule per professional
        }
        return schedule;
    }

    public void addSpecialty(MedicalSpecialty specialty) {
        if (specialty != null) {
            boolean known = false;
            for (MedicalSpecialty s : specialties) {
                if (TextUtil.normalize(s.getName()).equals(TextUtil.normalize(specialty.getName()))) {
                    known = true;
                }
            }
            if (!known) {
                specialties.add(specialty);
            }
        }
    }

    public Set<MedicalSpecialty> getSpecialties() {
        return Collections.unmodifiableSet(specialties);
    }

    public String getSpecialtyLabel() {
        if (specialties.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (MedicalSpecialty s : specialties) {
            if (sb.length() > 0) {
                sb.append(" / ");
            }
            sb.append(s.getName());
        }
        return sb.toString();
    }

    public boolean hasSpecialtyLike(String query) {
        for (MedicalSpecialty s : specialties) {
            if (TextUtil.normalize(s.getName()).contains(TextUtil.normalize(query))) {
                return true;
            }
        }
        return false;
    }

    public abstract String getRoleName();

    @Override
    public String toString() {
        return getDisplayName();
    }
}