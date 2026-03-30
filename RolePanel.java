package spmt;

import javax.swing.*;
import java.awt.*;

public class RolePanel extends JPanel {

    public RolePanel(MainFrame app) {
        setBackground(new Color(30, 30, 40));
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Choose Your Role");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        add(title, g);

        add(Box.createVerticalStrut(10), g);

        JButton teacherBtn = makeButton("Enter as Teacher", new Color(93, 122, 255));
        JButton studentBtn = makeButton("Enter as Student", new Color(34, 199, 122));
        JButton backBtn    = makeButton("Back",             new Color(60, 65, 85));

        teacherBtn.addActionListener(e -> app.show("Teacher"));
        studentBtn.addActionListener(e -> app.show("Login"));
        backBtn.addActionListener(e -> app.show("Welcome"));

        add(teacherBtn, g);
        add(studentBtn, g);
        add(backBtn, g);
    }

    private JButton makeButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(220, 46));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
