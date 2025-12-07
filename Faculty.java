import java.io.*;
import java.util.*;

public class Faculty {

    private static final String FACULTY_DIR = "data/FACULTY";
    private static final String FACULTY_MASTER = FACULTY_DIR + File.separator + "ProfessorsMasterList.txt";
    private static final String MASTER_FACULTY_SCHEDULE_FILE = FACULTY_DIR + File.separator
            + "MASTER_FACULTY_SCHEDULE.txt";
    private static final String COURSE_REQUESTS_DIR = FACULTY_DIR + File.separator + "COURSE_REQUESTS";
    private static final String COURSES_DIR = FACULTY_DIR + File.separator + "COURSES";
    private static final String RESIGNATIONS_DIR = FACULTY_DIR + File.separator + "RESIGNATIONS";
    private static final String FACULTY_MEMBERS_DIR = FACULTY_DIR + File.separator + "FACULTY_MEMBERS";
    private static final String STUDENT_ROOT = "data/STUDENTS";

    private String profId;
    private String fullName;
    private List<String> subjects = new ArrayList<>();

    public static void login() {
        ensureProfessorsMasterExists();
        System.out.println("\n--- FACULTY LOGIN ---");

        String id = Utils.input("Professor ID: ").trim();
        String pass = Utils.input("Password: ").trim();

        List<String> lines = Utils.readFile(FACULTY_MASTER);
        if (lines == null)
            lines = new ArrayList<>();

        for (String ln : lines) {
            String[] p = ln.split("\\|", -1);
            if (p.length < 3)
                continue;

            String fID = p[0].trim();
            String fPass = p[1].trim();

            if (fID.equalsIgnoreCase(id) && fPass.equals(pass)) {
                Faculty f = new Faculty();
                f.profId = fID;
                f.fullName = p[2].trim();
                if (p.length >= 5 && !p[4].trim().isEmpty()) {
                    f.subjects = Arrays.asList(p[4].split(","));
                } else if (p.length >= 4 && !p[3].trim().isEmpty()) {
                    f.subjects = Arrays.asList(p[3].split(","));
                } else {
                    f.subjects = new ArrayList<>();
                }

                Utils.ensureDir(FACULTY_MEMBERS_DIR + "/" + f.profId);

                System.out.println("\nLogin successful.\n");
                System.err.println("Welcome, " + f.fullName + "!");
                f.menu();
                return;
            }
        }

        System.out.println("Invalid ID or password.");
    }

    private static void ensureProfessorsMasterExists() {
        File f = new File(FACULTY_MASTER);
        if (f.exists())
            return;

        Utils.ensureDir(FACULTY_DIR);
        List<String> defaults = new ArrayList<>();
        defaults.add("PROF-0001|faculty123|Dr. Karen Louise O. Maniego|COS|MAJOR");
        defaults.add("PROF-0002|faculty123|Prof. Gabriel R. Bautista|COS|MAJOR");
        defaults.add("PROF-0003|faculty123|Dr. Shiela Marie C. Roxas|COS|MAJOR");
        defaults.add("PROF-0004|faculty123|Dr. Faith L. Austria|COS|MAJOR");
        defaults.add("PROF-0005|faculty123|Prof. Nilo M. Santiago|COS|MAJOR");
        defaults.add("PROF-0006|faculty123|Engr. Ramon D. Vergara|COE|MAJOR");
        defaults.add("PROF-0007|faculty123|Engr. Liza Mae F. Catindig|COE|MAJOR");
        defaults.add("PROF-0008|faculty123|Engr. Julius P. Manaloto|COE|MAJOR");
        defaults.add("PROF-0009|faculty123|Engr. Christine Joy L. Amurao|COE|MAJOR");
        defaults.add("PROF-0010|faculty123|Engr. Patrick V. Delos Reyes|COE|MINOR");
        defaults.add("PROF-0011|faculty123|Prof. Maricar G. Tumbaga|CIE|MAJOR");
        defaults.add("PROF-0012|faculty123|Prof. Angelo S. Nabong|CIE|MAJOR");
        defaults.add("PROF-0013|faculty123|Prof. Ruby Anne F. Umandap|CIE|MAJOR");
        defaults.add("PROF-0014|faculty123|Prof. Jerico A. Bituin|CIE|MAJOR");
        defaults.add("PROF-0015|faculty123|Prof. Danilyn P. Arcega|CIE|MINOR");
        defaults.add("PROF-0016|faculty123|Arch. Lorena P. Mabanta|CAFA|MAJOR");
        defaults.add("PROF-0017|faculty123|Arch. Joel R. Ramos|CAFA|MAJOR");
        defaults.add("PROF-0018|faculty123|Prof. Nika Alyssa D. Serafica|CAFA|MAJOR");
        defaults.add("PROF-0019|faculty123|Prof. Manuel I. Paderes|CAFA|MAJOR");
        defaults.add("PROF-0020|faculty123|Arch. Trisha Mae C. Lontoc|CAFA|MINOR");
        defaults.add("PROF-0021|faculty123|Prof. Ronald F. Villaverde|CIT|MAJOR");
        defaults.add("PROF-0022|faculty123|Prof. Jenny Rose T. Abellera|CIT|MAJOR");
        defaults.add("PROF-0023|faculty123|Prof. Kenrick M. Santos|CIT|MAJOR");
        defaults.add("PROF-0024|faculty123|Prof. Arvin B. Doria|CIT|MAJOR");
        defaults.add("PROF-0025|faculty123|Prof. Mary Antonette S. Garde|CIT|MINOR");
        defaults.add("PROF-0026|faculty123|Prof. Hannah Grace B. Layug|CLA|MAJOR");
        defaults.add("PROF-0027|faculty123|Prof. Michael John S. Fabella|CLA|MAJOR");
        defaults.add("PROF-0028|faculty123|Dr. Rhea Camille O. De Guzman|CLA|MINOR");
        defaults.add("PROF-0029|faculty123|Prof. Jerome P. Balagtas|CLA|MINOR");
        defaults.add("PROF-0030|faculty123|Prof. Ana Patricia L. Yumul|CLA|MINOR");

        Utils.writeRawFile(FACULTY_MASTER, defaults);
        System.out.println("[INFO] ProfessorsMasterList.txt created with defaults.");
    }

