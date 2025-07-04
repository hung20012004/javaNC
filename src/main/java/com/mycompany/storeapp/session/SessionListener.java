/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.session;

/**
 *
 * @author Manh Hung
 */
import com.mycompany.storeapp.model.entity.User;

public interface SessionListener {
    void onSessionChanged(User user);
}

