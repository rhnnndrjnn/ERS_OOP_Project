import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class Applicant {

    private static final String MASTER_FILE = "data/applicants.txt";
    private static final String STUDENT_MASTER_FILE = "data/students.txt";

    private String id;
    private String fullName;
    private String birthdate;
    private int age;
    private String num;
    private String course;
    private String address;
    private String gender;
    private String email;
    private String status;

    public Applicant(
            String id,
            String fullName,
            String birthdate,
            int age,
            String course,
            String address,
            String gender,
            String email,
            String num,
            String status) {
        this.id = id;
        this.fullName = fullName;
        this.birthdate = birthdate;
        this.age = age;
        this.num = num;
        this.course = course;
        this.address = address;
        this.gender = gender;
        this.email = email;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public int getAge() {
        return age;
    }

    public String getNum() {
        return num;
    }

    public String getCourse() {
        return course;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public static boolean loginGUI(String id, String password) {
        if (id == null || id.isEmpty() || password == null)
            return false;

        String hashed = Utils.hashPassword(password);
        List<String> master = Utils.readFile(MASTER_FILE);

        for (String line : master) {
            String[] p = line.split("\\|");

            if (p.length < 16)
                continue;

            String fileId = p[0].trim();
            String filePass = p[14].trim();

            if (fileId.equals(id) && filePass.equals(hashed)) {
                return true;
            }
        }
        return false;
    }

    public static String registerGUI(
            String last,
            String first,
            String middle,
            String ext,
            int age,
            String birthdate,
            String gender,
            String phone,
            String course,
            String email,
            String address,
            String father,
            String mother,
            String password) {
        try {
            String newId = generateApplicantID();

            FileWriter fw = new FileWriter(MASTER_FILE, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(
                    newId + "|" +
                            last + "|" +
                            first + "|" +
                            middle + "|" +
                            ext + "|" +
                            age + "|" +
                            birthdate + "|" +
                            gender + "|" +
                            phone + "|" +
                            course + "|" +
                            email + "|" +
                            address + "|" +
                            father + "|" +
                            mother + "|" +
                            Utils.hashPassword(password) + "|" + "PENDING");

            pw.close();
            fw.close();
            return newId;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFullNameWithId(String id) {
        Applicant a = getApplicantById(id);
        if (a == null)
            return "Unknown (" + id + ")";
        return a.getFullName() + " (" + id + ")";
    }

    private static String generateApplicantID() {
        int lastNumber = 1000;

        try {
            File f = new File(MASTER_FILE);
            if (f.exists()) {
                Scanner sc = new Scanner(f);

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.trim().isEmpty())
                        continue;

                    String id = line.split("\\|")[0]; // APP####
                    int n = Integer.parseInt(id.substring(3));
                    if (n >= lastNumber)
                        lastNumber = n + 1;
                }
                sc.close();
            }

        } catch (Exception ignored) {
        }

        return "APP" + lastNumber;
    }

    public static Applicant getApplicantById(String id) {
        try {
            File f = new File(MASTER_FILE);
            if (!f.exists())
                return null;

            Scanner sc = new Scanner(f);

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String[] p = line.split("\\|", -1);

                if (p[0].equals(id)) {

                    String fullName = p[1] + ", " +
                            p[2] + " " +
                            p[3] + " " +
                            p[4];

                    int age = Integer.parseInt(p[5]);

                    sc.close();
                    return new Applicant(
                            p[0], // ID
                            fullName, // Full Name
                            p[6], // Birthdate
                            age, // Age
                            p[9], // Course
                            p[11], // Address
                            p[7], // Gender
                            p[10], // Email
                            p[8], // Phone
                            p[15]);
                }
            }

            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String signup() {
        System.out.println("\n--- APPLICANT SIGN UP ---");

        String last = Utils.input("Last Name: ");
        String first = Utils.input("First Name: ");
        String middle = Utils.input("Middle Name: ");
        String ext = Utils.input("Extension: ");
        int age = Integer.parseInt(Utils.input("Age: "));
        String birthdate = Utils.input("Birthdate(MM/DD/YYYY): ");
        String gender = Utils.input("Gender: ");
        String phone = Utils.input("Phone: ");
        String course = Utils.input("Course: ");
        String email = Utils.input("Email: ");
        String address = Utils.input("Address: ");
        String father = Utils.input("Father's Name: ");
        String mother = Utils.input("Mother's Name: ");
        String password = Utils.input("Password: ");

        String newId = registerGUI(
                last, first, middle, ext,
                age, birthdate, gender, phone,
                course, email, address, father, mother, password);

        if (newId != null) {
            System.out.println("Registered Successfully");
            System.out.println("Applicant ID: " + newId);

            Applicant a = getApplicantById(newId);
            dashboard(a);
        } else {
            System.out.println("Registration failed. Please try again.");
        }

        return newId;
    }

    public static String login() {
        System.out.println("\n--- APPLICANT LOGIN ---");

        String id = Utils.input("Applicant ID: ");
        String password = Utils.input("Password: ");

        boolean ok = loginGUI(id, password);

        if (!ok) {
            System.out.println("Invalid ID or password!");
            return null;
        }

        Applicant a = getApplicantById(id);
        dashboard(a);
        return id;
    }

    public static void dashboard(Applicant a) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== APPLICANT DASHBOARD ===");
            System.out.println("Welcome, " + a.getFullName() + " (" + a.getId() + ")");
            System.out.println("1. View Information");
            System.out.println("2. View Permit");
            System.out.println("3. View Status");
            System.out.println("4. Logout");
            System.out.print("Select: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewInformation(a);
                    break;
                case "2":
                    viewPermit(a.getId());
                    break;
                case "3":
                    viewStatus(a);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void viewInformation(Applicant a) {
        System.out.println("\n=== YOUR INFORMATION ===");
        System.out.println("ID: " + a.getId());
        System.out.println("Full Name: " + a.getFullName());
        System.out.println("Birthdate: " + a.getBirthdate());
        System.out.println("Age: " + a.getAge());
        System.out.println("Gender: " + a.getGender());
        System.out.println("Course: " + a.getCourse());
        System.out.println("Email: " + a.getEmail());
        System.out.println("Phone: " + a.getNum());
        System.out.println("Address: " + a.getAddress());
    }

    public static void viewPermit(String applicantId) {

        String permitDir = "data/permits/";
        String permitFile = permitDir + applicantId + ".txt";

        try {
            File dir = new File(permitDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(permitFile);

            if (file.exists()) {
                List<String> saved = Utils.readFile(permitFile);

                System.out.println("\n--- Exam Permit ---");
                for (String line : saved) {
                    System.out.println(line);
                }
                return;
            }

            List<String> info = Utils.readFile("data/" + applicantId + ".txt");

            // Random generation
            String date = "2025-12-" + (10 + new Random().nextInt(10));
            String time = (8 + new Random().nextInt(3)) + ":00 AM";
            String room = "Room " + (100 + new Random().nextInt(50));

            // Save permit
            PrintWriter pw = new PrintWriter(new FileWriter(permitFile));
            pw.println("Exam Date: " + date);
            pw.println("Time: " + time);
            pw.println("Room: " + room);
            pw.close();

            // Display
            System.out.println("\n--- Exam Permit ---");
            System.out.println("Exam Date: " + date);
            System.out.println("Time: " + time);
            System.out.println("Room: " + room);

        } catch (Exception e) {
            System.out.println("Error while loading permit: " + e.getMessage());
        }
    }

    public static void viewStatus(Applicant a) {
        Scanner sc = new Scanner(System.in);

        while (true) { // Loop only for invalid input retry
            String status = a.getStatus().toUpperCase();

            switch (status) {
                case "PENDING":
                    System.out.println("\n=== APPLICATION STATUS ===");
                    System.out.println("Current Status: " + a.getStatus());
                    System.out.println("Your application is currently under review.");
                    return; // exit immediately

                case "REJECTED":
                    System.out.println("\n=== APPLICATION STATUS ===");
                    System.out.println("Current Status: " + a.getStatus());
                    System.out.println("We regret to inform you that your application was not successful.");
                    return; // exit immediately

                case "PASSED":
                    System.out.println("\n=== APPLICATION STATUS ===");
                    System.out.println("Current Status: " + a.getStatus());
                    System.out.println("Congratulations! You have passed the evaluation.");
                    System.out.println("Would you like to:");
                    System.out.println("[1] ACCEPT");
                    System.out.println("[2] DECLINE");
                    System.out.print("Choose: ");
                    String ch = sc.nextLine();

                    if (ch.equals("1")) {
                        updateDecision(a.getId(), "ACCEPTED");
                        a.status = "ACCEPTED"; // update local object
                        System.out.println("\n✓ Thank you for accepting!");
                        System.out.println("Converting your account to Student...");

                        // Load applicant info from file
                        List<String> applicants = Utils.readFile(MASTER_FILE);
                        List<String> info = null;
                        for (String line : applicants) {
                            String[] parts = line.split("\\|", -1);
                            if (parts[0].equals(a.getId())) {
                                info = new ArrayList<>(Arrays.asList(parts));
                                break;
                            }
                        }

                        if (info != null) {
                            transferToStudent(info, a.getId());
                            System.out.println("Status updated: ACCEPTED");
                        } else {
                            System.out.println("Error: applicant info not found!");
                        }

                        return; // exit after decision

                    } else if (ch.equals("2")) {
                        updateDecision(a.getId(), "DECLINED");
                        a.status = "DECLINED"; // update local object
                        System.out.println("\nYou declined the offer. Status updated to DECLINED.");
                        return; // exit after decision
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                    break;

                case "ACCEPTED":
                    System.out.println("\n=== APPLICATION STATUS ===");
                    System.out.println("Current Status: " + a.getStatus());
                    System.out.println("You already accepted the offer.");
                    return; // exit immediately

                case "DECLINED":
                    System.out.println("\n=== APPLICATION STATUS ===");
                    System.out.println("Current Status: " + a.getStatus());
                    System.out.println("You already declined the offer.");
                    return; // exit immediately

                default:
                    System.out.println("Unknown status.");
                    return; // exit immediately
            }
        }
    }

    public static void updateDecision(String id, String decision) {
        try {
            List<String> lines = Utils.readFile(MASTER_FILE);
            PrintWriter pw = new PrintWriter(new FileWriter(MASTER_FILE));

            for (String line : lines) {
                String[] p = line.split("\\|");

                if (p[0].equals(id)) {
                    p[15] = decision; // update final column
                    line = String.join("|", p);
                }
                pw.println(line);
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("Error saving decision.");
        }
    }

    public static void transferToStudent(List<String> info, String applicantId) {
        try {
            final String STUDENT_MASTER_FILE = "data/students.txt";

            // Ensure the info list has enough fields
            while (info.size() <= 15)
                info.add(""); // make sure index 15 exists (status)

            List<String> studentsMaster = Utils.readFile(STUDENT_MASTER_FILE);
            if (studentsMaster == null)
                studentsMaster = new ArrayList<>();

            // Generate new student ID
            String studentId = "ID-0001";
            if (!studentsMaster.isEmpty()) {
                String lastLine = studentsMaster.get(studentsMaster.size() - 1);
                String lastId = lastLine.split("\\|")[0]; // ID-XXXX
                int lastNum = Integer.parseInt(lastId.split("-")[1]);
                studentId = String.format("ID-%04d", lastNum + 1);
            }

            // Safely generate default password
            String defaultPass = generateDefaultPasswordSafe(info);

            // Add student to master list
            studentsMaster.add(studentId + "|" + Utils.hashPassword(defaultPass));
            Utils.writeFile(STUDENT_MASTER_FILE, studentsMaster);

            // Save individual student info
            // Save individual student info
            List<String> studentInfo = new ArrayList<>(info);
            studentInfo.set(0, studentId); // replace applicant ID with student ID
            while (studentInfo.size() <= 22)
                studentInfo.add(""); // fill empty columns
            Utils.writeFile("data/" + studentId + ".txt", studentInfo);

            // Update applicant status in applicants.txt
            List<String> applicants = Utils.readFile(MASTER_FILE);
            List<String> updatedApplicants = new ArrayList<>();
            for (String line : applicants) {
                if (line.trim().isEmpty()) {
                    updatedApplicants.add(line);
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts[0].equals(applicantId)) {
                    parts[15] = "PASSED"; // mark as passed
                    line = String.join("|", parts);
                }
                updatedApplicants.add(line);
            }
            Utils.writeFile(MASTER_FILE, updatedApplicants);

            System.out.println("Applicant " + applicantId + " transferred to Student successfully!");
            System.out.println("Student ID: " + studentId);
            System.out.println("Default Password: " + defaultPass);

        } catch (Exception e) {
            System.out.println("Error during transfer to student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Safely generate default password
    static String generateDefaultPasswordSafe(List<String> info) {
        String lastName = (info.size() > 1) ? info.get(1).toUpperCase().replaceAll(" ", "") : "UNKNOWN";
        String birthdate = (info.size() > 6) ? info.get(6) : "01011900"; // default MM/DD/YYYY if missing

        String[] birthParts = birthdate.split("/");
        if (birthParts.length < 2) {
            birthParts = new String[] { "01", "01" };
        }

        return lastName + birthParts[0] + birthParts[1]; // e.g., LASTNAME0101
    }

    public static void menu(String id) {
        Applicant a = getApplicantById(id);
        if (a == null) {
            System.out.println("Applicant not found.");
            return;
        }
    }

}