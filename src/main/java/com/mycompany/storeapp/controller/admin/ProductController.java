package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ProductDAO;
import com.mycompany.storeapp.model.entity.Product;
import javax.swing.JOptionPane;
import java.util.List;

public class ProductController {
    private final ProductDAO productDAO;

    public ProductController() {
        this.productDAO = new ProductDAO(new DatabaseConnection());
    }

    public ProductController(DatabaseConnection dbConnection) {
        this.productDAO = new ProductDAO(dbConnection);
    }

    public boolean createProduct(Product product) {
        try {
            if (!validateProduct(product)) {
                return false;
            }
            boolean result = productDAO.create(product);
            if (result) {
                showSuccessMessage("Tạo sản phẩm thành công!");
            } else {
                showErrorMessage("Không thể tạo sản phẩm!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo sản phẩm: " + e.getMessage());
            return false;
        }
    }

    public Product getProductById(long id) {
        try {
            if (id <= 0) {
                showErrorMessage("Mã sản phẩm không hợp lệ!");
                return null;
            }
            Product product = productDAO.getById(id);
            if (product == null) {
                showInfoMessage("Không tìm thấy sản phẩm với mã: " + id);
            }
            return product;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try {
            List<Product> products = productDAO.getAll();
            if (products.isEmpty()) {
                showInfoMessage("Chưa có sản phẩm nào trong hệ thống!");
            }
            return products;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            if (!validateProduct(product)) {
                return false;
            }
            boolean result = productDAO.update(product);
            if (result) {
                showSuccessMessage("Cập nhật sản phẩm thành công!");
            } else {
                showErrorMessage("Không thể cập nhật sản phẩm!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(long id) {
        try {
            if (id <= 0) {
                showErrorMessage("Mã sản phẩm không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc chắn muốn xóa sản phẩm này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            boolean result = productDAO.delete(id);
            if (result) {
                showSuccessMessage("Xóa sản phẩm thành công!");
            } else {
                showErrorMessage("Không thể xóa sản phẩm!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }

    private boolean validateProduct(Product product) {
        if (product == null) {
            showErrorMessage("Thông tin sản phẩm không được để trống!");
            return false;
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            showErrorMessage("Tên sản phẩm không được để trống!");
            return false;
        }
        if (product.getCategoryId() <= 0) {
            showErrorMessage("Danh mục không hợp lệ!");
            return false;
        }
        if (product.getMaterialId() <= 0) {
            showErrorMessage("Chất liệu không hợp lệ!");
            return false;
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            showErrorMessage("Thương hiệu không được để trống!");
            return false;
        }
        if (product.getPrice() < 0) {
            showErrorMessage("Giá sản phẩm không được âm!");
            return false;
        }
        if (product.getSalePrice() < 0 || (product.getSalePrice() > product.getPrice() && product.getSalePrice() > 0)) {
            showErrorMessage("Giá khuyến mãi không hợp lệ!");
            return false;
        }
        if (product.getStockQuantity() < 0) {
            showErrorMessage("Số lượng tồn kho không được âm!");
            return false;
        }
        if (product.getMinPurchaseQuantity() < 0 || product.getMinPurchaseQuantity() > product.getMaxPurchaseQuantity()) {
            showErrorMessage("Số lượng mua tối thiểu không hợp lệ!");
            return false;
        }
        if (product.getMaxPurchaseQuantity() < 0) {
            showErrorMessage("Số lượng mua tối đa không hợp lệ!");
            return false;
        }
        return true;
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
    }
}