/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.data;

import com.raven.data.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String FILE_PATH = "users.txt";
    
    // Save all users to file
    public static void saveUsers(List<User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.write(user.toFileString());
                writer.newLine();
            }
        }
    }
    
    // Load all users from file
    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            // Create file if it doesn't exist
            file.createNewFile();
            // Add default admin user
            users.add(new User("admin", "John", "hihi","Admin"));
            saveUsers(users);
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }
    
    // Add a new user
    public static void addUser(User user) throws IOException {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }
    
    // Find user by workID
    public static User findUser(String workID) throws IOException {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getWorkID().equals(workID)) {
                return user;
            }
        }
        return null;
    }
    
    
}
