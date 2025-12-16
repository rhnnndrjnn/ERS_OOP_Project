import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Student {

    static void authenticate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String extName;
    private String age, birthdate, school, phone, email;
    private String father, mother, address;
    protected String schoolYear;
    protected String yearLevel;
    protected String college;
    protected String program;
    private String status;
    private String examPermit, password;
    protected String section;

    public Student(String[] p) {
        p = normalize(p);

        id = p[0];
        lastName = p[1];
        firstName = p[2];
        middleName = p[3];
        extName = p[4];
        age = p[5];
        birthdate = p[6];
        school = p[7];
        phone = p[8];
        email = p[9];
        father = p[10];
        mother = p[11];
        address = p[12];
        schoolYear = p[13];
        yearLevel = p[14];
        college = p[15];
        program = p[16];
        status = p[17];
        examPermit = p[18];
        password = p[19];
        section = p[20];
    }

    private String[] normalize(String[] p) {
        String[] f = new String[21];
        for (int i = 0; i < 21; i++) {
            if (i < p.length)
                f[i] = (p[i] == null ? "" : p[i]);
            else
                f[i] = "";
        }
        if (f[20].trim().isEmpty())
            f[20] = "A";
        return f;
    }

    public static Student login() {
        System.out.println("\n--- STUDENT LOGIN ---");
        String id = Utils.input("Student ID: ").trim();
        String pass = Utils.input("Password: ").trim();

        Student s = authenticate(id, pass);

        if (s == null) {
            System.out.println("Error: Invalid Student ID or Password. Please try again.");
            return null;
        }

        s.menu();
        return s;
    }

    public static Student authenticate(String id, String pass) {

        File base = new File("data/STUDENTS");
        if (!base.exists()) {
            System.out.println("STUDENTS folder not found!");
            return null;
        }

        Queue<File> queue = new LinkedList<>();
        queue.add(base);

        while (!queue.isEmpty()) {
            File current = queue.poll();
            File[] files = current.listFiles();
            if (files == null)
                continue;

            for (File f : files) {

                if (f.isDirectory()) {
                    queue.add(f);
                    continue;
                }

                if (f.isFile() && f.getName().endsWith("_info.txt")) {

                    List<String> lines = Utils.readFile(f.getPath());
                    if (lines == null || lines.isEmpty())
                        continue;

                    // decrypt all fields
                    String dec = Utils.decryptEachField(lines.get(0));
                    if (dec == null || dec.isEmpty())
                        continue;

                    String[] p = dec.split("\\|", -1);
                    if (p.length < 21)
                        continue;

                    String storedId = p[0].trim();

                    // decrypt stored password
                    String storedPass = Utils.decryptValue(p[19].trim());

                    if (storedId.equals(id.trim()) && storedPass.equals(pass.trim())) {
                        return new Student(p); // SUCCESS LOGIN
                    }
                }
            }
        }

        System.out.println("Invalid ID or password!");
        return null;
    }

    public boolean saveStudent() {
        try {
            String year = yearLevel.replace(" ", "");
            String collegeDir = college.toUpperCase();
            String prog = program.toUpperCase();
            String sec = (section == null ? "A" : section.toUpperCase());

            if (!sec.equals("A") && !sec.equals("B"))
                sec = "A";

            String folder = "data/STUDENTS/" + year + "/" + collegeDir + "/" + prog + "/" + sec;
            Utils.ensureDir(folder);

            String file = folder + "/" + sec + "-students.txt";

            String[] p = normalize(new String[] {
                    id, lastName, firstName, middleName, extName,
                    age, birthdate, school, phone, email,
                    father, mother, address, schoolYear,
                    yearLevel, college, program, status,
                    examPermit, password, sec
            });

            String raw = String.join("|", p);
            Utils.appendToFile(file, Utils.encryptEachField(raw));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Student loadById(String id) {
        File base = new File("data/STUDENTS");
        if (!base.exists())
            return null;

        for (File yearFolder : base.listFiles()) {
            if (!yearFolder.isDirectory())
                continue;

            for (File collegeFolder : yearFolder.listFiles()) {
                if (!collegeFolder.isDirectory())
                    continue;

                for (File programFolder : collegeFolder.listFiles()) {
                    if (!programFolder.isDirectory())
                        continue;

                    for (File sectionFolder : programFolder.listFiles()) {
                        if (!sectionFolder.isDirectory())
                            continue;

                        String sec = sectionFolder.getName().toUpperCase();
                        File studentFile = new File(sectionFolder, sec + "-students.txt");
                        if (!studentFile.exists())
                            continue;

                        List<String> lines = Utils.readFile(studentFile.getPath());
                        if (lines == null)
                            continue;

                        for (String line : lines) {
                            String dec = Utils.decryptEachField(line);
                            String[] p = dec.split("\\|", -1);
                            if (p.length < 20)
                                continue;

                            if (p[0].trim().equals(id.trim())) {
                                String[] f = new String[21];
                                for (int i = 0; i < 21; i++) {
                                    if (i < p.length)
                                        f[i] = p[i];
                                    else
                                        f[i] = "";
                                }

                                if (f[20].trim().isEmpty())
                                    f[20] = sec;

                                return new Student(f);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== STUDENT PORTAL ===");
            System.out.println("Welcome, " + firstName + " " + lastName);
            System.out.println("1. Profile");
            System.out.println("2. Message");
            System.out.println("3. Schedule");
            System.out.println("4. Grades");
            System.out.println("5. Academic Panel");
            System.out.println("6. Logout");

            String ch = Utils.input("Select: ");

            switch (ch) {
                case "1":
                    showProfile();
                    break;
                case "2":
                    messageMenu();
                    break;
                case "3":
                    showSchedule();
                    break;
                case "4":
                    viewGrades();
                    break;
                case "5":
                    new StudentAcads(this).academicPanel();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void showProfile() {
        System.out.println("\n--- STUDENT INFORMATION ---");
        System.out.println("ID: " + id);
        System.out.println("Name: " + lastName + ", " + firstName);
        System.out.println("Program: " + program);
        System.out.println("College: " + college);
        System.out.println("Year Level: " + yearLevel);
        System.out.println("Section: " + section);
        System.out.println("Status: " + status);
    }

    private void messageMenu() {
        while (true) {
            System.out.println("\n--- MESSAGES ---");
            System.out.println("1. Inbox");
            System.out.println("2. Compose");
            System.out.println("3. Back");

            String choice = Utils.input("Select: ");

            switch (choice) {
                case "1":
                    inbox();
                    break;
                case "2":
                    compose();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void inbox() {
        String path = "data/STUDENTS/"
                + schoolYear.replace(" ", "") + "/"
                + yearLevel.replace(" ", "") + "/"
                + college.toUpperCase() + "/"
                + program.toUpperCase() + "/"
                + section + "/"
                + id + "/"
                + id + "_inbox.txt";

        List<String> lines = Utils.readFile(path);

        if (lines == null || lines.isEmpty()) {
            System.out.println("Inbox is empty.");
            return;
        }

        System.out.println("\n--- INBOX ---");
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4)
                continue;

            String fromName = parts[2];
            String dateSent = parts[1];
            String message = Utils.decrypt(parts[3]);

            System.out.println("FROM: " + fromName + " | " + dateSent);
            System.out.println(message);
            System.out.println("------------------------");
        }
    }

    private void compose() {
        String toId = Utils.input("Send To (ID): ").trim();
        Student recipient = Student.findById(toId);
        if (recipient == null) {
            System.out.println("Recipient ID not found!");
            return;
        }

        String message = Utils.input("Message: ").trim();
        if (message.isEmpty()) {
            System.out.println("Cannot send empty message.");
            return;
        }

        String date = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());

        String inboxPath = "data/STUDENTS/"
                + recipient.schoolYear.replace(" ", "") + "/"
                + recipient.yearLevel.replace(" ", "") + "/"
                + recipient.college.toUpperCase() + "/"
                + recipient.program.toUpperCase() + "/"
                + recipient.section + "/"
                + recipient.id + "/"
                + recipient.id + "_inbox.txt";

        List<String> inbox = Utils.readFile(inboxPath);
        if (inbox == null)
            inbox = new ArrayList<>();

        String encryptedMessage = Utils.encrypt(message);

        String raw = this.id + "|" + date + "|" + this.firstName + " " + this.lastName + "|" + encryptedMessage;

        inbox.add(raw);

        Utils.writeRawFile(inboxPath, inbox);

        System.out.println("Message sent to " + recipient.firstName + " " + recipient.lastName + "!");
    }

    public static Student findById(String studentId) {

        File base = new File("data/STUDENTS");
        if (!base.exists()) {
            System.out.println("[ERROR] STUDENTS directory not found.");
            return null;
        }

        Queue<File> queue = new LinkedList<>();
        queue.add(base);

        while (!queue.isEmpty()) {
            File current = queue.poll();
            File[] files = current.listFiles();
            if (files == null)
                continue;

            for (File f : files) {

                if (f.isDirectory()) {
                    // If folder name == student ID, check inside
                    if (f.getName().equalsIgnoreCase(studentId)) {

                        File infoFile = new File(f, studentId + "_info.txt");
                        if (infoFile.exists()) {

                            List<String> lines = Utils.readFile(infoFile.getPath());
                            if (lines == null || lines.isEmpty())
                                continue;

                            String dec = Utils.decryptEachField(lines.get(0));
                            if (dec == null || dec.isEmpty())
                                continue;

                            String[] p = dec.split("\\|", -1);
                            if (p.length < 21)
                                continue;

                            return new Student(p); // FOUND!
                        }
                    }

                    queue.add(f); // explore deeper
                }
            }
        }

        return null; // not found anywhere
    }

    private void showSchedule() {
        String path = "data/STUDENTS/"
                + schoolYear.replace(" ", "") + "/"
                + yearLevel.replace(" ", "") + "/"
                + college.toUpperCase() + "/"
                + program.toUpperCase() + "/"
                + section + "/" + program.toUpperCase() + section + "_schedule.txt";

        List<String> enc = Utils.readFile(path);

        if (enc == null || enc.isEmpty()) {
            System.out.println("No schedule found for " + program + " (" + section + ")");
            return;
        }

        System.out.println("\n--- SCHEDULE ---");
        System.out.println("No. |  Professor Id  |  Professor Name  |  Subject  |  Day  |  Time");
        int i = 1;
        for (String x : enc) {
            String[] parts = x.split("\\|");
            String profid = parts[0].trim();
            String profName = parts[1].trim();
            String sub = parts[2].trim();
            String day = parts[3].trim();
            String time = parts[4].trim();

            String print = i + " | " + profid + " | " + profName + " | " + sub + " | " + day + " | " + time;
            System.out.println(print);
            i++;
        }
    }

    private void viewGrades() {
        String path = "data/STUDENTS/"
                + schoolYear.replace(" ", "") + "/"
                + yearLevel.replace(" ", "") + "/"
                + college.toUpperCase() + "/"
                + program.toUpperCase() + "/"
                + section + "/"
                + id + "/"
                + id + "_grades.txt";

        List<String> enc = Utils.readFile(path);

        if (enc == null || enc.isEmpty()) {
            System.out.println("No grades recorded.");
            return;
        }

        System.out.println("\n--- GRADES ---");
        for (String x : enc)
            System.out.println(Utils.decryptEachField(x));
    }

    public boolean changePassword(String current, String newPassword) {
        String storedPassword = Utils.decryptValue(this.password); // get current stored password

        if (!storedPassword.equals(current)) {
            return false; // current password does not match
        }

        this.password = Utils.encrypt(newPassword); // encrypt and store new password

        // update in file
        List<String> lines = Utils.readFile("data/student.txt");
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            String dec = Utils.decryptEachField(line);
            String[] p = dec.split("\\|", -1);
            if (p.length > 19 && p[0].equals(this.id)) { // match by student ID
                p[19] = this.password; // update password field
                dec = String.join("|", p);
            }
            updated.add(Utils.encryptEachField(dec));
        }

        Utils.writeRawFile("data/student.txt", updated);
        return true;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getExtName() {
        return extName;
    }

    public String getAge() {
        return age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getSchool() {
        return school;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFather() {
        return father;
    }

    public String getMother() {
        return mother;
    }

    public String getAddress() {
        return address;
    }

    public String getSchoolYear() {
        return schoolYear;
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

    public String getStatus() {
        return status;
    }

    public String getExamPermit() {
        return examPermit;
    }

    public String getPassword() {
        return password;
    }

    public String getSection() {
        return section;
    }

    // Setters for editable fields
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setYearLevel(String YearLevel) {
        this.yearLevel = YearLevel;
    }

}