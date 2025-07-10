/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.TagDAO;
import com.mycompany.storeapp.model.entity.Tag;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Hi
 */
public class TagController {
    private final TagDAO tagDAO;
    
    public TagController() {
        var dbConnection = new DatabaseConnection();
        this.tagDAO = new TagDAO(dbConnection);
    }
    
    public TagController(DatabaseConnection dbConnection) {
        this.tagDAO = new TagDAO(dbConnection);
    }
    
    /**
     * Tạo mới một tag
     * @param tag Tag cần tạo
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createTag(Tag tag) {
        try {
            // Validate dữ liệu trước khi tạo
            if (!validateTag(tag)) {
                return false;
            }
            
            // Tạo slug từ name nếu chưa có
            if (tag.getSlug() == null || tag.getSlug().trim().isEmpty()) {
                tag.setSlug(generateSlug(tag.getName()));
            }
            
            // Kiểm tra tên đã tồn tại chưa
            if (tagDAO.existsByName(tag.getName())) {
                showErrorMessage("Tên tag đã tồn tại!");
                return false;
            }
            
            // Kiểm tra slug đã tồn tại chưa
            if (tagDAO.existsBySlug(tag.getSlug())) {
                showErrorMessage("Slug tag đã tồn tại!");
                return false;
            }
            
            boolean result = tagDAO.create(tag);
            if (result) {
                showSuccessMessage("Tạo tag thành công!");
            } else {
                showErrorMessage("Không thể tạo tag. Vui lòng thử lại!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo tag: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy tag theo ID
     * @param id ID của tag
     * @return Tag object hoặc null nếu không tìm thấy
     */
    public Tag getTagById(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID tag không hợp lệ!");
                return null;
            }
            
