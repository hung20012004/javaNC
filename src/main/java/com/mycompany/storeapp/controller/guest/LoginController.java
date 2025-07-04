package com.mycompany.storeapp.controller.guest;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.UserDAO;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;

public class LoginController {
    public String login(String email, String password) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        UserDAO userDAO = new UserDAO(dbConn);
        User user = userDAO.findByEmailAndPassword(email, password);
        dbConn.closeConnection();
        
        if (user != null) {
            Session.getInstance().setCurrentUser(user);
            return "Đăng nhập thành công!";
        } else {
            return "Tên đăng nhập hoặc mật khẩu không đúng.";
        }
    }

    public void logout() {
        Session.getInstance().clearSession();
    }
}