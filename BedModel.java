package edp.model;

import edp.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorModel {

    public static class Doctor {
        public int doctorId;
        public String licenseNumber;
        public String name;
        public String department;
        public String schedule;
        public String contactNumber;

        public Doctor(String licenseNumber, String name, String department,
                      String schedule, String contactNumber) {
            this.doctorId = -1;
            this.licenseNumber = licenseNumber;
            this.name = name;
            this.department = department;
            this.schedule = schedule;
            this.contactNumber = contactNumber;
        }

        public Doctor(int doctorId, String licenseNumber, String name,
                      String department, String schedule, String contactNumber) {
            this.doctorId = doctorId;
            this.licenseNumber = licenseNumber;
            this.name = name;
            this.department = department;
            this.schedule = schedule;
            this.contactNumber = contactNumber;
        }

        /**
         * Returns a row for the table.
         * Column 0 is formatted as "D1", "D2", etc. so the display shows a
         * human-friendly ID while the real integer is still stored in doctorId.
         */
        public Object[] toRow() {
            String displayId = (doctorId > 0) ? "D" + doctorId : "-";
            return new Object[]{displayId, licenseNumber, name, department, schedule, contactNumber};
        }
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT DoctorID, LicenseNumber, DoctorName, Department, AvailableSchedule, ContactNumber " +
                     "FROM DOCTOR ORDER BY DoctorID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO DOCTOR (LicenseNumber, DoctorName, Department, AvailableSchedule, ContactNumber) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doctor.licenseNumber);
            ps.setString(2, doctor.name);
            ps.setString(3, doctor.department);
            ps.setString(4, doctor.schedule);
            ps.setString(5, doctor.contactNumber);

            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE DOCTOR SET LicenseNumber=?, DoctorName=?, Department=?, " +
                     "AvailableSchedule=?, ContactNumber=? WHERE DoctorID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doctor.licenseNumber);
            ps.setString(2, doctor.name);
            ps.setString(3, doctor.department);
            ps.setString(4, doctor.schedule);
            ps.setString(5, doctor.contactNumber);
            ps.setInt(6, doctor.doctorId);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteDoctor(int doctorId) throws SQLException {
        String sql = "DELETE FROM DOCTOR WHERE DoctorID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            return ps.executeUpdate() == 1;
        }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("DoctorID"),
                rs.getString("LicenseNumber"),
                rs.getString("DoctorName"),
                rs.getString("Department"),
                rs.getString("AvailableSchedule"),
                rs.getString("ContactNumber")
        );
    }

    public boolean isLicenseNumberExists(String licenseNumber) throws SQLException {
        String sql = "SELECT 1 FROM DOCTOR WHERE LicenseNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licenseNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}