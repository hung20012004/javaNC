/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.PurchaseOrder;

import com.mycompany.storeapp.controller.admin.PurchaseOrderController;
import com.mycompany.storeapp.model.entity.PurchaseOrder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Content component for POKanban board displaying purchase orders
 * @author ADMIN
 */
public class POKanBanContent extends JPanel {
    private final PurchaseOrderController purchaseOrderController;
    private JPanel kanbanPanel;
    private Map<String, JPanel> statusColumns;
    private Map<String, List<PurchaseOrder>> ordersData;
    private Runnable refreshCallback;
    private PurchaseOrder selectedPurchaseOrder;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    // Color scheme cho các trạng thái
    private static final Color PENDING_COLOR = new Color(241, 196, 15);
    private static final Color PROCESSING_COLOR = new Color(155, 89, 182);
    private static final Color COMPLETED_COLOR = new Color(46, 204, 113);
    private static final Color CANCELLED_COLOR = new Color(231, 76, 60);

    public POKanBanContent(PurchaseOrderController purchaseOrderController) {
        this.purchaseOrderController = purchaseOrderController;
        this.statusColumns = new HashMap<>();
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        kanbanPanel = new JPanel();
        kanbanPanel.setBackground(new Color(236, 240, 241));
        setupKanbanBoard();
    }

    private void setupKanbanBoard() {
        kanbanPanel.setLayout(new BoxLayout(kanbanPanel, BoxLayout.X_AXIS));

        createStatusColumn(PurchaseOrderController.STATUS_PENDING, "Chờ xử lý", PENDING_COLOR);
        createStatusColumn(PurchaseOrderController.STATUS_PROCESSING, "Đang xử lý", PROCESSING_COLOR);
        createStatusColumn(PurchaseOrderController.STATUS_COMPLETED, "Hoàn thành", COMPLETED_COLOR);
        createStatusColumn(PurchaseOrderController.STATUS_CANCELLED, "Đã hủy", CANCELLED_COLOR);
    }

