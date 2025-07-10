package com.mycompany.storeapp.controller.shop;

import com.mycompany.storeapp.model.dao.ShippingAddressDAO;
import com.mycompany.storeapp.model.entity.ShippingAddress;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.session.Session;
import javax.swing.JOptionPane;
import java.util.List;

/**
 * Controller for managing shipping addresses
 * @author Hi
 */
public class ShippingAddressController {
    private final ShippingAddressDAO shippingAddressDAO;
    private final Session session;

    public ShippingAddressController() {
        this.shippingAddressDAO = new ShippingAddressDAO(DatabaseConnection.getInstance());
        this.session = Session.getInstance();
    }

    /**
     * Get all shipping addresses for current user
     */
    public List<ShippingAddress> getUserShippingAddresses() {
        if (session.getCurrentUser() == null) {
            return null;
        }
        return shippingAddressDAO.getShippingAddressesByUserId(session.getCurrentUser().getId());
    }

    /**
     * Get shipping address by ID
     */
    public ShippingAddress getShippingAddressById(int addressId) {
        return shippingAddressDAO.getShippingAddressById(addressId);
    }

    /**
     * Add new shipping address for current user
     */
    public boolean addShippingAddress(ShippingAddress address) {
        if (session.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập để thêm địa chỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate required fields
        if (!validateShippingAddress(address)) {
            return false;
        }

        // Set user ID from session
        address.setUserId(session.getCurrentUser().getId());

        // If this is the first address, set as default
        List<ShippingAddress> existingAddresses = getUserShippingAddresses();
        if (existingAddresses == null || existingAddresses.isEmpty()) {
            address.setDefault(true);
        }

        boolean success = shippingAddressDAO.addShippingAddress(address);
        if (success) {
            JOptionPane.showMessageDialog(null, "Thêm địa chỉ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Thêm địa chỉ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }

    /**
     * Update shipping address
     */
    public boolean updateShippingAddress(ShippingAddress address) {
        if (session.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập để cập nhật địa chỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate required fields
        if (!validateShippingAddress(address)) {
            return false;
        }

        // Ensure user can only update their own addresses
        ShippingAddress existingAddress = shippingAddressDAO.getShippingAddressById(address.getAddressId());
        if (existingAddress == null || existingAddress.getUserId() != session.getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(null, "Không có quyền cập nhật địa chỉ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean success = shippingAddressDAO.updateShippingAddress(address);
        if (success) {
            JOptionPane.showMessageDialog(null, "Cập nhật địa chỉ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Cập nhật địa chỉ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }

    /**
     * Delete shipping address
     */
    public boolean deleteShippingAddress(int addressId) {
        if (session.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập để xóa địa chỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Ensure user can only delete their own addresses
        ShippingAddress existingAddress = shippingAddressDAO.getShippingAddressById(addressId);
        if (existingAddress == null || existingAddress.getUserId() != session.getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(null, "Không có quyền xóa địa chỉ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Bạn có chắc chắn muốn xóa địa chỉ này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return false;
        }

        boolean success = shippingAddressDAO.deleteShippingAddress(addressId);
        if (success) {
            JOptionPane.showMessageDialog(null, "Xóa địa chỉ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Xóa địa chỉ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }

    /**
     * Set default shipping address
     */
    public boolean setDefaultAddress(int addressId) {
        if (session.getCurrentUser() == null) {
            return false;
        }

        // Ensure user can only set their own address as default
        ShippingAddress address = shippingAddressDAO.getShippingAddressById(addressId);
        if (address == null || address.getUserId() != session.getCurrentUser().getId()) {
            JOptionPane.showMessageDialog(null, "Không có quyền thay đổi địa chỉ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return shippingAddressDAO.setDefaultAddress(session.getCurrentUser().getId(), addressId);
    }

    /**
     * Get default shipping address for current user
     */
    public ShippingAddress getDefaultAddress() {
        if (session.getCurrentUser() == null) {
            return null;
        }
        return shippingAddressDAO.getDefaultAddress(session.getCurrentUser().getId());
    }

    /**
     * Validate shipping address fields
     */
    private boolean validateShippingAddress(ShippingAddress address) {
        if (address.getRecipientName() == null || address.getRecipientName().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tên người nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (address.getPhone() == null || address.getPhone().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (address.getStreetAddress() == null || address.getStreetAddress().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập địa chỉ chi tiết!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (address.getWard() == null || address.getWard().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập phường/xã!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (address.getDistrict() == null || address.getDistrict().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập quận/huyện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (address.getProvince() == null || address.getProvince().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tỉnh/thành phố!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate phone number format
        if (!address.getPhone().matches("^[0-9]{10,11}$")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Create ShippingAddress from form data
     */
    public ShippingAddress createShippingAddress(String recipientName, String phone, 
                                                String streetAddress, String ward, 
                                                String district, String province, 
                                                boolean isDefault) {
        ShippingAddress address = new ShippingAddress();
        address.setRecipientName(recipientName.trim());
        address.setPhone(phone.trim());
        address.setStreetAddress(streetAddress.trim());
        address.setWard(ward.trim());
        address.setDistrict(district.trim());
        address.setProvince(province.trim());
        address.setDefault(isDefault);
        
        return address;
    }

    /**
     * Get full formatted address string
     */
    public String getFormattedAddress(ShippingAddress address) {
        if (address == null) return "";
        
        StringBuilder fullAddress = new StringBuilder();
        if (address.getStreetAddress() != null && !address.getStreetAddress().isEmpty()) {
            fullAddress.append(address.getStreetAddress());
        }
        if (address.getWard() != null && !address.getWard().isEmpty()) {
            fullAddress.append(", ").append(address.getWard());
        }
        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            fullAddress.append(", ").append(address.getDistrict());
        }
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            fullAddress.append(", ").append(address.getProvince());
        }
        
        return fullAddress.toString();
    }
}