import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentAcads extends Student {

    public StudentAcads(Student s) {
        super(new String[] {
                s.getId(), s.getLastName(), s.getFirstName(), s.getMiddleName(), s.getExtName(),
                s.getAge(), s.getBirthdate(), s.getSchool(), s.getPhone(), s.getEmail(),
                s.getFather(), s.getMother(), s.getAddress(), s.getSchoolYear(),
                s.getYearLevel(), s.getCollege(), s.getProgram(), s.getStatus(),
                s.getExamPermit(), s.getPassword(), s.getSection()
        });
    }

    public void academicPanel() {
        while (true) {
            System.out.println("\n--- ACADEMIC PANEL ---");
            System.out.println("1. Calendar");
            System.out.println("2. Activity");
            System.out.println("3. View Assignments");
            System.out.println("4. Notes");
            System.out.println("5. Back");
            String choice = Utils.input("Select: ");

            switch (choice) {
                case "1":
                    showCalendar();
                    break;
                case "2":
                    activity();
                    break;
                case "3":
                    viewAssignments();
                    break;
                case "4":
                    notes();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void showCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar todayCal = Calendar.getInstance();
        int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
        int month = todayCal.get(Calendar.MONTH) + 1;
        int year = todayCal.get(Calendar.YEAR);

        displayCalendar(month, year, todayDay, sdf);

        String input = Utils.input("\nDo you want to view another month? (Y/N): ").trim().toUpperCase();
        if (input.equals("Y")) {
            month = Utils.inputInt("Enter month (1-12): ");
            year = Utils.inputInt("Enter year: ");
            displayCalendar(month, year, -1, sdf); 
        }
    }

    private void displayCalendar(int month, int year, int highlightDay, SimpleDateFormat sdf) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);

        int startDay = cal.get(Calendar.DAY_OF_WEEK);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String assignFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "/" + this.id + "/" +
                this.id + "_assignments.txt";
        List<String> assignments = Utils.readFile(assignFile);
        Set<Integer> assignmentDays = new HashSet<>();
        if (assignments != null) {
            for (String line : assignments) {
                String dec = Utils.decrypt(line);
                String[] parts = dec.split("\\|");
                if (parts.length >= 2) {
                    try {
                        Date d = sdf.parse(parts[1]);
                        Calendar cd = Calendar.getInstance();
                        cd.setTime(d);
                        if (cd.get(Calendar.MONTH) == month - 1 && cd.get(Calendar.YEAR) == year)
                            assignmentDays.add(cd.get(Calendar.DAY_OF_MONTH));
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        String scheduleFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "_schedule.txt";
        List<String> schedule = Utils.readFile(scheduleFile);
        Set<Integer> scheduleDays = new HashSet<>();
        if (schedule != null) {
            for (String line : schedule) {
                String dec = Utils.decrypt(line);
                try {
                    String datePart = dec.split(" - ")[0];
                    Date d = sdf.parse(datePart);
                    Calendar cd = Calendar.getInstance();
                    cd.setTime(d);
                    if (cd.get(Calendar.MONTH) == month - 1 && cd.get(Calendar.YEAR) == year)
                        scheduleDays.add(cd.get(Calendar.DAY_OF_MONTH));
                } catch (Exception ignored) {
                }
            }
        }

        System.out.println("\n=== " + month + "/" + year + " ===");
        System.out.println("Sun Mon Tue Wed Thu Fri Sat");

        for (int i = 1; i < startDay; i++)
            System.out.print("    ");

        for (int d = 1; d <= maxDay; d++) {
            String color = "";
            String reset = "\u001B[0m";

            if (d == highlightDay)
                color = "\u001B[34m"; // Blue = Today
            else if (assignmentDays.contains(d))
                color = "\u001B[31m"; // Red = Assignment due
            else if (scheduleDays.contains(d))
                color = "\u001B[32m"; // Green = Class scheduled

            System.out.printf(color + "%3d " + reset, d);

            if ((d + startDay - 1) % 7 == 0)
                System.out.println();
        }
        System.out.println();

        System.out.println("\n--- CLASS SCHEDULE ---");
        if (schedule != null && !schedule.isEmpty()) {
            for (String line : schedule)
                System.out.println("\u001B[32m" + Utils.decrypt(line) + "\u001B[0m");
        } else {
            System.out.println("No class schedule.");
        }

        System.out.println("\n--- ASSIGNMENTS ---");
        if (assignments != null && !assignments.isEmpty()) {
            for (String line : assignments)
                System.out.println("\u001B[31m" + Utils.decrypt(line) + "\u001B[0m");
        } else {
            System.out.println("No assignments.");
        }
    }

    private void activity() {
        System.out.println("\n--- ACTIVITY / NOTIFICATIONS ---");

        String assignFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "/" + this.id + "/" +
                this.id + "_assignments.txt";
        List<String> assignments = Utils.readFile(assignFile);
        System.out.println("\n--- ASSIGNMENTS ---");
        if (assignments != null && !assignments.isEmpty()) {
            for (String line : assignments)
                System.out.println(Utils.decrypt(line));
        } else {
            System.out.println("No assignments.");
        }

        String notesFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "/" + this.id + "/" +
                this.id + "_notes.txt";
        List<String> notes = Utils.readFile(notesFile);
        System.out.println("\n--- NOTES SAVED ---");
        if (notes != null && !notes.isEmpty()) {
            for (String line : notes)
                System.out.println(Utils.decrypt(line));
        } else {
            System.out.println("No notes yet.");
        }
    }

    private void viewAssignments() {
        String assignFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "/" + this.id + "/" +
                this.id + "_assignments.txt";
        List<String> assignments = Utils.readFile(assignFile);
        System.out.println("\n--- ASSIGNMENTS ---");
        if (assignments != null && !assignments.isEmpty()) {
            for (String line : assignments)
                System.out.println(Utils.decrypt(line));
        } else {
            System.out.println("No assignments.");
        }
    }

    private void notes() {
        String notesFile = "data/STUDENTS/" + this.schoolYear + "/" + this.yearLevel + "/" +
                this.college + "/" + this.program + "/" + this.section + "/" + this.id + "/" +
                this.id + "_notes.txt";
        Utils.ensureDir(new File(notesFile).getParent());
        List<String> content = Utils.readFile(notesFile);
        if (content == null)
            content = new ArrayList<>();

        while (true) {
            System.out.println("\n--- NOTES ---");
            if (content.isEmpty()) {
                System.out.println("(No notes yet)");
            } else {
                for (String line : content)
                    System.out.println(Utils.decrypt(line));
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add Note");
            System.out.println("2. Delete Notes");
            System.out.println("3. Back");

            String choice = Utils.input("Select: ");
            switch (choice) {
                case "1":
                    String note = Utils.input("Enter note: ");
                    content.add(Utils.encrypt(note));
                    break;
                case "2":
                    content.clear();
                    System.out.println("Notes deleted!");
                    break;
                case "3":
                    Utils.writeFile(notesFile, content);
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

}
