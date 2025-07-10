package com.mycompany.storeapp.view.layer;

import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;
import com.mycompany.storeapp.session.SessionListener;
import com.mycompany.storeapp.view.layer.AdminLayer;
import com.mycompany.storeapp.view.layer.GuestLayer;
import com.mycompany.storeapp.view.layer.ShopLayer;

import javax.swing.*;

public class LayerManager implements SessionListener {
    private JFrame currentFrame;
    private Session session;

    public LayerManager() {
        this.session = Session.getInstance();
        this.session.addSessionListener(this);
        initializeLayer();
    }

    private void initializeLayer() {
        User currentUser = session.getCurrentUser();
        System.out.println("Initializing layer with user: " + (currentUser != null ? "Role=" + currentUser.getRole() : "null"));
        switchLayer(currentUser);
    }

    @Override
    public void onSessionChanged(User user) {
        System.out.println("Session changed with user: " + (user != null ? "Role=" + user.getRole() : "null"));
        SwingUtilities.invokeLater(() -> switchLayer(user));
    }

    private void switchLayer(User user) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        if (user == null) {
            System.out.println("Switching to GuestLayer");
            currentFrame = new GuestLayer(this);
        } else if (user.getRole() == 5) {
            System.out.println("Switching to ShopLayer for role: " + user.getRole());
            currentFrame = new ShopLayer(this);
        } else {
            System.out.println("Switching to AdminLayer for role: " + user.getRole());
            currentFrame = new AdminLayer(this);
        }

        currentFrame.setVisible(true);
    }

    public void login(User user) {
        System.out.println("Logging in user: " + 
    (user != null ? "Name=" + user.getName() + ", Role=" + user.getRole() : "null"));

        session.setCurrentUser(user);
    }

    public void logout() {
        System.out.println("Logging out user");
        session.clearSession();
    }
}