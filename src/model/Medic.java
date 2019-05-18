package model;

import java.util.ArrayList;
import java.util.List;

public class Medic extends User {
    private List<Specialization> specializations;
    private List<Integer> patientsIds;

    public Medic(int id, String username, String hashPassword, UserDetails userDetails) {
        super(id, username, hashPassword, userDetails);
        this.specializations = new ArrayList<>();
        this.patientsIds = new ArrayList<>();
    }

    public void addSpecialization(Specialization s) {
        specializations.add(s);
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void addPatientId(int patientId) {
        patientsIds.add(patientId);
    }

    public List<Integer> getPatientsIds() {
        return patientsIds;
    }

    public void removePatientId(int patientId) {
        patientsIds.remove(patientId);
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.MEDIC;
    }
}
