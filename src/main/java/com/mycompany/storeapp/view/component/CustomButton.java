/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component;

/**
 *
 * @author Manh Hung
 */
import javax.swing.JButton;
public class CustomButton extends JButton {
    public CustomButton(String text) {
        super(text);
        setFocusPainted(false);
        setBackground(new java.awt.Color(59, 89, 182));
        setForeground(java.awt.Color.WHITE);
        setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    }
}