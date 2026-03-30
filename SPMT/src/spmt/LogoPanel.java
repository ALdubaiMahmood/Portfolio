package spmt;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
  Drawn entirely with Java2D - no image files needed.
 */
public class LogoPanel extends JPanel {

    private final int size;
    private static final Color NAVY   = new Color(30,  58,  95);
    private static final Color GOLD   = new Color(201, 150, 58);
    private static final Color GOLD2  = new Color(168, 120, 40);
    private static final Color WHITE  = Color.WHITE;

    public LogoPanel(int size) {
        this.size = size;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth drawing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        // Scale everything to the requested size (designed at 200x200)
        double scale = size / 200.0;
        g2.scale(scale, scale);

        draw(g2);
        g2.dispose();
    }

    private void draw(Graphics2D g) {

        // -- ARC OF 7 STARS across the top ------------------------
        float[] starAngles = { -148, -124, -101, -79, -57, -34, -11 };
        for (float angle : starAngles) {
            float rad = (float) Math.toRadians(angle);
            float sx = 100 + 78 * (float) Math.cos(rad);
            float sy = 108 + 78 * (float) Math.sin(rad);
            drawStar(g, sx, sy, 4.5f, 1.8f, GOLD);
        }

        // -- 4-POINT SPARKLES --------------------------------------
        drawSparkle(g, 44,  62, 5, GOLD);
        drawSparkle(g, 158, 58, 4, GOLD);

        // -- OPEN BOOK (navy pages) --------------------------------
        // Left page
        Path2D left = new Path2D.Float();
        left.moveTo(100, 160);
        left.curveTo(90, 150, 60, 142, 28, 148);
        left.curveTo(22, 140, 20, 118, 24, 108);
        left.curveTo(48, 102, 82, 108, 100, 130);
        left.closePath();
        g.setColor(NAVY);
        g.fill(left);

        // Right page
        Path2D right = new Path2D.Float();
        right.moveTo(100, 160);
        right.curveTo(110, 150, 140, 142, 172, 148);
        right.curveTo(178, 140, 180, 118, 176, 108);
        right.curveTo(152, 102, 118, 108, 100, 130);
        right.closePath();
        g.setColor(NAVY);
        g.fill(right);

        // Gold bottom binding strip
        Path2D binding = new Path2D.Float();
        binding.moveTo(28, 148);
        binding.quadTo(64, 157, 100, 163);
        binding.quadTo(136, 157, 172, 148);
        binding.quadTo(136, 159, 100, 167);
        binding.quadTo(64, 159, 28, 148);
        binding.closePath();
        g.setColor(GOLD);
        g.fill(binding);

        // Center spine line
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(100, 130, 100, 163);
        g.setStroke(new BasicStroke(1f));

        // -- LAUREL BRANCHES ---------------------------------------
        // Left stem curve
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawCurve(g, 100, 138, 86, 129, 70, 118, 50, 112);
        g.setStroke(new BasicStroke(1f));

        // Left leaves
        float[][] leftLeaves  = {{96,133,-30},{87,126,-42},{77,120,-53},{67,115,-63},{58,112,-70}};
        for (float[] lf : leftLeaves) drawLeaf(g, lf[0], lf[1], 10, 4.5f, lf[2]);

        // Right stem curve
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawCurve(g, 100, 138, 114, 129, 130, 118, 150, 112);
        g.setStroke(new BasicStroke(1f));

        // Right leaves (mirrored angles)
        float[][] rightLeaves = {{104,133,30},{113,126,42},{123,120,53},{133,115,63},{142,112,70}};
        for (float[] lf : rightLeaves) drawLeaf(g, lf[0], lf[1], 10, 4.5f, lf[2]);

        // -- MORTARBOARD CAP ---------------------------------------
        // Soft shadow under the cap
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(52, 88, 96, 12);

        // Flat board (diamond shape)
        int[] bx = {100, 158, 100, 42};
        int[] by = { 56,  78,  93, 78};
        g.setColor(NAVY);
        g.fillPolygon(bx, by, 4);

        // Top face of board (lighter shade)
        int[] tx = {100, 158, 100, 42};
        int[] ty = { 56,  78,  68, 78};
        g.setColor(new Color(42, 79, 124));
        g.fillPolygon(tx, ty, 4);

        // Gold brim band
        int[] gx = {100, 158, 148, 100,  52, 42};
        int[] gy = { 68,  78,  83,  72,  83, 78};
        g.setColor(GOLD);
        g.fillPolygon(gx, gy, 6);

        // Cap crown box
        g.setColor(NAVY);
        Path2D crown = new Path2D.Float();
        crown.moveTo(76, 90);
        crown.curveTo(76, 86, 80, 82, 100, 82);
        crown.curveTo(120, 82, 124, 86, 124, 90);
        crown.lineTo(124, 108);
        crown.curveTo(124, 112, 120, 116, 100, 116);
        crown.curveTo(80, 116, 76, 112, 76, 108);
        crown.closePath();
        g.fill(crown);

        // White button on top of board
        g.setColor(new Color(220, 220, 220));
        g.fillOval(96, 71, 8, 8);

        // Tassel cord from button to right
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawCurve(g, 100, 75, 120, 72, 135, 77, 140, 85);
        g.setStroke(new BasicStroke(1f));

        // Tassel knot
        g.setColor(GOLD);
        g.fillOval(137, 82, 7, 7);

        // Tassel strands hanging down
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(138, 89, 135, 100);
        g.drawLine(140, 89, 139, 101);
        g.drawLine(142, 89, 145, 100);
        g.setStroke(new BasicStroke(1f));

        // Tassel end fluff
        g.fillOval(135, 100, 11, 6);

        // -- DOTTED CIRCLE ARC (bottom decoration) ----------------
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1f, new float[]{2.5f, 4.0f}, 0f));
        g.drawArc(12, 23, 176, 176, 0, -180);
        g.setStroke(new BasicStroke(1f));
    }

    // -- Drawing helpers --------------------------------------------

    /** 5-point star */
    private void drawStar(Graphics2D g, float cx, float cy,
                          float rOuter, float rInner, Color c) {
        g.setColor(c);
        int n = 10;
        int[] px = new int[n];
        int[] py = new int[n];
        for (int i = 0; i < n; i++) {
            double a = Math.toRadians(-90 + i * 36);
            float r = (i % 2 == 0) ? rOuter : rInner;
            px[i] = Math.round(cx + r * (float) Math.cos(a));
            py[i] = Math.round(cy + r * (float) Math.sin(a));
        }
        g.fillPolygon(px, py, n);
    }

    /** 4-point sparkle / diamond star */
    private void drawSparkle(Graphics2D g, float cx, float cy, float r, Color c) {
        g.setColor(c);
        int n = 8;
        int[] px = new int[n];
        int[] py = new int[n];
        for (int i = 0; i < n; i++) {
            double a = Math.toRadians(i * 45);
            float rad = (i % 2 == 0) ? r : r * 0.25f;
            px[i] = Math.round(cx + rad * (float) Math.cos(a));
            py[i] = Math.round(cy + rad * (float) Math.sin(a));
        }
        g.fillPolygon(px, py, n);
    }

    /** Leaf as a rotated ellipse */
    private void drawLeaf(Graphics2D g, float cx, float cy,
                          float rx, float ry, float angleDeg) {
        g.setColor(GOLD);
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.rotate(Math.toRadians(angleDeg));
        g.fill(new Ellipse2D.Float(-rx, -ry, rx * 2, ry * 2));
        g.setTransform(old);
    }

    /** Cubic bezier curve helper */
    private void drawCurve(Graphics2D g,
                           float x1, float y1,
                           float cx1, float cy1,
                           float cx2, float cy2,
                           float x2, float y2) {
        Path2D p = new Path2D.Float();
        p.moveTo(x1, y1);
        p.curveTo(cx1, cy1, cx2, cy2, x2, y2);
        g.draw(p);
    }
}
