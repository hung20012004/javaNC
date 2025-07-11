package com.mycompany.storeapp.view.page.admin.Staff;

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

public class StaffFormDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JComboBox<String> genderComboBox;
    private JDateChooser dateOfBirthChooser;
    private JComboBox<String> roleComboBox;
    private JCheckBox isActiveCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private User staff;
    private boolean isEditMode;
    private boolean confirmed = false;

    // Màu sắc cho giao diện
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);
    private static final Color DARK_GRAY = new Color(127, 140, 141);

    private final String[] genders = {"", "Nam", "Nữ", "Khác"};
    private final String[] roles = {"Admin", "Quản lý", "Nhân viên bán hàng", "Nhân viên kho"};
    private final int[] roleIds = {1, 2, 3, 4};

    public StaffFormDialog(JFrame parent, String title, User staff) {
        super(parent, title, true);
        this.staff = staff != null ? staff : new User();
        this.isEditMode = (staff != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 800);
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
        
        // Password field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
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
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setPreferredSize(new Dimension(0, 40));
        genderComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderComboBox.setBackground(Color.WHITE);
        genderComboBox.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        
        // Role combo box
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setPreferredSize(new Dimension(0, 40));
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        
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
        JLabel titleLabel = new JLabel(isEditMode ? "CẬP NHẬT THÔNG TIN NHÂN VIÊN" : "THÊM NHÂN VIÊN MỚI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Account info section
        JPanel accountSection = createSection("THÔNG TIN TÀI KHOẢN");
        accountSection.add(createFieldPanel("Email *", emailField));
        accountSection.add(Box.createVerticalStrut(15));
        if (!isEditMode) {
            accountSection.add(createFieldPanel("Mật khẩu *", passwordField));
            accountSection.add(Box.createVerticalStrut(15));
        }
        accountSection.add(createFieldPanel("Vai trò *", roleComboBox));
        
        mainPanel.add(accountSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // Personal info section
        JPanel personalSection = createSection("THÔNG TIN CÁ NHÂN");
        personalSection.add(createFieldPanel("Họ và tên *", fullNameField));
        personalSection.add(Box.createVerticalStrut(15));
        personalSection.add(createFieldPanel("Số điện thoại *", phoneField));
        personalSection.add(Box.createVerticalStrut(15));
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
                saveStaff();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        // Add focus listeners for better UX
        addFocusListener(emailField);
        addFocusListener(fullNameField);
        addFocusListener(phoneField);
        addPasswordFocusListener(passwordField);
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

    private void addPasswordFocusListener(JPasswordField field) {
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
            emailField.setText(staff.getEmail());
            isActiveCheckBox.setSelected(staff.getIs_active());
            
            // Set role
            for (int i = 0; i < roleIds.length; i++) {
                if (roleIds[i] == staff.getRole()) {
                    roleComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            UserProfile profile = staff.getProfile();
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
        // Validate email
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

        // Validate password for new staff
        if (!isEditMode && passwordField.getPassword().length == 0) {
            showErrorMessage("Mật khẩu không được để trống!");
            passwordField.requestFocus();
            return false;
        }

        if (!isEditMode && passwordField.getPassword().length < 6) {
            showErrorMessage("Mật khẩu phải có ít nhất 6 ký tự!");
            passwordField.requestFocus();
            return false;
        }

        // Validate full name
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

        // Validate phone
        if (phoneField.getText().trim().isEmpty()) {
            showErrorMessage("Số điện thoại không được để trống!");
            phoneField.requestFocus();
            return false;
        }

        if (!isValidPhoneNumber(phoneField.getText().trim())) {
            showErrorMessage("Số điện thoại không hợp lệ! (Phải có 10 - 11 chữ số)");
            phoneField.requestFocus();
            return false;
        }

        // Validate role
        if (roleComboBox.getSelectedIndex() == -1) {
            showErrorMessage("Vui lòng chọn vai trò!");
            roleComboBox.requestFocus();
            return false;
        }

        // Validate date of birth
        if (dateOfBirthChooser.getDate() != null) {
            LocalDate birthDate = dateOfBirthChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now();
            LocalDate minAge = now.minusYears(65);
            LocalDate maxAge = now.minusYears(18);
            
            if (birthDate.isBefore(minAge) || birthDate.isAfter(maxAge)) {
                showErrorMessage("Ngày sinh không hợp lệ! (Tuổi từ 18 đến 65)");
                return false;
            }
        }

        return true;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
    }

    private void saveStaff() {
        // Tạo username từ email (phần trước @)
        String email = emailField.getText().trim().toLowerCase();
        String username = email.substring(0, email.indexOf('@'));
        
        staff.setName(username);
        staff.setEmail(email);
        staff.setIs_active(isActiveCheckBox.isSelected());
        
        // Set password for new staff
        if (!isEditMode) {
            staff.setPassword(new String(passwordField.getPassword()));
        }
        
        // Set role
        int selectedRoleIndex = roleComboBox.getSelectedIndex();
        staff.setRole(roleIds[selectedRoleIndex]);
        staff.setRole_id(roleIds[selectedRoleIndex]);

        UserProfile profile = staff.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            staff.setProfile(profile);
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

    public User getStaff() { 
        return staff; 
    }
    
    public boolean isConfirmed() { 
        return confirmed; 
    }
}