package edp.view;

import edp.controller.FrontdeskAppointmentController;
import edp.controller.FrontdeskPatientController;
import edp.controller.FrontdeskBillingController;
import edp.controller.FrontdeskTreatmentController;
import edp.controller.FrontdeskDoctorController;
import edp.controller.LoginController;
import edp.controller.FrontdeskRoomController;
import edp.controller.FrontdeskBedController;
import java.awt.*;
import javax.swing.*;
import edp.model.DoctorModel;
import edp.model.PatientModel;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class FrontdeskHospitalDashboardView extends JFrame {

    public FrontdeskHospitalDashboardView(String frontdesk) {
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Front Desk");
        roleLabel.setForeground(new Color(180, 210, 255));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(15, 70, 150));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setFocusPainted(false);

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?",
                    "Logout", JOptionPane.YES_NO_OPTION
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

        // ---- TABS ----
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // HOME
        tabbedPane.addTab("HOME", new AdminHomepage(frontdesk));

        // DOCTOR
        FrontdeskDoctorView doctorView = new FrontdeskDoctorView();
        new FrontdeskDoctorController(doctorView, new DoctorModel());
        tabbedPane.addTab("DOCTOR", embedView(doctorView));
        
        //PATIENT
        FrontdeskPatientView patientView = new FrontdeskPatientView();
        new FrontdeskPatientController(patientView, new PatientModel());
        tabbedPane.addTab("PATIENT", embedView(patientView));
        
        // APPOINTMENT
        FrontdeskAppointmentView appointmentView = new FrontdeskAppointmentView();
        new FrontdeskAppointmentController(appointmentView);
        tabbedPane.addTab("APPOINTMENT", embedView(appointmentView));

        // TREATMENT
        FrontdeskTreatmentView treatmentView = new FrontdeskTreatmentView();
        new FrontdeskTreatmentController(treatmentView);
        tabbedPane.addTab("TREATMENT", embedView(treatmentView));

        //BILLING
        FrontdeskBillingView billingView = new FrontdeskBillingView();
        new FrontdeskBillingController(billingView);      
        tabbedPane.addTab("BILLING", embedView(billingView));
        
        // ROOM
        FrontdeskRoomView roomView = new FrontdeskRoomView();
        new FrontdeskRoomController(roomView);
        tabbedPane.addTab("ROOM", embedView(roomView));

        // BED
        FrontdeskBedView bedView = new FrontdeskBedView();
        new FrontdeskBedController(bedView);
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
        SwingUtilities.invokeLater(() -> new FrontdeskHospitalDashboardView("FRONTDESK"));
    }
}