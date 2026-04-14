package edp.view;

import edp.model.BillingModel;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.List;

public class AdminBillingView extends JFrame {

    public JTable            table;
    public DefaultTableModel model;

    public JComboBox<String> cmbTreatmentId;
    public JTextField        txtTotalAmount;
    public JTextField        txtAmountPaid;
    public JComboBox<String> cmbPaymentStatus, cmbPaymentMethod;
    public JButton           btnClear, btnAdd, btnUpdate, btnDelete, btnRefreshLists, btnReceipt;

    // ── Date picker ──────────────────────────────────────────────────────────
    private JComboBox<String> cmbPaymentDateFake;
    private int pickedYear, pickedMonth, pickedDay;
    private boolean datePicked = false;

    public static final String[] PAYMENT_STATUS = {" ", "PAID", "PENDING", "PARTIAL"};
    public static final String[] PAYMENT_METHOD = {" ", "CASH", "INSURANCE", "CARD", "E-WALLET"};

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private static final Font  UI      = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  UI_BOLD = new Font("Segoe UI", Font.BOLD,  13);
    private static final Color BLUE    = new Color(15, 70, 150);

    public AdminBillingView() {
        setTitle("Billing");
        setSize(1100, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Calendar cal = Calendar.getInstance();
        pickedYear  = cal.get(Calendar.YEAR);
        pickedMonth = cal.get(Calendar.MONTH) + 1;
        pickedDay   = cal.get(Calendar.DAY_OF_MONTH);

        buildUI();
    }

    // ── Public date API ──────────────────────────────────────────────────────
    public String getPaymentDate() {
        return String.format("%04d-%02d-%02d", pickedYear, pickedMonth, pickedDay);
    }

    public void setPaymentDate(String date) {
        if (date == null || date.isEmpty()) return;
        try {
            String[] d = date.split("-");
            pickedYear  = Integer.parseInt(d[0]);
            pickedMonth = Integer.parseInt(d[1]);
            pickedDay   = Integer.parseInt(d[2]);
            datePicked = true;
            cmbPaymentDateFake.repaint();
        } catch (Exception ex) { java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "Date/value parse failed: " + ex.getMessage()); }
    }

    public void clearPaymentDate() {
        datePicked = false;
        Calendar cal = Calendar.getInstance();
        pickedYear  = cal.get(Calendar.YEAR);
        pickedMonth = cal.get(Calendar.MONTH) + 1;
        pickedDay   = cal.get(Calendar.DAY_OF_MONTH);
        cmbPaymentDateFake.repaint();
    }