            Tag tag = tagDAO.getById(id);
            if (tag == null) {
                showInfoMessage("Không tìm thấy tag với ID: " + id);
            }
            return tag;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin tag: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy tất cả tags
     * @return List các Tag
     */
    public List<Tag> getAllTags() {
        try {
            List<Tag> tags = tagDAO.getAll();
            if (tags.isEmpty()) {
                showInfoMessage("Chưa có tag nào trong hệ thống!");
            }
            return tags;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách tag: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tìm kiếm tag theo tên
     * @param name Tên tag cần tìm
     * @return List các Tag khớp với tên
     */
    public List<Tag> searchTagsByName(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                showErrorMessage("Tên tìm kiếm không được để trống!");
                return null;
            }
            
            List<Tag> tags = tagDAO.findByName(name.trim());
            if (tags.isEmpty()) {
                showInfoMessage("Không tìm thấy tag nào với tên: " + name);
            }
            return tags;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tìm kiếm tag: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cập nhật tag
     * @param tag Tag cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateTag(Tag tag) {
        try {
            // Validate dữ liệu trước khi cập nhật
            if (!validateTag(tag)) {
                return false;
            }
            
            if (tag.getTagId() == null || tag.getTagId() <= 0) {
                showErrorMessage("ID tag không hợp lệ!");
                return false;
            }
            
            // Cập nhật slug từ name nếu cần
            if (tag.getSlug() == null || tag.getSlug().trim().isEmpty()) {
                tag.setSlug(generateSlug(tag.getName()));
            }
            
            // Kiểm tra tên đã tồn tại chưa (trừ chính nó)
            Tag existingTag = tagDAO.getById(tag.getTagId());
            if (existingTag == null) {
                showErrorMessage("Tag không tồn tại!");
                return false;
            }
            
            if (!existingTag.getName().equals(tag.getName()) && tagDAO.existsByName(tag.getName())) {
                showErrorMessage("Tên tag đã tồn tại!");
                return false;
            }
            
            if (!existingTag.getSlug().equals(tag.getSlug()) && tagDAO.existsBySlug(tag.getSlug())) {
                showErrorMessage("Slug tag đã tồn tại!");
                return false;
            }
            
            boolean result = tagDAO.update(tag);
            if (result) {
                showSuccessMessage("Cập nhật tag thành công!");
            } else {
                showErrorMessage("Không thể cập nhật tag. Vui lòng thử lại!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật tag: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tag
     * @param id ID của tag cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteTag(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID tag không hợp lệ!");
                return false;
            }
            
            // Kiểm tra tag có tồn tại không
            Tag tag = tagDAO.getById(id);
            if (tag == null) {
                showErrorMessage("Tag không tồn tại!");
                return false;
            }
            
            // Kiểm tra tag có được sử dụng bởi product nào không
            if (tagDAO.isTagUsedByProducts(id)) {
                showErrorMessage("Không thể xóa tag vì đang được sử dụng bởi sản phẩm!");
                return false;
            }
            
            // Xác nhận trước khi xóa
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa tag '" + tag.getName() + "'?\nHành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            boolean result = tagDAO.delete(id);
            if (result) {
                showSuccessMessage("Xóa tag thành công!");
            } else {
                showErrorMessage("Không thể xóa tag. Vui lòng thử lại!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa tag: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy tags theo product ID
     * @param productId ID của product
     * @return List các Tag thuộc về product
     */
    public List<Tag> getTagsByProductId(int productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return null;
            }
            
            return tagDAO.getTagsByProductId(productId);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy tags của sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Thêm tag vào product
     * @param productId ID của product
     * @param tagId ID của tag
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addTagToProduct(int productId, int tagId) {
        try {
            if (productId <= 0 || tagId <= 0) {
                showErrorMessage("ID sản phẩm hoặc ID tag không hợp lệ!");
                return false;
            }
            
            boolean result = tagDAO.addTagToProduct(productId, tagId);
            if (result) {
                showSuccessMessage("Thêm tag vào sản phẩm thành công!");
            } else {
                showErrorMessage("Không thể thêm tag vào sản phẩm!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi thêm tag vào sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tag khỏi product
     * @param productId ID của product
     * @param tagId ID của tag
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeTagFromProduct(int productId, int tagId) {
        try {
            if (productId <= 0 || tagId <= 0) {
                showErrorMessage("ID sản phẩm hoặc ID tag không hợp lệ!");
                return false;
            }
            
            boolean result = tagDAO.removeTagFromProduct(productId, tagId);
            if (result) {
                showSuccessMessage("Xóa tag khỏi sản phẩm thành công!");
            } else {
                showErrorMessage("Không thể xóa tag khỏi sản phẩm!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa tag khỏi sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật tất cả tags cho một product
     * @param productId ID của product
     * @param tagIds List ID các tag mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateProductTags(int productId, List<Integer> tagIds) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            // Xóa tất cả tags cũ
            if (!tagDAO.removeAllTagsFromProduct(productId)) {
                showErrorMessage("Không thể xóa tags cũ!");
                return false;
            }
            
            // Thêm tags mới
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Integer tagId : tagIds) {
                    if (tagId != null && tagId > 0) {
                        if (!tagDAO.addTagToProduct(productId, tagId)) {
                            showErrorMessage("Không thể thêm tag với ID: " + tagId);
                            return false;
                        }
                    }
                }
            }
            
            showSuccessMessage("Cập nhật tags cho sản phẩm thành công!");
            return true;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật tags cho sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate dữ liệu tag
     * @param tag Tag cần validate
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateTag(Tag tag) {
        if (tag == null) {
            showErrorMessage("Thông tin tag không được để trống!");
            return false;
        }
        
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            showErrorMessage("Tên tag không được để trống!");
            return false;
        }
        
        if (tag.getName().trim().length() > 50) {
            showErrorMessage("Tên tag không được vượt quá 50 ký tự!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Tạo slug từ tên tag
     * @param name Tên tag
     * @return Slug string
     */
    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        return name.trim()
                .toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
    
    /**
     * Hiển thị thông báo thành công
     * @param message Nội dung thông báo
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Thành công",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Hiển thị thông báo lỗi
     * @param message Nội dung lỗi
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Hiển thị thông báo thông tin
     * @param message Nội dung thông tin
     */
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Thông tin",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}