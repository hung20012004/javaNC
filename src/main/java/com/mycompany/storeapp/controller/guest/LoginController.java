/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.guest;

import com.mycompany.storeapp.controller.admin.*;

/**
 *
 * @author Manh Hung
 */
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.UserDAO;
import com.mycompany.storeapp.model.entity.User;

public class LoginController {
    public String login(String email, String password) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        UserDAO userDAO = new UserDAO(dbConn);
        User user = userDAO.findByEmailAndPassword(email, password);
        dbConn.closeConnection();
        if (user != null) {
            return "Đăng nhập thành công!";
        } else {
            return "Tên đăng nhập hoặc mật khẩu không đúng.";
        }
    }
}
