package spmt;

import java.io.*;
import java.util.ArrayList;

public class FileManager {

    private static final String FILE = "students.txt";

    public static void save(ArrayList<Student> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Student s : list) {
                // Save student data line
                pw.println(s.getName() + "," + s.getId() + "," +
                        s.getAttendance() + "," + s.getStudyHours() + "," +
                        s.getParticipation() + "," + s.getBehavior() + "," +
                        s.getAssignments() + "," + s.getPoints());
                // Save each message on its own line
                for (String msg : s.getMessages()) {
                    pw.println("MSG:" + s.getId() + ":" + msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Student> load() {
        ArrayList<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("MSG:")) {
                    String[] p = line.split(":", 3);
                    if (p.length == 3) {
                        for (Student s : list) {
                            if (s.getId().equals(p[1])) {
                                s.addMessage(p[2]);
                                break;
                            }
                        }
                    }
                } else {
                    String[] d = line.split(",");
                    if (d.length >= 8) {
                        list.add(new Student(d[0], d[1],
                                Integer.parseInt(d[2]), Integer.parseInt(d[3]),
                                Integer.parseInt(d[4]), d[5],
                                Integer.parseInt(d[6]), Integer.parseInt(d[7])));
                    }
                }
            }
        } catch (FileNotFoundException ignored) {
            // No file yet - first run
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
