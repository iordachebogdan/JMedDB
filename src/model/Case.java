package model;

import java.util.ArrayList;
import java.util.List;

public class Case {
    private int id;
    private int patientId;
    private int ownerMedicId;
    private List<Integer> otherMedicsIds;

    private SymptomList symptomList;
    private Prescription prescription;

    private boolean completed;

    public Case(int id, Patient patient) {
        this.id = id;
        this.patientId = patient.getId();
        this.ownerMedicId = patient.getPersonalMedicId();
        this.otherMedicsIds = new ArrayList<>();

        this.symptomList = new SymptomList();
        this.prescription = new Prescription();

        this.completed = false;
    }

    public SymptomList getSymptomList() {
        return symptomList;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public int getOwnerMedicId() {
        return ownerMedicId;
    }

    public List<Integer> getOtherMedicsIds() {
        return otherMedicsIds;
    }

    public void addMedic(int medicId) {
        otherMedicsIds.add(medicId);
    }

    public int getPatientId() {
        return patientId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        completed = true;
    }

    public int getId() {
        return id;
    }
}
