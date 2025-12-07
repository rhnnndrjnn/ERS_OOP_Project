import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Admin {

    private static final String ADMIN_CREDENTIALS = "data/ADMIN/admin.txt";

    private static void initAdminFile() {
        File f = new File(ADMIN_CREDENTIALS);

        if (!f.exists()) {
            System.out.println("Admin file missing. Creating default admin...");

            Utils.ensureDir("data/ADMIN");

            String user = "admin.northwind.edu";
            String hashed = Utils.hashPassword("admin123");
            String role = "ADMIN";

            String raw = user + "|" + hashed + "|" + role;

            List<String> out = new ArrayList<>();
            out.add(raw);
            Utils.writeFile(ADMIN_CREDENTIALS, out);

            System.out.println("Default admin created: admin.northwind.edu | admin123");
        }
    }

    public static boolean login() {

        initAdminFile();

        List<String> file = Utils.readFile(ADMIN_CREDENTIALS);
        if (file == null || file.isEmpty()) {
            System.out.println("Admin file empty!");
            return false;
        }

        String decryptedLine = Utils.decryptEachField(file.get(0));

        String[] parts = decryptedLine.split("\\|");
        if (parts.length < 3) {
            System.out.println("Admin file corrupted!");
            return false;
        }

        String storedUser = parts[0];
        String storedHash = parts[1];
        String storedRole = parts[2];

        String inputUser = Utils.input("Enter Admin Username: ");
        String inputPass = Utils.input("Enter Admin Password: ");

        String hashedInput = Utils.hashPassword(inputPass);

        if (!inputUser.equals(storedUser) || !hashedInput.equals(storedHash)) {
            System.out.println("Wrong username or password!");
            return false;
        }

        System.out.println("\nWelcome, " + storedRole + "");
        adminMenu();
        return true;
    }

    private static void adminMenu() {

        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Applicants Management");
            System.out.println("2. Resignation Approvals");
            System.out.println("3. Schedule Management");
            System.out.println("4. Student Promotion");
            System.out.println("5. Log Out");

            String ch = Utils.input("Choice: ");

            switch (ch) {
                case "1":
                    applicantsMenu();
                    break;
                case "2":
                    processResignation();
                    break;
                case "3":
                    generateSchedules();
                    break;
                case "4":
                    promoteStudents();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid!");
            }
        }
    }

    private static void applicantsMenu() {
        System.out.println("\n=== APPLICANTS MANAGEMENT ===");

        File base = new File("data/APPLICANTS");
        if (!base.exists()) {
            System.out.println("Applicants directory not found.");
            return;
        }

        List<File> applicantFiles = new ArrayList<>();
        findApplicantFiles(base, applicantFiles);

        if (applicantFiles.isEmpty()) {
            System.out.println("No applicant files found.");
            return;
        }

        class ApplicantLine {
            File source;
            String raw;
            String[] p;
        }

        List<ApplicantLine> allApplicants = new ArrayList<>();

        for (File f : applicantFiles) {
            List<String> lines = Utils.readFile(f.getPath());
            if (lines == null)
                continue;

            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;

                String dec = Utils.decryptEachField(line);
                String[] p = dec.split("\\|", -1);
                if (p.length < 19)
                    continue;

                if (!p[18].equalsIgnoreCase("PENDING"))
                    continue;

                ApplicantLine a = new ApplicantLine();
                a.source = f;
                a.raw = line;
                a.p = p;

                allApplicants.add(a);
            }
        }

        if (allApplicants.isEmpty()) {
            System.out.println("No pending applicants found.");
            return;
        }

        System.out.println("\n=== PENDING APPLICANTS ===");
        System.out.println("[ID] | [Full Name] | [College] | [Program] | [Status]");
        System.out.println("--------------------------------------------------------");
        for (ApplicantLine a : allApplicants) {
            String full = a.p[1] + " " + a.p[2] + " " + a.p[3];
            System.out.println(a.p[0] + " | " + full + " | " + a.p[14] + " | " + a.p[16] + " | " + a.p[18]);
        }

        String filter = Utils.input("\nFilter by College (COS/COE/CLA/CAFA/CIT/CIE or ALL): ").toUpperCase().trim();
        Set<String> valid = Set.of("COS", "COE", "CLA", "CAFA", "CIT", "CIE");

        if (!filter.equals("ALL") && !valid.contains(filter)) {
            System.out.println("Invalid college.");
            return;
        }

        List<ApplicantLine> filteredList = new ArrayList<>();
        for (ApplicantLine a : allApplicants) {
            if (filter.equals("ALL") || a.p[14].equalsIgnoreCase(filter)) {
                filteredList.add(a);
            }
        }

        if (filteredList.isEmpty()) {
            System.out.println("No pending applicants in this college.");
            return;
        }

        System.out.println("\n=== FILTERED PENDING APPLICANTS ===");
        for (ApplicantLine a : filteredList) {
            String full = a.p[1] + " " + a.p[2] + " " + a.p[3];
            System.out.println(a.p[0] + " | " + full + " | " + a.p[14] + " | " + a.p[16] + " | " + a.p[18]);
        }

        while (!filteredList.isEmpty()) {
            String id = Utils.input("\nEnter Applicant ID to update (0 to exit): ").trim();
            if (id.equals("0"))
                break;

            boolean found = false;

            for (ApplicantLine a : new ArrayList<>(filteredList)) {
                if (a.p[0].equalsIgnoreCase(id)) {
                    found = true;

                    String act = Utils.input("Approve (A) / Reject (R): ").toUpperCase().trim();
                    if (!act.equals("A") && !act.equals("R")) {
                        System.out.println("Invalid choice. Must be A or R.");
                        break;
                    }

                    a.p[18] = act.equals("A") ? "Passed" : "Failed";
                    String updated = String.join("|", a.p);

                    List<String> fileLines = Utils.readFile(a.source.getPath());
                    for (int i = 0; i < fileLines.size(); i++) {
                        String decLine = Utils.decryptEachField(fileLines.get(i));
                        String[] parts = decLine.split("\\|", -1);

                        if (parts[0].equalsIgnoreCase(id)) {
                            fileLines.set(i, Utils.encryptEachField(updated));
                            break;
                        }
                    }
                    Utils.writeRawFile(a.source.getPath(), fileLines);

                    filteredList.remove(a);
                    System.out.println("Applicant updated and removed from list.");

                    break;
                }
            }

            if (!found) {
                System.out.println("Applicant ID not found.");
            }

            // Remaining pending
            if (!filteredList.isEmpty()) {
                System.out.println("\n=== REMAINING PENDING APPLICANTS ===");
                for (ApplicantLine a : filteredList) {
                    String full = a.p[1] + " " + a.p[2] + " " + a.p[3];
                    System.out.println(a.p[0] + " | " + full + " | " + a.p[14] + " | " + a.p[16] + " | " + a.p[18]);
                }
            } else {
                System.out.println("All pending applicants processed.");
            }
        }
    }

    private static void findApplicantFiles(File dir, List<File> list) {
        if (dir == null || !dir.exists())
            return;

        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (File f : files) {
            if (f.isDirectory()) {
                findApplicantFiles(f, list);
            } else if (f.isFile() && f.getName().toLowerCase().endsWith("_applicants.txt")) {
                list.add(f);
            }
        }
    }

    private static final String RESIGN_FOLDER = "data/FACULTY/RESIGNATIONS";
    private static final String FACULTY_FILE = "data/FACULTY/professorsMasterList.txt";

    private static void processResignation() {

        File folder = new File(RESIGN_FOLDER);
        folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith("_resignation.txt"));
        if (files == null || files.length == 0) {
            System.out.println("No pending resignations.");
            return;
        }
        for (File f : files) {

            List<String> encLines = Utils.readFile(f.getPath());
            if (encLines == null || encLines.isEmpty())
                continue;

            for (String enc : encLines) {
                if (enc.trim().isEmpty())
                    continue;

                String dec = Utils.decryptEachField(enc);
                String[] parts = dec.split("\\|", -1);

                if (parts.length < 4)
                    continue;

                String id = parts[0];
                String name = parts[1];
                String reason = parts[2];
                String status = parts[3];

                if (!status.equalsIgnoreCase("Pending"))
                    continue;

                System.out.printf("%-12s | %-25s | %-30s | %-10s%n", id, name, reason, status);

                String act = Utils.input("Accept (A) / Reject (R): ").toUpperCase();

                if (act.equals("A")) {

                    List<String> profs = Utils.readFile(FACULTY_FILE);
                    List<String> newList = new ArrayList<>();

                    for (String line : profs) {
                        String decryptedLine = Utils.decryptEachField(line);

                        if (!decryptedLine.startsWith(id))
                            newList.add(Utils.encryptEachField(decryptedLine));
                    }

                    Utils.writeFile(FACULTY_FILE, newList);
                    status = "Accepted";

                } else if (act.equals("R")) {
                    status = "Rejected";
                } else {
                    System.out.println("Invalid action. Skipping...");
                    continue;
                }

                String newLine = id + "|" + name + "|" + reason + "|" + status;
                Utils.writeFile(f.getPath(), Collections.singletonList(Utils.encryptEachField(newLine)));

                System.out.println("Processed: " + name + " (" + status + ")");
                System.out.println();
            }
        }

        System.out.println("All pending resignations processed.");
    }

    private static void generateSchedules() {
        System.out.println("Schedule generation module not yet configured for encryption.");
    }

    private static void promoteStudents() {
        System.out.println("\n=== STUDENT PROMOTION PANEL ===");

        String currentSY = Utils.input("Current School Year (e.g., 2025-2026): ").trim();

        String[] parts = currentSY.split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);
        String nextSY = (start + 1) + "-" + (end + 1);

        File base = new File("data/STUDENTS/" + currentSY);
        if (!base.exists()) {
            System.out.println("No students found for " + currentSY);
            return;
        }

        List<File> studentFolders = new ArrayList<>();
        findStudentFolders(base, studentFolders);

        if (studentFolders.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\n=== STUDENTS (" + currentSY + ") ===");
        System.out.println("[ID] | [Year] | [College] | [Program] | [Section] | [Last Name]");

        for (File f : studentFolders) {
            File infoFile = new File(f, f.getName() + "_info.txt");
            if (!infoFile.exists())
                continue;

            List<String> enc = Utils.readFile(infoFile.getPath());
            if (enc.isEmpty())
                continue;

            String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

            System.out.printf("%s | %s | %s | %s | %s | %s%n",
                    p[0], p[14], p[15], p[16], p[20], p[1]);
        }

        String promoteChoice = Utils.input("\nPromote (A)ll or by (ID)? ").toUpperCase().trim();
        List<File> promoteList = new ArrayList<>();

        if (promoteChoice.equals("A")) {
            promoteList.addAll(studentFolders);
        } else if (promoteChoice.equals("ID")) {
            String target = Utils.input("Enter Student ID: ").trim();
            for (File f : studentFolders)
                if (f.getName().equalsIgnoreCase(target))
                    promoteList.add(f);

            if (promoteList.isEmpty()) {
                System.out.println("Student not found.");
                return;
            }
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.println("\n=== PROMOTION TYPE ===");
        System.out.println("1. Regular Promotion (Auto Next Year)");
        System.out.println("2. Irregular Promotion (Choose SY & Year Level)");
        System.out.println("3. Drop Student");
        int type = Integer.parseInt(Utils.input("Choose: "));

        switch (type) {
            case 1:
                for (File f : promoteList)
                    regularPromote(f, currentSY, nextSY);
                System.out.println("\nRegular promotion complete!");
                break;
            case 2:
                String targetSY = Utils.input("Target School Year: ").trim();
                String targetYear = Utils.input("Target Year Level: ").replace(" ", "");

                for (File f : promoteList)
                    irregularPromote(f, currentSY, targetSY, targetYear);

                System.out.println("\nIrregular promotion complete!");
                break;
            case 3:
                for (File f : promoteList)
                    dropStudent(f, currentSY);

                System.out.println("\nStudents dropped successfully!");
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void findStudentFolders(File dir, List<File> list) {
        if (!dir.exists())
            return;

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                if (f.getName().startsWith("STU"))
                    list.add(f);
                else
                    findStudentFolders(f, list);
            }
        }
    }

    private static void regularPromote(File f, String currentSY, String nextSY) {

        File infoFile = new File(f, f.getName() + "_info.txt");
        List<String> enc = Utils.readFile(infoFile.getPath());
        if (enc.isEmpty())
            return;

        String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

        p[14] = getNextYear(p[14]);

        String studentID = p[0];
        String year = p[14];
        String college = p[15];
        String program = p[16];
        String section = p[20];

        File archiveFolder = new File("data/STUDENTS/ARCHIVES/" + currentSY + "_archives/" + studentID);
        archiveFolder.mkdirs();
        copyFolder(f, archiveFolder);

        File newFolder = new File("data/STUDENTS/" + nextSY + "/" +
                year + "/" + college + "/" + program + "/" + section + "/" + studentID);
        newFolder.mkdirs();

        moveFiles(f, newFolder);

        String updated = Utils.encryptEachField(String.join("|", p));
        Utils.writeRawFile(new File(newFolder, studentID + "_info.txt").getPath(),
                Collections.singletonList(updated));

        deleteFolder(f);
    }

    private static void irregularPromote(File f, String currentSY, String targetSY, String targetYear) {

        File infoFile = new File(f, f.getName() + "_info.txt");
        List<String> enc = Utils.readFile(infoFile.getPath());
        if (enc.isEmpty())
            return;

        String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

        p[14] = targetYear;

        String studentID = p[0];
        String college = p[15];
        String program = p[16];
        String section = p[20];

        File archiveFolder = new File("data/STUDENTS/ARCHIVES/" + currentSY + "_archives/" + studentID);
        archiveFolder.mkdirs();
        copyFolder(f, archiveFolder);

        File newFolder = new File("data/STUDENTS/" + targetSY + "/" +
                targetYear + "/" + college + "/" + program + "/" + section + "/" + studentID);
        newFolder.mkdirs();

        moveFiles(f, newFolder);

        String updated = Utils.encryptEachField(String.join("|", p));
        Utils.writeRawFile(new File(newFolder, studentID + "_info.txt").getPath(),
                Collections.singletonList(updated));

        deleteFolder(f);
    }

    private static void dropStudent(File f, String currentSY) {

        String studentID = f.getName();

        File dropFolder = new File("data/STUDENTS/ARCHIVES/DROPPED_STUDENTS/" + studentID);
        dropFolder.mkdirs();

        copyFolder(f, dropFolder);

        deleteFolder(f);
    }

    private static void moveFiles(File src, File dest) {
        for (File file : src.listFiles()) {
            file.renameTo(new File(dest, file.getName()));
        }
    }

    private static void copyFolder(File src, File dest) {
        dest.mkdirs();
        for (File file : src.listFiles()) {
            File target = new File(dest, file.getName());
            try {
                if (file.isDirectory()) {
                    copyFolder(file, target);
                } else {
                    Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFolder(File folder) {
        if (!folder.exists())
            return;
        for (File f : folder.listFiles()) {
            if (f.isDirectory())
                deleteFolder(f);
            f.delete();
        }
        folder.delete();
    }

    private static String getNextYear(String current) {
        switch (current.toLowerCase()) {
            case "1styear":
                return "2ndYear";
            case "2ndyear":
                return "3rdYear";
            case "3rdyear":
                return "4thYear";
            case "4thyear":
                return "Graduated";
            default:
                return current;
        }
    }
}