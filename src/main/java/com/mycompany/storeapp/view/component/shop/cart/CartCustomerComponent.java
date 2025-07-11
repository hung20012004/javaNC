package com.mycompany.storeapp.view.component.shop.cart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CartCustomerComponent extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JTextField customerField;
    private JTextArea notesArea;

    public CartCustomerComponent() {
        setupComponent();
    }

    private void setupComponent() {
        
    }

    public String getCustomerName() {
        return customerField.getText().trim();
    }

    public void setCustomerName(String name) {
        customerField.setText(name);
    }

    public String getNotes() {
        return notesArea.getText().trim();
    }

    public void setNotes(String notes) {
        notesArea.setText(notes);
    }

    public void reset() {
        customerField.setText("Khách lẻ");
        notesArea.setText("");
    }
}