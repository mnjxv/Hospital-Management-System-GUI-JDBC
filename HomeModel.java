package edp.model;

import edp.database.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillingModel {

    private int billingId;
    private int treatmentId;
    private double totalAmount;
    private double amountPaid;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String paymentMethod;

    public BillingModel() {}

    public BillingModel(int billingId, int treatmentId, double totalAmount, double amountPaid,
                        String paymentStatus, LocalDate paymentDate, String paymentMethod) {
        this.billingId = billingId;
        this.treatmentId = treatmentId;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public int getBillingId()            { return billingId; }
    public void setBillingId(int v)      { this.billingId = v; }
    public int getTreatmentId()          { return treatmentId; }
    public void setTreatmentId(int v)    { this.treatmentId = v; }
    public double getTotalAmount()       { return totalAmount; }
    public void setTotalAmount(double v) { this.totalAmount = v; }
    public double getAmountPaid()        { return amountPaid; }
    public void setAmountPaid(double v)  { this.amountPaid = v; }
    public String getPaymentStatus()     { return paymentStatus; }
    public void setPaymentStatus(String v) { this.paymentStatus = v; }
    public LocalDate getPaymentDate()    { return paymentDate; }
    public void setPaymentDate(LocalDate v) { this.paymentDate = v; }
    public String getPaymentMethod()     { return paymentMethod; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }

    public String getDisplayId() { return String.valueOf(billingId); }

    public Object[] toRow() {
        return new Object[]{
            getDisplayId(),
            treatmentId,
            totalAmount,
            amountPaid,
            paymentStatus,
            paymentDate != null ? paymentDate.toString() : "",
            paymentMethod
        };
    }

    public static List<Object[]> getAll() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT b.BillingID, b.TreatmentID, t.TreatmentFee, b.AmountPaid, b.PaymentStatus, b.PaymentDate, b.PaymentMethod " +
                     "FROM BILLING b LEFT JOIN TREATMENT t ON b.TreatmentID = t.TreatmentID ORDER BY b.BillingID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("BillingID"),
                    rs.getInt("TreatmentID"),
                    rs.getDouble("TreatmentFee"),
                    rs.getDouble("AmountPaid"),
                    rs.getString("PaymentStatus"),
                    rs.getString("PaymentDate"),
                    rs.getString("PaymentMethod")
                });
            }
        }
        return rows;
    }

    public void add() throws SQLException {
        String sql = "INSERT INTO BILLING (TreatmentID, AmountPaid, PaymentStatus, PaymentDate, PaymentMethod) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, treatmentId);
            ps.setDouble(2, amountPaid);
            ps.setString(3, paymentStatus);
            ps.setDate(4, paymentDate != null ? Date.valueOf(paymentDate) : null);
            ps.setString(5, paymentMethod);
            ps.executeUpdate();
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE BILLING SET TreatmentID=?, AmountPaid=?, PaymentStatus=?, PaymentDate=?, PaymentMethod=? WHERE BillingID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, treatmentId);
            ps.setDouble(2, amountPaid);
            ps.setString(3, paymentStatus);
            ps.setDate(4, paymentDate != null ? Date.valueOf(paymentDate) : null);
            ps.setString(5, paymentMethod);
            ps.setInt(6, billingId);
            ps.executeUpdate();
        }
    }

    public static void delete(int billingId) throws SQLException {
        String sql = "DELETE FROM BILLING WHERE BillingID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billingId);
            ps.executeUpdate();
        }
    }

    public static List<String> getAllTreatmentIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT TreatmentID FROM TREATMENT ORDER BY TreatmentID";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getString("TreatmentID"));
            }
        }
        return ids;
    }
}