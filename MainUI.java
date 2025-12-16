import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainUI extends JFrame {

    private static final LineBorder DEFAULT_LINE = new LineBorder(Color.GRAY, 2, true);
    private static final LineBorder FOCUS_LINE = new LineBorder((new Color(0, 11, 61)), 3, true);
    private static final EmptyBorder PADDING = new EmptyBorder(0, 10, 0, 0);

    private JButton selectedChoice;

    public MainUI() {
        setTitle("Northwind University");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon("src/loginbg.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(null);
        add(bgPanel, BorderLayout.CENTER);

        JLabel SYR = new JLabel("Select Your Role");
        SYR.setBounds(920, 130, 200, 30);
        SYR.setFont(new Font("Segoe UI", Font.BOLD, 16));
        SYR.setForeground(Color.BLACK);
        bgPanel.add(SYR);

        JButton applicantButton = choiceButton("Applicant", true);
        applicantButton.setBounds(810, 170, 160, 50);
        JButton studentButton = choiceButton("Student", false);
        studentButton.setBounds(1000, 170, 160, 50);
        JButton facultyButton = choiceButton("Faculty", false);
        facultyButton.setBounds(810, 250, 160, 50);
        JButton adminButton = choiceButton("Admin", false);
        adminButton.setBounds(1000, 250, 160, 50);

        bgPanel.add(applicantButton);
        bgPanel.add(studentButton);
        bgPanel.add(facultyButton);
        bgPanel.add(adminButton);

        ApplicantLoginUI applicantUI = new ApplicantLoginUI(bgPanel);
        applicantUI.showUI();

        applicantButton.addActionListener(e -> {
            clearLoginPanel(bgPanel);
            new ApplicantLoginUI(bgPanel).showUI();
        });

        studentButton.addActionListener(e -> {
            clearLoginPanel(bgPanel);
            new StudentLoginUI(bgPanel).showUI();
        });

        facultyButton.addActionListener(e -> {
            clearLoginPanel(bgPanel);
            new FacultyLoginUI(bgPanel).showUI();
        });

        adminButton.addActionListener(e -> {
            clearLoginPanel(bgPanel);
            new AdminLoginUI(bgPanel).showUI();
        });

        setVisible(true);
    }

    private void clearLoginPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            boolean isRoleButton = (comp instanceof JButton) &&
                    (((JButton) comp).getText().equals("Applicant") ||
                            ((JButton) comp).getText().equals("Student") ||
                            ((JButton) comp).getText().equals("Faculty") ||
                            ((JButton) comp).getText().equals("Admin"));

            boolean isLabelSYR = (comp instanceof JLabel) &&
                    ((JLabel) comp).getText().equals("Select Your Role");

            if (!isRoleButton && !isLabelSYR) {
                panel.remove(comp);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, width, height);
        tf.setOpaque(false);
        tf.setForeground(Color.BLACK);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setCaretColor(Color.BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(DEFAULT_LINE, PADDING));

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(FOCUS_LINE, PADDING));
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(DEFAULT_LINE, PADDING));
            }
        });

        return tf;
    }

    private JPasswordField createPasswordField(int x, int y, int width, int height) {
        JPasswordField pf = new JPasswordField();
        pf.setBounds(x, y, width, height);
        pf.setOpaque(false);
        pf.setForeground(Color.BLACK);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setCaretColor(Color.BLACK);
        pf.setBorder(BorderFactory.createCompoundBorder(DEFAULT_LINE, PADDING));

        pf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(FOCUS_LINE, PADDING));
            }

            @Override
            public void focusLost(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(DEFAULT_LINE, PADDING));
            }
        });

        return pf;
    }

    private JButton logincreateButton(String text, int x, int y, int width, int height, int fontSize) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setForeground(Color.BLACK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        Color bgStart = new Color(174, 178, 194);
        Color bgEnd = new Color(0, 11, 61);
        Color textStart = Color.BLACK;
        Color textEnd = Color.WHITE;
        Color borderColor = new Color(0, 11, 61);

        btn.setBackground(bgStart);
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 2, true));

        final Timer[] timer = { null };
        final float[] progress = { 0f };

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (timer[0] != null && timer[0].isRunning())
                    timer[0].stop();
                progress[0] = 0f;

                timer[0] = new Timer(15, ev -> {
                    progress[0] += 0.05f;
                    if (progress[0] > 1f)
                        progress[0] = 1f;

                    int r = (int) (bgStart.getRed() + progress[0] * (bgEnd.getRed() - bgStart.getRed()));
                    int g = (int) (bgStart.getGreen() + progress[0] * (bgEnd.getGreen() - bgStart.getGreen()));
                    int b = (int) (bgStart.getBlue() + progress[0] * (bgEnd.getBlue() - bgStart.getBlue()));
                    btn.setBackground(new Color(r, g, b));

                    int tr = (int) (textStart.getRed() + progress[0] * (textEnd.getRed() - textStart.getRed()));
                    int tg = (int) (textStart.getGreen() + progress[0] * (textEnd.getGreen() - textStart.getGreen()));
                    int tb = (int) (textStart.getBlue() + progress[0] * (textEnd.getBlue() - textStart.getBlue()));
                    btn.setForeground(new Color(tr, tg, tb));

                    if (progress[0] >= 1f)
                        timer[0].stop();
                });
                timer[0].start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (timer[0] != null && timer[0].isRunning())
                    timer[0].stop();
                progress[0] = 1f;

                timer[0] = new Timer(15, ev -> {
                    progress[0] -= 0.05f;
                    if (progress[0] < 0f)
                        progress[0] = 0f;

                    int r = (int) (bgStart.getRed() + progress[0] * (bgEnd.getRed() - bgStart.getRed()));
                    int g = (int) (bgStart.getGreen() + progress[0] * (bgEnd.getGreen() - bgStart.getGreen()));
                    int b = (int) (bgStart.getBlue() + progress[0] * (bgEnd.getBlue() - bgStart.getBlue()));
                    btn.setBackground(new Color(r, g, b));

                    int tr = (int) (textStart.getRed() + progress[0] * (textEnd.getRed() - textStart.getRed()));
                    int tg = (int) (textStart.getGreen() + progress[0] * (textEnd.getGreen() - textStart.getGreen()));
                    int tb = (int) (textStart.getBlue() + progress[0] * (textEnd.getBlue() - textStart.getBlue()));
                    btn.setForeground(new Color(tr, tg, tb));

                    if (progress[0] <= 0f)
                        timer[0].stop();
                });
                timer[0].start();
            }
        });

        return btn;
    }

    private JButton signupcreateButton(String text, int x, int y, int width, int height, int fontSize) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setForeground(Color.BLACK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0, true));
        Color bgStart = new Color(211, 211, 211);
        Color bgEnd = new Color(222, 181, 100);
        Color textStart = Color.BLACK;
        Color textEnd = Color.BLACK;

        btn.setBackground(bgStart);

        final Timer[] timer = { null };
        final float[] progress = { 0f };

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (timer[0] != null && timer[0].isRunning())
                    timer[0].stop();
                progress[0] = 0f;

                timer[0] = new Timer(15, ev -> {
                    progress[0] += 0.05f;
                    if (progress[0] > 1f)
                        progress[0] = 1f;

                    int r = (int) (bgStart.getRed() + progress[0] * (bgEnd.getRed() - bgStart.getRed()));
                    int g = (int) (bgStart.getGreen() + progress[0] * (bgEnd.getGreen() - bgStart.getGreen()));
                    int b = (int) (bgStart.getBlue() + progress[0] * (bgEnd.getBlue() - bgStart.getBlue()));
                    btn.setBackground(new Color(r, g, b));

                    int tr = (int) (textStart.getRed() + progress[0] * (textEnd.getRed() - textStart.getRed()));
                    int tg = (int) (textStart.getGreen() + progress[0] * (textEnd.getGreen() - textStart.getGreen()));
                    int tb = (int) (textStart.getBlue() + progress[0] * (textEnd.getBlue() - textStart.getBlue()));
                    btn.setForeground(new Color(tr, tg, tb));

                    if (progress[0] >= 1f)
                        timer[0].stop();
                });
                timer[0].start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (timer[0] != null && timer[0].isRunning())
                    timer[0].stop();
                progress[0] = 1f;

                timer[0] = new Timer(15, ev -> {
                    progress[0] -= 0.05f;
                    if (progress[0] < 0f)
                        progress[0] = 0f;

                    int r = (int) (bgStart.getRed() + progress[0] * (bgEnd.getRed() - bgStart.getRed()));
                    int g = (int) (bgStart.getGreen() + progress[0] * (bgEnd.getGreen() - bgStart.getGreen()));
                    int b = (int) (bgStart.getBlue() + progress[0] * (bgEnd.getBlue() - bgStart.getBlue()));
                    btn.setBackground(new Color(r, g, b));

                    int tr = (int) (textStart.getRed() + progress[0] * (textEnd.getRed() - textStart.getRed()));
                    int tg = (int) (textStart.getGreen() + progress[0] * (textEnd.getGreen() - textStart.getGreen()));
                    int tb = (int) (textStart.getBlue() + progress[0] * (textEnd.getBlue() - textStart.getBlue()));
                    btn.setForeground(new Color(tr, tg, tb));

                    if (progress[0] <= 0f)
                        timer[0].stop();
                });
                timer[0].start();
            }
        });

        return btn;
    }

    private JButton choiceButton(String text, boolean isSelectedDefault) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 50));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);

        Color borderDefault = Color.GRAY;
        Color borderSelected = new Color(0, 11, 61);
        Color textDefault = Color.GRAY;
        Color textSelected = Color.WHITE;

        if (isSelectedDefault) {
            btn.setBorder(BorderFactory.createLineBorder(borderSelected, 3, true));
            btn.setForeground(textSelected);
            btn.setBackground(borderSelected);
            selectedChoice = btn;
        } else {
            btn.setBorder(BorderFactory.createLineBorder(borderDefault, 1, true));
            btn.setForeground(textDefault);
            btn.setBackground(Color.WHITE);
        }

        final int duration = 300;
        final int fps = 60;
        final int totalFrames = duration / (1000 / fps);

        btn.addMouseListener(new MouseAdapter() {
            Timer hoverTimer;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn == selectedChoice)
                    return;
                if (hoverTimer != null && hoverTimer.isRunning())
                    hoverTimer.stop();

                hoverTimer = new Timer(1000 / fps, null);
                final int[] frame = { 0 };

                hoverTimer.addActionListener(ev -> {
                    float progress = (float) frame[0] / totalFrames;
                    int r = (int) (borderDefault.getRed()
                            + progress * (borderSelected.getRed() - borderDefault.getRed()));
                    int g = (int) (borderDefault.getGreen()
                            + progress * (borderSelected.getGreen() - borderDefault.getGreen()));
                    int b = (int) (borderDefault.getBlue()
                            + progress * (borderSelected.getBlue() - borderDefault.getBlue()));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(r, g, b), 3, true));

                    int tr = (int) (borderDefault.getRed() + progress * (borderSelected.getRed() - Color.WHITE.getRed()));
                    int tg = (int) (borderDefault.getGreen() + progress * (borderSelected.getGreen() - Color.WHITE.getGreen()));
                    int tb = (int) (borderDefault.getBlue() + progress * (borderSelected.getBlue() - Color.WHITE.getBlue()));

                    // Clamp between 0 and 255
                    tr = Math.max(0, Math.min(255, tr));
                    tg = Math.max(0, Math.min(255, tg));
                    tb = Math.max(0, Math.min(255, tb));

                    btn.setForeground(new Color(tr, tg, tb));

                    frame[0]++;
                    if (frame[0] > totalFrames)
                        hoverTimer.stop();
                });

                hoverTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn == selectedChoice)
                    return;
                if (hoverTimer != null && hoverTimer.isRunning())
                    hoverTimer.stop();

                hoverTimer = new Timer(1000 / fps, null);
                final int[] frame = { 0 };

                hoverTimer.addActionListener(ev -> {
                    float progress = (float) frame[0] / totalFrames;
                    int r = (int) (borderSelected.getRed()
                            - progress * (borderSelected.getRed() - borderDefault.getRed()));
                    int g = (int) (borderSelected.getGreen()
                            - progress * (borderSelected.getGreen() - borderDefault.getGreen()));
                    int b = (int) (borderSelected.getBlue()
                            - progress * (borderSelected.getBlue() - borderDefault.getBlue()));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(r, g, b), 1, true));

                    int tr = (int) (borderSelected.getRed()
                            - progress * (borderSelected.getRed() - textDefault.getRed()));
                    int tg = (int) (borderSelected.getGreen()
                            - progress * (borderSelected.getGreen() - textDefault.getGreen()));
                    int tb = (int) (borderSelected.getBlue()
                            - progress * (borderSelected.getBlue() - textDefault.getBlue()));
                    btn.setForeground(new Color(tr, tg, tb));

                    frame[0]++;
                    if (frame[0] > totalFrames)
                        hoverTimer.stop();
                });

                hoverTimer.start();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (btn == selectedChoice)
                    return;

                JButton old = selectedChoice;
                selectedChoice = btn;

                Timer deselectTimer = new Timer(1000 / fps, null);
                final int[] frame = { 0 };
                deselectTimer.addActionListener(ev -> {
                    float progress = (float) frame[0] / totalFrames;

                    int rOld = (int) (borderSelected.getRed()
                            - progress * (borderSelected.getRed() - borderDefault.getRed()));
                    int gOld = (int) (borderSelected.getGreen()
                            - progress * (borderSelected.getGreen() - borderDefault.getGreen()));
                    int bOld = (int) (borderSelected.getBlue()
                            - progress * (borderSelected.getBlue() - borderDefault.getBlue()));
                    old.setBorder(BorderFactory.createLineBorder(new Color(rOld, gOld, bOld), 1, true));

                    int trOld = (int) (textSelected.getRed()
                            - progress * (textSelected.getRed() - textDefault.getRed()));
                    int tgOld = (int) (textSelected.getGreen()
                            - progress * (textSelected.getGreen() - textDefault.getGreen()));
                    int tbOld = (int) (textSelected.getBlue()
                            - progress * (textSelected.getBlue() - textDefault.getBlue()));
                    old.setForeground(new Color(trOld, tgOld, tbOld));
                    old.setBackground(Color.WHITE);

                    frame[0]++;
                    if (frame[0] > totalFrames)
                        deselectTimer.stop();
                });
                deselectTimer.start();

                Timer selectTimer = new Timer(1000 / fps, null);
                final int[] frame2 = { 0 };
                selectTimer.addActionListener(ev -> {
                    float progress = (float) frame2[0] / totalFrames;

                    int r = (int) (borderDefault.getRed()
                            + progress * (borderSelected.getRed() - borderDefault.getRed()));
                    int g = (int) (borderDefault.getGreen()
                            + progress * (borderSelected.getGreen() - borderDefault.getGreen()));
                    int b = (int) (borderDefault.getBlue()
                            + progress * (borderSelected.getBlue() - borderDefault.getBlue()));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(r, g, b), 3, true));

                    Color startColor = btn.getForeground();
                    Color endColor = Color.WHITE;

                    int tr = (int) (startColor.getRed() + progress * (endColor.getRed() - startColor.getRed()));
                    int tg = (int) (startColor.getGreen() + progress * (endColor.getGreen() - startColor.getGreen()));
                    int tb = (int) (startColor.getBlue() + progress * (endColor.getBlue() - startColor.getBlue()));

                    btn.setForeground(new Color(tr, tg, tb));

                    int br = (int) (Color.WHITE.getRed() + progress * (borderSelected.getRed() - Color.WHITE.getRed()));
                    int bg = (int) (Color.WHITE.getGreen()
                            + progress * (borderSelected.getGreen() - Color.WHITE.getGreen()));
                    int bb = (int) (Color.WHITE.getBlue()
                            + progress * (borderSelected.getBlue() - Color.WHITE.getBlue()));
                    btn.setBackground(new Color(br, bg, bb));

                    frame2[0]++;
                    if (frame2[0] > totalFrames)
                        selectTimer.stop();
                });
                selectTimer.start();
            }
        });

        return btn;

    }

    class ApplicantLoginUI {
    private JPanel panel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public ApplicantLoginUI(JPanel panel) {
        this.panel = panel;
    }

    public void showUI() {
        // --- Fields ---
        idField = createTextField(830, 380, 310, 30);
        passwordField = createPasswordField(830, 459, 310, 30);

        // --- Labels ---
        JLabel AppID = new JLabel("Applicant ID");
        AppID.setBounds(830, 350, 200, 30);
        AppID.setFont(new Font("Segoe UI", Font.BOLD, 15));
        AppID.setForeground(Color.BLACK);

        JLabel AppPass = new JLabel("Password");
        AppPass.setBounds(830, 429, 200, 30);
        AppPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        AppPass.setForeground(Color.BLACK);

        JLabel DHA = new JLabel("<html><u>Don't Have an Account?</u></html>");
        DHA.setBounds(875, 588, 200, 30);

        // --- Buttons ---
        loginButton = logincreateButton("Login", 910, 545, 150, 35, 15);
        signupButton = signupcreateButton("Sign Up", 1020, 586, 60, 30, 12);

        // --- Add to panel ---
        panel.add(idField);
        panel.add(passwordField);
        panel.add(AppID);
        panel.add(AppPass);
        panel.add(DHA);
        panel.add(loginButton);
        panel.add(signupButton);

        panel.revalidate();
        panel.repaint();

        // --- Signup button logic ---
        signupButton.addActionListener(e -> {
            new ApplicantSignupUI().setVisible(true);
        });

        // --- Login button logic ---
        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter both ID and Password.",
                        "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Call login method from Applicant class
            Applicant a = Applicant.loginGUI(id, password);

            if (a != null) {
                // Open dashboard with all required fields
                new ApplicantDashboardUI(
                        a.getId(),
                        a.getFullName(),
                        a.getBirthdate(),
                        a.getAge(),
                        a.getProgram(),
                        a.getAddress(),
                        a.getEmail(),
                        a.getPhone(),
                        a.getStatus(),
                        a.getExamDate(),
                        a.getExamTime(),
                        a.getExamRoom(),
                        a.getSchool(),
                        a.getFathersName(),
                        a.getMothersName(),
                        a.getSchoolYear(),
                        a.getYearLevel(),
                        a.getCollege()
                ).setVisible(true);

                // Close MainUI
                SwingUtilities.getWindowAncestor(panel).dispose();
            } else {
                JOptionPane.showMessageDialog(panel,
                        "Invalid ID or Password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}


    class StudentLoginUI {
        private JPanel panel;
        private JTextField idField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public StudentLoginUI(JPanel panel) {
            this.panel = panel;
        }

        public void showUI() {
            idField = createTextField(830, 380, 310, 30);
            passwordField = createPasswordField(830, 459, 310, 30);

             JLabel StudID = new JLabel("Student ID");
            StudID.setBounds(830, 350, 200, 30);
            StudID.setFont(new Font("Segoe UI", Font.BOLD, 15));
            StudID.setForeground(Color.BLACK);

            JLabel StudPass = new JLabel("Password");
            StudPass.setBounds(830, 429, 200, 30);
            StudPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
            StudPass.setForeground(Color.BLACK);
            loginButton = logincreateButton("Login", 910, 545, 150, 35, 15);

            loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if(id.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(panel,
                    "Please enter both ID and Password.",
                    "Missing Fields",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Call StudentGUI login
            Student s = Student.authenticate(id, password);

            if(s != null){
                new StudentDashboardGUI(s).setVisible(true); // open dashboard
                SwingUtilities.getWindowAncestor(panel).dispose(); // close MainUI
            } else {
                JOptionPane.showMessageDialog(panel, 
                    "Invalid ID or Password!", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });


            panel.add(idField);
            panel.add(passwordField);
            panel.add(loginButton);
            panel.add(StudID);
            panel.add(StudPass);

            panel.revalidate();
            panel.repaint();
        }
    }

  class FacultyLoginUI {
    private JPanel panel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public FacultyLoginUI(JPanel panel) {
        this.panel = panel;
    }

    public void showUI() {
        idField = createTextField(830, 380, 310, 30);
        passwordField = createPasswordField(830, 459, 310, 30);

        JLabel FacID = new JLabel("Faculty ID");
        FacID.setBounds(830, 350, 200, 30);
        FacID.setFont(new Font("Segoe UI", Font.BOLD, 15));
        FacID.setForeground(Color.BLACK);

        JLabel FacPass = new JLabel("Password");
        FacPass.setBounds(830, 429, 200, 30);
        FacPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        FacPass.setForeground(Color.BLACK);

        loginButton = logincreateButton("Login", 910, 545, 150, 35, 15);
        loginButton.addActionListener(e -> {
            String facID = idField.getText().trim();
            String facPass = new String(passwordField.getPassword()).trim();

            if (facID.isEmpty() || facPass.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter both ID and Password.",
                        "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- File check (same as old logic) ---
            java.util.List<String> lines = Utils.readFile("data/FACULTY/ProfessorsMasterList.txt");
            if (lines == null) {
                JOptionPane.showMessageDialog(panel,
                        "Faculty master list not found.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean found = false;
            String profName = null;
            java.util.List<String> subjectsList = new java.util.ArrayList<>();

            for (String ln : lines) {
                String[] p = ln.split("\\|", -1);
                if (p.length < 3) continue;

                String fID = p[0].trim();
                String fPW = p[1].trim();

                if (facID.equalsIgnoreCase(fID) && facPass.equals(fPW)) {
                    found = true;
                    profName = p[2].trim();

                    if (p.length >= 5 && !p[4].trim().isEmpty()) {
                        subjectsList = java.util.Arrays.asList(p[4].split(","));
                    } else if (p.length >= 4 && !p[3].trim().isEmpty()) {
                        subjectsList = java.util.Arrays.asList(p[3].split(","));
                    }
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(panel,
                        "Invalid Faculty ID or Password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Utils.ensureDir("data/FACULTY/FACULTY_MEMBERS/" + facID);

            JOptionPane.showMessageDialog(panel,
                    "Welcome, " + profName + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            new FacultyDashboardUI(facID, profName, subjectsList).setVisible(true);
            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        panel.add(idField);
        panel.add(passwordField);
        panel.add(FacID);
        panel.add(FacPass);
        panel.add(loginButton);

        panel.revalidate();
        panel.repaint();
    }
}


    class AdminLoginUI {
    private JPanel panel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public AdminLoginUI(JPanel panel) {
        this.panel = panel;
    }

    public void showUI() {
        // Text fields
        idField = createTextField(830, 380, 310, 30);
        passwordField = createPasswordField(830, 459, 310, 30);

        // Labels
        JLabel AdminID = new JLabel("Admin ID");
        AdminID.setBounds(830, 350, 200, 30);
        AdminID.setFont(new Font("Segoe UI", Font.BOLD, 15));
        AdminID.setForeground(Color.BLACK);

        JLabel AdminPass = new JLabel("Password");
        AdminPass.setBounds(830, 429, 200, 30);
        AdminPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        AdminPass.setForeground(Color.BLACK);

        // Login button
        loginButton = logincreateButton("Login", 910, 545, 150, 35, 15);

        loginButton.addActionListener(e -> {
            String adminID = idField.getText().trim();
            String adminPass = new String(passwordField.getPassword()).trim();

            if (adminID.isEmpty() || adminPass.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter both ID and Password.",
                        "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- File check for admins ---
            java.util.List<String> lines = Utils.readFile("data/ADMIN/admin.txt");
            if (lines == null) {
                JOptionPane.showMessageDialog(panel,
                        "Admin master list not found.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean found = false;
            String adminName = null;

            for (String ln : lines) {
                String[] p = ln.split("\\|", -1);
                if (p.length < 2) continue;

                String aID = p[0].trim();
                String aPW = p[1].trim();

                if (adminID.equalsIgnoreCase(aID) && adminPass.equals(aPW)) {
                    found = true;
                    adminName = (p.length >= 3) ? p[2].trim() : "Admin";
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(panel,
                        "Invalid Admin ID or Password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Optional: ensure admin folder exists
            Utils.ensureDir("data/ADMIN/ADMIN_MEMBERS/" + adminID);

            // Open Admin dashboard
           new AdminDashboardUI().setVisible(true);
            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        // Add components to panel
        panel.add(idField);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(AdminID);
        panel.add(AdminPass);

        panel.revalidate();
        panel.repaint();
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}