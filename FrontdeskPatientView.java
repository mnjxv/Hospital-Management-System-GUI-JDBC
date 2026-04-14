package edp.model;

import edp.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BedModel {
    private Integer bedId;
    private String roomId;
    private String status;

    public BedModel(Integer bedId, String roomId, String status) {
        this.bedId  = bedId;
        this.roomId = roomId;
        this.status = status;
    }

    public Integer getBedId()          { return bedId; }
    public String  getRoomId()         { return roomId; }
    public String  getStatus()         { return status; }

    public String getFormattedBedId() {
        if (bedId == null) return "AUTO";
        return String.format("B%02d", bedId);
    }

    public static int parseFormattedBedId(String formatted) {
        if (formatted == null || formatted.isEmpty()) return 0;
        return Integer.parseInt(formatted.replaceAll("[^0-9]", ""));
    }

    // ✅ Returns room IDs formatted as "R1", "R2", etc. for combo box
    public static List<String> getAllRoomIds() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT RoomID FROM ROOM ORDER BY RoomID";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add("R" + rs.getInt("RoomID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<BedModel> getAll() {
        List<BedModel> list = new ArrayList<>();
        String sql = "SELECT BedID, RoomID, BStatus FROM BED";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BedModel(
                    rs.getInt("BedID"),
                    "R" + rs.getInt("RoomID"),  // ✅ format as R1, R2...
                    rs.getString("BStatus")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int add() throws SQLException {
        String sql = "INSERT INTO BED (RoomID, BStatus) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // ✅ strip "R" prefix before saving to DB
            ps.setInt(1, Integer.parseInt(roomId.replaceAll("[^0-9]", "")));
            ps.setString(2, status.toUpperCase());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bedId = rs.getInt(1);
                    return bedId;
                }
            }
        }
        return 0;
    }

    public void update() throws SQLException {
        String sql = "UPDATE BED SET RoomID=?, BStatus=? WHERE BedID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // ✅ strip "R" prefix before saving to DB
            ps.setInt(1, Integer.parseInt(roomId.replaceAll("[^0-9]", "")));
            ps.setString(2, status.toUpperCase());
            ps.setInt(3, bedId);
            ps.executeUpdate();
        }
    }

    public static void delete(int bedId) throws SQLException {
        String sql = "DELETE FROM BED WHERE BedID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bedId);
            ps.executeUpdate();
        }
    }

    public static void deleteBedsByRoom(String roomId) throws SQLException {
        String sql = "DELETE FROM BED WHERE RoomID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(roomId.replaceAll("[^0-9]", "")));
            ps.executeUpdate();
        }
    }
    
    public static boolean isBedOccupied(int bedId) throws SQLException {
        String sql = "SELECT BStatus FROM BED WHERE BedID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bedId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "OCCUPIED".equals(rs.getString("BStatus"));
            }
        }
        return false;
    }
    
    public static boolean isRoomOccupied(String roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INPATIENT WHERE RoomID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roomId.replaceAll("[^0-9]", ""));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}