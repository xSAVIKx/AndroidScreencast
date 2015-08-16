package com.github.xsavikx.android.screencast.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JPanelScreen extends JPanel {

  private static final long serialVersionUID = -2034873107028503004L;
  private float coef = 1;
  private double origX;
  private double origY;
  private boolean landscape;

  private Dimension size = null;
  private BufferedImage image = null;

  public JPanelScreen() {
    this.setFocusable(true);
  }

  public Point getRawPoint(Point p1) {
    Point p2 = new Point();
    p2.x = (int) ((p1.x - origX) / coef);
    p2.y = (int) ((p1.y - origY) / coef);
    return p2;
  }

  public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
    this.landscape = landscape;
    this.size = size;
    this.image = image;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (size == null)
      return;
    if (size.height == 0)
      return;
    Graphics2D g2 = (Graphics2D) g;
    g2.clearRect(0, 0, getWidth(), getHeight());
    double width = Math.min(getWidth(), size.width * getHeight() / size.height);
    coef = (float) width / size.width;
    double height = width * size.height / size.width;
    origX = (getWidth() - width) / 2;
    origY = (getHeight() - height) / 2;
    g2.drawImage(image, (int) origX, (int) origY, (int) width, (int) height, this);
  }

}
