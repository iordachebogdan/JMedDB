package controller;

import dao.CaseDao;
import dao.MedicRequestDao;
import dao.UserDao;
import dao.exceptions.NotFoundException;
import dao.impl.CaseDaoImpl;
import dao.impl.MedicRequestDaoImpl;
import dao.impl.UserDaoImpl;
import model.*;
import service.AuthenticationService;
import service.LoggingService;

import java.sql.SQLException;

public class CaseController {
    private static CaseController instance = null;

    private UserDao userDao;
    private LoggingService loggingService;
    private AuthenticationService authenticationService;
    private CaseDao caseDao;

    private CaseController() {
        userDao = new UserDaoImpl();
        loggingService = LoggingService.getInstance();
        authenticationService = AuthenticationService.getInstance();
        caseDao = new CaseDaoImpl();
    }

    public static CaseController getInstance() {
        if (instance == null)
            instance = new CaseController();
        return instance;
    }

    public Case getCaseById(String token, int caseId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.READ,
                this.getClass().getName(),
                String.format("Read case with id=%d", caseId)
        );
        int id = authenticationService.extractIdFromToken(token);
        Case c;
        User user;
        try {
            c = caseDao.getCaseById(caseId);
            user = userDao.getUserById(id);
            if (id != c.getPatientId() && id != c.getOwnerMedicId() && !c.getOtherMedicsIds().contains(id))
                throw new IllegalArgumentException("Not authorized to view case");
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, user.getUsername(), LoggingService.Status.OK);
        return c;
    }

    public int createCase(String token) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.CREATE,
                this.getClass().getName(),
                "Patient creates case"
        );
        int id = authenticationService.extractIdFromToken(token);
        Patient patient;
        int caseId;
        try {
            patient = (Patient) userDao.getUserById(id);
            caseId = caseDao.createCase(id);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, patient.getUsername(), LoggingService.Status.OK);
        return caseId;
    }

    public void addMedication(String token, int caseId, Medication medication) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
            LoggingService.Operation.UPDATE,
            this.getClass().getName(),
            "Medic adds medication to case with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        Case c;
        try {
            medic = (Medic) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (medic.getId() != c.getOwnerMedicId() && !c.getOtherMedicsIds().contains(medic.getId()))
                throw new IllegalArgumentException("Not authorized to add medication");
            caseDao.addMedication(caseId, medication);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }

    public void removeMedication(String token, int caseId, Medication medication) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic removes medication from case with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        Case c;
        try {
            medic = (Medic) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (medic.getId() != c.getOwnerMedicId() && !c.getOtherMedicsIds().contains(medic.getId()))
                throw new IllegalArgumentException("Not authorized to remove medication");
            caseDao.removeMedication(caseId, medication);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }

    public void addSymptom(String token, int caseId, Symptom symptom) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient adds symptom to case with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Patient patient;
        Case c;
        try {
            patient = (Patient) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (patient.getId() != c.getPatientId())
                throw new IllegalArgumentException("Not authorized to add symptom");
            caseDao.addSymptom(caseId, symptom);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, patient.getUsername(), LoggingService.Status.OK);
    }

    public void removeSymptom(String token, int caseId, Symptom symptom) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Patient removes symptom from case with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Patient patient;
        Case c;
        try {
            patient = (Patient) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (patient.getId() != c.getPatientId())
                throw new IllegalArgumentException("Not authorized to remove symptom");
            caseDao.removeSymptom(caseId, symptom);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, patient.getUsername(), LoggingService.Status.OK);
    }

    public void setCompleted(String token, int caseId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic sets case completed with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        Case c;
        try {
            medic = (Medic) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (medic.getId() != c.getOwnerMedicId())
                throw new IllegalArgumentException("Not authorized to set completed");
            caseDao.setCompleted(caseId);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }

    public void addMedic(String token, int caseId, int medicId) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.UPDATE,
                this.getClass().getName(),
                "Medic adds medic with id=" + medicId + " to case with id=" + caseId
        );
        int id = authenticationService.extractIdFromToken(token);
        Medic medic;
        Case c;
        try {
            medic = (Medic) userDao.getUserById(id);
            c = caseDao.getCaseById(caseId);
            if (medic.getId() != c.getOwnerMedicId() && !c.getOtherMedicsIds().contains(id))
                throw new IllegalArgumentException("Not authorized to add medic");
            caseDao.addMedic(caseId, medicId);
        } catch (Exception e) {
            loggingService.log(entry, null, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }

        loggingService.log(entry, medic.getUsername(), LoggingService.Status.OK);
    }
}
