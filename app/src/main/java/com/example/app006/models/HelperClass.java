package com.example.app006.models;

public class HelperClass {
    private String name, email, username, password, userType;

    public HelperClass() {
        // Default constructor required for Firebase
    }

    public HelperClass(String name, String email, String username, String userType, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.userType = userType;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
