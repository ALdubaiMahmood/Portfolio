package spmt;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class TeacherPanel extends JPanel {

    private MainFrame app;
    private DefaultTableModel model;
    private JTable table;

    private static final String[] COLS = {
        "Name", "ID", "Attendance", "Study Hrs",
        "Participation", "Behavior", "Assignments", "Points", "Status"
    };

    // Colors
    private static final Color BG     = new Color(30,  30,  40);
    private static final Color CARD   = new Color(26,  31,  46);
    private static final Color ACCENT = new Color(93, 122, 255);
    private static final Color RED    = new Color(255, 79, 109);
    private static final Color GREEN  = new Color(34, 199, 122);
    private static final Color YELLOW = new Color(245, 197, 66);
    private static final Color TEXT   = Color.WHITE;
    private static final Color TEXT2  = new Color(140, 148, 168);

    public TeacherPanel(MainFrame app) {
        this.app = app;
        setBackground(BG);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        refresh();
    }

    // -- Top bar with title and Back button ----------------------
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);

        JLabel title = new JLabel("Teacher Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        bar.add(title, BorderLayout.WEST);

        JButton back = btn("Back", new Color(60, 65, 85));
        back.addActionListener(e -> app.show("Role"));
        bar.add(back, BorderLayout.EAST);

        return bar;
    }

    // -- Student table --------------------------------------------
    private JScrollPane buildTable() {
        model = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(50, 60, 90));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(40, 45, 60));
        table.setShowGrid(true);
        table.getTableHeader().setBackground(new Color(20, 22, 35));
        table.getTableHeader().setForeground(TEXT2);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Color the Status column
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v == null ? "" : v.toString().toLowerCase();
                setForeground(s.contains("danger") ? RED : s.contains("average") ? YELLOW : GREEN);
                setBackground(sel ? new Color(50,60,90) : CARD);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        // Set default renderer for other cells (dark background)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? new Color(50, 60, 90) : CARD);
                setForeground(TEXT);
                return this;
            }
        };
        for (int i = 0; i < 8; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 75)));
        return scroll;
    }

    // -- Bottom button bar: Add, Delete, Note ---------------------
    private JPanel buildButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setBackground(BG);

        JButton addBtn    = btn("+ Add Student",    ACCENT);
        JButton deleteBtn = btn("- Delete Student", RED);
        JButton noteBtn   = btn("Send Note",        new Color(245, 197, 66));

        addBtn.addActionListener(e -> addStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        noteBtn.addActionListener(e -> sendNote());

        bar.add(addBtn);
        bar.add(deleteBtn);
        bar.add(noteBtn);

        JLabel hint = new JLabel("  Select a row first to Delete or Send Note");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(TEXT2);
        bar.add(hint);

        return bar;
    }

    // -- Add Student -----------------------------------------------
    private void addStudent() {
        JTextField nameF  = field();
        JTextField idF    = field();
        JTextField attF   = field();
        JTextField hrsF   = field();
        JTextField partF  = field();
        JTextField behF   = field();
        JTextField asgnF  = field();
        JTextField ptsF   = field();

        Object[] form = {
            "Name:",               nameF,
            "ID:",                 idF,
            "Attendance (0-100):", attF,
            "Study Hours:",        hrsF,
            "Participation:",      partF,
            "Behavior:",           behF,
            "Pending Assignments:",asgnF,
            "Points:",             ptsF
        };

        int result = JOptionPane.showConfirmDialog(this, form,
                "Add New Student", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameF.getText().trim().toUpperCase();
            String id   = idF.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                error("Name and ID cannot be empty.");
                return;
            }

            // Check duplicate ID
            for (Student s : app.students) {
                if (s.getId().equals(id)) {
                    error("A student with ID " + id + " already exists.");
                    return;
                }
            }

            Student s = new Student(name, id,
                    Integer.parseInt(attF.getText().trim()),
                    Integer.parseInt(hrsF.getText().trim()),
                    Integer.parseInt(partF.getText().trim()),
                    behF.getText().trim(),
                    Integer.parseInt(asgnF.getText().trim()),
                    Integer.parseInt(ptsF.getText().trim()));

            app.students.add(s);
            FileManager.save(app.students);
            refresh();
            info(name + " added successfully!");

        } catch (NumberFormatException ex) {
            error("Please enter valid numbers for Attendance, Hours, Participation, Assignments, and Points.");
        }
    }

    // -- Delete Student --------------------------------------------
    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Please select a student from the table first.");
            return;
        }

        String name = model.getValueAt(row, 0).toString();
        String id   = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + name + " (ID: " + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        app.students.removeIf(s -> s.getId().equals(id));
        FileManager.save(app.students);
        refresh();
        info(name + " has been deleted.");
    }

    // -- Send Note -------------------------------------------------
    private void sendNote() {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Please select a student from the table first.");
            return;
        }

        String id   = model.getValueAt(row, 1).toString();
        String name = model.getValueAt(row, 0).toString();

        JTextArea area = new JTextArea(5, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        int result = JOptionPane.showConfirmDialog(this,
                new JScrollPane(area),
                "Send note to " + name,
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String text = area.getText().trim();
        if (text.isEmpty()) {
            error("Note cannot be empty.");
            return;
        }

        for (Student s : app.students) {
            if (s.getId().equals(id)) {
                s.addMessage("Teacher: " + text);
                break;
            }
        }
        FileManager.save(app.students);
        info("Note sent to " + name + "!");
    }

    // -- Refresh table (sorted by status) -------------------------
    public void refresh() {
        ArrayList<Student> sorted = new ArrayList<>(app.students);
        sorted.sort(Comparator
                .comparingInt(this::rank)
                .thenComparingInt(s -> -s.getPoints()));

        model.setRowCount(0);
        for (Student s : sorted) {
            model.addRow(new Object[]{
                s.getName(),
                s.getId(),
                s.getAttendance() + "%",
                s.getStudyHours() + "h",
                s.getParticipation(),
                s.getBehavior(),
                s.getAssignments(),
                s.getPoints(),
                s.getStatus()
            });
        }
    }

    private int rank(Student s) {
        String st = s.getStatus();
        if (st.contains("Danger"))  return 0;
        if (st.contains("Average")) return 1;
        return 2;
    }

    // -- Helpers ---------------------------------------------------
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField field() {
        JTextField f = new JTextField(15);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }

    private void info(String msg)  { JOptionPane.showMessageDialog(this, msg, "SPMT", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
