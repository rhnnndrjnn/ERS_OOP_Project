import java.util.*;
import java.time.*;
import java.time.format.TextStyle;
import java.io.*;
import java.util.Locale;

public class StudentsAcads {
    private String studentId;
    private List<String> activities = new ArrayList<>();
    private List<Assignment> assignments = new ArrayList<>();
    private Map<LocalDate, List<String>> calendarTasks = new TreeMap<>();
    private static final String DATA_PATH = "data/";

    public StudentsAcads(String studentId) {
        this.studentId = studentId;
        loadAssignmentsFromFile();  // Load assignments from file if exists
        generateNotifications();    // Update activity feed and calendar
    }

    // ===== ACTIVITY MENU =====
    public void activityMenu() {
        System.out.println("\n=== Activity Feed ===");
        if (activities.isEmpty()) System.out.println("No notifications yet.");
        else for (String note : activities) System.out.println("- " + note);
        Utils.input("Press ENTER to return...");
    }

    // ===== CLASSES MENU =====
    public void classesMenu() {
        System.out.println("\n=== Classes and Assignments ===");
        List<String> profile = Utils.readFile(DATA_PATH + studentId + ".txt");
        if (profile.size() <= 6) return;
        String course = profile.get(6);
        List<String> schedule = Utils.readFile("Schedule_" + course + ".txt");

        for (String line : schedule) {
            String[] parts = line.split(",", 4);
            System.out.println("\nSubject: " + parts[1]);
            System.out.println("Teacher: " + parts[0]);
            System.out.println("Day: " + parts[3]);
            System.out.println("Time: " + parts[2]);

            System.out.println("Assignments/Announcements:");
            int count = 1;
            for (Assignment a : assignments) {
                if (a.getSubject().equalsIgnoreCase(parts[1])) {
                    System.out.println(count + ". [" + a.getStatus() + "] " + a.getTitle() + " (Due: " + a.getDueDate() + ")");
                    count++;
                }
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
                case 1 -> { month--; if (month < 1) { month = 12; year--; } }
                case 2 -> { month = today.getMonthValue(); year = today.getYear(); }
                case 3 -> { month++; if (month > 12) { month = 1; year++; } }
                case 4 -> { int selMonth = Utils.inputInt("Enter month (1-12): "); if (selMonth >= 1 && selMonth <= 12) month = selMonth; else System.out.println("Invalid month!"); }
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

        for (int i = 1; i < dayOfWeekValue; i++) System.out.print("\t");

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            System.out.print(day);
            if (calendarTasks.containsKey(date)) System.out.print("*");
            System.out.print("\t");
            if ((day + dayOfWeekValue - 1) % 7 == 0) System.out.println();
        }
        System.out.println("\n* indicates tasks on that day.\n");

        for (LocalDate date : calendarTasks.keySet()) {
            if (date.getMonthValue() == month && date.getYear() == year) {
                System.out.print(date.getDayOfMonth() + " " + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ": ");
                for (String t : calendarTasks.get(date)) System.out.print(t + "; ");
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
                System.out.println(count + ". " + a.getTitle() + " (" + a.getSubject() + ") Due: " + a.getDueDate());
                map.put(count, a);
                count++;
            }
        }

        // Display Past Due
        if (!pastDue.isEmpty()) {
            System.out.println("\n--- Past Due ---");
            for (Assignment a : pastDue) {
                System.out.println(count + ". " + a.getTitle() + " (" + a.getSubject() + ") Due: " + a.getDueDate());
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
            loadAssignments();       // generate new assignments
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
                if (parts.length > 3 && parts[3].equalsIgnoreCase("Completed")) a.complete();
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
        if (profile.size() <= 6) return;
        String course = profile.get(6);
        List<String> schedule = Utils.readFile("Schedule_" + course + ".txt");
        Random rand = new Random();

        for (String line : schedule) {
            String[] parts = line.split(",", 4);
            String subject = parts[1];
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
            calendarTasks.computeIfAbsent(a.getDueDate(), k -> new ArrayList<>()).add(a.getTitle() + " (" + a.getSubject() + ")");
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

        public String getSubject() { return subject; }
        public String getTitle() { return title; }
        public LocalDate getDueDate() { return dueDate; }

        public String getStatus() {
            LocalDate today = LocalDate.now();
            if (status.equals("Completed")) return "Completed";
            if (dueDate.isBefore(today)) return "Past Due";
            return "Upcoming";
        }

        public void complete() { status = "Completed"; }
    }
}