package edp.model;

import edp.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomModel {

    private String roomId;
    private String roomCategory;

    public RoomModel(String roomId, String roomCategory) {
        this.roomId       = roomId;
        this.roomCategory = roomCategory;
    }

    public String getRoomId()       { return roomId; }
    public String getRoomCategory() { return roomCategory; }

    public void add() throws SQLException {
        String sql = "INSERT INTO ROOM (RoomCategory) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCategory.toUpperCase());
            ps.executeUpdate();
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE ROOM SET RoomCategory=? WHERE RoomID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCategory.toUpperCase());
            ps.setString(2, roomId.replaceAll("[^0-9]", ""));
            ps.executeUpdate();
        }
    }

    public static void delete(String roomId) throws SQLException {
        String sql = "DELETE FROM ROOM WHERE RoomID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId.replaceAll("[^0-9]", ""));
            ps.executeUpdate();
        }
    }

    public static List<RoomModel> getAll() {
        List<RoomModel> list = new ArrayList<>();
        String sql = "SELECT * FROM ROOM";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RoomModel(
                    "R" + rs.getString("RoomID"),
                    rs.getString("RoomCategory")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static String generateRoomId() {
        String sql = "SELECT TOP 1 RoomID FROM ROOM ORDER BY RoomID DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("RoomID");
                String numPart = lastId.replaceAll("\\D+", "");
                int num = Integer.parseInt(numPart);
                return "R" + (num + 1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "R1";
    }

    public static List<Object[]> getAllForTable() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT RoomID, RoomCategory FROM ROOM ORDER BY RoomID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    "R" + rs.getString("RoomID"),
                    rs.getString("RoomCategory")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    public static String[] getAllRoomIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT RoomID FROM ROOM ORDER BY RoomID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ids.add("R" + rs.getString("RoomID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ids.toArray(new String[0]);
    }
}
