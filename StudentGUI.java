import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class StudentGUI {

    private JPanel contentPanel;
    private Student student;

    // ---------- PROFILE ----------
    public static class ProfilePanel extends JPanel {
        public ProfilePanel(Student student) {
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(Color.WHITE);
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            Font labelFont = new Font("Segoe UI", Font.PLAIN, 24);
            Font infoFont = new Font("Segoe UI", Font.BOLD, 26);

            // Spacing
            int paddingLeft = 40;
            int col1 = paddingLeft;
            int col2 = paddingLeft + 560;
            int col3 = paddingLeft + 1120;

            int y = 30;
            int gap = 85;

            // -------- Row 1 --------
            card.add(labeledInfo("Full Name",
                    student.getFirstName() + " " + student.getLastName(),
                    col1, y, labelFont, infoFont));

            // -------- Row 2 --------
            card.add(labeledInfo("Age", student.getAge(), col1, y + gap, labelFont, infoFont));
            card.add(labeledInfo("Birthdate", student.getBirthdate(), col2, y + gap, labelFont, infoFont));
            card.add(labeledInfo("School", student.getSchool(), col3, y + gap, labelFont, infoFont));

            // -------- Row 3 --------
            card.add(labeledInfo("Phone Number", student.getPhone(), col1, y + gap * 2, labelFont, infoFont));
            card.add(labeledInfo("Email Address", student.getEmail(), col2, y + gap * 2, labelFont, infoFont));
            card.add(labeledInfo("Program", student.getProgram(), col3, y + gap * 2, labelFont, infoFont));

            // -------- Row 4 --------
            card.add(labeledInfo("Father's Name", student.getFather(), col1, y + gap * 3, labelFont, infoFont));
            card.add(labeledInfo("Mother's Name", student.getMother(), col2, y + gap * 3, labelFont, infoFont));

            // -------- Row 5 (Address - full width) --------
            card.add(labeledInfo("Address", student.getAddress(), col1, y + gap * 4, labelFont, infoFont));

            // -------- Row 6 --------
            card.add(labeledInfo("School Year", student.getSchoolYear(), col1, y + gap * 5, labelFont, infoFont));
            card.add(labeledInfo("Year Level", student.getYearLevel(), col2, y + gap * 5, labelFont, infoFont));
            card.add(labeledInfo("College", student.getCollege(), col3, y + gap * 5, labelFont, infoFont));
        }

        private JPanel labeledInfo(String label, String value, int x, int y, Font lf, Font vf) {
            JPanel p = new JPanel(null);
            p.setOpaque(false);
            p.setBounds(x, y, 520, 70);

            JLabel lbl = new JLabel(label);
            lbl.setFont(lf);
            lbl.setForeground(new Color(80, 80, 80));
            lbl.setBounds(0, 0, 520, 24);
            p.add(lbl);

            JLabel val = new JLabel(value == null ? "" : value);
            val.setFont(vf);
            val.setForeground(new Color(10, 20, 70));
            val.setBounds(0, 26, 520, 36);
            p.add(val);

            return p;
        }

        // RoundedPanel (same as yours)
        static class RoundedPanel extends JPanel {
            int arc;
            RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }


    // ---------- MESSAGE ----------
    public static class MessagePanel extends JPanel {
        private final Student student;
        private JPanel rightContent;

        public MessagePanel(Student student) {
            this.student = student;
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(new Color(250, 245, 240));
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            // ===================== LEFT SIDE MENU =====================
            RoundedPanel left = new RoundedPanel(20);
            left.setBackground(new Color(240, 235, 225));
            left.setBounds(20, 20, 280, 560);
            left.setLayout(null);
            card.add(left);

            JButton inboxBtn = new JButton("Inbox");
            inboxBtn.setBounds(30, 40, 220, 60);
            inboxBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            inboxBtn.setBackground(new Color(215, 210, 205)); // selected look
            inboxBtn.setForeground(new Color(5, 15, 50));
            inboxBtn.setBorderPainted(false);
            left.add(inboxBtn);

            JButton composeBtn = new JButton("Compose");
            composeBtn.setBounds(30, 120, 220, 60);
            composeBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            composeBtn.setBackground(new Color(230, 225, 220)); // unselected
            composeBtn.setForeground(new Color(150, 150, 150));
            composeBtn.setBorderPainted(false);
            left.add(composeBtn);

            // ===================== RIGHT CONTENT AREA =====================
            rightContent = new JPanel(null);
            rightContent.setOpaque(false);
            rightContent.setBounds(330, 20, 1420, 560);
            card.add(rightContent);

            // Default = inbox
            showInbox();

            inboxBtn.addActionListener(e -> {
                inboxBtn.setBackground(new Color(215, 210, 205));
                composeBtn.setBackground(new Color(230, 225, 220));
                inboxBtn.setForeground(new Color(5, 15, 50));
                composeBtn.setForeground(new Color(150, 150, 150));
                showInbox();
            });

            composeBtn.addActionListener(e -> {
                composeBtn.setBackground(new Color(215, 210, 205));
                inboxBtn.setBackground(new Color(230, 225, 220));
                composeBtn.setForeground(new Color(5, 15, 50));
                inboxBtn.setForeground(new Color(150, 150, 150));
                showCompose();
            });
        }

        // ===========================================================
        // Inbox View
        // ===========================================================
        private void showInbox() {
            rightContent.removeAll();

            java.util.List<String> lines = loadInbox();
            int y = 20;

            if (lines == null || lines.isEmpty()) {
                JLabel empty = new JLabel("No messages available.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                empty.setForeground(new Color(5, 15, 50));
                empty.setBounds(20, 20, 600, 40);
                rightContent.add(empty);
            } else {
                for (String line : lines) {

                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 4) continue;

                    String senderName = parts[2];
                    String date = parts[1];
                    String msg = Utils.decrypt(parts[3]);

                    JPanel msgBox = createMessageBox(senderName, date, msg);
                    msgBox.setBounds(20, y, 1380, 150);
                    rightContent.add(msgBox);

                    y += 170;
                }
            }

            rightContent.revalidate();
            rightContent.repaint();
        }

        private JPanel createMessageBox(String sender, String date, String msg) {

            RoundedPanel p = new RoundedPanel(25);
            p.setBackground(new Color(234, 223, 207));
            p.setLayout(null);

            JLabel head = new JLabel("From: " + sender + "   |   " + date);
            head.setFont(new Font("Segoe UI", Font.BOLD, 22));
            head.setForeground(new Color(5, 15, 50));
            head.setBounds(30, 20, 1200, 30);
            p.add(head);

            JTextArea ta = new JTextArea(msg);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            ta.setBackground(new Color(234, 223, 207));
            ta.setForeground(new Color(5, 15, 50));

            JScrollPane sp = new JScrollPane(ta);
            sp.setBounds(30, 60, 1300, 70);
            sp.setBorder(null);
            p.add(sp);

            return p;
        }

        // ===========================================================
        // Compose View
        // ===========================================================
        private void showCompose() {
            rightContent.removeAll();

            JLabel toLbl = new JLabel("To :");
            toLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            toLbl.setForeground(new Color(5, 15, 50));
            toLbl.setBounds(20, 10, 200, 40);
            rightContent.add(toLbl);

            RoundedPanel toBox = new RoundedPanel(20);
            toBox.setBackground(new Color(234, 223, 207));
            toBox.setBounds(20, 55, 1380, 55);
            toBox.setLayout(null);
            rightContent.add(toBox);

            JTextField toField = new JTextField();
            toField.setBounds(20, 12, 1340, 32);
            toField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            toField.setBorder(null);
            toField.setBackground(new Color(234, 223, 207));
            toBox.add(toField);

            JLabel msgLbl = new JLabel("Message :");
            msgLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            msgLbl.setForeground(new Color(5, 15, 50));
            msgLbl.setBounds(20, 130, 200, 40);
            rightContent.add(msgLbl);

            RoundedPanel msgBox = new RoundedPanel(20);
            msgBox.setLayout(null);
            msgBox.setBackground(new Color(234, 223, 207));
            msgBox.setBounds(20, 175, 1380, 300);
            rightContent.add(msgBox);

            JTextArea msgArea = new JTextArea();
            msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            msgArea.setBorder(null);
            msgArea.setBackground(new Color(234, 223, 207));
            msgArea.setLineWrap(true);
            msgArea.setWrapStyleWord(true);

            JScrollPane sp = new JScrollPane(msgArea);
            sp.setBounds(20, 20, 1340, 260);
            sp.setBorder(null);
            msgBox.add(sp);

            JButton send = new JButton("Send");
            send.setFont(new Font("Segoe UI", Font.BOLD, 20));
            send.setForeground(new Color(5, 15, 50));
            send.setBackground(new Color(234, 223, 207));
            send.setBorderPainted(false);
            send.setBounds(1220, 490, 180, 55);
            rightContent.add(send);

            // SEND ACTION SAME AS YOUR ORIGINAL VERSION
            send.addActionListener(e -> {
                String toId = toField.getText().trim();
                String message = msgArea.getText().trim();

                if (toId.isEmpty() || message.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                Student recipient = Student.findById(toId);
                if (recipient == null) {
                    JOptionPane.showMessageDialog(this, "Recipient not found.");
                    return;
                }

                // build inbox path
                String inboxPath = String.format("data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                        recipient.getSchoolYear().replace(" ", ""),
                        recipient.getYearLevel().replace(" ", ""),
                        recipient.getCollege().toUpperCase(),
                        recipient.getProgram().toUpperCase(),
                        recipient.getSection(),
                        recipient.getId()
                );

                List<String> inbox = Utils.readFile(inboxPath);
                if (inbox == null) inbox = new ArrayList<>();

                String date = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
                String raw = student.getId() + "|" + date + "|" +
                        (student.getFirstName() + " " + student.getLastName()) +
                        "|" + Utils.encrypt(message);

                inbox.add(raw);
                Utils.writeRawFile(inboxPath, inbox);
                JOptionPane.showMessageDialog(this, "Message sent!");

                showCompose();
            });

            rightContent.revalidate();
            rightContent.repaint();
        }

        private List<String> loadInbox() {
            String path = String.format(
                    "data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                    student.getSchoolYear().replace(" ", ""),
                    student.getYearLevel().replace(" ", ""),
                    student.getCollege().toUpperCase(),
                    student.getProgram().toUpperCase(),
                    student.getSection(),
                    student.getId()
            );
            return Utils.readFile(path);
        }

        // RoundedPanel
        static class RoundedPanel extends JPanel {
            int arc;
            RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }


    // ---------- SCHEDULE ----------
    public static class SchedulePanel extends JPanel {

        public SchedulePanel(Student student) {
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(Color.WHITE);
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
            Font rowFont = new Font("Segoe UI", Font.PLAIN, 20);

            int col1 = 60;      // Date & Time
            int col2 = 750;     // Course Title
            int col3 = 1450;    // Professor

            // ------- HEADER -------
            card.add(makeHeader("Date & Time", col1, 40, headerFont));
            card.add(makeHeader("Course Title", col2, 40, headerFont));
            card.add(makeHeader("Professor", col3, 40, headerFont));

            // ------ LOAD SCHEDULE ------
            String sy = student.getSchoolYear();
            String yearLevel = student.getYearLevel();
            String course = student.getProgram();
            String section = student.getSection();
            String dept = student.getCollege();

            String crsAndSec = course + section;
            String schedPath =
                    "data/STUDENTS/" + sy.replace(" ", "") + "/" + yearLevel + "/"
                            + dept + "/" + course + "/" + section + "/"
                            + crsAndSec + "_Schedule.txt";

            java.util.List<String[]> rows = new ArrayList<>();

            try {
                File f = new File(schedPath);
                if (f.exists() && Files.size(f.toPath()) > 0) {
                    List<String> lines = Files.readAllLines(f.toPath());
                    for (String line : lines) {
                        String[] p = line.split("\\|");
                        if (p.length >= 6) {
                            rows.add(new String[]{
                                    p[3] + " " + p[4], // date + time
                                    p[2],              // course title
                                    p[5]               // professor
                            });
                        }
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            // DISPLAY ROWS
            int y = 110;
            int gap = 60;

            for (String[] r : rows) {
                card.add(makeRow(r[0], col1, y, rowFont));
                card.add(makeRow(r[1], col2, y, rowFont));
                card.add(makeRow(r[2], col3, y, rowFont));
                y += gap;
            }
        }

        // HEADER LABEL
        private JLabel makeHeader(String text, int x, int y, Font f) {
            JLabel h = new JLabel(text);
            h.setFont(f);
            h.setForeground(new Color(80, 90, 100));
            h.setBounds(x, y, 500, 30);
            return h;
        }

        // ROW CELL
        private RoundedLabel makeRow(String text, int x, int y, Font f) {
            RoundedLabel row = new RoundedLabel(text);
            row.setFont(f);
            row.setForeground(new Color(25, 30, 60));
            row.setBackground(new Color(240, 235, 230));
            row.setBounds(x, y, 500, 45);
            return row;
        }

        // Rounded Label
        static class RoundedLabel extends JLabel {
            RoundedLabel(String t) {
                super(t);
                setOpaque(false);
                setHorizontalAlignment(SwingConstants.CENTER);
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2.dispose();
            }
        }

        // Rounded Panel
        static class RoundedPanel extends JPanel {
            int arc;
            RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }


    // ---------- GRADES ----------
    public static class GradesPanel extends JPanel {

        public GradesPanel(Student student) {
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(Color.WHITE);
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
            Font rowFont = new Font("Segoe UI", Font.PLAIN, 20);

            int col1 = 60;      // Course
            int col2 = 750;     // Professor
            int col3 = 1450;    // Grade

            // HEADER ONLY — NO TITLE
            card.add(makeHeader("Course", col1, 40, headerFont));
            card.add(makeHeader("Professor", col2, 40, headerFont));
            card.add(makeHeader("Grade", col3, 40, headerFont));

            // LOAD GRADES
            String sy = student.getSchoolYear();
            String yearLevel = student.getYearLevel();
            String course = student.getProgram();
            String section = student.getSection();
            String dept = student.getCollege();

            String studentDir =
                    "data/STUDENTS/" + sy.replace(" ", "") + "/" +
                            yearLevel + "/" + dept + "/" + course + "/" + section + "/";

            String fileName = student.getId() + "_grades.txt";
            java.util.List<String[]> rows = new ArrayList<>();

            try {
                File f = new File(studentDir + fileName);
                if (f.exists() && Files.size(f.toPath()) > 0) {

                    List<String> lines = Files.readAllLines(f.toPath());

                    for (String line : lines) {
                        String[] p = line.split("\\|");

                        String courseName = (p.length > 2) ? p[2].trim() : "";
                        String professor = (p.length > 1) ? p[1].trim() : "";
                        String grade = "";

                        for (int i = 3; i < p.length; i++) {
                            if (p[i].contains(":")) {
                                grade = p[i].split(":")[1]; // get numeric grade
                            }
                        }

                        rows.add(new String[]{courseName, professor, grade});
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            // DISPLAY ROWS
            int y = 110;
            int gap = 60;

            for (String[] r : rows) {
                card.add(makeRow(r[0], col1, y, rowFont));
                card.add(makeRow(r[1], col2, y, rowFont));
                card.add(makeRow(r[2], col3, y, rowFont));
                y += gap;
            }
        }

        // HEADER LABEL
        private JLabel makeHeader(String text, int x, int y, Font f) {
            JLabel h = new JLabel(text);
            h.setFont(f);
            h.setForeground(new Color(80, 90, 100));
            h.setBounds(x, y, 500, 30);
            return h;
        }

        // ROW CELL
        private RoundedLabel makeRow(String text, int x, int y, Font f) {
            RoundedLabel row = new RoundedLabel(text);
            row.setFont(f);
            row.setForeground(new Color(25, 30, 60));
            row.setBackground(new Color(240, 235, 230));
            row.setBounds(x, y, 500, 45);
            return row;
        }

        // Rounded label
        static class RoundedLabel extends JLabel {
            RoundedLabel(String t) {
                super(t);
                setOpaque(false);
                setHorizontalAlignment(SwingConstants.CENTER);
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2.dispose();
            }
        }

        // Rounded Panel
        static class RoundedPanel extends JPanel {
            int arc;
            RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }

    // ---------- ACADEMIC PANEL ----------
    public static class AcademicPanel extends JPanel {

        private final Student student;
        private final JPanel rightContent;
        private final CardLayout rightLayout;

        private final JButton calendarBtn;
        private final JButton activityBtn;
        private final JButton assignmentsBtn;
        private final JButton notesBtn;

        // calendar state
        private Calendar cal;
        private JLabel monthYearLabel;
        private JLabel[] dayCells;

        public AcademicPanel(Student student) {
            this.student = student;
            setLayout(null);
            setOpaque(false);

            RoundedPanel card = new RoundedPanel(20);
            card.setBackground(new Color(250, 245, 240)); // same as other panels
            card.setBounds(20, 10, 1770, 610);
            card.setLayout(null);
            add(card);

            // ===== LEFT SIDE (Calendar / Activity / Assignments / Notes) =====
            RoundedPanel left = new RoundedPanel(20);
            left.setBackground(new Color(244, 238, 228));        // light beige
            left.setBounds(20, 20, 280, 560);
            left.setLayout(null);
            card.add(left);

            Font navFont = new Font("Segoe UI", Font.BOLD, 22);

            calendarBtn = createNavButton("Calendar", 40, navFont);
            activityBtn = createNavButton("Activity", 120, navFont);
            assignmentsBtn = createNavButton("View Assignments", 200, navFont);
            notesBtn = createNavButton("Notes", 280, navFont);

            left.add(calendarBtn);
            left.add(activityBtn);
            left.add(assignmentsBtn);
            left.add(notesBtn);

            // ===== RIGHT CONTENT (CardLayout) =====
            rightLayout = new CardLayout();
            rightContent = new JPanel(rightLayout);
            rightContent.setOpaque(false);
            rightContent.setBounds(330, 20, 1420, 560);
            card.add(rightContent);

            JPanel calendarView = buildCalendarView();
            JPanel activityView = buildActivityView();
            JPanel assignmentsView = buildAssignmentsView();
            JPanel notesView = buildNotesView();

            rightContent.add(calendarView, "CAL");
            rightContent.add(activityView, "ACT");
            rightContent.add(assignmentsView, "ASSIGN");
            rightContent.add(notesView, "NOTES");

            // Default selection
            selectNav("CAL");

            calendarBtn.addActionListener(e -> selectNav("CAL"));
            activityBtn.addActionListener(e -> selectNav("ACT"));
            assignmentsBtn.addActionListener(e -> selectNav("ASSIGN"));
            notesBtn.addActionListener(e -> selectNav("NOTES"));
        }

        // === NAV BUTTONS ===
        private JButton createNavButton(String text, int y, Font font) {
            JButton b = new JButton(text);
            b.setBounds(30, y, 220, 60);
            b.setFont(font);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setMargin(new Insets(0, 25, 0, 0));
            return b;
        }

        private void selectNav(String key) {
            Color selectedBg = new Color(215, 210, 205);
            Color unselectedBg = new Color(230, 225, 220);
            Color selectedFg = new Color(5, 15, 50);
            Color unselectedFg = new Color(150, 150, 150);

            JButton[] all = {calendarBtn, activityBtn, assignmentsBtn, notesBtn};
            for (JButton b : all) {
                b.setBackground(unselectedBg);
                b.setForeground(unselectedFg);
            }
            JButton active =
                    "CAL".equals(key) ? calendarBtn :
                            "ACT".equals(key) ? activityBtn :
                                    "ASSIGN".equals(key) ? assignmentsBtn : notesBtn;
            active.setBackground(selectedBg);
            active.setForeground(selectedFg);

            rightLayout.show(rightContent, key);
        }

        // === CALENDAR VIEW ===
        private JPanel buildCalendarView() {
            JPanel root = new JPanel(null);
            root.setOpaque(false);

            // main calendar box
            RoundedPanel calBox = new RoundedPanel(25);
            calBox.setBackground(new Color(245, 236, 222));   // beige like mockup
            calBox.setLayout(null);
            calBox.setBounds(40, 30, 900, 500);
            root.add(calBox);

            monthYearLabel = new JLabel("", SwingConstants.CENTER);
            monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            monthYearLabel.setForeground(new Color(8, 20, 72));
            monthYearLabel.setBounds(0, 20, 900, 40);
            calBox.add(monthYearLabel);

            // Month navigation (up / down arrows)
            JButton prev = new JButton("▲");
            JButton next = new JButton("▼");
            prev.setBounds(650, 18, 60, 44);
            next.setBounds(720, 18, 60, 44);
            for (JButton b : new JButton[]{prev, next}) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setFont(new Font("Segoe UI", Font.BOLD, 22));
                b.setBackground(new Color(235, 224, 205));
            }
            calBox.add(prev);
            calBox.add(next);

            JPanel grid = new JPanel(null);
            grid.setOpaque(false);
            grid.setBounds(60, 80, 780, 380);
            calBox.add(grid);

            Font dayHeaderFont = new Font("Segoe UI", Font.BOLD, 16);
            String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            int cellW = 780 / 7;
            int cellH = 55;

            // day headers
            for (int i = 0; i < 7; i++) {
                JLabel h = new JLabel(days[i], SwingConstants.CENTER);
                h.setFont(dayHeaderFont);
                h.setForeground(new Color(25, 30, 60));
                h.setBounds(i * cellW, 0, cellW, cellH);
                grid.add(h);
            }

            dayCells = new JLabel[42];
            Font dateFont = new Font("Segoe UI", Font.PLAIN, 18);

            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 7; col++) {
                    int idx = row * 7 + col;
                    JLabel cell = new JLabel("", SwingConstants.CENTER);
                    cell.setFont(dateFont);
                    cell.setOpaque(true);
                    cell.setBackground(new Color(245, 239, 232));
                    cell.setForeground(new Color(5, 15, 50));
                    cell.setBounds(col * cellW, (row + 1) * cellH, cellW, cellH);
                    cell.setBorder(BorderFactory.createLineBorder(new Color(220, 210, 200)));
                    dayCells[idx] = cell;
                    grid.add(cell);
                }
            }

            cal = Calendar.getInstance();
            updateCalendarGrid();

            prev.addActionListener(e -> {
                cal.add(Calendar.MONTH, -1);
                updateCalendarGrid();
            });
            next.addActionListener(e -> {
                cal.add(Calendar.MONTH, 1);
                updateCalendarGrid();
            });

            // SUMMARY BOX (right side)
            RoundedPanel summaryBox = new RoundedPanel(25);
            summaryBox.setBackground(new Color(245, 236, 222));
            summaryBox.setLayout(null);
            summaryBox.setBounds(980, 60, 380, 410);
            root.add(summaryBox);

            JLabel sTitle = new JLabel("Summary:");
            sTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            sTitle.setForeground(new Color(8, 20, 72));
            sTitle.setBounds(30, 25, 300, 30);
            summaryBox.add(sTitle);

            JTextArea summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            summaryArea.setBackground(new Color(245, 236, 222));
            summaryArea.setForeground(new Color(5, 15, 50));

            // content can be replaced with real StudentAcads events if you have them
            summaryArea.setText("• No recorded academic events for this month.");

            JScrollPane sp = new JScrollPane(summaryArea);
            sp.setBorder(null);
            sp.setBounds(30, 70, 320, 300);
            summaryBox.add(sp);

            return root;
        }

        private void updateCalendarGrid() {
            // month/year label
            SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy");
            monthYearLabel.setText(fmt.format(cal.getTime()).toUpperCase());

            Calendar temp = (Calendar) cal.clone();
            temp.set(Calendar.DAY_OF_MONTH, 1);
            int firstDay = temp.get(Calendar.DAY_OF_WEEK) - 1; // 0..6
            int maxDay = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (JLabel cell : dayCells) {
                cell.setText("");
                cell.setBackground(new Color(245, 239, 232));
            }

            Calendar today = Calendar.getInstance();

            for (int d = 1; d <= maxDay; d++) {
                int index = firstDay + d - 1;
                if (index < 0 || index >= dayCells.length) continue;
                JLabel c = dayCells[index];
                c.setText(String.valueOf(d));

                // highlight today
                if (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                        && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH) == d) {
                    c.setBackground(new Color(222, 214, 204)); // circle-ish highlight
                }
            }
        }

        // === ACTIVITY VIEW ===
        private JPanel buildActivityView() {
            JPanel root = new JPanel(null);
            root.setOpaque(false);

            RoundedPanel listBox = new RoundedPanel(25);
            listBox.setBackground(new Color(245, 236, 222));
            listBox.setBounds(60, 40, 1280, 460);
            listBox.setLayout(null);
            root.add(listBox);

            java.util.List<String> inbox = loadInboxForActivity();
            int y = 40;
            int gap = 55;

            if (inbox == null || inbox.isEmpty()) {
                JLabel none = new JLabel("No recent activities.", SwingConstants.LEFT);
                none.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                none.setForeground(new Color(5, 15, 50));
                none.setBounds(50, 40, 600, 40);
                listBox.add(none);
            } else {
                int max = Math.min(7, inbox.size());
                for (int i = 0; i < max; i++) {
                    String line = inbox.get(i);
                    String[] p = line.split("\\|", -1);
                    if (p.length < 4) continue;
                    String sender = p[2];
                    String date = p[1];

                    String text = "• " + sender + " sent you a message on " + date;
                    RoundedPanel bar = new RoundedPanel(25);
                    bar.setBackground(new Color(234, 223, 207));
                    bar.setBounds(40, y, 1200, 40);
                    bar.setLayout(null);

                    JLabel lbl = new JLabel(text);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                    lbl.setForeground(new Color(5, 15, 50));
                    lbl.setBounds(25, 5, 1150, 30);
                    bar.add(lbl);

                    listBox.add(bar);
                    y += gap;
                }
            }

            return root;
        }

        private java.util.List<String> loadInboxForActivity() {
            String path = String.format(
                    "data/STUDENTS/%s/%s/%s/%s/%s/%s_inbox.txt",
                    student.getSchoolYear().replace(" ", ""),
                    student.getYearLevel().replace(" ", ""),
                    student.getCollege().toUpperCase(),
                    student.getProgram().toUpperCase(),
                    student.getSection(),
                    student.getId()
            );
            return Utils.readFile(path);
        }

        // === ASSIGNMENTS VIEW ===
        private JPanel buildAssignmentsView() {
            JPanel root = new JPanel(null);
            root.setOpaque(false);

            // top tab bar (Upcoming / Past Due / Completed)
            JPanel tabs = new JPanel(null);
            tabs.setOpaque(false);
            tabs.setBounds(40, 0, 1200, 80);
            root.add(tabs);

            Font tabFont = new Font("Segoe UI", Font.BOLD, 22);

            JButton upcoming = new JButton("Upcoming");
            JButton pastDue = new JButton("Past Due");
            JButton completed = new JButton("Completed");

            JButton[] btns = {upcoming, pastDue, completed};
            String[] keys = {"UP", "PAST", "DONE"};

            int x = 40;
            for (JButton b : btns) {
                b.setBounds(x, 30, 200, 40);
                b.setFont(tabFont);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setContentAreaFilled(false);
                b.setHorizontalAlignment(SwingConstants.LEFT);
                x += 220;
                tabs.add(b);
            }

            JPanel content = new JPanel(new CardLayout());
            content.setOpaque(false);
            content.setBounds(40, 80, 1320, 440);
            root.add(content);

            JPanel upPanel = buildAssignmentsListPanel("No upcoming assignments.");
            JPanel pastPanel = buildAssignmentsListPanel("No past-due assignments.");
            JPanel donePanel = buildAssignmentsListPanel("No completed assignments.");

            content.add(upPanel, "UP");
            content.add(pastPanel, "PAST");
            content.add(donePanel, "DONE");

            CardLayout layout = (CardLayout) content.getLayout();

            Runnable refreshTabs = () -> {
                Font normal = new Font("Segoe UI", Font.PLAIN, 22);
                Color dark = new Color(8, 20, 72);
                Color light = new Color(170, 170, 170);

                upcoming.setFont(normal);
                pastDue.setFont(normal);
                completed.setFont(normal);

                upcoming.setForeground(light);
                pastDue.setForeground(light);
                completed.setForeground(light);
            };

            ActionListener listener = e -> {
                refreshTabs.run();
                String cmd = e.getActionCommand();
                layout.show(content, cmd);
                JButton src = (JButton) e.getSource();
                src.setFont(new Font("Segoe UI", Font.BOLD, 22));
                src.setForeground(new Color(8, 20, 72));
            };

            for (int i = 0; i < btns.length; i++) {
                btns[i].setActionCommand(keys[i]);
                btns[i].addActionListener(listener);
            }

            // default
            refreshTabs.run();
            upcoming.doClick();

            return root;
        }

        private JPanel buildAssignmentsListPanel(String emptyText) {
            JPanel root = new JPanel(null);
            root.setOpaque(false);

            RoundedPanel card1 = new RoundedPanel(25);
            card1.setBackground(new Color(245, 236, 222));
            card1.setBounds(40, 40, 1240, 160);
            card1.setLayout(null);
            root.add(card1);

            RoundedPanel card2 = new RoundedPanel(25);
            card2.setBackground(new Color(245, 236, 222));
            card2.setBounds(40, 220, 1240, 160);
            card2.setLayout(null);
            root.add(card2);

            JLabel lbl = new JLabel(emptyText, SwingConstants.LEFT);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            lbl.setForeground(new Color(5, 15, 50));
            lbl.setBounds(50, 60, 1100, 30);
            card1.add(lbl);

            return root;
        }

        // === NOTES VIEW ===
        private JPanel buildNotesView() {
            JPanel root = new JPanel(null);
            root.setOpaque(false);

            RoundedPanel box = new RoundedPanel(25);
            box.setBackground(new Color(245, 236, 222));
            box.setBounds(60, 40, 1280, 440);
            box.setLayout(null);
            root.add(box);

            JTextArea notesArea = new JTextArea();
            notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setBackground(new Color(245, 236, 222));
            notesArea.setForeground(new Color(5, 15, 50));
            notesArea.setBorder(null);

            // load notes from file if exists
            String path = notesPath();
            List<String> lines = Utils.readFile(path);
            if (lines != null && !lines.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line).append("\n");
                }
                notesArea.setText(sb.toString());
            } else {
                notesArea.setText("");
            }

            JScrollPane sp = new JScrollPane(notesArea);
            sp.setBorder(null);
            sp.setBounds(30, 30, 1210, 340);
            box.add(sp);

            JButton save = new JButton("Save Notes");
            save.setFont(new Font("Segoe UI", Font.BOLD, 18));
            save.setForeground(new Color(5, 15, 50));
            save.setBackground(new Color(234, 223, 207));
            save.setBorderPainted(false);
            save.setBounds(1080, 380, 150, 40);
            box.add(save);

            save.addActionListener(e -> {
                String[] arr = notesArea.getText().split("\\R");
                Utils.writeRawFile(path, Arrays.asList(arr));
                JOptionPane.showMessageDialog(this, "Notes saved.");
            });

            return root;
        }

        private String notesPath() {
            return String.format(
                    "data/STUDENTS/%s/%s/%s/%s/%s/%s_notes.txt",
                    student.getSchoolYear().replace(" ", ""),
                    student.getYearLevel().replace(" ", ""),
                    student.getCollege().toUpperCase(),
                    student.getProgram().toUpperCase(),
                    student.getSection(),
                    student.getId()
            );
        }

        // local RoundedPanel (soft corners)
        static class RoundedPanel extends JPanel {
            int arc;
            RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }

    // ---------- HELPER TABLE RENDER ----------
    private static void addTable(JPanel card, String[] columns, List<String[]> dataList, int x, int y, int w, int h) {
        String[][] data = dataList.toArray(new String[0][]);
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(x, y, w, h);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        card.add(scroll);
    }

    // Utility RoundedPanel reused
    static class RoundedPanel extends JPanel {
        int arc;
        RoundedPanel(int arc) { this.arc = arc; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public StudentGUI(Student student) {
        this.student = student;
    }
}
