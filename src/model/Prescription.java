package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prescription {
    private Set<Medication> medicationSet;

    Prescription() {
        medicationSet = new HashSet<>();
    }

    public void addMedication(Medication m) {
        medicationSet.add(m);
    }

    public void removeMedication(Medication m) {
        medicationSet.remove(m);
    }

    public List<Medication> getMedication() {
        return new ArrayList<>(medicationSet);
    }
}
