package com.example.meow;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserStore {


    private static final Map<String, String> sessions = new HashMap<>();
    public static boolean addUser(String username, String email, String plainPassword) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.err.println("Username or email already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean validateUser(String username, String plainPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(plainPassword, hashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String createSession(String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, username);
        return token;
    }

    public static String getUsernameFromToken(String token) {
        return sessions.get(token);
    }

    public static void invalidateToken(String token) {
        sessions.remove(token);
    }
}