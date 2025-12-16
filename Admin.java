import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class Admin {

    private static final String ADMIN_CREDENTIALS = "data/ADMIN/admin.txt";
    private static final String MASTER_FACULTY_SCHEDULE_FILE = "data/FACULTY" + File.separator
            + "MASTER_FACULTY_SCHEDULE.txt";
    private static final String FACULTY_DIR = "data/FACULTY";
    private static final String FACULTY_MASTER = FACULTY_DIR + File.separator + "ProfessorsMasterList.txt";
    private static final String FACULTY_SCHED_PROF = FACULTY_DIR + File.separator + "FACULTY_MEMBERS";
    private static final String COURSES_DIR = FACULTY_DIR + File.separator + "COURSES";

    public static class ApplicantLine {
        public File source;
        public String raw;
        public String[] p;
    }

    private static List<ApplicantLine> guiAppList = new ArrayList<>();

    public static void setGuiAppList(List<ApplicantLine> list) {
        guiAppList = list;
    }

    public static List<ApplicantLine> getGuiAppList() {
        return guiAppList;
    }

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

    private static String guiUsername = null;
    private static String guiPassword = null;

    public static void setGuiUsername(String u) {
        guiUsername = u;
    }

    public static void setGuiPassword(String p) {
        guiPassword = p;
    }

    public static boolean login() {
        initAdminFile();
        boolean console = false;

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

        String inputUser;
        String inputPass;

        if (guiUsername != null && guiPassword != null) {
            inputUser = guiUsername;
            inputPass = guiPassword;

            guiUsername = null;
            guiPassword = null;
        } else {
            console = true;
            inputUser = Utils.input("Enter Admin Username: ");
            inputPass = Utils.input("Enter Admin Password: ");
        }

        String hashedInput = Utils.hashPassword(inputPass);

        if (!inputUser.equals(storedUser) || !hashedInput.equals(storedHash)) {
            System.out.println("Wrong username or password!");
            return false;
        }

        System.out.println("\nWelcome, " + storedRole + "");
        if (console) {
            adminMenu();
            return true;
        }
        return true;
    }

    static String chset = null;

    public static void setGuiCh(String x) {
        chset = x;
    };

    static void adminMenu() {

        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Applicants Management");
            System.out.println("2. Resignation Approvals");
            System.out.println("3. Schedule Management");
            System.out.println("4. Student Promotion");
            System.out.println("5. Log Out");

            String ch;
            if (chset != null) {
                ch = chset;
            } else {
                ch = Utils.input("Choice: ");
            }

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

    private static boolean textExist = false;
    private static String IdFromUi = "";

    static void settextGuiExist(boolean x) {
        textExist = x;
    }

    private static boolean getTextGuiExist() {
        return textExist;
    };

    static void applicantsMenu() {
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
        List<Admin.ApplicantLine> guiList = new ArrayList<>();
        for (ApplicantLine a : allApplicants) {
            Admin.ApplicantLine outer = new Admin.ApplicantLine();
            outer.source = a.source;
            outer.raw = a.raw;
            outer.p = a.p;
            guiList.add(outer);
        }
        Admin.setGuiAppList(guiList);

        System.out.println("\n=== PENDING APPLICANTS ===");
        System.out.println("[ID] | [Full Name] | [College] | [Program] | [Status]");
        System.out.println("--------------------------------------------------------");
        for (ApplicantLine a : allApplicants) {
            String full = a.p[1] + " " + a.p[2] + " " + a.p[3];
            System.out.println(a.p[0] + " | " + full + " | " + a.p[15] + " | " + a.p[16] + " | " + a.p[18]);
        }
        Set<String> valid = Set.of("COS", "COE", "CLA", "CAFA", "CIT", "CIE");
        String filter = "";
        if (!textExist) {
            filter = Utils.input("\nFilter by College (COS/COE/CLA/CAFA/CIT/CIE or ALL): ").toUpperCase().trim();
        } else {
            filter = "ALL";
        }

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
            System.out.println(a.p[0] + " | " + full + " | " + a.p[15] + " | " + a.p[16] + " | " + a.p[18]);
        }

        while (!filteredList.isEmpty()) {
            String id = "";
            if (getTextGuiExist()) {
                id = AdminDashboardUI.getIdfromui();
            } else {
                id = Utils.input("\nEnter Applicant ID to update (0 to exit): ").trim();
                if (id.equals("0"))
                    break;
            }

            boolean found = false;

            for (ApplicantLine a : new ArrayList<>(filteredList)) {
                if (a.p[0].equalsIgnoreCase(id)) {
                    found = true;

                    String act = "";
                    if (textExist) {
                        act = AdminDashboardUI.getchfromui();
                    } else {
                        act = Utils.input("Approve (A) / Reject (R): ").toUpperCase().trim();
                    }
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

    static void findApplicantFiles(File dir, List<File> list) {
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

    static final String RESIGN_FOLDER = "data/FACULTY/RESIGNATIONS";
    private static final String FACULTY_FILE = "data/FACULTY/professorsMasterList.txt";

    static String actGui = null;
    static List<String> resListGui = new ArrayList<>();

    static String getActGui() {
        return actGui;
    }

    static void setActGui(String x) {
        actGui = x;
    }

    static List<String> getResList() {
        return resListGui;
    }

    static void setResList(List<String> resLists) {
        resListGui = resLists;
    }

    static void processResignation() {

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

            setResList(encLines);

            if (!resListGui.isEmpty()) {
                return;
            }

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

                String act = "";

                if (actGui != null) {
                    act = actGui;
                } else {
                    act = Utils.input("Accept (A) / Reject (R): ").toUpperCase();
                }

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

    public static List<ApplicantLine> getApplicantsForGUI(String collegeFilter, String yearLevelFilter) {
        File base = new File("data/APPLICANTS");
        List<ApplicantLine> guiList = new ArrayList<>();

        if (!base.exists())
            return guiList; // empty list if no folder

        List<File> applicantFiles = new ArrayList<>();
        findApplicantFiles(base, applicantFiles);

        for (File f : applicantFiles) {
            List<String> lines = Utils.readFile(f.getPath());
            if (lines == null)
                continue;

            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;

                String dec = Utils.decryptEachField(line);
                String[] p = dec.split("\\|", -1);
                if (p.length < 21)
                    continue;

                ApplicantLine a = new ApplicantLine();
                a.source = f;
                a.raw = line;
                a.p = p;

                boolean collegeMatch = (collegeFilter == null || collegeFilter.equalsIgnoreCase("ALL")
                        || p[14].equalsIgnoreCase(collegeFilter));
                boolean yearMatch = (yearLevelFilter == null || yearLevelFilter.equalsIgnoreCase("ALL")
                        || p[15].equalsIgnoreCase(yearLevelFilter)); // assuming p[15] = year level

                if (collegeMatch && yearMatch) {
                    guiList.add(a);
                }
            }
        }

        setGuiAppList(guiList); // update GUI list
        return guiList;
    }

    private static String syfromgui = "";
    private static String yearlevelfromgui = "";
    private static String deptfromgui = "";
    private static String coursefromgui = "";
    private static String secfromgui = "";

    void setFields(String sy, String yr, String dept, String course, String sec) {
        syfromgui = sy;
        yearlevelfromgui = yr;
        deptfromgui = dept;
        coursefromgui = course;
        secfromgui = sec;
    }

    static void generateSchedules() {
        int choice;
        if (syfromgui.isEmpty() && yearlevelfromgui.isEmpty() && deptfromgui.isEmpty() && coursefromgui.isEmpty()
                && secfromgui.isEmpty()) {
            System.out.println("\n=== SCHEDULE MANAGEMENT PANEL ===");
            System.out.println("1. Generate Schedule");
            System.out.println("2. Check Request");
            System.out.println("3. Back");
            choice = Utils.inputInt("Enter option(1-3): ");
        } else {
            choice = 1;
        }

        switch (choice) {
            case 1:
                String sy = "";
                String lvl = "";
                String dept = "";
                String course = "";
                String sec = "";
                if (!syfromgui.isEmpty() && !yearlevelfromgui.isEmpty() && !deptfromgui.isEmpty()
                        && !coursefromgui.isEmpty() && !secfromgui.isEmpty()) {
                    sy = syfromgui;
                    lvl = yearlevelfromgui;
                    dept = deptfromgui;
                    course = coursefromgui;
                    sec = secfromgui;
                } else {
                    System.out.println("\n=== SCHEDULE GENERATOR ===");
                    sy = Utils.input("Enter Academic Year: ");
                    lvl = Utils.input("Enter Year Level: ");
                    dept = Utils.input("Enter Department(e.g. CIE): ");
                    course = Utils.input("Enter Course(e.g. BSIS): ");
                    sec = Utils.input("Enter Section(e.g. A, B): ");
                }

                CreateSchedule(lvl, course, sec, sy, dept);
                break;

            case 2:
                viewReq();
                break;
            case 3:
                return;
        }

    }

    private static void CreateSchedule(String yearLevel, String course, String section, String sy, String collegedept) {
        File profMasterFile = new File(MASTER_FACULTY_SCHEDULE_FILE);
        List<String> majorsAndMinors = Utils.readFile(FACULTY_MASTER);
        Collections.shuffle(majorsAndMinors);
        // for(String line : profMasterFile)

        List<String> getRandomSub = majorsAndMinors.stream().limit(8).collect(Collectors.toList());
        // index: 0234
        for (String line : getRandomSub) {
            String parts[] = line.split("\\|");
            String profID = parts[0];
            String profName = parts[2];
            String dept = parts[3];
            String type = parts[4];

            List<String[]> currentSchedule = getProfScheduleFromMaster(profID);
            String day, time;
            do {
                day = Admin.getDay();
                time = Admin.createTime();
            } while (hasConflict(currentSchedule, day, time));

            String subject = "";

            if (type.equals("MAJOR")) {
                String path = COURSES_DIR + "/" + dept + "_" + type + ".txt";
                subject = Admin.getSubject(path);
            } else {
                String path = COURSES_DIR + "/" + type + ".txt";
                subject = Admin.getSubject(path);
            }

            File profFile = new File(FACULTY_SCHED_PROF + "/" + profID + "/" + profID + "_Schedule.txt");
            if (!profFile.exists()) {
                try {
                    profFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            profFile.getParentFile().mkdirs();
            try (FileWriter saveSchedToProf = new FileWriter(profFile, true)) {
                saveSchedToProf.write(yearLevel + "|" + course + "|" + section + "|" + subject + "|" + day + "|" + time
                        + "|" + sy + "|" + collegedept + "\n");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            // save to master file
            try (FileWriter saveSchedToProf = new FileWriter(profMasterFile, true)) {
                saveSchedToProf.write(profID + "|" + yearLevel + "|" + course + "|" + section + "|" + subject + "|"
                        + day + "|" + time + "|" + sy + "|" + collegedept + "\n");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    private static String createTime() {
        Random rand = new Random();
        int startTime = rand.nextInt(19 - 7 + 1) + 7;
        int dur = rand.nextInt(3) + 1;
        int endTime = startTime + dur;
        String finalTime = startTime + "-" + endTime;
        return finalTime;
    }

    private static String getSubject(String path) {
        List<String> subjects = Utils.readFile(path);
        Random rand = new Random();
        String subject = subjects.get(rand.nextInt(subjects.size()));
        return subject;
    }

    private static String getDay() {
        String[] days = { "MON", "TUE", "WED", "THURS", "FRI", "SAT" };
        Random rand = new Random();
        String randomDay = days[rand.nextInt(days.length)];
        return randomDay;
    }

    private static List<String[]> getProfScheduleFromMaster(String profID) {
        List<String[]> schedule = new ArrayList<>();
        List<String> lines = Utils.readFile(MASTER_FACULTY_SCHEDULE_FILE);
        if (lines == null)
            return schedule;

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts[0].equalsIgnoreCase(profID)) {
                schedule.add(parts);
            }
        }
        return schedule;
    }

    private static boolean hasConflict(List<String[]> schedule, String day, String time) {
        String[] newTimeParts = time.split("-");
        int newStart = Integer.parseInt(newTimeParts[0]);
        int newEnd = Integer.parseInt(newTimeParts[1]);

        for (String[] s : schedule) {
            String scheduledDay = s[3];
            if (!scheduledDay.equalsIgnoreCase(day))
                continue;

            String[] existingTimeParts = s[4].split("-");
            int existingStart = Integer.parseInt(existingTimeParts[0]);
            int existingEnd = Integer.parseInt(existingTimeParts[1]);
            // OVERLAP CHECKER
            if (newStart < existingEnd && newEnd > existingStart) {
                return true;
            }
        }
        return false;
    }

    private static int choiceFromGui = 0;
    private static int numFromGui = 0;

    static void setChoiceFromGui(int x) {
        choiceFromGui = x;
    }

    private static int getNumFromGui() {
        return numFromGui;
    }

    private static final String COURSE_REQUEST_FILE = "data/FACULTY/COURSE_REQUESTS/course_request.txt";

    static void viewReq() {
        File reqFile = new File(COURSE_REQUEST_FILE);
        if (!reqFile.exists()) {
            System.out.println("No pending requests.");
            return;
        }

        List<String> requests = Utils.readFile(COURSE_REQUEST_FILE);
        if (requests == null || requests.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        System.out.println("ROW | PROF_ID | NAME | REASON | STATUS");
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + " | " + requests.get(i));
        }

        System.out.println("1. Approve a Request");
        System.out.println("2. Reject a Request");
        System.out.println("3. back");
        int choice = Utils.inputInt("Enter option(1-3): ");

        if (choice == 3) {
            return;
        }

        if (choice == 2) {
            int rowToReject = Utils.inputInt("Enter row number to reject: ") - 1;
            if (rowToReject < 0 || rowToReject >= requests.size()) {
                System.out.println("Invalid row.");
                return;
            }

            String lineToReject = requests.get(rowToReject);
            String[] partsToReject = lineToReject.split("\\|");
            if (partsToReject.length < 4) {
                System.out.println("Malformed request.");
                return;
            }

            String profIdToReject = partsToReject[0].trim();
            updateRequestStatus(profIdToReject, "REJECTED");
            System.out.println("Request REJECTED.");
            return;
        }

        int row = Utils.inputInt("Enter row number to approve: ") - 1;
        if (row < 0 || row >= requests.size()) {
            System.out.println("Invalid row.");
            return;
        }

        String line = requests.get(row);
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            System.out.println("Malformed request.");
            return;
        }

        String profId = parts[0].trim();

        boolean success = approveAndCreateSchedule(profId);

        if (success) {
            updateRequestStatus(profId, "APPROVED");
            System.out.println("Request APPROVED. Schedule created.");
        } else {
            updateRequestStatus(profId, "REJECTED");
            System.out.println("Request REJECTED. No students found or conflict detected.");
        }
    }

    private static void updateRequestStatus(String profId, String status) {
        List<String> lines = Utils.readFile(COURSE_REQUEST_FILE);
        if (lines == null)
            return;

        List<String> updated = new ArrayList<>();
        for (String l : lines) {
            String[] parts = l.split("\\|");
            if (parts[0].trim().equalsIgnoreCase(profId)) {
                updated.add(parts[0] + "|" + parts[1] + "|" + parts[2] + "|" + status);
            } else {
                updated.add(l);
            }
        }
        Utils.writeRawFile(COURSE_REQUEST_FILE, updated);
    }

    static boolean approveAndCreateSchedule(String profId) {
        File studentsBase = new File("data/STUDENTS");
        if (!studentsBase.exists())
            return false;

        File[] schoolYears = studentsBase.listFiles(File::isDirectory);
        if (schoolYears == null)
            return false;

        for (File syFolder : schoolYears) {
            String sy = syFolder.getName();

            for (File yearFolder : syFolder.listFiles(File::isDirectory)) {
                String yearLevel = yearFolder.getName();

                for (File deptFolder : yearFolder.listFiles(File::isDirectory)) {

                    for (File courseFolder : deptFolder.listFiles(File::isDirectory)) {

                        for (File sectionFolder : courseFolder.listFiles(File::isDirectory)) {

                            // Traverse NW-* folders
                            for (File nwFolder : sectionFolder
                                    .listFiles(f -> f.isDirectory() && f.getName().startsWith("NW-"))) {

                                File[] infoFiles = nwFolder
                                        .listFiles(f -> f.isFile() && f.getName().endsWith("_info.txt"));
                                if (infoFiles == null || infoFiles.length == 0)
                                    continue;

                                // Pick first _info.txt file
                                File infoFile = infoFiles[0];

                                // Parse department, course, section, year level from folder structure
                                String dept = deptFolder.getName();
                                String course = courseFolder.getName();
                                String section = sectionFolder.getName();

                                List<String[]> currentSchedule = getProfScheduleFromMaster(profId);
                                String day, time;
                                do {
                                    day = getDay();
                                    time = createTime();
                                } while (hasConflict(currentSchedule, day, time));

                                String subject = getRandomSubject(dept);

                                // Save schedule
                                saveSchedule(profId, yearLevel, course, section, subject, day, time, sy, dept);
                                return true; // Approved successfully
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void saveSchedule(
            String profId,
            String year,
            String course,
            String section,
            String subject,
            String day,
            String time,
            String sy,
            String dept) {

        File profFile = new File(FACULTY_SCHED_PROF + "/" + profId + "/" + profId + "_Schedule.txt");
        profFile.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(profFile, true)) {
            fw.write(year + "|" + course + "|" + section + "|" + subject + "|" + day + "|" + time + "|" + sy + "|"
                    + dept + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(MASTER_FACULTY_SCHEDULE_FILE, true)) {
            fw.write(profId + "|" + year + "|" + course + "|" + section + "|" + subject + "|" + day + "|" + time + "|"
                    + sy + "|" + dept + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRandomSubject(String dept) {
        String path = COURSES_DIR + "/" + dept + "_MAJOR.txt";
        List<String> subjects = Utils.readFile(path);
        if (subjects == null || subjects.isEmpty())
            return "TBA";
        return subjects.get(new Random().nextInt(subjects.size()));
    }

    static void deleteRequest(int index) {
        String path = "data/FACULTY/COURSE_REQUESTS/course_request.txt";
        List<String> lines = Utils.readFile(path);
        if (lines == null || index < 0 || index >= lines.size()) {
            System.out.println("Invalid index or file empty, cannot delete request.");
            return;
        }

        lines.remove(index);
        Utils.writeRawFile(path, lines);
        System.out.println("Request removed from course_request.txt");
    }

    private static String schoolYear = "";
    private static String promotechoicefromgui = "";
    private static String promotiontype = "";
    private static String targetSy = "";
    private static String academicYear;

    public static void setAcademicYear(String ay) {
        academicYear = ay;
    }

    public static String getAcademicYear() {
        return academicYear;
    }

    public static String getSchoolYear() {
        return schoolYear;
    }

    public static void setSchoolYear(String v) {
        schoolYear = v;
    }

    public static String getPromotechoicefromgui() {
        return promotechoicefromgui;
    }

    public static void setPromotechoicefromgui(String v) {
        promotechoicefromgui = v;
    }

    public static String getPromotiontype() {
        return promotiontype;
    }

    public static void setPromotiontype(String v) {
        promotiontype = v;
    }

    public static String getTargetSy() {
        return targetSy;
    }

    public static void setTargetSy(String v) {
        targetSy = v;
    }

    static void promoteStudents() {
        System.out.println("\n=== STUDENT PROMOTION PANEL ===");
        String currentSY;

        if (schoolYear != null && !schoolYear.isEmpty()) {
            currentSY = getSchoolYear();
        } else {
            currentSY = Utils.input("Current School Year (e.g., 2025-2026): ").trim();
        }
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
        System.out.println("[ID] | [Year] | [College] | [Program] | [Section] | [Last Name] | [GWA] | [REMARKS]");

        for (File f : studentFolders) {
            File infoFile = new File(f, f.getName() + "_info.txt");
            if (!infoFile.exists())
                continue;

            List<String> enc = Utils.readFile(infoFile.getPath());
            if (enc.isEmpty())
                continue;

            // Decrypt student info
            String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

            if (p.length < 21) {
                System.out.println("Not enough fields for student: " + f.getName());
                continue;
            }

            File gradesFile = new File(f, f.getName() + "_grades.txt");

            // Decrypt student info
            List<String> encInfo = Utils.readFile(new File(f, f.getName() + "_info.txt").getPath());
            if (encInfo.isEmpty())
                continue;
            String[] infoParts = Utils.decryptEachField(encInfo.get(0)).split("\\|", -1);

            // Current subject from schedule
            String currentSubject = "YourSubjectHere"; // set from schedule iteration

            Float profGrade = null; // grade given by current professor
            List<Float> allGrades = new ArrayList<>();

            if (gradesFile.exists()) {
                for (String line : Utils.readFile(gradesFile.getPath())) {
                    line = line.trim();
                    if (line.isEmpty())
                        continue;

                    String[] gParts = line.split("~", -1);
                    if (gParts.length < 4)
                        continue;

                    String profIdFromFile = Utils.decryptEachField(gParts[0]);
                    String subjectFromFile = Utils.decryptEachField(gParts[2]);

                    try {
                        float grade = Float.parseFloat(gParts[3]);
                        allGrades.add(grade);

                        if (profIdFromFile.equals(profIdFromFile) && subjectFromFile.equals(currentSubject)) {
                            profGrade = grade;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric grade for student " + f.getName() + ": " + gParts[3]);
                    }
                }
            }

            float ave = 0;
            if (!allGrades.isEmpty()) {
                float sum = 0;
                for (float g : allGrades)
                    sum += g;
                ave = sum / allGrades.size();
            }

            String remarks = (ave > 74) ? "PASSED" : "FAILED";

            System.out.printf("%s | %s | %s | %s | %s | %s | %.2f | %s%n",
                    p[0],
                    p.length > 14 ? p[14] : "N/A",
                    p.length > 15 ? p[15] : "N/A",
                    p.length > 16 ? p[16] : "N/A",
                    p.length > 20 ? p[20] : "N/A",
                    p[1],
                    ave,
                    remarks);
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

    public static List<String[]> getStudentsForGUI(String sy, String section, String program) {
        List<String[]> students = new ArrayList<>();
        File base = new File("data/STUDENTS/" + sy);
        if (!base.exists())
            return students;

        List<File> studentFolders = new ArrayList<>();
        findStudentFolders(base, studentFolders);

        for (File f : studentFolders) {
            File infoFile = new File(f, f.getName() + "_info.txt");
            if (!infoFile.exists())
                continue;

            List<String> enc = Utils.readFile(infoFile.getPath());
            if (enc.isEmpty())
                continue;

            String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

            if (!p[14].equalsIgnoreCase(section) || !p[16].equalsIgnoreCase(program))
                continue;

            int sum = 0, count = 0;
            for (int i = 21; i <= 28; i++) {
                try {
                    sum += Integer.parseInt(p[i].trim());
                    count++;
                } catch (NumberFormatException e) {
                }
            }
            float ave = count > 0 ? (float) sum / count : 0;
            String remarks = ave > 74 ? "PASSED" : "FAILED";

            students.add(new String[] { p[0], p[14], p[16], p[1], String.valueOf(ave), remarks });
        }
        return students;
    }

    static void findStudentFolders(File dir, List<File> list) {
        if (!dir.exists())
            return;

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                if (f.getName().startsWith("NW-"))
                    list.add(f);
                else
                    findStudentFolders(f, list);
            }
        }
    }

    static void regularPromote(File f, String currentSY, String nextSY) {

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

    static void irregularPromote(File f, String currentSY, String targetSY, String targetYear) {

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

    static void dropStudent(File f, String currentSY) {

        String studentID = f.getName();

        File dropFolder = new File("data/STUDENTS/ARCHIVES/DROPPED_STUDENTS/" + studentID);
        dropFolder.mkdirs();

        copyFolder(f, dropFolder);

        deleteFolder(f);
    }

    static void moveFiles(File src, File dest) {
        for (File file : src.listFiles()) {
            try {
                System.out.println(
                        "Moving " + file.getAbsolutePath() + " -> " + new File(dest, file.getName()).getAbsolutePath());
                Files.move(file.toPath(), new File(dest, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void moveFolder(File src, File dest) {
        if (!src.exists())
            return;
        copyFolder(src, dest);
        deleteFolder(src);
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

    static void deleteFolder(File folder) {
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

    public static void updateApplicantStatus(String id, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateApplicantStatus'");
    }

    public static void setIdFromUI(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setIdFromUI'");
    }

}