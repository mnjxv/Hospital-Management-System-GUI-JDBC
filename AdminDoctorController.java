package edp.controller;

import edp.database.DBConnection;
import edp.model.TreatmentModel;
import edp.view.AdminTreatmentView;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminTreatmentController {

    private final AdminTreatmentView view;

    public AdminTreatmentController(AdminTreatmentView view) {
        this.view = view;
        view.refreshPatientCombo();
        attachListeners();
        loadTreatments();
    }

    private void attachListeners() {
        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFieldsFromSelectedRow();
        });
        view.btnAdd.addActionListener(e    -> addTreatment());
        view.btnUpdate.addActionListener(e -> updateTreatment());
        view.btnDelete.addActionListener(e -> deleteTreatment());
        view.btnClear.addActionListener(e  -> view.clearFields());
        view.btnRefreshPatientList.addActionListener(e -> view.refreshPatientCombo());
    }

    public void loadTreatments() {
        List<TreatmentModel> list = new ArrayList<>();
        String sql = "SELECT * FROM TREATMENT";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TreatmentModel(
                    "T" + rs.getString("TreatmentID"),
                    "P" + rs.getString("PatientID"),
                    rs.getDate("TreatmentDate") != null
                        ? rs.getDate("TreatmentDate").toString() : "",
                    rs.getString("Diagnosis"),
                    rs.getDate("FollowUpDate") != null
                        ? rs.getDate("FollowUpDate").toString() : "",
                    rs.getBigDecimal("TreatmentFee") != null
                        ? rs.getBigDecimal("TreatmentFee").toString() : ""
                ));
            }
            view.populateTable(list);
        } catch (SQLException ex) {
            view.showError("Error loading treatments: " + ex.getMessage());
        }
    }

    private void addTreatment() {
        TreatmentModel t = getFieldValues();
        if (t == null) return;
        
        String patientIdForDb = t.getPatientId().replaceAll("[^0-9]", "");
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM PATIENT WHERE PatientID = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, patientIdForDb);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    view.showError("Patient ID does not exist. Please select a valid patient.");
                    return;
                }
            }
        } catch (SQLException ex) {
            view.showError("Error checking patient: " + ex.getMessage());
            return;
        }
        
        String sql = "INSERT INTO TREATMENT (PatientID, TreatmentDate, " +
                     "Diagnosis, FollowUpDate, TreatmentFee) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientIdForDb);
            ps.setDate(2, Date.valueOf(t.getTreatmentDate()));
            ps.setString(3, t.getDiagnosis());
            ps.setDate(4, t.getFollowUpDate() != null && !t.getFollowUpDate().isEmpty()
                ? Date.valueOf(t.getFollowUpDate()) : null);
            ps.setBigDecimal(5, t.getTreatmentFee() != null && !t.getTreatmentFee().isEmpty()
                ? new BigDecimal(t.getTreatmentFee()) : null);
            ps.executeUpdate();
            view.showMessage("Treatment added successfully.");
            view.clearFields();
            loadTreatments();
        } catch (SQLException ex) {
            view.showError("Error adding treatment: " + ex.getMessage());
        }
    }

    private void updateTreatment() {
        int row = view.table.getSelectedRow();
        if (row < 0) { view.showError("Select a row to update."); return; }
        String idStr = view.model.getValueAt(row, 0).toString().replaceAll("[^0-9]", "");
        TreatmentModel t = getFieldValues();
        if (t == null) return;
        String sql = "UPDATE TREATMENT SET PatientID=?, TreatmentDate=?, Diagnosis=?, " +
                     "FollowUpDate=?, TreatmentFee=? WHERE TreatmentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String patientIdForDb = t.getPatientId().replaceAll("[^0-9]", "");
            ps.setString(1, patientIdForDb);
            ps.setDate(2, Date.valueOf(t.getTreatmentDate()));
            ps.setString(3, t.getDiagnosis());
            ps.setDate(4, t.getFollowUpDate() != null && !t.getFollowUpDate().isEmpty()
                ? Date.valueOf(t.getFollowUpDate()) : null);
            ps.setBigDecimal(5, t.getTreatmentFee() != null && !t.getTreatmentFee().isEmpty()
                ? new BigDecimal(t.getTreatmentFee()) : null);
            ps.setString(6, idStr);
            ps.executeUpdate();
            view.showMessage("Treatment updated successfully.");
            view.clearFields();
            loadTreatments();
        } catch (SQLException ex) {
            view.showError("Error updating treatment: " + ex.getMessage());
        }
    }

    private void deleteTreatment() {
        int row = view.table.getSelectedRow();
        if (row < 0) { view.showError("Select a row to delete."); return; }
        String formattedId = view.model.getValueAt(row, 0).toString();
        int id = Integer.parseInt(formattedId.replaceAll("[^0-9]", ""));
        int confirm = JOptionPane.showConfirmDialog(view,
            "Delete treatment ID " + formattedId + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM TREATMENT WHERE TreatmentID=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            view.showMessage("Treatment deleted successfully.");
            view.clearFields();
            loadTreatments();
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("constraint")) {
                view.showError("Cannot delete: This record is linked to other data. Please remove related records first.");
            } else {
                view.showError("Error deleting treatment: " + ex.getMessage());
            }
        }
    }

    private void fillFieldsFromSelectedRow() {
        int row = view.table.getSelectedRow();
        if (row < 0) return;
        
        String patientIdInTable = view.model.getValueAt(row, 1).toString().replaceAll("[^0-9]", "");
        
        // Ensure combo has the item
        boolean found = false;
        for (int i = 0; i < view.cboPatientId.getItemCount(); i++) {
            if (view.cboPatientId.getItemAt(i).equals(patientIdInTable)) {
                found = true;
                break;
            }
        }
        if (!found && !patientIdInTable.isEmpty()) {
            view.cboPatientId.addItem(patientIdInTable);
        }
        view.cboPatientId.setSelectedItem(patientIdInTable);
        
        view.txtDiagnosis.setText(view.model.getValueAt(row, 3).toString());
        view.txtTreatmentFee.setText(
            view.model.getValueAt(row, 5) != null
                ? view.model.getValueAt(row, 5).toString() : "");
        view.setTreatmentDate(view.model.getValueAt(row, 2).toString());
        view.setFollowUpDate(
            view.model.getValueAt(row, 4) != null
                ? view.model.getValueAt(row, 4).toString() : "");
    }

    private TreatmentModel getFieldValues() {
        String pid  = view.cboPatientId.getSelectedItem() != null
                        ? view.cboPatientId.getSelectedItem().toString() : "";
        String diag = view.txtDiagnosis.getText().trim();
        String fee  = view.txtTreatmentFee.getText().trim();
        if (pid.isEmpty() || diag.isEmpty()) {
            view.showError("Patient ID and Diagnosis are required.");
            return null;
        }
        
        if (!fee.isEmpty() && !fee.matches("\\d+")) {
            view.showError("Treatment fee must be a valid number.");
            return null;
        }

        return new TreatmentModel(null, pid,
            view.getTreatmentDate(), diag, view.getFollowUpDate(), fee);
    }
}
