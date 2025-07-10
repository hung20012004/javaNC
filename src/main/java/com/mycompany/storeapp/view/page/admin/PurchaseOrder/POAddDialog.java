/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.PurchaseOrder;

import com.mycompany.storeapp.controller.admin.PurchaseOrderController;
import com.mycompany.storeapp.model.entity.PurchaseOrder;
import com.mycompany.storeapp.model.entity.PurchaseOrderDetail;
import com.mycompany.storeapp.model.entity.Supplier;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.Size;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.dao.SupplierDAO;
import com.mycompany.storeapp.model.dao.SizeDAO;
import com.mycompany.storeapp.model.dao.ColorDAO;
import com.mycompany.storeapp.model.dao.ProductDAO;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Dialog để thêm đơn nhập hàng mới với bảng sản phẩm và nhà cung cấp
 * @author ADMIN
 */
public class POAddDialog extends JDialog {
    private final PurchaseOrderController purchaseOrderController;
    private final SupplierDAO supplierDAO;
    private final ProductVariantDAO productVariantDAO;
    private final SizeDAO sizeDAO;
    private final ColorDAO colorDAO;
    private final ProductDAO productDAO;
    private final Runnable onSaveCallback;
    private JComboBox<Supplier> supplierComboBox;
    private JTextField createdByUserIdField;
    private JTextField totalAmountField;
    private JTextArea noteArea;
    private JDateChooser orderDateChooser;
    private JDateChooser expectedDateChooser;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JButton addProductButton;
    private JButton saveButton;
    private JButton cancelButton;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private final int currentUserId = 1; // Giả định lấy từ session, thay bằng logic thực tế
    private List<PurchaseOrderDetail> productList = new ArrayList<>();

