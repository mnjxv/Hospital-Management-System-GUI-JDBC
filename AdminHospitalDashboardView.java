package edp.view;

import edp.model.RoomModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrontdeskRoomView extends JFrame {

    public JTable table;
    public DefaultTableModel model;
    public JTextField txtRoomId;
    public JComboBox<String> cmbRoomCategory;
    public JButton btnUpdate, btnClear, btnRefreshList;

    private static final java.util.List<FrontdeskRoomView> openViews = new java.util.ArrayList<>();

    public static void refreshAllRoomLists() {
        for (FrontdeskRoomView view : openViews) {
            try {
                view.populateTable(RoomModel.getAllForTable());
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

    public FrontdeskRoomView() {
        setTitle("Room Management");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
    }

    public void buildUI() {
        model = new DefaultTableModel(new String[]{"Room ID", "Room Category"}, 0) {
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

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Room ID
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0;
        form.add(label("Room ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtRoomId = new JTextField(16);
        txtRoomId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRoomId.setEditable(false);
        txtRoomId.setBackground(new Color(230, 235, 245));
        txtRoomId.setPreferredSize(new Dimension(160, 32));
        form.add(txtRoomId, gbc);

        // Room Category
        gbc.gridx = 2; gbc.weightx = 0;
        form.add(label("Room Category"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbRoomCategory = new JComboBox<>(new String[]{" ", "PUBLIC", "PRIVATE", "SEMI-PRIVATE"});
        cmbRoomCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRoomCategory.setBackground(Color.WHITE);
        cmbRoomCategory.setPreferredSize(new Dimension(160, 32));
        form.add(cmbRoomCategory, gbc);

        // Buttons
        btnClear  = createButton("Clear",  new Color(120, 120, 140));
        btnUpdate = createButton("Update", new Color(30, 140, 80));
        btnRefreshList = createButton("Refresh List", new Color(80, 100, 140));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefreshList);
        btnPanel.add(btnClear);
        btnPanel.add(btnUpdate);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.weightx = 1; gbc.insets = new Insets(12, 8, 0, 8);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);
    }

    public void clearFields() {
        cmbRoomCategory.setSelectedIndex(0);
        txtRoomId.setText("");
        table.clearSelection();
    }

    public void populateTable(List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    private JLabel label(String text) {
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
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Color hover = color.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    public JButton getBtnRefreshList() { return btnRefreshList; }
}