package edp.controller;

import edp.database.DBConnection;
import edp.view.ReceiptView;
import edp.view.AdminBillingView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReceiptController {

    private final AdminBillingView view;

    public ReceiptController(AdminBillingView view) {
        this.view = view;
    }

    public void showReceipt() {
        int row = view.table.getSelectedRow();
        if (row < 0) {
            view.showError("Please select a billing record to print receipt.");
            return;
        }

        String billingId = view.model.getValueAt(row, 0).toString();
        String treatmentId = view.model.getValueAt(row, 1).toString();
        String amountPaid = view.model.getValueAt(row, 3).toString();
        String paymentDate = view.model.getValueAt(row, 5).toString();
        String paymentMethod = view.model.getValueAt(row, 6).toString();

        String patientName = getPatientName(treatmentId.replace("T", ""));

        ReceiptView receiptView = new ReceiptView();
        receiptView.setReceiptData(
            billingId,
            patientName,
            treatmentId,
            "Php " + amountPaid,
            paymentDate,
            paymentMethod
        );
        receiptView.setVisible(true);
    }

    private String getPatientName(String treatmentId) {
        String name = "N/A";
        String sql = "SELECT p.PatientName FROM TREATMENT t JOIN PATIENT p ON t.PatientID = p.PatientID WHERE t.TreatmentID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(treatmentId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("PatientName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
