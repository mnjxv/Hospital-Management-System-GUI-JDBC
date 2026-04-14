package edp.controller;

import edp.model.DoctorModel;
import edp.model.DoctorModel.Doctor;
import edp.view.FrontdeskDoctorView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class FrontdeskDoctorController {

    private final FrontdeskDoctorView view;
    private final DoctorModel model;

    public FrontdeskDoctorController(FrontdeskDoctorView view, DoctorModel model) {
        this.view = view;
        this.model = model;
        attachListeners();
        loadDoctors();
    }

    private void attachListeners() {
        view.getBtnRefresh().addActionListener(e -> loadDoctors());
    }

    public void loadDoctors() {
        try {
            List<Doctor> doctors = model.getAllDoctors();
            view.populateTable(doctors);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                "Failed to load doctors:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}