    private void createStatusColumn(String status, String title, Color headerColor) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        JPanel columnHeader = new JPanel(new BorderLayout());
        columnHeader.setBackground(headerColor);
        columnHeader.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel countLabel = new JLabel("0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(Color.WHITE);
        countLabel.setOpaque(true);
        countLabel.setBackground(new Color(0, 0, 0, 50));
        countLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        countLabel.setName(status + "_count");

        columnHeader.add(titleLabel, BorderLayout.CENTER);
        columnHeader.add(countLabel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setupDropTarget(contentPanel, status);

        column.add(columnHeader, BorderLayout.NORTH);
        column.add(scrollPane, BorderLayout.CENTER);

        kanbanPanel.add(column);
        statusColumns.put(status, contentPanel);
    }

    private void setupDropTarget(JPanel panel, String targetStatus) {
        new DropTarget(panel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                panel.setBackground(new Color(232, 245, 233));
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}

            @Override
            public void dragExit(DropTargetEvent dte) {
                panel.setBackground(Color.WHITE);
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                panel.setBackground(Color.WHITE);
                handleDrop(dtde, targetStatus);
            }
        });
    }

    private void handleDrop(DropTargetDropEvent dtde, String targetStatus) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            Transferable transferable = dtde.getTransferable();
            POTransferData poData = (POTransferData) transferable.getTransferData(POTransferData.PO_FLAVOR);

            if (poData == null || poData.currentStatus == null) {
                showErrorDialog("Dữ liệu đơn nhập hàng không hợp lệ!");
                dtde.dropComplete(false);
                return;
            }

            String normalizedCurrentStatus = poData.currentStatus.trim();
            String normalizedTargetStatus = targetStatus.trim();

            if (normalizedCurrentStatus.equals(normalizedTargetStatus)) {
                dtde.dropComplete(true);
                return;
            }

            List<String> validNextStatuses = purchaseOrderController.getValidNextStatuses(normalizedCurrentStatus);
            if (validNextStatuses != null && validNextStatuses.contains(normalizedTargetStatus)) {
                PurchaseOrderController.POTransitionResult result = purchaseOrderController.changePurchaseOrderStatus(poData.poId, normalizedTargetStatus);
                if (result.isSuccess()) {
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    showSuccessDialog("Cập nhật trạng thái thành công!");
                } else {
                    showErrorDialog(result.getMessage());
                }
            } else {
                String validStatusesStr = validNextStatuses != null ? String.join(", ", validNextStatuses) : "Không có";
                showWarningDialog(String.format("Không thể chuyển từ '%s' sang '%s'.\nCác trạng thái hợp lệ: %s",
                        normalizedCurrentStatus, normalizedTargetStatus, validStatusesStr));
            }

            dtde.dropComplete(true);
        } catch (Exception e) {
            dtde.dropComplete(false);
            showErrorDialog("Lỗi khi chuyển đổi trạng thái: " + e.getMessage());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JScrollPane horizontalScrollPane = new JScrollPane(kanbanPanel);
        horizontalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        horizontalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        horizontalScrollPane.setBorder(null);

        add(horizontalScrollPane, BorderLayout.CENTER);
    }

    public void loadData(Map<String, List<PurchaseOrder>> ordersData, Map<String, Integer> counts) {
        this.ordersData = ordersData;

        for (JPanel panel : statusColumns.values()) {
            panel.removeAll();
        }

        for (Map.Entry<String, List<PurchaseOrder>> entry : ordersData.entrySet()) {
            String status = entry.getKey();
            List<PurchaseOrder> orders = entry.getValue();
            JPanel column = statusColumns.get(status);

            if (column != null) {
                for (PurchaseOrder po : orders) {
                    JPanel poCard = createPOCard(po);
                    column.add(poCard);
                    column.add(Box.createVerticalStrut(10));
                }
            }

            updateCountLabel(status, counts.getOrDefault(status, 0));
        }

        revalidate();
        repaint();
    }

    private JPanel createPOCard(PurchaseOrder po) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(12, 12, 12, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(200, 120));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel idLabel = new JLabel("Đơn nhập #" + po.getPoId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(51, 51, 51));

        JLabel dateLabel = new JLabel(dateFormatter.format(po.getOrderDate()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(127, 140, 141));

        JLabel supplierLabel = new JLabel("NCC: " + po.getSupplierId());
        supplierLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        supplierLabel.setForeground(new Color(51, 51, 51));

        JLabel totalLabel = new JLabel(currencyFormat.format(po.getTotalAmount()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(46, 204, 113));

        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(supplierLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(totalLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        setupDragSource(card, po);

        //if (PurchaseOrderController.STATUS_PENDING.equals(po.getStatus())) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedPurchaseOrder = po;
                    if (e.getClickCount() == 2) {
                        showEditPurchaseOrderDialog(po);
                    }
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLUE, 2),
                            new EmptyBorder(12, 12, 12, 12)));
                    for (JPanel column : statusColumns.values()) {
                        for (Component c : column.getComponents()) {
                            if (c instanceof JPanel && c != card) {
                                ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                                        new EmptyBorder(12, 12, 12, 12)));
                            }
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(241, 248, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                }
            });
        //}

        return card;
    }

    private void setupDragSource(JPanel card, PurchaseOrder po) {
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(card, DnDConstants.ACTION_MOVE,
                dge -> {
                    POTransferData transferData = new POTransferData(po.getPoId(), po.getStatus());
                    dge.startDrag(DragSource.DefaultMoveDrop, transferData);
                });

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                card.setBackground(new Color(255, 249, 196));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
    }

    private void updateCountLabel(String status, int count) {
        Component[] components = kanbanPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel column = (JPanel) comp;
                Component header = column.getComponent(0);
                if (header instanceof JPanel) {
                    JPanel headerPanel = (JPanel) header;
                    Component[] headerComps = headerPanel.getComponents();
                    for (Component headerComp : headerComps) {
                        if (headerComp instanceof JLabel) {
                            JLabel label = (JLabel) headerComp;
                            if ((status + "_count").equals(label.getName())) {
                                label.setText(String.valueOf(count));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void showEditPurchaseOrderDialog(PurchaseOrder po) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        POEditDialog.showDialog(parentWindow, po, purchaseOrderController, refreshCallback);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public PurchaseOrder getSelectedPurchaseOrder() {
        return selectedPurchaseOrder;
    }

    public Map<String, List<PurchaseOrder>> getOrdersData() {
        return ordersData;
    }

    public Map<String, JPanel> getStatusColumns() {
        return statusColumns;
    }

    public static class POTransferData implements Transferable {
        public static final DataFlavor PO_FLAVOR = new DataFlavor(POTransferData.class, "PurchaseOrder");

        private final int poId;
        private final String currentStatus;

        public POTransferData(int poId, String currentStatus) {
            this.poId = poId;
            this.currentStatus = currentStatus;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{PO_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return PO_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return this;
        }
    }
}