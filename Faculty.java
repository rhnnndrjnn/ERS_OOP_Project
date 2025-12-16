import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final String FACULTY_SCHED_PROF = FACULTY_DIR + File.separator + "FACULTY_MEMBERS";

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

    private void requestPanel() {
        Utils.ensureDir(COURSE_REQUESTS_DIR);
        String filePath = COURSE_REQUESTS_DIR + "/course_request.txt";

        List<String> checkStatus = Utils.readFile(filePath);
        List<String> saveStatus = Utils.readFile(filePath);

        if (checkStatus != null) {
            for (String line : checkStatus) {

                String[] parts = line.split("\\|", -1);
                if (parts.length < 4)
                    continue;

                String profIdFromFile = parts[0].trim();
                String reqstatus = parts[3].trim();

                if (profIdFromFile.equals(this.profId)) {

                    switch (reqstatus) {
                        case "PENDING":
                            System.out.println("You already have a pending request. Please wait for admin approval.");
                            return;

                        case "APPROVED":
                            System.out.println("Your previous request has been approved.");
                            int index = saveStatus.indexOf(line);
                            Admin.deleteRequest(index);
                            if (!Utils.input("Do you want to submit another request? (Y/N): ")
                                    .trim().equalsIgnoreCase("Y")) {
                                return;
                            }
                            break;

                        case "REJECTED":
                            System.out.println("Your previous request was denied.");
                            int i = saveStatus.indexOf(line);
                            Admin.deleteRequest(i);
                            if (!Utils.input("Do you want to submit another request? (Y/N): ")
                                    .trim().equalsIgnoreCase("Y")) {
                                return;
                            }
                            break;
                    }
                }
            }
        }

        String reason = Utils.input("Enter reason for course request: ").trim();
        if (reason.isEmpty()) {
            System.out.println("Request cancelled. Reason cannot be empty.");
            return;
        }

        String status = "PENDING";
        String record = this.profId + "|" + this.fullName + "|" + reason + "|" + status;

        saveStatus.add(record);
        Utils.writeRawFile(filePath, saveStatus);
        // Utils.appendToFile(filePath, record);
        System.out.println("Course request submitted.");
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
                case 2 -> requestPanel();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewMyCourses() {
        System.out.println("\n--- MY COURSES ---");

        String path = FACULTY_SCHED_PROF + "/" + profId + "/" + profId + "_Schedule.txt";
        List<String> sched = Utils.readFile(path);
        Path fileSched = Paths.get(path);

        if (!Files.exists(fileSched) || sched.isEmpty()) {
            System.out.println(
                    "\nYou currently have no schedule for now, make sure to approve your initial schedule first in [SCHEDULE APPROVALS].");
            return;
        }

        System.out.println("\n====== MY SCHEDULE ==========");

        List<String> approvedKeys = displayApprovedSchedules();

        System.out.println("\n====== AVAILABLE SCHEDULE ==========");
        System.out.println("# | [Year Level] | [Course] | [Section] | [Subject] | [Day and Time]");
        int idx = 1;
        List<String> available = new ArrayList<>();

        for (String line : sched) {
            String[] parts = line.split("\\|");
            String year = parts[0].trim();
            String course = parts[1].trim();
            String sec = parts[2].trim();
            String sub = parts[3].trim();
            String day = parts[4].trim();
            String time = parts[5].trim();

            String key = year + "|" + course + "|" + sec + "|" + sub + "|" + day + "|" + time;

            if (approvedKeys.contains(key))
                continue;

            available.add(line);

            System.out.println(
                    idx++ + " | " + year + " | " + course + " | " + sec + " | " +
                            sub + " | " + day + "/" + time);
        }

        int choice = Utils.inputInt("\nEnter the row number to accept, or 0 to cancel: ");

        if (choice <= 0 || choice > sched.size()) {
            System.out.println("Cancelled or invalid selection.");
            return;
        }

        // Only accept the selected schedule
        String selectedLine = sched.get(choice - 1);
        String[] parts = selectedLine.split("\\|");
        String yearLevel = parts[0].trim();
        String course = parts[1].trim();
        String section = parts[2].trim();
        String subject = parts[3].trim();
        String day = parts[4].trim();
        String time = parts[5].trim();
        String sy = parts[6].trim();
        String dept = parts[7].trim();

        String crsAndsec = course + section;
        String studDirPath = "data/STUDENTS/" + sy + "/" + yearLevel + "/" + dept + "/" + course + "/" + section + "/";
        File studDir = new File(studDirPath);
        if (!studDir.exists()) {
            System.out.println("Student directory does not exist: " + studDirPath);
            return;
        }

        String scheduleFilePath = studDirPath + crsAndsec + "_Schedule.txt";
        File scheduleFile = new File(scheduleFilePath);
        try {
            if (!scheduleFile.exists()) {
                scheduleFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> lines = Utils.readFile(scheduleFilePath);
        if (lines == null)
            lines = new ArrayList<>();

        // Append only the selected schedule
        lines.add(profId + "|" + fullName + "|" + subject + "|" + day + "|" + time);
        Utils.writeRawFile(scheduleFilePath, lines);

        System.out.println("Schedule saved to students in section " + section + " for " + course + ".");
    }

    private List<String> displayApprovedSchedules() {

        System.out.println("[Year Level] | [Course] | [Section] | [Subject] | [Day and Time]");
        List<String> approved = new ArrayList<>();

        File root = new File("data/STUDENTS");
        if (!root.exists())
            return approved;

        for (File syDir : root.listFiles(File::isDirectory)) {
            for (File yearDir : syDir.listFiles(File::isDirectory)) {
                for (File collegeDir : yearDir.listFiles(File::isDirectory)) {
                    for (File courseDir : collegeDir.listFiles(File::isDirectory)) {
                        for (File sectionDir : courseDir.listFiles(File::isDirectory)) {

                            File[] schedFiles = sectionDir.listFiles(
                                    (d, n) -> n.endsWith("_Schedule.txt"));
                            if (schedFiles == null)
                                continue;

                            for (File sf : schedFiles) {
                                for (String line : Utils.readFile(sf.getPath())) {

                                    if (!line.startsWith(profId + "|"))
                                        continue;

                                    String[] p = line.split("\\|");
                                    if (p.length != 5)
                                        continue;

                                    String key = yearDir.getName() + "|" +
                                            courseDir.getName() + "|" +
                                            sectionDir.getName() + "|" +
                                            p[2] + "|" + p[3] + "|" + p[4];

                                    approved.add(key);

                                    System.out.println(
                                            yearDir.getName() + " | " +
                                                    courseDir.getName() + " | " +
                                                    sectionDir.getName() + " | " +
                                                    p[2] + " | " + p[3] + "/" + p[4]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return approved;
    }

    private void gradeManagement() {
        System.out.println("\n--- GRADE MANAGEMENT ---");

        List<StudentEntry> students = getAllStudentsHandledByFaculty();

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        displayStudents(students);

        int pick = Utils.inputInt("\nSelect student #: ") - 1;
        if (pick < 0 || pick >= students.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        StudentEntry target = students.get(pick);

        if (target.grade != null) {
            System.out.println("already graded");
            return;
        }

        float grade = Utils.inputFloat("Enter grade: ");
        saveGrade(target, grade);
        System.out.println("Grade saved successfully.");

        students = getAllStudentsHandledByFaculty();
        displayStudents(students);
    }

    private void saveGrade(StudentEntry s, float grade) {
        try {
            List<String> lines = new ArrayList<>();
            boolean found = false;

            if (s.gradesFile.exists()) {
                lines = Utils.readFile(s.gradesFile.getPath());
            }

            String encProfId = Utils.encryptEachField(profId);
            String encFullName = Utils.encryptEachField(fullName);
            String encSubject = Utils.encryptEachField(s.subject);
            String newLine = encProfId + "~" + encFullName + "~" + encSubject + "~" + grade;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split("~", -1);
                if (parts.length < 4)
                    continue;

                if (parts[0].equals(encProfId) && parts[2].equals(encSubject)) {
                    lines.set(i, newLine);
                    found = true;
                    break;
                }
            }

            if (!found) {
                lines.add(newLine);
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(s.gradesFile))) {
                for (String line : lines) {
                    pw.println(line);
                }
            }

            s.grade = grade;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Float getProfGrade(File gradesFile, String subject) {
        if (!gradesFile.exists())
            return null;

        String encProfId = Utils.encryptEachField(profId);
        String encSubject = Utils.encryptEachField(subject);

        for (String line : Utils.readFile(gradesFile.getPath())) {
            if (line.trim().isEmpty())
                continue;
            String[] parts = line.split("~", -1);
            if (parts.length < 4)
                continue;

            if (parts[0].equals(encProfId) && parts[2].equals(encSubject)) {
                try {
                    return Float.parseFloat(parts[3]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private List<StudentEntry> getAllStudentsHandledByFaculty() {
        List<StudentEntry> result = new ArrayList<>();

        File sched = new File("data/FACULTY/FACULTY_MEMBERS/" + profId + "/" + profId + "_Schedule.txt");
        if (!sched.exists())
            return result;

        List<String> schedLines = Utils.readFile(sched.getPath());
        File studentsRoot = new File("data/STUDENTS");

        for (String line : schedLines) {
            String[] p = line.split("\\|");
            if (p.length < 8)
                continue;

            String year = p[0];
            String program = p[1];
            String section = p[2];
            String subject = p[3];
            String schoolYear = p[6];
            String college = p[7];

            File sectionDir = new File(studentsRoot,
                    schoolYear + "/" + year + "/" + college + "/" + program + "/" + section);
            if (!sectionDir.exists())
                continue;

            for (File studentDir : sectionDir.listFiles(f -> f.isDirectory())) {
                File info = new File(studentDir, studentDir.getName() + "_info.txt");
                File grades = new File(studentDir, studentDir.getName() + "_grades.txt");
                if (!info.exists())
                    continue;

                String dec = Utils.decryptEachField(Utils.readFile(info.getPath()).get(0));
                String[] s = dec.split("\\|", -1);

                Float grade = getProfGrade(grades, subject);

                result.add(new StudentEntry(s[0], program, section, s[1], grades, grade, subject));
            }
        }
        return result;
    }

    private void displayStudents(List<StudentEntry> list) {
        System.out.println("\n# | STUDENT ID | PROGRAM | SECTION | LAST NAME | GRADE");
        System.out.println("----------------------------------------------------");

        int i = 1;
        for (StudentEntry s : list) {
            String lastNameDisplay = s.lastName + (s.grade != null ? " (Graded)" : "");
            System.out.printf("%d | %s | %s | %s | %s | %s%n",
                    i++,
                    s.studentId,
                    s.program,
                    s.section,
                    lastNameDisplay,
                    s.grade == null ? "" : s.grade);
        }
    }

    class StudentEntry {
        String studentId;
        String program;
        String section;
        String lastName;
        File gradesFile;
        Float grade;
        String subject;

        StudentEntry(String id, String prog, String sec, String ln, File g, Float grade, String subject) {
            studentId = id;
            program = prog;
            section = sec;
            lastName = ln;
            gradesFile = g;
            this.grade = grade;
            this.subject = subject;
        }
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