    private void menu() {
        while (true) {
            System.out.println("\n===== FACULTY DASHBOARD =====");
            System.out.println("Professor ID: " + profId);
            System.out.println("Name: " + fullName);
            System.out.println("\n1. Courses");
            System.out.println("2. Grade Management");
            System.out.println("3. Assignment");
            System.out.println("4. Resignation");
            System.out.println("5. Logout");

            int choice = Utils.inputInt("Choice: ");

            switch (choice) {
                case 1 -> coursesMenu();
                case 2 -> gradeManagement();
                case 3 -> giveAssignment();
                case 4 -> resign();
                case 5 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void coursesMenu() {
        while (true) {
            System.out.println("\n--- COURSES ---");
            System.out.println("1. My Courses");
            System.out.println("2. Request Course");
            System.out.println("3. Back");

            int choice = Utils.inputInt("Choice: ");
            switch (choice) {
                case 1 -> viewMyCourses();
                case 2 -> requestCourse();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewMyCourses() {
        System.out.println("\n--- MY COURSES ---");

        String profFolder = FACULTY_MEMBERS_DIR + "/" + profId;
        Utils.ensureDir(profFolder);
        File dir = new File(profFolder);

        File[] files = dir.listFiles((d, name) -> name.endsWith("_courses.txt"));
        if (files == null || files.length == 0) {
            System.out.println("No courses assigned yet.");
            return;
        }

        for (File f : files) {
            List<String> lines = Utils.readFile(f.getPath());
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }

    private void requestCourse() {
        Utils.ensureDir(COURSE_REQUESTS_DIR);
        String reason = Utils.input("Enter reason for course request: ").trim();
        if (reason.isEmpty()) {
            System.out.println("Request cancelled. Reason cannot be empty.");
            return;
        }
        String path = COURSE_REQUESTS_DIR + "/" + profId + "_request.txt";
        Utils.appendToFile(path, Utils.encrypt(reason));
        System.out.println("Course request submitted.");
    }

    private void gradeManagement() {
        System.out.println("\n--- GRADE MANAGEMENT ---");
        String studentId = Utils.input("Enter Student ID: ").trim();
        File studentFile = findStudentFile(studentId);
        if (studentFile == null) {
            System.out.println("Student not found.");
            return;
        }

        List<String> lines = Utils.readFile(studentFile.getPath());
        List<String> updated = new ArrayList<>();
        double grade = Utils.inputDouble("Enter grade: ");

        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length > 0 && p[0].equalsIgnoreCase(studentId)) {
                updated.add(line + "|" + profId + ":" + grade);
            } else {
                updated.add(line);
            }
        }
        Utils.writeFile(studentFile.getPath(), updated);
        System.out.println("Grade recorded successfully for " + studentId);
    }

    private File findStudentFile(String studentId) {
        File root = new File(STUDENT_ROOT);
        List<File> allFiles = getAllStudentFiles(root);
        for (File f : allFiles) {
            List<String> lines = Utils.readFile(f.getPath());
            for (String line : lines) {
                String[] p = line.split("\\|", -1);
                if (p.length > 0 && p[0].equalsIgnoreCase(studentId))
                    return f;
            }
        }
        return null;
    }

    private List<File> getAllStudentFiles(File dir) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null)
            return result;

        for (File f : files) {
            if (f.isDirectory()) {
                result.addAll(getAllStudentFiles(f));
            } else if (f.getName().endsWith("_grades.txt")) {
                result.add(f);
            }
        }
        return result;
    }

    private void giveAssignment() {
        System.out.println("\n--- ASSIGN ASSIGNMENT ---");

        String schoolYear = Utils.input("School Year (e.g., 2024-2025): ").trim();
        String yearLevel = Utils.input("Year Level (e.g., 1stYear): ").trim();
        String program = Utils.input("Program (BSIT/BSCS/etc.): ").trim().toUpperCase();
        String section = Utils.input("Section: ").trim();

        File sectionFolder = new File(
                STUDENT_ROOT + "/" + schoolYear + "/" + yearLevel + "/" + program + "/" + section);
        if (!sectionFolder.exists()) {
            System.out.println("Section folder not found.");
            return;
        }

        File scheduleFile = new File(sectionFolder, section + "_schedule.txt");
        if (!scheduleFile.exists()) {
            System.out.println("Schedule file not found for this section.");
            return;
        }

        List<String> schedLines = Utils.readFile(scheduleFile.getPath());
        List<String> availableCourses = new ArrayList<>();

        for (String line : schedLines) {
            String[] parts = line.split(",");
            if (parts.length > 0) {
                String courseName = parts[0].trim();
                if (subjects.contains(courseName)) {
                    availableCourses.add(courseName);
                }
            }
        }

        if (availableCourses.isEmpty()) {
            System.out.println("No available courses in the schedule that you handle.");
            return;
        }

        System.out.println("\nSelect Course from Section Schedule:");
        for (int i = 0; i < availableCourses.size(); i++) {
            System.out.println((i + 1) + ". " + availableCourses.get(i));
        }

        int choice = Utils.inputInt("Choice: ");
        if (choice < 1 || choice > availableCourses.size()) {
            System.out.println("Invalid choice. Assignment cancelled.");
            return;
        }

        String selectedCourse = availableCourses.get(choice - 1);

        String title = Utils.input("Assignment Title: ").trim();
        String desc = Utils.input("Description: ").trim();
        String deadline = Utils.input("Deadline (MM/DD/YYYY): ").trim();

        if (title.isEmpty() || desc.isEmpty() || deadline.isEmpty()) {
            System.out.println("All fields are required. Assignment cancelled.");
            return;
        }

        String assignmentFile = sectionFolder.getPath() + "/" + section + "_assignments.txt";
        Utils.ensureDir(sectionFolder.getPath());

        String line = fullName + "|" + selectedCourse + "|" + title + "|" + desc + "|" + deadline
                + "|UPCOMING|PAST DUE|COMPLETED";
        Utils.appendToFile(assignmentFile, Utils.encrypt(line));

        System.out.println("Assignment successfully saved for section " + section + " under course: " + selectedCourse);
    }

    private void resign() {
        System.out.println("\n--- RESIGNATION REQUEST ---");

        Utils.ensureDir(RESIGNATIONS_DIR);

        String filePath = RESIGNATIONS_DIR + "/" + profId + "_resignation.txt";
        File file = new File(filePath);

        if (file.exists()) {
            List<String> info = Utils.readFile(filePath);

            if (!info.isEmpty()) {
                String decrypted = Utils.decryptEachField(info.get(0));
                String[] parts = decrypted.split("\\|", -1);

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

        String line = profId + "|" + fullName + "|" + reason + "|Pending";
        Utils.writeFile(filePath, Collections.singletonList(line));

        System.out.println("Your resignation request has been submitted.");
        System.out.println("Status: Pending (waiting for admin approval)");
    }

}
