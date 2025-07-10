package com.mycompany.storeapp.view.component.shop.order;

import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;
import com.mycompany.storeapp.session.SessionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CustomerInfoPanel extends JPanel implements SessionListener {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField customerIdField;
    
    private Session session;

    public CustomerInfoPanel() {
        this.session = Session.getInstance();
        this.session.addSessionListener(this); // Đăng ký listener để nhận thông báo khi session thay đổi
        initializePanel();
        setupComponents();
        setupLayout();
        loadCurrentUserInfo(); // Tự động load thông tin user khi khởi tạo
    }

    private void initializePanel() {
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "👤 Thông tin khách hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
    }

    private void setupComponents() {
        // ID khách hàng
        customerIdField = new JTextField();
        customerIdField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        customerIdField.setEditable(false); // ID không cho chỉnh sửa
        customerIdField.setBackground(new Color(249, 250, 251));

        // Tên khách hàng
        customerNameField = new JTextField();
        customerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Email
        customerEmailField = new JTextField();
        customerEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // ID khách hàng
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerIdField, gbc);

        // Tên khách hàng
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerEmailField, gbc);
    }

    /**
     * Load thông tin của user hiện tại vào form từ Session
     */
    private void loadCurrentUserInfo() {
        User currentUser = session.getCurrentUser();
        
        if (currentUser != null) {
            // Điền thông tin vào các field
            customerIdField.setText(String.valueOf(currentUser.getId()));
            customerNameField.setText(currentUser.getName());
            customerEmailField.setText(currentUser.getEmail());
        } else {
            // Nếu không có user, clear form
            resetForm();
        }
    }

    /**
     * Implement SessionListener - được gọi khi session thay đổi
     */
    @Override
    public void onSessionChanged(User user) {
        SwingUtilities.invokeLater(() -> {
            if (user != null) {
                // User đã đăng nhập, load thông tin
                customerIdField.setText(String.valueOf(user.getId()));
                customerNameField.setText(user.getName());
                customerEmailField.setText(user.getEmail());
            } else {
                // User đã logout, clear form
                resetForm();
            }
        });
    }

    /**
     * Reset form về trạng thái mặc định
     */
    public void resetForm() {
        customerIdField.setText("");
        customerNameField.setText("");
        customerEmailField.setText("");
    }

    // Getters
    public String getCustomerId() { return customerIdField.getText().trim(); }
    public String getCustomerName() { return customerNameField.getText().trim(); }
    public String getCustomerEmail() { return customerEmailField.getText().trim(); }

    // Setters
    public void setCustomerId(String id) { customerIdField.setText(id); }
    public void setCustomerName(String name) { customerNameField.setText(name); }
    public void setCustomerEmail(String email) { customerEmailField.setText(email); }

    // Validation
    public boolean validateFields() {
        if (customerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerNameField.requestFocus();
            return false;
        }
        
        // Validate email nếu có nhập
        String email = customerEmailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerEmailField.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Kiểm tra email có hợp lệ không
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Getter cho field (để focus khi cần)
    public JTextField getCustomerNameField() { return customerNameField; }
    public JTextField getCustomerEmailField() { return customerEmailField; }
    public JTextField getCustomerIdField() { return customerIdField; }
    
    /**
     * Cleanup khi component bị hủy
     */
    public void cleanup() {
        if (session != null) {
            // Có thể thêm method removeSessionListener nếu cần
            // session.removeSessionListener(this);
        }
    }
}