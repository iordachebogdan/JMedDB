package service;

public class AuthenticationService {
    private static AuthenticationService instance = null;

    private AuthenticationService() {
    }

    public static AuthenticationService getInstance() {
        if (instance == null)
            instance = new AuthenticationService();
        return instance;
    }
}
