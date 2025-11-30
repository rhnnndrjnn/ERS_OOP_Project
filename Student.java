import java.util.*;
import java.time.*;
import java.time.format.TextStyle;
import java.io.*;

public class Student extends User {
    private static final String STUDENT_MASTER_FILE = "data/students.txt";
    private String teacher, time, course, subject, day;

    public Student(String id, String name, String password, String course) {
        super(id, name);
        this.course = course;
        this.teacher = "";
        this.time = "";
        this.subject = "";
        this.day = "";
    }

    // Student login
    public static String login() {
        String id = Utils.input("Student ID: ");
        String pass = Utils.hashPassword(Utils.input("Password: "));

        List<String> master = Utils.readFile(STUDENT_MASTER_FILE);
        boolean found = false;

        for (String line : master) {
            String[] parts = line.split("\\|");
            if (parts.length >= 2 && parts[0].equals(id) && parts[1].equals(pass)) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Invalid Student ID or password.");
            return null;
        }

        System.out.println("Welcome, " + getName(id));
        mainMenu(id);
        return id;
    }

    // Helper to get student name
    private static String getName(String studentId) {
        List<String> info = Utils.readFile("data/" + studentId + ".txt");
        // guard: expect [last, first, middle, ext, age, birthdate, course, ...]
        if (info.size() >= 3) {
            String first = info.size() > 1 ? info.get(1) : "";
            String middle = info.size() > 2 ? info.get(2) : "";
            String last = info.size() > 0 ? info.get(0) : "";
            String full = (first + " " + middle + " " + last).trim();
            return full.isEmpty() ? studentId : full;
        } else {
            return studentId;
        }
    }

