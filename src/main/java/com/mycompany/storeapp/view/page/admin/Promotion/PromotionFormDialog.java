package com.mycompany.storeapp.view.page.admin.Promotion;

import com.mycompany.storeapp.model.entity.Promotion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class PromotionFormDialog extends JDialog {
    private JTextField codeField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> discountTypeCombo;
    private JTextField discountValueField;
    private JTextField minOrderValueField;
    private JTextField maxDiscountField;
    private JTextField usageLimitField;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JCheckBox activeCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private Promotion promotion;
    private boolean isEditMode;
    private boolean confirmed = false;

    public PromotionFormDialog(JFrame parent, String title, Promotion promotion) {
        super(parent, title, true);
        this.promotion = promotion != null ? promotion : new Promotion();
        this.isEditMode = (promotion != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        codeField = new JTextField();
        codeField.setPreferredSize(new Dimension(0, 35));
        
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(0, 35));
        
        descriptionArea = new JTextArea(3, 0);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        discountTypeCombo = new JComboBox<>(new String[]{"percent", "fixed"});
        discountTypeCombo.setPreferredSize(new Dimension(0, 35));
        
        discountValueField = new JTextField();
        discountValueField.setPreferredSize(new Dimension(0, 35));
        
        minOrderValueField = new JTextField();
        minOrderValueField.setPreferredSize(new Dimension(0, 35));
        
        maxDiscountField = new JTextField();
        maxDiscountField.setPreferredSize(new Dimension(0, 35));
        
        usageLimitField = new JTextField();
        usageLimitField.setPreferredSize(new Dimension(0, 35));

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd HH:mm"));
        startDateSpinner.setPreferredSize(new Dimension(0, 35));
        
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd HH:mm"));
        endDateSpinner.setPreferredSize(new Dimension(0, 35));

        activeCheckBox = new JCheckBox("Kích hoạt");
        activeCheckBox.setSelected(true);

        saveButton = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        saveButton.setBackground(new java.awt.Color(52, 152, 219));
        saveButton.setForeground(java.awt.Color.WHITE);
        
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new java.awt.Color(149, 165, 166));
        cancelButton.setForeground(java.awt.Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createFieldPanel("Mã khuyến mãi *", codeField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Tên khuyến mãi *", nameField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Mô tả", new JScrollPane(descriptionArea)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Loại giảm giá *", discountTypeCombo));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Giá trị giảm giá *", discountValueField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Giá trị đơn hàng tối thiểu", minOrderValueField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Giá trị giảm giá tối đa", maxDiscountField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Giới hạn sử dụng", usageLimitField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Ngày bắt đầu", startDateSpinner));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFieldPanel("Ngày kết thúc", endDateSpinner));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(activeCheckBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                savePromotion();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        if (isEditMode) {
            codeField.setText(promotion.getCode());
            nameField.setText(promotion.getName());
            descriptionArea.setText(promotion.getDescription());
            discountTypeCombo.setSelectedItem(promotion.getDiscountType());
            discountValueField.setText(String.valueOf(promotion.getDiscountValue()));
            minOrderValueField.setText(String.valueOf(promotion.getMinOrderValue()));
            maxDiscountField.setText(String.valueOf(promotion.getMaxDiscount()));
            usageLimitField.setText(String.valueOf(promotion.getUsageLimit()));
            
            if (promotion.getStartDate() != null) {
                startDateSpinner.setValue(java.sql.Timestamp.valueOf(promotion.getStartDate()));
            }
            if (promotion.getEndDate() != null) {
                endDateSpinner.setValue(java.sql.Timestamp.valueOf(promotion.getEndDate()));
            }
            
            activeCheckBox.setSelected(promotion.isActive());
        } else {
            minOrderValueField.setText("0");
            maxDiscountField.setText("0");
            usageLimitField.setText("0");
        }
    }

    private boolean validateForm() {
        if (codeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (codeField.getText().trim().length() > 50) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi không được vượt quá 50 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khuyến mãi không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().length() > 200) {
            JOptionPane.showMessageDialog(this, "Tên khuyến mãi không được vượt quá 200 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descriptionArea.getText().length() > 1000) {
            JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 1000 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double discountValue = Double.parseDouble(discountValueField.getText());
            if (discountValue <= 0) {
                JOptionPane.showMessageDialog(this, "Giá trị giảm giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (discountTypeCombo.getSelectedItem().equals("percent") && discountValue > 100) {
                JOptionPane.showMessageDialog(this, "Giá trị giảm giá theo phần trăm không được vượt quá 100!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị giảm giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double minOrderValue = Double.parseDouble(minOrderValueField.getText());
            if (minOrderValue < 0) {
                JOptionPane.showMessageDialog(this, "Giá trị đơn hàng tối thiểu không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị đơn hàng tối thiểu phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double maxDiscount = Double.parseDouble(maxDiscountField.getText());
            if (maxDiscount < 0) {
                JOptionPane.showMessageDialog(this, "Giá trị giảm giá tối đa không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị giảm giá tối đa phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int usageLimit = Integer.parseInt(usageLimitField.getText());
            if (usageLimit < 0) {
                JOptionPane.showMessageDialog(this, "Giới hạn sử dụng không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giới hạn sử dụng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void savePromotion() {
        promotion.setCode(codeField.getText().trim());
        promotion.setName(nameField.getText().trim());
        promotion.setDescription(descriptionArea.getText().trim());
        promotion.setDiscountType((String) discountTypeCombo.getSelectedItem());
        promotion.setDiscountValue(Double.parseDouble(discountValueField.getText()));
        promotion.setMinOrderValue(Double.parseDouble(minOrderValueField.getText()));
        promotion.setMaxDiscount(Double.parseDouble(maxDiscountField.getText()));
        promotion.setUsageLimit(Integer.parseInt(usageLimitField.getText()));
        
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        
        if (startDate != null) {
            promotion.setStartDate(new java.sql.Timestamp(startDate.getTime()).toLocalDateTime());
        }
        if (endDate != null) {
            promotion.setEndDate(new java.sql.Timestamp(endDate.getTime()).toLocalDateTime());
        }
        
        promotion.setActive(activeCheckBox.isSelected());
        
//        if (!isEditMode) {
//            promotion.setUsedCount(0);
//            promotion.setCreatedAt(LocalDateTime.now());
//            promotion.setUpdatedAt(LocalDateTime.now());
//        } else {
//            promotion.setUpdatedAt(LocalDateTime.now());
//        }
        
        confirmed = true;
        dispose();
    }

    public Promotion getPromotion() { 
        return promotion; 
    }
    
    public boolean isConfirmed() { 
        return confirmed; 
    }
}