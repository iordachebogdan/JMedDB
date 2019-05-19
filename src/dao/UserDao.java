package dao;

import dao.exceptions.NotFoundException;
import model.Medic;
import model.Specialization;
import model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    User getUserById(int id) throws SQLException, NotFoundException;
    User getUserByUsername(String username) throws SQLException, NotFoundException;
    List<Medic> getAllMedics() throws SQLException, NotFoundException;
    List<Medic> getMedicsBySpecialization(Specialization specialization) throws SQLException, NotFoundException;

    void createUser(User user) throws SQLException;

    void addSpecializationToMedic(int medicId, Specialization specialization) throws SQLException;
}
