package edp.controller;

import edp.model.PatientModel;
import edp.model.PatientModel.Patient;
import edp.model.RoomModel;
import edp.view.AdminPatientView;
import edp.view.AdminAppointmentView;
import edp.view.FrontdeskAppointmentView;
import edp.view.FrontdeskPatientView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class AdminPatientController {

    private final AdminPatientView view;
    private final PatientModel     model;
    private String selectedPatientId = null;

    public AdminPatientController(AdminPatientView view, PatientModel model) {
        this.view  = view;
        this.model = model;

        attachListeners();
        loadRoomOptions();
        view.initializeForm();
        loadPatients();
    }

    private void loadRoomOptions() {
        try {
            String[] rooms = RoomModel.getAllRoomIds();
            view.setRoomOptions(rooms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void attachListeners() {
        view.getCmbFilter().addActionListener(e -> loadPatients());
        view.getBtnAdd().addActionListener(e -> handleAdd());
        view.getBtnUpdate().addActionListener(e -> handleUpdate());
        view.getBtnDelete().addActionListener(e -> handleDelete());
        view.getBtnClear().addActionListener(e -> handleClear());
        view.getBtnRefreshList().addActionListener(e -> loadPatients());

        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) handleRowSelection();
        });
    }

    public void loadPatients() {
        String filter = (String) view.getCmbFilter().getSelectedItem();
        try {
            List<Patient> patients = model.getPatients(filter);
            view.populateTable(patients);
        } catch (SQLException ex) {
            showError("Failed to load patients:\n" + ex.getMessage());
        }
    }

    private void handleAdd() {
        if (!validateRequiredFields()) return;

        Patient p = buildPatientFromForm();
        // patientId is intentionally left empty/null here —
        // SQL AUTO_INCREMENT + "P" prefix is handled by PatientModel.addPatient()

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Add patient \"" + p.name + "\"?",
                "Confirm Add",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (model.addPatient(p)) {
                showInfo("Patient added successfully.");
                view.clearFields();
                selectedPatientId = null;
                loadPatients();
                AdminAppointmentView.refreshAllPatientLists();
                FrontdeskAppointmentView.refreshAllPatientLists();
                AdminPatientView.refreshAllPatientLists();
                FrontdeskPatientView.refreshAllPatientLists();
            } else {
                showError("No record was inserted. Please try again.");
            }
        } catch (SQLException ex) {
            showError("Error adding patient:\n" + ex.getMessage());
        }
    }

    private void handleUpdate() {
        if (selectedPatientId == null || selectedPatientId.isEmpty()) {
            showError("Please select a patient from the table to update.");
            return;
        }
        if (!validateRequiredFields()) return;

        Patient p = buildPatientFromForm();
        p.patientId = selectedPatientId;   // keep the original ID

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Update patient \"" + p.name + "\" (ID: " + p.patientId + ")?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (model.updatePatient(p)) {
                showInfo("Patient updated successfully.");
                view.clearFields();
                selectedPatientId = null;
                loadPatients();
                AdminAppointmentView.refreshAllPatientLists();
                FrontdeskAppointmentView.refreshAllPatientLists();
                AdminPatientView.refreshAllPatientLists();
                FrontdeskPatientView.refreshAllPatientLists();
            } else {
                showError("No record was updated. The Patient ID may no longer exist.");
            }
        } catch (SQLException ex) {
            showError("Error updating patient:\n" + ex.getMessage());
        }
    }

    private void handleDelete() {
        if (selectedPatientId == null || selectedPatientId.isEmpty()) {
            showError("Please select a patient from the table to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Permanently delete patient with ID \"" + selectedPatientId + "\"?\n"
              + "This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (model.deletePatient(selectedPatientId)) {
                showInfo("Patient deleted successfully.");
                view.clearFields();
                selectedPatientId = null;
                loadPatients();
                AdminAppointmentView.refreshAllPatientLists();
                FrontdeskAppointmentView.refreshAllPatientLists();
                AdminPatientView.refreshAllPatientLists();
                FrontdeskPatientView.refreshAllPatientLists();
            } else {
                showError("No record was deleted. The Patient ID may no longer exist.");
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("constraint")) {
                showError("Cannot delete: This record is linked to other data. Please remove related records first.");
            } else {
                showError("Error deleting patient:\n" + ex.getMessage());
            }
        }
    }

    private void handleRowSelection() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) return;

        selectedPatientId = view.getTable().getValueAt(row, 0).toString();

        view.setName        (view.getTable().getValueAt(row, 1).toString());
        view.setGender      (view.getTable().getValueAt(row, 2).toString());
        view.setAddress     (view.getTable().getValueAt(row, 3).toString());
        view.setContact     (view.getTable().getValueAt(row, 4).toString());
        view.setDob         (view.getTable().getValueAt(row, 5).toString());
        Object medHist = view.getTable().getValueAt(row, 6);
        view.setMedHistory  (medHist != null ? medHist.toString() : "");
        view.setPatientType (view.getTable().getValueAt(row, 7).toString());
        Object roomNum = view.getTable().getValueAt(row, 8);
        view.setRoom        (roomNum != null ? roomNum.toString() : "");
    }

    private Patient buildPatientFromForm() {
        Patient p = new Patient();
        p.name           = view.getName();
        p.gender         = view.getGender();
        p.address        = view.getAddress();
        p.contactNumber  = view.getContact();
        p.dateOfBirth    = view.getDob();
        p.medicalHistory = view.getMedHistory();
        p.patientType    = view.getPatientType();
        p.roomId         = view.getRoom();
        return p;
    }

    private boolean validateRequiredFields() {
    if (view.getName().isEmpty()) {
        showError("Name is required.");
        return false;
    }
    
    
    if (!view.getName().matches("^[A-Za-z ]+$")) {
        showError("Name must contain letters only.");
        return false;
    }


    if (view.getGender() == null || view.getGender().isEmpty()) {
        showError("Gender is required.");
        return false;
    }
    
    if (!view.getContact().matches("\\d{11}")) {
        showError("Contact number must contain exactly 11 digits.");
        return false;
    }

    if (view.getPatientType() == null || view.getPatientType().isEmpty()) {
        showError("Patient Type is required (Inpatient or Outpatient).");
        return false;
    }

    return true;
}

    public void handleClear() {
        view.clearFields();
        selectedPatientId = null;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}