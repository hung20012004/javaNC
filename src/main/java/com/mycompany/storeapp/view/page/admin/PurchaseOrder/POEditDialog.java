/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.PurchaseOrder;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.controller.admin.PurchaseOrderController;
import com.mycompany.storeapp.controller.admin.PurchaseOrderController.POTransitionResult;
import com.mycompany.storeapp.model.dao.ColorDAO;
import com.mycompany.storeapp.model.dao.ProductDAO;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.dao.SizeDAO;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.PurchaseOrder;
import com.mycompany.storeapp.model.entity.PurchaseOrderDetail;
import com.mycompany.storeapp.model.entity.Size;
import com.mycompany.storeapp.model.entity.User;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.mycompany.storeapp.session.Session;
import com.mycompany.storeapp.model.dao.UserDAO;

public class POEditDialog extends JDialog {
   private final PurchaseOrder purchaseOrder;
    private final PurchaseOrderController purchaseOrderController;
    private final Runnable onSaveCallback;
    private JTextField supplierIdField;
    private JTextField createdByUserIdField;
    private JTextField totalAmountField;
    private JTextArea noteArea;
    private JComboBox<String> statusComboBox;
    private JDateChooser orderDateChooser;
    private JDateChooser expectedDateChooser;
    private JLabel poIdLabel;
    private JPanel productListPanel;
    private JScrollPane productScrollPane;
    private JButton saveButton;
    private JButton cancelButton;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    
    private JLabel createdByNameLabel;
    
    private JButton addProductButton; 
    private JTable productTable; 
    private DefaultTableModel productTableModel; 
    private List<PurchaseOrderDetail> productList = new ArrayList<>(); 


    private final ProductDAO productDAO = new ProductDAO(new DatabaseConnection());
    private final SizeDAO sizeDAO = new SizeDAO(new DatabaseConnection());
    private final ColorDAO colorDAO = new ColorDAO(new DatabaseConnection());
    private final ProductVariantDAO productVariantDAO = new ProductVariantDAO(new DatabaseConnection());
    private final UserDAO userDAO = new UserDAO(new DatabaseConnection());

