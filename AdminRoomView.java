package edp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ReceiptView extends JFrame {

    private String billingId;
    private String patientName;
    private String treatmentId;
    private String amount;
    private String date;
    private String method;

    private static final Color PRIMARY = new Color(15, 70, 150);
    private static final Color LIGHT_BLUE = new Color(230, 236, 250);
    private static final Color DARK_GRAY = new Color(60, 60, 60);
    private static final Font UI_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font UI_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font UI_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public ReceiptView() {
        setTitle("Payment Receipt");
        setSize(380, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void setReceiptData(String billingId, String patientName, String treatmentId,
                               String amount, String date, String method) {
        this.billingId = billingId;
        this.patientName = patientName;
        this.treatmentId = treatmentId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        buildUI();
    }

    private void buildUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new LineBorder(PRIMARY, 2));

        mainPanel.add(createHeader());
        mainPanel.add(createContent());
        mainPanel.add(createFooter());

        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(25, 20, 20, 20));

        header.add(Box.createVerticalStrut(8));

        JLabel lblHospital = new JLabel("BULACAN MEDICAL MISSION GROUP");
        lblHospital.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHospital.setForeground(Color.WHITE);
        lblHospital.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblHospital);

        JLabel lblHospital2 = new JLabel("HOSPITAL AND PHARMACY");
        lblHospital2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHospital2.setForeground(Color.WHITE);
        lblHospital2.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblHospital2);

        JLabel lblAddress = new JLabel("1306 Gov Fortunato Halili Ave, Bocaue, 3018 Bulacan");
        lblAddress.setFont(UI_SMALL);
        lblAddress.setForeground(new Color(200, 210, 230));
        lblAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblAddress);

        JLabel lblTitle = new JLabel("OFFICIAL RECEIPT");
        lblTitle.setFont(UI_BOLD);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(15));
        header.add(lblTitle);

        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        content.add(createInfoRow("Billing ID:", billingId));
        content.add(Box.createVerticalStrut(10));
        content.add(createInfoRow("Patient Name:", patientName));
        content.add(Box.createVerticalStrut(10));
        content.add(createInfoRow("Treatment:", treatmentId));
        content.add(Box.createVerticalStrut(10));
        content.add(createInfoRow("Payment Date:", date));
        content.add(Box.createVerticalStrut(10));
        content.add(createInfoRow("Payment Method:", method));

        content.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 200, 200));
        content.add(sep);
        content.add(Box.createVerticalStrut(15));

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.X_AXIS));
        totalPanel.setBackground(Color.WHITE);

        JLabel lblTotal = new JLabel("TOTAL AMOUNT");
        lblTotal.setFont(UI_BOLD);
        lblTotal.setForeground(DARK_GRAY);
        totalPanel.add(lblTotal);
        totalPanel.add(Box.createHorizontalGlue());

        JLabel lblAmount = new JLabel("PHP " + amount);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAmount.setForeground(PRIMARY);
        totalPanel.add(lblAmount);

        content.add(totalPanel);

        return content;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UI_PLAIN);
        lbl.setForeground(DARK_GRAY);
        row.add(lbl);
        row.add(Box.createHorizontalGlue());

        JLabel val = new JLabel(value);
        val.setFont(UI_PLAIN);
        val.setForeground(Color.BLACK);
        row.add(val);

        return row;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(LIGHT_BLUE);
        footer.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblThanks = new JLabel("Thank you for your payment!");
        lblThanks.setFont(UI_PLAIN);
        lblThanks.setForeground(DARK_GRAY);
        lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(lblThanks);

        footer.add(Box.createVerticalStrut(5));

        JLabel lblNote = new JLabel("Please keep this receipt for your records.");
        lblNote.setFont(UI_SMALL);
        lblNote.setForeground(Color.GRAY);
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(lblNote);

        return footer;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        btnPanel.setBackground(new Color(245, 248, 255));

        // Print / Save as PDF
        JButton btnPrint = new JButton("Print / Save as PDF");
        btnPrint.setFont(UI_BOLD);
        btnPrint.setBackground(new Color(30, 140, 80));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setBorder(new EmptyBorder(10, 25, 10, 25));
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> printReceipt());

        JButton btnClose = new JButton("Close");
        btnClose.setFont(UI_BOLD);
        btnClose.setBackground(PRIMARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setBorder(new EmptyBorder(10, 25, 10, 25));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        return btnPanel;
    }

    /**
     * Opens the system print dialog. Select "Microsoft Print to PDF" (or any
     * PDF printer) to save the receipt as a PDF file.
     * Prints the live, already-rendered main panel so no off-screen layout issues.
     */
    private void printReceipt() {
        // Use the live main panel (already laid out and visible) to avoid blank prints
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(createHeader());
        mainPanel.add(createContent());
        mainPanel.add(createFooter());

        // Force a real layout pass so all child sizes are computed
        mainPanel.setPreferredSize(new Dimension(360, 600));
        mainPanel.setSize(360, 600);
        mainPanel.addNotify();        // hooks into the peer/toolkit so fonts/metrics resolve
        mainPanel.validate();         // recursively lays out all children

        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setJobName("Receipt - " + billingId);

        java.awt.print.PageFormat pf = job.defaultPage();
        pf.setOrientation(java.awt.print.PageFormat.PORTRAIT);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                 java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                                 java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            double scaleX = pageFormat.getImageableWidth()  / (double) mainPanel.getWidth();
            double scaleY = pageFormat.getImageableHeight() / (double) mainPanel.getHeight();
            double scale  = Math.min(scaleX, scaleY);
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.scale(scale, scale);
            mainPanel.printAll(g2d);  // printAll paints component + all children recursively
            return java.awt.print.Printable.PAGE_EXISTS;
        }, pf);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                    "Printing failed: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
