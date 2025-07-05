/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.session;

import com.mycompany.storeapp.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private static Session instance = new Session();
    private User currentUser;
    private List<SessionListener>listeners = new ArrayList<>();

    private Session() {}

    public static Session getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        notifySessionChanged();
    }

    public void clearSession() {
        this.currentUser = null;
        notifySessionChanged();
    }

    // Đăng ký listener
    public void addSessionListener(SessionListener listener) {
        listeners.add(listener);
    }

    // Thông báo khi session thay đổi
    private void notifySessionChanged() {
        for (SessionListener listener : listeners) {
            listener.onSessionChanged(currentUser);
        }
    }
}
