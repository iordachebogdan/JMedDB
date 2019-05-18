package model;

import java.util.ArrayList;
import java.util.List;

public class Medic extends User {
    private List<Specialization> specializations;
    private List<Patient> patients;

    public Medic(int id, String username, String hashPassword, UserDetails userDetails) {
        super(id, username, hashPassword, userDetails);
        this.specializations = new ArrayList<>();
        this.patients = new ArrayList<>();
    }

    public void addSpecialization(Specialization s) {
        specializations.add(s);
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void addPatient(Patient p) {
        patients.add(p);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void removePatient(Patient p) {
        patients.remove(p);
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.MEDIC;
    }
}
