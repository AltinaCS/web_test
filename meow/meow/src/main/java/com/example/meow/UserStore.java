package com.example.meow;
import org.mindrot.jbcrypt.BCrypt;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserStore {
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, String> sessions = new HashMap<>();

    public static boolean addUser(String username, String email, String plainPassword) {
        if (users.containsKey(username)) return false;

        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        users.put(username, new User(username, email, hashedPassword));
        System.out.println(hashedPassword);
        return true;
    }

    public static boolean validateUser(String username, String plainPassword) {
        User user = users.get(username);
        if (user == null) return false;

        // Compare hashed password with input password
        return BCrypt.checkpw(plainPassword, user.getPassword());
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