package spmt;

import java.util.ArrayList;

public class Student {

    private String name;
    private String id;
    private int attendance;
    private int studyHours;
    private int participation;
    private String behavior;
    private int assignments;
    private int points;
    private ArrayList<String> messages = new ArrayList<>();

    public Student(String name, String id, int attendance, int studyHours,
                   int participation, String behavior, int assignments, int points) {
        this.name          = name;
        this.id            = id;
        this.attendance    = attendance;
        this.studyHours    = studyHours;
        this.participation = participation;
        this.behavior      = behavior;
        this.assignments   = assignments;
        this.points        = points;
    }

    // Getters
    public String getName()        { return name; }
    public String getId()          { return id; }
    public int getAttendance()     { return attendance; }
    public int getStudyHours()     { return studyHours; }
    public int getParticipation()  { return participation; }
    public String getBehavior()    { return behavior; }
    public int getAssignments()    { return assignments; }
    public int getPoints()         { return points; }
    public ArrayList<String> getMessages() { return messages; }

    // Setters
    public void setAttendance(int v)    { attendance    = v; }
    public void setStudyHours(int v)    { studyHours    = v; }
    public void setParticipation(int v) { participation = v; }
    public void setBehavior(String v)   { behavior      = v; }
    public void setAssignments(int v)   { assignments   = v; }
    public void setPoints(int v)        { points        = v; }

    public void addMessage(String msg) { messages.add(msg); }

    public String getStatus() {
        if (points >= 100)          return "Excellent - Champion";
        if (attendance < 50 || assignments > 3 || behavior.equalsIgnoreCase("Weak"))
                                    return "In Danger";
        if (attendance < 70 || assignments > 1)
                                    return "Average";
        return "Excellent";
    }
}
