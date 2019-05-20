package service;

import dao.UserDao;
import dao.exceptions.NotFoundException;
import dao.impl.UserDaoImpl;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

public class AuthenticationService {
    private static AuthenticationService instance = null;

    private AuthenticationService() {
    }

    public static AuthenticationService getInstance() {
        if (instance == null)
            instance = new AuthenticationService();
        return instance;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public String hashFunction(String password) {
        byte[] digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            digest = md.digest();
        } catch(NoSuchAlgorithmException exp) {
            digest = password.getBytes();
        }
        return toHexString(digest);
    }

    public String authenticateUser(User user, String password) {
        if (!hashFunction(password).equals(user.getHashPassword()))
            return null;
        Random random = new Random();
        String token = random.ints(48,122)
                .mapToObj(i -> (char) i)
                .limit(50)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        token = token + user.getId();
        byte[] encoding = Base64.getEncoder().encode(token.getBytes());
        return new String(encoding);
    }

    public int extractIdFromToken(String token) {
        byte[] decoding = Base64.getDecoder().decode(token.getBytes());
        String decodedToken = new String(decoding);
        return Integer.parseInt(decodedToken.substring(50));
    }

    public static void main(String[] args) throws NotFoundException, SQLException {
        AuthenticationService authenticationService = AuthenticationService.getInstance();
        UserDao userDao = new UserDaoImpl();
        String token = authenticationService.authenticateUser(userDao.getUserById(16), "parola");
        System.out.println(authenticationService.extractIdFromToken(token));
    }
}
