package controller;

import dao.MedicRequestDao;
import dao.UserDao;
import dao.exceptions.NotFoundException;
import dao.impl.MedicRequestDaoImpl;
import dao.impl.UserDaoImpl;
import model.Patient;
import model.User;
import service.AuthenticationService;
import service.LoggingService;

import java.sql.SQLException;

public class PatientController {
    private static PatientController instance = null;

    private UserDao userDao;
    private LoggingService loggingService;
    private AuthenticationService authenticationService;
    private MedicRequestDao medicRequestDao;

    private PatientController() {
        userDao = new UserDaoImpl();
        loggingService = LoggingService.getInstance();
        authenticationService = AuthenticationService.getInstance();
        medicRequestDao = new MedicRequestDaoImpl();
    }

    public static PatientController getInstance() {
        if (instance == null)
            instance = new PatientController();
        return instance;
    }

    public Patient getPatientPersonalData(String token) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "Patient reads his data"
        );

        int id = authenticationService.extractIdFromToken(token);
        Patient patient;
        try {
            patient = (Patient)userDao.getUserById(id);
        } catch (SQLException | NotFoundException e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }
        loggingService.log(entry, patient.getUsername(), LoggingService.Status.OK);
        return patient;
    }

    public Patient getPatientById(String token, int patientId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                "User reads data from patient with id=" + patientId
        );

        int id = authenticationService.extractIdFromToken(token);
        User user;
        try {
            user = userDao.getUserById(id);
            if (user instanceof Patient && user.getId() != patientId)
                throw new IllegalArgumentException("Not authorized");
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        Patient patient;
        try {
            patient = (Patient) userDao.getUserById(patientId);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.NOT_FOUND);
            throw e;
        }

        loggingService.log(entry, user.getUsername(), LoggingService.Status.OK);
        return patient;
    }

    public void sendPatientRequest(String token, int medicId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient sends request to medic with id=" + medicId
        );
        int patientId = authenticationService.extractIdFromToken(token);
        Patient patient;
        try {
            patient = (Patient)userDao.getUserById(patientId);
            medicRequestDao.addRequest(medicId, patientId);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.BAD_REQUEST);
            throw e;
        }

        loggingService.log(entry, patient.getUsername(), LoggingService.Status.OK);
    }
}
