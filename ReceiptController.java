package edp.controller;

import edp.model.PatientModel;
import edp.model.PatientModel.Patient;
import edp.model.RoomModel;
import edp.view.FrontdeskPatientView;
import edp.view.AdminAppointmentView;
import edp.view.FrontdeskAppointmentView;
import javax.swing.event.ListSelectionEvent;
import java.sql.SQLException;
import java.util.List;

public class FrontdeskPatientController {

    private final FrontdeskPatientView patientView;
    private final PatientModel model;

    public FrontdeskPatientController(FrontdeskPatientView view, PatientModel model) {
        this.patientView = view;
        this.model = model;
        attachListeners();
        loadRoomOptions();
        patientView.initializeForm();
        loadPatients();
    }

    private void loadRoomOptions() {
        try {
            String[] rooms = RoomModel.getAllRoomIds();
            patientView.setRoomOptions(rooms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ───────────────────────── Listeners ─────────────────────────
    private void attachListeners() {
        patientView.getCmbFilter().addActionListener(e -> loadPatients());
        patientView.getBtnAdd().addActionListener(e -> addPatient());
        patientView.getBtnUpdate().addActionListener(e -> updatePatient());
        patientView.getBtnClear().addActionListener(e -> patientView.clearFields());
        patientView.getBtnRefreshList().addActionListener(e -> loadPatients());

        patientView.getTable().getSelectionModel()
                .addListSelectionListener((ListSelectionEvent e) -> {
                    if (!e.getValueIsAdjusting()) populateFormFromTable();
                });
    }

    // ───────────────────────── Load Patients ─────────────────────────
    private void loadPatients() {
        try {
            String filter = (String) patientView.getCmbFilter().getSelectedItem();
            List<Patient> patients = model.getPatients(filter);
            patientView.populateTable(patients);
        } catch (SQLException ex) {
            patientView.showError("Failed to load patients:\n" + ex.getMessage());
        }
    }

    // ───────────────────────── Populate Form ─────────────────────────
    private void populateFormFromTable() {
        int row = patientView.getTable().getSelectedRow();
        if (row == -1) return;

        int modelRow = patientView.getTable().convertRowIndexToModel(row);
        javax.swing.table.DefaultTableModel tm =
                (javax.swing.table.DefaultTableModel) patientView.getTable().getModel();

        // Get values first
        String patientType = (String) tm.getValueAt(modelRow, 7);
        Object roomVal = tm.getValueAt(modelRow, 8);
        String roomStr = roomVal != null ? roomVal.toString() : "";

        // Set room BEFORE patient type (so visibility is correct)
        patientView.setRoom(roomStr);
        // Then set patient type
        patientView.setPatientType(patientType);
        
        patientView.setPatientName((String) tm.getValueAt(modelRow, 1));
        patientView.setGender((String) tm.getValueAt(modelRow, 2));
        patientView.setAddress((String) tm.getValueAt(modelRow, 3));
        patientView.setContact((String) tm.getValueAt(modelRow, 4));
        patientView.setDob((String) tm.getValueAt(modelRow, 5));
        patientView.setMedHistory((String) tm.getValueAt(modelRow, 6));
    }

    // ───────────────────────── Add Patient ─────────────────────────
    private void addPatient() {
        if (!validateNameAndContact()) return;

        Patient p = buildPatientFromForm();
        try {
            if (model.addPatient(p)) {
                patientView.showSuccess("Patient added successfully!");
                loadPatients();
                patientView.clearFields();
                AdminAppointmentView.refreshAllPatientLists();
                FrontdeskAppointmentView.refreshAllPatientLists();
            } else {
                patientView.showError("Patient could not be added.");
            }
        } catch (SQLException ex) {
            patientView.showError("Add failed:\n" + ex.getMessage());
        }
    }

    // ───────────────────────── Update Patient ─────────────────────────
    private void updatePatient() {
        String id = patientView.getSelectedPatientId();
        if (id == null) {
            patientView.showWarning("Please select a patient to update.");
            return;
        }

        if (!validateNameAndContact()) return;

        Patient p = buildPatientFromForm(id);
        try {
            if (model.updatePatient(p)) {
                patientView.showSuccess("Patient updated successfully!");
                loadPatients();
                patientView.clearFields();
                AdminAppointmentView.refreshAllPatientLists();
                FrontdeskAppointmentView.refreshAllPatientLists();
            } else {
                patientView.showError("Patient could not be updated.");
            }
        } catch (SQLException ex) {
            patientView.showError("Update failed:\n" + ex.getMessage());
        }
    }

    // ───────────────────────── Validation ─────────────────────────
    private boolean validateNameAndContact() {
        String name = patientView.getPatientName();
        String contact = patientView.getContact();

        if (name.isEmpty()) {
            patientView.showWarning("Patient name is required.");
            return false;
        }

        if (!name.matches("^[A-Za-z ]+$")) {
            patientView.showWarning("Name must contain letters only.");
            return false;
        }

        if (!contact.matches("\\d{11}")) {
            patientView.showWarning("Contact number must contain exactly 11 digits.");
            return false;
        }

        return true;
    }

    // ───────────────────────── Build Patient Object ─────────────────────────
    private Patient buildPatientFromForm() {
        Patient p = new Patient();
        p.name = patientView.getPatientName();
        p.gender = patientView.getGender();
        p.address = patientView.getAddress();
        p.contactNumber = patientView.getContact();
        p.dateOfBirth = patientView.getDob();
        p.medicalHistory = patientView.getMedHistory();
        p.patientType = patientView.getPatientType();
        p.roomId = patientView.getRoom();
        return p;
    }

    private Patient buildPatientFromForm(String id) {
        Patient p = new Patient();
        p.patientId = id;
        p.name = patientView.getPatientName();
        p.gender = patientView.getGender();
        p.address = patientView.getAddress();
        p.contactNumber = patientView.getContact();
        p.dateOfBirth = patientView.getDob();
        p.medicalHistory = patientView.getMedHistory();
        p.patientType = patientView.getPatientType();
        p.roomId = patientView.getRoom();
        return p;
    }
}