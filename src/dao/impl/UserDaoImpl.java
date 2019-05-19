package dao.impl;

import dao.UserDao;
import dao.exceptions.NotFoundException;
import model.*;
import service.DbService;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static model.UserTypeEnum.MEDIC;
import static model.UserTypeEnum.PATIENT;

public class UserDaoImpl implements UserDao {
    private DbService dbServiceInstance;

    public UserDaoImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    private User getUser(Connection connection, PreparedStatement statement) throws SQLException, NotFoundException {
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next())
            throw new NotFoundException("User not found");

        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstname");
        String lastName = resultSet.getString("lastname");
        String email = resultSet.getString("email");
        String bio = resultSet.getString("bio");
        UserTypeEnum type = UserTypeEnum.valueOf(resultSet.getString("type"));

        UserDetails userDetails = new UserDetails(firstName, lastName, email, bio);
        User result = null;

        if (type == UserTypeEnum.PATIENT) {
            Patient patient = new Patient(id, username, password, userDetails);
            statement = connection.prepareStatement("select personal_medic_id from patient where id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                patient.setPersonalMedicId(resultSet.getInt(1));
            }
            result = patient;
            statement = connection.prepareStatement("select id from cases where patient_id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.addCaseId(resultSet.getInt(1));
            }
        } else {
            Medic medic = new Medic(id, username, password, userDetails);
            statement = connection.prepareStatement("select name from specialization where medic_id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                medic.addSpecialization(new Specialization(resultSet.getString(1)));
            }

            statement = connection.prepareStatement("select patient_id from medic_patients where medic_id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                medic.addPatientId(resultSet.getInt(1));
            }
            result = medic;

            Set<Integer> caseIds = new HashSet<>();
            statement = connection.prepareStatement("select id from cases where owner_medic_id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                caseIds.add(resultSet.getInt(1));
            }
            statement = connection.prepareStatement("select case_id from case_medics where medic_id=?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                caseIds.add(resultSet.getInt(1));
            }
            for (Integer caseId : caseIds)
                result.addCaseId(caseId);
        }

        return result;
    }

    @Override
    public User getUserById(int id) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from user where id=?");
        statement.setInt(1, id);
        User result = getUser(connection, statement);
        dbServiceInstance.returnConnection(connection);
        return result;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from user where username=?");
        statement.setString(1, username);
        User result = getUser(connection, statement);
        dbServiceInstance.returnConnection(connection);
        return result;
    }

    @Override
    public List<Medic> getAllMedics() throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select id from user where type=?");
        statement.setString(1, MEDIC.name());
        ResultSet resultSet = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (resultSet.next())
            ids.add(resultSet.getInt(1));
        dbServiceInstance.returnConnection(connection);

        List<Medic> result = new ArrayList<>();
        for (Integer id : ids)
            result.add((Medic)getUserById(id));
        return result;
    }

    @Override
    public List<Medic> getMedicsBySpecialization(Specialization specialization) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select medic_id from specialization where name=?");
        statement.setString(1, specialization.getName());
        ResultSet resultSet = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (resultSet.next())
            ids.add(resultSet.getInt(1));
        dbServiceInstance.returnConnection(connection);

        List<Medic> result = new ArrayList<>();
        for (Integer id : ids)
            result.add((Medic)getUserById(id));
        return result;
    }

    @Override
    public void createUser(User user) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into user values(?, ?, ?, ?, ?, ?, ?, ?)",
                new int[]{1});
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getHashPassword());
        statement.setString(4, user.getUserDetails().getFirstName());
        statement.setString(5, user.getUserDetails().getLastName());
        statement.setString(6, user.getUserDetails().getEmail());
        statement.setString(7, user.getUserDetails().getBio());
        statement.setString(8, user.getType().name());

        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next())
            user.setId(generatedKeys.getInt(1));

        if (user.getType() == PATIENT) {
            statement = connection.prepareStatement("insert into patient values(?, ?)");
            statement.setInt(1, user.getId());
            statement.setNull(2, Types.INTEGER);
            statement.executeUpdate();
        }

        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void addSpecializationToMedic(int medicId, Specialization specialization) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into specialization values(?, ?, ?)");
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, medicId);
        statement.setString(3, specialization.getName());
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    public static void main(String[] args) throws NotFoundException, SQLException {
        UserDao userDao = new UserDaoImpl();
        User patient = userDao.getUserById(4);
        User medic = userDao.getUserByUsername("medic2");
        List<Medic> medics = userDao.getMedicsBySpecialization(new Specialization("cardio"));

//        User user = new Patient(0, "patient3", "8287458823facb8ff918dbfabcd22ccb",
//                new UserDetails("Dan", "Dumbo", "dd@a.com", "bla bla bla"));
//        userDao.createUser(user);

//        userDao.addSpecializationToMedic(1, new Specialization("neuro"));
    }
}
