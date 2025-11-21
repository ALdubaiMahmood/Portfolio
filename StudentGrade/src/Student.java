public class Student {
    public static void main(String[] args) {

        // Fixed scores
        double math = 90;
        double physics = 80;
        double programming = 45;

        // Calculate overall average
        double average = (math + physics + programming) / 3;
        int avgInt = (int) average; // Convert average to an integer

        // Determine grade for each subject
        char mathGrade;
        if (math >= 90) mathGrade = 'A';
        else if (math >= 80) mathGrade = 'B';
        else if (math >= 70) mathGrade = 'C';
        else if (math >= 60) mathGrade = 'D';
        else mathGrade = 'F';

        char physicsGrade;
        if (physics >= 90) physicsGrade = 'A';
        else if (physics >= 80) physicsGrade = 'B';
        else if (physics >= 70) physicsGrade = 'C';
        else if (physics >= 60) physicsGrade = 'D';
        else physicsGrade = 'F';

        char programmingGrade;
        if (programming >= 90) programmingGrade = 'A';
        else if (programming >= 80) programmingGrade = 'B';
        else if (programming >= 70) programmingGrade = 'C';
        else if (programming >= 60) programmingGrade = 'D';
        else programmingGrade = 'F';

        // Determine the final grade based on the average
        char finalGrade;
        if (avgInt >= 90) finalGrade = 'A';
        else if (avgInt >= 80) finalGrade = 'B';
        else if (avgInt >= 70) finalGrade = 'C';
        else if (avgInt >= 60) finalGrade = 'D';
        else finalGrade = 'F';

        // Print results
        System.out.println("===== Student Grade Calculator =====");
        System.out.println("Math: " + math + "  Grade: " + mathGrade);
        System.out.println("Physics: " + physics + "  Grade: " + physicsGrade);
        System.out.println("Programming: " + programming + "  Grade: " + programmingGrade);
        System.out.println("-----------------------------------");
        System.out.println("Average: " + avgInt + "  Final Grade: " + finalGrade);

        // Simple feedback message
        if (finalGrade == 'A') {
            System.out.println("Excellent!");
        } else if (finalGrade == 'B') {
            System.out.println("Good job!");
        } else if (finalGrade == 'C') {
            System.out.println("Fair effort.");
        } else if (finalGrade == 'D') {
            System.out.println("You passed, but try to improve.");
        } else {
            System.out.println("Failed. Work harder next time!");
        }
    }
}
