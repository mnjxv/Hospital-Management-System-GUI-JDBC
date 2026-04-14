package edp.view;

import edp.database.DBConnection;
import edp.model.PatientModel;
import edp.model.TreatmentModel;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;
import java.util.List;

public class AdminTreatmentView extends JFrame {

    public JTable table;
    public DefaultTableModel model;

    public JComboBox<String> cboPatientId;
    public JTextField txtDiagnosis, txtTreatmentFee;

    private JComboBox<String> cmbTreatmentDateFake;
    private JComboBox<String> cmbFollowUpDateFake;
    private JButton           btnTreatmentDateArrow;
    private JButton           btnFollowUpDateArrow;

    private int treatPickedYear, treatPickedMonth, treatPickedDay;
    private int followPickedYear, followPickedMonth, followPickedDay;
    private boolean treatDatePicked = false;
    private boolean followDatePicked = false;

    public JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefreshPatientList;

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public AdminTreatmentView() {
        setTitle("Treatment Management");
        setSize(1200, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Calendar cal = Calendar.getInstance();
        treatPickedYear  = followPickedYear  = cal.get(Calendar.YEAR);
        treatPickedMonth = followPickedMonth = cal.get(Calendar.MONTH) + 1;
        treatPickedDay   = followPickedDay   = cal.get(Calendar.DAY_OF_MONTH);

        buildTable();
        buildForm();
    }

    public void refreshPatientCombo() {
        cboPatientId.removeAllItems();
        cboPatientId.addItem("");
        try {
            List<String> ids = PatientModel.getAllPatientIds();
            for (String id : ids) {
                cboPatientId.addItem(id.replaceAll("[^0-9]", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildTable() {
        model = new DefaultTableModel(
                new String[]{
                        "Treatment ID", "Patient ID",
                        "Treatment Date", "Diagnosis",
                        "Follow-Up Date", "Treatment Fee"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== ROW 1 =====
        gbc.gridy = 0;

        // ✅ Patient ID ComboBox
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Patient ID"), gbc);
        cboPatientId = new JComboBox<>();
        cboPatientId.setBackground(Color.WHITE);
        cboPatientId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboPatientId.setPreferredSize(new Dimension(160, 32));
        cboPatientId.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setText(value == null || value.toString().isEmpty() ? " " : value.toString());
                return lbl;
            }
        });
        refreshPatientCombo();
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cboPatientId, gbc);

        // Treatment Date picker
        cmbTreatmentDateFake = buildDateCombo(true);
        gbc.gridx = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Treatment Date"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbTreatmentDateFake.setPreferredSize(new Dimension(160, 32));
        form.add(cmbTreatmentDateFake, gbc);

        // Follow-Up Date picker
        cmbFollowUpDateFake = buildDateCombo(false);
        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Follow-Up Date"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        cmbFollowUpDateFake.setPreferredSize(new Dimension(160, 32));
        form.add(cmbFollowUpDateFake, gbc);

        // ===== ROW 2 =====
        gbc.gridy = 1;
        addField(form, gbc, "Diagnosis",     txtDiagnosis    = new JTextField(16), 0);
        addField(form, gbc, "Treatment Fee", txtTreatmentFee = new JTextField(16), 2);

        // ===== BUTTONS =====
        btnClear  = createButton("Clear",              new Color(120, 120, 140));
        btnAdd    = createButton("Add",                new Color(15, 90, 180));
        btnUpdate = createButton("Update",            new Color(30, 140, 80));
        btnDelete = createButton("Delete",            new Color(190, 40, 40));
        btnRefreshPatientList = createButton("Refresh List", new Color(80, 100, 140));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshPatientList);
        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    private JComboBox<String> buildDateCombo(boolean isTreatment) {
        JComboBox<String> combo = new JComboBox<>(new String[]{""});
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(160, 32));

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton();
                if (isTreatment) btnTreatmentDateArrow = btn;
                else             btnFollowUpDateArrow  = btn;
                btn.setBackground(new Color(230, 235, 245));
                btn.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(180, 190, 210)));
                btn.setFocusPainted(false);
                btn.setIcon(new Icon() {
                    @Override
                    public int getIconWidth()  { return 8; }
                    @Override
                    public int getIconHeight() { return 5; }
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(80, 80, 80));
                        g2.fillPolygon(new int[]{x, x+8, x+4}, new int[]{y, y, y+5}, 3);
                        g2.dispose();
                    }
                });
                btn.addActionListener(e -> showCalendarPopup(combo, isTreatment));
                return btn;
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                int y = isTreatment ? treatPickedYear  : followPickedYear;
                int m = isTreatment ? treatPickedMonth : followPickedMonth;
                int d = isTreatment ? treatPickedDay   : followPickedDay;
                boolean picked = isTreatment ? treatDatePicked : followDatePicked;
                lbl.setText(picked ? MONTH_NAMES[m - 1] + " " + d + ", " + y : "Select Date");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                return lbl;
            }
        });

        combo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showCalendarPopup(combo, isTreatment);
            }
        });

        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                showCalendarPopup(combo, isTreatment);
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        return combo;
    }

    private void showCalendarPopup(JComboBox<String> combo, boolean isTreatment) {
        JWindow popup = new JWindow(this);
        popup.setLayout(new BorderLayout());

        int initYear  = isTreatment ? treatPickedYear  : followPickedYear;
        int initMonth = isTreatment ? treatPickedMonth : followPickedMonth;

        int[] displayYear  = {initYear};
        int[] displayMonth = {initMonth - 1};

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(new Color(180, 196, 225)));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 70, 150));
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JButton prev = navBtn("‹"), next = navBtn("›");
        JLabel monthYearLbl = new JLabel("", SwingConstants.CENTER);
        monthYearLbl.setForeground(Color.WHITE);
        monthYearLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        header.add(prev, BorderLayout.WEST);
        header.add(monthYearLbl, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);

        JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        root.add(header, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);

        int currentPickedDay   = isTreatment ? treatPickedDay   : followPickedDay;
        int currentPickedMonth = isTreatment ? treatPickedMonth : followPickedMonth;
        int currentPickedYear  = isTreatment ? treatPickedYear  : followPickedYear;

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
                boolean sel = displayYear[0]  == currentPickedYear
                           && displayMonth[0] == currentPickedMonth - 1
                           && day             == currentPickedDay;

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 11));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setMargin(new Insets(1, 1, 1, 1));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setBackground(sel ? new Color(15, 90, 180) : Color.WHITE);
                btn.setForeground(sel ? Color.WHITE : new Color(30, 30, 30));

                if (!sel) {
                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(220, 232, 255)); }
                        @Override
                        public void mouseExited (MouseEvent e) { btn.setBackground(Color.WHITE); }
                    });
                }

                btn.addActionListener(e -> {
                    if (isTreatment) {
                        treatPickedYear  = displayYear[0];
                        treatPickedMonth = displayMonth[0] + 1;
                        treatPickedDay   = day;
                        treatDatePicked = true;
                        cmbTreatmentDateFake.repaint();
                    } else {
                        followPickedYear  = displayYear[0];
                        followPickedMonth = displayMonth[0] + 1;
                        followPickedDay   = day;
                        followDatePicked = true;
                        cmbFollowUpDateFake.repaint();
                    }
                    popup.dispose();
                });
                grid.add(btn);
            }
            grid.revalidate();
            grid.repaint();
        };

        prev.addActionListener(e -> {
            if (--displayMonth[0] < 0)  { displayMonth[0] = 11; displayYear[0]--; }
            rebuild.run();
        });
        next.addActionListener(e -> {
            if (++displayMonth[0] > 11) { displayMonth[0] = 0;  displayYear[0]++; }
            rebuild.run();
        });

        rebuild.run();
        popup.add(root);
        popup.pack();

        Point loc = combo.getLocationOnScreen();
        popup.setLocation(loc.x, loc.y + combo.getHeight());

        popup.addWindowFocusListener(new WindowAdapter() {
            @Override public void windowLostFocus(WindowEvent e) { popup.dispose(); }
        });

        popup.setVisible(true);
    }

    // ================= PUBLIC DATE API =================
    public String getTreatmentDate() {
        return String.format("%04d-%02d-%02d", treatPickedYear, treatPickedMonth, treatPickedDay);
    }

    public String getFollowUpDate() {
        return String.format("%04d-%02d-%02d", followPickedYear, followPickedMonth, followPickedDay);
    }

    public void setTreatmentDate(String date) {
        if (date == null || date.isEmpty()) return;
        try {
            String[] d = date.split("-");
            treatPickedYear  = Integer.parseInt(d[0]);
            treatPickedMonth = Integer.parseInt(d[1]);
            treatPickedDay   = Integer.parseInt(d[2]);
            treatDatePicked = true;
            cmbTreatmentDateFake.repaint();
        } catch (Exception ex) { java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "Date/value parse failed: " + ex.getMessage()); }
    }

    public void setFollowUpDate(String date) {
        if (date == null || date.isEmpty()) return;
        try {
            String[] d = date.split("-");
            followPickedYear  = Integer.parseInt(d[0]);
            followPickedMonth = Integer.parseInt(d[1]);
            followPickedDay   = Integer.parseInt(d[2]);
            followDatePicked = true;
            cmbFollowUpDateFake.repaint();
        } catch (Exception ex) { java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "Date/value parse failed: " + ex.getMessage()); }
    }

    public void resetDates() {
        treatDatePicked = false;
        followDatePicked = false;
        Calendar cal = Calendar.getInstance();
        treatPickedYear  = followPickedYear  = cal.get(Calendar.YEAR);
        treatPickedMonth = followPickedMonth = cal.get(Calendar.MONTH) + 1;
        treatPickedDay   = followPickedDay   = cal.get(Calendar.DAY_OF_MONTH);
        cmbTreatmentDateFake.repaint();
        cmbFollowUpDateFake.repaint();
    }

    public void populateTable(List<TreatmentModel> treatments) {
        model.setRowCount(0);
        for (TreatmentModel t : treatments) {
            model.addRow(new Object[]{
                t.getTreatmentId(),
                t.getPatientId(),
                t.getTreatmentDate(),
                t.getDiagnosis(),
                t.getFollowUpDate(),
                t.getTreatmentFee()
            });
        }
    }

    public void clearFields() {
        if (cboPatientId.getItemCount() > 0) cboPatientId.setSelectedIndex(0);
        txtDiagnosis.setText("");
        txtTreatmentFee.setText("");
        resetDates();
        table.clearSelection();
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
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

    private void addField(JPanel panel, GridBagConstraints gbc,
                          String labelText, JTextField field, int x) {
        gbc.gridx = x; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(label(labelText), gbc);
        gbc.gridx = x + 1; gbc.weightx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(160, 32));
        panel.add(field, gbc);
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