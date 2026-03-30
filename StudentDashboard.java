package spmt;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JPanel {

    private static final Color BG    = new Color(30, 30, 40);
    private static final Color CARD  = new Color(26, 31, 46);
    private static final Color TEXT  = Color.WHITE;
    private static final Color TEXT2 = new Color(140, 148, 168);
    private static final Color GREEN = new Color(34, 199, 122);
    private static final Color RED   = new Color(255, 79, 109);
    private static final Color YELL  = new Color(245, 197, 66);
    private static final Color BLUE  = new Color(93, 122, 255);

    public StudentDashboard(MainFrame app, Student s) {
        setBackground(BG);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // -- Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);

        JLabel welcome = new JLabel("Welcome, " + s.getName() + "   (ID: " + s.getId() + ")");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(TEXT);
        top.add(welcome, BorderLayout.WEST);

        JButton back = btn("Back");
        back.addActionListener(e -> app.show("Role"));
        top.add(back, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // -- Center: left = stats, right = messages
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setBackground(BG);
        center.add(buildStatsCard(s));
        center.add(buildMessagesCard(s));
        add(center, BorderLayout.CENTER);

        // -- Bottom: certificate or progress
        add(buildCertPanel(s), BorderLayout.SOUTH);
    }

    // -- Stats card ------------------------------------------------
    private JPanel buildStatsCard(Student s) {
        JPanel card = new JPanel(new GridLayout(8, 2, 8, 8));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 55, 75)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        addRow(card, "Attendance:",    s.getAttendance() + "%");
        addRow(card, "Study Hours:",   s.getStudyHours() + "h / week");
        addRow(card, "Participation:", String.valueOf(s.getParticipation()));
        addRow(card, "Behavior:",      s.getBehavior());
        addRow(card, "Assignments:",   String.valueOf(s.getAssignments()));
        addRow(card, "Points:",        String.valueOf(s.getPoints()));
        addRow(card, "Status:",        s.getStatus());
        addRow(card, "",               "");   // spacer row

        // Color the status value label
        Component[] comps = card.getComponents();
        // Status value is at index 13 (7th pair, right side)
        if (comps.length > 13 && comps[13] instanceof JLabel) {
            JLabel statusVal = (JLabel) comps[13];
            String st = s.getStatus().toLowerCase();
            statusVal.setForeground(st.contains("danger") ? RED : st.contains("average") ? YELL : GREEN);
            statusVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        return card;
    }

    private void addRow(JPanel panel, String key, String value) {
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        k.setForeground(TEXT2);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(TEXT);

        panel.add(k);
        panel.add(v);
    }

    // -- Messages card ---------------------------------------------
    private JPanel buildMessagesCard(Student s) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 55, 75)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Messages from Teacher");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(title, BorderLayout.NORTH);

        if (s.getMessages().isEmpty()) {
            JLabel none = new JLabel("No messages yet.", SwingConstants.CENTER);
            none.setForeground(TEXT2);
            none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            card.add(none, BorderLayout.CENTER);
        } else {
            JPanel list = new JPanel();
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            list.setBackground(CARD);
            for (String msg : s.getMessages()) {
                JLabel ml = new JLabel("<html>" + msg + "</html>");
                ml.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                ml.setForeground(TEXT);
                ml.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, BLUE),
                        BorderFactory.createEmptyBorder(6, 10, 6, 6)));
                list.add(ml);
                list.add(Box.createVerticalStrut(6));
            }
            JScrollPane scroll = new JScrollPane(list);
            scroll.getViewport().setBackground(CARD);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            card.add(scroll, BorderLayout.CENTER);
        }

        return card;
    }

    // -- Certificate panel -----------------------------------------
    private JPanel buildCertPanel(Student s) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(BG);

        if (s.getPoints() >= 100) {
            JLabel cert = new JLabel("Certificate of Excellence awarded to " + s.getName() + "!");
            cert.setFont(new Font("Segoe UI", Font.BOLD, 15));
            cert.setForeground(new Color(201, 150, 58));  // gold
            p.add(cert);
        } else {
            int need = 100 - s.getPoints();
            JLabel info = new JLabel("Earn " + need + " more points to unlock your Certificate of Excellence.");
            info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            info.setForeground(TEXT2);
            p.add(info);
        }

        return p;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(new Color(60, 65, 85));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(100, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
