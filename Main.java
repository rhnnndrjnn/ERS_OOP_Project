
public class Main {
    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== ENROLLMENT SYSTEM ===");
            System.out.println("1. Applicant");
            System.out.println("2. Student");
            System.out.println("3. Faculty");
            System.out.println("4. Admin");
            System.out.println("5. Exit");

            String choice = Utils.input("Choice: ");

            switch (choice) {
                case "1":
                    applicantMenu();
                    break;
                case "2":
                    Student.login();
                    break;
                case "3":
                    Faculty.login();
                    break;
                case "4":
                    Admin.login();
                    break;
                case "5":
                    exit = true;
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void applicantMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- APPLICANT MENU ---");
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
            System.out.println("3. Back");

            String choice = Utils.input("Choice: ");

            switch (choice) {
                case "1":
                    Applicant newApplicant = new Applicant();
                    newApplicant.signUpInteractive();
                    break;

                case "2":
                    Applicant loginApplicant = new Applicant();
                    String id = loginApplicant.loginInteractive();
                    if (id != null) {
                        loginApplicant.menu(id);
                    }
                    break;

                case "3":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
