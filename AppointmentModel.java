package edp.model;

import edp.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PatientModel {

    public static class Patient {
        public String patientId;
        public String name;
        public String gender;
        public String address;
        public String contactNumber;
        public String dateOfBirth;
        public String medicalHistory;
        public String patientType;
        public String roomId;

        public Patient() {}

        public Patient(String patientId, String name, String gender,
                       String address, String contactNumber,
                       String dateOfBirth, String medicalHistory,
                       String patientType, String roomId) {
            this.patientId      = patientId;
            this.name           = name;
            this.gender         = gender;
            this.address        = address;
            this.contactNumber  = contactNumber;
            this.dateOfBirth    = dateOfBirth;
            this.medicalHistory = medicalHistory;
            this.patientType    = patientType;
            this.roomId         = roomId;
        }

        public Object[] toRow() {
            return new Object[]{
                patientId, name, gender, address,
                contactNumber, dateOfBirth, medicalHistory, patientType, roomId
            };
        }
    }

    public List<Patient> getPatients(String typeFilter) throws SQLException {
        List<Patient> list = new ArrayList<>();

        boolean filterAll = (typeFilter == null || typeFilter.equalsIgnoreCase("All"));

        String sql;
        if (filterAll) {
            sql = "SELECT p.*, " +
                  "  (SELECT TOP 1 i.RoomID FROM INPATIENT i WHERE i.PatientID = p.PatientID ORDER BY i.AdmissionDate DESC) AS RoomID " +
                  "FROM PATIENT p ORDER BY p.PatientID";
        } else {
            sql = "SELECT p.*, " +
                  "  (SELECT TOP 1 i.RoomID FROM INPATIENT i WHERE i.PatientID = p.PatientID ORDER BY i.AdmissionDate DESC) AS RoomID " +
                  "FROM PATIENT p WHERE p.PatientType = ? ORDER BY p.PatientID";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filterAll) ps.setString(1, typeFilter);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // FIX 2: addPatient — INPATIENT requires DoctorID (NOT NULL) and AdmissionDate (NOT NULL).
    // Since the Patient form has no DoctorID field, we use the first available DoctorID
    // as a placeholder. AdmissionDate defaults to today.
    // OUTPATIENT requires VisitDateTime (NOT NULL), so we use the current timestamp.
    public boolean addPatient(Patient p) throws SQLException {
        // Insert into PATIENT table first (no RoomID column here)
        String sql = "INSERT INTO PATIENT " +
                     "(PatientName, Gender, Address, ContactNumber, " +
                     "DateOfBirth, MedicalHistory, PatientType) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.name);
            ps.setString(2, p.gender);
            ps.setString(3, p.address);
            ps.setString(4, p.contactNumber);
            ps.setString(5, p.dateOfBirth);
            ps.setString(6, p.medicalHistory);
            ps.setString(7, p.patientType);

            int rows = ps.executeUpdate();
            if (rows != 1) return false;

            // Retrieve the auto-generated PatientID
            String newPatientId = null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) newPatientId = keys.getString(1);
            }
            if (newPatientId == null) return false;

            if ("INPATIENT".equalsIgnoreCase(p.patientType)) {
                String firstDoctorId = getFirstDoctorId(conn);
                String roomIdNum = (p.roomId != null) ? p.roomId.replaceAll("[^0-9]", "") : "";

                conn.setAutoCommit(false);
                try {
                    executeQuietly(conn, "SET IDENTITY_INSERT INPATIENT ON");
                    String inSql;
                    if (!roomIdNum.isEmpty()) {
                        inSql = "INSERT INTO INPATIENT (PatientID, DoctorID, RoomID, AdmissionDate) VALUES (?, ?, ?, CAST(GETDATE() AS DATE))";
                    } else {
                        inSql = "INSERT INTO INPATIENT (PatientID, DoctorID, AdmissionDate) VALUES (?, ?, CAST(GETDATE() AS DATE))";
                    }
                    try (PreparedStatement inPs = conn.prepareStatement(inSql)) {
                        inPs.setString(1, newPatientId);
                        inPs.setString(2, firstDoctorId);
                        if (!roomIdNum.isEmpty()) inPs.setString(3, roomIdNum);
                        inPs.executeUpdate();
                    }
                    executeQuietly(conn, "SET IDENTITY_INSERT INPATIENT OFF");
                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }

            } else if ("OUTPATIENT".equalsIgnoreCase(p.patientType)) {
                conn.setAutoCommit(false);
                try {
                    executeQuietly(conn, "SET IDENTITY_INSERT OUTPATIENT ON");
                    String outSql = "INSERT INTO OUTPATIENT (PatientID, VisitDateTime) VALUES (?, GETDATE())";
                    try (PreparedStatement outPs = conn.prepareStatement(outSql)) {
                        outPs.setString(1, newPatientId);
                        outPs.executeUpdate();
                    }
                    executeQuietly(conn, "SET IDENTITY_INSERT OUTPATIENT OFF");
                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            return true;
        }
    }

    // FIX 3: updatePatient — same issue; INPATIENT needs DoctorID + AdmissionDate.
    // Preserve existing DoctorID/AdmissionDate when re-inserting the INPATIENT row.
    public boolean updatePatient(Patient p) throws SQLException {
        String patientIdForDb = p.patientId.replaceAll("[^0-9]", "");

        // Update core patient fields in PATIENT table
        String sql = "UPDATE PATIENT SET PatientName=?, Gender=?, Address=?, ContactNumber=?, "
                   + "DateOfBirth=?, MedicalHistory=?, PatientType=? WHERE PatientID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.name);
            ps.setString(2, p.gender);
            ps.setString(3, p.address);
            ps.setString(4, p.contactNumber);
            ps.setString(5, p.dateOfBirth);
            ps.setString(6, p.medicalHistory);
            ps.setString(7, p.patientType);
            ps.setString(8, patientIdForDb);
            boolean updated = ps.executeUpdate() == 1;

            if ("INPATIENT".equalsIgnoreCase(p.patientType)) {
                String roomIdNum = (p.roomId != null) ? p.roomId.replaceAll("[^0-9]", "") : "";

                String existingDoctorId = null;
                String existingAdmissionDate = null;
                String selectSql = "SELECT TOP 1 DoctorID, AdmissionDate FROM INPATIENT WHERE PatientID = ? ORDER BY AdmissionDate DESC";
                try (PreparedStatement selPs = conn.prepareStatement(selectSql)) {
                    selPs.setString(1, patientIdForDb);
                    try (ResultSet rs = selPs.executeQuery()) {
                        if (rs.next()) {
                            existingDoctorId    = rs.getString("DoctorID");
                            existingAdmissionDate = rs.getString("AdmissionDate");
                        }
                    }
                }

                if (existingDoctorId == null) existingDoctorId = getFirstDoctorId(conn);
                if (existingAdmissionDate == null) existingAdmissionDate = java.time.LocalDate.now().toString();

                conn.setAutoCommit(false);
                try {
                    executeQuietly(conn, "DELETE FROM INPATIENT WHERE PatientID = ?", patientIdForDb);
                    executeQuietly(conn, "SET IDENTITY_INSERT INPATIENT ON");

                    String inSql;
                    if (!roomIdNum.isEmpty()) {
                        inSql = "INSERT INTO INPATIENT (PatientID, DoctorID, RoomID, AdmissionDate) VALUES (?, ?, ?, ?)";
                    } else {
                        inSql = "INSERT INTO INPATIENT (PatientID, DoctorID, AdmissionDate) VALUES (?, ?, ?)";
                    }
                    try (PreparedStatement inPs = conn.prepareStatement(inSql)) {
                        inPs.setString(1, patientIdForDb);
                        inPs.setString(2, existingDoctorId);
                        if (!roomIdNum.isEmpty()) {
                            inPs.setString(3, roomIdNum);
                            inPs.setString(4, existingAdmissionDate);
                        } else {
                            inPs.setString(3, existingAdmissionDate);
                        }
                        inPs.executeUpdate();
                    }
                    executeQuietly(conn, "SET IDENTITY_INSERT INPATIENT OFF");
                    executeQuietly(conn, "DELETE FROM OUTPATIENT WHERE PatientID = ?", patientIdForDb);
                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }

            } else if ("OUTPATIENT".equalsIgnoreCase(p.patientType)) {
                conn.setAutoCommit(false);
                try {
                    executeQuietly(conn, "DELETE FROM INPATIENT WHERE PatientID = ?", patientIdForDb);

                    String checkSql = "SELECT COUNT(*) FROM OUTPATIENT WHERE PatientID = ?";
                    int count = 0;
                    try (PreparedStatement chkPs = conn.prepareStatement(checkSql)) {
                        chkPs.setString(1, patientIdForDb);
                        try (ResultSet rs = chkPs.executeQuery()) {
                            if (rs.next()) count = rs.getInt(1);
                        }
                    }
                    if (count == 0) {
                        executeQuietly(conn, "SET IDENTITY_INSERT OUTPATIENT ON");
                        String outSql = "INSERT INTO OUTPATIENT (PatientID, VisitDateTime) VALUES (?, GETDATE())";
                        try (PreparedStatement outPs = conn.prepareStatement(outSql)) {
                            outPs.setString(1, patientIdForDb);
                            outPs.executeUpdate();
                        }
                        executeQuietly(conn, "SET IDENTITY_INSERT OUTPATIENT OFF");
                    }
                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            return updated;
        }
    }

    public boolean deletePatient(String patientId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String idForQuery = patientId.replaceAll("[^0-9]", "");
                executeQuietly(conn, "DELETE FROM OUTPATIENT WHERE PatientID = ?", idForQuery);
                executeQuietly(conn, "DELETE FROM INPATIENT WHERE PatientID = ?", idForQuery);
                executeQuietly(conn, "DELETE FROM APPOINTMENT WHERE PatientID = ?", idForQuery);
                executeQuietly(conn, "DELETE FROM BILLING WHERE TreatmentID IN (SELECT TreatmentID FROM TREATMENT WHERE PatientID = ?)", idForQuery);
                executeQuietly(conn, "DELETE FROM TREATMENT WHERE PatientID = ?", idForQuery);
                executeQuietly(conn, "DELETE FROM PATIENT WHERE PatientID = ?", idForQuery);

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Helper: get the first available DoctorID from the DOCTOR table
    private String getFirstDoctorId(Connection conn) throws SQLException {
        String sql = "SELECT TOP 1 DoctorID FROM DOCTOR ORDER BY DoctorID";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("DoctorID");
        }
        throw new SQLException("No doctors found in the system. Please add a doctor first.");
    }

    private void executeQuietly(Connection conn, String sql, String param) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Suppress non-critical cleanup errors
        }
    }

    private void executeQuietly(Connection conn, String sql) {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            // Suppress non-critical cleanup errors
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        String roomId = null;
        try { roomId = rs.getString("RoomID"); } catch (SQLException ex) { java.util.logging.Logger.getLogger("PatientModel").log(java.util.logging.Level.WARNING, "RoomID column not found or null for patient row: " + ex.getMessage()); }
        String displayRoomId = (roomId != null && !roomId.isEmpty()) ? "R" + roomId : "";
        return new Patient(
            "P" + rs.getString("PatientID"),
            rs.getString("PatientName"),
            rs.getString("Gender"),
            rs.getString("Address"),
            rs.getString("ContactNumber"),
            rs.getString("DateOfBirth"),
            rs.getString("MedicalHistory"),
            rs.getString("PatientType"),
            displayRoomId
        );
    }

    public static List<String> getAllPatientIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT PatientID FROM PATIENT ORDER BY PatientID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ids.add("P" + rs.getString("PatientID"));
            }
        }
        return ids;
    }
}