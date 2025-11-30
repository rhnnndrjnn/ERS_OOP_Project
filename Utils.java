import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Utils {

    private static final Scanner sc = new Scanner(System.in);

    // ===================== INPUT METHODS =====================
    public static String input(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int inputInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
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

    // ===================== FILE HELPERS =====================
    public static List<String> readFile(String path) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists()) return lines;

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
            PrintWriter pw = new PrintWriter(new FileWriter(path));
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + path);
        }
    }

    // ===================== DIRECTORY =====================
    public static void ensureDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ===================== PASSWORD HASH (OPTIONAL) =====================
    public static String hashPassword(String password) {
        // Simple placeholder; replace with real hashing if needed
        return Integer.toString(password.hashCode());
    }
}
