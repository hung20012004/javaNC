/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.guest;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.UserDAO;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;

/**
 *
 * @author Manh Hung
 */
public class RegisterController {
    private final UserDAO userDAO;
    
    public RegisterController() {
        this.userDAO = new UserDAO(DatabaseConnection.getInstance());
    }
    
    public String register(String name, String email, String password, Integer roleId) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            return "Tên không được để trống.";
        }
        
        if (name.trim().length() < 2) {
            return "Tên phải có ít nhất 2 ký tự.";
        }
        
        if (name.trim().length() > 100) {
            return "Tên không được vượt quá 100 ký tự.";
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống.";
        }
        
        if (!isValidEmail(email.trim())) {
            return "Email không hợp lệ.";
        }
        
        // Validate password
        if (password == null || password.trim().isEmpty()) {
            return "Mật khẩu không được để trống.";
        }
        
        if (password.trim().length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        }
        
        // Check if email already exists
        if (userDAO.findByEmail(email.trim()) != null) {
            return "Email đã được sử dụng.";
        }
        
        // Create new user
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(password.trim());
        user.setRole(roleId != null ? roleId : 0); 
        
        boolean success = userDAO.save(user);
        if (success) {
            // Set current user in session
            Session.getInstance().setCurrentUser(user);
            return "Đăng ký thành công!";
        } else {
            return "Đăng ký thất bại. Vui lòng thử lại.";
        }
    }
    
    /**
     * Validate email format using regex
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Backward compatibility - method without name parameter
     * @deprecated Use register(String name, String email, String password, Integer roleId) instead
     */
    @Deprecated
    public String register(String email, String password, Integer roleId) {
        return register("", email, password, roleId);
    }
}