package edp.controller;

import edp.model.AppointmentModel;
import edp.view.AdminAppointmentView;

import javax.swing.*;

public class AdminAppointmentController {

    private final AdminAppointmentView view;

    public AdminAppointmentController(AdminAppointmentView view) {
        this.view = view;
        view.loadPatientIds();
        view.loadDoctorIds();
        initActions();
        loadAppointments();
    }

    private void initActions() {

        view.btnRefreshLists.addActionListener(e -> {
            view.loadPatientIds();
            view.loadDoctorIds();
            JOptionPane.showMessageDialog(view, "Patient & Doctor lists refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        view.btnAdd.addActionListener(e -> {
            if (view.txtPurpose.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Purpose is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String patientId = (String) view.cmbPatientId.getSelectedItem();
                String doctorId  = (String) view.cmbDoctorId.getSelectedItem();
                if (patientId == null || patientId.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Please select a Patient ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (doctorId == null || doctorId.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Please select a Doctor ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                AppointmentModel a = new AppointmentModel(
                        null, patientId, doctorId,
                        view.getDateAndTime(),
                        view.txtPurpose.getText().trim(),
                        (String) view.cmbStatus.getSelectedItem()
                );
                a.add();
                loadAppointments();
                clearFields();
                JOptionPane.showMessageDialog(view, "Appointment added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, ex.getMessage());
            }
        });

        view.btnUpdate.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Select an appointment to update.");
                return;
            }
            try {
                String formatted  = view.model.getValueAt(row, 0).toString();
                String numericId  = String.valueOf(
                        AppointmentModel.parseFormattedAppointmentId(formatted));
                String patientId = (String) view.cmbPatientId.getSelectedItem();
                String doctorId  = (String) view.cmbDoctorId.getSelectedItem();
                AppointmentModel a = new AppointmentModel(
                        numericId, patientId, doctorId,
                        view.getDateAndTime(),
                        view.txtPurpose.getText().trim(),
                        (String) view.cmbStatus.getSelectedItem()
                );
                a.update();
                loadAppointments();
                clearFields();
                JOptionPane.showMessageDialog(view, "Appointment updated successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, ex.getMessage());
            }
        });

        view.btnDelete.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) return;
            try {
                String formatted = view.model.getValueAt(row, 0).toString();
                String numericId = String.valueOf(
                        AppointmentModel.parseFormattedAppointmentId(formatted));
                AppointmentModel.delete(numericId);
                loadAppointments();
                clearFields();
                JOptionPane.showMessageDialog(view, "Appointment deleted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, ex.getMessage());
            }
        });

        view.btnClear.addActionListener(e -> clearFields());

        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.table.getSelectedRow();
            if (row != -1) {
                String patientIdInTable = view.model.getValueAt(row, 1).toString().replaceAll("[^0-9]", "");
                String doctorIdInTable = view.model.getValueAt(row, 2).toString().replaceAll("[^0-9]", "");
                
                if (view.cmbPatientId.getItemCount() > 0) {
                    view.cmbPatientId.setSelectedItem(patientIdInTable);
                }
                if (view.cmbDoctorId.getItemCount() > 0) {
                    view.cmbDoctorId.setSelectedItem(doctorIdInTable);
                }
                view.setDateAndTime(view.model.getValueAt(row, 3).toString());
                view.txtPurpose.setText(view.model.getValueAt(row, 4).toString());
                view.cmbStatus.setSelectedItem(view.model.getValueAt(row, 5).toString());
            }
        });
    }

    private void loadAppointments() {
        try {
            view.model.setRowCount(0);
            for (AppointmentModel a : AppointmentModel.getAll()) {
                view.model.addRow(new Object[]{
                        a.getFormattedAppointmentId(),
                        a.getFormattedPatientId(),
                        a.getFormattedDoctorId(),
                        a.getDateAndTime(),
                        a.getPurpose(),
                        a.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage());
        }
    }

    private void clearFields() {
        if (view.cmbPatientId.getItemCount() > 0) view.cmbPatientId.setSelectedIndex(0);
        if (view.cmbDoctorId.getItemCount()  > 0) view.cmbDoctorId.setSelectedIndex(0);
        view.txtPurpose.setText("");
        view.resetDateAndTime();
        view.cmbStatus.setSelectedIndex(0);
        view.table.clearSelection();
    }
}