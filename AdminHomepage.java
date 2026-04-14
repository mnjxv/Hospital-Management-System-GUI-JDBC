package edp.view;

import edp.model.AppointmentModel;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.List;

public class AdminAppointmentView extends JFrame {

    public JTable table;
    public DefaultTableModel model;

    // ── Patient ID and Doctor ID are ComboBoxes ──
    public JComboBox<String> cmbPatientId, cmbDoctorId;
    public JTextField txtPurpose;
    public JComboBox<String> cmbTime;
    public JComboBox<String> cmbStatus;

    // The "fake combobox" date picker
    private JComboBox<String> cmbDateFake;
    private JButton           btnDateArrow;

    public JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefreshLists;

    // Static list to track all open Appointment views for auto-refresh
    private static final java.util.List<AdminAppointmentView> openViews = new java.util.ArrayList<>();

    public static void refreshAllPatientLists() {
        for (AdminAppointmentView view : openViews) {
            view.loadPatientIds();
        }
    }

    public static void refreshAllDoctorLists() {
        for (AdminAppointmentView view : openViews) {
            view.loadDoctorIds();
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            if (!openViews.contains(this)) openViews.add(this);
            loadPatientIds();
            loadDoctorIds();
        } else {
            openViews.remove(this);
        }
    }

    public void loadPatientIds() {
        cmbPatientId.removeAllItems();
        cmbPatientId.addItem("");
        try {
            List<String> ids = AppointmentModel.getAllPatientIds();
            for (String id : ids) cmbPatientId.addItem(id.replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Could not load patient list:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDoctorIds() {
        cmbDoctorId.removeAllItems();
        cmbDoctorId.addItem("");
        try {
            List<String> ids = AppointmentModel.getAllDoctorIds();
            for (String id : ids) cmbDoctorId.addItem(id.replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Could not load doctor list:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int pickedYear, pickedMonth, pickedDay;
    private boolean datePicked = false;

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public AdminAppointmentView() {
        setTitle("Appointment Management");
        setSize(1200, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        buildTable();
        buildForm();
        Calendar cal = Calendar.getInstance();
        pickedYear  = cal.get(Calendar.YEAR);
        pickedMonth = cal.get(Calendar.MONTH) + 1;
        pickedDay   = cal.get(Calendar.DAY_OF_MONTH);
        updateDateCombo();
    }

    private void buildTable() {
        model = new DefaultTableModel(
                new String[]{
                    "Appointment ID","Patient ID","Doctor ID",
                    "Date & Time","Purpose","Status"
                }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(15, 70, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ROW 1 — Patient ID | Doctor ID | Purpose
        gbc.gridy = 0;
        
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Patient ID"), gbc);
        cmbPatientId = new JComboBox<>();
        cmbPatientId.setBackground(Color.WHITE);
        cmbPatientId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPatientId.setPreferredSize(new Dimension(160, 32));
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbPatientId, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Doctor ID"), gbc);
        cmbDoctorId = new JComboBox<>();
        cmbDoctorId.setBackground(Color.WHITE);
        cmbDoctorId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDoctorId.setPreferredSize(new Dimension(160, 32));
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(cmbDoctorId, gbc);

        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Purpose"), gbc);
        txtPurpose = new JTextField(16);
        gbc.gridx = 5; gbc.weightx = 1;
        txtPurpose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPurpose.setPreferredSize(new Dimension(160, 32));
        txtPurpose.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        form.add(txtPurpose, gbc);

        // ROW 2 — Date & Time | Status
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Date & Time"), gbc);

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dateTimePanel.setOpaque(false);

        cmbDateFake = new JComboBox<>(new String[]{ "" });
        cmbDateFake.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDateFake.setBackground(Color.WHITE);
        cmbDateFake.setPreferredSize(new Dimension(160, 32));
        cmbDateFake.setBorder(null);
        cmbDateFake.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                btnDateArrow = new JButton();
                btnDateArrow.setBackground(new Color(230, 235, 245));
                btnDateArrow.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(180, 190, 210)));
                btnDateArrow.setFocusPainted(false);
                btnDateArrow.setIcon(new Icon() {
                    @Override public int getIconWidth()  { return 8; }
                    @Override public int getIconHeight() { return 5; }
                    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(80, 80, 80));
                        int[] xs = { x, x + 8, x + 4 };
                        int[] ys = { y, y,     y + 5 };
                        g2.fillPolygon(xs, ys, 3);
                        g2.dispose();
                    }
                });
                btnDateArrow.addActionListener(e -> showCalendarPopup(cmbDateFake));
                return btnDateArrow;
            }
        });
        cmbDateFake.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setText(datePicked ? MONTH_NAMES[pickedMonth - 1] + " " + pickedDay + ", " + pickedYear : "Select Date");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                return lbl;
            }
        });
        cmbDateFake.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { showCalendarPopup(cmbDateFake); }
        });
        cmbDateFake.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(cmbDateFake::hidePopup);
            }
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
        dateTimePanel.add(cmbDateFake);

        // ── TIME ──
        cmbTime = new JComboBox<>();
        for (int h = 1; h <= 12; h++)
            for (int m = 0; m < 60; m += 5) {
                cmbTime.addItem(String.format("%d:%02dam", h, m));
                cmbTime.addItem(String.format("%d:%02dpm", h, m));
            }
        cmbTime.setSelectedItem("7:00am");
        cmbTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTime.setBackground(Color.WHITE);
        cmbTime.setPreferredSize(new Dimension(160, 32));
        cmbTime.setBorder(null);
        dateTimePanel.add(cmbTime);

        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        form.add(dateTimePanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 4; gbc.weightx = 0;
        form.add(label("Status"), gbc);
        cmbStatus = createStatusCombo();
        gbc.gridx = 5; gbc.weightx = 1;
        form.add(cmbStatus, gbc);

        // BUTTONS
        btnClear  = createButton("Clear",new Color(120, 120, 140));
        btnRefreshLists = createButton("Refresh List",new Color(80, 100, 140));
        btnAdd    = createButton("Add",new Color(15, 90, 180));
        btnUpdate = createButton("Update",new Color(30, 140, 80));
        btnDelete = createButton("Delete",new Color(190, 40, 40));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshLists);
        btnPanel.add(btnClear); btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate); btnPanel.add(btnDelete);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    // ── Calendar popup ────────────────────────────────────────────────────────

    private void showCalendarPopup(Component anchor) {
        JDialog popup = new JDialog(this, false);
        popup.setUndecorated(true);
        popup.setSize(240, 220);
        Point p = anchor.getLocationOnScreen();
        popup.setLocation(p.x, p.y + anchor.getHeight() + 2);

        final int[] displayYear  = { pickedYear };
        final int[] displayMonth = { pickedMonth - 1 };

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(new Color(150, 170, 210), 1));
        popup.setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 70, 150));
        header.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JButton prev = navBtn("\u2039");
        JButton next = navBtn("\u203A");
        JLabel monthYearLbl = new JLabel("", SwingConstants.CENTER);
        monthYearLbl.setForeground(Color.WHITE);
        monthYearLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.add(prev, BorderLayout.WEST);
        header.add(monthYearLbl, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));
        root.add(grid, BorderLayout.CENTER);

        Runnable rebuild = () -> {
            monthYearLbl.setText(MONTH_NAMES[displayMonth[0]] + " " + displayYear[0]);
            grid.removeAll();

            for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setForeground(new Color(100, 120, 160));
                grid.add(l);
            }

            Calendar cal = Calendar.getInstance();
            cal.set(displayYear[0], displayMonth[0], 1);
            int firstDow    = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < firstDow; i++) grid.add(new JLabel(""));

            for (int d = 1; d <= daysInMonth; d++) {
                final int day = d;
                boolean sel = displayYear[0]  == pickedYear
                           && displayMonth[0] == pickedMonth - 1
                           && day             == pickedDay;

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 11));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setMargin(new Insets(1,1,1,1));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setBackground(sel ? new Color(15, 90, 180) : Color.WHITE);
                btn.setForeground(sel ? Color.WHITE : new Color(30, 30, 30));

                if (!sel) {
                    btn.addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(220, 232, 255)); }
                        @Override public void mouseExited (MouseEvent e) { btn.setBackground(Color.WHITE); }
                    });
                }
                btn.addActionListener(e -> {
                    pickedYear  = displayYear[0];
                    pickedMonth = displayMonth[0] + 1;
                    pickedDay   = day;
                    datePicked = true;
                    updateDateCombo();
                    popup.dispose();
                });
                grid.add(btn);
            }
            grid.revalidate();
            grid.repaint();
        };

        prev.addActionListener(e -> {
            if (--displayMonth[0] < 0) { displayMonth[0] = 11; displayYear[0]--; }
            rebuild.run();
        });
        next.addActionListener(e -> {
            if (++displayMonth[0] > 11) { displayMonth[0] = 0; displayYear[0]++; }
            rebuild.run();
        });

        rebuild.run();

        popup.addWindowFocusListener(new WindowAdapter() {
            @Override public void windowLostFocus(WindowEvent e) { popup.dispose(); }
        });

        popup.setVisible(true);
    }

    private JButton navBtn(String t) {
        JButton b = new JButton(t);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(15, 70, 150));
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 6, 0, 6));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void updateDateCombo() {
        cmbDateFake.repaint();
    }

    // ── Public API (controller unchanged) ─────────────────────────────────────

    public String getDateAndTime() {
        return String.format("%04d-%02d-%02d %s",
                pickedYear, pickedMonth, pickedDay,
                cmbTime.getSelectedItem());
    }

    public void setDateAndTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) return;
        try {
            String[] parts = dateTime.trim().split(" ");
            String[] d = parts[0].split("-");
            pickedYear  = Integer.parseInt(d[0]);
            pickedMonth = Integer.parseInt(d[1]);
            pickedDay   = Integer.parseInt(d[2]);
            datePicked = true;
            updateDateCombo();
            if (parts.length > 1) {
                String t = parts[1].toLowerCase();
                for (int i = 0; i < cmbTime.getItemCount(); i++)
                    if (cmbTime.getItemAt(i).equals(t)) { cmbTime.setSelectedItem(t); break; }
            }
        } catch (Exception ex) { java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "Date/value parse failed: " + ex.getMessage()); }
    }

    public void resetDateAndTime() {
        datePicked = false;
        Calendar cal = Calendar.getInstance();
        pickedYear  = cal.get(Calendar.YEAR);
        pickedMonth = cal.get(Calendar.MONTH) + 1;
        pickedDay   = cal.get(Calendar.DAY_OF_MONTH);
        updateDateCombo();
        cmbTime.setSelectedItem("7:00am");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JComboBox<String> createStatusCombo() {
        JComboBox<String> c = new JComboBox<>(new String[]{" ", "SCHEDULED", "COMPLETED", "ONGOING"});
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setForeground(new Color(30, 30, 30));
        c.setPreferredSize(new Dimension(160, 32));
        c.setBorder(null);
        return c;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(50, 70, 110));
        return l;
    }

    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = color.darker();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(color); }
        });
        return b;
    }
}