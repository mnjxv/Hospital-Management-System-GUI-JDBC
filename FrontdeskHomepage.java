package edp.view;

import edp.model.DoctorModel.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

//Frontdesk Doctor View — READ ONLY.
public class FrontdeskDoctorView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    private static final String[] COLUMNS = {
        "Doctor ID", "License Number", "Name",
        "Department", "Available Schedule", "Contact Number"
    };

    public FrontdeskDoctorView() {
        setTitle("Doctor Directory");
        setSize(950, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildTable();
        buildBottomPanel();
    }

    // ---- TABLE ----
    private void buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setGridColor(new Color(220, 228, 240));
        table.setShowGrid(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(15, 70, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        // Constrain Doctor ID column width
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // ---- BOTTOM (refresh + info) ----
    private void buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bottom.setBackground(new Color(245, 248, 255));
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JLabel infoLabel = new JLabel("View Only — Contact an administrator to make changes.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 120));

        btnRefresh = createButton("Refresh Table", new Color(80, 100, 140));
        bottom.add(btnRefresh);
        bottom.add(infoLabel);
        add(bottom, BorderLayout.SOUTH);
    }

    // ---- PUBLIC API used by Controller ----
    //Replaces table contents with the given doctor list
    public void populateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor d : doctors) tableModel.addRow(d.toRow());
    }

    public JTable  getTable()      { return table; }
    public JButton getBtnRefresh() { return btnRefresh; }

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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }
}