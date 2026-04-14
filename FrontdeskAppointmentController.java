package edp.controller;

import edp.database.DBConnection;
import edp.model.TreatmentModel;
import edp.view.FrontdeskTreatmentView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FrontdeskTreatmentController {

    private final FrontdeskTreatmentView view;

    public FrontdeskTreatmentController(FrontdeskTreatmentView view) {
        this.view = view;
        attachListeners();
        loadTreatments();
    }

    private void attachListeners() {
        view.btnRefreshTable.addActionListener(e -> loadTreatments());
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
            ex.printStackTrace();
            view.showError("Error loading treatments: " + ex.getMessage());
        }
    }
}
