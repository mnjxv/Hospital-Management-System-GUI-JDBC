package edp;

import edp.database.DBConnection;
import edp.view.LoginView;
import edp.controller.LoginController;

public class Main {
    public static void main(String[] args) {
        try {
            java.sql.Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Connected!");
                conn.close();  
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
