/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component;

import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * Custom table component tái sử dụng với hỗ trợ hiển thị hình ảnh và variants tương tác
 * @author Hi
 */
public class CustomTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private String[] columnNames;
    private String[] fieldNames;
    private boolean hasActionColumn;
    private int imageColumnIndex = -1; // Index của cột hình ảnh
    private int variantColumnIndex = -1; // Index của cột variant
    
    // Cache cho hình ảnh để tránh load lại nhiều lần
    private static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private static final ImageIcon DEFAULT_IMAGE = createDefaultImage();
    private static final ImageIcon LOADING_IMAGE = createLoadingImage();
    
    // Store current data for variant interactions
    private List<?> currentData;
    
    public CustomTable(String[] columnNames, String[] fieldNames, boolean hasActionColumn) {
        this.columnNames = columnNames;
        this.fieldNames = fieldNames;
        this.hasActionColumn = hasActionColumn;
        
        // Tìm index của cột hình ảnh và variant
        findSpecialColumnIndexes();
        
        initComponents();
        setupTable();
        setupLayout();
    }
    
    private void findSpecialColumnIndexes() {
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i].toLowerCase();
            String fieldName = fieldNames != null && i < fieldNames.length ? fieldNames[i].toLowerCase() : "";
            
            // Tìm cột hình ảnh
            if (columnName.contains("hình ảnh") || columnName.contains("image") || fieldName.contains("image")) {
                imageColumnIndex = i;
            }
            
            // Tìm cột variant
            if (columnName.contains("variant") || fieldName.contains("variant")) {
                variantColumnIndex = i;
            }
        }
    }
    
    private static ImageIcon createDefaultImage() {
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, 80, 80);
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawRect(0, 0, 79, 79);
        g2d.setColor(new Color(120, 120, 120));
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int x = (80 - fm.stringWidth(text)) / 2;
        int y = (80 + fm.getAscent()) / 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    private static ImageIcon createLoadingImage() {
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(248, 249, 250));
        g2d.fillRect(0, 0, 80, 80);
        g2d.setColor(new Color(52, 152, 219));
        g2d.drawRect(0, 0, 79, 79);
        g2d.setColor(new Color(52, 152, 219));
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Loading...";
        int x = (80 - fm.stringWidth(text)) / 2;
        int y = (80 + fm.getAscent()) / 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    private void initComponents() {
        // Tạo table model
        if (hasActionColumn) {
            String[] headers = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, headers, 0, columnNames.length);
            headers[columnNames.length] = "Thao tác";
            tableModel = new DefaultTableModel(headers, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == getColumnCount() - 1; // Chỉ cho phép edit cột action
                }
            };
        } else {
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }
        
        // Tạo table
        table = new JTable(tableModel);
        
        // Tạo scroll pane
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
    }
    
    private void setupTable() {
        // Table styling
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(imageColumnIndex >= 0 || variantColumnIndex >= 0 ? 120 : 45); // Tăng chiều cao nếu có hình ảnh hoặc variant
        table.setGridColor(new Color(236, 240, 241));
        table.setSelectionBackground(new Color(217, 237, 247));
        table.setSelectionForeground(new Color(51, 51, 51));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        
        // Setup image column nếu có
        if (imageColumnIndex >= 0) {
            setupImageColumn();
        }
        
        // Setup variant column nếu có
        if (variantColumnIndex >= 0) {
            setupVariantColumn();
        }
        
        // Cell renderer cho zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        component.setBackground(Color.WHITE);
                    } else {
                        component.setBackground(new Color(248, 249, 250));
                    }
                }
                
                // Center align cho các cột số và action
                if (column == 0 || (hasActionColumn && column == table.getColumnCount() - 1)) {
                    ((DefaultTableCellRenderer) component).setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    ((DefaultTableCellRenderer) component).setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return component;
            }
        });
        
        // Setup action column nếu có
        if (hasActionColumn) {
            setupActionColumn();
        }
        
        // Auto resize columns
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Row selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Double click event
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        fireRowDoubleClicked(selectedRow);
                    }
                }
            }
        });
    }
    
    private void setupImageColumn() {
        TableColumn imageColumn = table.getColumnModel().getColumn(imageColumnIndex);
        imageColumn.setPreferredWidth(100);
        imageColumn.setMaxWidth(120);
        imageColumn.setMinWidth(80);
        
        // Custom renderer cho image column
        imageColumn.setCellRenderer(new ImageCellRenderer());
    }
    
    private void setupVariantColumn() {
        TableColumn variantColumn = table.getColumnModel().getColumn(variantColumnIndex);
        variantColumn.setPreferredWidth(200);
        variantColumn.setMaxWidth(300);
        variantColumn.setMinWidth(150);
        
        // Custom renderer cho variant column
        variantColumn.setCellRenderer(new VariantCellRenderer());
    }
    
    private void setupActionColumn() {
        int actionColumnIndex = table.getColumnCount() - 1;
        TableColumn actionColumn = table.getColumnModel().getColumn(actionColumnIndex);
        actionColumn.setPreferredWidth(150);
        actionColumn.setMaxWidth(150);
        actionColumn.setMinWidth(150);
        
        // Custom renderer cho action column
        actionColumn.setCellRenderer(new ActionCellRenderer());
        actionColumn.setCellEditor(new ActionCellEditor());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // Methods để thao tác với data
    public void setData(List<?> dataList) {
        this.currentData = dataList;
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
        // Add data rows
        for (Object item : dataList) {
            Object[] rowData = extractRowData(item);
            if (hasActionColumn) {
                Object[] rowWithAction = new Object[rowData.length + 1];
                System.arraycopy(rowData, 0, rowWithAction, 0, rowData.length);
                rowWithAction[rowData.length] = "actions";
                tableModel.addRow(rowWithAction);
            } else {
                tableModel.addRow(rowData);
            }
        }
        
        table.revalidate();
        table.repaint();
    }
    
    private Object[] extractRowData(Object item) {
        Object[] rowData = new Object[fieldNames.length];
        
        try {
            Class<?> clazz = item.getClass();
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                
                // Convert field name to getter method name
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                
                try {
                    java.lang.reflect.Method getter = clazz.getMethod(getterName);
                    Object value = getter.invoke(item);
                    
                    // Format value if needed
                    if (value instanceof Boolean) {
                        rowData[i] = (Boolean) value ? "Có" : "Không";
                    } else if (value instanceof java.util.Date) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        rowData[i] = sdf.format((java.util.Date) value);
                    } else {
                        rowData[i] = value != null ? value.toString() : "Không tìm thấy dữ liệu";
                    }
                } catch (Exception e) {
                    rowData[i] = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return rowData;
    }
    
    public Object getSelectedRowData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            return tableModel.getDataVector().get(selectedRow);
        }
        return null;
    }
    
    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }
    
    public void clearSelection() {
        table.clearSelection();
    }
    
    // Event handling
    private ActionListener editActionListener;
    private ActionListener deleteActionListener;
    private ActionListener rowDoubleClickListener;
    
    public void setEditActionListener(ActionListener listener) {
        this.editActionListener = listener;
    }
    
    public void setDeleteActionListener(ActionListener listener) {
        this.deleteActionListener = listener;
    }
    
    public void setRowDoubleClickListener(ActionListener listener) {
        this.rowDoubleClickListener = listener;
    }
    
    private void fireRowDoubleClicked(int row) {
        if (rowDoubleClickListener != null) {
            rowDoubleClickListener.actionPerformed(
                new java.awt.event.ActionEvent(this, row, "doubleClick"));
        }
    }
    
    // Inner class for image cell renderer
    private class ImageCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);
            
            // Set background color
            if (!isSelected) {
                if (row % 2 == 0) {
                    panel.setBackground(Color.WHITE);
                } else {
                    panel.setBackground(new Color(248, 249, 250));
                }
            } else {
                panel.setBackground(new Color(217, 237, 247));
            }
            
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            String imageUrl = value != null ? value.toString().trim() : "";
            
            if (imageUrl.isEmpty()) {
                imageLabel.setIcon(DEFAULT_IMAGE);
            } else {
                // Check cache first
                ImageIcon cachedIcon = imageCache.get(imageUrl);
                if (cachedIcon != null) {
                    imageLabel.setIcon(cachedIcon);
                } else {
                    // Show loading image while loading
                    imageLabel.setIcon(LOADING_IMAGE);
                    
                    // Load image asynchronously
                    SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                        @Override
                        protected ImageIcon doInBackground() throws Exception {
                            return loadImageFromUrl(imageUrl);
                        }
                        
                        @Override
                        protected void done() {
                            try {
                                ImageIcon icon = get();
                                if (icon != null) {
                                    imageCache.put(imageUrl, icon);
                                    // Update the table to refresh the image
                                    SwingUtilities.invokeLater(() -> {
                                        table.repaint();
                                    });
                                }
                            } catch (Exception e) {
                                // Handle error - image will stay as loading image
                                System.err.println("Error loading image: " + imageUrl);
                            }
                        }
                    };
                    worker.execute();
                }
            }
            
            panel.add(imageLabel, BorderLayout.CENTER);
            return panel;
        }
        
        private ImageIcon loadImageFromUrl(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                BufferedImage originalImage = ImageIO.read(url);
                
                if (originalImage != null) {
                    // Resize image to fit cell
                    Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image from URL: " + imageUrl);
            }
            
            return DEFAULT_IMAGE;
        }
    }
    
    // Inner class for variant cell renderer - Updated version
    private class VariantCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setOpaque(true);

            // Set background color
            if (!isSelected) {
                if (row % 2 == 0) {
                    mainPanel.setBackground(Color.WHITE);
                } else {
                    mainPanel.setBackground(new Color(248, 249, 250));
                }
            } else {
                mainPanel.setBackground(new Color(217, 237, 247));
            }

            // Get variant data for this row
            List<ProductVariant> variants = getVariantsForRow(row);

            if (variants == null || variants.isEmpty()) {
                JLabel noVariantLabel = new JLabel("Không có variant");
                noVariantLabel.setForeground(new Color(150, 150, 150));
                noVariantLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                noVariantLabel.setHorizontalAlignment(SwingConstants.CENTER);
                mainPanel.add(noVariantLabel, BorderLayout.CENTER);
                return mainPanel;
            }

            // Create variant display panel
            JPanel variantPanel = createVariantPanel(variants, row);
            mainPanel.add(variantPanel, BorderLayout.CENTER);

            return mainPanel;
        }

        private List<ProductVariant> getVariantsForRow(int row) {
            if (currentData == null || row >= currentData.size()) {
                return null;
            }

            Object item = currentData.get(row);

            // Handle EnhancedProduct from ProductGUI
            if (item.getClass().getSimpleName().equals("EnhancedProduct")) {
                try {
                    java.lang.reflect.Method getVariants = item.getClass().getMethod("getVariants");
                    @SuppressWarnings("unchecked")
                    List<ProductVariant> variants = (List<ProductVariant>) getVariants.invoke(item);
                    return variants;
                } catch (Exception e) {
                    System.err.println("Error getting variants: " + e.getMessage());
                }
            }

            return null;
        }

        private JPanel createVariantPanel(List<ProductVariant> variants, int row) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);

            // Group variants by color
            Map<String, List<ProductVariant>> colorGroups = new HashMap<>();
            for (ProductVariant variant : variants) {
                String colorKey = getColorKey(variant);
                colorGroups.computeIfAbsent(colorKey, k -> new ArrayList<>()).add(variant);
            }

            // Create interactive variant display
            int colorCount = 0;
            for (Map.Entry<String, List<ProductVariant>> entry : colorGroups.entrySet()) {
                if (colorCount >= 3) { // Limit to 3 colors for display
                    break;
                }

                String colorKey = entry.getKey();
                List<ProductVariant> colorVariants = entry.getValue();

                JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
                colorPanel.setOpaque(false);

                // Create color display component
                JComponent colorDisplay = createColorDisplay(colorVariants.get(0), colorKey);
                colorPanel.add(colorDisplay);

                // Size labels (clickable)
                for (ProductVariant variant : colorVariants) {
                    String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                    boolean hasStock = variant.getStockQuantity() > 0;

                    JLabel sizeLabel = new JLabel(size);
                    sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    sizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    sizeLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                    sizeLabel.setOpaque(true);

                    if (hasStock) {
                        sizeLabel.setForeground(new Color(39, 174, 96));
                        sizeLabel.setBackground(new Color(232, 245, 237));
                    } else {
                        sizeLabel.setForeground(new Color(189, 195, 199));
                        sizeLabel.setBackground(new Color(245, 245, 245));
                    }

                    // Add tooltip with stock info
                    sizeLabel.setToolTipText(String.format("Size: %s, Kho: %d", size, variant.getStockQuantity()));

                    // Add click listener for size
                    sizeLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            highlightVariantsBySize(size, variants, row);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (hasStock) {
                                sizeLabel.setBackground(new Color(212, 235, 222));
                            } else {
                                sizeLabel.setBackground(new Color(235, 235, 235));
                            }
                            sizeLabel.repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            if (hasStock) {
                                sizeLabel.setBackground(new Color(232, 245, 237));
                            } else {
                                sizeLabel.setBackground(new Color(245, 245, 245));
                            }
                            sizeLabel.repaint();
                        }
                    });

                    colorPanel.add(sizeLabel);
                }

                panel.add(colorPanel);
                colorCount++;
            }

            // Add "more" indicator if there are more colors
            if (colorGroups.size() > 3) {
                JLabel moreLabel = new JLabel("+" + (colorGroups.size() - 3) + " màu khác");
                moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                moreLabel.setForeground(new Color(127, 140, 141));
                moreLabel.setHorizontalAlignment(SwingConstants.CENTER);
                moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                moreLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

                // Add click listener to show all variants
                moreLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showAllVariants(variants, row);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        moreLabel.setForeground(new Color(52, 152, 219));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        moreLabel.setForeground(new Color(127, 140, 141));
                    }
                });

                panel.add(moreLabel);
            }

            return panel;
        }

        private String getColorKey(ProductVariant variant) {
            if (variant.getColor() != null) {
                return variant.getColor().getName() + "|" + 
                       (variant.getColor().getDescription() != null ? variant.getColor().getDescription() : "");
            }
            return "Mặc định|#CCCCCC";
        }

        private JComponent createColorDisplay(ProductVariant variant, String colorKey) {
            JPanel colorPanel = new JPanel(new BorderLayout());
            colorPanel.setOpaque(false);
            colorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            String[] parts = colorKey.split("\\|");
            String colorName = parts[0];
            String colorCode = parts.length > 1 ? parts[1] : "#CCCCCC";

            // Create color circle
            JPanel colorCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Parse color code
                    Color color = parseColor(colorCode);

                    // Draw color circle
                    g2d.setColor(color);
                    g2d.fillOval(2, 2, 16, 16);

                    // Draw border
                    g2d.setColor(new Color(189, 195, 199));
                    g2d.drawOval(2, 2, 16, 16);

                    g2d.dispose();
                }
            };

            colorCircle.setPreferredSize(new Dimension(20, 20));
            colorCircle.setOpaque(false);

            // Create color name label
            JLabel colorLabel = new JLabel(colorName);
            colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            colorLabel.setForeground(new Color(52, 73, 94));
            colorLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

            colorPanel.add(colorCircle, BorderLayout.WEST);
            colorPanel.add(colorLabel, BorderLayout.CENTER);

            // Add tooltip
            colorPanel.setToolTipText(String.format("Màu: %s (%s)", colorName, colorCode));

            // Add click listener
            colorPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    highlightVariantsByColor(colorName, getVariantsForRow(getRowFromComponent(colorPanel)), 
                                           getRowFromComponent(colorPanel));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    colorPanel.setOpaque(true);
                    colorPanel.setBackground(new Color(230, 230, 230));
                    colorPanel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    colorPanel.setOpaque(false);
                    colorPanel.repaint();
                }
            });

            return colorPanel;
        }

        private Color parseColor(String colorCode) {
            try {
                // Remove # if present
                if (colorCode.startsWith("#")) {
                    colorCode = colorCode.substring(1);
                }

                // Parse hex color
                if (colorCode.length() == 6) {
                    int r = Integer.parseInt(colorCode.substring(0, 2), 16);
                    int g = Integer.parseInt(colorCode.substring(2, 4), 16);
                    int b = Integer.parseInt(colorCode.substring(4, 6), 16);
                    return new Color(r, g, b);
                }
            } catch (Exception e) {
                // Fall back to default color
            }

            return new Color(204, 204, 204); // Default gray
        }

        private int getRowFromComponent(Component component) {
            // Find the row by traversing up the component hierarchy
            Component parent = component;
            while (parent != null && !(parent instanceof JTable)) {
                parent = parent.getParent();
            }

            if (parent instanceof JTable) {
                JTable table = (JTable) parent;
                Point point = SwingUtilities.convertPoint(component, new Point(0, 0), table);
                return table.rowAtPoint(point);
            }

            return -1;
        }

        private void highlightVariantsByColor(String selectedColor, List<ProductVariant> variants, int row) {
            if (variants == null) return;

            // Get all sizes available for the selected color
            List<String> availableSizes = new ArrayList<>();
            for (ProductVariant variant : variants) {
                String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                if (color.equals(selectedColor)) {
                    String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                    availableSizes.add(size);
                }
            }

            // Show tooltip or highlight effect
            showColorSizeInfo(selectedColor, availableSizes, row);
        }

        private void highlightVariantsBySize(String selectedSize, List<ProductVariant> variants, int row) {
            if (variants == null) return;

            // Get all colors available for the selected size
            List<String> availableColors = new ArrayList<>();
            for (ProductVariant variant : variants) {
                String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                if (size.equals(selectedSize)) {
                    String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                    availableColors.add(color);
                }
            }

            // Show tooltip or highlight effect
            showSizeColorInfo(selectedSize, availableColors, row);
        }

        private void showColorSizeInfo(String color, List<String> sizes, int row) {
            SwingUtilities.invokeLater(() -> {
                String message = String.format("Màu %s có sizes: %s", color, String.join(", ", sizes));
                JOptionPane.showMessageDialog(table, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        private void showSizeColorInfo(String size, List<String> colors, int row) {
            SwingUtilities.invokeLater(() -> {
                String message = String.format("Size %s có màu: %s", size, String.join(", ", colors));
                JOptionPane.showMessageDialog(table, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        private void showAllVariants(List<ProductVariant> variants, int row) {
            SwingUtilities.invokeLater(() -> {
                // Create a detailed variant display dialog
                JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(table), "Tất cả variants", true);
                dialog.setLayout(new BorderLayout());

                // Create variant table
                String[] columnNames = {"Màu", "Size", "Kho", "Giá", "Trạng thái"};
                Object[][] data = new Object[variants.size()][5];

                for (int i = 0; i < variants.size(); i++) {
                    ProductVariant variant = variants.get(i);
                    data[i][0] = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                    data[i][1] = variant.getSize() != null ? variant.getSize().getName() : "Free";
                    data[i][2] = variant.getStockQuantity();
                    data[i][3] = String.format("%,.0f VNĐ", variant.getPrice());
                    data[i][4] = variant.getStockQuantity() > 0 ? "Còn hàng" : "Hết hàng";
                }

                JTable variantTable = new JTable(data, columnNames);
                variantTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                variantTable.setRowHeight(30);
                variantTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

                JScrollPane scrollPane = new JScrollPane(variantTable);
                scrollPane.setPreferredSize(new Dimension(500, 300));

                dialog.add(scrollPane, BorderLayout.CENTER);

                // Add close button
                JButton closeButton = new JButton("Đóng");
                closeButton.addActionListener(e -> dialog.dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(closeButton);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                dialog.pack();
                dialog.setLocationRelativeTo(table);
                dialog.setVisible(true);
            });
        }
    }
    
    // Inner classes for action column
    private class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            if (!isSelected) {
                if (row % 2 == 0) {
                    panel.setBackground(Color.WHITE);
                } else {
                    panel.setBackground(new Color(248, 249, 250));
                }
            } else {
                panel.setBackground(new Color(217, 237, 247));
            }
            
            JButton editBtn = new JButton("Sửa️");
            editBtn.setPreferredSize(new Dimension(50, 25));
            editBtn.setBackground(new Color(241, 196, 15));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            
            JButton deleteBtn = new JButton("Xóa");
            deleteBtn.setPreferredSize(new Dimension(50, 25));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            
            panel.add(editBtn);
            panel.add(deleteBtn);
            
            return panel;
        }
    }
    
    private class ActionCellEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private int currentRow;
        
        public ActionCellEditor() {
            super(new JTextField());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            
            editBtn = new JButton("Sửa");
            editBtn.setPreferredSize(new Dimension(50, 25));
            editBtn.setBackground(new Color(241, 196, 15));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            deleteBtn = new JButton("Xóa");
            deleteBtn.setPreferredSize(new Dimension(50, 25));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            editBtn.addActionListener(e -> {
                if (editActionListener != null) {
                    editActionListener.actionPerformed(
                        new java.awt.event.ActionEvent(CustomTable.this, currentRow, "edit"));
                }
                fireEditingStopped();
            });
            
            deleteBtn.addActionListener(e -> {
                if (deleteActionListener != null) {
                    deleteActionListener.actionPerformed(
                        new java.awt.event.ActionEvent(CustomTable.this, currentRow, "delete"));
                }
                fireEditingStopped();
            });
            
            panel.add(editBtn);
            panel.add(deleteBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "actions";
        }
    }
}