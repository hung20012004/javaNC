/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.model.dao.PurchaseOrderDAO;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.entity.PurchaseOrder;
import com.mycompany.storeapp.model.entity.PurchaseOrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class PurchaseOrderController {
    private final PurchaseOrderDAO purchaseOrderDAO;
    private final ProductVariantDAO productVariantDAO;

    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    
    private static final List<String> EDITABLE_STATUSES = Arrays.asList(STATUS_PENDING);

    
    private static final Map<String, List<String>> STATUS_TRANSITIONS = new HashMap<>();

    static {
        STATUS_TRANSITIONS.put(STATUS_PENDING, Arrays.asList(STATUS_PROCESSING, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_PROCESSING, Arrays.asList(STATUS_COMPLETED, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_COMPLETED, Arrays.asList()); 
        STATUS_TRANSITIONS.put(STATUS_CANCELLED, Arrays.asList()); 
    }

    public PurchaseOrderController(DatabaseConnection connection) {
        this.purchaseOrderDAO = new PurchaseOrderDAO(connection);
        this.productVariantDAO = new ProductVariantDAO(connection);
    }


    public Map<String, List<PurchaseOrder>> getPurchaseOrdersGroupedByStatus() {
        Map<String, List<PurchaseOrder>> groupedOrders = new HashMap<>();
        groupedOrders.put(STATUS_PENDING, purchaseOrderDAO.getPurchaseOrdersByStatus(STATUS_PENDING));
        groupedOrders.put(STATUS_PROCESSING, purchaseOrderDAO.getPurchaseOrdersByStatus(STATUS_PROCESSING));
        groupedOrders.put(STATUS_COMPLETED, purchaseOrderDAO.getPurchaseOrdersByStatus(STATUS_COMPLETED));
        groupedOrders.put(STATUS_CANCELLED, purchaseOrderDAO.getPurchaseOrdersByStatus(STATUS_CANCELLED));
        return groupedOrders;
    }


    public Map<String, Integer> getPurchaseOrderCountByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put(STATUS_PENDING, purchaseOrderDAO.getPurchaseOrderCountByStatus(STATUS_PENDING));
        counts.put(STATUS_PROCESSING, purchaseOrderDAO.getPurchaseOrderCountByStatus(STATUS_PROCESSING));
        counts.put(STATUS_COMPLETED, purchaseOrderDAO.getPurchaseOrderCountByStatus(STATUS_COMPLETED));
        counts.put(STATUS_CANCELLED, purchaseOrderDAO.getPurchaseOrderCountByStatus(STATUS_CANCELLED));
        return counts;
    }


    public POTransitionResult changePurchaseOrderStatus(int poId, String newStatus) {
        try {
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
            if (purchaseOrder == null) {
                return new POTransitionResult(false, "Không tìm thấy đơn nhập hàng");
            }
            String currentStatus = purchaseOrder.getStatus().trim();
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                return new POTransitionResult(false, 
                    String.format("Không thể chuyển từ trạng thái '%s' sang '%s'", currentStatus, newStatus));
            }
            boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, newStatus);
            if (success && newStatus.equals(STATUS_COMPLETED)) {
                updateInventoryOnCompletion(poId); // Gọi tại đây
            }
            return success ? new POTransitionResult(true, "Cập nhật trạng thái thành công") 
                          : new POTransitionResult(false, "Lỗi khi cập nhật trạng thái");
        } catch (Exception e) {
            return new POTransitionResult(false, "Lỗi hệ thống: " + e.getMessage());
        }
    }

 
    private void updateInventoryOnCompletion(int poId) {
        List<PurchaseOrderDetail> details = purchaseOrderDAO.getPurchaseOrderById(poId).getDetails();
        for (PurchaseOrderDetail detail : details) {
            int variantId = detail.getProductId(); // Giả định productId là variantId
            int quantity = detail.getQuantity();
            productVariantDAO.updateQuantity(variantId, quantity); // Cộng số lượng vào product_variants
        }
    }

    public boolean canEditPurchaseOrder(int poId) {
        PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
        if (purchaseOrder == null) {
            return false;
        }
        return EDITABLE_STATUSES.contains(purchaseOrder.getStatus());
    }

    public boolean canEditPurchaseOrder(String status) {
        return EDITABLE_STATUSES.contains(status);
    }

    public List<String> getValidNextStatuses(String currentStatus) {
        return STATUS_TRANSITIONS.getOrDefault(currentStatus, Arrays.asList());
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        List<String> validNextStatuses = STATUS_TRANSITIONS.get(currentStatus);
        return validNextStatuses != null && validNextStatuses.contains(newStatus);
    }

    public PurchaseOrder getPurchaseOrderDetails(int poId) {
        return purchaseOrderDAO.getPurchaseOrderById(poId);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderDAO.getAllPurchaseOrders();
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        return purchaseOrderDAO.getPurchaseOrdersByStatus(status);
    }

    public POTransitionResult cancelPurchaseOrder(int poId, String reason) {
        PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
        if (purchaseOrder == null) {
            return new POTransitionResult(false, "Không tìm thấy đơn nhập hàng");
        }
        String currentStatus = purchaseOrder.getStatus();
        if (!Arrays.asList(STATUS_PENDING, STATUS_PROCESSING).contains(currentStatus)) {
            return new POTransitionResult(false, "Không thể hủy đơn nhập hàng ở trạng thái " + currentStatus);
        }
        return changePurchaseOrderStatus(poId, STATUS_CANCELLED);
    }


    public POTransitionResult startProcessing(int poId) {
        return changePurchaseOrderStatus(poId, STATUS_PROCESSING);
    }


    public POTransitionResult completePurchaseOrder(int poId) {
        return changePurchaseOrderStatus(poId, STATUS_COMPLETED);
    }

    public boolean createPurchaseOrder(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> details) {
        try {
            return purchaseOrderDAO.createPurchaseOrder(purchaseOrder, details);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        try {
            // Cho phép cập nhật trạng thái mà không cần kiểm tra EDITABLE_STATUSES
            return purchaseOrderDAO.updatePurchaseOrder(purchaseOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePurchaseOrder(int poId) {
        try {
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
            if (purchaseOrder == null) {
                return false;
            }
            if (!canEditPurchaseOrder(purchaseOrder.getPoId())) {
                return false;
            }
            return purchaseOrderDAO.deletePurchaseOrder(poId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<String> getAllStatuses() {
        return Arrays.asList(STATUS_PENDING, STATUS_PROCESSING, STATUS_COMPLETED, STATUS_CANCELLED);
    }


    public static class POTransitionResult {
        private final boolean success;
        private final String message;

        public POTransitionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("POTransitionResult{success=%s, message='%s'}", success, message);
        }
    }
    

}