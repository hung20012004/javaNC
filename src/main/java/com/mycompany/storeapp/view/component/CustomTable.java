package com.mycompany.storeapp.view.component;

import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.view.component.admin.ActionCellEditor;
import com.mycompany.storeapp.view.component.admin.ActionCellRenderer;
import com.mycompany.storeapp.view.component.admin.CustomTableCellRenderer;
import com.mycompany.storeapp.view.component.admin.ImageCellRenderer;
import com.mycompany.storeapp.view.component.admin.VariantCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.TableCellRenderer;

public class CustomTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private String[] columnNames;
    private String[] fieldNames;
    private boolean hasActionColumn;
    private int imageColumnIndex = -1;
    private int variantColumnIndex = -1;
    
    private static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private static final ImageIcon DEFAULT_IMAGE = createDefaultImage();
    private static final ImageIcon LOADING_IMAGE = createLoadingImage();
    
    private List<?> currentData;
    
    public CustomTable(String[] columnNames, String[] fieldNames, boolean hasActionColumn) {
        this.columnNames = columnNames;
        this.fieldNames = fieldNames;
        this.hasActionColumn = hasActionColumn;
        
        findSpecialColumnIndexes();
        initComponents();
        setupTable();
        setupLayout();
    }
    
    private void findSpecialColumnIndexes() {
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i].toLowerCase();
            String fieldName = fieldNames != null && i < fieldNames.length ? fieldNames[i] : "";
            
            if (columnName.contains("hình ảnh") || columnName.contains("image") || fieldName.contains("image")) {
                imageColumnIndex = i;
            }
            
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
        if (hasActionColumn) {
            String[] headers = new String[columnNames.length + 1];
            System.arraycopy(columnNames, 0, headers, 0, columnNames.length);
            headers[columnNames.length] = "Thao tác";
            tableModel = new DefaultTableModel(headers, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == getColumnCount() - 1;
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
        
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
    }
    
    private void setupTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(imageColumnIndex >= 0 || variantColumnIndex >= 0 ? 120 : 45);
        table.setGridColor(new Color(236, 240, 241));
        table.setSelectionBackground(new Color(217, 237, 247));
        table.setSelectionForeground(new Color(51, 51, 51));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        
        if (imageColumnIndex >= 0) {
            setupImageColumn();
        }
        
        if (variantColumnIndex >= 0) {
            setupVariantColumn();
        }
        
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        if (hasActionColumn) {
            setupActionColumn();
        }
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
        imageColumn.setCellRenderer(new ImageCellRenderer());
    }
    
    private void setupVariantColumn() {
        TableColumn variantColumn = table.getColumnModel().getColumn(variantColumnIndex);
        variantColumn.setPreferredWidth(200);
        variantColumn.setMaxWidth(300);
        variantColumn.setMinWidth(150);
        variantColumn.setCellRenderer(new VariantCellRenderer(table, currentData));
    }
    
    private void setupActionColumn() {
        int actionColumnIndex = table.getColumnCount() - 1;
        TableColumn actionColumn = table.getColumnModel().getColumn(actionColumnIndex);
        actionColumn.setPreferredWidth(150);
        actionColumn.setMaxWidth(150);
        actionColumn.setMinWidth(150);
        actionColumn.setCellRenderer(new ActionCellRenderer());
        actionColumn.setCellEditor(new ActionCellEditor(this));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setData(List<?> dataList) {
        this.currentData = dataList;
        
        tableModel.setRowCount(0);
        
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
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
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                try {
                    Object value = getNestedValue(item, fieldName);
                    if (fieldName.equals("profile.gender") && value != null) {
                        String gender = value.toString().toLowerCase();
                        if (gender.equals("male")) {
                            rowData[i] = "Nam";
                        } else if (gender.equals("female")) {
                            rowData[i] = "Nữ";
                        } else if (gender.equals("other")) {
                            rowData[i] = "Khác";
                        } else {
                            rowData[i] = value.toString();
                        }
                    } else if (value instanceof Boolean) {
                        rowData[i] = (Boolean) value ? "Có" : "Không";
                    } else if (value instanceof java.util.Date) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        rowData[i] = sdf.format((java.util.Date) value);
                    } else {
                        rowData[i] = value != null ? value.toString() : "";
                    }
                } catch (Exception e) {
                    rowData[i] = "Không tìm thấy dữ liệu";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return rowData;
    }
    
    private Object getNestedValue(Object obj, String fieldPath) {
        try {
            String[] parts = fieldPath.split("\\.");
            Object currentObj = obj;

            for (String part : parts) {
                if (currentObj == null) {
                    return null;
                }

                String getterName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                Class<?> clazz = currentObj.getClass();
                java.lang.reflect.Method getter = clazz.getMethod(getterName);
                currentObj = getter.invoke(currentObj);
            }

            return currentObj;
        } catch (Exception e) {
            System.err.println("Error accessing field: " + fieldPath + " - " + e.getMessage());
            return null;
        }
    }
    
    public void setCustomCellRenderer(int columnIndex, TableCellRenderer renderer) {
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            column.setCellRenderer(renderer);
        } else {
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
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
    
    public ActionListener getEditActionListener() {
        return editActionListener;
    }
    
    public ActionListener getDeleteActionListener() {
        return deleteActionListener;
    }
    
    public List<?> getCurrentData() {
        return currentData;
    }
    
    public static ImageIcon getDefaultImage() {
        return DEFAULT_IMAGE;
    }
    
    public static ImageIcon getLoadingImage() {
        return LOADING_IMAGE;
    }
    
    public static ConcurrentHashMap<String, ImageIcon> getImageCache() {
        return imageCache;
    }
}