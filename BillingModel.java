package edp.model;

import edp.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentModel {
    private String treatmentId;
    private String patientId;
    private String treatmentDate;
    private String diagnosis;
    private String followUpDate;
    private String treatmentFee;

    public TreatmentModel() {}

    public TreatmentModel(String treatmentId, String patientId,
                          String treatmentDate, String diagnosis,
                          String followUpDate, String treatmentFee) {
        this.treatmentId   = treatmentId;
        this.patientId     = patientId;
        this.treatmentDate = treatmentDate;
        this.diagnosis     = diagnosis;
        this.followUpDate  = followUpDate;
        this.treatmentFee  = treatmentFee;
    }

    public TreatmentModel(String treatmentId, String patientId,
                          String treatmentDate, String diagnosis,
                          String followUpDate) {
        this(treatmentId, patientId, treatmentDate, diagnosis, followUpDate, null);
    }

    public String getTreatmentId()   { return treatmentId; }
    public String getPatientId()     { return patientId; }
    public String getTreatmentDate() { return treatmentDate; }
    public String getDiagnosis()     { return diagnosis; }
    public String getFollowUpDate()  { return followUpDate; }
    public String getTreatmentFee()  { return treatmentFee; }

    public void setTreatmentId(String treatmentId)    { this.treatmentId = treatmentId; }
    public void setPatientId(String patientId)        { this.patientId = patientId; }
    public void setTreatmentDate(String treatmentDate){ this.treatmentDate = treatmentDate; }
    public void setDiagnosis(String diagnosis)        { this.diagnosis = diagnosis; }
    public void setFollowUpDate(String followUpDate)  { this.followUpDate = followUpDate; }
    public void setTreatmentFee(String treatmentFee)  { this.treatmentFee = treatmentFee; }

    public static List<TreatmentModel> getAll() throws SQLException {
        List<TreatmentModel> list = new ArrayList<>();
        String sql = "SELECT TreatmentID, PatientID, TreatmentDate, Diagnosis, FollowUpDate, TreatmentFee FROM TREATMENT ORDER BY TreatmentID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TreatmentModel(
                    rs.getString("TreatmentID"),
                    "P" + rs.getString("PatientID"),
                    rs.getString("TreatmentDate"),
                    rs.getString("Diagnosis"),
                    rs.getString("FollowUpDate"),
                    rs.getString("TreatmentFee")
                ));
            }
        }
        return list;
    }

    public void add() throws SQLException {
        String sql = "INSERT INTO TREATMENT (PatientID, TreatmentDate, Diagnosis, FollowUpDate, TreatmentFee) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.replaceAll("[^0-9]", ""));
            ps.setString(2, treatmentDate);
            ps.setString(3, diagnosis);
            ps.setString(4, followUpDate);
            ps.setString(5, treatmentFee);
            ps.executeUpdate();
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE TREATMENT SET PatientID=?, TreatmentDate=?, Diagnosis=?, FollowUpDate=?, TreatmentFee=? WHERE TreatmentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId.replaceAll("[^0-9]", ""));
            ps.setString(2, treatmentDate);
            ps.setString(3, diagnosis);
            ps.setString(4, followUpDate);
            ps.setString(5, treatmentFee);
            ps.setString(6, treatmentId.replaceAll("[^0-9]", ""));
            ps.executeUpdate();
        }
    }

    public static void delete(String treatmentId) throws SQLException {
        String sql = "DELETE FROM TREATMENT WHERE TreatmentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treatmentId.replaceAll("[^0-9]", ""));
            ps.executeUpdate();
        }
    }
}
