/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin.SidebarComponent;

/**
 *
 * @author Hi
 */
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends BasicScrollBarUI {
    private Color scrollThumbColor;
    private Color scrollTrackColor;
    private Color thumbHighlightColor;
    private int thumbRadius = 6;
    private int thumbMargin = 2;
    
    public CustomScrollBarUI() {
        this(new Color(203, 213, 225), new Color(241, 245, 249));
    }
    
    public CustomScrollBarUI(Color thumbColor, Color trackColor) {
        this.scrollThumbColor = thumbColor;
        this.scrollTrackColor = trackColor;
        this.thumbHighlightColor = new Color(148, 163, 184);
    }
    
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = scrollThumbColor;
        this.thumbHighlightColor = thumbHighlightColor;
        this.thumbLightShadowColor = scrollThumbColor;
        this.thumbDarkShadowColor = thumbHighlightColor;
        this.trackColor = scrollTrackColor;
        this.trackHighlightColor = scrollTrackColor;
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createInvisibleButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createInvisibleButton();
    }
    
    private JButton createInvisibleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setBorder(null);
        button.setFocusable(false);
        return button;
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Xác định màu thumb dựa trên trạng thái
        Color thumbColor = getThumbColor();
        g2.setColor(thumbColor);
        
        // Vẽ thumb với bo tròn
        int x = thumbBounds.x + thumbMargin;
        int y = thumbBounds.y + thumbMargin;
        int width = thumbBounds.width - (thumbMargin * 2);
        int height = thumbBounds.height - (thumbMargin * 2);
        
        g2.fillRoundRect(x, y, width, height, thumbRadius, thumbRadius);
        
        g2.dispose();
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(scrollTrackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        
        g2.dispose();
    }
    
    private Color getThumbColor() {
        if (isDragging) {
            return thumbHighlightColor;
        } else if (isThumbRollover()) {
            return thumbHighlightColor;
        } else {
            return scrollThumbColor;
        }
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(12, 48);
        } else {
            return new Dimension(48, 12);
        }
    }
    
    // Setter methods để có thể thay đổi màu sau khi tạo
    public void setScrollThumbColor(Color color) {
        this.scrollThumbColor = color;
        this.thumbColor = color;
        if (scrollbar != null) {
            scrollbar.repaint();
        }
    }
    
    public void setScrollTrackColor(Color color) {
        this.scrollTrackColor = color;
        this.trackColor = color;
        if (scrollbar != null) {
            scrollbar.repaint();
        }
    }
    
    public void setThumbHighlightColor(Color color) {
        this.thumbHighlightColor = color;
        if (scrollbar != null) {
            scrollbar.repaint();
        }
    }
    
    public void setThumbRadius(int radius) {
        this.thumbRadius = radius;
        if (scrollbar != null) {
            scrollbar.repaint();
        }
    }
    
    public void setThumbMargin(int margin) {
        this.thumbMargin = margin;
        if (scrollbar != null) {
            scrollbar.repaint();
        }
    }
    
    // Override để tắt các hiệu ứng không cần thiết
    @Override
    protected void installDefaults() {
        super.installDefaults();
        configureScrollBarColors();
    }
    
    @Override
    protected void paintDecreaseHighlight(Graphics g) {
        // Không vẽ gì để tạo giao diện sạch
    }
    
    @Override
    protected void paintIncreaseHighlight(Graphics g) {
        // Không vẽ gì để tạo giao diện sạch
    }
}
