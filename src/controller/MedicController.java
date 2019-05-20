package controller;

import dao.MedicRequestDao;
import dao.UserDao;
import dao.exceptions.NotFoundException;
import dao.impl.MedicRequestDaoImpl;
import dao.impl.UserDaoImpl;
import model.Medic;
import model.Specialization;
import service.AuthenticationService;
import service.LoggingService;

import java.sql.SQLException;
import java.util.List;

public class MedicController {
    private static MedicController instance = null;

    private UserDao userDao;
    private LoggingService loggingService;
    private AuthenticationService authenticationService;
    private MedicRequestDao medicRequestDao;

    private MedicController() {
        userDao = new UserDaoImpl();
        loggingService = LoggingService.getInstance();
        authenticationService = AuthenticationService.getInstance();
        medicRequestDao = new MedicRequestDaoImpl();
    }

    public static MedicController getInstance() {
        if (instance == null)
            instance = new MedicController();
        return instance;
    }

    public Medic getMedicData(String token) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Medic reads his data"
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        try {
            medic = (Medic)userDao.getUserById(id);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
        return medic;
    }

    public Medic getMedicById(int id) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User reads medic with id=" + id
        );
        Medic medic;
        try {
            medic = (Medic)userDao.getUserById(id);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.NOT_FOUND);
            throw e;
        }

        loggingService.log(entry, null, LoggingService.Status.OK);
        return medic;
    }

    public List<Medic> getAllMedics() throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User reads all medics"
        );
        List<Medic> medics;
        try {
            medics = userDao.getAllMedics();
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.BAD_REQUEST);
            throw e;
        }

        loggingService.log(entry, null, LoggingService.Status.OK);
        return medics;
    }

    public List<Medic> getAllMedicsBySpecialization(Specialization specialization) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User reads all medics with specialization=" + specialization.getName()
        );
        List<Medic> medics;
        try {
            medics = userDao.getMedicsBySpecialization(specialization);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.BAD_REQUEST);
            throw e;
        }

        loggingService.log(entry, null, LoggingService.Status.OK);
        return medics;
    }

    public List<Integer> getRequestPatientsIds(String token) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Medic reads his patient requests"
        );
        int id = authenticationService.extractIdFromToken(token);
        List<Integer> patientsIds;
        Medic medic;
        try {
            patientsIds = medicRequestDao.getRequestPatientsIds(id);
            medic = (Medic)userDao.getUserById(id);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
        return patientsIds;
    }

    public void acceptRequest(String token, int patientId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic accepts request from patient with id=" + patientId
        );
        int medicId = authenticationService.extractIdFromToken(token);
        Medic medic;
        try {
            medic = (Medic)userDao.getUserById(medicId);
            medicRequestDao.acceptRequest(medicId, patientId);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }

    public void addSpecialization(String token, Specialization specialization) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic adds specialization " + specialization.getName()
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        try {
            medic = (Medic)userDao.getUserById(id);
            userDao.addSpecializationToMedic(id, specialization);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }
}
