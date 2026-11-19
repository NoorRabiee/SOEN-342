package main.java.ats.domain;

public class Nurse extends HealthcareProfessional {
    public Nurse(String firstName, String lastName, String title) {
        super(firstName, lastName, title);
    }

    @Override
    public String getRoleName() {
        return "Nurse";
    }
}