    // Main menu (formatted as requested)
    private static void mainMenu(String studentId) {
        while (true) {
            System.out.println("\n=== MAIN MENU: ===");
            System.out.println("1. Message");
            System.out.println("2. View Profile");
            System.out.println("3. Edit profile");
            System.out.println("4. Schedule");
            System.out.println("5. Grades");
            System.out.println("6. Acads Panel");
            System.out.println("7. Log out");

            String choice = Utils.input("Select an option: ");
            switch (choice) {
                case "1":
                    messageMenu(studentId);
                    break;
                case "2":
                    viewProfile(studentId);
                    break;
                case "3":
                    editProfile(studentId);
                    break;
                case "4":
                    viewSchedule(studentId); // renamed to match method below
                    break;
                case "5":
                    viewGrades(studentId);
                    break;
                case "6":
                    acadsPanel(studentId);
                    break;
                case "7":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Message submenu
    private static void messageMenu(String studentId) {
        while (true) {
            System.out.println("\n=== Messages ===");
            System.out.println("1. Inbox");
            System.out.println("2. Send Message");
            System.out.println("3. Back");
            String choice = Utils.input("Select an option: ");
            switch (choice) {
                case "1":
                    inbox(studentId);
                    break;
                case "2":
                    send(studentId);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // View student profile (console)
    public static void viewProfile(String studentId) {
        List<String> info = Utils.readFile("data/" + studentId + ".txt");
        System.out.println("\n=== Personal Information ===");
        System.out.println("Course: " + safeGetField(info, 6));
        System.out.println("Last Name: " + safeGetField(info, 0));
        System.out.println("First Name: " + safeGetField(info, 1));
        System.out.println("Middle Name: " + safeGetField(info, 2));
        System.out.println("Extension Name: " + safeGetField(info, 3));
        System.out.println("Age: " + safeGetField(info, 4));
        System.out.println("Birthdate: " + safeGetField(info, 5));
        System.out.println("Status: " + safeGetField(info, 10));
    }

    // Safe getter for array index (single definition)
    private static String safeGetField(List<String> info, int index) {
        return (index < info.size() && info.get(index) != null) ? info.get(index) : "";
    }

    // Edit profile placeholder (user requested "Edit profile" option)
    private static void editProfile(String studentId) {
        System.out.println("\n=== Edit Profile ===");

        List<String> info = Utils.readFile("data/" + studentId + ".txt");
        if (info.size() < 7) {
            System.out.println("Corrupted profile file.");
            return;
        }

        String[] labels = {
                "Last Name", "First Name", "Middle Name", "Extension", "Age", "Birthdate", "Course"
        };

        for (int i = 0; i < labels.length; i++) {
            System.out.println(labels[i] + ": " + safeGetField(info, i));
            String newVal = Utils.input("Enter new value (leave blank to keep): ");
            if (!newVal.trim().isEmpty()) {
                ensureSize(info, i);
                info.set(i, newVal);
            }
        }

        Utils.writeFile("data/" + studentId + ".txt", info);
        System.out.println("Profile updated.");
    }

    // Ensure the list has at least size elements (indexes up to size-1 valid)
    private static void ensureSize(List<String> list, int size) {
        while (list.size() <= size) {
            list.add("");
        }
    }

    // View grades stored inside student profile file (indices 11..15 -> grades,
    // index 16 -> average)
    public static void viewGrades(String studentId) {
        List<String> info = Utils.readFile("data/" + studentId + ".txt");

        System.out.println("\n=== Grades ===");
        boolean hasGrades = false;
        double total = 0;
        int count = 0;

        // we expect subjects/grades to be stored at indices 11..15 (5 subjects)
        for (int i = 11; i <= 15; i++) {
            if (i < info.size() && !info.get(i).trim().isEmpty()) {
                System.out.println("Subject " + (i - 10) + ": " + info.get(i));
                try {
                    total += Double.parseDouble(info.get(i));
                    count++;
                } catch (NumberFormatException e) {
                    // if not numeric, skip averaging
                }
                hasGrades = true;
            }
        }

        if (!hasGrades) {
            System.out.println("No grades recorded yet.");
        } else {
            double avg = (count > 0) ? total / count : 0;
            System.out.printf("Average: %.2f\n", avg);

            // store average at index 16
            ensureSize(info, 16);
            info.set(16, String.format("%.2f", avg));
            Utils.writeFile("data/" + studentId + ".txt", info);
        }
    }

    private static void inbox(String studentId) {
        System.out.println("\n=== Inbox ===");

        String file = "data/" + studentId + "_inbox.txt";
        List<String> messages = Utils.readFile(file);

        if (messages.isEmpty()) {
            System.out.println("Inbox is empty.");
            Utils.input("Press ENTER to return...");
            return;
        }

        int count = 1;
        for (String line : messages) {
            String[] p = line.split("\\|");
            String sender = p.length > 0 ? p[0] : "";
            String msg = p.length > 1 ? p[1] : "";
            String time = p.length > 2 ? p[2] : "";

            System.out.println("\n[" + count + "]");
            System.out.println("From: " + sender);
            System.out.println("Message: " + msg);
            System.out.println("Time: " + time);
            count++;
        }

        Utils.input("\nPress ENTER to return...");
    }

    private static void send(String studentId) {
        System.out.println("\n=== Send Message ===");

        String recipient = Utils.input("Recipient ID: ");
        String message = Utils.input("Message: ");

        String file = "data/" + recipient + "_inbox.txt";

        String timestamp = LocalDateTime.now().toString();
        String entry = studentId + "|" + message + "|" + timestamp;

        List<String> messages = Utils.readFile(file);
        messages.add(entry);
        Utils.writeFile(file, messages);

        System.out.println("Message sent.");
    }

    // Getters (kept names consistent)
    public String getTeacher() {
        return teacher;
    }

    public String getTime() {
        return time;
    }

    public String getCourse() {
        return course;
    }

    public String getSubject() {
        return subject;
    }

    public String getDay() {
        return day;
    }

    // Returns the subjects for console view (already present)
    public static String[][] getSubjects(String studentId) {

        List<String> info = Utils.readFile("data/" + studentId + ".txt");
        if (info.size() <= 6)
            return new String[0][0];

        String course = info.get(6).trim();
        List<String> lines = Utils.readFile("Schedule_" + course + ".txt");

        String[][] data = new String[lines.size()][3];
        int i = 0;

        for (String line : lines) {
            String[] p = line.split(",", 4);
            String teacher = p.length > 0 ? p[0] : "";
            String subject = p.length > 1 ? p[1] : "";
            String time = p.length > 2 ? p[2] : "";

            data[i][0] = subject;
            data[i][1] = teacher;
            data[i][2] = time;
            i++;
        }

        return data;
    }

    // --- Implemented helper methods used by GUI calls ---

    // Returns basic student info array for StudentProfileGUI
    public static String[] getStudentInfo(String id) {
        List<String> info = Utils.readFile("data/" + id + ".txt");
        String last = safeGetField(info, 0);
        String first = safeGetField(info, 1);
        String middle = safeGetField(info, 2);
        String name = (first + " " + middle + " " + last).trim();
        if (name.isEmpty())
            name = id;
        String age = safeGetField(info, 4);
        String course = safeGetField(info, 6);
        String birthday = safeGetField(info, 7);
        return new String[] { id, name, age, course, birthday };
    }

    // Convert stored grades into a table for StudentGradesGUI
    public static String[][] getGrades(String id) {
        List<String> info = Utils.readFile("data/" + id + ".txt");
        // grades expected at indices 11..15
        List<String[]> rows = new ArrayList<>();
        for (int i = 11; i <= 15; i++) {
            if (i < info.size() && !info.get(i).trim().isEmpty()) {
                String grade = info.get(i).trim();
                String code = "SUB" + (i - 10);
                String desc = "Subject " + (i - 10);
                rows.add(new String[] { code, desc, grade });
            }
        }
        String[][] arr = new String[rows.size()][3];
        for (int i = 0; i < rows.size(); i++)
            arr[i] = rows.get(i);
        return arr;
    }

    // Provide schedule table for StudentScheduleGUI
    public static String[][] getSchedule(String id) {
        List<String> info = Utils.readFile("data/" + id + ".txt");
        if (info.size() <= 6)
            return new String[0][0];
        String course = info.get(6).trim();
        List<String> lines = Utils.readFile("Schedule_" + course + ".txt");
        String[][] data = new String[lines.size()][4];
        int i = 0;
        for (String line : lines) {
            String[] p = line.split(",", 4);
            String teacher = p.length > 0 ? p[0] : "";
            String subject = p.length > 1 ? p[1] : "";
            String time = p.length > 2 ? p[2] : "";
            String day = p.length > 3 ? p[3] : "";
            data[i][0] = subject;
            data[i][1] = teacher;
            data[i][2] = day;
            data[i][3] = time;
            i++;
        }
        return data;
    }

    // section->studentInfo, schedule
    // ScheduleMasterFile: Teacher, subject, time
    // sectionSched: Subject, teacher, time
    public void generateSchedule(String course) {
        File schedFile = new File("Schedule_" + course + ".txt");

        if (schedFile.exists()) {
            loadCourseSched(course);
            System.out.println("Course schedule already exists. Loaded existing schedule.");
            return;
        }

        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> teachers = new ArrayList<>();
        File teacherMasterFile = new File("Professors.txt");
        try (Scanner profandsub = new Scanner(teacherMasterFile)) {
            while (profandsub.hasNextLine()) {
                String line = profandsub.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length < 2)
                    continue;

                String teacherName = parts[0].trim();
                teachers.add(teacherName);

                for (int i = 1; i < parts.length; i++) {
                    String sub = parts[i].trim();
                    if (!sub.isEmpty()) {
                        subjects.add(sub);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.shuffle(subjects);
        Collections.shuffle(teachers);

        int totalSubjects = Math.min(6, Math.min(teachers.size(), subjects.size()));
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        String[] startTimeSlots = { "7:00", "10:00", "13:00", "16:00" };

        Random rand = new Random();

        for (int i = 0; i < totalSubjects; i++) {
            String subject = subjects.remove(rand.nextInt(subjects.size()));
            String teacher = teachers.remove(rand.nextInt(teachers.size()));
            String startTime = startTimeSlots[i % startTimeSlots.length];

            String[] parts = startTime.split(":", 2);
            String slicedStartTime = parts[0].trim();
            int convertedStartTime = Integer.parseInt(slicedStartTime);
            int finalTime = convertedStartTime + 3;
            String timeRange = startTime + "-" + finalTime + ":00";
            String day = days[rand.nextInt(days.length)];
            createSched(course, teacher, subject, timeRange, day);
        }
    }

    public boolean isTeacherBusy(String teacher, String time, String day) {
        File f = new File("Schedule_MasterFile.txt");

        if (!f.exists())
            return false;

        try (Scanner scan = new Scanner(f)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty())
                    continue;
                String[] p = line.split(",", 5);
                if (p.length < 5)
                    continue;

                String fileTeacher = p[1].trim();
                String fileTime = p[3].trim();
                String fileDay = p[4].trim();

                if (fileTeacher.equals(teacher) && fileDay.equalsIgnoreCase(day) && timeOverlap(fileTime, time)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCourseBusy(String course, String time, String day) {
        File f = new File("Schedule_MasterFile.txt");

        if (!f.exists())
            return false;

        try (Scanner scan = new Scanner(f)) {
            while (scan.hasNextLine()) {
                String[] p = scan.nextLine().trim().split(",", 5);
                if (p.length < 5)
                    continue;

                if (p[0].trim().equals(course)
                        && p[3].trim().equals(time)
                        && p[4].trim().equalsIgnoreCase(day)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createSched(String course, String teacher, String subject, String time, String day) {
        while (isTeacherBusy(teacher, time, day) || isCourseBusy(course, time, day)) {
            String[] newTime = CreateNewTime(time, day);
            if (newTime == null) {
                break;
            }
            time = newTime[0];
            day = newTime[1];
        }

        try (FileWriter saveSectionFile = new FileWriter("Schedule_" + course + ".txt", true)) {
            saveSectionFile.write(teacher + "," + subject + "," + time + "," + day + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try (FileWriter saveMasterFile = new FileWriter("Schedule_MasterFile.txt", true)) {
            saveMasterFile.write(course + "," + teacher + "," + subject + "," + time + "," + day + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Console version of View Subjects
    public static void viewSubjects(String studentId) {
        List<String> info = Utils.readFile("data/" + studentId + ".txt");

        if (info.size() <= 6) {
            System.out.println("Error: No course assigned to student.");
            return;
        }

        String course = info.get(6).trim();
        String file = "Schedule_" + course + ".txt";
        List<String> lines = Utils.readFile(file);

        System.out.println("\n=== SUBJECT LIST FOR COURSE: " + course + " ===");

        if (lines.isEmpty()) {
            System.out.println("No subjects found for this course.");
            return;
        }

        int count = 1;
        for (String s : lines) {
            String[] p = s.split(",", 4);
            if (p.length >= 2) {
                System.out.println(count + ". " + p[1]); // subject name
                count++;
            }
        }
        System.out.println();
    }

    public String[] CreateNewTime(String timeRange, String day) {
        String[] parts = timeRange.split("-", 2);
        if (parts.length < 2)
            return null;

        String start = parts[0].trim();
        int startHour = Integer.parseInt(start.split(":")[0]);

        startHour++;

        if (startHour >= 21) {
            startHour = 7;
            day = nextDay(day);
        }

        int endhour = startHour + 3;
        String newTime = startHour + ":00-" + endhour + ":00";
        return new String[] { newTime, day };
    }

    public String nextDay(String day) {
        String days[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return days[(i + 1) % 6];
            }
        }
        return day;
    }

    public boolean timeOverlap(String t1, String t2) {
        String[] p1 = t1.split("-", 2);
        String[] p2 = t2.split("-", 2);

        int s1 = Integer.parseInt(p1[0].split(":")[0]);
        int e1 = Integer.parseInt(p1[1].split(":")[0]);

        int s2 = Integer.parseInt(p2[0].split(":")[0]);
        int e2 = Integer.parseInt(p2[1].split(":")[0]);

        return (s1 < e2) && (s2 < e1);
    }

    public void loadCourseSched(String course) {
        File f = new File("Schedule_" + course + ".txt");

        if (!f.exists()) {
            return;
        }

        try (Scanner scan = new Scanner(f)) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String[] p = line.split(",", 4);
                if (p.length < 4)
                    continue;

                this.teacher = p[0].trim();
                this.subject = p[1].trim();
                this.time = p[2].trim();
                this.day = p[3].trim();
            }

            System.out.println("Section schedule loaded for: " + course);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // renamed to match mainMenu call
    public static void viewSchedule(String studentID) {
        List<String> info = Utils.readFile("data/" + studentID + ".txt");

        if (info.size() <= 6) {
            System.out.println("Error: Student profile missing course info.");
            return;
        }
        String course = info.get(6).trim();
        String schedFile = "Schedule_" + course + ".txt";

        List<String> lines = Utils.readFile(schedFile);

        System.out.println("\n===== CLASS SCHEDULE FOR COURSE: " + course + " =====");

        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;
            String[] p = line.split(",", 4);
            String teacher = p.length > 0 ? p[0] : "";
            String subject = p.length > 1 ? p[1] : "";
            String time = p.length > 2 ? p[2] : "";
            String day = p.length > 3 ? p[3] : "";

            System.out.println("\nSubject: " + subject);
            System.out.println("Teacher: " + teacher);
            System.out.println("Day: " + day);
            System.out.println("Time: " + time);
        }

        System.out.println("\n======================================\n");
    }

    // Acads Panel submenu
    private static void acadsPanel(String studentId) {
        StudentsAcads acads = new StudentsAcads(studentId);
        while (true) {
            System.out.println("\n=== Acads Panel: ===");
            System.out.println("1. Activity");
            System.out.println("2. View class");
            System.out.println("3. View assignments");
            System.out.println("4. Notes");
            System.out.println("5. Calendar");
            System.out.println("6. Back");

            String choice = Utils.input("Select an option: ");
            switch (choice) {
                case "1":
                    acads.activityMenu();
                    break;
                case "2":
                    acads.classesMenu();
                    break;
                case "3":
                    acads.assignmentMenu();
                    break;
                case "4":
                    viewNotes(studentId);
                    break;
                case "5":
                    acads.calendarMenu();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewNotes(String studentId) {
        String notesFile = "data/" + studentId + "_notes.txt";
        List<String> notes = Utils.readFile(notesFile);

        System.out.println("\n=== Notes ===");
        if (notes.isEmpty()) {
            System.out.println("No notes available.");
        } else {
            for (String line : notes) {
                System.out.println(line);
            }
        }

        System.out.println("\nOptions:");
        System.out.println("1. Add/Edit Note");
        System.out.println("2. Back");

        String choice = Utils.input("Select an option: ");
        if (choice.equals("1")) {
            String newNote = Utils.input("Enter your note: ");
            notes.add(newNote);
            Utils.writeFile(notesFile, notes);
            System.out.println("Note saved successfully.");
        } else if (!choice.equals("2")) {
            System.out.println("Invalid option.");
        }
    }

    // ADDED: StudentsAcads inner class (kept and cleaned)
    static class StudentsAcads {
        private String studentId;
        private List<String> activities = new ArrayList<>();
        private List<Assignment> assignments = new ArrayList<>();
        private Map<LocalDate, List<String>> calendarTasks = new TreeMap<>();
        private static final String DATA_PATH = "data/";

        public StudentsAcads(String studentId) {
            this.studentId = studentId;
            loadAssignmentsFromFile(); // Load assignments from file if exists
            generateNotifications(); // Update activity feed and calendar
        }

        // ===== ACTIVITY MENU =====
        public void activityMenu() {
            System.out.println("\n=== Activity Feed ===");
            if (activities.isEmpty())
                System.out.println("No notifications yet.");
            else
                for (String note : activities)
                    System.out.println("- " + note);
            Utils.input("Press ENTER to return...");
        }

        // ===== CLASSES MENU =====
        public void classesMenu() {
            System.out.println("\n=== Classes and Assignments ===");
            List<String> profile = Utils.readFile(DATA_PATH + studentId + ".txt");
            if (profile.size() <= 6) {
                System.out.println("No profile/course data found.");
                Utils.input("Press ENTER to return...");
                return;
            }
            String course = profile.get(6);
            List<String> schedule = Utils.readFile("Schedule_" + course + ".txt");

            if (schedule.isEmpty()) {
                System.out.println("No schedule data for course: " + course);
                Utils.input("Press ENTER to return...");
                return;
            }

            for (String line : schedule) {
                String[] parts = line.split(",", 4);
                String teacher = parts.length > 0 ? parts[0] : "";
                String subject = parts.length > 1 ? parts[1] : "";
                String time = parts.length > 2 ? parts[2] : "";
                String day = parts.length > 3 ? parts[3] : "";

                System.out.println("\nSubject: " + subject);
                System.out.println("Teacher: " + teacher);
                System.out.println("Day: " + day);
                System.out.println("Time: " + time);

                System.out.println("Assignments/Announcements:");
                int count = 1;
                for (Assignment a : assignments) {
                    if (a.getSubject().equalsIgnoreCase(subject)) {
                        System.out.println(
                                count + ". [" + a.getStatus() + "] " + a.getTitle() + " (Due: " + a.getDueDate() + ")");
                        count++;
                    }
                }
                if (count == 1) {
                    System.out.println("None.");
                }
            }
            Utils.input("Press ENTER to return...");
        }

        // ===== CALENDAR MENU =====
        public void calendarMenu() {
            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int year = today.getYear();

            boolean exit = false;
            while (!exit) {
                renderCalendar(month, year);
                System.out.println("\n=== Calendar Navigation ===");
                System.out.println("1. Previous Month");
                System.out.println("2. Current Month");
                System.out.println("3. Next Month");
                System.out.println("4. Select Month");
                System.out.println("0. Back");

                int choice = Utils.inputInt("Choice: ");
                switch (choice) {
                    case 1 -> {
                        month--;
                        if (month < 1) {
                            month = 12;
                            year--;
                        }
                    }
                    case 2 -> {
                        month = today.getMonthValue();
                        year = today.getYear();
                    }
                    case 3 -> {
                        month++;
                        if (month > 12) {
                            month = 1;
                            year++;
                        }
                    }
                    case 4 -> {
                        int selMonth = Utils.inputInt("Enter month (1-12): ");
                        if (selMonth >= 1 && selMonth <= 12)
                            month = selMonth;
                        else
                            System.out.println("Invalid month!");
                    }
                    case 0 -> exit = true;
                    default -> System.out.println("Invalid choice!");
                }
            }
        }

        private void renderCalendar(int month, int year) {
            LocalDate firstDay = LocalDate.of(year, month, 1);
            int dayOfWeekValue = firstDay.getDayOfWeek().getValue();
            int daysInMonth = firstDay.lengthOfMonth();

            System.out.println("\n=== Calendar: " + firstDay.getMonth() + " " + year + " ===");
            System.out.println("Mon\tTue\tWed\tThu\tFri\tSat\tSun");

            for (int i = 1; i < dayOfWeekValue; i++)
                System.out.print("\t");

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(year, month, day);
                System.out.print(day);
                if (calendarTasks.containsKey(date))
                    System.out.print("*");
                System.out.print("\t");
                if ((day + dayOfWeekValue - 1) % 7 == 0)
                    System.out.println();
            }
            System.out.println("\n* indicates tasks on that day.\n");

            for (LocalDate date : calendarTasks.keySet()) {
                if (date.getMonthValue() == month && date.getYear() == year) {
                    System.out.print(date.getDayOfMonth() + " "
                            + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ": ");
                    for (String t : calendarTasks.get(date))
                        System.out.print(t + "; ");
                    System.out.println();
                }
            }
        }

        // ===== ASSIGNMENT MENU (Organized by Status) =====
        public void assignmentMenu() {
            LocalDate today = LocalDate.now();
            System.out.println("\n=== Assignments ===");

            // Separate assignments by status
            List<Assignment> upcoming = new ArrayList<>();
            List<Assignment> pastDue = new ArrayList<>();
            List<Assignment> completed = new ArrayList<>();

            for (Assignment a : assignments) {
                switch (a.getStatus()) {
                    case "Upcoming" -> upcoming.add(a);
                    case "Past Due" -> pastDue.add(a);
                    case "Completed" -> completed.add(a);
                }
            }

            int count = 1;
            Map<Integer, Assignment> map = new HashMap<>();

            // Display Upcoming
            if (!upcoming.isEmpty()) {
                System.out.println("\n--- Upcoming ---");
                for (Assignment a : upcoming) {
                    System.out
                            .println(count + ". " + a.getTitle() + " (" + a.getSubject() + ") Due: " + a.getDueDate());
                    map.put(count, a);
                    count++;
                }
            }

            // Display Past Due
            if (!pastDue.isEmpty()) {
                System.out.println("\n--- Past Due ---");
                for (Assignment a : pastDue) {
                    System.out
                            .println(count + ". " + a.getTitle() + " (" + a.getSubject() + ") Due: " + a.getDueDate());
                    map.put(count, a);
                    count++;
                }
            }

            // Display Completed
            if (!completed.isEmpty()) {
                System.out.println("\n--- Completed ---");
                for (Assignment a : completed) {
                    System.out.println(count + ". " + a.getTitle() + " (" + a.getSubject() + ") Completed");
                    map.put(count, a);
                    count++;
                }
            }

            System.out.println("\nEnter assignment number to mark as Completed, or 0 to go back:");
            int choice = Utils.inputInt("Choice: ");

            if (choice != 0 && map.containsKey(choice)) {
                Assignment selected = map.get(choice);
                if (selected.getStatus().equals("Completed")) {
                    System.out.println("This assignment is already completed!");
                } else {
                    selected.complete();
                    System.out.println("Assignment marked as Completed!");
                    generateNotifications();
                    saveAssignmentsToFile();
                    checkAndGenerateNewAssignments();
                }
            }
        }

        // ===== CHECK AND GENERATE NEW ASSIGNMENTS =====
        private void checkAndGenerateNewAssignments() {
            boolean allCompleted = assignments.stream().allMatch(a -> a.getStatus().equals("Completed"));
            if (allCompleted) {
                System.out.println("\nAll assignments completed! Generating new assignments...\n");
                loadAssignments(); // generate new assignments
                generateNotifications();
                saveAssignmentsToFile();
            }
        }

        // ===== FILE HANDLING =====
        private void loadAssignmentsFromFile() {
            assignments.clear();
            File file = new File(DATA_PATH + studentId + "_assignments.txt");
            if (!file.exists()) {
                loadAssignments(); // generate random if no file
                saveAssignmentsToFile();
                return;
            }

            try {
                List<String> lines = Utils.readFile(DATA_PATH + studentId + "_assignments.txt");
                for (String line : lines) {
                    String[] parts = line.split("\\|");
                    LocalDate due = LocalDate.parse(parts[2]);
                    Assignment a = new Assignment(parts[0], parts[1], due);
                    if (parts.length > 3 && parts[3].equalsIgnoreCase("Completed"))
                        a.complete();
                    assignments.add(a);
                }
            } catch (Exception e) {
                System.out.println("Error loading assignments: " + e.getMessage());
            }
        }

        private void saveAssignmentsToFile() {
            List<String> lines = new ArrayList<>();
            for (Assignment a : assignments) {
                lines.add(a.getSubject() + "|" + a.getTitle() + "|" + a.getDueDate() + "|" + a.getStatus());
            }
            Utils.writeFile(DATA_PATH + studentId + "_assignments.txt", lines);
        }

        // ===== RANDOM GENERATION =====
        private void loadAssignments() {
            assignments.clear();
            List<String> profile = Utils.readFile(DATA_PATH + studentId + ".txt");
            if (profile.size() <= 6)
                return;
            String course = profile.get(6);
            List<String> schedule = Utils.readFile("Schedule_" + course + ".txt");
            Random rand = new Random();

            for (String line : schedule) {
                String[] parts = line.split(",", 4);
                String subject = parts.length > 1 ? parts[1] : "Unknown";
                int numAssignments = 1 + rand.nextInt(2);
                for (int i = 1; i <= numAssignments; i++) {
                    LocalDate due = LocalDate.now().plusDays(rand.nextInt(20));
                    assignments.add(new Assignment(subject, "Assignment " + i + " for " + subject, due));
                }
            }
        }

        private void generateNotifications() {
            activities.clear();
            calendarTasks.clear();
            for (Assignment a : assignments) {
                activities.add("Assignment [" + a.getTitle() + "] (" + a.getSubject() + ") Status: " + a.getStatus());
                calendarTasks.computeIfAbsent(a.getDueDate(), k -> new ArrayList<>())
                        .add(a.getTitle() + " (" + a.getSubject() + ")");
            }
        }

        // ===== ASSIGNMENT CLASS =====
        private static class Assignment {
            private String subject, title, status = "Upcoming";
            private LocalDate dueDate;

            public Assignment(String subject, String title, LocalDate dueDate) {
                this.subject = subject;
                this.title = title;
                this.dueDate = dueDate;
            }

            public String getSubject() {
                return subject;
            }

            public String getTitle() {
                return title;
            }

            public LocalDate getDueDate() {
                return dueDate;
            }

            public String getStatus() {
                LocalDate today = LocalDate.now();
                if (status.equals("Completed"))
                    return "Completed";
                if (dueDate.isBefore(today))
                    return "Past Due";
                return "Upcoming";
            }

            public void complete() {
                status = "Completed";
            }
        }
    }

    // If the User base class expects an instance method, provide an implementation.
    @Override
    public void viewProfile() {
        try {
            // assuming User has getId() method; if not, override appropriately in your User
            // class
            String id = this.getId();
            Student.viewProfile(id);
        } catch (Exception e) {
            // fallback: print message
            System.out.println("Unable to show profile (instance). Call Student.viewProfile(id) instead.");
        }
    }
// Get activities for GUI
public static String[] getActivities(String studentId) {
    StudentsAcads acads = new StudentsAcads(studentId);
    List<String> acts = acads.activities; // get activities list
    return acts.toArray(new String[0]);
}

// Get assignments for GUI
public static String[] getAssignments(String studentId) {
    StudentsAcads acads = new StudentsAcads(studentId);
    List<String> arr = new ArrayList<>();
    for (StudentsAcads.Assignment a : acads.assignments) {
        arr.add(a.getTitle() + " [" + a.getStatus() + "] (" + a.getSubject() + ") Due: " + a.getDueDate());
    }
    return arr.toArray(new String[0]);
}

// Get notes for GUI
public static String[] getNotes(String studentId) {
    String notesFile = "data/" + studentId + "_notes.txt";
    List<String> notes = Utils.readFile(notesFile);
    return notes.toArray(new String[0]);
}

// Get calendar events for GUI
public static String[] getCalendar(String studentId) {
    StudentsAcads acads = new StudentsAcads(studentId);
    List<String> events = new ArrayList<>();
    for (Map.Entry<LocalDate, List<String>> entry : acads.calendarTasks.entrySet()) {
        String line = entry.getKey() + ": " + String.join(", ", entry.getValue());
        events.add(line);
    }
    return events.toArray(new String[0]);
}

}
