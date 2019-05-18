package model;

import java.util.Date;

public class Symptom {
    private final String description;
    private final Date firstAppearance;

    public Symptom(String description, Date firstAppearance) {
        this.description = description;
        this.firstAppearance = firstAppearance;
    }

    public String getDescription() {
        return description;
    }

    public Date getFirstAppearance() {
        return firstAppearance;
    }

    @Override
    public String toString() {
        return description;
    }
}
