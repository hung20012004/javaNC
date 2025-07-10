package com.mycompany.storeapp.controller.shop;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.UserDAO;
import com.mycompany.storeapp.model.entity.User;

public class UserController {
    private UserDAO userDAO;
    private User currentUser;
    
    public UserController(DatabaseConnection dbConn) {
        this.userDAO = new UserDAO(dbConn);
        this.currentUser = null;
    }
    
    public boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        User user = userDAO.findByEmailAndPassword(email.trim(), password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    public boolean register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        if (userDAO.findByEmail(email.trim()) != null) {
            return false;
        }
        
        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(password);
        newUser.setRole(5);
        
        boolean saved = userDAO.save(newUser);
        if (saved) {
            this.currentUser = userDAO.findByEmail(email.trim());
        }
        return saved;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }
    
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : null;
    }
    
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    public int getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : 0;
    }
    
    public boolean updateUserInfo(String name, String email) {
        if (currentUser == null) {
            return false;
        }
        
        if (name != null && !name.trim().isEmpty()) {
            currentUser.setName(name.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(currentUser.getEmail())) {
                User existingUser = userDAO.findByEmail(email.trim());
                if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                    return false;
                }
            }
            currentUser.setEmail(email.trim());
        }
        
        return true;
    }
    
    public boolean isEmailExists(String email) {
        return userDAO.findByEmail(email) != null;
    }
}