package service;

import config.Configuration;
import model.UserTypeEnum;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DbService {
    private static DbService instance = null;

    private static final int INITIAL_POOL_CAPACITY = 10;
    private LinkedList<Connection> pool;

    private DbService() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        pool = new LinkedList<>();
        for (int i = 0; i < INITIAL_POOL_CAPACITY; ++i) {
            try {
                pool.add(DriverManager.getConnection(Configuration.DB_CONNECTION_STRING));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (pool.isEmpty())
            pool.add(DriverManager.getConnection(Configuration.DB_CONNECTION_STRING));
        return pool.pop();
    }

    public synchronized void returnConnection(Connection connection) {
        pool.push(connection);
    }

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
