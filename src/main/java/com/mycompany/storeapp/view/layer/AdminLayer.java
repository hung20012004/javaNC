/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.layer;

/**
 * Admin Layout sử dụng component architecture - Optimized Version
 * @author Manh Hung
 */
import com.mycompany.storeapp.view.component.admin.FooterComponent;
import com.mycompany.storeapp.view.component.admin.HeaderComponent;
import com.mycompany.storeapp.view.component.admin.Sidebar;
import com.mycompany.storeapp.view.page.admin.Category.CategoryGUI;
import com.mycompany.storeapp.view.page.admin.Order.OrderKanbanView;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class AdminLayer extends JFrame {
    // Components
    private HeaderComponent headerComponent;
    private Sidebar sidebarComponent;
    private FooterComponent footerComponent;
    private JPanel mainContentPanel;
    private JPanel currentContentPanel;
    
    // UI Colors
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CONTENT_BACKGROUND = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    // Content cache để tránh tạo lại nhiều lần
    private Map<String, JPanel> contentCache = new HashMap<>();
    
    public AdminLayer() {
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showDashboard(); // Hiển thị trang mặc định
    }
    
    private void initializeFrame() {
        setTitle("Hệ thống quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set icon nếu có
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("https://res.cloudinary.com/deczn9jtq/image/upload/v1751535499/logo_nhtaxb.png"));
        } catch (Exception e) {
            // Icon không tồn tại, bỏ qua
        }
    }
    
    private void initializeComponents() {
        // Initialize header component với constructor mới
        headerComponent = new HeaderComponent();
        
        // Thiết lập logout action cho header
        headerComponent.addLogoutActionListener(e -> handleLogout());
        
        // Initialize sidebar component
        sidebarComponent = new Sidebar();
        
        // Thiết lập menu action listener cho sidebar
        sidebarComponent.setMenuActionListener(this::handleMenuAction);
        
        // Initialize footer component
        footerComponent = new FooterComponent();
        
        // Initialize main content panel
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        currentContentPanel = new JPanel(new BorderLayout());
        currentContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.add(currentContentPanel, BorderLayout.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add components to frame
        add(headerComponent, BorderLayout.NORTH);
        add(sidebarComponent, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
        add(footerComponent, BorderLayout.SOUTH);
        
        // Set background
        getContentPane().setBackground(BACKGROUND_COLOR);
    }
    
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        
        // Component resize listener for responsive design
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleWindowResize();
            }
        });
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        // Alt + Q để logout
        KeyStroke logoutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(logoutKeyStroke, "logout");
        getRootPane().getActionMap().put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        // F11 để toggle fullscreen
        KeyStroke fullscreenKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(fullscreenKeyStroke, "fullscreen");
        getRootPane().getActionMap().put("fullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });
        // Alt + S để toggle sidebar
        KeyStroke sidebarToggleKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(sidebarToggleKeyStroke, "toggleSidebar");
        getRootPane().getActionMap().put("toggleSidebar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebarComponent.setCollapsed(!sidebarComponent.isCollapsed());
            }
        });
    }
    
    private void handleWindowClosing() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn thoát khỏi hệ thống?",
            "Xác nhận thoát",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            cleanup();
            dispose();
            System.exit(0);
        }
    }
    
    private void handleWindowResize() {
        // Handle responsive design changes
        Dimension size = getSize();
        
        // Responsive sidebar (nếu SidebarComponent hỗ trợ)
        if (size.width < 1200) {
            sidebarComponent.setCollapsed(true);
        } else {
            sidebarComponent.setCollapsed(false);
        }
        
        // Responsive content padding
        EmptyBorder padding;
        if (size.width < 800) {
            padding = new EmptyBorder(10, 10, 10, 10);
        } else if (size.width < 1200) {
            padding = new EmptyBorder(15, 15, 15, 15);
        } else {
            padding = new EmptyBorder(20, 20, 20, 20);
        }
        mainContentPanel.setBorder(padding);
    }
    
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Clear session data
            cleanup();
            dispose();
            
            // Show login form
            SwingUtilities.invokeLater(() -> {
                // new LoginForm().setVisible(true);
                System.out.println("Redirecting to login...");
            });
        }
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
            case "dashboard":
                return createContentPanel("Dashboard", "Tổng quan về hoạt động của cửa hàng", "📊");
            case "notifications":
                return createContentPanel("Thông báo", "Quản lý thông báo hệ thống", "🔔");
            case "products":
                return createContentPanel("Quản lý sản phẩm", "Thêm, sửa, xóa và quản lý sản phẩm", "📦");
            case "categories":
                return new CategoryGUI();
            case "suppliers":
                return createContentPanel("Quản lý nhà cung cấp", "Thông tin các nhà cung cấp", "🚚");
            case "inventory-checks":
                return createContentPanel("Kiểm kho", "Kiểm tra và quản lý tồn kho", "📋");
            case "sizes":
                return createContentPanel("Quản lý kích thước", "Quản lý các kích thước sản phẩm", "📐");
            case "colors":
                return createContentPanel("Quản lý màu sắc", "Quản lý bảng màu sản phẩm", "🎨");
            case "materials":
                return createContentPanel("Quản lý chất liệu", "Quản lý chất liệu sản phẩm", "🧵");
            case "tags":
                return createContentPanel("Quản lý Tag", "Quản lý thẻ cho sản phẩm", "🏷️");
            case "orders":
                return new OrderKanbanView();
            case "order-warehouse":
                return createContentPanel("Đóng hàng", "Quản lý việc đóng gói đơn hàng", "📦");
            case "order-shipping":
                return createContentPanel("Vận chuyển", "Theo dõi vận chuyển đơn hàng", "🚛");
            case "purchase-orders":
                return createContentPanel("Đơn nhập hàng", "Quản lý đơn hàng nhập từ nhà cung cấp", "🛒");
            case "payment":
                return createContentPanel("Danh sách thanh toán", "Quản lý các giao dịch thanh toán", "💳");
            case "reconcile-vnpay":
                return createContentPanel("Đối soát VNPay", "Đối soát giao dịch với VNPay", "🔄");
            case "payment-report":
                return createContentPanel("Báo cáo thanh toán", "Thống kê và báo cáo thanh toán", "📊");
            case "customers":
                return createContentPanel("Quản lý khách hàng", "Thông tin và lịch sử khách hàng", "👥");
            case "support-requests":
                return createContentPanel("Yêu cầu hỗ trợ", "Xử lý yêu cầu hỗ trợ từ khách hàng", "🎧");
            case "reviews":
                return createContentPanel("Quản lý đánh giá", "Quản lý đánh giá và nhận xét", "⭐");
            case "banners":
                return createContentPanel("Banner khuyến mãi", "Quản lý banner và chương trình khuyến mãi", "🎯");
            case "settings":
                return createContentPanel("Cài đặt hệ thống", "Cấu hình các thiết lập hệ thống", "⚙️");
            case "staffs":
                return createContentPanel("Quản lý nhân viên", "Quản lý thông tin nhân viên", "👤");
            default:
                return createContentPanel("Dashboard", "Tổng quan về hoạt động của cửa hàng", "📊");
        }
    }
    
    private void showErrorContent(String errorMessage) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(CONTENT_BACKGROUND);
        
        JLabel errorLabel = new JLabel("<html><center>" +
                "❌ " + errorMessage + "<br><br>" +
                "<span style='color: #9CA3AF; font-size: 12px;'>Vui lòng thử lại sau</span>" +
                "</center></html>", JLabel.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setBorder(new EmptyBorder(40, 20, 40, 20));
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        updateMainContent(errorPanel);
    }
    
    public void updateMainContent(JPanel newContent) {
        currentContentPanel.removeAll();
        currentContentPanel.add(newContent, BorderLayout.CENTER);
        currentContentPanel.revalidate();
        currentContentPanel.repaint();
        
        animateContentTransition();
    }
    
    private void animateContentTransition() {
        final int animationDuration = 200;
        final int steps = 10;
        final int stepDelay = animationDuration / steps;

        Timer timer = new Timer(stepDelay, new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                // Add your animation logic here
                if (step >= steps) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Tạo content panel với title và description
     */
    private JPanel createContentPanel(String title, String description, String icon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BACKGROUND);

        JPanel headerSection = createContentHeader(title, description, icon);
        panel.add(headerSection, BorderLayout.NORTH);

        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CONTENT_BACKGROUND);
        contentArea.setBorder(new EmptyBorder(30, 0, 0, 0));

        JPanel placeholderPanel = createPlaceholderContent(title);
        contentArea.add(placeholderPanel, BorderLayout.CENTER);
        
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContentHeader(String title, String description, String icon) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CONTENT_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(CONTENT_BACKGROUND);
        
        if (icon != null && !icon.isEmpty()) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            iconLabel.setSize(20,20);
            iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
            titlePanel.add(iconLabel);
        }
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(17, 24, 39));
        titlePanel.add(titleLabel);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        headerPanel.add(titlePanel);
        headerPanel.add(descLabel);
        
        return headerPanel;
    }
    
    private JPanel createPlaceholderContent(String pageName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BACKGROUND);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JLabel label = new JLabel("<html><center>" +
                "📋 Nội dung trang " + pageName + " sẽ được phát triển ở đây<br><br>" +
                "<span style='color: #9CA3AF; font-size: 12px;'>Tích hợp với các form và component cụ thể</span>" +
                "</center></html>", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(156, 163, 175));
        label.setBorder(new EmptyBorder(40, 20, 40, 20));
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private void showDashboard() {
        handleMenuAction("dashboard");
    }
    
    private void toggleFullscreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
    
    private void cleanup() {
        contentCache.clear();
        
        System.gc();
    }

    public void setUserInfo(String username) {
        headerComponent.setUserInfo(username);
    }
    
    public void setCopyrightText(String text) {
        footerComponent.setCopyrightText(text);
    }
    
    public void setVersionText(String version) {
        footerComponent.setVersionText(version);
    }
    
    public void navigateToPage(String action) {
        handleMenuAction(action);
    }
    
    public void refreshCurrentPage() {
        // Clear cache for current page and reload
        // Implementation depends on how you track current page
    }
    
    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }
    
    public Sidebar getSidebarComponent() {
        return sidebarComponent;
    }
    
    public FooterComponent getFooterComponent() {
        return footerComponent;
    }
    
    public JPanel getMainContentPanel() {
        return mainContentPanel;
    }
}