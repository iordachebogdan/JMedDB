package controller;

import dao.UserDao;
import dao.exceptions.NotFoundException;
import dao.impl.UserDaoImpl;
import model.*;
import service.AuthenticationService;
import service.LoggingService;

import java.sql.SQLException;

public class AuthenticationController {
    private static AuthenticationController instance = null;

    private UserDao userDao;
    private LoggingService loggingService;
    private AuthenticationService authenticationService;

    private AuthenticationController() {
        this.userDao = new UserDaoImpl();
        this.loggingService = LoggingService.getInstance();
        this.authenticationService = AuthenticationService.getInstance();
    }

    public static AuthenticationController getInstance() {
        if (instance == null)
            instance = new AuthenticationController();
        return instance;
    }

    public UserTypeEnum getType(String token) throws NotFoundException, SQLException {
        int id = authenticationService.extractIdFromToken(token);
        User user = userDao.getUserById(id);
        return user.getType();
    }

    public User registerUser(String username, String password,
                             String firstName, String lastName, String email, String bio,
                             UserTypeEnum type) throws SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.CREATE,
                this.getClass().getName(),
                String.format("User tries to register username=%s", username)
        );

        User user;
        if (type == UserTypeEnum.MEDIC) {
            user = new Medic(0, username, authenticationService.hashFunction(password),
                    new UserDetails(firstName, lastName, email, bio));
        } else {
            user = new Patient(0, username, authenticationService.hashFunction(password),
                    new UserDetails(firstName, lastName, email, bio));
        }
        try {
            userDao.createUser(user);
        } catch (SQLException e) {
            loggingService.log(entry, null, LoggingService.Status.BAD_REQUEST);
            throw e;
        }
        loggingService.log(entry, username, LoggingService.Status.OK);
        return user;
    }

    public String authenticateUser(String username, String password) throws NotFoundException, SQLException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.AUTHENTICATE,
                this.getClass().getName(),
                "User tries to login"
        );
        User user;
        try {
            user = userDao.getUserByUsername(username);
        } catch (SQLException | NotFoundException e) {
            loggingService.log(entry, username, LoggingService.Status.UNAUTHORIZED);
            throw e;
        }
        String token = authenticationService.authenticateUser(user, password);
        if (token == null)
            loggingService.log(entry, username, LoggingService.Status.UNAUTHORIZED);
        else
            loggingService.log(entry, username, LoggingService.Status.OK);
        return token;
    }
}
