package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SymptomList {
    private Set<Symptom> symptomSet;

    SymptomList() {
        symptomSet = new HashSet<>();
    }

    public void addSymptom(Symptom s) {
        symptomSet.add(s);
    }

    public void removeSymptom(Symptom s) {
        symptomSet.remove(s);
    }

    public List<Symptom> getSymptoms() {
        return new ArrayList<>(symptomSet);
    }
}
