package model;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    private int id;
    private String username;
    private String hashPassword;
    private UserDetails userDetails;

    private List<Integer> casesIds;

    public User(int id, String username, String hashPassword, UserDetails userDetails) {
        this.id = id;
        this.username = username;
        this.hashPassword = hashPassword;
        this.userDetails = userDetails;
        this.casesIds = new ArrayList<>();
    }

    public abstract UserTypeEnum getType();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void addCaseId(int caseId) {
        casesIds.add(caseId);
    }

    public List<Integer> getAssignedCasesIds() {
        return casesIds;
    }
}
