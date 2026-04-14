package edp.view;

import edp.model.BedModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminBedView extends JFrame {

    public static void refreshAllBedLists() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public JTable table;
    public DefaultTableModel model;
    public JComboBox<String> cboRoomId;
    public JComboBox<String> cboBStatus;
    public JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefreshRooms;

    public AdminBedView() {
        setTitle("Bed Management");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
    }

    public void refreshRoomCombo() {
        cboRoomId.removeAllItems();
        cboRoomId.addItem("");
        for (String rid : BedModel.getAllRoomIds()) {
            cboRoomId.addItem(rid);
        }
    }

    private void buildUI() {
        model = new DefaultTableModel(
                new String[]{"Bed ID", "Room ID", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(15, 70, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room ID ComboBox
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblRoom = new JLabel("Room ID");
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRoom.setForeground(new Color(50, 70, 110));
        form.add(lblRoom, gbc);

        cboRoomId = new JComboBox<>();
        cboRoomId.setBackground(Color.WHITE);
        cboRoomId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboRoomId.setPreferredSize(new Dimension(160, 32));
        cboRoomId.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setText(value == null || value.toString().isEmpty() ? " " : value.toString());
                return lbl;
            }
        });
        cboRoomId.addItem("");
        for (String rid : BedModel.getAllRoomIds()) cboRoomId.addItem(rid);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cboRoomId, gbc);

        // Bed Status
        gbc.gridx = 2; gbc.weightx = 0;
        JLabel lblStatus = new JLabel("Bed Status");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(50, 70, 110));
        form.add(lblStatus, gbc);

        cboBStatus = new JComboBox<>(new String[]{" ", "AVAILABLE", "OCCUPIED"});
        cboBStatus.setBackground(Color.WHITE);
        cboBStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboBStatus.setPreferredSize(new Dimension(160, 32));
        gbc.gridx = 3; gbc.weightx = 1;
        form.add(cboBStatus, gbc);

        // Buttons
        btnAdd    = createButton("Add",    new Color(15, 90, 180));
        btnUpdate = createButton("Update", new Color(30, 140, 80));
        btnDelete = createButton("Delete", new Color(190, 40, 40));
        btnClear  = createButton("Clear",  new Color(120, 120, 140));
        btnRefreshRooms = createRefreshButton("Refresh List");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 11, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshRooms);
        btnPanel.add(btnClear);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 5;
        gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(scrollPane, BorderLayout.CENTER);
        add(form, BorderLayout.SOUTH);
    }

    private JButton createRefreshButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(80, 100, 140));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        Color hoverColor = new Color(80, 100, 140).darker();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hoverColor); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(new Color(80, 100, 140)); }
        });
        return b;
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
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }
}
