package dao;

import dao.exceptions.NotFoundException;
import model.*;

import java.sql.SQLException;

public interface CaseDao {
    Case getCaseById(int id) throws SQLException, NotFoundException;
    int createCase(int patientId) throws NotFoundException, SQLException;

    void addSymptom(int caseId, Symptom symptom) throws SQLException;
    void addMedication(int caseId, Medication medication) throws SQLException;
    void removeSymptom(int caseId, Symptom symptom) throws SQLException;
    void removeMedication(int caseId, Medication medication) throws SQLException;

    void addMedic(int caseId, int medicId) throws SQLException, NotFoundException;

    void setCompleted(int caseId) throws SQLException;
}