    public POEditDialog(Window parent, PurchaseOrder purchaseOrder, PurchaseOrderController purchaseOrderController, Runnable onSaveCallback) {
        super(parent, "Chi tiết đơn nhập hàng #" + purchaseOrder.getPoId(), ModalityType.APPLICATION_MODAL);
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderController = purchaseOrderController;
        this.onSaveCallback = onSaveCallback;
        initComponents();
        setupLayout();
        populateFields();
        loadProductList();
        setupEventHandlers();
        setSize(1000, 900);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            int userId = currentUser.getId();
            createdByUserIdField.setText(String.valueOf(userId)); 
            User user = userDAO.getUserById(userId); 
            createdByNameLabel.setText("Tên người tạo: " + (user != null && user.getName() != null ? user.getName() : "Không xác định"));
        } else {
            createdByUserIdField.setText(""); 
            createdByNameLabel.setText("Tên người tạo: Không xác định");
        }
    }

    private void initComponents() {
        poIdLabel = new JLabel("Mã đơn nhập hàng: #" + purchaseOrder.getPoId());
        poIdLabel.setFont(poIdLabel.getFont().deriveFont(Font.BOLD, 16f));

        supplierIdField = new JTextField(20);
        createdByUserIdField = new JTextField(20);
        totalAmountField = new JTextField(20);
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        orderDateChooser = new JDateChooser();
        orderDateChooser.setDateFormatString("dd/MM/yyyy");
        expectedDateChooser = new JDateChooser();
        expectedDateChooser.setDateFormatString("dd/MM/yyyy");

       statusComboBox = new JComboBox<>(new String[]{
                "Chờ xử lý",
                "Đang xử lý",
                "Hoàn thành",
                "Đã hủy"
        });

        String[] columnNames = {"Tên sản phẩm", "Đơn giá", "Số lượng", "Tên Size", "Tên Color", "Thành tiền"};
        this.productTableModel = new DefaultTableModel(columnNames, 0) { 
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.productTable = new JTable(this.productTableModel); // Sử dụng this.productTable
        JTable productTable = new JTable(productTableModel);
        this.productTable.setRowHeight(25);
        this.productTable.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer());
        this.productTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        productScrollPane = new JScrollPane(this.productTable); 
        productScrollPane = new JScrollPane(productTable);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productScrollPane.setPreferredSize(new Dimension(750, 200));
        this.productListPanel = new JPanel(new BorderLayout()); 
        this.productListPanel.add(productScrollPane, BorderLayout.CENTER); 

        saveButton = new JButton("Lưu thay đổi");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(120, 35));

        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 35));
        
        addProductButton = new JButton("Thêm sản phẩm");
        createdByNameLabel = new JLabel("Tên người tạo: ");
        createdByNameLabel.setFont(createdByNameLabel.getFont().deriveFont(Font.PLAIN, 14f));
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
        headerPanel.add(poIdLabel, gbc);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Thông tin đơn hàng", createOrderInfoPanel());
        tabbedPane.addTab("Danh sách sản phẩm", createProductsPanel());
        tabbedPane.addTab("Trạng thái", createStatusPanel());

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
        orderPanel.add(new JLabel("Mã nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(supplierIdField, gbc);

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
        orderPanel.add(createdByNameLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Tổng giá trị:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(totalAmountField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Ngày đặt hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(orderDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        orderPanel.add(new JLabel("Ngày dự kiến:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        orderPanel.add(expectedDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
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
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addProductButton);

        productsPanel.add(titleLabel, BorderLayout.NORTH);
        productsPanel.add(topPanel, BorderLayout.WEST);
        productsPanel.add(productListPanel, BorderLayout.CENTER);

        return productsPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        statusPanel.add(new JLabel("Trạng thái đơn nhập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusPanel.add(statusComboBox, gbc);

        return statusPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void populateFields() {
        supplierIdField.setText(String.valueOf(purchaseOrder.getSupplierId()));
        createdByUserIdField.setText(String.valueOf(purchaseOrder.getCreatedByUserId()));
        totalAmountField.setText(currencyFormat.format(purchaseOrder.getTotalAmount()));
        noteArea.setText(purchaseOrder.getNote() != null ? purchaseOrder.getNote() : "");
        orderDateChooser.setDate(purchaseOrder.getOrderDate());
        expectedDateChooser.setDate(purchaseOrder.getExpectedDate());
        String status = purchaseOrder.getStatus();
        if ("pending".equals(status)) status = "Chờ xử lý";
        else if ("processing".equals(status)) status = "Đang xử lý";
        else if ("completed".equals(status)) status = "Hoàn thành";
        else if ("cancelled".equals(status)) status = "Đã hủy";
        statusComboBox.setSelectedItem(status);
    }

   private void loadProductList() {
        List<PurchaseOrderDetail> orderItems = purchaseOrder.getDetails();
        productList.clear();
        if (orderItems != null) {
            productList.addAll(orderItems);
        }
        updateProductTable();
    }

   
   private void updateProductTable() {
        productTableModel.setRowCount(0);
        for (PurchaseOrderDetail detail : productList) {
            int variantId = detail.getProductId();
            ProductVariant variant = productVariantDAO.getVariantById(variantId);
            String productName = detail.getProductName();
            String sizeName = detail.getSizeName();
            String colorName = detail.getColorName();

            if (variant != null) {
                productName = variant.getProduct() != null ? variant.getProduct().getName() : (productName != null ? productName : "Không xác định");
                Size size = sizeDAO.getById(variant.getSizeId());
                com.mycompany.storeapp.model.entity.Color color = colorDAO.getById(variant.getColorId());
                sizeName = (size != null) ? size.getName() : (sizeName != null ? sizeName : "Không xác định");
                colorName = (color != null) ? color.getName() : (colorName != null ? colorName : "Không xác định");
            }

            productTableModel.addRow(new Object[]{
                productName,
                currencyFormat.format(detail.getUnitPrice()),
                detail.getQuantity(),
                sizeName,
                colorName,
                currencyFormat.format(detail.getSubTotal())
            });
        }
        updateTotalAmount();
    }
   
   private void updateTotalAmount() {
    double total = productList.stream().mapToDouble(PurchaseOrderDetail::getSubTotal).sum();
    totalAmountField.setText(currencyFormat.format(total));
    }

    private void setupEventHandlers() {
        addProductButton.addActionListener(e -> showAddProductDialog());
        saveButton.addActionListener(e -> savePurchaseOrder());
        cancelButton.addActionListener(e -> dispose());

       productTable.addMouseListener(new MouseAdapter() {
    private long pressStartTime = 0;
    private static final long LONG_PRESS_THRESHOLD = 500; // Giảm xuống 500ms

    @Override
    public void mousePressed(MouseEvent e) {
        pressStartTime = System.currentTimeMillis();
        // Chọn hàng tại vị trí chuột
        int row = productTable.rowAtPoint(e.getPoint());
        if (row >= 0 && row < productTable.getRowCount()) {
            productTable.setRowSelectionInterval(row, row);
        }
    }

        @Override
        public void mouseReleased(MouseEvent e) {
            long pressDuration = System.currentTimeMillis() - pressStartTime;
            if (pressDuration >= LONG_PRESS_THRESHOLD) {
                int row = productTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < productList.size()) {
                    int option = JOptionPane.showConfirmDialog(POEditDialog.this,
                            "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        productList.remove(row);
                        updateProductTable();
                        JOptionPane.showMessageDialog(POEditDialog.this, 
                                "Đã xóa sản phẩm khỏi danh sách!", 
                                "Thông báo", 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(POEditDialog.this, 
                            "Vui lòng chọn một sản phẩm hợp lệ!", 
                            "Lỗi", 
                            JOptionPane.WARNING_MESSAGE);
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

        JComboBox<com.mycompany.storeapp.model.entity.Color> colorComboBox = new JComboBox<>(); // Sửa đổi
        colorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof com.mycompany.storeapp.model.entity.Color) { // Sửa đổi
                    setText(((com.mycompany.storeapp.model.entity.Color) value).getName()); // Sửa đổi
                }
                return this;
            }
        });

        JTextField quantityField = new JTextField(10);
        JTextField unitPriceField = new JTextField(10);

        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                int productId = (int) selectedProduct.getProductId();
                List<ProductVariant> variants = productVariantDAO.getVariantsByProductId(productId);
                sizeComboBox.removeAllItems();
                colorComboBox.removeAllItems();
                for (ProductVariant variant : variants) {
                    Size size = sizeDAO.getById(variant.getSizeId());
                    com.mycompany.storeapp.model.entity.Color color = colorDAO.getById(variant.getColorId()); // Sửa đổi
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
        saveDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
                    Product selectedProduct = (Product) productComboBox.getSelectedItem();
                    Size selectedSize = (Size) sizeComboBox.getSelectedItem();
                    com.mycompany.storeapp.model.entity.Color selectedColor = (com.mycompany.storeapp.model.entity.Color) colorComboBox.getSelectedItem(); // Sửa đổi

                    if (quantity <= 0 || unitPrice <= 0) {
                        JOptionPane.showMessageDialog(addProductDialog, "Số lượng và đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (selectedProduct == null || selectedSize == null || selectedColor == null) {
                        JOptionPane.showMessageDialog(addProductDialog, "Vui lòng chọn đầy đủ sản phẩm, kích cỡ và màu sắc!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    ProductVariant variant = productVariantDAO.getVariantByProductSizeColor((int) selectedProduct.getProductId(), selectedSize.getSizeId(), (int) selectedColor.getColorId()); // Sửa đổi
                    if (variant == null) {
                        JOptionPane.showMessageDialog(addProductDialog, "Biến thể sản phẩm không tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    PurchaseOrderDetail detail = new PurchaseOrderDetail();
                    detail.setProductId(variant.getVariantId());
                    detail.setUnitPrice(unitPrice);
                    detail.setQuantity(quantity);
                    detail.setSubTotal(unitPrice * quantity);
                    detail.setProductName(selectedProduct.getName());
                    detail.setSizeName(selectedSize.getName());
                    detail.setColorName(selectedColor.getName());

                    productList.add(detail);
                    updateProductTable();
                    addProductDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addProductDialog, "Vui lòng nhập đơn giá và số lượng là số hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addProductDialog, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        dialogPanel.add(saveDialogButton, gbc);

        addProductDialog.add(dialogPanel);
        addProductDialog.setVisible(true);
}

    private void savePurchaseOrder() {
        try {
            if (!validateInput()) {
                return;
            }

            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            saveButton.setText("Đang lưu...");

            int supplierId = Integer.parseInt(supplierIdField.getText().trim());
            int createdByUserId = Integer.parseInt(createdByUserIdField.getText().trim());
            double totalAmount = currencyFormat.parse(totalAmountField.getText().trim()).doubleValue();
            String note = noteArea.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();
            
            if ("Chờ xử lý".equals(status)) status = "pending";
            else if ("Đang xử lý".equals(status)) status = "processing";
            else if ("Hoàn thành".equals(status)) status = "completed";
            else if ("Đã hủy".equals(status)) status = "cancelled";
            
            java.util.Date orderDate = orderDateChooser.getDate();
            java.util.Date expectedDate = expectedDateChooser.getDate();

            if (orderDate == null || expectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đặt và ngày dự kiến!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            purchaseOrder.setSupplierId(supplierId);
            purchaseOrder.setCreatedByUserId(createdByUserId);
            purchaseOrder.setTotalAmount(totalAmount);
            purchaseOrder.setNote(note);
            purchaseOrder.setStatus(status);
            purchaseOrder.setOrderDate(orderDate);
            purchaseOrder.setExpectedDate(expectedDate);
            purchaseOrder.setDetails(productList); // Thêm mới

            POTransitionResult result;
            if (status.equals(PurchaseOrderController.STATUS_COMPLETED)) {
                result = purchaseOrderController.completePurchaseOrder(purchaseOrder.getPoId());
            } else {
                result = purchaseOrderController.changePurchaseOrderStatus(purchaseOrder.getPoId(), status);
            }

            if (result.isSuccess()) {
                if (purchaseOrderController.updatePurchaseOrder(purchaseOrder)) { // Thêm mới
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật đơn nhập hàng thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật chi tiết đơn nhập hàng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        result.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
        }
    }

    private boolean validateInput() {
        if (supplierIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập mã nhà cung cấp!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            supplierIdField.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(supplierIdField.getText().trim());
            Integer.parseInt(createdByUserIdField.getText().trim());
            currencyFormat.parse(totalAmountField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Mã nhà cung cấp, mã người tạo và tổng giá trị phải là số hợp lệ!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public static void showDialog(Window parent, PurchaseOrder purchaseOrder, PurchaseOrderController purchaseOrderController, Runnable onSaveCallback) {
        POEditDialog dialog = new POEditDialog(parent, purchaseOrder, purchaseOrderController, onSaveCallback);
        dialog.setVisible(true);
    }

        private boolean comboBoxContainsColor(JComboBox<com.mycompany.storeapp.model.entity.Color> comboBox, com.mycompany.storeapp.model.entity.Color item) { // Sửa đổi
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
    

}