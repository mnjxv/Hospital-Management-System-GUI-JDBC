package edp.view;

import edp.model.TreatmentModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrontdeskTreatmentView extends JFrame {

    public JTable table;
    public DefaultTableModel model;
    public JButton btnRefreshTable;
    private JLabel infoLabel;

    public FrontdeskTreatmentView() {
        setTitle("Treatment Management");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        model = new DefaultTableModel(
            new String[]{
                "Treatment ID", "Patient ID", "Treatment Date", "Diagnosis", "Follow-Up Date"
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

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        infoLabel = new JLabel("View Only — Contact an administrator to make changes.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 120));
        btnRefreshTable = createButton("Refresh Table", new Color(80, 100, 140));
        infoPanel.add(btnRefreshTable);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }

    public void populateTable(List<TreatmentModel> treatments) {
        model.setRowCount(0);
        for (TreatmentModel t : treatments) {
            model.addRow(new Object[]{
                t.getTreatmentId(),
                t.getPatientId(),
                t.getTreatmentDate(),
                t.getDiagnosis(),
                t.getFollowUpDate()
            });
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
