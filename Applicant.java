import java.io.File;
import java.util.*;

public class Applicant {

    private static final String APPLICANT_BASE = "data/APPLICANTS";
    private static final String PERMIT_DIR = "data/APPLICANTS/PERMITS";

    private String id, lastName, firstName, middleName, extensionName, age, birthdate, school;
    private String phone, email, fathersName, mothersName, address, schoolYear, yearLevel;
    private String college, program, password, status, examPermit;

    public Applicant() {
        ensureFolders();
    }

    private Applicant(List<String> fields) {
        this.id = getSafe(fields, 0);
        this.lastName = getSafe(fields, 1);
        this.firstName = getSafe(fields, 2);
        this.middleName = getSafe(fields, 3);
        this.extensionName = getSafe(fields, 4);
        this.age = getSafe(fields, 5);
        this.birthdate = getSafe(fields, 6);
        this.school = getSafe(fields, 7);
        this.phone = getSafe(fields, 8);
        this.email = getSafe(fields, 9);
        this.fathersName = getSafe(fields, 10);
        this.mothersName = getSafe(fields, 11);
        this.address = getSafe(fields, 12);
        this.schoolYear = getSafe(fields, 13);
        this.yearLevel = getSafe(fields, 14);
        this.college = getSafe(fields, 15);
        this.program = getSafe(fields, 16);
        this.password = getSafe(fields, 17);
        this.status = getSafe(fields, 18);
        this.examPermit = getSafe(fields, 19);
    }

    private String getSafe(List<String> list, int idx) {
        if (list == null)
            return "";
        if (idx < 0 || idx >= list.size())
            return "";
        return list.get(idx);
    }

    private void ensureFolders() {
        String[] years = { "1stYear", "2ndYear", "3rdYear", "4thYear" };
        String[] colleges = { "COE", "CIE", "CLA", "COS", "CAFA", "CIT" };

        Map<String, String[]> programs = new HashMap<>();
        programs.put("COE", new String[] { "BSCE", "BSEE", "BSME" });
        programs.put("CIE", new String[] { "BSIE", "BTVTE" });
        programs.put("CLA", new String[] { "BSBM", "BSE", "BSHM" });
        programs.put("COS", new String[] { "BSIT", "BSCS", "BSIS", "BSES", "BASLT" });
        programs.put("CAFA", new String[] { "BSA", "BFA", "BGT" });
        programs.put("CIT", new String[] { "BSFT", "BET" });

        for (String y : years) {
            for (String c : colleges) {
                Utils.ensureDir(APPLICANT_BASE + "/" + y + "/" + c);
            }
        }

        Utils.ensureDir(PERMIT_DIR);
    }

    public void signUpInteractive() {
        System.out.println("\n--- APPLICANT SIGN UP ---");
        List<String> info = new ArrayList<>();

        info.add(Utils.input("Last Name: "));
        info.add(Utils.input("First Name: "));
        info.add(Utils.input("Middle Name: "));
        info.add(Utils.input("Extension Name: "));
        info.add(Utils.input("Age: "));
        info.add(Utils.input("Birthdate (MM/DD/YYYY): "));
        info.add(Utils.input("School: "));
        info.add(Utils.input("Phone Number: "));
        info.add(Utils.input("Email Address: "));
        info.add(Utils.input("Father's Name: "));
        info.add(Utils.input("Mother's Name: "));
        info.add(Utils.input("Address: "));
        info.add(Utils.input("School Year: "));
        info.add(Utils.input("Year Level (1stYear/2ndYear/3rdYear/4thYear): "));
        info.add(Utils.input("College (COE/CIE/CLA/COS/CAFA/CIT): "));
        info.add(Utils.input("Program (e.g. BSIT): "));
        info.add(Utils.input("Password: "));

        Applicant a = signUp(info);
        if (a != null) {
            System.out.println("\nRegistered successfully.");
            System.out.println("Applicant ID: " + a.id);

            menu(a.id);
        } else {
            System.out.println("\nRegistration failed.");
        }
    }

    public String loginInteractive() {
        System.out.println("\n--- APPLICANT LOGIN ---");
        String inputId = Utils.input("Applicant ID: ");
        String inputPass = Utils.input("Password: ");
        if (login(inputId, inputPass)) {
            System.out.println("Login successful.");
            return inputId;
        } else {
            System.out.println("Invalid ID or password.");
            return null;
        }
    }

