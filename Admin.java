import java.io.*;
import java.util.*;

public class Admin {

    private static final String ADMIN_CREDENTIALS = "data/admin_credentials.txt";
    private static final String RESIGN_FOLDER = "data/Resignation/";
    private static final String FACULTY_FOLDER = "data/Faculty/";
    private static final String SCHEDULES_FOLDER = "data/Schedules/";
    private static final String FACULTY_FILE = "data/professors.txt";
    private static final String APPLICANT_MASTER_FILE = "data/applicants.txt";

    public static boolean login() {
        List<String> creds = Utils.readFile(ADMIN_CREDENTIALS);

        if (creds.size() < 2) {
            System.out.println("Admin credentials file corrupted!");
            return false;
        }

        String correctUser = creds.get(0).trim();
        String correctPass = creds.get(1).trim();

        String user = Utils.input("Enter Admin Username: ");
        String pass = Utils.input("Enter Admin Password: ");

        if (!user.equals(correctUser) || !pass.equals(correctPass)) {
            System.out.println("Wrong username or password!");
            return false;
        }

        System.out.println("Admin logged in successfully.\n");
        adminMenu();
        return true;
    }

    private static void adminMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Applicants Management");
            System.out.println("2. Resignation Approvals");
            System.out.println("3. Course Schedule Generator");
            System.out.println("4. Log Out");

            String choice = Utils.input("Choice: ");

            switch (choice) {
                case "1":
                    selectCourseApplicants();
                    break;
                case "2":
                    processResignations();
                    break;
                case "3":
                    adminGenerateCourseSchedule();
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void selectCourseApplicants() {
        String course = Utils.input("Enter course name: ").toUpperCase();

        List<String> master = Utils.readFile(APPLICANT_MASTER_FILE);
        if (master.isEmpty()) {
            System.out.println("No applicants yet.");
            return;
        }

        List<String> validIds = new ArrayList<>();
        System.out.println("\nApplicants for " + course + ":");

        for (String line : master) {
            if (line.trim().isEmpty())
                continue;

            String[] parts = line.split("\\|");
            if (parts.length < 16)
                continue; // Ensure enough fields

            String applicantId = parts[0]; // Applicant ID
            String applicantCourse = parts[9]; // Course

            if (!applicantCourse.equalsIgnoreCase(course))
                continue;

            // Build full name
            String last = parts[1];
            String first = parts[2];
            String middle = parts[3];

            String fullName = first;
            if (!middle.isEmpty())
                fullName += " " + middle;
            if (!last.isEmpty())
                fullName += " " + last;

            String status = parts[15].isEmpty() ? "Pending" : parts[15];

            System.out.println(applicantId + " | " + fullName + " | Status: " + status);
            validIds.add(applicantId);
        }

        if (validIds.isEmpty()) {
            System.out.println("No applicants for this course.");
            return;
        }

        String appNo = Utils.input("\nEnter Applicant ID to approve/reject (0 to go back): ");
        if (appNo.equals("0"))
            return;

        if (!validIds.contains(appNo)) {
            System.out.println("Invalid Applicant ID.");
            return;
        }

        // Update the master file
        List<String> updatedMaster = new ArrayList<>();
        for (String line : master) {
            if (line.trim().isEmpty()) {
                updatedMaster.add(line);
                continue;
            }

            String[] parts = line.split("\\|");
            if (parts[0].equals(appNo)) { // Match applicant ID at index 0
                String action = Utils.input("Approve (A) / Reject (R): ").toUpperCase();
                if (action.equals("A")) {
                    parts[15] = "Passed";
                    System.out.println("Applicant approved.");
                } else if (action.equals("R")) {
                    parts[15] = "Failed";
                    System.out.println("Applicant rejected.");
                } else {
                    System.out.println("Invalid action! Status not changed.");
                }
                line = String.join("|", parts);
            }
            updatedMaster.add(line);
        }

        // Save updated master file
        Utils.writeFile(APPLICANT_MASTER_FILE, updatedMaster);
    }

    private static void processResignations() {
        File folder = new File(RESIGN_FOLDER);
        if (!folder.exists())
            folder.mkdirs();

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("\nNo resignation requests.");
            return;
        }

        System.out.println("\n=== RESIGNATION REQUESTS ===");

        for (File f : files) {
            try {
                List<String> info = Utils.readFile(f.getPath());
                if (info.isEmpty())
                    continue;

                // File format: facultyID | Full Name | Reason | Status
                String[] parts = info.get(0).split("\\|", -1);
                if (parts.length < 4)
                    continue;

                String facultyID = parts[0].trim();
                String name = parts[1].trim();
                String reason = parts[2].trim();
                String status = parts[3].trim();

                // Do NOT process if already accepted or rejected
                if (!status.equalsIgnoreCase("Pending")) {
                    System.out.println("\n[SKIPPED] " + facultyID + " (" + name + ") - Already " + status);
                    continue;
                }

                // Display only pending resignations
                System.out.println("\nFaculty ID: " + facultyID);
                System.out.println("Name: " + name);
                System.out.println("Reason: " + reason);
                System.out.println("Status: " + status);

                String choice = Utils.input("Accept (A) / Reject (R): ").toUpperCase();

                if (choice.equals("A")) {

                    // Remove from professors list
                    List<String> professors = Utils.readFile(FACULTY_FILE);
                    List<String> updated = new ArrayList<>();

                    for (String line : professors) {
                        if (!line.startsWith(facultyID))
                            updated.add(line);
                    }
                    Utils.writeFile(FACULTY_FILE, updated);

                    // Remove faculty file
                    File facultyFile = new File(FACULTY_FOLDER + facultyID + ".txt");
                    if (facultyFile.exists())
                        facultyFile.delete();

                    // Update status in the resignation file
                    String newLine = facultyID + " | " + name + " | " + reason + " | Accepted";
                    Utils.writeFile(f.getPath(), Collections.singletonList(newLine));

                    System.out.println("Resignation accepted.");

                } else if (choice.equals("R")) {
                    // Update file status to Rejected
                    String newLine = facultyID + " | " + name + " | " + reason + " | Rejected";
                    Utils.writeFile(f.getPath(), Collections.singletonList(newLine));

                    System.out.println("Resignation rejected.");

                } else {
                    System.out.println("Invalid choice. Skipping...");
                }

            } catch (Exception e) {
                System.out.println("Error processing resignation file: " + e.getMessage());
            }
        }
    }

    private static void adminGenerateCourseSchedule() {
        File folder = new File(SCHEDULES_FOLDER);
        if (!folder.exists())
            folder.mkdirs();

        File[] list = folder.listFiles();
        if (list == null || list.length == 0) {
            System.out.println("\nNo available schedules.");
            return;
        }

        System.out.println("\nAvailable schedules found. Assigning randomly...\n");

        Random r = new Random();

        File selected = list[r.nextInt(list.length)];

        System.out.println("Assigned professor schedule:");
        List<String> schedule = Utils.readFile(selected.getPath());

        for (String s : schedule) {
            System.out.println(s);
        }

        Utils.input("Press ENTER to continue...");
    }
}
