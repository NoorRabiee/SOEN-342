package main.java.ats.catalog;

import main.java.ats.domain.HealthcareProfessional;
import main.java.ats.domain.FamilyPhysician;
import main.java.ats.domain.SpecialistPhysician;
import main.java.ats.domain.Nurse;
import main.java.ats.domain.MedicalSpecialty;
import main.java.ats.util.TextUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HealthcareProfessionalCatalog {
    private final Map<String, HealthcareProfessional> byName = new LinkedHashMap<>();

    private static String key(String first, String last) {
        return TextUtil.normalize(first + " " + last);
    }

    public HealthcareProfessional findByFullName(String first, String last) {
        return byName.get(key(first, last));
    }

    /**
     * Creates a professional. The concrete type is derived from the title and specialty:
     * "Nurse ..." -> Nurse, "Family Medicine" -> FamilyPhysician, otherwise SpecialistPhysician
     */
    public HealthcareProfessional create(String first, String last, String title, MedicalSpecialty specialty) {
        if (findByFullName(first, last) != null) {
            throw new IllegalStateException("professional already exists: " + first + " " + last);
        }
        HealthcareProfessional hp;
        if (TextUtil.normalize(title).contains("nurse")) {
            hp = new Nurse(first, last, title);
        } else if (specialty != null && TextUtil.normalize(specialty.getName()).equals("family medicine")) {
            hp = new FamilyPhysician(first, last, title);
        } else {
            hp = new SpecialistPhysician(first, last, title);
        }
        hp.addSpecialty(specialty);
        byName.put(key(first, last), hp);
        return hp;
    }

    public List<HealthcareProfessional> search(String namePart, String specialtyPart) {
        List<HealthcareProfessional> out = new ArrayList<>();
        for (HealthcareProfessional hp : byName.values()) {
            boolean nameOk = TextUtil.isBlank(namePart) || TextUtil.containsAllTokens(hp.getDisplayName(), namePart);
            boolean specOk = TextUtil.isBlank(specialtyPart) || hp.hasSpecialtyLike(specialtyPart);
            if (nameOk && specOk) {
                out.add(hp);
            }
        }
        return out;
    }

    // hp := find(professionalId)
    public HealthcareProfessional find(int professionalId) {
        for (HealthcareProfessional hp : byName.values()) {
            if (hp.getId() == professionalId) {
                return hp;
            }
        }
        return null;
    }

    public List<HealthcareProfessional> getAll() {
        return new ArrayList<>(byName.values());
    }

}
