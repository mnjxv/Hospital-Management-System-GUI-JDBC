package edp.view;

import javax.swing.*;
import java.awt.*;

public class AdminLoginView extends JFrame {

    public JButton btnLogin;
    public JTextField txtUsername;
    public JPasswordField txtPassword;
    public JCheckBox chkShowPassword;

    public AdminLoginView() {
        setTitle("Hospital Management System - Admin Login");
        setSize(480, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(10, 50, 120),
                    0, getHeight(), new Color(20, 80, 160)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));
        card.setPreferredSize(new Dimension(380, 420));

        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("🏥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Admin Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(15, 60, 130));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Hospital Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 120, 160));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        iconPanel.add(iconLabel);
        iconPanel.add(Box.createVerticalStrut(12));
        iconPanel.add(titleLabel);
        iconPanel.add(Box.createVerticalStrut(6));
        iconPanel.add(subtitleLabel);
        iconPanel.add(Box.createVerticalStrut(30));

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLbl.setForeground(new Color(50, 70, 110));
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsername.setPreferredSize(new Dimension(280, 42));

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLbl.setForeground(new Color(50, 70, 110));
        passLbl.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel passPanel = new JPanel();
        passPanel.setOpaque(false);
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.X_AXIS));
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 220), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        chkShowPassword = new JCheckBox("Show");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkShowPassword.setForeground(new Color(100, 120, 160));
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•');
        });

        JPanel showPassPanel = new JPanel();
        showPassPanel.setOpaque(false);
        showPassPanel.setLayout(new BoxLayout(showPassPanel, BoxLayout.Y_AXIS));
        showPassPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        showPassPanel.add(txtPassword);
        showPassPanel.add(Box.createVerticalStrut(2));
        showPassPanel.add(chkShowPassword);

        formPanel.add(userLbl);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(txtUsername);
        formPanel.add(passLbl);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(showPassPanel);

        card.add(iconPanel);
        card.add(formPanel);

        btnLogin = createStyledButton("Sign In", new Color(15, 70, 150));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        btnPanel.add(btnLogin);


        card.add(btnPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(card, gbc);

        getRootPane().setDefaultButton(btnLogin);
        txtUsername.requestFocusInWindow();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? color.darker() : 
                           getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 48));
        return btn;
    }
}
