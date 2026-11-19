package main.java.ats.domain;

public class SpecialistPhysician extends HealthcareProfessional {
    public SpecialistPhysician(String firstName, String lastName, String title) {
        super(firstName, lastName, title);
    }

    @Override
    public String getRoleName() {
        return "Specialist physician";
    }
    
}
