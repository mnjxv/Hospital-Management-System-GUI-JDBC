package edp.view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    public JButton btnLogin;
    public JTextField txtUsername;
    public JPasswordField txtPassword;
    public JCheckBox chkShowPassword;

    public LoginView() {
        setTitle("Hospital Management System - Login");
        setSize(500, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ================= BACKGROUND =================
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 65, 140),
                        0, getHeight(), new Color(30, 100, 180)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        background.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 60));
        add(background, BorderLayout.CENTER);

        // ================= CARD =================
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 390));
        card.setBorder(BorderFactory.createEmptyBorder(25, 35, 15, 35));

        // ================= CONTENT =================
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 60, 130));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 120, 160));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUsername = createLabel("Username");
        txtUsername = createTextField();

        JLabel lblPassword = createLabel("Password");
        txtPassword = new JPasswordField();
        styleField(txtPassword);

        chkShowPassword = new JCheckBox("Show password");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setOpaque(false);
        chkShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPassword.addActionListener(e ->
                txtPassword.setEchoChar(
                        chkShowPassword.isSelected() ? (char) 0 : '•'
                )
        );

        btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(15, 70, 150));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ================= ADD COMPONENTS =================
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);

        card.add(lblUsername);
        card.add(Box.createVerticalStrut(5));
        card.add(txtUsername);

        card.add(lblPassword);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);

        card.add(chkShowPassword);
        card.add(Box.createVerticalStrut(12));  // ✅ smaller space
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));   // ✅ reduced bottom space

        background.add(card);
        getRootPane().setDefaultButton(btnLogin);
    }

    // ================= HELPERS =================

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(50, 70, 110));
        lbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 195, 220)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}