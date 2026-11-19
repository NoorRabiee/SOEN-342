package main.java.ats.domain;

import main.java.ats.util.TextUtil;

public class MedicalSpecialty {
    private final String name;

    public MedicalSpecialty(String name) {
        if (TextUtil.isBlank(name)) {
            throw new IllegalArgumentException("specialty name is required");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }   
}
