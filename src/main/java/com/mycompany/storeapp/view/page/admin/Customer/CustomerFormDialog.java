package com.mycompany.storeapp.view.page.admin.Customer;

import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.model.entity.UserProfile;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class CustomerFormDialog extends JDialog {
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JComboBox<String> genderComboBox;
    private JDateChooser dateOfBirthChooser;
    private JCheckBox isActiveCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private User customer;
    private boolean isEditMode;
    private boolean confirmed = false;

    // Màu sắc cho giao diện
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color DARK_GRAY = new Color(127, 140, 141);

    public CustomerFormDialog(JFrame parent, String title, User customer) {
        super(parent, title, true);
        this.customer = customer != null ? customer : new User();
        this.isEditMode = (customer != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 760);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        // Set icon và styling cho dialog
        setBackground(Color.WHITE);
        getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, PRIMARY_COLOR));
    }

    private void initComponents() {
        // Email field
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(0, 40));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Full name field
        fullNameField = new JTextField();
        fullNameField.setPreferredSize(new Dimension(0, 40));
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Phone field
        phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(0, 40));
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Gender combo box
        genderComboBox = new JComboBox<>(new String[]{"", "Nam", "Nữ", "Khác"});
        genderComboBox.setPreferredSize(new Dimension(0, 40));
        genderComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderComboBox.setBackground(Color.WHITE);
        genderComboBox.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        
        // Date chooser
        dateOfBirthChooser = new JDateChooser();
        dateOfBirthChooser.setPreferredSize(new Dimension(0, 40));
        dateOfBirthChooser.setDateFormatString("dd/MM/yyyy");
        dateOfBirthChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateOfBirthChooser.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        
        // Active checkbox
        isActiveCheckBox = new JCheckBox("Tài khoản hoạt động");
        isActiveCheckBox.setSelected(true);
        isActiveCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        isActiveCheckBox.setForeground(SUCCESS_COLOR);
        isActiveCheckBox.setBackground(Color.WHITE);
        isActiveCheckBox.setFocusPainted(false);

        // Save button
        saveButton = new JButton(isEditMode ? "CẬP NHẬT" : "THÊM MỚI");
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Cancel button
        cancelButton = new JButton("HỦY");
        cancelButton.setPreferredSize(new Dimension(80, 40));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelButton.setBackground(DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects
        addButtonHoverEffect(saveButton, SUCCESS_COLOR);
        addButtonHoverEffect(cancelButton, DANGER_COLOR);
    }

    private void addButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(isEditMode ? "CẬP NHẬT THÔNG TIN KHÁCH HÀNG" : "THÊM KHÁCH HÀNG MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Contact info section
        JPanel contactSection = createSection("THÔNG TIN LIÊN HỆ");
        contactSection.add(createFieldPanel("Email *", emailField));
        contactSection.add(Box.createVerticalStrut(15));
        contactSection.add(createFieldPanel("Họ và tên *", fullNameField));
        contactSection.add(Box.createVerticalStrut(15));
        contactSection.add(createFieldPanel("Số điện thoại", phoneField));
        
        mainPanel.add(contactSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // Personal info section
        JPanel personalSection = createSection("THÔNG TIN CÁ NHÂN");
        personalSection.add(createFieldPanel("Giới tính", genderComboBox));
        personalSection.add(Box.createVerticalStrut(15));
        personalSection.add(createFieldPanel("Ngày sinh", dateOfBirthChooser));
        
        mainPanel.add(personalSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // Status section
        JPanel statusSection = createSection("TRẠNG THÁI");
        statusSection.add(isActiveCheckBox);
        
        mainPanel.add(statusSection);
        mainPanel.add(Box.createVerticalStrut(25));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add padding to button panel
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
    }

    private JPanel createSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            SECONDARY_COLOR
        );
        
        section.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));
        
        return section;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(DARK_GRAY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveCustomer();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        // Add focus listeners for better UX
        addFocusListener(emailField);
        addFocusListener(fullNameField);
        addFocusListener(phoneField);
    }

    private void addFocusListener(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    private void loadData() {
        if (isEditMode) {
            emailField.setText(customer.getEmail());
            isActiveCheckBox.setSelected(customer.getIs_active());
            
            UserProfile profile = customer.getProfile();
            if (profile != null) {
                fullNameField.setText(profile.getFullName());
                phoneField.setText(profile.getPhone());
                
                String gender = "";
                if ("male".equals(profile.getGender())) gender = "Nam";
                else if ("female".equals(profile.getGender())) gender = "Nữ";
                else if ("other".equals(profile.getGender())) gender = "Khác";
                genderComboBox.setSelectedItem(gender);
                
                if (profile.getDateOfBirth() != null) {
                    Date date = Date.from(profile.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    dateOfBirthChooser.setDate(date);
                }
            }
        }
    }

    private boolean validateForm() {
        if (emailField.getText().trim().isEmpty()) {
            showErrorMessage("Email không được để trống!");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showErrorMessage("Email không hợp lệ!");
            emailField.requestFocus();
            return false;
        }

        if (fullNameField.getText().trim().isEmpty()) {
            showErrorMessage("Họ và tên không được để trống!");
            fullNameField.requestFocus();
            return false;
        }

        if (fullNameField.getText().trim().length() > 100) {
            showErrorMessage("Họ và tên không được vượt quá 100 ký tự!");
            fullNameField.requestFocus();
            return false;
        }

        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
            showErrorMessage("Số điện thoại không hợp lệ! (Phải có 10 - 11 chữ số)");
            phoneField.requestFocus();
            return false;
        }

        if (dateOfBirthChooser.getDate() != null) {
            LocalDate birthDate = dateOfBirthChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now();
            LocalDate minAge = now.minusYears(120);
            LocalDate maxAge = now.minusYears(13);
            
            if (birthDate.isBefore(minAge) || birthDate.isAfter(maxAge)) {
                showErrorMessage("Ngày sinh không hợp lệ! (Tuổi từ 13 đến 120)");
                return false;
            }
        }

        return true;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
    }

    private void saveCustomer() {
        // Tạo username từ email (phần trước @)
        String email = emailField.getText().trim().toLowerCase();
        String username = email.substring(0, email.indexOf('@'));
        
        customer.setName(username);
        customer.setEmail(email);
        customer.setRole(5); // Customer role
        customer.setIs_active(isActiveCheckBox.isSelected());
        
        // Set default password nếu là customer mới
        if (!isEditMode) {
            customer.setPassword("123456"); // Default password
        }

        UserProfile profile = customer.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            customer.setProfile(profile);
        }

        profile.setFullName(fullNameField.getText().trim());
        profile.setPhone(phoneField.getText().trim());
        
        String selectedGender = (String) genderComboBox.getSelectedItem();
        String genderValue = "";
        if ("Nam".equals(selectedGender)) {
            genderValue = "male";
        } else if ("Nữ".equals(selectedGender)) {
            genderValue = "female";
        } else if ("Khác".equals(selectedGender)) {
            genderValue = "other";
        }
        profile.setGender(genderValue);
        
        if (dateOfBirthChooser.getDate() != null) {
            LocalDate birthDate = dateOfBirthChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            profile.setDateOfBirth(birthDate);
        }

        confirmed = true;
        dispose();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidPhoneNumber(String phone) {
        String phonePattern = "^[0-9]{10,11}$";
        return Pattern.matches(phonePattern, phone);
    }

    public User getCustomer() { 
        return customer; 
    }
    
    public boolean isConfirmed() { 
        return confirmed; 
    }
}