    public Applicant signUp(List<String> info) {
        try {
            while (info.size() < 17)
                info.add("");

            String newId = generateApplicantID();
            String status = "PENDING";
            String permit = "EX-" + newId.substring(3);

            String yearLevel = info.get(13);
            String college = info.get(14);
            String program = info.get(15);

            List<String> fields = new ArrayList<>();
            fields.add(newId);
            fields.addAll(info);
            fields.add(status);
            fields.add(permit);
            String path = APPLICANT_BASE + "/" + yearLevel + "/" + college + "/" + program + "_APPLICANTS.txt";
            String line = String.join("|", fields);
            Utils.appendToFile(path, line);

            return getApplicantById(newId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean login(String inputId, String inputPass) {
        try {
            File base = new File(APPLICANT_BASE);
            for (File yearFolder : base.listFiles()) {
                if (!yearFolder.isDirectory())
                    continue;
                for (File collegeFolder : yearFolder.listFiles()) {
                    if (!collegeFolder.isDirectory())
                        continue;
                    for (File appFile : collegeFolder.listFiles()) {
                        if (!appFile.getName().endsWith("_APPLICANTS.txt"))
                            continue;

                        List<String> lines = Utils.readFile(appFile.getPath());
                        if (lines == null)
                            continue;

                        for (String enc : lines) {
                            String dec = Utils.decryptEachField(enc);
                            String[] p = dec.split("\\|", -1);

                            if (p.length < 18)
                                continue;

                            if (p[0].trim().equals(inputId.trim()) && p[17].trim().equals(inputPass.trim())) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void menu(String applicantId) {
        Applicant loaded = getApplicantById(applicantId);
        if (loaded == null) {
            System.out.println("Applicant not found.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== APPLICANT DASHBOARD ===");
            System.out.println("Welcome, " + loaded.firstName + " " + loaded.lastName + " (" + loaded.id + ")");
            System.out.println("1. View Information");
            System.out.println("2. View Permit");
            System.out.println("3. View Status");
            System.out.println("4. Logout");
            System.out.print("Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewInformation(loaded);
                    break;
                case "2":
                    viewPermit(loaded.id);
                    break;
                case "3":
                    viewStatus(loaded);
                    loaded = getApplicantById(applicantId);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void viewInformation(Applicant a) {
        System.out.println("\n=== YOUR INFORMATION ===");
        System.out.println("ID: " + a.id);
        System.out.println("Last Name: " + a.lastName);
        System.out.println("First Name: " + a.firstName);
        System.out.println("Middle Name: " + a.middleName);
        System.out.println("Extension Name: " + a.extensionName);
        System.out.println("Age: " + a.age);
        System.out.println("Birthdate: " + a.birthdate);
        System.out.println("School: " + a.school);
        System.out.println("Phone: " + a.phone);
        System.out.println("Email: " + a.email);
        System.out.println("Father's Name: " + a.fathersName);
        System.out.println("Mother's Name: " + a.mothersName);
        System.out.println("Address: " + a.address);
        System.out.println("School Year: " + a.schoolYear);
        System.out.println("Year Level: " + a.yearLevel);
        System.out.println("College: " + a.college);
        System.out.println("Program: " + a.program);
        ;
        System.out.println("Status: " + a.status);
    }

    private void viewPermit(String applicantId) {
        try {
            String permitFile = "data/APPLICANTS/PERMITS/permits.txt";
            File file = new File(permitFile);
            Utils.ensureDir(file.getParentFile().getPath());

            List<String> saved = Utils.readFile(permitFile);
            boolean found = false;

            if (saved != null) {
                for (String line : saved) {
                    if (line.startsWith(applicantId + "|")) {
                        System.out.println("\n--- Exam Permit ---");
                        String[] parts = line.split("\\|", -1);
                        System.out.println("Applicant: " + parts[1] + " " + parts[2] + " (" + parts[0] + ")");
                        System.out.println("Exam Date: " + parts[3]);
                        System.out.println("Time: " + parts[4]);
                        System.out.println("Room: " + parts[5]);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                Applicant a = getApplicantById(applicantId);
                if (a == null) {
                    System.out.println("Applicant not found.");
                    return;
                }

                String date = "2025-12-" + (10 + new Random().nextInt(10));
                String time = (8 + new Random().nextInt(3)) + ":00 AM";
                String room = "Room " + (100 + new Random().nextInt(50));

                String permitLine = String.join("|",
                        applicantId, a.firstName, a.lastName, date, time, room);

                Utils.appendToFile(permitFile, permitLine);

                System.out.println("\n--- Exam Permit ---");
                System.out.println("Applicant: " + a.firstName + " " + a.lastName + " (" + a.id + ")");
                System.out.println("Exam Date: " + date);
                System.out.println("Time: " + time);
                System.out.println("Room: " + room);
            }

        } catch (Exception e) {
            System.out.println("Error while loading permit: " + e.getMessage());
        }
    }

    private void viewStatus(Applicant a) {
        Scanner sc = new Scanner(System.in);
        String currStatus = a.status == null ? "UNKNOWN" : a.status.toUpperCase();

        switch (currStatus) {
            case "PENDING":
                System.out.println("\n === Current Status ===\n");
                System.out.println(a.status);
                System.out.println("\nYour application is currently under review.");
                break;
            case "REJECTED":
                System.out.println("\n === Current Status ===\n");
                System.out.println(a.status);
                System.out.println("\nWe regret to inform you that your application was not successful this time.");
                ;
                break;
            case "PASSED":
                System.out.println("\nCongratulations!");
                System.out.println("You have PASSED the entrance examination.");
                System.out.println();
                System.out.println("Would you like to:");
                System.out.println("1. ACCEPT the admission offer");
                System.out.println("2. DECLINE the admission offer");
                System.out.print("Enter choice (1 or 2): ");

                String ch = sc.nextLine().trim();

                if (ch.equals("1")) {
                    updateStatus(a.getId(), "ACCEPTED");
                    System.out.println("\nYou have ACCEPTED the admission offer.\nWelcome to the University!");
                    createStudentAccount(a);
                } else if (ch.equals("2")) {
                    updateStatus(a.getId(), "DECLINED");
                    System.out.println("\nYou have DECLINED the admission offer.\nWe respect your decision.");
                } else {
                    System.out.println("\nInvalid choice.");
                }
                break;
            case "ACCEPTED":
                System.out.println("\nAlready accepted.");
                break;
            case "DECLINED":
                System.out.println("\nAlready declined.");
                break;
            default:
                System.out.println("\nUnknown status.");
                break;
        }
    }

    private void createStudentAccount(Applicant a) {

        String year = a.yearLevel.replace(" ", "");
        String college = a.college.toUpperCase();
        String program = a.program.toUpperCase();
        String schoolyear = a.schoolYear.replace(" ", "");

        String programFolderPath = "data/STUDENTS/" + schoolyear + "/" + year + "/" + college + "/" + program;
        Utils.ensureDir(programFolderPath);

        String section = "A";
        File sectionA = new File(programFolderPath + "/A");
        File sectionB = new File(programFolderPath + "/B");

        int countA = sectionA.exists() ? sectionA.listFiles().length : 0;
        int countB = sectionB.exists() ? sectionB.listFiles().length : 0;

        if (countA < 50) {
            section = "A";
        } else if (countB < 50) {
            section = "B";
        } else {
            System.out.println("Program " + program + " already has 100 students (A+B). Cannot add more.");
            return;
        }

        String sectionPath = programFolderPath + "/" + section;
        Utils.ensureDir(sectionPath);

        File sectionFolder = new File(sectionPath);
        String studentId = generateStudentId(sectionFolder);

        String ln = a.lastName == null ? "" : a.lastName.toUpperCase().replaceAll("\\s+", "");
        String mmdd = "0000";
        try {
            String[] d = a.birthdate.split("/");
            if (d.length >= 2)
                mmdd = d[0] + d[1];
        } catch (Exception ignored) {
        }
        String studentPass = ln + mmdd;

        List<String> fields = new ArrayList<>();
        fields.add(studentId);
        fields.add(a.lastName);
        fields.add(a.firstName);
        fields.add(a.middleName);
        fields.add(a.extensionName);
        fields.add(a.age);
        fields.add(a.birthdate);
        fields.add(a.school);
        fields.add(a.phone);
        fields.add(a.email);
        fields.add(a.fathersName);
        fields.add(a.mothersName);
        fields.add(a.address);
        fields.add(a.schoolYear);
        fields.add(a.yearLevel);
        fields.add(a.college);
        fields.add(a.program);
        fields.add("ENROLLED");
        fields.add(a.examPermit);
        fields.add(studentPass);
        fields.add(section);

        String studentFolderPath = sectionPath + "/" + studentId;
        Utils.ensureDir(studentFolderPath);

        String filePath = studentFolderPath + "/" + studentId + "_info.txt";
        String plainLine = String.join("|", fields);
        Utils.writeFile(filePath, Collections.singletonList(plainLine));

        System.out.println("\n=== STUDENT ACCOUNT GENERATED ===");
        System.out.println("Student ID: " + studentId);
        System.out.println("Default Password: " + studentPass);
        System.out.println("Assigned Section: " + section);
    }

    private static String generateStudentId(File programFolder) {

        int maxId = 0;

        File[] sections = programFolder.listFiles();
        if (sections == null)
            return "STU0001";

        for (File section : sections) {
            if (!section.isDirectory())
                continue;

            File studentFile = new File(
                    section,
                    section.getName().toUpperCase() + "-students.txt");

            if (!studentFile.exists())
                continue;

            List<String> lines = Utils.readFile(studentFile.getPath());
            if (lines == null)
                continue;

            for (String enc : lines) {
                String dec = Utils.decryptEachField(enc);
                String[] p = dec.split("\\|", -1);

                if (p.length < 1)
                    continue;

                String id = p[0].trim();

                if (id.startsWith("STU")) {
                    try {
                        int n = Integer.parseInt(id.substring(3));
                        if (n > maxId)
                            maxId = n;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        int next = maxId + 1;
        return String.format("STU%04d", next);
    }

    private boolean updateStatus(String applicantId, String newStatus) {
        try {
            Applicant a = getApplicantById(applicantId);
            if (a == null)
                return false;

            String year = a.getYearLevel();
            String college = a.getCollege();
            String program = a.getProgram();

            String filePath = APPLICANT_BASE + "/" + year + "/" + college + "/" + program + "_APPLICANTS.txt";

            List<String> lines = Utils.readFile(filePath);
            if (lines == null)
                return false;

            List<String> updated = new ArrayList<>();

            for (String encLine : lines) {

                String dec = Utils.decryptEachField(encLine);
                String[] p = dec.split("\\|", -1);

                if (p.length < 20) {
                    updated.add(encLine);
                    continue;
                }

                if (!p[0].equals(applicantId)) {
                    updated.add(encLine);
                    continue;
                }

                p[18] = newStatus;

                String fixed = String.join("|", p);
                String newEncrypted = Utils.encryptEachField(fixed);

                updated.add(newEncrypted);
            }

            Utils.writeRawFile(filePath, updated);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Applicant getApplicantById(String id) {
        try {
            File base = new File(APPLICANT_BASE);
            for (File y : base.listFiles()) {
                if (!y.isDirectory())
                    continue;
                for (File c : y.listFiles()) {
                    if (!c.isDirectory())
                        continue;
                    for (File f : c.listFiles()) {
                        if (!f.getName().endsWith("_APPLICANTS.txt"))
                            continue;

                        List<String> lines = Utils.readFile(f.getPath());
                        if (lines == null)
                            continue;

                        for (String enc : lines) {
                            String dec = Utils.decryptEachField(enc);
                            String[] p = dec.split("\\|", -1);

                            if (p[0].equals(id))
                                return new Applicant(Arrays.asList(p));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateApplicantID() {
        int maxId = 0;
        File base = new File(APPLICANT_BASE);
        if (!base.exists())
            return "APP0001";

        for (File year : base.listFiles()) {
            if (!year.isDirectory())
                continue;
            for (File college : year.listFiles()) {
                if (!college.isDirectory())
                    continue;
                for (File f : college.listFiles()) {
                    if (!f.getName().endsWith("_APPLICANTS.txt"))
                        continue;

                    List<String> lines = Utils.readFile(f.getPath());
                    if (lines == null)
                        continue;

                    for (String enc : lines) {
                        String dec = Utils.decryptEachField(enc);
                        String[] p = dec.split("\\|", -1);

                        if (p.length == 0)
                            continue;
                        String id = p[0];

                        if (id.startsWith("APP")) {
                            try {
                                int num = Integer.parseInt(id.substring(3));
                                if (num > maxId)
                                    maxId = num;
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        }
        return String.format("APP%04d", maxId + 1);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getFullName() {
        return firstName + " " + (middleName.isEmpty() ? "" : middleName + " ") + lastName;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public String getCollege() {
        return college;
    }

    public String getProgram() {
        return program;
    }

}