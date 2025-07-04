package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.model.entity.Category;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.border.EmptyBorder;

public class CategoryListComponent extends JPanel {
    private List<Category> categories;
    private JPanel categoryPanel;
    private String selectedCategory;
    private Consumer<String> categorySelectionListener;

    public CategoryListComponent(List<Category> categories, Consumer<String> categorySelectionListener) {
        this.categories = categories;
        this.categorySelectionListener = categorySelectionListener;
        this.selectedCategory = "all";
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 10));
        setPreferredSize(new Dimension(200, 0));
        initializeComponents();
    }

    private void initializeComponents() {
        JLabel categoryLabel = new JLabel("Danh mục sản phẩm");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        categoryLabel.setForeground(new Color(55, 65, 81));
        categoryLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(Color.WHITE);

        refreshCategoryButtons();

        JScrollPane categoryScrollPane = new JScrollPane(categoryPanel);
        categoryScrollPane.setBorder(null);
        categoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        categoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(categoryLabel, BorderLayout.NORTH);
        add(categoryScrollPane, BorderLayout.CENTER);
    }

    private void refreshCategoryButtons() {
        categoryPanel.removeAll();
        for (Category category : categories) {
            JButton categoryButton = createCategoryButton(category);
            categoryPanel.add(categoryButton);
            categoryPanel.add(Box.createVerticalStrut(8));
        }
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private JButton createCategoryButton(Category category) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(category.getSlug().equals(selectedCategory) ? new Color(59, 130, 246) : Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(12, 15, 12, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 50));
        button.setMaximumSize(new Dimension(170, 50));

        JLabel iconLabel = new JLabel(category.getSlug().equals("all") ? "📦" : "📁");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel nameLabel = new JLabel(category.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(category.getSlug().equals(selectedCategory) ? Color.WHITE : new Color(55, 65, 81));

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        textPanel.setOpaque(false);
        textPanel.add(iconLabel);
        textPanel.add(nameLabel);

        button.add(textPanel, BorderLayout.CENTER);

        button.addActionListener(e -> {
            selectedCategory = category.getSlug();
            categorySelectionListener.accept(selectedCategory);
            refreshCategoryButtons();
        });

        return button;
    }
}