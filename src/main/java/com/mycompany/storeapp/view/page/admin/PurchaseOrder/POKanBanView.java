/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.storeapp.view.page.admin.PurchaseOrder;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.controller.admin.PurchaseOrderController;
import com.mycompany.storeapp.model.entity.PurchaseOrder;
import com.mycompany.storeapp.view.component.admin.KanbanComponent.POKanBanHeader;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Kanban view for managing purchase orders
 * @author ADMIN
 */
public class POKanBanView extends JPanel {
    private final PurchaseOrderController purchaseOrderController;
    private POKanBanHeader header;
    private POKanBanContent content;
    private Map<String, List<PurchaseOrder>> ordersData;
    private Map<String, Integer> orderCounts;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public POKanBanView() {
        var dbConnection = new DatabaseConnection();
        this.purchaseOrderController = new PurchaseOrderController(dbConnection);
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initComponents() {
        header = new POKanBanHeader();
        content = new POKanBanContent(purchaseOrderController);
        content.setRefreshCallback(this::refreshData);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        header.addRefreshListener(e -> refreshData());

        header.addAddListener(e -> showAddPurchaseOrderDialog());

//        header.addUpdateListener(e -> {
//            PurchaseOrder selectedPO = content.getSelectedPurchaseOrder();
//            if (selectedPO != null) {
//                if (!purchaseOrderController.canEditPurchaseOrder(selectedPO.getPoId())) {
//                    showErrorDialog("Không thể chỉnh sửa đơn nhập hàng ở trạng thái " + selectedPO.getStatus() + "!");
//                    return;
//                }
//                showEditPurchaseOrderDialog(selectedPO);
//            } else {
//                showErrorDialog("Vui lòng chọn một đơn nhập hàng để chỉnh sửa!");
//            }
//        });

        header.addDeleteListener(e -> {
            PurchaseOrder selectedPO = content.getSelectedPurchaseOrder();
            if (selectedPO != null) {
                if (!purchaseOrderController.canEditPurchaseOrder(selectedPO.getPoId())) {
                    showErrorDialog("Không thể xóa đơn nhập hàng ở trạng thái " + selectedPO.getStatus() + "!");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa đơn nhập hàng #" + selectedPO.getPoId() + "?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (purchaseOrderController.deletePurchaseOrder(selectedPO.getPoId())) {
                        showSuccessDialog("Xóa đơn nhập hàng thành công!");
                        refreshData();
                    } else {
                        showErrorDialog("Lỗi khi xóa đơn nhập hàng!");
                    }
                }
            } else {
                showErrorDialog("Vui lòng chọn một đơn nhập hàng để xóa!");
            }
        });

//        header.addExportListener(e -> exportToCSV());
    }

    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ordersData = purchaseOrderController.getPurchaseOrdersGroupedByStatus();
                orderCounts = purchaseOrderController.getPurchaseOrderCountByStatus();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    content.loadData(ordersData, orderCounts);
                } catch (Exception e) {
                    showErrorDialog("Lỗi khi tải dữ liệu: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }


    private double calculateTotalCost() {
        double totalCost = 0.0;
        if (ordersData != null) {
            for (PurchaseOrder po : ordersData.getOrDefault(PurchaseOrderController.STATUS_COMPLETED, List.of())) {
                totalCost += po.getTotalAmount();
            }
        }
        return totalCost;
    }

    private void refreshData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        header.getRefreshButton().setText("Đang tải...");
        header.getRefreshButton().setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ordersData = purchaseOrderController.getPurchaseOrdersGroupedByStatus();
                orderCounts = purchaseOrderController.getPurchaseOrderCountByStatus();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    content.loadData(ordersData, orderCounts);
                } catch (Exception e) {
                    showErrorDialog("Lỗi khi làm mới dữ liệu: " + e.getMessage());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    header.getRefreshButton().setText("Làm mới");
                    header.getRefreshButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("don_nhap_hang_" + System.currentTimeMillis() + ".csv")) {
            // Write header
            writer.append("\"Mã đơn\",\"Mã nhà cung cấp\",\"Ngày đặt\",\"Ngày dự kiến\",\"Tổng tiền\",\"Trạng thái\",\"Ghi chú\"\n");

            // Write data
            if (ordersData != null) {
                for (List<PurchaseOrder> orders : ordersData.values()) {
                    for (PurchaseOrder po : orders) {
                        writer.append(String.format("\"%d\",\"%d\",\"%s\",\"%s\",\"%.2f\",\"%s\",\"%s\"\n",
                                po.getPoId(),
                                po.getSupplierId(),
                                dateFormatter.format(po.getOrderDate()),
                                dateFormatter.format(po.getExpectedDate()),
                                po.getTotalAmount(),
                                po.getStatus(),
                                po.getNote() != null ? po.getNote().replace("\"", "\"\"") : ""));
                    }
                }
            }

            showSuccessDialog("Xuất CSV thành công!");
        } catch (IOException e) {
            showErrorDialog("Lỗi khi xuất CSV: " + e.getMessage());
        }
    }

    private void showAddPurchaseOrderDialog() {
        POAddDialog dialog = new POAddDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm đơn nhập hàng mới",
                null,
                purchaseOrderController,
                this::refreshData
        );
        dialog.setVisible(true);
    }

    private void showEditPurchaseOrderDialog(PurchaseOrder po) {
        POEditDialog.showDialog((Window) SwingUtilities.getWindowAncestor(this), po, purchaseOrderController, this::refreshData);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public void refreshView() {
        refreshData();
    }

    public POKanBanHeader getHeader() {
        return header;
    }

    public POKanBanContent getContent() {
        return content;
    }

    public PurchaseOrderController getPurchaseOrderController() {
        return purchaseOrderController;
    }

    public Map<String, List<PurchaseOrder>> getOrdersData() {
        return ordersData;
    }

    public Map<String, Integer> getOrderCounts() {
        return orderCounts;
    }
}