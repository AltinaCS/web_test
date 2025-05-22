package com.example.meow;

public class User {
    private String username;
    private String password;

    // Constructors, Getters, Setters
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
