package service;

import model.UserTypeEnum;

import java.sql.*;

public class DbService {
    private static DbService instance = null;

    private DbService() {}

    public static DbService getInstance() {
        if (instance == null)
            instance = new DbService();
        return instance;
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/jmed?" +
                    "user=root&password=123456");
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user");
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                UserTypeEnum userTypeEnum = UserTypeEnum.valueOf(resultSet.getString("type"));
                System.out.println(username + " " + userTypeEnum.name());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
