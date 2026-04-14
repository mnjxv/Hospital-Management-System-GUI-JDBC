package edp.view;

import edp.controller.AdminPatientController;
import edp.controller.AdminAppointmentController;
import edp.controller.AdminBedController;
import edp.controller.AdminBillingController;
import edp.controller.LoginController;
import edp.controller.AdminTreatmentController;
import edp.controller.AdminDoctorController;
import edp.controller.AdminRoomController;
import edp.model.DoctorModel;
import edp.model.PatientModel;
import java.awt.*;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdminHospitalDashboardView extends JFrame {

    public AdminHospitalDashboardView(String admin) {
        setTitle("Bulacan Medical Mission Group Hospital and Pharmacy");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 70, 150));
        header.setPreferredSize(new Dimension(0, 55));

        JLabel title = new JLabel("   Bulacan Medical Mission Group Hospital and Pharmacy");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);

        // ---- RIGHT PANEL ----
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Admin");
        roleLabel.setForeground(new Color(180, 210, 255));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(15, 70, 150));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                LoginView loginView = new LoginView();
                new LoginController(loginView);
                loginView.setVisible(true);
            }
        });

        rightPanel.add(roleLabel);
        rightPanel.add(logoutBtn);
        header.add(rightPanel, BorderLayout.EAST);

        // ---- TABBED PANE ----
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // HOME
        tabbedPane.addTab("HOME", new AdminHomepage(admin));

        // DOCTOR
        AdminDoctorView doctorView = new AdminDoctorView();
        new AdminDoctorController(doctorView, new DoctorModel());
        tabbedPane.addTab("DOCTOR", embedView(doctorView));

        // PATIENT ← FIXED: controller now wired up
        AdminPatientView patientView = new AdminPatientView();
        new AdminPatientController(patientView, new PatientModel());
        tabbedPane.addTab("PATIENT", embedView(patientView));

        // APPOINTMENT
        AdminAppointmentView appointmentView = new AdminAppointmentView();
        new AdminAppointmentController(appointmentView);
        tabbedPane.addTab("APPOINTMENT", embedView(appointmentView));

        // TREATMENT
        AdminTreatmentView treatmentView = new AdminTreatmentView();
        new AdminTreatmentController(treatmentView);
        tabbedPane.addTab("TREATMENT", embedView(treatmentView));

        // BILLING  (was missing the controller)
AdminBillingView billingView = new AdminBillingView();
new AdminBillingController(billingView);          // ← add this line
tabbedPane.addTab("BILLING", embedView(billingView));
        // ROOM
        AdminRoomView roomView = new AdminRoomView();
        new AdminRoomController(roomView);
        tabbedPane.addTab("ROOM", embedView(roomView));

        // BED
        AdminBedView bedView = new AdminBedView();
        new AdminBedController(bedView);
        tabbedPane.addTab("BED", embedView(bedView));

        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel embedView(JFrame frame) {
        JPanel panel = new JPanel(new BorderLayout());
        Container content = frame.getContentPane();
        content.setBackground(new Color(245, 248, 255));
        panel.add(content, BorderLayout.CENTER);
        frame.dispose();
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminHospitalDashboardView("ADMIN"));
    }
}