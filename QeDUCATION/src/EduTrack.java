import java.util.Scanner;

public class EduTrack {

    // Storage (adjust MAX if you need more)
    static final int MAX = 100;
    static String[] subjects = new String[MAX];
    static double[] scores = new double[MAX];
    static String[] letterGrades = new String[MAX];
    static int count = 0;

    // Resource storage
    static String[] resources = new String[100];
    static String[] resourceLinks = new String[100];
    static int resCount = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            printMainMenu();
            choice = readInt(sc, "Enter choice: ", 1, 7);

            switch (choice) {
                case 1:
                    addSubject(sc);
                    break;
                case 2:
                    viewAllSubjects();
                    break;
                case 3:
                    calculateAndDisplayGPA();
                    break;
                case 4:
                    performanceReport(sc);
                    break;
                case 5:
                    resourcesMenu(sc);
                    break;
                case 6:
                    clearAllData(sc);
                    break;
                case 7:
                    System.out.println("Exiting EduTrack. Good luck with your studies!");
                    break;
                default:
                    System.out.println("Unknown option.");
            }

        } while (choice != 7);

        sc.close();
    }

    // ---------- Menus ----------
    public static void printMainMenu() {
        System.out.println("\n======================================");
        System.out.println("           EduTrack (SDG 4)");
        System.out.println("======================================");
        System.out.println("1. Add Subject and Score");
        System.out.println("2. View All Subjects & Letter Grades");
        System.out.println("3. Calculate GPA & Overall Letter");
        System.out.println("4. Performance Report (strongest/weakest + recommendations)");
        System.out.println("5. Resources (Add / View)");
        System.out.println("6. Clear All Data");
        System.out.println("7. Exit");
    }

    // ---------- Input helpers with validation ----------
    // Read integer with bounds and reprompt on invalid input
    public static int readInt(Scanner sc, String prompt, int min, int max) {
        int val;
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            try {
                val = Integer.parseInt(line.trim());
                if (val < min || val > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Please try again.");
            }
        }
    }

    // Read double with bounds and reprompt on invalid input
    public static double readDouble(Scanner sc, String prompt, double min, double max) {
        double val;
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            try {
                val = Double.parseDouble(line.trim());
                if (val < min || val > max) {
                    System.out.printf("Please enter a value between %.2f and %.2f.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    // ---------- Core features ----------
    public static void addSubject(Scanner sc) {
        if (count >= MAX) {
            System.out.println("Maximum subjects reached.");
            return;
        }

        System.out.print("Enter subject name: ");
        String subj = sc.nextLine().trim();
        if (subj.isEmpty()) {
            System.out.println("Subject name cannot be empty.");
            return;
        }

        double score = readDouble(sc, "Enter score (0 - 100): ", 0.0, 100.0);

        subjects[count] = subj;
        scores[count] = score;
        letterGrades[count] = scoreToLetter(score);

        System.out.printf("Saved: %s | Score: %.2f | Grade: %s%n", subj, score, letterGrades[count]);
        count++;
    }

    public static void viewAllSubjects() {
        if (count == 0) {
            System.out.println("No subject records found.");
            return;
        }
        System.out.println("\n--- Subjects & Grades ---");
        for (int i = 0; i < count; i++) {
            System.out.printf("%d. %s  |  Score: %.2f  |  Grade: %s%n", i + 1, subjects[i], scores[i], letterGrades[i]);
        }
    }

    // Calculate GPA based on letter grade points and display overall letter
    public static void calculateAndDisplayGPA() {
        if (count == 0) {
            System.out.println("No records to calculate GPA.");
            return;
        }

        double totalPoints = 0.0;
        for (int i = 0; i < count; i++) {
            totalPoints += letterToPoint(letterGrades[i]);
        }
        double gpa = totalPoints / count;
        String overallLetter = gpaToOverallLetter(gpa);

        System.out.println("\n--- GPA & Overall Letter ---");
        System.out.printf("Subjects counted: %d%n", count);
        System.out.printf("GPA (4.0 scale): %.2f%n", gpa);
        System.out.printf("Overall Letter Score: %s%n", overallLetter);
        System.out.println("Feedback: " + overallFeedback(overallLetter));
    }

    // Performance report: strongest/weakest, suggestions
    public static void performanceReport(Scanner sc) {
        if (count == 0) {
            System.out.println("No records to generate report.");
            return;
        }

        // Find highest and lowest
        double highestScore = scores[0];
        double lowestScore = scores[0];
        int idxHigh = 0, idxLow = 0;
        double sum = 0;

        for (int i = 0; i < count; i++) {
            double s = scores[i];
            sum += s;
            if (s > highestScore) {
                highestScore = s;
                idxHigh = i;
            }
            if (s < lowestScore) {
                lowestScore = s;
                idxLow = i;
            }
        }

        double average = sum / count;

        System.out.println("\n--- Performance Report ---");
        System.out.printf("Average Score: %.2f%n", average);
        System.out.printf("Strongest Subject: %s (%.2f) Grade: %s%n", subjects[idxHigh], highestScore, letterGrades[idxHigh]);
        System.out.printf("Weakest Subject: %s (%.2f) Grade: %s%n", subjects[idxLow], lowestScore, letterGrades[idxLow]);

        // Identify all weak subjects (<50) and give recommendations
        StringBuilder weakList = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (scores[i] < 50.0) { // threshold for "weak"
                weakList.append(String.format("- %s (%.2f) %n", subjects[i], scores[i]));
            }
        }

        if (weakList.length() > 0) {
            System.out.println("\nFound subjects needing improvement:");
            System.out.print(weakList.toString());
            System.out.println("\nRecommendations:");
            System.out.println("1) Review core concepts and re-do practice exercises for the weak subjects.");
            System.out.println("2) Use targeted resources (see Resources menu).");
            System.out.println("3) Schedule short daily study sessions (25-50 mins) focusing on weak topics.");
            System.out.println("4) Ask peers or teachers for clarification and try past exam questions.");
        } else {
            System.out.println("\nNo subjects below 50. Keep consistent study to maintain/improve performance!");
        }

        // Offer to show suggested resources automatically matched by keyword
        System.out.print("\nWould you like automatic resource suggestions for the weakest subject(s)? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            suggestResourcesForWeakSubjects();
        }
    }

    // ---------- Resources submenu ----------
    public static void resourcesMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("\n--- Resources Menu ---");
            System.out.println("1. Add New Resource");
            System.out.println("2. View All Resources");
            System.out.println("3. Back to Main Menu");
            choice = readInt(sc, "Choice: ", 1, 3);

            switch (choice) {
                case 1:
                    addResource(sc);
                    break;
                case 2:
                    viewResources();
                    break;
                case 3:
                    break;
            }
        } while (choice != 3);
    }

    public static void addResource(Scanner sc) {
        System.out.print("Enter resource title (e.g. 'Algebra Crash Course'): ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Resource title cannot be empty.");
            return;
        }
        System.out.print("Enter resource link or note (optional): ");
        String link = sc.nextLine().trim();

        resources[resCount] = title;
        resourceLinks[resCount] = link;
        resCount++;
        System.out.println("Resource added.");
    }

    public static void viewResources() {
        if (resCount == 0) {
            System.out.println("No resources added yet.");
            return;
        }
        System.out.println("\n--- Resources List ---");
        for (int i = 0; i < resCount; i++) {
            System.out.printf("%d. %s %s%n", i + 1, resources[i], resourceLinks[i].isEmpty() ? "" : " | " + resourceLinks[i]);
        }
    }

    // Suggest resources by checking keywords in subject names and matching resource titles
    public static void suggestResourcesForWeakSubjects() {
        boolean foundAny = false;
        for (int i = 0; i < count; i++) {
            if (scores[i] < 50.0) {
                String subj = subjects[i].toLowerCase();
                System.out.printf("\nSuggestions for %s:%n", subjects[i]);
                // naive matching: look for resource titles containing key words
                for (int r = 0; r < resCount; r++) {
                    String title = resources[r].toLowerCase();
                    if (title.contains(subj) || title.contains(subj.split(" ")[0])) {
                        System.out.printf("- %s %s%n", resources[r], resourceLinks[r].isEmpty() ? "" : "| " + resourceLinks[r]);
                        foundAny = true;
                    }
                }
                // generic fallback suggestions
                System.out.println("- Use summary notes and practice questions.");
                System.out.println("- Try short video lessons or past year papers.");
            }
        }
        if (!foundAny) {
            System.out.println("No direct resource matches found — try adding resources in the Resources menu.");
        }
    }

    // ---------- Utility and calculation methods ----------
    // Convert numeric score to letter grade (A,B,C,F)
    public static String scoreToLetter(double score) {
        if (score >= 85.0) return "A";
        else if (score >= 70.0) return "B";
        else if (score >= 50.0) return "C";
        else return "F";
    }

    // Map letter to grade point for GPA
    public static double letterToPoint(String letter) {
        switch (letter) {
            case "A":
                return 4.0;
            case "B":
                return 3.0;
            case "C":
                return 2.0;
            default:
                return 0.0; // F or unknown
        }
    }

    // Convert GPA (average grade points) to an overall letter score
    public static String gpaToOverallLetter(double gpa) {
        // thresholds chosen so A~3.5+, B~2.5-3.49, C~1.0-2.49, F <1.0
        if (gpa >= 3.5) return "A";
        else if (gpa >= 2.5) return "B";
        else if (gpa >= 1.0) return "C";
        else return "F";
    }

    // Human readable feedback
    public static String overallFeedback(String letter) {
        switch (letter) {
            case "A":
                return "Excellent. Keep up the strong performance and help peers if you can.";
            case "B":
                return "Good. Focus on improving weaker topics to reach top performance.";
            case "C":
                return "Satisfactory. Create a study schedule and seek help for challenging topics.";
            default:
                return "Failing overall. Immediate action: meetings with instructor, targeted practice, and use resources.";
        }
    }

    // Clear all stored subject/resource data (with confirmation)
    public static void clearAllData(Scanner sc) {
        System.out.print("Are you sure you want to clear all subject data and resources? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            for (int i = 0; i < count; i++) {
                subjects[i] = null;
                scores[i] = 0;
                letterGrades[i] = null;
            }
            for (int i = 0; i < resCount; i++) {
                resources[i] = null;
                resourceLinks[i] = null;
            }
            count = 0;
            resCount = 0;
            System.out.println("All data cleared.");
        } else {
            System.out.println("Clear canceled.");
        }
    }
}
