package model;

public class Patient extends User {
    private Integer personalMedicId;

    public Patient(int id, String username, String hashPassword, UserDetails userDetails) {
        super(id, username, hashPassword, userDetails);
        this.personalMedicId = null;
    }

    @Override
    public UserTypeEnum getType() {
        return UserTypeEnum.PATIENT;
    }

    public int getPersonalMedicId() {
        return personalMedicId;
    }

    public void setPersonalMedicId(int personalMedicId) {
        this.personalMedicId = personalMedicId;
    }
}
