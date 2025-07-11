package com.mycompany.storeapp.view.layer;

import com.mycompany.storeapp.view.component.admin.FooterComponent;
import com.mycompany.storeapp.view.component.admin.HeaderComponent;
import com.mycompany.storeapp.view.component.admin.Sidebar;
import com.mycompany.storeapp.view.page.admin.Banner.BannerGUI;
import com.mycompany.storeapp.view.page.admin.Category.CategoryGUI;
import com.mycompany.storeapp.view.page.admin.Color.ColorGUI;
import com.mycompany.storeapp.view.page.admin.Customer.CustomerGUI;
import com.mycompany.storeapp.view.page.admin.Material.MaterialGUI;
import com.mycompany.storeapp.view.page.admin.Size.SizeGUI;
import com.mycompany.storeapp.view.page.admin.Supplier.SupplierGUI;
import com.mycompany.storeapp.view.page.admin.Order.OrderKanbanView;
import com.mycompany.storeapp.view.page.admin.Product.ProductGUI;
import com.mycompany.storeapp.view.page.admin.PurchaseOrder.POKanBanView;
import com.mycompany.storeapp.view.page.admin.Report.ReportPanel;
import com.mycompany.storeapp.view.page.admin.Role.RoleGUI;
import com.mycompany.storeapp.view.page.admin.Tag.TagGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class AdminLayer extends JFrame {
    private HeaderComponent headerComponent;
    private Sidebar sidebarComponent;
    private FooterComponent footerComponent;
    private JPanel mainContentPanel;
    private JPanel currentContentPanel;
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CONTENT_BACKGROUND = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private Map<String, JPanel> contentCache = new HashMap<>();
    private LayerManager layerManager;

    public AdminLayer(LayerManager layerManager) {
        this.layerManager = layerManager;
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showDashboard();
    }

    private void initializeFrame() {
        setTitle("Hệ thống quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icons/shop.png"));
        } catch (Exception e) {
        }
    }

    private void initializeComponents() {
        headerComponent = new HeaderComponent();
        headerComponent.addLogoutActionListener(e -> handleLogout());
        sidebarComponent = new Sidebar();
        sidebarComponent.setMenuActionListener(this::handleMenuAction);
        footerComponent = new FooterComponent();
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        currentContentPanel = new JPanel(new BorderLayout());
        currentContentPanel.setBackground(CONTENT_BACKGROUND);
        mainContentPanel.add(currentContentPanel, BorderLayout.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(headerComponent, BorderLayout.NORTH);
        add(sidebarComponent, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
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
        KeyStroke logoutKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(logoutKeyStroke, "logout");
        getRootPane().getActionMap().put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
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
            cleanup();
            layerManager.logout();
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
                return new ReportPanel();
            case "products":
                return new ProductGUI();
            case "categories":
                return new CategoryGUI();
            case "suppliers":
                return new SupplierGUI();
            case "sizes":
                return new SizeGUI();
            case "colors":
                return new ColorGUI();
            case "materials":
                 return new MaterialGUI();
            case "tags":
                return new TagGUI();
            case "orders":
                return new OrderKanbanView();
            case "purchase-orders":
                return new POKanBanView();
            case "customers":
                return new CustomerGUI();
            case "banners":
                return new BannerGUI();
            case "staffs":
                return createContentPanel("Quản lý nhân viên", "Quản lý thông tin nhân viên", "👤");
            case "roles":
                return new RoleGUI();
            default:
                return new ReportPanel();
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
                if (step >= steps) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

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