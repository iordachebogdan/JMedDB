import controller.AuthenticationController;
import controller.PatientController;
import dao.exceptions.NotFoundException;
import gui.LoginFrame;
import model.Patient;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws NotFoundException, SQLException {
        LoginFrame loginFrame = new LoginFrame();
    }
}
