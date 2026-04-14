package edp.view;

import edp.model.PatientModel.Patient;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FrontdeskPatientView extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        txtName, txtAddress, txtContact, txtMedHistory;
    private JComboBox<String> cmbFilter, cmbGender, cmbPatientType, cmbRoom;
    private JLabel            lblRoom;
    private CalendarButton    dobPicker;
    private JButton           btnAdd, btnUpdate, btnClear, btnRefreshList;

    private static final String[] COLUMNS = {
        "Patient ID", "Name", "Gender", "Address",
        "Contact Number", "Date of Birth", "Medical History", "Patient Type", "Room ID"
    };
    private static final Font  UI      = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  UI_BOLD = new Font("Segoe UI", Font.BOLD,  13);
    private static final Color BLUE    = new Color(15, 70, 150);

    private static final java.util.List<FrontdeskPatientView> openViews = new java.util.ArrayList<>();

    public static void refreshAllPatientLists() {
        for (FrontdeskPatientView view : openViews) {
            try {
                edp.model.PatientModel model = new edp.model.PatientModel();
                view.populateTable(model.getPatients(null));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            if (!openViews.contains(this)) openViews.add(this);
        } else {
            openViews.remove(this);
        }
    }

    public FrontdeskPatientView() {
        setTitle("Patient Management - Front Desk");
        setSize(1100, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildTable();
        buildFilterBar();
        buildForm();
    }

    // ── Filter Bar ──────────────────────────────────────────────────────────────
    private void buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        bar.setBackground(new Color(230, 236, 250));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 196, 225)));

        JLabel lbl = new JLabel("Filter by Patient Type:");
        lbl.setFont(UI_BOLD); lbl.setForeground(new Color(30, 60, 120));

        cmbFilter = new JComboBox<>(new String[]{"All", "INPATIENT", "OUTPATIENT"});
        cmbFilter.setFont(UI);
        cmbFilter.setBackground(Color.WHITE);
        cmbFilter.setPreferredSize(new Dimension(150, 28));
        bar.add(lbl); bar.add(cmbFilter);
        add(bar, BorderLayout.NORTH);
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private void buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(UI);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setAutoCreateRowSorter(true);

        JTableHeader h = table.getTableHeader();
        h.setFont(UI_BOLD); h.setBackground(BLUE); h.setForeground(Color.WHITE);
        h.setReorderingAllowed(false);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    private void buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Name, Date of Birth, Gender
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(styledLabel("Name"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtName = new JTextField(16);
        txtName.setFont(UI);
        txtName.setPreferredSize(new Dimension(160, 32));
        form.add(txtName, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(styledLabel("Date of Birth"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        dobPicker = new CalendarButton();
        form.add(dobPicker, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        form.add(styledLabel("Gender"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        cmbGender = new JComboBox<>(new String[]{" ", "MALE", "FEMALE"});
        cmbGender.setFont(UI);
        cmbGender.setBackground(Color.WHITE);
        cmbGender.setPreferredSize(new Dimension(160, 32));
        form.add(cmbGender, gbc);

        // Row 2: Contact, Address, Medical History
        gbc.gridy = 1;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(styledLabel("Contact Number"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtContact = new JTextField(16);
        txtContact.setFont(UI);
        txtContact.setPreferredSize(new Dimension(160, 32));
        ((AbstractDocument) txtContact.getDocument()).setDocumentFilter(createNumberFilter());
        form.add(txtContact, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(styledLabel("Address"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtAddress = new JTextField(16);
        txtAddress.setFont(UI);
        txtAddress.setPreferredSize(new Dimension(160, 32));
        form.add(txtAddress, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        form.add(styledLabel("Medical History"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        txtMedHistory = new JTextField(16);
        txtMedHistory.setFont(UI);
        txtMedHistory.setPreferredSize(new Dimension(160, 32));
        form.add(txtMedHistory, gbc);

        // Row 3: Patient Type, Room ID
        gbc.gridy = 2;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(styledLabel("Patient Type"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbPatientType = new JComboBox<>(new String[]{" ", "INPATIENT", "OUTPATIENT"});
        cmbPatientType.setFont(UI);
        cmbPatientType.setBackground(Color.WHITE);
        cmbPatientType.setPreferredSize(new Dimension(160, 32));
        cmbPatientType.addActionListener(e -> updateRoomFieldVisibility());
        form.add(cmbPatientType, gbc);

        lblRoom = styledLabel("Room ID");
        gbc.gridx = 2; gbc.weightx = 0;
        form.add(lblRoom, gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbRoom = new JComboBox<>();
        cmbRoom.setFont(UI);
        cmbRoom.setBackground(Color.WHITE);
        cmbRoom.setPreferredSize(new Dimension(160, 32));
        form.add(cmbRoom, gbc);

        // Buttons
        btnClear  = createButton("Clear",  new Color(120, 120, 140));
        btnAdd    = createButton("Add",    new Color(15, 90, 180));
        btnUpdate = createButton("Update", new Color(30, 140, 80));
        btnRefreshList = createButton("Refresh List", new Color(80, 100, 140));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshList);
        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(50, 70, 110));
        return lbl;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(UI_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        Color hover = color.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }


    // ── Public API ────────────────────────────────────────────────────────────
    public JTable             getTable()      { return table; }
    public JComboBox<String>  getCmbFilter()  { return cmbFilter; }
    public JButton getBtnAdd()    { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnClear()  { return btnClear; }
    public JButton getBtnRefreshList() { return btnRefreshList; }

    public String getPatientName() { return txtName.getText().trim(); }
    public String getDob()         { return dobPicker.getDate(); }
    public String getGender()      { return (String) cmbGender.getSelectedItem(); }
    public String getContact()     { return txtContact.getText().trim(); }
    public String getAddress()     { return txtAddress.getText().trim(); }
    public String getMedHistory()  { return txtMedHistory.getText().trim(); }
    public String getPatientType() { return (String) cmbPatientType.getSelectedItem(); }
    public String getRoom()        { return (String) cmbRoom.getSelectedItem(); }

    public void setPatientName(String v) { txtName.setText(v); }
    public void setDob        (String v) { dobPicker.setDate(v); }
    public void setGender     (String v) { cmbGender.setSelectedItem(v); }
    public void setContact    (String v) { txtContact.setText(v); }
    public void setAddress    (String v) { txtAddress.setText(v); }
    public void setMedHistory (String v) { txtMedHistory.setText(v); }
    public void setPatientType(String v) { cmbPatientType.setSelectedItem(v); }

    public void setRoom(String v) {
        if (v == null || v.isEmpty()) {
            cmbRoom.setSelectedIndex(-1);
        } else {
            // Add to combo if not present
            boolean found = false;
            for (int i = 0; i < cmbRoom.getItemCount(); i++) {
                if (cmbRoom.getItemAt(i).equals(v)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                cmbRoom.addItem(v);
            }
            cmbRoom.setSelectedItem(v);
        }
    }

    /** Returns the Patient ID of the currently selected table row, or null if none. */
    public String getSelectedPatientId() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
    }

    public void populateTable(List<Patient> patients) {
        tableModel.setRowCount(0);
        for (Patient p : patients) tableModel.addRow(p.toRow());
    }

    private void updateRoomFieldVisibility() {
        String type = (String) cmbPatientType.getSelectedItem();
        boolean isInpatient = "INPATIENT".equals(type);
        lblRoom.setVisible(isInpatient);
        cmbRoom.setVisible(isInpatient);
        if (!isInpatient) {
            cmbRoom.setSelectedIndex(-1);
        }
    }

    public void setRoomOptions(String[] rooms) {
        cmbRoom.setModel(new DefaultComboBoxModel<>(rooms));
    }

    public void initializeForm() {
        updateRoomFieldVisibility();
    }

    public void clearFields() {
        txtName.setText(""); dobPicker.clear();
        cmbGender.setSelectedIndex(0); txtContact.setText("");
        txtAddress.setText(""); txtMedHistory.setText("");
        cmbPatientType.setSelectedIndex(0); cmbRoom.setSelectedIndex(-1); table.clearSelection();
        txtName.requestFocus();
    }

    private DocumentFilter createNumberFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    public void showError  (String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    public void showWarning(String msg) { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }

    // ── CalendarButton ────────────────────────────────────────────────────────
    public static class CalendarButton extends JPanel {
        private static final DateTimeFormatter DISP = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        private static final DateTimeFormatter SQL  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        private LocalDate               date      = null;
        private final JComboBox<String> fakeCombo;

        public CalendarButton() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(160, 32));

            fakeCombo = new JComboBox<>(new String[]{""});
            fakeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fakeCombo.setBackground(Color.WHITE);

            fakeCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    lbl.setText(date == null ? "Select Date" : date.format(DISP));
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                    return lbl;
                }
            });

            fakeCombo.setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton arrow = new JButton();
                    arrow.setBackground(new Color(230, 235, 245));
                    arrow.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(180, 190, 210)));
                    arrow.setFocusPainted(false);
                    arrow.setIcon(new Icon() {
                        @Override
                        public int getIconWidth()  { return 8; }
                        @Override
                        public int getIconHeight() { return 5; }
                        @Override
                        public void paintIcon(Component c, Graphics g, int x, int y) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(80, 80, 80));
                            g2.fillPolygon(new int[]{x, x + 8, x + 4}, new int[]{y, y, y + 5}, 3);
                            g2.dispose();
                        }
                    });
                    arrow.addActionListener(e -> openCalendar());
                    return arrow;
                }
            });

            fakeCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    lbl.setText(date != null ? date.format(DISP) : "Select Date");
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                    return lbl;
                }
            });

            fakeCombo.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { openCalendar(); }
            });

            fakeCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                    SwingUtilities.invokeLater(fakeCombo::hidePopup);
                }
                @Override
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
                @Override
                public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
            });

            add(fakeCombo, BorderLayout.CENTER);
        }

        private void openCalendar() {
            JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setUndecorated(true);
            dlg.add(new CalendarPanel(date != null ? date : LocalDate.now(), d -> {
                date = d;
                fakeCombo.repaint();
                dlg.dispose();
            }));
            dlg.pack();
            Point loc = fakeCombo.getLocationOnScreen();
            dlg.setLocation(loc.x, loc.y + fakeCombo.getHeight());
            dlg.setVisible(true);
        }

        public String getDate() { return date == null ? "" : date.format(SQL); }

        public void setDate(String iso) {
            try {
                if (iso == null || iso.isBlank()) { date = null; }
                else { date = LocalDate.parse(iso.substring(0, 10), SQL); }
            } catch (Exception ex) { date = null; }
            fakeCombo.repaint();
        }

        public void clear() { date = null; fakeCombo.repaint(); }
    }

    // ── CalendarPanel ─────────────────────────────────────────────────────────
    private static class CalendarPanel extends JPanel {
        private static final Color HDR = new Color(15, 70, 150), HOV = new Color(200, 220, 255);
        interface Picker { void pick(LocalDate d); }

        private YearMonth    month;
        private final Picker onPick;

        CalendarPanel(LocalDate init, Picker onPick) {
            this.month  = YearMonth.from(init);
            this.onPick = onPick;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(new Color(180, 196, 225)));
            setBackground(Color.WHITE);
            refresh();
        }

        private void refresh() {
            removeAll();

            JButton prev = navBtn("‹"), next = navBtn("›");
            prev.addActionListener(e -> { month = month.minusMonths(1); refresh(); });
            next.addActionListener(e -> { month = month.plusMonths(1);  refresh(); });

            JLabel title = new JLabel(
                month.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
                + " " + month.getYear(), SwingConstants.CENTER);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JPanel hdr = new JPanel(new BorderLayout());
            hdr.setBackground(HDR); hdr.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            hdr.add(prev, BorderLayout.WEST); hdr.add(title, BorderLayout.CENTER); hdr.add(next, BorderLayout.EAST);
            add(hdr, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
            grid.setBackground(Color.WHITE);
            grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

            for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11)); l.setForeground(new Color(100, 110, 140));
                grid.add(l);
            }

            LocalDate today = LocalDate.now();
            int offset = month.atDay(1).getDayOfWeek().getValue() % 7;
            for (int i = 0; i < offset; i++) grid.add(new JLabel());

            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate d     = month.atDay(day);
                boolean isToday = d.equals(today);
                JLabel cell     = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                cell.setOpaque(true); cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cell.setPreferredSize(new Dimension(34, 28));
                cell.setBackground(isToday ? HDR : Color.WHITE);
                cell.setForeground(isToday ? Color.WHITE : new Color(30, 30, 30));
                cell.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { if (!isToday) cell.setBackground(HOV); }
                    @Override public void mouseExited (MouseEvent e) { if (!isToday) cell.setBackground(Color.WHITE); }
                    @Override public void mouseClicked(MouseEvent e) { onPick.pick(d); }
                });
                grid.add(cell);
            }
            add(grid, BorderLayout.CENTER);
            revalidate(); repaint();
        }

        private JButton navBtn(String t) {
            JButton b = new JButton(t);
            b.setForeground(Color.WHITE); b.setBackground(HDR);
            b.setBorderPainted(false); b.setFocusPainted(false); b.setOpaque(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }
    }
}