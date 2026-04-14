package edp.model;

import edp.database.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentModel {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String dateAndTime;
    private String purpose;
    private String status;

    public AppointmentModel() {}
    public AppointmentModel(String appointmentId, String patientId, String doctorId,
                            String dateAndTime, String purpose, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateAndTime = dateAndTime;
        this.purpose = purpose;
        this.status = status;
    }

    public String getAppointmentId()      { return appointmentId; }
    public String getPatientId()          { return patientId; }
    public String getDoctorId()           { return doctorId; }
    public String getDateAndTime()        { return dateAndTime; }
    public String getPurpose()            { return purpose; }
    public String getStatus()             { return status; }

    public String getFormattedPatientId() { 
        return "P" + patientId;
    }
    public String getFormattedDoctorId()  { 
        return "D" + doctorId;
    }

    public String getFormattedAppointmentId() {
        try {
            int num = Integer.parseInt(appointmentId.trim());
            return String.format("A%03d", num);
        } catch (NumberFormatException e) {
            return appointmentId;
        }
    }

    public static int parseFormattedAppointmentId(String formatted) {
        return Integer.parseInt(formatted.replaceAll("[^0-9]", ""));
    }

    public static List<AppointmentModel> getAll() {
        List<AppointmentModel> list = new ArrayList<>();
        String sql = "SELECT AppointmentID, PatientID, DoctorID, DateAndTime, Purpose, AStatus FROM APPOINTMENT";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AppointmentModel(
                    rs.getString("AppointmentID"),
                    rs.getString("PatientID"),
                    rs.getString("DoctorID"),
                    rs.getString("DateAndTime"),
                    rs.getString("Purpose"),
                    rs.getString("AStatus")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error loading appointments:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    public void add() throws Exception {
        // Check if PatientID exists in database
        String patientIdNum = patientId.replaceAll("[^0-9]", "");
        String checkSql = "SELECT COUNT(*) FROM PATIENT WHERE PatientID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, patientIdNum);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new Exception("Patient ID not found.");
            }
        }
        
        // Check if DoctorID exists in database
        String doctorIdNum = doctorId.replaceAll("[^0-9]", "");
        String checkDocSql = "SELECT COUNT(*) FROM DOCTOR WHERE DoctorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkDocSql)) {
            checkPs.setString(1, doctorIdNum);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new Exception("Doctor ID not found.");
            }
        }
        
        String sql = "INSERT INTO APPOINTMENT (PatientID, DoctorID, DateAndTime, Purpose, AStatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientIdNum);
            ps.setString(2, doctorIdNum);
            ps.setString(3, dateAndTime);
            ps.setString(4, purpose);
            ps.setString(5, status);
            ps.executeUpdate();
        }
    }

    public void update() throws Exception {
        // Check if PatientID exists in database
        String patientIdNum = patientId.replaceAll("[^0-9]", "");
        String checkSql = "SELECT COUNT(*) FROM PATIENT WHERE PatientID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, patientIdNum);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new Exception("Patient ID not found.");
            }
        }
        
        // Check if DoctorID exists in database
        String doctorIdNum = doctorId.replaceAll("[^0-9]", "");
        String checkDocSql = "SELECT COUNT(*) FROM DOCTOR WHERE DoctorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkDocSql)) {
            checkPs.setString(1, doctorIdNum);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new Exception("Doctor ID not found.");
            }
        }
        
        String sql = "UPDATE APPOINTMENT SET PatientID=?, DoctorID=?, DateAndTime=?, Purpose=?, AStatus=? WHERE AppointmentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientIdNum);
            ps.setString(2, doctorIdNum);
            ps.setString(3, dateAndTime);
            ps.setString(4, purpose);
            ps.setString(5, status);
            ps.setString(6, appointmentId);
            ps.executeUpdate();
        }
    }

    public static void delete(String appointmentId) throws Exception {
        String sql = "DELETE FROM APPOINTMENT WHERE AppointmentID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentId);
            ps.executeUpdate();
        }
    }

    public static List<String> getAllPatientIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT PatientID FROM PATIENT ORDER BY PatientID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
             while (rs.next()) {
                 ids.add(rs.getString("PatientID"));
             }
        }
        return ids;
    }

    public static List<String> getAllDoctorIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT DoctorID FROM DOCTOR ORDER BY DoctorID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getString("DoctorID"));
            }
        }
        return ids;
    }
}