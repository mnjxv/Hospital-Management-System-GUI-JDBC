package edp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DBConnection — SECURITY FIX APPLIED (HIGH vulnerability resolved)
 *
 * Original vulnerability: Database URL, username (sa — superadmin), and
 * password ("admin") were hardcoded as plaintext constants in source code.
 * The connection also used encrypt=false, sending all SQL traffic in plaintext.
 *
 * Fix applied:
 *   1. Credentials are loaded from an external db.properties file (excluded
 *      from version control via .gitignore) — not hardcoded in source code.
 *   2. Connection encryption is enabled (encrypt=true).
 *   3. The 'sa' account should be replaced with a limited-privilege app user
 *      in db.properties (see instructions below).
 *
 * Setup instructions:
 *   Create src/db.properties (add to .gitignore — never commit this file):
 *
 *     db.url=jdbc:sqlserver://localhost:1433;databaseName=BMMG_HOSPITAL;encrypt=true;trustServerCertificate=true
 *     db.user=bmmg_app_user
 *     db.password=YourStrongPasswordHere
 *
 *   Then create a limited SQL Server login:
 *     CREATE LOGIN bmmg_app_user WITH PASSWORD = 'YourStrongPasswordHere';
 *     USE BMMG_HOSPITAL;
 *     CREATE USER bmmg_app_user FOR LOGIN bmmg_app_user;
 *     GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO bmmg_app_user;
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    // Loaded once from db.properties — never hardcoded
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "db.properties not found on classpath. " +
                    "Create src/db.properties with db.url, db.user, db.password."
                );
            }
            props.load(in);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load db.properties: " + e.getMessage(), e);
            throw new RuntimeException("Database configuration could not be loaded.", e);
        }
        URL      = props.getProperty("db.url");
        USER     = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Database connected successfully.");
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.severe("SQL Server JDBC Driver not found: " + e.getMessage());
            throw new SQLException("Driver not found. Add mssql-jdbc jar to classpath.", e);
        }
    }
}
