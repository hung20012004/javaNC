package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.MaterialController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Material;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.text.Normalizer;
import java.util.Random;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ProductFormPanel extends JPanel {
    
    // Controllers
    private CategoryController categoryController;
    private MaterialController materialController;
    
    // Form fields
    private JTextField nameField;
    private JTextField brandField;
    private JTextField skuField;
    private JTextField slugField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Material> materialComboBox;
    private JComboBox<String> genderComboBox;
    private JTextField priceField;
    private JTextField salePriceField;
    private JTextField stockQuantityField;
    private JTextField minPurchaseField;
    private JTextField maxPurchaseField;
    private JTextArea descriptionArea;
    private JTextArea careInstructionArea;
    private JCheckBox activeCheckBox;
    
    // Data
    private List<Category> categories;
    private List<Material> materials;
    
    public ProductFormPanel(CategoryController categoryController, MaterialController materialController) {
        this.categoryController = categoryController;
        this.materialController = materialController;
        
        initComponents();
        loadData();
        setupLayout();
    }
    
    private void initComponents() {
        // Text fields
        nameField = new JTextField(20);
        brandField = new JTextField(20);
        skuField = new JTextField(20);
        slugField = new JTextField(20);
        priceField = new JTextField(20);
        salePriceField = new JTextField(20);
        stockQuantityField = new JTextField(20);
        minPurchaseField = new JTextField(20);
        maxPurchaseField = new JTextField(20);
        
        // Combo boxes
        categoryComboBox = new JComboBox<>();
        materialComboBox = new JComboBox<>();
        genderComboBox = new JComboBox<>(new String[]{"Nam", "Nữ", "Unisex"});
        
        // Text areas
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        
        careInstructionArea = new JTextArea(3, 20);
        careInstructionArea.setWrapStyleWord(true);
        careInstructionArea.setLineWrap(true);
        
        // Checkbox
        activeCheckBox = new JCheckBox("Kích hoạt");
        activeCheckBox.setSelected(true);
        
        // Set default values
        minPurchaseField.setText("1");
        maxPurchaseField.setText("999");
        stockQuantityField.setText("0");
        priceField.setText("0.0");
        salePriceField.setText("0.0");
        
        setupAutoGeneration();
    }
    
    private void loadData() {
        try {
            // Load categories
            categories = categoryController.getAllCategories();
            categoryComboBox.removeAllItems();
            categoryComboBox.addItem(null); // Add empty option
            if (categories != null) {
                for (Category category : categories) {
                    categoryComboBox.addItem(category);
                }
            }
            
            // Load materials
            materials = materialController.getAllMaterials();
            materialComboBox.removeAllItems();
            if (materials != null) {
                for (Material material : materials) {
                    materialComboBox.addItem(material);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Basic Information Panel
        JPanel basicPanel = createBasicInfoPanel();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(basicPanel, gbc);
        
        // Price and Stock Panel
        JPanel pricePanel = createPriceStockPanel();
        gbc.gridy = 1;
        mainPanel.add(pricePanel, gbc);
        
        // Description Panel
        JPanel descPanel = createDescriptionPanel();
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(descPanel, gbc);
        
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Thông tin cơ bản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Name and Brand
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Thương hiệu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(brandField, gbc);
        
        // Row 2: SKU and Slug
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("SKU:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(skuField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Slug:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(slugField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createGenerateSKUButton(), gbc);
        
        // Row 3: Category and Material
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(categoryComboBox, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Chất liệu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(materialComboBox, gbc);
        
        // Row 4: Gender and Active
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(genderComboBox, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createPriceStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Giá và Tồn kho"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Price and Sale Price
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Giá gốc:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(priceField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Giá khuyến mãi:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(salePriceField, gbc);
        
        // Row 2: Stock and Purchase Limits
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Tồn kho:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(stockQuantityField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Số lượng mua tối thiểu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(minPurchaseField, gbc);
        
        // Row 3: Max Purchase
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Số lượng mua tối đa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(maxPurchaseField, gbc);
        
        return panel;
    }
    
    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Mô tả và Hướng dẫn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Description
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.6;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        // Care Instructions
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.4;
        panel.add(new JLabel("Hướng dẫn bảo quản:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(careInstructionArea), gbc);
        
        return panel;
    }
    
    public void setEditable(boolean editable) {
        nameField.setEditable(editable);
        brandField.setEditable(editable);
        skuField.setEditable(editable);
        slugField.setEditable(editable);
        priceField.setEditable(editable);
        salePriceField.setEditable(editable);
        stockQuantityField.setEditable(editable);
        minPurchaseField.setEditable(editable);
        maxPurchaseField.setEditable(editable);
        descriptionArea.setEditable(editable);
        careInstructionArea.setEditable(editable);
        
        categoryComboBox.setEnabled(editable);
        materialComboBox.setEnabled(editable);
        genderComboBox.setEnabled(editable);
        activeCheckBox.setEnabled(editable);
    }
    
    public void populateForm(Product product) {
        if (product == null) return;
        
        setNewProduct(false);
        
        nameField.setText(product.getName());
        brandField.setText(product.getBrand());
        skuField.setText(product.getSku());
        slugField.setText(product.getSlug());
        priceField.setText(String.valueOf(product.getPrice()));
        salePriceField.setText(String.valueOf(product.getSalePrice()));
        stockQuantityField.setText(String.valueOf(product.getStockQuantity()));
        minPurchaseField.setText(String.valueOf(product.getMinPurchaseQuantity()));
        maxPurchaseField.setText(String.valueOf(product.getMaxPurchaseQuantity()));
        descriptionArea.setText(product.getDescription());
        careInstructionArea.setText(product.getCareInstruction());
        activeCheckBox.setSelected(product.isActive());
        
        // Set combo box selections
        genderComboBox.setSelectedItem(product.getGender());
        
        // Set category
        if (product.getCategoryId() != null) {
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                Category category = categoryComboBox.getItemAt(i);
                if (category != null && category.getCategoryId() == product.getCategoryId()) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Set material
        for (int i = 0; i < materialComboBox.getItemCount(); i++) {
            Material material = materialComboBox.getItemAt(i);
            if (material != null && material.getMaterialId() == product.getMaterialId()) {
                materialComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public boolean validateForm() {
        // Required fields validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (brandField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Thương hiệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            brandField.requestFocus();
            return false;
        }
        
        if (skuField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "SKU không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            skuField.requestFocus();
            return false;
        }
        
        if (materialComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chất liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Numeric fields validation
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Giá không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        try {
            double salePrice = Double.parseDouble(salePriceField.getText().trim());
            if (salePrice < 0) {
                JOptionPane.showMessageDialog(this, "Giá khuyến mãi không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                salePriceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá khuyến mãi phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            salePriceField.requestFocus();
            return false;
        }
        
        try {
            int stock = Integer.parseInt(stockQuantityField.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Tồn kho không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                stockQuantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tồn kho phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            stockQuantityField.requestFocus();
            return false;
        }
        
        try {
            int minPurchase = Integer.parseInt(minPurchaseField.getText().trim());
            int maxPurchase = Integer.parseInt(maxPurchaseField.getText().trim());
            if (minPurchase < 1) {
                JOptionPane.showMessageDialog(this, "Số lượng mua tối thiểu phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                minPurchaseField.requestFocus();
                return false;
            }
            if (maxPurchase < minPurchase) {
                JOptionPane.showMessageDialog(this, "Số lượng mua tối đa phải lớn hơn hoặc bằng số lượng mua tối thiểu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                maxPurchaseField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng mua phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Tự động generate SKU từ tên sản phẩm
     * Format: PREFIX + normalized name + random number
     */
    private String generateSKU(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "";
        }

        // Normalize và làm sạch tên sản phẩm
        String normalizedName = normalizeVietnamese(productName.trim().toUpperCase());

        // Lấy các từ đầu tiên (tối đa 3 từ)
        String[] words = normalizedName.split("\\s+");
        StringBuilder skuBuilder = new StringBuilder();

        // Prefix cho SKU
        skuBuilder.append("SP");

        // Lấy 2-3 ký tự đầu của mỗi từ
        int wordCount = Math.min(words.length, 3);
        for (int i = 0; i < wordCount; i++) {
            String word = words[i];
            if (word.length() >= 2) {
                skuBuilder.append(word.substring(0, 2));
            } else if (word.length() == 1) {
                skuBuilder.append(word);
            }
        }

        // Thêm số random 3 chữ số
        Random random = new Random();
        int randomNumber = random.nextInt(900) + 100; // 100-999
        skuBuilder.append(randomNumber);

        return skuBuilder.toString();
    }
    
    /**
    * Normalize tiếng Việt - chuyển đổi ký tự có dấu thành không dấu
    */
   private String normalizeVietnamese(String text) {
       if (text == null) return "";

       // Normalize Unicode
       String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);

       // Loại bỏ các ký tự dấu
       normalized = normalized.replaceAll("\\p{M}", "");

       // Thay thế các ký tự đặc biệt của tiếng Việt
       normalized = normalized.replace("Đ", "D").replace("đ", "d");

       // Chỉ giữ lại chữ cái và số
       normalized = normalized.replaceAll("[^a-zA-Z0-9\\s]", "");

       // Loại bỏ khoảng trắng thừa
       normalized = normalized.replaceAll("\\s+", " ");

       return normalized.trim();
   }

   /**
    * Tự động generate slug từ tên sản phẩm
    */
   private String generateSlug(String productName) {
       if (productName == null || productName.trim().isEmpty()) {
           return "";
       }

       String slug = normalizeVietnamese(productName.trim().toLowerCase());

       // Thay thế khoảng trắng bằng dấu gạch ngang
       slug = slug.replaceAll("\\s+", "-");

       // Loại bỏ các ký tự đặc biệt (chỉ giữ chữ cái, số và dấu gạch ngang)
       slug = slug.replaceAll("[^a-z0-9-]", "");

       // Loại bỏ dấu gạch ngang liên tiếp
       slug = slug.replaceAll("-+", "-");

       // Loại bỏ dấu gạch ngang ở đầu và cuối
       slug = slug.replaceAll("^-+|-+$", "");

       return slug;
   }
   
   /**
    * Setup auto-generation cho SKU và Slug khi tên sản phẩm thay đổi
    * Thêm vào method initComponents()
    */
   private void setupAutoGeneration() {
       nameField.getDocument().addDocumentListener(new DocumentListener() {
           @Override
           public void insertUpdate(DocumentEvent e) {
               updateSKUAndSlug();
           }

           @Override
           public void removeUpdate(DocumentEvent e) {
               updateSKUAndSlug();
           }

           @Override
           public void changedUpdate(DocumentEvent e) {
               updateSKUAndSlug();
           }

           private void updateSKUAndSlug() {
               SwingUtilities.invokeLater(() -> {
                   String productName = nameField.getText();

                   // Chỉ auto-generate nếu SKU field đang trống hoặc đang tạo mới
                   if (skuField.getText().trim().isEmpty() || isNewProduct) {
                       String newSKU = generateSKU(productName);
                       skuField.setText(newSKU);
                   }

                   // Chỉ auto-generate nếu Slug field đang trống hoặc đang tạo mới
                   if (slugField.getText().trim().isEmpty() || isNewProduct) {
                       String newSlug = generateSlug(productName);
                       slugField.setText(newSlug);
                   }
               });
           }
       });
   }

   /**
    * Thêm button để manually generate SKU
    */
   private JButton createGenerateSKUButton() {
       JButton generateButton = new JButton("Generate SKU");
       generateButton.addActionListener(e -> {
           String productName = nameField.getText();
           if (!productName.trim().isEmpty()) {
               String newSKU = generateSKU(productName);
               skuField.setText(newSKU);

               String newSlug = generateSlug(productName);
               slugField.setText(newSlug);
           } else {
               JOptionPane.showMessageDialog(this, 
                   "Vui lòng nhập tên sản phẩm trước!", 
                   "Thông báo", JOptionPane.WARNING_MESSAGE);
           }
       });
       return generateButton;
   }

   // Thêm biến instance để track trạng thái tạo mới
   private boolean isNewProduct = true;

   /**
    * Method để set trạng thái tạo mới hoặc chỉnh sửa
    */
   public void setNewProduct(boolean isNewProduct) {
       this.isNewProduct = isNewProduct;
   }

   /**
    * Method để clear form khi tạo mới
    */
   public void clearForm() {
       nameField.setText("");
       brandField.setText("");
       skuField.setText("");
       slugField.setText("");
       categoryComboBox.setSelectedIndex(0);
       materialComboBox.setSelectedIndex(0);
       genderComboBox.setSelectedIndex(0);
       priceField.setText("0.0");
       salePriceField.setText("0.0");
       stockQuantityField.setText("0");
       minPurchaseField.setText("1");
       maxPurchaseField.setText("999");
       descriptionArea.setText("");
       careInstructionArea.setText("");
       activeCheckBox.setSelected(true);

       // Set trạng thái tạo mới
       setNewProduct(true);
   }
    
    public Product getProductFromForm() {
        Product product = new Product();
        
        product.setName(nameField.getText().trim());
        product.setBrand(brandField.getText().trim());
        product.setSku(skuField.getText().trim());
        product.setSlug(slugField.getText().trim());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setSalePrice(Double.parseDouble(salePriceField.getText().trim()));
        product.setStockQuantity(Integer.parseInt(stockQuantityField.getText().trim()));
        product.setMinPurchaseQuantity(Integer.parseInt(minPurchaseField.getText().trim()));
        product.setMaxPurchaseQuantity(Integer.parseInt(maxPurchaseField.getText().trim()));
        product.setDescription(descriptionArea.getText().trim());
        product.setCareInstruction(careInstructionArea.getText().trim());
        product.setActive(activeCheckBox.isSelected());
        product.setGender((String) genderComboBox.getSelectedItem());
        
        // Set category
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            product.setCategoryId(selectedCategory.getCategoryId());
        }
        
        // Set material
        Material selectedMaterial = (Material) materialComboBox.getSelectedItem();
        if (selectedMaterial != null) {
            product.setMaterialId(selectedMaterial.getMaterialId());
        }
        
        return product;
    }
}