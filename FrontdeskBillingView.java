package edp.model;

import edp.database.DBConnection;
import java.sql.*;

public class HomeModel {

    public static class DashboardStats {
        public int totalPatients;
        public int totalDoctors;
        public int totalAppointments;
        public int availableBeds;
        public int occupiedBeds;
        public int pendingBilling;
        public int totalRooms;
    }

    public static DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        String[] queries = {
            "SELECT COUNT(*) FROM PATIENT",
            "SELECT COUNT(*) FROM DOCTOR",
            "SELECT COUNT(*) FROM APPOINTMENT",
            "SELECT COUNT(*) FROM BED WHERE BStatus='AVAILABLE'",
            "SELECT COUNT(*) FROM BED WHERE BStatus='OCCUPIED'",
            "SELECT COUNT(*) FROM BILLING WHERE PaymentStatus='PENDING'",
            "SELECT COUNT(*) FROM ROOM"
        };

        try (Connection conn = DBConnection.getConnection()) {
            for (int i = 0; i < queries.length; i++) {
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(queries[i])) {
                    if (rs.next()) {
                        switch (i) {
                            case 0: stats.totalPatients = rs.getInt(1); break;
                            case 1: stats.totalDoctors = rs.getInt(1); break;
                            case 2: stats.totalAppointments = rs.getInt(1); break;
                            case 3: stats.availableBeds = rs.getInt(1); break;
                            case 4: stats.occupiedBeds = rs.getInt(1); break;
                            case 5: stats.pendingBilling = rs.getInt(1); break;
                            case 6: stats.totalRooms = rs.getInt(1); break;
                        }
                    }
                } catch (SQLException ex) {
                    System.err.println("Query skipped (table may not exist): " + queries[i]);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stats;
    }
}