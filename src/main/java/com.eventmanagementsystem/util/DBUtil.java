package com.eventmanagementsystem.util;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.*;

public final class DBUtil {
    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    private static String URL = "jdbc:mysql://localhost:3306/eventdb";
    private static String USER = "root";
    private static String PASS = "";
    private static boolean driverLoaded = false;

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); driverLoaded = true; }
        catch (ClassNotFoundException e) { LOGGER.severe("No Driver"); }
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties p = new Properties(); p.load(in);
                URL = p.getProperty("db.url", URL);
                USER = p.getProperty("db.user", USER);
                PASS = p.getProperty("db.password", PASS);
            }
        } catch (Exception e) {}
    }
    private DBUtil() {}
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) throw new SQLException("Driver missing");
        return DriverManager.getConnection(URL, USER, PASS);
    }
    public static boolean testConnection() {
        try (Connection c = getConnection()) { return c != null && !c.isClosed(); } catch (SQLException e) { return false; }
    }
}