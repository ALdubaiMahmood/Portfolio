package spmt;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private CardLayout cards = new CardLayout();
    private JPanel     root  = new JPanel(cards);

    ArrayList<Student> students;

    /**
     * POLYMORPHISM in action:
     * currentUser is of type User (the abstract parent class).
     * At runtime it can hold a Teacher OR a Student object.
     * This is polymorphism -- one variable, multiple forms.
     */
    User currentUser;

    public MainFrame() {
        setTitle("Student Performance & Motivation Tracker");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 40));

        students = FileManager.load();
        if (students.isEmpty()) addDefaults();

        root.add(new WelcomePanel(this),          "Welcome");
        root.add(new RolePanel(this),             "Role");
        root.add(new TeacherPanel(this),          "Teacher");
        root.add(new StudentLoginPanel(this),     "Login");

        add(root);
        show("Welcome");
    }

    public void show(String name) {
        cards.show(root, name);
    }
    public void loginAsTeacher() {
        currentUser = new Teacher("Teacher", "T01");
        System.out.println("Logged in as: " + currentUser.getRole());
        show("Teacher");
    }

    public void loginAsStudent(Student s) {
        currentUser = s;
        System.out.println("Logged in as: " + currentUser.getRole() + " - " + currentUser.getName());
    }

    public JPanel getRoot()       { return root;  }
    public CardLayout getCards()  { return cards; }

    private void addDefaults() {
        students.add(new Student("BADR",     "1",  80, 10, 5,  "Good",      1, 90));
        students.add(new Student("MOHAMMED", "2",  60,  5, 2,  "Weak",      2, 40));
        students.add(new Student("ANAS",     "3", 100, 15, 10, "Excellent", 0, 100));
        students.add(new Student("AHMED",    "4",  50,  4, 1,  "Weak",      3, 30));
        students.add(new Student("ALAA",     "5",  90, 12, 6,  "Good",      0, 70));
        students.add(new Student("ISSE",     "6",  70,  8, 4,  "Average",   1, 55));
        FileManager.save(students);
    }
}
