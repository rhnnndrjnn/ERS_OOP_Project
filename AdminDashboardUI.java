import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminDashboardUI extends JFrame{

    private static final String BG_IMAGE = "src/dashboard_admin.png";
    private CardLayout cardLayout;
    private JPanel cardPanel;
    JPanel underline;
    private static final String FACULTY_DIR = "data/FACULTY";
    private static final String FACULTY_MASTER = FACULTY_DIR + File.separator + "ProfessorsMasterList.txt";
    private static String[] courses = {"BASALT","BFA", "BGT","BSA","BSBM","BSCE","BSCS","BSE","BSES",
                                        "BSEE","BSFT", "BSHM", "BSIE","BSIS","BSIT", "BSME","BTVTE"};
    private static String[] years = {"1stYear", "2ndYear", "3rdYear", "4thYear"};
    private static String[] school_year_global = {"2025-2026", "2026-2027", "2027-2028", "2028-2029", "2029-2030", "2030-2031", "2031-2032"};
    private static String[] department_global = {"CAFA", "CIE", "CIT", "CLA", "COE", "COS"};
    private static String[] section_global = {"A", "B"};
    Color lightBeige = new Color(234, 223, 207);
    Color dark= new Color(191, 175, 155);

    public AdminDashboardUI(){
        Admin.processResignation();
        initAdminDashboardUI();
    }
    public void initAdminDashboardUI(){
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== BACKGROUND =====
        BackgroundPanel bg = new BackgroundPanel(BG_IMAGE);
        bg.setLayout(null);
        setContentPane(bg);

        // Logout Button
        JLabel logout = new JLabel("LOG OUT");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logout.setForeground(Color.BLACK);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logout.setText("<html><u>LOG OUT</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logout.setText("LOG OUT");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginUI().setVisible(true);
            }
        });
        logout.setBounds(1200, 30, 120, 40);
        bg.add(logout);

        int x = 20, y = 120, w = 1320, h = 580;
        // ===== CENTER WHITE CARD =====
        RoundedPanel card = new RoundedPanel(25);
        card.setBounds(x, y, w, h);
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        bg.add(card);

        // ===== TABS PANEL =====
        JPanel tabPanel = new JPanel(null);
        tabPanel.setOpaque(false);
        tabPanel.setBounds(60, 20,1400, 70);
        card.add(tabPanel);

        Font tabFont = new Font("Segoe UI", Font.BOLD, 20);
        JLabel tab1 = new JLabel("Applicants Management");
        JLabel tab2 = new JLabel("Resignation Approvals");
        JLabel tab3 = new JLabel("Course Schedule Generator");
        JLabel tab4 = new JLabel("Promotion");
        JLabel[] tabs = {tab1, tab2, tab3, tab4};

        tab1.setFont(tabFont);
        tab2.setFont(tabFont);
        tab3.setFont(tabFont);
        tab4.setFont(tabFont);

        tab1.setForeground(new Color(15, 36, 96)); // selected
        tab2.setForeground(Color.GRAY);
        tab3.setForeground(Color.GRAY);
        tab4.setForeground(Color.GRAY);

        tab1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab4.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tab1.setBounds(0, 0, 330, 40);
        tab2.setBounds(300, 0, 330, 40);
        tab3.setBounds(600, 0, 330, 40);
        tab4.setBounds(980, 0, 330, 40);

        tabPanel.add(tab1);
        tabPanel.add(tab2);
        tabPanel.add(tab3);
        tabPanel.add(tab4);
       
        underline = new JPanel();
        underline.setBackground(new Color(15, 36, 96)); 
        underline.setBounds(0, 45, 280, 4);  // Under tab1 by default
        tabPanel.add(underline);

        // Separator line
        JSeparator sep = new JSeparator();
        sep.setBounds(30, 80, 1250, 2);
        card.add(sep);
        
        // CardPanel setup
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBounds(10, 60, 1300, 500);
        cardPanel.setOpaque(false);

        cardPanel.add(AppManagement(), "Applicants");
        cardPanel.add(ReApp(), "Approvals");
        cardPanel.add(SchedManagement(), "Schedule");
        cardPanel.add(Promotion(), "Promotion");

        // Show Applicants panel by default
        cardLayout.show(cardPanel, "Applicants");

        card.add(cardPanel);
        
        tab1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab1.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Applicants");
                Admin.setGuiCh("1");
                moveUnderline(0, 235);
            }
        });
        tab2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab2.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Approvals");
                Admin.setGuiCh("2");
                moveUnderline(300, 235);
            }
        });
        tab3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab3.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Schedule");
                Admin.setGuiCh("3");
                moveUnderline(600, 235);
            }
        });
        tab4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resetTabs(tabs);
                tab4.setForeground(new Color(15, 36, 96));
                cardLayout.show(cardPanel, "Promotion");
                Admin.setGuiCh("4");
                moveUnderline(900, 235);
            }
        });

        setVisible(true);
    }

    // ==================== APP MANAGEMENT PANEL ======================================
    private JPanel AppManagement() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
        Color bgcolor = new Color(154,153,250);

        p.add(createLabel("Program: ", 400, 50, infoFont));
        p.add(createLabel("Year Level: ", 20, 50, infoFont));

        // ===== Course Button =====
        JButton courseButton = new JButton("Choose program");
        courseButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        courseButton.setBounds(500, 48, 180, 40);
        courseButton.putClientProperty("JButton.buttonType", "roundRect");  
        courseButton.putClientProperty("JButton.style", "toolBar");
        courseButton.setBackground(lightBeige);
        p.add(courseButton);

        // ===== Year Level Button =====
        JButton yrButton = new JButton("Choose year level");
        yrButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        yrButton.setBounds(150, 48, 210, 40);
        yrButton.putClientProperty("JButton.buttonType", "roundRect");  
        yrButton.putClientProperty("JButton.style", "toolBar");
        yrButton.setBackground(lightBeige);
        p.add(yrButton);

        UIManager.put("PopupMenu.background", Color.WHITE);
        UIManager.put("PopupMenu.foreground", Color.BLACK);
        UIManager.put("PopupMenu.borderColor", new Color(0xad8330));
        UIManager.put("PopupMenu.selectionBackground", bgcolor);
        UIManager.put("PopupMenu.selectionForeground", Color.WHITE);

        JLabel courseLabel = new JLabel("No selected course ");
        courseLabel.setForeground(new Color(70, 70, 70));
        courseLabel.setBounds(20, 100, 800, 30);
        courseLabel.setFont(labelFont);
        p.add(courseLabel);

        // ===== Master List of Applicants =====
        List<File> applicantFiles = new ArrayList<>();
        Admin.findApplicantFiles(new File("data/APPLICANTS"), applicantFiles);

        List<Admin.ApplicantLine> guiList = new ArrayList<>();

        for (File f : applicantFiles) {
            List<String> lines = Utils.readFile(f.getPath());
            if (lines == null) continue;

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                String dec = Utils.decryptEachField(line);
                String[] parts = dec.split("\\|", -1);
                if (parts.length < 19) continue;
                if (!parts[18].equalsIgnoreCase("PENDING")) continue; // PENDING only

                Admin.ApplicantLine a = new Admin.ApplicantLine();
                a.source = f;
                a.raw = line;
                a.p = parts;

                guiList.add(a);
            }
        }

        // Set GUI list
        Admin.setGuiAppList(guiList);

        // ===== Table Holder =====
        JPanel tableHolder = new JPanel(new BorderLayout());
        tableHolder.setBounds(0, 140, 1300, 350);
        tableHolder.setOpaque(false);
        p.add(tableHolder);

        // ===== Initial Table =====
        JScrollPane scrollPane = ApplicantsCourseTable(guiList);
        tableHolder.add(scrollPane, BorderLayout.CENTER);

        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ===== State for filtering =====
        final String[] selectedCourse = {""};
        final String[] selectedYear = {""};

        // ===== Method to update combined filter =====
        Runnable updateFilter = () -> {
            List<RowFilter<Object,Object>> filters = new ArrayList<>();
            if (!selectedCourse[0].isEmpty()) filters.add(RowFilter.regexFilter(selectedCourse[0], 3));
            if (!selectedYear[0].isEmpty()) filters.add(RowFilter.regexFilter(selectedYear[0], 4));
            if (filters.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.andFilter(filters));
        };

        attachRowClickApproval(table, model, sorter, updateFilter);

        // ===== Show All Button =====
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        showAllBtn.setBounds(850, 48, 210, 40);
        showAllBtn.putClientProperty("JButton.buttonType", "roundRect");  
        showAllBtn.putClientProperty("JButton.style", "toolBar");
        showAllBtn.setBackground(bgcolor);
        p.add(showAllBtn);

        showAllBtn.addActionListener(e -> {
            selectedCourse[0] = "";
            selectedYear[0] = "";
            courseButton.setText("Choose course");
            yrButton.setText("Choose year level");
            courseLabel.setText("No selected course ");
            courseLabel.setFont(labelFont);
            sorter.setRowFilter(null);
        });

        // ===== Course List and Popup =====
        JList<String> courseList = new JList<>(courses);
        courseList.setBackground(Color.WHITE);
        courseList.setForeground(Color.BLACK);
        courseList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseList.setSelectionBackground(new Color(15,36,96));
        courseList.setSelectionForeground(Color.WHITE);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu coursePopup = new JPopupMenu();
        coursePopup.add(new JScrollPane(courseList));

        courseButton.addActionListener(e -> coursePopup.show(courseButton, 0, courseButton.getHeight()));

        courseList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selected = courseList.getSelectedValue();
                selectedCourse[0] = selected;
                courseButton.setText(selected);
                courseLabel.setFont(infoFont);
                courseLabel.setText("Applicants in " + selected + ":");
                updateFilter.run();
                coursePopup.setVisible(false);
            }
        });

        // ===== Year Level List and Popup =====
        JList<String> yearList = new JList<>(years);
        yearList.setBackground(Color.WHITE);
        yearList.setForeground(Color.BLACK);
        yearList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearList.setSelectionBackground(new Color(15,36,96));
        yearList.setSelectionForeground(Color.WHITE);
        yearList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu yearPopup = new JPopupMenu();
        yearPopup.add(new JScrollPane(yearList));

        yrButton.addActionListener(e -> yearPopup.show(yrButton, 0, yrButton.getHeight()));

        yearList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selected = yearList.getSelectedValue();
                selectedYear[0] = selected;
                yrButton.setText(selected);
                updateFilter.run();
                yearPopup.setVisible(false);
            }
        });

        return p;
    }   

    //========================= DISPLAY TABLE PER COURSE ========================================
    static String getchfromui(){ return chGui;}
    private static void setchFromui(String id){ chGui = id;}
    private static String chGui;
    private static String IdFromUi = "";
    static void setIdFromUi(String x){ IdFromUi = x;}
    static String getIdfromui(){ return IdFromUi; }

    public JScrollPane ApplicantsCourseTable(List<Admin.ApplicantLine> applicantLines) {
        Color outlineColor = new Color(0xd6ae5f); 
        Color tableBg = lightBeige; 

        List<Admin.ApplicantLine> pendingApplicants = applicantLines.stream()
                .filter(a -> a.p[18].equalsIgnoreCase("PENDING"))
                .toList();

        String[] cols = {"ID", "Full Name", "College", "Program", "Year Level", "Status"};
        String[][] rows = new String[pendingApplicants.size()][cols.length];

        for (int i = 0; i < pendingApplicants.size(); i++) {
            Admin.ApplicantLine a = pendingApplicants.get(i);
            rows[i][0] = a.p[0]; 
            rows[i][1] = a.p[1] + " " + a.p[2] + " " + a.p[3]; 
            rows[i][2] = a.p[15]; 
            rows[i][3] = a.p[16]; 
            rows[i][4] = a.p[14]; 
            rows[i][5] = a.p[18]; 
        }

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setBackground(tableBg);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);   
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if (status.equalsIgnoreCase("Passed")) c.setForeground(new Color(0,128,0));
                else if (status.equalsIgnoreCase("Failed")) c.setForeground(Color.RED);
                else c.setForeground(Color.BLACK); // PENDING
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(dark, 3, true));
        scroll.getViewport().setBackground(tableBg);
        
        return scroll;
    }

                
    private void attachRowClickApproval(JTable table, DefaultTableModel model, TableRowSorter<DefaultTableModel> sorter, Runnable refreshFilter) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String id = model.getValueAt(modelRow, 0).toString();
                    String name = model.getValueAt(modelRow, 1).toString();
                    setIdFromUi(id);
                    Admin.settextGuiExist(true);
                    int choice = JOptionPane.showOptionDialog(
                        null,
                        "Do you want to Approve or Decline " + name + "?",
                        "Applicant Decision",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Approve", "Reject"},
                        "Approve"
                    );

                    if (choice == 0) {
                        setchFromui("A");
                        model.setValueAt("Passed", modelRow, 5);
                    } else if (choice == 1) {
                        setchFromui("R");
                        model.setValueAt("Failed", modelRow, 5);
                    }
                    
                    Admin.applicantsMenu();
                    // Refresh filter so updated status may remain visible
                    if (refreshFilter != null) refreshFilter.run();
                }
            }
        });
    }

    // ==================== RESIGNATION APPROVALS PANEL ==============================
    private JPanel ReApp(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
        Font cellFont = new Font("Segoe UI", Font.PLAIN, 18);

        RoundedPanel tableContainer = new RoundedPanel(25);
        tableContainer.setBounds(25, 48, 1200, 450);
        tableContainer.setBackground(dark);
        tableContainer.setLayout(null);
        p.add(tableContainer);

        String[] columnNames = {
            "Faculty ID", "Full Name", "Reason", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable table = new JTable(model);
        table.setFont(cellFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(headerFont);
        table.setBackground(lightBeige); 
        table.setForeground(Color.BLACK);
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(dark, 1));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);   

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(12, 12, 1180, 430);
        scroll.setBorder(BorderFactory.createLineBorder(dark, 1));
        tableContainer.add(scroll);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;

                String id = model.getValueAt(row, 0).toString();
                String name = model.getValueAt(row, 1).toString();
                String reason = model.getValueAt(row, 2).toString();

                // Show dialog
                int choice = JOptionPane.showOptionDialog(
                    null,
                    "Faculty: " + name + "\nReason: " + reason + "\n\nApprove this resignation?",
                    "Resignation Decision",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"Approve", "Reject", "Cancel"},
                    "Approve"
                );

                if (choice == 0) { 
                    updateResignationStatus(id, "Accepted");
                    model.setValueAt("Accepted", row, 3);
                } 
                else if (choice == 1) { 
                    updateResignationStatus(id, "Rejected");
                    model.setValueAt("Rejected", row, 3);
                }
            }
        });
        
        loadResignationTable(model);

        return p;
    }

    private void loadResignationTable(DefaultTableModel model) {
        List<String> list = Admin.getResList();
        model.setRowCount(0);

        for (String enc : list) {
            if (enc.trim().isEmpty()) continue;

            String dec = Utils.decryptEachField(enc);
            String[] p = dec.split("\\|", -1);

            String id = p[0];
            String name = p[1];
            String reason = p[2];
            String status = p[3];

            model.addRow(new Object[]{ id, name, reason, status });
        }
    }

    private void updateResignationStatus(String id, String newStatus) {
        try {
            List<String> lines = Admin.getResList(); // your stored GUI list
            List<String> updated = new ArrayList<>();

            for (String enc : lines) {
                String dec = Utils.decryptEachField(enc);
                String[] p = dec.split("\\|", -1);

                if (p[0].equals(id)) {
                    p[3] = newStatus; // update status
                    updated.add(Utils.encryptEachField(String.join("|", p)));
                } else {
                    updated.add(enc);
                }
            }

            // Overwrite file
            File folder = new File(Admin.RESIGN_FOLDER);
            File f = new File(folder, id + "_resignation.txt");
            Utils.writeRawFile(f.getPath(), updated);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // ======================= SCHEDULE MANAGEMENT ==============================
    private JPanel SchedManagement(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);
        Color colorClicked = new Color(69, 56, 42);
        Color defaultColor = Color.WHITE;

        RoundedPanel buttonsPanel = new RoundedPanel(25);
        buttonsPanel.setBounds(25, 48, 250, 100);
        buttonsPanel.setBackground(lightBeige);
        p.add(buttonsPanel);

        RoundedPanel genPanel = new RoundedPanel(25);
        genPanel.setBounds(300, 48, 950, 450);
        genPanel.setBackground(lightBeige);
        genPanel.setVisible(true);
        genPanel.setLayout(null);
        p.add(genPanel);

        RoundedPanel checkReqPanel = new RoundedPanel(25);
        checkReqPanel.setBounds(300, 48, 950, 450);
        checkReqPanel.setBackground(lightBeige);
        checkReqPanel.setVisible(false);
        checkReqPanel.setLayout(null);
        p.add(checkReqPanel);

        JButton generateButton = new JButton("Generate schedule");
        generateButton.setFont(buttonFont);
        generateButton.setBounds(10, 80, 300, 110);
        generateButton.putClientProperty("JButton.buttonType", "roundRect");  
        generateButton.putClientProperty("JButton.style", "toolBar");
        buttonsPanel.add(generateButton);

        JButton checkButton = new JButton("Check Request");
        checkButton.setFont(buttonFont);
        checkButton.setBounds(10, 120, 300, 110);
        checkButton.putClientProperty("JButton.buttonType", "roundRect");  
        checkButton.putClientProperty("JButton.style", "toolBar");
        buttonsPanel.add(checkButton);

        generateButton.setBackground(defaultColor);
        checkButton.setBackground(defaultColor);

        generateButton.addActionListener(e -> {
            genPanel.setVisible(true);
            generateButton.setBackground(colorClicked);
            checkButton.setBackground(defaultColor);
            checkReqPanel.setVisible(false);
            JPanel p1 = genSchedule();
            genPanel.removeAll();      
            genPanel.add(p1);          
            genPanel.repaint();
            genPanel.revalidate();
            p.repaint();
            p.revalidate();
        });

        checkButton.addActionListener(e -> {
            genPanel.setVisible(false);
            checkButton.setBackground(colorClicked);
            generateButton.setBackground(defaultColor);
            checkReqPanel.setVisible(true);
            JPanel p2 = checkReq();
            checkReqPanel.add(p2);
            checkReqPanel.repaint();
            checkReqPanel.revalidate();
            p.repaint();
            p.revalidate();
        });


        return p;
    }
    private static String year_Level = "";
    private static String program = "";
    private static String acadYear = "";
    private static String department = "";
    private static String section = "";

    private JPanel genSchedule(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 0, 950, 450);
        p.setLayout(null);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 20);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);

        p.add(createLabel("Year Level: ", 40, 40, infoFont));
        p.add(createLabel("Program: ", 390, 40, infoFont));
        p.add(createLabel("Department: ", 40, 120, infoFont));
        p.add(createLabel("Academic Year: ", 390 , 120, infoFont));
        p.add(createLabel("Section: ", 40 , 200, infoFont));

        JButton courseButton = new JButton("Choose year level");
        courseButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        courseButton.setBounds(160, 38, 180, 40);
        courseButton.putClientProperty("JButton.buttonType", "roundRect");  
        courseButton.putClientProperty("JButton.style", "toolBar");
        courseButton.setBackground(Color.WHITE);
        p.add(courseButton);

        JList<String> yearLevelList = new JList<>(years);
        yearLevelList.setBackground(Color.WHITE);
        yearLevelList.setForeground(Color.BLACK);
        yearLevelList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearLevelList.setSelectionBackground(new Color(15,36,96));
        yearLevelList.setSelectionForeground(Color.WHITE);
        yearLevelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu yearLevelPopup = new JPopupMenu();
        yearLevelPopup.add(new JScrollPane(yearLevelList));

        courseButton.addActionListener(e -> {
            yearLevelPopup.show(courseButton, 0, courseButton.getHeight());
        });

        yearLevelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = yearLevelList.getSelectedValue();
                if (selected != null) {
                    switch(selected){
                        case "1stYear": year_Level = "1"; break;
                        case "2ndYear": year_Level = "2"; break;
                        case "3rdYear": year_Level = "3"; break;
                        case "4thYear": year_Level = "4"; break;
                    }          
                    courseButton.setText(selected);  
                    yearLevelPopup.setVisible(false);
                }
            }
        });

        // ===================== PROGRAM =====================
        JButton progBtn = new JButton("Choose course");
        progBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        progBtn.setBounds(550, 38, 180, 40);
        progBtn.putClientProperty("JButton.buttonType", "roundRect");  
        progBtn.putClientProperty("JButton.style", "toolBar");
        progBtn.setBackground(Color.WHITE);
        p.add(progBtn);

        JList<String> programList = new JList<>(courses);
        programList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu programPopup = new JPopupMenu();
        programPopup.add(new JScrollPane(programList));

        progBtn.addActionListener(e -> programPopup.show(progBtn, 0, progBtn.getHeight()));
        programList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                program = programList.getSelectedValue();
                progBtn.setText(program);
                programPopup.setVisible(false);
            }
        });
        // ===================== ACADEMIC YEAR =====================
        JButton ayBtn = new JButton("Choose AY");
        ayBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        ayBtn.setBounds(550, 118, 180, 40);
        ayBtn.putClientProperty("JButton.buttonType", "roundRect");  
        ayBtn.putClientProperty("JButton.style", "toolBar");
        ayBtn.setBackground(Color.WHITE);
        p.add(ayBtn);

        JList<String> ayList = new JList<>(school_year_global);
        ayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu ayPopup = new JPopupMenu();
        ayPopup.add(new JScrollPane(ayList));

        ayBtn.addActionListener(e -> ayPopup.show(ayBtn, 0, ayBtn.getHeight()));
        ayList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                acadYear = ayList.getSelectedValue();
                ayBtn.setText(acadYear);
                ayPopup.setVisible(false);
            }
        });

        // ===================== DEPARTMENT =====================
        JButton deptBtn = new JButton("Choose department");
        deptBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        deptBtn.setBounds(160, 118, 180, 40);
        deptBtn.putClientProperty("JButton.buttonType", "roundRect");  
        deptBtn.putClientProperty("JButton.style", "toolBar");
        deptBtn.setBackground(Color.WHITE);
        p.add(deptBtn);

        JList<String> deptList = new JList<>(department_global);
        deptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu deptPopup = new JPopupMenu();
        deptPopup.add(new JScrollPane(deptList));

        deptBtn.addActionListener(e -> deptPopup.show(deptBtn, 0, deptBtn.getHeight()));
        deptList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                department = deptList.getSelectedValue();
                deptBtn.setText(department);
                deptPopup.setVisible(false);
            }
        });

        // ===================== SECTION =====================
        JButton secBtn = new JButton("Choose section");
        secBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        secBtn.setBounds(160, 198, 180, 40);
        secBtn.putClientProperty("JButton.buttonType", "roundRect");  
        secBtn.putClientProperty("JButton.style", "toolBar");
        secBtn.setBackground(Color.WHITE);
        p.add(secBtn);;

        JList<String> secList = new JList<>(section_global);
        secList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPopupMenu secPopup = new JPopupMenu();
        secPopup.add(new JScrollPane(secList));

        secBtn.addActionListener(e -> secPopup.show(secBtn, 0, secBtn.getHeight()));
        secList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                section = secList.getSelectedValue();
                secBtn.setText(section);
                secPopup.setVisible(false);
            }
        });
        // ============== GENERATE BUTTON ================
        JButton generateBtn = new JButton("Generate Schedule");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        generateBtn.setBounds(300, 330, 380, 50);
        generateBtn.setBackground(new Color(69, 56, 42));
        generateBtn.setForeground(Color.WHITE);
        p.add(generateBtn);

        generateBtn.addActionListener(e -> {
            if (year_Level.isEmpty() || program.isEmpty() || department.isEmpty() || acadYear.isEmpty() || section.isEmpty()) {
                JOptionPane.showMessageDialog(p, "Please fill all fields before generating schedule.", "Incomplete Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                String msg = String.format("Generating schedule for:\nYear Level: %s\nProgram: %s\nDepartment: %s\nAcademic Year: %s\nSection: %s",
                        year_Level, program, department, acadYear, section);
                Admin admin = new Admin();
                admin.setFields(acadYear, year_Level, department, program, section);
                Admin.generateSchedules();

                JOptionPane.showMessageDialog(p, msg, "Schedule Generated", JOptionPane.INFORMATION_MESSAGE);
                
            }
        });

        return p;
    }

    private static int numToAdmin;
    private static int ch;
    private static void setNumToAdmin(int x){ numToAdmin = x; }
    private static void setCh(int x){ ch = x; }
    static int getNumToGui(){ return numToAdmin; }
    static int getCh(){ return ch; }
    
    private JPanel checkReq(){
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 0, 950, 450);
        p.setLayout(null);
        Font infoFont = new Font("Segoe UI", Font.BOLD, 20);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 22);
        
        String path = "data/ADMIN/" + "schedule_requests.txt";
        List<String> list = Utils.readFile(path);
        List<String> profnamelist = Utils.readFile(FACULTY_MASTER);
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Faculty ID", "Name", "Status"}, 0);

        for (String s : list) {
            String[] parts = s.split("\\|");
            String profId = parts[0].trim();
            String reason = parts[1].trim();
            String status = "";
            String name = "";
            if(reason.equals("APPROVED")){
                status = "APPROVED";
            }
            if(reason.equals("REJECTED")){
                status = "REJECTED";
            }
            else{
                status = "PENDING";
            }

            for(String prof : profnamelist){
                String[] pr = prof.split("\\|");
                String id = pr[0].trim();
                if(id.equals(profId)){
                    name = pr[2].trim();
                }
            }
            model.addRow(new Object[]{profId, name, status});
        }

        JTable table = new JTable(model);
        table.setFont(labelFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(infoFont);
        table.setBackground(lightBeige); 
        table.setForeground(Color.BLACK);
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(dark, 1));
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color. WHITE);
        table.setShowGrid(true);           
        table.setGridColor(Color.BLACK);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 20, 900, 400);  
        p.add(sp);   

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) return;

                String rawLine = list.get(row);
                String[] parts = rawLine.split("\\|");
                String reason = parts[1].trim();
                String yearLevel = parts[2].trim();
                String profId = parts[0].trim();
                String course = parts[3].trim();
                String sec = parts[4].trim();
                String sub = parts[5].trim();
                String day = parts[6].trim();
                String time = parts[7].trim();

                String[] options = {"Approve", "Reject"};

                int choice = JOptionPane.showOptionDialog(
                        null,
                        "REQUESTED SCHEDULE: " + yearLevel + "-" + course + "-" + sec + "-" + sub + "-" + day + "-" + time + "\nReason: " + reason,
                        "Requested by " + profId,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (choice == 0) {
                    System.out.println("Approved: " + rawLine);
                    model.setValueAt("APPROVED", row, 2);
                    setCh(1);
                    Admin.setChoiceFromGui(1);
                } else if (choice == 1) {
                    System.out.println("Rejected: " + rawLine);
                    model.setValueAt("REJECTED", row, 2);
                    setCh(0);
                    Admin.setChoiceFromGui(1);
                }
                setNumToAdmin(row);
                Admin.viewReq();
            }
        });


        return p;
    }
    //====================================  PROMOTION  ==========================================================================
    
    private JPanel Promotion() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        Font infoFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
        Font cellFont = new Font("Segoe UI", Font.PLAIN, 18);

        // ================= TABLE CONTAINER =================
        RoundedPanel tableContainer = new RoundedPanel(25);
        tableContainer.setBounds(25, 100, 1200, 350);
        tableContainer.setBackground(dark);
        tableContainer.setLayout(null);
        p.add(tableContainer);

        String[] columnNames = {"Student ID", "Year Level", "College", "Program", "Section", "Last Name", "GWA", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(cellFont);
        table.setRowHeight(30);

        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setBackground(dark);
        table.getTableHeader().setForeground(Color.WHITE);

        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(12, 12, 1180, 330);
        tableContainer.add(scroll);

        // ============= SELECT ALL CHECKBOX =================
        JCheckBox selectAll = new JCheckBox("Select All");
        selectAll.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        selectAll.setBounds(1020, 60, 150, 30);
        p.add(selectAll);

        selectAll.addActionListener(e -> {
            if (selectAll.isSelected()) table.selectAll();
            else table.clearSelection();
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ============= DROPDOWNS =================
        // Year Level
        JButton yrButton = new JButton("Choose Year Level");
        yrButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        yrButton.setBounds(40, 48, 230, 40);
        yrButton.putClientProperty("JButton.buttonType", "roundRect");
        yrButton.putClientProperty("JButton.style", "toolBar");
        yrButton.setBackground(lightBeige);
        p.add(yrButton);

        JList<String> yearList = new JList<>(years); 
        yearList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearList.setSelectionBackground(new Color(15,36,96));
        yearList.setSelectionForeground(Color.WHITE);
        JPopupMenu yearPopup = new JPopupMenu();
        yearPopup.add(new JScrollPane(yearList));

        yrButton.addActionListener(e -> yearPopup.show(yrButton, 0, yrButton.getHeight()));

        // Academic Year
        JButton acadYearBtn = new JButton("Choose Academic Year");
        acadYearBtn.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        acadYearBtn.setBounds(780, 48, 230, 40);
        acadYearBtn.putClientProperty("JButton.buttonType", "roundRect");
        acadYearBtn.putClientProperty("JButton.style", "toolBar");
        acadYearBtn.setBackground(lightBeige);
        p.add(acadYearBtn);

        JList<String> acadList = new JList<>(school_year_global);
        acadList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        acadList.setSelectionBackground(new Color(15,36,96));
        acadList.setSelectionForeground(Color.WHITE);
        JPopupMenu acadPopup = new JPopupMenu();
        acadPopup.add(new JScrollPane(acadList));

        acadYearBtn.addActionListener(e -> acadPopup.show(acadYearBtn, 0, acadYearBtn.getHeight()));

        // Section
        JButton sectionButton = new JButton("Choose Section");
        sectionButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        sectionButton.setBounds(530, 48, 230, 40);
        sectionButton.putClientProperty("JButton.buttonType", "roundRect");
        sectionButton.putClientProperty("JButton.style", "toolBar");
        sectionButton.setBackground(lightBeige);
        p.add(sectionButton);

        JList<String> sectionList = new JList<>(section_global);
        sectionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionList.setSelectionBackground(new Color(15,36,96));
        sectionList.setSelectionForeground(Color.WHITE);
        JPopupMenu sectionPopup = new JPopupMenu();
        sectionPopup.add(new JScrollPane(sectionList));

        sectionButton.addActionListener(e -> sectionPopup.show(sectionButton, 0, sectionButton.getHeight()));

        // Program
        JButton progButton = new JButton("Choose Program");
        progButton.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        progButton.setBounds(290, 48, 210, 40);
        progButton.putClientProperty("JButton.buttonType", "roundRect");
        progButton.putClientProperty("JButton.style", "toolBar");
        progButton.setBackground(lightBeige);
        p.add(progButton);

        JList<String> progList = new JList<>(courses);
        progList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        progList.setSelectionBackground(new Color(15,36,96));
        progList.setSelectionForeground(Color.WHITE);
        JPopupMenu progPopup = new JPopupMenu();
        progPopup.add(new JScrollPane(progList));

        progButton.addActionListener(e -> progPopup.show(progButton, 0, progButton.getHeight()));

    
        yearList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selectedYear = yearList.getSelectedValue();
                yrButton.setText(selectedYear);
                Admin.setSchoolYear(selectedYear);
                yearPopup.setVisible(false);

                if (Admin.getAcademicYear() != null)
                    loadStudentsIntoTable(model);
            }
        });

        sectionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                sectionButton.setText(sectionList.getSelectedValue());
                sectionPopup.setVisible(false);
                Admin.setPromotiontype(sectionList.getSelectedValue());

                if (Admin.getAcademicYear() != null)
                    loadStudentsIntoTable(model);
            }
        });

        progList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                progButton.setText(progList.getSelectedValue());
                progPopup.setVisible(false);
                Admin.setPromotechoicefromgui(progList.getSelectedValue());

                if (Admin.getAcademicYear() != null)
                    loadStudentsIntoTable(model);
            }
        });

        acadList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selectedAY = acadList.getSelectedValue();
                acadYearBtn.setText(selectedAY);
                acadPopup.setVisible(false);
                Admin.setAcademicYear(selectedAY);

                // now Academic Year is set, so load table
                loadStudentsIntoTable(model);
            }
        });

        // ============= PROMOTE / REJECT BUTTONS =================
        JButton promoteBtn = new JButton("PROMOTE");
        promoteBtn.setBounds(600, 460, 150, 40);
        promoteBtn.setBackground(new Color(80, 180, 80));
        promoteBtn.setForeground(Color.WHITE);
        promoteBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(promoteBtn);

        JButton rejectBtn = new JButton("REJECT");
        rejectBtn.setBounds(800, 460, 150, 40);
        rejectBtn.setBackground(new Color(200, 60, 60));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(rejectBtn);

        p.setPreferredSize(new Dimension(1250, 600));

        // ============= BUTTON LOGIC =================
        promoteBtn.addActionListener(e -> {
    int[] rows = table.getSelectedRows();
    if (rows.length == 0) {
        JOptionPane.showMessageDialog(null, "Select student(s) to promote.");
        return;
    }

    String[] options = {"Regular Promotion", "Irregular Promotion"};
    int choice = JOptionPane.showOptionDialog(null,
            "Choose promotion type:",
            "Promotion Type",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

    if (choice == -1) return; // canceled

    new Thread(() -> {
        String[] parts = Admin.getAcademicYear().split("-");
        String nextSY = (Integer.parseInt(parts[0]) + 1) + "-" + (Integer.parseInt(parts[1]) + 1);

        for (int r : rows) {
            String studentID = table.getValueAt(r, 0).toString();
            String year      = table.getValueAt(r, 1).toString();
            String college   = table.getValueAt(r, 2).toString();
            String program   = table.getValueAt(r, 3).toString();
            String section   = table.getValueAt(r, 4).toString();

            File studentFolder = new File("data/STUDENTS/" + Admin.getAcademicYear() + "/" +
                                        year + "/" + college + "/" + program + "/" + section + "/" + studentID);

            

            if (!studentFolder.exists()) continue;

            if (choice == 0) { // Regular Promotion
                Admin.regularPromote(studentFolder, Admin.getAcademicYear(), nextSY);
            } else if (choice == 1) { // Irregular Promotion
                String targetSY = JOptionPane.showInputDialog("Enter target School Year:");
                String targetYear = JOptionPane.showInputDialog("Enter target Year Level:");
                if (targetSY != null && targetYear != null)
                    Admin.irregularPromote(studentFolder, Admin.getAcademicYear(), targetSY.trim(), targetYear.trim().replace(" ", ""));
            }
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Selected students promoted!");
            loadStudentsIntoTable(model);
        });
    }).start();
});


        rejectBtn.addActionListener(e -> {
    int[] rows = table.getSelectedRows();
    if (rows.length == 0) {
        JOptionPane.showMessageDialog(null, "Select student(s) to reject.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to reject the selected student(s)?",
            "Confirm Reject",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    new Thread(() -> {
        for (int r : rows) {
            String studentID = table.getValueAt(r, 0).toString();
            String year      = table.getValueAt(r, 1).toString();
            String college   = table.getValueAt(r, 2).toString();
            String program   = table.getValueAt(r, 3).toString();
            String section   = table.getValueAt(r, 4).toString();

            File studentFolder = new File("data/STUDENTS/" + Admin.getAcademicYear() + "/" +
                                        year + "/" + college + "/" + program + "/" + section + "/" + studentID);
            if (!studentFolder.exists()) continue;

            File dropFolder = new File("data/STUDENTS/ARCHIVES/DROPPED_STUDENTS/" + studentID);
            dropFolder.mkdirs();

            Admin.moveFolder(studentFolder, dropFolder); // recursive move
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Selected student(s) rejected!");
            loadStudentsIntoTable(model);
        });
    }).start();
});




        return p;
    }

    // ================= HELPER METHOD TO LOAD STUDENTS =================
    private void loadStudentsIntoTable(DefaultTableModel model) {
    model.setRowCount(0);

    if (Admin.getAcademicYear() == null) return;

    File baseDir = new File("data/STUDENTS/" + Admin.getAcademicYear());
    List<File> studentFolders = new ArrayList<>();
    Admin.findStudentFolders(baseDir, studentFolders); // collect all student folders

    for (File studentFolder : studentFolders) {
        File studentInfo = new File(studentFolder, studentFolder.getName() + "_info.txt");
        System.out.println("Checking student folder: " + studentFolder.getAbsolutePath());
        System.out.println("Info file exists? " + studentInfo.exists());
        if (studentInfo.exists()) {
            List<String> enc = Utils.readFile(studentInfo.getPath());
            if (enc.isEmpty()) continue;
            String[] p = Utils.decryptEachField(enc.get(0)).split("\\|", -1);

            String lastName = p[1];
            String college = p[15]; // adjust index according to your fields
            String gwa = p[13];     // adjust index according to your fields
            String remarks = p[14]; // adjust index according to your fields
            String year = p[14];
            String program = p[16];
            String section = p[20];

            // optional: only show if matches dropdown selections
            if (program.equals(Admin.getPromotechoicefromgui()) && section.equals(Admin.getPromotiontype())) {
                model.addRow(new Object[]{
                    studentFolder.getName(), // Student ID
                    year,                    // Year Level
                    college,                 // College
                    program,                 // Program
                    section,                 // Section
                    lastName,                // Last Name
                    gwa,                     // GWA
                    remarks                  // Remarks
                });
            }
        }
    }
}

    // ================= HELPER METHOD TO LOAD STUDENTS =================


    
    Font btnFont = new Font("Segoe UI", Font.ITALIC, 20);

    private JButton styledButton(String text, int x, int y, int w, int h, Color bg) {
        JButton b = new JButton(text);
        b.setFont(btnFont);
        b.setBounds(x, y, w, h);
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.putClientProperty("JButton.style", "toolBar");
        b.setBackground(bg);
        return b;
    }

    // ==========================================================
    private void resetTabs(JLabel[] tabs) {
        for (JLabel t : tabs) t.setForeground(Color.GRAY);
    }
    
    private JLabel createLabel(String t, int x, int y, Font f) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setBounds(x, y, 800, 30);
        l.setForeground(new Color(70, 70, 70));
        return l;
    }

    private void moveUnderline(int x, int width) {
        underline.setBounds(x, 45, width, 4);
        underline.repaint();
    }
}

class BackgroundPanel extends JPanel {
        private BufferedImage img;

        BackgroundPanel(String path) {
            try {
                File f = new File(path);
                img = f.exists() ? ImageIO.read(f) : null;
            } catch (Exception ignored) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (img != null) {
                int pw = getWidth();
                int ph = getHeight();

                double imgRatio = (double) img.getWidth() / img.getHeight();
                double panelRatio = (double) pw / ph;

                int w, h;
                if (panelRatio > imgRatio) {
                    w = pw;
                    h = (int)(pw / imgRatio);
                } else {
                    h = ph;
                    w = (int)(ph * imgRatio);
                }

                int x = (pw - w) / 2;
                int y = (ph - h) / 2;

                g.drawImage(img, x, y, w, h, this);
            }

            g.setColor(new Color(0,0,0,60));
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }
    
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

