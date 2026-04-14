package edp.view;

import edp.model.DoctorModel.Doctor;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDoctorView extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtLicense, txtName, txtContact;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbSchedule;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefreshList;
    
    private static final String[] COLUMNS = {
            "Doctor ID", "License Number", "Name",
            "Department", "Available Schedule", "Contact Number" };
    private static final String[] SCHEDULE_OPTIONS = {
            "",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY",
            "MONDAY - FRIDAY",
            "MONDAY - SATURDAY",
            "MONDAY, WEDNESDAY, FRIDAY",
            "TUESDAY, THURSDAY, SATURDAY"};
    private static final String[] DEPARTMENT_OPTIONS = {
            "",
            "OBGYN", "SURGERY", "ORTHOPEDIC", "ONCOLOGY", "UROLOGY",
            "PEDIA", "ENDOCRINOLOGY", "INTERNAL MEDICINE", "PULMONOLOGY", "ENT"};
    
    public AdminDoctorView() {
        setTitle("Doctor Management");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildTable();
        buildForm();
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(200, 220, 255));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(15, 70, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

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

        //License Number, Name, Department
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(styledLabel("License Number"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtLicense = new JTextField(16);
        txtLicense.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtLicense.setPreferredSize(new Dimension(160, 32));
        form.add(txtLicense, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(styledLabel("Name"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtName = new JTextField(16);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(160, 32));
        form.add(txtName, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        form.add(styledLabel("Department"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        cmbDepartment = new JComboBox<>(DEPARTMENT_OPTIONS);
        cmbDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDepartment.setBackground(Color.WHITE);
        cmbDepartment.setPreferredSize(new Dimension(160, 32));
        cmbDepartment.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setText(value == null || value.toString().isEmpty() ? "Select Department" : value.toString());
                return lbl;
            }
        });
        form.add(cmbDepartment, gbc);

        // Row 2: Schedule, Contact
        gbc.gridy = 1;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(styledLabel("Available Schedule"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbSchedule = new JComboBox<>(SCHEDULE_OPTIONS);
        cmbSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbSchedule.setBackground(Color.WHITE);
        cmbSchedule.setPreferredSize(new Dimension(160, 32));
        cmbSchedule.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setText(value == null || value.toString().isEmpty() ? "Select Schedule" : value.toString());
                return lbl;
            }
        });
        form.add(cmbSchedule, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(styledLabel("Contact Number"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtContact = new JTextField(16);
        txtContact.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContact.setPreferredSize(new Dimension(160, 32));
        ((AbstractDocument) txtContact.getDocument()).setDocumentFilter(createNumberFilter());
        form.add(txtContact, gbc);

        // Buttons
        btnClear  = createButton("Clear",        new Color(120, 120, 140));
        btnAdd    = createButton("Add",          new Color(15, 90, 180));
        btnUpdate = createButton("Update",      new Color(30, 140, 80));
        btnDelete = createButton("Delete",      new Color(190, 40, 40));
        btnRefreshList = createButton("Refresh List", new Color(80, 100, 140));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshList);
        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    public void populateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor d : doctors) tableModel.addRow(d.toRow());
    }

    public JTable getTable()        { return table; }
    public JButton getBtnAdd()      { return btnAdd; }
    public JButton getBtnUpdate()   { return btnUpdate; }
    public JButton getBtnDelete()   { return btnDelete; }
    public JButton getBtnClear()    { return btnClear; }
    public JButton getBtnRefreshList() { return btnRefreshList; }

    public String getLicense()    { return txtLicense.getText().trim(); }
    @Override
    public String getName()       { return txtName.getText().trim(); }
    public String getDepartment() {
        Object sel = cmbDepartment.getSelectedItem();
        return sel != null ? sel.toString() : "";
    }
    public String getContact()    { return txtContact.getText().trim(); }

    //Returns the currently selected schedule option
    public String getSchedule() {
        Object selected = cmbSchedule.getSelectedItem();
        return selected != null ? selected.toString() : "";
    }

    public void setLicense(String v)    { txtLicense.setText(v); }
    @Override
    public void setName(String v)       { txtName.setText(v); }
    public void setDepartment(String v) {
        for (int i = 0; i < cmbDepartment.getItemCount(); i++) {
            if (cmbDepartment.getItemAt(i).equalsIgnoreCase(v)) {
                cmbDepartment.setSelectedIndex(i);
                return;
            }
        }
        cmbDepartment.setSelectedIndex(0);
    }
    public void setContact(String v)    { txtContact.setText(v); }

    //Selects the matching item in the combo box
    public void setSchedule(String v) {
        for (int i = 0; i < cmbSchedule.getItemCount(); i++) {
            if (cmbSchedule.getItemAt(i).equalsIgnoreCase(v)) {
                cmbSchedule.setSelectedIndex(i);
                return;
            }
        }
        cmbSchedule.setSelectedIndex(0); // default if not matched
    }

    public void clearFields() {
        txtLicense.setText("");
        txtName.setText("");
        cmbDepartment.setSelectedIndex(0);
        txtContact.setText("");
        cmbSchedule.setSelectedIndex(0);
        table.clearSelection();
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        Color hover = color.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
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
}