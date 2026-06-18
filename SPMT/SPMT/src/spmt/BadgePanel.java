package spmt;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * BADGE PANEL:
 * Draws a custom shield/emblem badge using Java2D — no image files needed.
 * Badge tier is determined by the student's points:
 *   90-94 → HONOR ROLL (blue)
 *   95-99 → ELITE      (purple)
 *   100   → LEGEND     (gold)
 */
public class BadgePanel extends JPanel {

    public enum Tier { HONOR_ROLL, ELITE, LEGEND }

    private final int  size;
    private final Tier tier;

    // Tier colour palettes
    private static final Color[] BLUE_PAL   = {
        new Color(93, 122, 255),   // main
        new Color(50,  80, 200),   // dark
        new Color(180, 200, 255),  // light / highlight
        new Color(220, 230, 255)   // text / star
    };
    private static final Color[] PURPLE_PAL = {
        new Color(180, 120, 255),
        new Color(110,  60, 200),
        new Color(220, 190, 255),
        new Color(240, 230, 255)
    };
    private static final Color[] GOLD_PAL   = {
        new Color(201, 150,  58),
        new Color(140,  90,  20),
        new Color(255, 215, 100),
        new Color(255, 245, 200)
    };

    public BadgePanel(int size, Tier tier) {
        this.size = size;
        this.tier = tier;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
    }

    /** Convenience: build from a student's badge string */
    public static BadgePanel forStudent(Student s, int size) {
        if (!s.hasBadge()) return null;
        Tier t;
        switch (s.getBadge()) {
            case "LEGEND":    t = Tier.LEGEND;    break;
            case "ELITE":     t = Tier.ELITE;     break;
            default:          t = Tier.HONOR_ROLL; break;
        }
        return new BadgePanel(size, t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        // Designed at 120x120, scale to requested size
        double sc = size / 120.0;
        g2.scale(sc, sc);
        draw(g2);
        g2.dispose();
    }

    private void draw(Graphics2D g) {
        Color[] pal = palette();
        Color main  = pal[0];
        Color dark  = pal[1];
        Color light = pal[2];
        Color white = pal[3];

        int cx = 60, cy = 58;

        //  Outer glow / shadow 
        g.setColor(new Color(main.getRed(), main.getGreen(), main.getBlue(), 40));
        g.fillOval(10, 10, 100, 100);

        // Shield body 
        Path2D shield = shield(cx, cy - 2, 46, 52);

        // Drop shadow
        g.setColor(new Color(0, 0, 0, 60));
        Graphics2D gs = (Graphics2D) g.create();
        gs.translate(3, 4);
        gs.fill(shield);
        gs.dispose();

        // Gradient fill: dark at top → main in middle → dark at bottom
        GradientPaint gp = new GradientPaint(cx, cy - 54, dark, cx, cy + 54, main);
        g.setPaint(gp);
        g.fill(shield);

        // Inner shield (inset highlight band at top)
        Path2D inner = shield(cx, cy - 2, 38, 44);
        g.setColor(new Color(255, 255, 255, 18));
        g.fill(inner);

        // Shield border
        g.setColor(light);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(shield);
        g.setStroke(new BasicStroke(1f));

        //  SPMT initials 
        g.setColor(white);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        String initials = "SPMT";
        int tw = fm.stringWidth(initials);
        g.drawString(initials, cx - tw / 2, cy + 6);

        //  Thin divider line below text 
        g.setColor(new Color(255, 255, 255, 80));
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(cx - 20, cy + 12, cx + 20, cy + 12);

        // Tier label below divider 
        g.setColor(light);
        g.setFont(new Font("Segoe UI", Font.BOLD, 7));
        fm = g.getFontMetrics();
        String label = tierLabel();
        tw = fm.stringWidth(label);
        g.drawString(label, cx - tw / 2, cy + 22);

        // Stars across the top of the shield 
        int numStars = starCount();
        float starSpacing = 14f;
        float startX = cx - ((numStars - 1) * starSpacing) / 2f;
        for (int i = 0; i < numStars; i++) {
            drawStar(g, startX + i * starSpacing, cy - 33, 4.5f, 1.8f, white);
        }

        //  Bottom ribbon 
        Path2D ribbon = new Path2D.Float();
        ribbon.moveTo(cx - 28, cy + 40);
        ribbon.curveTo(cx - 28, cy + 48, cx - 14, cy + 50, cx, cy + 50);
        ribbon.curveTo(cx + 14, cy + 50, cx + 28, cy + 48, cx + 28, cy + 40);
        ribbon.lineTo(cx + 22, cy + 36);
        ribbon.lineTo(cx, cy + 38);
        ribbon.lineTo(cx - 22, cy + 36);
        ribbon.closePath();
        g.setColor(dark);
        g.fill(ribbon);
        g.setColor(light);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(ribbon);
        g.setStroke(new BasicStroke(1f));

        // Points label on ribbon
        g.setColor(white);
        g.setFont(new Font("Segoe UI", Font.BOLD, 7));
        fm = g.getFontMetrics();
        String pts = pointsLabel();
        tw = fm.stringWidth(pts);
        g.drawString(pts, cx - tw / 2, cy + 46);
    }

    //  Shield shape (rounded bottom, flat top with notch) 
    private Path2D shield(float cx, float cy, float hw, float hh) {
        Path2D p = new Path2D.Float();
        float t  = cy - hh;       // top y
        float b  = cy + hh;       // bottom tip y
        float l  = cx - hw;       // left x
        float r  = cx + hw;       // right x
        float notch = 8;          // top-center notch depth

        p.moveTo(l, t);
        p.lineTo(cx - notch, t);
        p.curveTo(cx - notch / 2, t + notch, cx + notch / 2, t + notch, cx + notch, t);
        p.lineTo(r, t);
        p.curveTo(r + 4, t, r + 6, t + 6, r + 6, t + 14);
        p.lineTo(r + 6, cy + 10);
        p.curveTo(r + 6, cy + hh * 0.7f, cx + hw * 0.4f, b - 8, cx, b);
        p.curveTo(cx - hw * 0.4f, b - 8, l - 6, cy + hh * 0.7f, l - 6, cy + 10);
        p.lineTo(l - 6, t + 14);
        p.curveTo(l - 6, t + 6, l - 4, t, l, t);
        p.closePath();
        return p;
    }

    private void drawStar(Graphics2D g, float cx, float cy,
                          float rOuter, float rInner, Color c) {
        g.setColor(c);
        int n = 10;
        int[] px = new int[n], py = new int[n];
        for (int i = 0; i < n; i++) {
            double a = Math.toRadians(-90 + i * 36);
            float  r = (i % 2 == 0) ? rOuter : rInner;
            px[i] = Math.round(cx + r * (float) Math.cos(a));
            py[i] = Math.round(cy + r * (float) Math.sin(a));
        }
        g.fillPolygon(px, py, n);
    }

    private Color[] palette() {
        switch (tier) {
            case ELITE:  return PURPLE_PAL;
            case LEGEND: return GOLD_PAL;
            default:     return BLUE_PAL;
        }
    }

    private String tierLabel() {
        switch (tier) {
            case ELITE:  return "ELITE";
            case LEGEND: return "LEGEND";
            default:     return "HONOR ROLL";
        }
    }

    private String pointsLabel() {
        switch (tier) {
            case ELITE:  return "95 – 99 PTS";
            case LEGEND: return "100 PTS";
            default:     return "90 – 94 PTS";
        }
    }

    private int starCount() {
        switch (tier) {
            case ELITE:  return 2;
            case LEGEND: return 3;
            default:     return 1;
        }
    }
}
