import controller.AuthenticationController;
import controller.PatientController;
import dao.exceptions.NotFoundException;
import model.Patient;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws NotFoundException, SQLException {
        AuthenticationController authenticationController = AuthenticationController.getInstance();
        PatientController patientController = PatientController.getInstance();

        String token = authenticationController.authenticateUser("patient3", "parola");
        Patient patient = patientController.getPatientPersonalData(token);
        System.out.println(patient.getAssignedCasesIds());
    }
}
