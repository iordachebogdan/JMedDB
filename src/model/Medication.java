package model;

import java.util.Date;

public class Medication {
    private final String name;
    private final String administration;
    private final Date fromDate;
    private final Date toDate;

    public Medication(String name, String administration, Date fromDate, Date toDate) {
        this.name = name;
        this.administration = administration;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getName() {
        return name;
    }

    public String getAdministration() {
        return administration;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        return name;
    }
}
