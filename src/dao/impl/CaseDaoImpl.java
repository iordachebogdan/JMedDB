package dao.impl;

import dao.CaseDao;
import dao.UserDao;
import dao.exceptions.NotFoundException;
import model.Case;
import model.Medication;
import model.Patient;
import model.Symptom;
import service.DbService;

import java.sql.*;
import java.util.Date;

public class CaseDaoImpl implements CaseDao {
    private DbService dbServiceInstance;

    public CaseDaoImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    @Override
    public Case getCaseById(int id) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from cases where id=?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next())
            throw new NotFoundException("Case not found");

        int patientId = resultSet.getInt(2);
        int ownerMedicId = resultSet.getInt(3);
        boolean completed = resultSet.getBoolean(4);
        Case c = new Case(id, patientId, ownerMedicId);
        if (completed)
            c.setCompleted();

        statement = connection.prepareStatement("select medic_id from case_medics where case_id=?");
        statement.setInt(1, id);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int otherMedicId = resultSet.getInt(1);
            c.addMedic(otherMedicId);
        }

        statement = connection.prepareStatement("select * from symptom where case_id=?");
        statement.setInt(1, id);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String description = resultSet.getString(3);
            Date firstApp = resultSet.getDate(4);
            c.getSymptomList().addSymptom(new Symptom(description, firstApp));
        }

        statement = connection.prepareStatement("select * from medication where case_id=?");
        statement.setInt(1, id);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString(3);
            String administration = resultSet.getString(4);
            Date fromDate = resultSet.getDate(5);
            Date toDate = resultSet.getDate(6);
            c.getPrescription().addMedication(new Medication(name, administration, fromDate, toDate));
        }

        dbServiceInstance.returnConnection(connection);
        return c;
    }

    @Override
    public int createCase(int patientId) throws NotFoundException, SQLException {
        UserDao userDao = new UserDaoImpl();
        Patient patient = (Patient)userDao.getUserById(patientId);

        if (patient.getPersonalMedicId() == null)
            throw new NotFoundException("No personal medic assigned");

        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into cases values(?, ?, ?, ?)",
                new int[]{1});
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, patientId);
        statement.setInt(3, patient.getPersonalMedicId());
        statement.setBoolean(4, false);

        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        int id = -1;
        if (generatedKeys.next())
            id = generatedKeys.getInt(1);

        dbServiceInstance.returnConnection(connection);
        return id;
    }

    @Override
    public void addSymptom(int caseId, Symptom symptom) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into symptom values(?, ?, ?, ?)");
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, caseId);
        statement.setString(3, symptom.getDescription());
        statement.setDate(4, new java.sql.Date(symptom.getFirstAppearance().getTime()));
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void addMedication(int caseId, Medication medication) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into medication values(?, ?, ?, ?, ?, ?)");
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, caseId);
        statement.setString(3, medication.getName());
        statement.setString(4, medication.getAdministration());
        statement.setDate(5, new java.sql.Date(medication.getFromDate().getTime()));
        statement.setDate(6, new java.sql.Date(medication.getToDate().getTime()));
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void removeSymptom(int caseId, Symptom symptom) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("delete from symptom where description=?");
        statement.setString(1, symptom.getDescription());
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void removeMedication(int caseId, Medication medication) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("delete from medication where name=?");
        statement.setString(1, medication.getName());
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void addMedic(int caseId, int medicId) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert ignore into case_medics values(?, ?)");
        statement.setInt(1, caseId);
        statement.setInt(2, medicId);
        statement.executeUpdate();

        Case c = getCaseById(caseId);

        statement = connection.prepareStatement("insert ignore into medic_patients values(?, ?)");
        statement.setInt(1, medicId);
        statement.setInt(2, c.getPatientId());
        statement.executeUpdate();
    }

    @Override
    public void setCompleted(int caseId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("update cases set completed=1 where id=?");
        statement.setInt(1, caseId);
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    public static void main(String[] args) throws NotFoundException, SQLException {
        CaseDao caseDao = new CaseDaoImpl();
        Case c = caseDao.getCaseById(1);

//        System.out.println(caseDao.createCase(16));
        caseDao.addSymptom(3, new Symptom("diarheea", new Date()));
        caseDao.addMedication(3, new Medication("bla", "daily", new Date(), new Date()));
        caseDao.removeSymptom(3, new Symptom("diarheea", new Date()));
        caseDao.removeMedication(3, new Medication("bla", "daily", new Date(), new Date()));

        caseDao.setCompleted(3);

        caseDao.addMedic(3, 1);
    }
}
