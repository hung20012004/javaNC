/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin.SidebarComponent;

/**
 *
 * @author Hi
 */
import java.util.Arrays;
import java.util.List;

public class MenuDataProvider {
    
    public static class MenuItemData {
        public final String icon;
        public final String text;
        public final String action;
        
        public MenuItemData(String icon, String text, String action) {
            this.icon = icon;
            this.text = text;
            this.action = action;
        }
    }
    
    public static class MenuGroupData {
        public final String title;
        public final List<MenuItemData> items;
        
        public MenuGroupData(String title, MenuItemData... items) {
            this.title = title;
            this.items = Arrays.asList(items);
        }
    }
    
    /**
     * Lấy dữ liệu menu mặc định cho admin
     */
    public static List<MenuGroupData> getDefaultAdminMenuData() {
        return Arrays.asList(
            // Tổng quan
            new MenuGroupData("Tổng quan",
                new MenuItemData("📊", "Dashboard", "dashboard")
            ),
            
            // Quản lý sản phẩm
            new MenuGroupData("Quản lý sản phẩm",
                new MenuItemData("📦", "Sản phẩm", "products"),
                new MenuItemData("🏷️", "Danh mục", "categories"),
                new MenuItemData("🚚", "Nhà cung cấp", "suppliers"),
                new MenuItemData("📐", "Kích thước", "sizes"),
                new MenuItemData("🎨", "Màu sắc", "colors"),
                new MenuItemData("🧵", "Chất liệu", "materials"),
                new MenuItemData("🏷️", "Tag", "tags")
            ),
            
            // Quản lý đơn hàng
            new MenuGroupData("Quản lý đơn hàng",
                new MenuItemData("🛍️", "Đơn hàng", "orders"),
                new MenuItemData("🛒", "Đơn nhập hàng", "purchase-orders")
            ),
            
            // Quản lý khách hàng
            new MenuGroupData("Quản lý khách hàng",
                new MenuItemData("👥", "Khách hàng", "customers")
            ),
            
            // Marketing
            new MenuGroupData("Marketing",
                new MenuItemData("🎁", "Khuyến mãi", "promotions")
            ),
            
            // Cài đặt hệ thống
            new MenuGroupData("Cài đặt hệ thống",
                new MenuItemData("👤", "Quản lý nhân viên", "staffs"),
                new MenuItemData("🖊️", "Quản lý chức vụ", "roles")
            )
        );
    }
    
    /**
     * Lấy dữ liệu menu cho user (nếu cần)
     */
    public static List<MenuGroupData> getDefaultUserMenuData() {
        return Arrays.asList(
            new MenuGroupData("Tài khoản",
                new MenuItemData("👤", "Thông tin cá nhân", "profile"),
                new MenuItemData("🛍️", "Đơn hàng của tôi", "my-orders"),
                new MenuItemData("❤️", "Sản phẩm yêu thích", "favorites")
            ),
            
            new MenuGroupData("Hỗ trợ",
                new MenuItemData("💬", "Liên hệ", "contact"),
                new MenuItemData("❓", "Câu hỏi thường gặp", "faq")
            )
        );
    }
    
    /**
     * Tìm menu item theo action
     */
    public static MenuItemData findMenuItemByAction(List<MenuGroupData> menuData, String action) {
        return menuData.stream()
                .flatMap(group -> group.items.stream())
                .filter(item -> action.equals(item.action))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Tìm menu group chứa action
     */
    public static MenuGroupData findGroupContainingAction(List<MenuGroupData> menuData, String action) {
        return menuData.stream()
                .filter(group -> group.items.stream().anyMatch(item -> action.equals(item.action)))
                .findFirst()
                .orElse(null);
    }
}
