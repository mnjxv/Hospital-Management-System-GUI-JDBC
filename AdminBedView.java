package edp.view;

import edp.model.HomeModel;
import edp.model.HomeModel.DashboardStats;
import java.awt.*;
import javax.swing.*;

public class AdminHomepage extends JPanel {
    private JLabel[] countLabels = new JLabel[7];

    private final String[] titles = {
        "Total Patients", "Total Doctors", "Appointments", "Available Beds", "Occupied Beds", "Pending Billing", "Total Rooms"
    };

    private final Color[] colors = {
        new Color(15, 90, 180),
        new Color(30, 140, 80),
        new Color(160, 100, 0),
        new Color(30, 120, 160),
        new Color(190, 50, 50),
        new Color(130, 40, 140),
        new Color(60, 100, 180)
    };

    public AdminHomepage(String adminName) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(245, 248, 255));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 10, 30));

        JLabel welcomeLabel = new JLabel("Welcome back, " + adminName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(15, 70, 150));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        JLabel subLabel = new JLabel("Here's a quick overview of the hospital today.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(100, 120, 160));
        subLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        welcomePanel.add(subLabel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(new Color(15, 70, 150));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshCounts());

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRight.setOpaque(false);
        topRight.add(refreshBtn);
        welcomePanel.add(topRight, BorderLayout.EAST);

        add(welcomePanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 248, 255));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JPanel topRow = new JPanel(new GridLayout(1, 4, 18, 0));
        topRow.setBackground(new Color(245, 248, 255));
        for (int i = 0; i < 4; i++) {
            topRow.add(createStatCard(i));
        }

        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 18, 0));
        bottomRow.setBackground(new Color(245, 248, 255));
        bottomRow.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        for (int i = 4; i < 7; i++) {
            bottomRow.add(createStatCard(i));
        }

        wrapper.add(topRow, BorderLayout.NORTH);
        wrapper.add(bottomRow, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createStatCard(int index) {
        Color cardColor = colors[index];

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        card.setPreferredSize(new Dimension(0, 160));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel countLabel = new JLabel(getStatValue(index), SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        countLabel.setForeground(Color.WHITE);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 80));
        sep.setMaximumSize(new Dimension(80, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(titles[index], SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(220, 235, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        inner.add(countLabel);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sep);
        inner.add(titleLabel);

        card.add(inner);
        countLabels[index] = countLabel;

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2, true),
                    BorderFactory.createEmptyBorder(14, 10, 14, 10)
                ));
                card.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
                card.repaint();
            }
        });

        return card;
    }

    private void refreshCounts() {
        DashboardStats stats = HomeModel.getDashboardStats();
        int[] values = {
            stats.totalPatients, stats.totalDoctors, stats.totalAppointments,
            stats.availableBeds, stats.occupiedBeds, stats.pendingBilling, stats.totalRooms
        };
        for (int i = 0; i < values.length; i++) {
            if (countLabels[i] != null) {
                countLabels[i].setText(String.valueOf(values[i]));
            }
        }
        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, "Dashboard refreshed successfully.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getStatValue(int index) {
        DashboardStats stats = HomeModel.getDashboardStats();
        switch (index) {
            case 0: return String.valueOf(stats.totalPatients);
            case 1: return String.valueOf(stats.totalDoctors);
            case 2: return String.valueOf(stats.totalAppointments);
            case 3: return String.valueOf(stats.availableBeds);
            case 4: return String.valueOf(stats.occupiedBeds);
            case 5: return String.valueOf(stats.pendingBilling);
            case 6: return String.valueOf(stats.totalRooms);
            default: return "—";
        }
    }
}