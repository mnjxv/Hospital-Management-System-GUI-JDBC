package edp.controller;

import edp.model.DoctorModel;
import edp.model.DoctorModel.Doctor;
import edp.view.AdminDoctorView;
import edp.view.AdminAppointmentView;
import edp.view.FrontdeskAppointmentView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class AdminDoctorController {

    public final AdminDoctorView view;
    public final DoctorModel model;
    public int selectedDoctorId = -1;

    public AdminDoctorController(AdminDoctorView view, DoctorModel model) {
        this.view = view;
        this.model = model;

        attachListeners();
        loadDoctors();
    }

    public void attachListeners() {
        view.getBtnAdd().addActionListener(e -> handleAdd());
        view.getBtnUpdate().addActionListener(e -> handleUpdate());
        view.getBtnDelete().addActionListener(e -> handleDelete());
        view.getBtnClear().addActionListener(e -> handleClear());
        view.getBtnRefreshList().addActionListener(e -> loadDoctors());
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) handleRowSelection();
        });
    }

    public void loadDoctors() {
        try {
            List<Doctor> doctors = model.getAllDoctors();
            view.populateTable(doctors);
        } catch (SQLException ex) {
            showError("Failed to load doctors:\n" + ex.getMessage());
        }
    }

    public void handleAdd() {
        if (!validateFields()) return;

        try {
            if (model.isLicenseNumberExists(view.getLicense())) {
                showError("License number already exists!");
                return;
            }
        } catch (SQLException ex) {
            showError("Error checking license number:\n" + ex.getMessage());
            return;
        }

        Doctor newDoctor = new Doctor(
                view.getLicense(),
                view.getName(),
                view.getDepartment(),
                view.getSchedule(),
                view.getContact()
        );

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Add doctor \"" + newDoctor.name + "\"?",
                "Confirm Add",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int generatedId = model.addDoctor(newDoctor);
            showInfo(generatedId > 0
                    ? "Doctor added! ID: D" + generatedId
                    : "Doctor added successfully.");
            handleClear();
            loadDoctors();
            AdminAppointmentView.refreshAllDoctorLists();
            FrontdeskAppointmentView.refreshAllDoctorLists();
        } catch (SQLException ex) {
            showError("Error adding doctor:\n" + ex.getMessage());
        }
    }

    public void handleUpdate() {
        if (selectedDoctorId == -1) {
            showError("Please select a doctor from the table.");
            return;
        }
        if (!validateFields()) return;

        Doctor updated = new Doctor(
                selectedDoctorId,
                view.getLicense(),
                view.getName(),
                view.getDepartment(),
                view.getSchedule(),
                view.getContact()
        );

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Update doctor \"" + updated.name + "\" (ID: D" + updated.doctorId + ")?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (model.updateDoctor(updated)) showInfo("Doctor updated successfully.");
            else showError("No record updated.");
            handleClear();
            loadDoctors();
            AdminAppointmentView.refreshAllDoctorLists();
            FrontdeskAppointmentView.refreshAllDoctorLists();
        } catch (SQLException ex) {
            showError("Error updating doctor:\n" + ex.getMessage());
        }
    }

    public void handleDelete() {
        if (selectedDoctorId == -1) {
            showError("Please select a doctor to delete.");
            return;
        }

        String doctorName = view.getName().isEmpty()
                ? "D" + selectedDoctorId
                : view.getName();

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Permanently delete doctor \"" + doctorName + "\" (ID: D" + selectedDoctorId + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (model.deleteDoctor(selectedDoctorId)) showInfo("Doctor deleted successfully.");
            else showError("No record deleted.");
            handleClear();
            loadDoctors();
            AdminAppointmentView.refreshAllDoctorLists();
            FrontdeskAppointmentView.refreshAllDoctorLists();
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("constraint")) {
                showError("Cannot delete: This record is linked to other data. Please remove related records first.");
            } else {
                showError("Error deleting doctor:\n" + ex.getMessage());
            }
        }
    }

    public void handleRowSelection() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) { selectedDoctorId = -1; return; }

        Object idObj = view.getTable().getValueAt(row, 0);
        String idStr = idObj != null ? idObj.toString().replaceAll("(?i)^D", "") : "-1";
        try {
            selectedDoctorId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            selectedDoctorId = -1;
        }

        view.setLicense(nullSafe(view.getTable().getValueAt(row, 1)));
        view.setName(nullSafe(view.getTable().getValueAt(row, 2)));
        view.setDepartment(nullSafe(view.getTable().getValueAt(row, 3)));
        view.setSchedule(nullSafe(view.getTable().getValueAt(row, 4)));
        view.setContact(nullSafe(view.getTable().getValueAt(row, 5)));
    }

    public void handleClear() {
        view.clearFields();
        selectedDoctorId = -1;
    }

    public boolean validateFields() {
        String license = view.getLicense();
        String contact = view.getContact();

        if (license.isEmpty()) {
            showError("License is required.");
            return false;
        }
        if (view.getLicense().isEmpty()) { 
            showError("License is required.");    
        return false; }

        if (view.getName().isEmpty()) {
            showError("Name is required.");
            return false;
        }
        
         if (!view.getName().matches("^[A-Za-z ]+$")) {
        showError("Name must contain letters only.");
        return false;
    }
        
        if (view.getDepartment().isEmpty()) {
            showError("Department is required.");
            return false;
        }
        if (contact.isEmpty()) {
            showError("Contact Number is required.");
            return false;
        }
        if (!contact.matches("\\d{11}")) {
            showError("Contact number must contain exactly 11 digits.");
            return false;
        }
        return true;
    }

    public String nullSafe(Object value) {
        return value != null ? value.toString() : "";
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}