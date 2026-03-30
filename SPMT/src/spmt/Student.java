package spmt;

import java.util.ArrayList;

/**
 * INHERITANCE:
 * Student extends User, so it automatically gets
 * getName(), getId(), setName(), setId(), toString().
 *
 * POLYMORPHISM:
 * Student overrides getRole() from User with its own version.
 *
 * INTERFACE:
 * Student implements Evaluatable, which means it must
 * provide a getStatus() method.
 */
public class Student extends User implements Evaluatable {

    private int    attendance;
    private int    studyHours;
    private int    participation;
    private String behavior;
    private int    assignments;
    private int    points;
    private ArrayList<String> messages = new ArrayList<>();

    public Student(String name, String id, int attendance, int studyHours,
                   int participation, String behavior, int assignments, int points) {
        super(name, id);   // calls User constructor (Inheritance)
        this.attendance    = attendance;
        this.studyHours    = studyHours;
        this.participation = participation;
        this.behavior      = behavior;
        this.assignments   = assignments;
        this.points        = points;
    }

    // Getters
    public int    getAttendance()    { return attendance;    }
    public int    getStudyHours()    { return studyHours;    }
    public int    getParticipation() { return participation; }
    public String getBehavior()      { return behavior;      }
    public int    getAssignments()   { return assignments;   }
    public int    getPoints()        { return points;        }
    public ArrayList<String> getMessages() { return messages; }

    // Setters
    public void setAttendance(int v)    { attendance    = v; }
    public void setStudyHours(int v)    { studyHours    = v; }
    public void setParticipation(int v) { participation = v; }
    public void setBehavior(String v)   { behavior      = v; }
    public void setAssignments(int v)   { assignments   = v; }
    public void setPoints(int v)        { points        = v; }

    public void addMessage(String msg)  { messages.add(msg); }

    /**
     * POLYMORPHISM:
     * Overrides the abstract getRole() from User.
     * When you call getRole() on a Student, this runs.
     */
    @Override
    public String getRole() {
        return "Student";
    }

    /**
     * INTERFACE IMPLEMENTATION:
     * getStatus() is required by the Evaluatable interface.
     * It evaluates how the student is performing.
     */
    @Override
    public String getStatus() {
        if (points >= 100)
            return "Excellent - Champion";
        if (attendance < 50 || assignments > 3 || behavior.equalsIgnoreCase("Weak"))
            return "In Danger";
        if (attendance < 70 || assignments > 1)
            return "Average";
        return "Excellent";
    }
}
