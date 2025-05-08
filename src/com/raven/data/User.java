package com.raven.data;

public class User {
    private String workID;
    private String password;
    private String role;
    private String name;  // Add this field
    
    public User(String workID, String name, String password, String role) {
        this.workID = workID;
        this.name = name;
        this.password = password;
        this.role = role;
    }
    
    // Add getter for name
    public String getName() { return name; }
    
    // Update file format methods
    public String toFileString() {
        return workID + "," + name + "," + password + "," + role;
    }
    
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length == 4) {
            return new User(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }

    public String getPassword() {
        return password;
    }
        

    public String getRole() {
        return role;
    }

    public String getWorkID() {
        return workID;
    }
    
    
}