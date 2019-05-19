package dao.impl;

import dao.MedicRequestDao;
import dao.exceptions.NotFoundException;
import service.DbService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicRequestDaoImpl implements MedicRequestDao {
    private DbService dbServiceInstance;

    public MedicRequestDaoImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    @Override
    public List<Integer> getRequestPatientsIds(int medicId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select patient_id from medic_request where medic_id=?");
        statement.setInt(1, medicId);
        ResultSet resultSet = statement.executeQuery();

        List<Integer> patientsIds = new ArrayList<>();
        while (resultSet.next())
            patientsIds.add(resultSet.getInt(1));
        dbServiceInstance.returnConnection(connection);
        return patientsIds;
    }

    @Override
    public void acceptRequest(int medicId, int patientId) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("delete from medic_request where medic_id=? and patient_id=?");
        statement.setInt(1, medicId);
        statement.setInt(2, patientId);

        int changed = statement.executeUpdate();
        if (changed == 0)
            throw new NotFoundException("No request found");

        statement = connection.prepareStatement("update patient set personal_medic_id=? where id=?");
        statement.setInt(1, medicId);
        statement.setInt(2, patientId);
        statement.executeUpdate();

        statement = connection.prepareStatement("insert ignore into medic_patients values(?, ?)");
        statement.setInt(1, medicId);
        statement.setInt(2, patientId);
        statement.executeUpdate();

        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void addRequest(int medicId, int patientId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("delete from medic_request where patient_id=?");
        statement.setInt(1, patientId);
        statement.executeUpdate();

        statement = connection.prepareStatement("insert into medic_request values(?, ?, ?)");
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, medicId);
        statement.setInt(3, patientId);
        statement.executeUpdate();

        dbServiceInstance.returnConnection(connection);
    }

    public static void main(String[] args) throws SQLException, NotFoundException {
        MedicRequestDao medicRequestDao = new MedicRequestDaoImpl();
        medicRequestDao.addRequest(3, 16);
        System.out.println(medicRequestDao.getRequestPatientsIds(3));
        medicRequestDao.acceptRequest(3, 16);
    }
}
