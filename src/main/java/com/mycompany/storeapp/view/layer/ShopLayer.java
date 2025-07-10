package com.mycompany.storeapp.view.layer;

import com.mycompany.storeapp.view.component.shop.ShopHeaderComponent;
import com.mycompany.storeapp.view.component.shop.ShopNavbarComponent;
import com.mycompany.storeapp.view.component.shop.ShopFooterComponent;
import com.mycompany.storeapp.view.component.shop.cart.CartComponent;
import com.mycompany.storeapp.view.component.shop.POSComponent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ShopLayer extends JFrame {
    private ShopHeaderComponent headerComponent;
    private ShopNavbarComponent navbarComponent;
    private ShopFooterComponent footerComponent;
    private CartComponent cartComponent;
    private JPanel mainContentPanel;
    private JPanel currentContentPanel;
    private JSplitPane mainSplitPane;
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color SECONDARY_COLOR = new java.awt.Color(16, 185, 129);
    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(249, 250, 251);
    private static final java.awt.Color CONTENT_BACKGROUND = java.awt.Color.WHITE;
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(229, 231, 235);
    private static final java.awt.Color DANGER_COLOR = new java.awt.Color(239, 68, 68);
    private static final java.awt.Color WARNING_COLOR = new java.awt.Color(245, 158, 11);
    private Map<String, JPanel> contentCache = new HashMap<>();
    private boolean isCartVisible = true;
    private LayerManager layerManager;

    public ShopLayer(LayerManager layerManager) {
        this.layerManager = layerManager;
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showPOS();
    }

    private void initializeFrame() {
        setTitle("Hệ thống bán hàng - POS System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icons/shop.png"));
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        headerComponent = new ShopHeaderComponent();
        headerComponent.addLogoutActionListener(e -> handleLogout());
        headerComponent.addToggleCartActionListener(e -> toggleCart());
        navbarComponent = new ShopNavbarComponent();
        navbarComponent.setMenuActionListener(e -> handleMenuAction(e.getActionCommand()));
        cartComponent = new CartComponent();
        cartComponent.setPreferredSize(new Dimension(250, 0));
        cartComponent.setMinimumSize(new Dimension(200, 0));
        footerComponent = new ShopFooterComponent();
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        currentContentPanel = new JPanel(new BorderLayout());
        currentContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.add(currentContentPanel, BorderLayout.CENTER);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainContentPanel, cartComponent);
        mainSplitPane.setResizeWeight(0.75);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setBorder(null);
        mainSplitPane.setDividerSize(8);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerComponent, BorderLayout.NORTH);
        topPanel.add(navbarComponent, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        add(footerComponent, BorderLayout.SOUTH);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleWindowResize();
            }
        });
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        addKeyboardShortcut(KeyEvent.VK_F1, 0, "pos", e -> handleMenuAction("pos"));
        addKeyboardShortcut(KeyEvent.VK_F2, 0, "toggle_cart", e -> toggleCart());
        addKeyboardShortcut(KeyEvent.VK_F3, 0, "new_order", e -> handleNewOrder());
        addKeyboardShortcut(KeyEvent.VK_F4, 0, "payment", e -> handlePayment());
        addKeyboardShortcut(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK, "logout", e -> handleLogout());
    }

    private void addKeyboardShortcut(int keyCode, int modifiers, String actionKey, ActionListener action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        getRootPane().getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }

    private void handleWindowClosing() {
        if (cartComponent.hasItems()) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Giỏ hàng hiện có sản phẩm. Bạn có chắc chắn muốn thoát?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        cleanup();
        dispose();
        System.exit(0);
    }

    private void handleWindowResize() {
        Dimension size = getSize();
        if (size.width < 1000 && isCartVisible) {
            hideCart();
        } else if (size.width >= 1000 && !isCartVisible) {
            showCart();
        }
        EmptyBorder padding = size.width < 800 ? new EmptyBorder(8, 8, 8, 8) : new EmptyBorder(15, 15, 15, 15);
        mainContentPanel.setBorder(padding);
    }

    private void handleLogout() {
        if (cartComponent.hasItems()) {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Giỏ hàng hiện có sản phẩm. Bạn có muốn lưu đơn hàng trước khi đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (option == JOptionPane.CANCEL_OPTION) return;
            if (option == JOptionPane.YES_OPTION) handleSaveOrder();
        }
        cleanup();
        layerManager.logout();
    }

    private void handleMenuAction(String action) {
        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                return getOrCreateContent(action);
            }
            @Override
            protected void done() {
                try {
                    JPanel content = get();
                    updateMainContent(content);
                    navbarComponent.setActiveItem(action);
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorContent("Lỗi khi tải nội dung: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel getOrCreateContent(String action) {
        if (contentCache.containsKey(action)) {
            return contentCache.get(action);
        }
        JPanel content = createContentForAction(action);
        contentCache.put(action, content);
        return content;
    }

    private JPanel createContentForAction(String action) {
        switch (action) {
            case "pos":
                return new POSComponent(this, cartComponent);
            case "products":
                return createSimpleContentPanel("Danh sách sản phẩm", "Quản lý và tìm kiếm sản phẩm", "🛍️");
            case "orders":
                return createSimpleContentPanel("Đơn hàng", "Quản lý đơn hàng", "📋");
            case "customers":
                return createSimpleContentPanel("Khách hàng", "Thông tin khách hàng", "👥");
            case "reports":
                return createSimpleContentPanel("Báo cáo", "Thống kê bán hàng", "📊");
            case "settings":
                return createSimpleContentPanel("Cài đặt", "Cấu hình hệ thống", "⚙️");
            default:
                return new POSComponent(this, cartComponent);
        }
    }

    private JPanel createSimpleContentPanel(String title, String description, String icon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BACKGROUND);
        JLabel label = new JLabel("<html><center>" +
                icon + "<br><br>" +
                "<h2 style='color: #6B7280;'>" + description + "</h2>" +
                "<br><p style='color: #9CA3AF; font-size: 12px;'>Nội dung sẽ được phát triển</p>" +
                "</center></html>", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void showErrorContent(String errorMessage) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(CONTENT_BACKGROUND);
        JLabel errorLabel = new JLabel("<html><center>" +
                "❌ " + errorMessage + "<br><br>" +
                "<span style='color: #9CA3AF; font-size: 12px;'>Vui lòng thử lại sau</span>" +
                "</center></html>", JLabel.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(DANGER_COLOR);
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        updateMainContent(errorPanel);
    }

    public void updateMainContent(JPanel newContent) {
        currentContentPanel.removeAll();
        currentContentPanel.add(newContent, BorderLayout.CENTER);
        currentContentPanel.revalidate();
        currentContentPanel.repaint();
    }

    public void toggleCart() {
        if (isCartVisible) {
            hideCart();
        } else {
            showCart();
        }
    }

    public void showCart() {
        mainSplitPane.setRightComponent(cartComponent);
        mainSplitPane.setDividerLocation(mainSplitPane.getSize().width - 250);
        isCartVisible = true;
    }

    public void hideCart() {
        mainSplitPane.setRightComponent(null);
        isCartVisible = false;
    }

    public void addToCart(int variantId, int quantity) {
        cartComponent.addItem(variantId, quantity);
    }

    private void showPOS() {
        handleMenuAction("pos");
    }

    private void handleNewOrder() {
        cartComponent.clearCart();
        JOptionPane.showMessageDialog(this, "Đã tạo đơn hàng mới!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSaveOrder() {
        if (!cartComponent.hasItems()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Đã lưu đơn hàng!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handlePayment() {
        if (!cartComponent.hasItems()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận thanh toán đơn hàng?",
            "Thanh toán",
            JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            cartComponent.clearCart();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void focusSearch() {
        System.out.println("Focus search bar");
    }

    private void cleanup() {
        contentCache.clear();
        System.gc();
    }

    public ShopHeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    public ShopNavbarComponent getNavbarComponent() {
        return navbarComponent;
    }

    public ShopFooterComponent getFooterComponent() {
        return footerComponent;
    }

    public CartComponent getCartComponent() {
        return cartComponent;
    }
}