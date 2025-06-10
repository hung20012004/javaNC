/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component;

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
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * Custom table component tái sử dụng với hỗ trợ hiển thị hình ảnh
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
    
    // Cache cho hình ảnh để tránh load lại nhiều lần
    private static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private static final ImageIcon DEFAULT_IMAGE = createDefaultImage();
    private static final ImageIcon LOADING_IMAGE = createLoadingImage();
    
    public CustomTable(String[] columnNames, String[] fieldNames, boolean hasActionColumn) {
        this.columnNames = columnNames;
        this.fieldNames = fieldNames;
        this.hasActionColumn = hasActionColumn;
        
        // Tìm index của cột hình ảnh
        findImageColumnIndex();
        
        initComponents();
        setupTable();
        setupLayout();
    }
    
    private void findImageColumnIndex() {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].toLowerCase().contains("hình ảnh") || 
                columnNames[i].toLowerCase().contains("image") ||
                (fieldNames != null && i < fieldNames.length && 
                 fieldNames[i].toLowerCase().contains("image"))) {
                imageColumnIndex = i;
                break;
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
        table.setRowHeight(imageColumnIndex >= 0 ? 90 : 45); // Tăng chiều cao dòng nếu có hình ảnh
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
                        rowData[i] = value != null ? value.toString() : "";
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
            
            JButton editBtn = new JButton("Sửa");
            editBtn.setPreferredSize(new Dimension(50, 25));
            editBtn.setBackground(new Color(241, 196, 15));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            
            JButton deleteBtn = new JButton("Xóa");
            deleteBtn.setPreferredSize(new Dimension(50, 25));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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