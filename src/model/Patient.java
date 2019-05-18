package model;

public class Patient extends User {
    private Medic personalMedic;

    public Patient(int id, String username, String hashPassword, UserDetails userDetails) {
        super(id, username, hashPassword, userDetails);
        this.personalMedic = null;
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.PATIENT;
    }

    public Medic getPersonalMedic() {
        return personalMedic;
    }

    public void setPersonalMedic(Medic personalMedic) {
        this.personalMedic = personalMedic;
    }
}
