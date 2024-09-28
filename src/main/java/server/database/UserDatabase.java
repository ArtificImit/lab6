package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.userspace.User;

import java.util.HashMap;

public class UserDatabase {
    private Connection connection;
    public UserDatabase() throws SQLException {
        String url = "jdbc:postgresql://localhost:8387/UserDatabase";
        String user = "s412968";
        String password = "dZEZ<0434";
        connection = DriverManager.getConnection(url, user, password);
    }
    public boolean registerUser(String username, String passwordHash) {
        String checkUserQuery = "SELECT username FROM users WHERE username = ?";
        String insertUserQuery = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {

            checkStmt.setString(1, username);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                return false; // Пользователь уже существует
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, passwordHash);
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean authenticateUser(String username, String passwordHash) {
        String selectUserQuery = "SELECT username, password_hash FROM users WHERE username = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectUserQuery)) {
            selectStmt.setString(1, username);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password_hash");
                if (storedPasswordHash.equals(passwordHash)) {
                    return true; // Аутентификация успешна
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Аутентификация не удалась
    }
}

