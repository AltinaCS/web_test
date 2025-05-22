package com.example.meow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserStore {
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, String> sessions = new HashMap<>();

    public static boolean addUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        return true;
    }

    public static boolean validateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
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