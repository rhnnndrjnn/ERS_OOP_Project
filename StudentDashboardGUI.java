import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StudentDashboardGUI extends JFrame {

    private static final String BG_IMAGE = "src/studbg.png";

    private final Student student;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel underline;

    public StudentDashboardGUI(Student student) {
        this.student = student;
        initUI();
    }

    private void initUI() {
        setTitle("Student Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(null);
        setContentPane(bg);

        // Header - Title + Welcome + Logout

        JLabel welcome = new JLabel("Welcome, " + student.getFirstName() + " " + student.getLastName()
                + " (" + student.getId() + ")");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 23));
        welcome.setForeground(new Color(40, 40, 40));
        welcome.setBounds(205, 80, 600, 30);
        bg.add(welcome);

        JLabel logout = new JLabel("Log Out");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logout.setForeground(Color.BLACK);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setBounds(1650, 50, 120, 40);
        bg.add(logout);
        logout.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new MainUI().setVisible(true);
            }
            @Override public void mouseEntered(MouseEvent e) { logout.setText("<html><u>Log Out</u></html>"); }
            @Override public void mouseExited(MouseEvent e) { logout.setText("Log Out"); }
        });

        // White rounded card (main container)
        RoundedPanel card = new RoundedPanel(25);
        card.setBounds(40, 180, 1850, 760); // similar size to applicant
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        bg.add(card);

        // TAB panel inside card
        JPanel tabPanel = new JPanel(null);
        tabPanel.setOpaque(false);
        tabPanel.setBounds(30, 20, 1700, 60);
        card.add(tabPanel);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 22);
        JLabel tab1 = new JLabel("Profile");
        JLabel tab2 = new JLabel("Message");
        JLabel tab3 = new JLabel("Schedule");
        JLabel tab4 = new JLabel("Grades");
        JLabel tab5 = new JLabel("Academic Panel");

        JLabel[] tabs = {tab1, tab2, tab3, tab4, tab5};

        // Place tabs (approx positions similar to screenshot)
        tab1.setFont(tabFont); tab2.setFont(tabFont); tab3.setFont(tabFont); tab4.setFont(tabFont); tab5.setFont(tabFont);
        tab1.setBounds(20, 10, 200, 40);
        tab2.setBounds(240, 10, 200, 40);
        tab3.setBounds(460, 10, 200, 40);
        tab4.setBounds(680, 10, 200, 40);
        tab5.setBounds(900, 10, 240, 40);

        // initial colors
        tab1.setForeground(new Color(15, 36, 96));
        tab2.setForeground(Color.GRAY);
        tab3.setForeground(Color.GRAY);
        tab4.setForeground(Color.GRAY);
        tab5.setForeground(Color.GRAY);

        tab1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tabPanel.add(tab1); tabPanel.add(tab2); tabPanel.add(tab3); tabPanel.add(tab4); tabPanel.add(tab5);

        // underline (blue) like applicant
        underline = new JPanel();
        underline.setBackground(new Color(15, 36, 96));
        underline.setBounds(20, 52, 200, 4);
        tabPanel.add(underline);

        // Separator under tabs
        JSeparator sep = new JSeparator();
        sep.setBounds(20, 80, 1810, 2);
        card.add(sep);

        // Card panel for pages
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBounds(20, 90, 1810, 640);
        cardPanel.setOpaque(false);
        card.add(cardPanel);

        // Add pages (use StudentGUI_Panels for content)
        cardPanel.add(new StudentGUI.ProfilePanel(student), "PROFILE");
        cardPanel.add(new StudentGUI.MessagePanel(student), "MESSAGE");
        cardPanel.add(new StudentGUI.SchedulePanel(student), "SCHEDULE");
        cardPanel.add(new StudentGUI.GradesPanel(student), "GRADES");
        cardPanel.add(new StudentGUI.AcademicPanel(student), "ACADEMIC");

        // show profile initially
        cardLayout.show(cardPanel, "PROFILE");

        // Tab click listeners (change card and underline)
        tab1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectTab(tabs, tab1, 20, 200, "PROFILE"); }
        });
        tab2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectTab(tabs, tab2, 240, 200, "MESSAGE"); }
        });
        tab3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectTab(tabs, tab3, 460, 200, "SCHEDULE"); }
        });
        tab4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectTab(tabs, tab4, 680, 200, "GRADES"); }
        });
        tab5.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectTab(tabs, tab5, 900, 240, "ACADEMIC"); }
        });

        setVisible(true);
    }

    private void selectTab(JLabel[] tabs, JLabel clicked, int underlineX, int underlineW, String cardName) {
        for (JLabel t : tabs) t.setForeground(Color.GRAY);
        clicked.setForeground(new Color(15, 36, 96));
        underline.setBounds(underlineX, 52, underlineW, 4);
        underline.repaint();
        cardLayout.show(cardPanel, cardName);
    }

    // Background panel with image
    class BackgroundPanel extends JPanel {
        BufferedImage img;
        BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); } catch (Exception ignored) {}
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // RoundedPanel same as applicant
    class RoundedPanel extends JPanel {
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

    // Test runner
    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }
}
