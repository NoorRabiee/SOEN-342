package main.java.ats.catalog;

import main.java.ats.domain.MedicalSpecialty;
import main.java.ats.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class MedicalSpecialtyCatalog {
    private final Map<String, MedicalSpecialty> byName = new LinkedHashMap<>();

    public MedicalSpecialty getOrCreate(String name) {
        String key = TextUtil.normalize(name);
        MedicalSpecialty s = byName.get(key);
        if (s == null) {
            s = new MedicalSpecialty(name);
            byName.put(key, s);
        }
        return s;
    }

    public List<MedicalSpecialty> getAll() {
        return new ArrayList<>(byName.values());
    }
}
