package dao;

import dao.exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface MedicRequestDao {
    List<Integer> getRequestPatientsIds(int medicId) throws SQLException;

    void acceptRequest(int medicId, int patientId) throws SQLException, NotFoundException;
    void addRequest(int medicId, int patientId) throws SQLException;
}
