package spmt;

import javax.swing.*;
import java.awt.*;

public class StudentLoginPanel extends JPanel {

    public StudentLoginPanel(MainFrame app) {
        setBackground(new Color(30, 30, 40));
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.CENTER;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Student Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        g.fill = GridBagConstraints.NONE;
        add(title, g);

        JLabel sub = new JLabel("Enter your name and ID", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(140, 148, 168));
        add(sub, g);

        add(Box.createVerticalStrut(10), g);

        // Name field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(nameLabel, g);

        JTextField nameField = new JTextField(18);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(250, 36));
        g.fill = GridBagConstraints.HORIZONTAL;
        add(nameField, g);

        // ID field
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.fill = GridBagConstraints.NONE;
        add(idLabel, g);

        JTextField idField = new JTextField(18);
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idField.setPreferredSize(new Dimension(250, 36));
        g.fill = GridBagConstraints.HORIZONTAL;
        add(idField, g);

        add(Box.createVerticalStrut(6), g);

        // Login button
        g.fill = GridBagConstraints.NONE;
        JButton loginBtn = makeBtn("Login", new Color(93, 122, 255));
        loginBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String id   = idField.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name and ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student found = null;
            for (Student s : app.students) {
                if (s.getName().equalsIgnoreCase(name) && s.getId().equals(id)) {
                    found = s;
                    break;
                }
            }

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Student not found! Check your name and ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show the student's dashboard
            String panelName = "Dashboard_" + found.getId();
            boolean exists = false;
            for (Component c : app.getRoot().getComponents()) {
                if (panelName.equals(c.getName())) { exists = true; break; }
            }
            if (!exists) {
                StudentDashboard dash = new StudentDashboard(app, found);
                dash.setName(panelName);
                app.getRoot().add(dash, panelName);
            }
            app.show(panelName);
        });
        add(loginBtn, g);

        JButton backBtn = makeBtn("Back", new Color(60, 65, 85));
        backBtn.addActionListener(e -> app.show("Role"));
        add(backBtn, g);

        // Allow Enter key to trigger login
        idField.addActionListener(e -> loginBtn.doClick());
        nameField.addActionListener(e -> loginBtn.doClick());
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(220, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
