package com.github.xsavikx.androidscreencast.ui;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class JPanelScreen extends JPanel {
    private static final long serialVersionUID = -2034873107028503004L;
    private float coef = 1;
    private double origX;
    private double origY;
    private Dimension size = null;
    private BufferedImage image = null;

    @Inject
    public JPanelScreen() {
        setFocusable(true);
    }

    public Point getRawPoint(final Point p1) {
        final Point p2 = new Point();
        p2.x = (int) ((p1.x - origX) / coef);
        p2.y = (int) ((p1.y - origY) / coef);
        return p2;
    }

    public void handleNewImage(final Dimension size, final BufferedImage image) {
        this.size = size;
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (isNotInitialized())
            return;
        final Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, getWidth(), getHeight());
        final double width = Math.min(getWidth(), size.width * getHeight() / size.height);
        coef = (float) width / size.width;
        final double height = width * size.height / size.width;
        origX = (getWidth() - width) / 2;
        origY = (getHeight() - height) / 2;
        g2.drawImage(image, (int) origX, (int) origY, (int) width, (int) height, this);
    }

    private boolean isNotInitialized() {
        return size == null || size.height == 0 || size.width == 0;
    }

}
