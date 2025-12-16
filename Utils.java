import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Utils {

    private static Scanner sc = new Scanner(System.in);

    // Read input as string
    public static String input(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // Read input as integer
    public static int inputInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    public static double inputDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public static float inputFloat(String prompt) {
        Scanner sc = new Scanner(System.in);
        float value = 0f;
        while (true) {
            System.out.print(prompt);
            try {
                value = Float.parseFloat(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return value;
    }

    public static List<String> readFile(String path) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists())
                return lines;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);

            }

            br.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + path);
        }
        return lines;
    }

    public static void writeFile(String path, List<String> lines) {
        try {
            ensureDir(new File(path).getParent());

            PrintWriter pw = new PrintWriter(new FileWriter(path));

            for (String line : lines) {
                pw.println(encryptEachField(line));
            }

            pw.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + path);
        }
    }

    public static void appendToFile(String path, String line) {
        try {
            ensureDir(new File(path).getParent());

            PrintWriter pw = new PrintWriter(new FileWriter(path, true));
            pw.println(line); // Already encrypted before passing
            pw.close();
        } catch (Exception e) {
            System.out.println("Error appending file: " + path);
            e.printStackTrace();
        }
    }

    public static void ensureDir(String path) {
        if (path == null || path.isEmpty())
            return;

        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static final int SHIFT = 3;

    public static String encrypt(String text) {
        if (text == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            int v = c + SHIFT;
            if (v > 126)
                v = (v - 127) + 32;
            sb.append((char) v);
        }
        return sb.toString();
    }

    public static String decrypt(String text) {
        if (text == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            int v = c - SHIFT;
            if (v < 32)
                v = 127 - (32 - v);
            sb.append((char) v);
        }
        return sb.toString();
    }

    public static String encryptEachField(String line) {
        if (line == null || line.isEmpty())
            return "";
        String[] parts = line.split("\\|");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(encrypt(parts[i]));
            if (i < parts.length - 1)
                sb.append("|");
        }
        return sb.toString();
    }

    public static String decryptEachField(String line) {
        if (line == null || line.isEmpty())
            return "";
        String[] parts = line.split("\\|");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(decrypt(parts[i]));
            if (i < parts.length - 1)
                sb.append("|");
        }
        return sb.toString();
    }

    public static void writeRawFile(String path, List<String> lines) {
        try {
            ensureDir(new File(path).getParent());

            PrintWriter pw = new PrintWriter(new FileWriter(path));

            for (String line : lines) {
                pw.println(line);
            }

            pw.close();
        } catch (Exception e) {
            System.out.println("Error writing raw file: " + path);
        }
    }

    public static String decryptValue(String val) {
        if (val == null)
            return "";

        boolean looksPlain = val.chars().allMatch(c -> c >= 32 && c <= 126);
        if (looksPlain)
            return val;

        try {
            String maybe = Utils.decryptEachField(val);
            if (maybe != null && !maybe.isEmpty())
                return maybe;
        } catch (Exception ignored) {
        }

        return val;
    }

    private static void applicantsMenu() {

        System.out.println("\n=== APPLICANTS MANAGEMENT ===");

        File base = new File("data/APPLICANTS");
        if (!base.exists() || !base.isDirectory()) {
            System.out.println("Applicants directory not found.");
            return;
        }

        List<File> applicantFiles = new ArrayList<>();
        findApplicantFiles(base, applicantFiles);
        if (applicantFiles.isEmpty()) {
            System.out.println("No applicant files found.");
            return;
        }

        List<ApplicantEntry> entries = new ArrayList<>();

        for (File f : applicantFiles) {
            List<String> encLines = Utils.readFile(f.getPath());
            if (encLines == null)
                continue;

            for (String enc : encLines) {
                if (enc == null || enc.trim().isEmpty())
                    continue;
                String dec = Utils.decryptEachField(enc);
                String[] p = dec.split("\\|", -1);
                if (p.length < 19) {
                    continue;
                }

                ApplicantEntry ae = new ApplicantEntry();
                ae.sourceFile = f;
                ae.encryptedLine = enc;
                ae.decryptedLine = dec;
                ae.fields = p;
                entries.add(ae);
            }
        }

        if (entries.isEmpty()) {
            System.out.println("No applicants stored.");
            return;
        }

        System.out.println("\n=== ALL APPLICANTS ===");
        System.out.println("[ID] | [Full Name] | [College] | [Program] | [Status]");
        System.out.println("--------------------------------------------------------");

        for (ApplicantEntry ae : entries) {
            String id = ae.fields[0];
            String full = ae.fields[1] + " " + ae.fields[2] + " " + ae.fields[3];
            String college = ae.fields[15];
            String program = ae.fields[16];
            String status = ae.fields[18];
            System.out.println(id + " | " + full + " | " + college + " | " + program + " | " + status);
        }

        String filter = Utils.input("\nFilter by College (COS/COE/CLA/CAFA/CIT/CIE or ALL): ").toUpperCase().trim();
        Set<String> valid = new HashSet<>(Arrays.asList("COS", "COE", "CLA", "CAFA", "CIT", "CIE"));

        List<ApplicantEntry> filtered = entries;
        if (!filter.isEmpty() && !filter.equals("ALL")) {
            if (!valid.contains(filter)) {
                System.out.println("Invalid college. Valid: " + valid);
                return;
            }
            filtered = new ArrayList<>();
            System.out.println("\n=== FILTERED (" + filter + ") ===");
            System.out.println("[ID] | [Full Name] | [College] | [Program] | [Status]");
            System.out.println("--------------------------------------------------------");
            for (ApplicantEntry ae : entries) {
                String college = ae.fields[15];
                if (college.equalsIgnoreCase(filter)) {
                    filtered.add(ae);
                    String id = ae.fields[0];
                    String full = ae.fields[1] + " " + ae.fields[2] + " " + ae.fields[3];
                    String program = ae.fields[16];
                    String status = ae.fields[18];
                    System.out.println(id + " | " + full + " | " + college + " | " + program + " | " + status);
                }
            }
            if (filtered.isEmpty()) {
                System.out.println("No applicants for " + filter);
            }
        }

        while (true) {
            String id = Utils.input("\nEnter Applicant ID to update (0 to exit): ").trim();
            if (id.equals("0"))
                break;

            boolean found = false;

            Map<File, List<String>> fileToLines = new HashMap<>();
            for (File f : applicantFiles) {
                List<String> lines = Utils.readFile(f.getPath());
                if (lines == null)
                    lines = new ArrayList<>();
                fileToLines.put(f, new ArrayList<>(lines));
            }

            for (ApplicantEntry ae : filtered) {
                if (ae.fields[0].equalsIgnoreCase(id)) {
                    found = true;
                    String act = Utils.input("Approve (A) / Reject (R): ").toUpperCase().trim();
                    String newStatus = act.equals("A") ? "Passed" : "Failed";

                    String[] newFields = Arrays.copyOf(ae.fields, Math.max(ae.fields.length, 19));
                    newFields[18] = newStatus;

                    String newDecryptedLine = String.join("|", newFields);
                    String newEncryptedLine = Utils.encryptEachField(newDecryptedLine);

                    List<String> fileLines = fileToLines.get(ae.sourceFile);
                    for (int i = 0; i < fileLines.size(); i++) {
                        String cur = fileLines.get(i);
                        if (cur.equals(ae.encryptedLine)) {
                            fileLines.set(i, newEncryptedLine);
                            break;
                        }
                    }

                    Utils.writeRawFile(ae.sourceFile.getPath(), fileLines);

                    System.out.println("Updated applicant " + id + " -> " + newStatus);
                    break;
                }
            }

            if (!found) {
                System.out.println("Applicant ID not found in current list/filter.");
            } else {
                System.out.println("Reloading applicant list...");
                applicantsMenu();
                return;
            }
        }
    }

    private static class ApplicantEntry {
        File sourceFile;
        String encryptedLine;
        String decryptedLine;
        String[] fields;
    }

    private static void findApplicantFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (File f : files) {
            if (f.isDirectory())
                findApplicantFiles(f, list);
            else if (f.getName().toLowerCase().endsWith("_applicants.txt"))
                list.add(f);
        }
    }

    // Simple hash function for password
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password; // fallback
        }
    }

    // ----------------------------
    // Rounded Panel (Utility)
    // ----------------------------
    public static class RoundedButton extends JButton {

        private int arc = 40;

        public RoundedButton(String text) {

            super(text);

            setOpaque(false); // allow custom paint
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.BOLD, 22));
        }

        public void setArc(int arc) {
            this.arc = arc;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background color for button
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static class RoundedPanel extends JPanel {

        private int arc;
        private Color background;

        public RoundedPanel(int arc) {
            this.arc = arc;
            this.background = getBackground();
            setOpaque(false);
        }

        public RoundedPanel(int arc, Color bg) {
            this.arc = arc;
            this.background = bg;
            setOpaque(false);
        }

        @Override
        public void setBackground(Color bg) {
            this.background = bg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.dispose();
        }
    }

}