    // ── Treatment ID combo helpers ───────────────────────────────────────────
    public void loadTreatmentIds() {
        cmbTreatmentId.removeAllItems();
        cmbTreatmentId.addItem("");
        try {
            List<String> ids = BillingModel.getAllTreatmentIds();
            for (String id : ids) cmbTreatmentId.addItem(id.replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Could not load treatment list:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTreatmentIds() {
        loadTreatmentIds();
    }

    public String getSelectedTreatmentId() {
        Object sel = cmbTreatmentId.getSelectedItem();
        return sel == null ? "" : sel.toString().replace("T", "");
    }

    public void setTreatmentId(String val) {
        String item = val.startsWith("T") ? val : "T" + val;
        cmbTreatmentId.setSelectedItem(item);
    }

    // ── Table population ─────────────────────────────────────────────────────
    public void populateTable(java.util.List<Object[]> rows) {
        table.setRowSorter(null);   // detach sorter to suppress stale-index warning
        model.setRowCount(0);
        for (Object[] r : rows) {
            model.addRow(new Object[]{
                "B" + r[0], "T" + r[1], r[2], r[3], r[4], r[5], r[6]
            });
        }
        table.setAutoCreateRowSorter(true);  // re-attach fresh sorter
    }

    public void clearFields() {
        if (cmbTreatmentId.getItemCount() > 0) cmbTreatmentId.setSelectedIndex(0);
        txtTotalAmount.setText("");
        txtAmountPaid.setText("");
        cmbPaymentStatus.setSelectedIndex(0);
        cmbPaymentMethod.setSelectedIndex(0);
        clearPaymentDate();
        table.clearSelection();
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── UI build ─────────────────────────────────────────────────────────────
    private void buildUI() {

        // TABLE
        model = new DefaultTableModel(
            new String[]{
                "Billing ID", "Treatment ID", "Total Amount", "Amount Paid",
                "Payment Status", "Payment Date", "Payment Method"
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(UI);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JTableHeader h = table.getTableHeader();
        h.setFont(UI_BOLD); h.setBackground(BLUE); h.setForeground(Color.WHITE);
        h.setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // FORM
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ── ROW 0: Treatment ID | Total Amount | Amount Paid ──────────────────
        gbc.gridy = 0;

        cmbTreatmentId = new JComboBox<>();
        cmbTreatmentId.setFont(UI);
        cmbTreatmentId.setBackground(Color.WHITE);
        cmbTreatmentId.setPreferredSize(new Dimension(160, 32));
        cmbTreatmentId.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setText(value == null || value.toString().isEmpty() ? " " : value.toString());
                return lbl;
            }
        });
        loadTreatmentIds();
        addComboField(form, gbc, "Treatment ID", cmbTreatmentId, 0);

        txtTotalAmount = new JTextField(16);
        txtTotalAmount.setFont(UI);
        txtTotalAmount.setPreferredSize(new Dimension(160, 32));
        addTextField(form, gbc, "Total Amount", txtTotalAmount, 2);

        txtAmountPaid = new JTextField(16);
        txtAmountPaid.setFont(UI);
        txtAmountPaid.setPreferredSize(new Dimension(160, 32));
        addTextField(form, gbc, "Amount Paid", txtAmountPaid, 4);

        // ── ROW 1: Payment Status | Payment Method | Payment Date ─────────────
        gbc.gridy = 1;

        cmbPaymentStatus = new JComboBox<>(PAYMENT_STATUS);
        cmbPaymentStatus.setFont(UI);
        cmbPaymentStatus.setBackground(Color.WHITE);
        cmbPaymentStatus.setPreferredSize(new Dimension(160, 32));
        addComboField(form, gbc, "Payment Status", cmbPaymentStatus, 0);

        cmbPaymentMethod = new JComboBox<>(PAYMENT_METHOD);
        cmbPaymentMethod.setFont(UI);
        cmbPaymentMethod.setBackground(Color.WHITE);
        cmbPaymentMethod.setPreferredSize(new Dimension(160, 32));
        addComboField(form, gbc, "Payment Method", cmbPaymentMethod, 2);

        cmbPaymentDateFake = buildDateCombo();
        cmbPaymentDateFake.setPreferredSize(new Dimension(170, 28));
        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        form.add(label("Payment Date"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        form.add(cmbPaymentDateFake, gbc);

        // ── BUTTONS ───────────────────────────────────────────────────────────
        btnClear  = createButton("Clear",  new Color(120, 120, 140));
        btnAdd    = createButton("Add",    new Color(15, 90, 180));
        btnUpdate = createButton("Update", new Color(30, 140, 80));
        btnDelete = createButton("Delete", new Color(190, 40, 40));
        btnRefreshLists = createButton("Refresh List", new Color(80, 100, 140));
        btnReceipt = createButton("Print Receipt", new Color(80, 100, 140));


        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnReceipt);
        btnPanel.add(btnRefreshLists);
        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 6; gbc.weightx = 1;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    // ── Calendar date combo ──────────────────────────────────────────────────
    private JComboBox<String> buildDateCombo() {
        JComboBox<String> combo = new JComboBox<>(new String[]{""});
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton();
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
                btn.addActionListener(e -> showCalendarPopup(combo));
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
                lbl.setText(datePicked ? MONTH_NAMES[pickedMonth - 1] + " " + pickedDay + ", " + pickedYear : "Select Date");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                return lbl;
            }
        });

        combo.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { showCalendarPopup(combo); }
        });

        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                showCalendarPopup(combo);
            }
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        return combo;
    }

    private void showCalendarPopup(JComboBox<String> combo) {
        JWindow popup = new JWindow(this);
        popup.setLayout(new BorderLayout());

        int[] displayYear  = {pickedYear};
        int[] displayMonth = {pickedMonth - 1};

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(new Color(180, 196, 225)));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
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

        final int currentDay   = pickedDay;
        final int currentMonth = pickedMonth;
        final int currentYear  = pickedYear;

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
                boolean sel = displayYear[0]  == currentYear
                           && displayMonth[0] == currentMonth - 1
                           && day             == currentDay;

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
                        @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(220, 232, 255)); }
                        @Override public void mouseExited (MouseEvent e) { btn.setBackground(Color.WHITE); }
                    });
                }

                btn.addActionListener(e -> {
                    pickedYear  = displayYear[0];
                    pickedMonth = displayMonth[0] + 1;
                    pickedDay   = day;
                    datePicked = true;
                    cmbPaymentDateFake.repaint();
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

    private JButton navBtn(String t) {
        JButton b = new JButton(t);
        b.setForeground(Color.WHITE);
        b.setBackground(BLUE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 6, 0, 6));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Helpers (same as FrontdeskBillingView) ────────────────────────────────
    private void addTextField(JPanel panel, GridBagConstraints gbc,
                              String labelText, JTextField field, int x) {
        gbc.gridx = x; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(label(labelText), gbc);
        gbc.gridx = x + 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void addComboField(JPanel panel, GridBagConstraints gbc,
                               String labelText, JComboBox<String> combo, int x) {
        gbc.gridx = x; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(label(labelText), gbc);
        gbc.gridx = x + 1; gbc.weightx = 1;
        panel.add(combo, gbc);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(50, 70, 110));
        return l;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(UI_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Color hover = color.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited (MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private JButton createSmallButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setToolTipText("Click to refresh treatment list");
        Color hover = color.darker();
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(color); }
        });
        return b;
    }
}