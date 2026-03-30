package spmt;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame app) {
        setBackground(new Color(30, 30, 40));
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(8, 0, 8, 0);

        // Logo - drawn with Java2D, no image file needed
        LogoPanel logo = new LogoPanel(140);
        add(logo, g);

        // App title
        JLabel title = new JLabel("Student Performance & Motivation Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        add(title, g);

        // Tagline
        JLabel sub = new JLabel("Track . Motivate . Elevate");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(140, 148, 168));
        add(sub, g);

        add(Box.createVerticalStrut(16), g);

        // Get Started button
        JButton btn = makeButton("Get Started");
        btn.addActionListener(e -> app.show("Role"));
        add(btn, g);
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(93, 122, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(180, 45));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}