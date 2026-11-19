package main.java.ats.domain;

public class FamilyPhysician extends HealthcareProfessional {
    public FamilyPhysician(String firstName, String lastName, String title) {
        super(firstName, lastName, title);
    }

    @Override
    public String getRoleName() {
        return "Family physician";
    }
}