    public POAddDialog(JFrame parent, String title, PurchaseOrder purchaseOrder, PurchaseOrderController purchaseOrderController, Runnable onSaveCallback) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.purchaseOrderController = purchaseOrderController;
        this.supplierDAO = new SupplierDAO(new DatabaseConnection());
        this.productVariantDAO = new ProductVariantDAO(new DatabaseConnection());
        this.sizeDAO = new SizeDAO(new DatabaseConnection());
        this.colorDAO = new ColorDAO(new DatabaseConnection());
        this.productDAO = new ProductDAO(new DatabaseConnection());
        this.onSaveCallback = onSaveCallback;
        initComponents();
        setupLayout();
        setupEventHandlers();
        setSize(900, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        // Combo box nhà cung cấp (hiển thị tên, lưu ID)
        supplierComboBox = new JComboBox<>(supplierDAO.getAll().toArray(new Supplier[0]));
        supplierComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier) {
                    setText(((Supplier) value).getName());
                }
                return this;
            }
        });

        createdByUserIdField = new JTextField(String.valueOf(currentUserId), 20);
        createdByUserIdField.setEditable(false); // Không cho sửa mã người tạo
        totalAmountField = new JTextField(20);
        totalAmountField.setEditable(false); // Tự động tính
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        orderDateChooser = new JDateChooser();
        orderDateChooser.setDateFormatString("dd/MM/yyyy");
        expectedDateChooser = new JDateChooser();
        expectedDateChooser.setDateFormatString("dd/MM/yyyy");

        // Bảng sản phẩm với tìm kiếm và chọn
        String[] columnNames = {"Tên sản phẩm", "Đơn giá", "Số lượng", "Tên Size", "Tên Color", "Thành tiền"};
        productTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2; // Chỉ cho phép nhập đơn giá và số lượng
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(25);
        productTable.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer());
        productTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setPreferredSize(new Dimension(850, 200));

        addProductButton = new JButton("Thêm sản phẩm");
        saveButton = new JButton("Lưu đơn nhập");
        saveButton.setBackground(new java.awt.Color(46, 204, 113));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(120, 35));
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new java.awt.Color(231, 76, 60));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 35));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Thêm đơn nhập hàng mới"), gbc);
        headerPanel.getComponent(0).setFont(new Font("Segoe UI", Font.BOLD, 16));

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin đơn hàng", createOrderInfoPanel());
        tabbedPane.addTab("Danh sách sản phẩm", createProductsPanel());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createOrderInfoPanel() {
        JPanel orderPanel = new JPanel(new GridBagLayout());
        orderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        orderPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(supplierComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Mã người tạo:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(createdByUserIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Tổng giá trị:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(totalAmountField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Ngày đặt hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(orderDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Ngày dự kiến:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(expectedDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        JScrollPane notesScroll = new JScrollPane(noteArea);
        notesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        orderPanel.add(notesScroll, gbc);

        return orderPanel;
    }

    private JPanel createProductsPanel() {
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Danh sách sản phẩm");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        productsPanel.add(titleLabel, BorderLayout.NORTH);
        productsPanel.add(addProductButton, BorderLayout.WEST);
        productsPanel.add(createProductTablePanel(), BorderLayout.CENTER);

        return productsPanel;
    }

    private JPanel createProductTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        return buttonPanel;
    }

   private void setupEventHandlers() {
    addProductButton.addActionListener(e -> {
        showAddProductDialog();
    });
    saveButton.addActionListener(e -> {
        savePurchaseOrder();
    });
    cancelButton.addActionListener(e -> dispose());

    // Thêm sự kiện long press để xóa sản phẩm
    productTable.addMouseListener(new MouseAdapter() {
        private long pressStartTime = 0;
        private static final long LONG_PRESS_THRESHOLD = 1000; // 1 giây

        @Override
        public void mousePressed(MouseEvent e) {
            pressStartTime = System.currentTimeMillis();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            long pressDuration = System.currentTimeMillis() - pressStartTime;
            if (pressDuration >= LONG_PRESS_THRESHOLD) {
                int row = productTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < productList.size()) {
                    int option = JOptionPane.showConfirmDialog(POAddDialog.this,
                            "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        productList.remove(row);
                        updateProductTable();
                    }
                }
            }
        }
    });

    getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    getRootPane().setDefaultButton(saveButton);
}

private void showAddProductDialog() {
    try {
        JDialog addProductDialog = new JDialog(this, "Thêm sản phẩm", true);
        addProductDialog.setSize(400, 300);
        addProductDialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        
        JComboBox<Product> productComboBox = new JComboBox<>(productDAO.getAllProducts().toArray(new Product[0]));
        productComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getName());
                }
                return this;
            }
        });

    
        JComboBox<Size> sizeComboBox = new JComboBox<>();
        sizeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Size) {
                    setText(((Size) value).getName());
                }
                return this;
            }
        });

        JComboBox<Color> colorComboBox = new JComboBox<>();
        colorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Color) {
                    setText(((Color) value).getName());
                }
                return this;
            }
        });


        JTextField quantityField = new JTextField(10);
        JTextField unitPriceField = new JTextField(10);

     
        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            System.out.println("Selected Product: " + (selectedProduct != null ? selectedProduct.getName() : "null"));
            if (selectedProduct != null) {
                int productId = (int) selectedProduct.getProductId();
                System.out.println("Product ID: " + productId);
                List<ProductVariant> variants = productVariantDAO.getVariantsByProductId(productId);
                System.out.println("Number of variants: " + (variants != null ? variants.size() : 0));
                sizeComboBox.removeAllItems();
                colorComboBox.removeAllItems();
                for (ProductVariant variant : variants) {
                    Size size = sizeDAO.getById(variant.getSizeId());
                    Color color = colorDAO.getById(variant.getColorId());
                    System.out.println("Variant: " + variant.getVariantId() + ", Size ID: " + variant.getSizeId() + ", Color ID: " + variant.getColorId());
                    if (size != null && !comboBoxContainsItem(sizeComboBox, size)) {
                        sizeComboBox.addItem(size);
                    }
                    if (color != null && !comboBoxContainsColor(colorComboBox, color)) {
                        colorComboBox.addItem(color);
                    }
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        dialogPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(productComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialogPanel.add(new JLabel("Kích cỡ:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(sizeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialogPanel.add(new JLabel("Màu sắc:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(colorComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialogPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialogPanel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(unitPriceField, gbc);

        JButton saveDialogButton = new JButton("Lưu");
        saveDialogButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                Product selectedProduct = (Product) productComboBox.getSelectedItem();
                Size selectedSize = (Size) sizeComboBox.getSelectedItem();
                Color selectedColor = (Color) colorComboBox.getSelectedItem();

                System.out.println("Selected: Product=" + (selectedProduct != null ? selectedProduct.getName() : "null") +
                        ", Size=" + (selectedSize != null ? selectedSize.getName() : "null") +
                        ", Color=" + (selectedColor != null ? selectedColor.getName() : "null") +
                        ", Quantity=" + quantity + ", UnitPrice=" + unitPrice);

                if (quantity <= 0 || unitPrice <= 0) {
                    JOptionPane.showMessageDialog(addProductDialog, "Số lượng và đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selectedProduct == null || selectedSize == null || selectedColor == null) {
                    JOptionPane.showMessageDialog(addProductDialog, "Vui lòng chọn đầy đủ sản phẩm, kích cỡ và màu sắc!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ProductVariant variant = productVariantDAO.getVariantByProductSizeColor((int) selectedProduct.getProductId(), selectedSize.getSizeId(), (int) selectedColor.getColorId());
                if (variant == null) {
                    JOptionPane.showMessageDialog(addProductDialog, "Biến thể sản phẩm không tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Tạo PurchaseOrderDetail với thông tin tạm thời
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setProductId(variant.getVariantId()); // Sử dụng variantId làm productId tạm thời
                detail.setUnitPrice(unitPrice);
                detail.setQuantity(quantity);
                detail.setSubTotal(unitPrice * quantity);
                detail.setProductName(selectedProduct.getName()); // Lưu tên sản phẩm tạm thời
                detail.setSizeName(selectedSize.getName()); // Lưu tên Size tạm thời
                detail.setColorName(selectedColor.getName()); // Lưu tên Color tạm thời

                productList.add(detail);
                updateProductTable();
                updateTotalAmount();
                addProductDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addProductDialog, "Vui lòng nhập đơn giá là số hợp lệ (ví dụ: 3000)!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                System.err.println("Number format error: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addProductDialog, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
                System.err.println("Error in saveDialogButton: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        dialogPanel.add(saveDialogButton, gbc);

        addProductDialog.add(dialogPanel);
        addProductDialog.setVisible(true);
    } catch (Exception ex) {
        System.err.println("Error in showAddProductDialog: " + ex.getMessage());
        ex.printStackTrace();
    }
}

private void updateProductTable() {
    productTableModel.setRowCount(0);
    for (PurchaseOrderDetail detail : productList) {
        productTableModel.addRow(new Object[]{
            detail.getProductName(), // Tên sản phẩm tạm thời
            currencyFormat.format(detail.getUnitPrice()),
            detail.getQuantity(),
            detail.getSizeName(), // Tên Size tạm thời
            detail.getColorName(), // Tên Color tạm thời
            currencyFormat.format(detail.getSubTotal())
        });
    }
}

    private void updateTotalAmount() {
        double total = productList.stream().mapToDouble(PurchaseOrderDetail::getSubTotal).sum();
        totalAmountField.setText(currencyFormat.format(total));
    }

    private void savePurchaseOrder() {
        try {
            System.out.println("Starting savePurchaseOrder...");
            if (!validateInput()) {
                System.out.println("Validation failed.");
                return;
            }

            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            saveButton.setText("Đang lưu...");

            PurchaseOrder newPO = new PurchaseOrder();
            newPO.setSupplierId(((Supplier) supplierComboBox.getSelectedItem()).getSupplierId());
            newPO.setCreatedByUserId(currentUserId);
            newPO.setTotalAmount(currencyFormat.parse(totalAmountField.getText().trim()).doubleValue());
            newPO.setNote(noteArea.getText().trim());
            newPO.setStatus(PurchaseOrderController.STATUS_PENDING);
            java.util.Date orderDateUtil = orderDateChooser.getDate();
            Timestamp orderDate = orderDateUtil != null ? new Timestamp(orderDateUtil.getTime()) : new Timestamp(System.currentTimeMillis());
            newPO.setOrderDate(orderDate);

            java.util.Date expectedDateUtil = expectedDateChooser.getDate();
            Timestamp expectedDate = expectedDateUtil != null ? new Timestamp(expectedDateUtil.getTime()) : new Timestamp(System.currentTimeMillis());
            newPO.setExpectedDate(expectedDate);

            // Gán createdAt và updatedAt bằng thời gian hiện tại dưới dạng Timestamp
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            newPO.setCreatedAt(currentTimestamp);
            newPO.setUpdatedAt(currentTimestamp);

            newPO.setDetails(productList);

            System.out.println("Purchase Order: SupplierID=" + newPO.getSupplierId() + ", Total=" + newPO.getTotalAmount() +
                    ", Status=" + newPO.getStatus() + ", OrderDate=" + (newPO.getOrderDate() != null ? dateFormatter.format(newPO.getOrderDate()) : "null") +
                    ", ExpectedDate=" + (newPO.getExpectedDate() != null ? dateFormatter.format(newPO.getExpectedDate()) : "null"));
            System.out.println("Number of details: " + (productList != null ? productList.size() : 0));

            if (purchaseOrderController.createPurchaseOrder(newPO, productList)) {
                System.out.println("Purchase order saved successfully. PO ID: " + newPO.getPoId());
                if (newPO.getStatus().equals(PurchaseOrderController.STATUS_COMPLETED)) {
                    for (PurchaseOrderDetail detail : productList) {
                        ProductVariant variant = productVariantDAO.getVariantById(detail.getProductId());
                        if (variant != null) {
                            variant.setStockQuantity(variant.getStockQuantity() + detail.getQuantity());
                            productVariantDAO.updateProductVariant(variant);
                            System.out.println("Updated stock for variant ID: " + variant.getVariantId());
                        } else {
                            System.out.println("Variant not found for detail ID: " + detail.getProductId());
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Thêm đơn nhập hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (onSaveCallback != null) {
                    System.out.println("Calling onSaveCallback...");
                    onSaveCallback.run();
                }
                dispose();
            } else {
                System.out.println("Failed to save purchase order. Check PurchaseOrderController.");
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi thêm đơn nhập hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error in savePurchaseOrder: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            saveButton.setText("Lưu đơn nhập");
        }
    }

    private boolean validateInput() {
        System.out.println("Validating input...");
        if (supplierComboBox.getSelectedItem() == null) {
            System.out.println("No supplier selected.");
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (orderDateChooser.getDate() == null || expectedDateChooser.getDate() == null) {
            System.out.println("Date fields are null: Order=" + orderDateChooser.getDate() + ", Expected=" + expectedDateChooser.getDate());
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đặt và ngày dự kiến!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (productList.isEmpty()) {
            System.out.println("No products in list.");
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        System.out.println("Validation passed.");
        return true;
    }

    // Renderer để hiển thị tiền tệ
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = formatter.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    private boolean comboBoxContainsColor(JComboBox<Color> comboBox, Color item) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
        if (comboBox.getItemAt(i).equals(item)) {
            return true;
        }
    }
    return false;
}
    private boolean comboBoxContainsItem(JComboBox<Size> comboBox, Size item) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
        if (comboBox.getItemAt(i).equals(item)) {
            return true;
        }
    }
    return false;
}
    
}