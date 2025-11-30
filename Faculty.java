import java.io.*;
import java.util.*;

public class Faculty {

    private static final String FACULTY_FILE = "data/professors.txt";

    private String profId;
    private String fullName;
    private List<String> subjects;

    // ===================== LOGIN =====================
    public static void login() {
        System.out.println("\n--- FACULTY LOGIN ---");

        String id = Utils.input("Professor ID: ").trim();
        String pass = Utils.input("Password: ").trim(); // Plain text password

        List<String> profs = Utils.readFile(FACULTY_FILE);
        for (String line : profs) {
            String[] p = line.split("\\|", -1);
            if (p.length < 4)
                continue;

            String fID = p[0].trim();
            String fPass = p[1].trim();

            if (fID.equals(id) && fPass.equals(pass)) {
                Faculty f = new Faculty();
                f.profId = fID;
                f.fullName = p[2].trim();
                f.subjects = Arrays.asList(p[3].split(","));

                System.out.println("\nLogin successful. Welcome, " + f.fullName + "!");
                f.menu();
                return;
            }
        }

        System.out.println("Invalid ID or password.");
    }

    // ===================== DASHBOARD =====================
    private void menu() {
        while (true) {
            System.out.println("\n===== FACULTY DASHBOARD =====");
            System.out.println("Professor ID: " + profId);
            System.out.println("Name: " + fullName);
            System.out.println("\n1. My Courses");
            System.out.println("2. Total Students");
            System.out.println("3. Encode Grades");
            System.out.println("4. Manage Schedule");
            System.out.println("5. Resignation");
            System.out.println("6. Logout");

            int choice = Utils.inputInt("Choice: ");

            switch (choice) {
                case 1 -> viewMyCourses();
                case 2 -> totalStudents();
                case 3 -> encodeGrades();
                case 4 -> manageSchedule();
                case 5 -> resign();
                case 6 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ===================== VIEW COURSES =====================
    private void viewMyCourses() {
        System.out.println("\n--- MY COURSES ---");
        System.out.println("Subjects handled: " + String.join(", ", subjects));
    }

    // ===================== TOTAL STUDENTS =====================
    private void totalStudents() {
        System.out.println("\n--- TOTAL STUDENTS ---");
        System.out.println("1. View All");
        System.out.println("2. View by Program");

        int ch = Utils.inputInt("Choice: ");
        File studentsDir = new File("data/students");
        if (!studentsDir.exists() || studentsDir.listFiles() == null) {
            System.out.println("No students yet.");
            return;
        }

        int count = 0;
        if (ch == 1) {
            count = studentsDir.listFiles().length;
            System.out.println("Total Students (All Programs): " + count);
            return;
        }

        String prog = Utils.input("Enter program (BSIT/BSCS/BSIS): ").toUpperCase();

        for (File f : studentsDir.listFiles()) {
            List<String> info = Utils.readFile(f.getPath());
            if (info.size() > 9 && info.get(9).equalsIgnoreCase(prog)) {
                count++;
            }
        }
        System.out.println("Total Students in " + prog + ": " + count);
    }

    // ===================== ENCODE GRADES =====================
    private void encodeGrades() {
        System.out.println("\n--- ENCODE GRADES ---");
        System.out.println("Subjects handled: " + String.join(", ", subjects));

        String studId = Utils.input("Enter Student ID: ");
        File studFile = new File("data/students/" + studId);
        if (!studFile.exists()) {
            System.out.println("Student not found.");
            return;
        }

        List<String> info = Utils.readFile(studFile.getPath());
        double sum = 0;
        List<String> grades = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            double g = Utils.inputDouble("Enter grade " + i + ": ");
            grades.add(String.valueOf(g));
            sum += g;
        }
        double avg = sum / 5;

        // Ensure enough space in file
        while (info.size() < 17)
            info.add("");
        for (int i = 0; i < 5; i++) {
            if (info.size() > 17 + i)
                info.set(17 + i, grades.get(i));
            else
                info.add(grades.get(i));
        }
        if (info.size() > 22)
            info.set(22, String.valueOf(avg));
        else
            info.add(String.valueOf(avg));

        Utils.writeFile(studFile.getPath(), info);

        System.out.println("Grades encoded successfully.");
        System.out.println("Average: " + avg);
    }

    // ===================== MANAGE SCHEDULE =====================
    private void manageSchedule() {
        System.out.println("\n--- MANAGE SCHEDULE ---");
        System.out.println("1. Add Schedule");
        System.out.println("2. View Schedule");
        int ch = Utils.inputInt("Choice: ");

        switch (ch) {
            case 1 -> addSchedule();
            case 2 -> viewSchedule();
            default -> System.out.println("Invalid choice.");
        }
    }

    private String getScheduleFile() {
        return "data/Schedules/" + profId + "_schedule.txt";
    }

    private void addSchedule() {
        Utils.ensureDir("data/Schedules");

        String days = Utils.input("Days (e.g. MON,TUE,FRI): ");
        String time = Utils.input("Time (e.g. 9-11): ");
        String progs = Utils.input("Programs (e.g. BSIT,BSCS): ");

        List<String> file = Utils.readFile(getScheduleFile());
        if (file.isEmpty())
            file.add("[" + String.join(", ", subjects) + "]");

        file.add(days + " | " + time + " | " + progs);
        Utils.writeFile(getScheduleFile(), file);

        System.out.println("Schedule added successfully!");
    }

    private void viewSchedule() {
        List<String> file = Utils.readFile(getScheduleFile());
        if (file.isEmpty()) {
            System.out.println("No schedule yet.");
            return;
        }

        System.out.println("\n--- SCHEDULE FOR " + fullName + " ---");
        for (String s : file) {
            System.out.println(s);
        }
    }

    private void resign() {
        System.out.println("\n--- RESIGNATION REQUEST ---");

        String filePath = "data/Resignation/resignation_" + profId + ".txt";
        File file = new File(filePath);

        if (file.exists()) {
            List<String> info = Utils.readFile(filePath);

            if (!info.isEmpty()) {
                String[] parts = info.get(0).split("\\|");
                if (parts.length >= 4) {
                    String existingReason = parts[2].trim();
                    String status = parts[3].trim();

                    System.out.println("You already submitted a resignation.");
                    System.out.println("Reason: " + existingReason);
                    System.out.println("Status: " + status);
                    return; 
                }
            }
        }

        String reason = Utils.input("Enter your reason for resignation: ").trim();

        if (reason.isEmpty()) {
            System.out.println("Resignation cancelled. Reason cannot be empty.");
            return;
        }

        Utils.ensureDir("data/Resignation");

        List<String> lines = new ArrayList<>();
        lines.add(profId + " | " + fullName + " | " + reason + " | Pending");

        Utils.writeFile(filePath, lines);

        System.out.println("Your resignation request has been submitted.");
        System.out.println("Status: Pending (waiting for admin approval)");
    }

}
