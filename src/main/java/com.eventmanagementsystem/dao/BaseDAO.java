package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.util.DBUtil;
import com.eventmanagementsystem.util.Session;
import java.sql.*;
import java.util.logging.Logger;

public abstract class BaseDAO {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected Connection getConnection() throws SQLException { return DBUtil.getConnection(); }

    protected int executeCount(String sql) {
        try(Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getInt(1);
        } catch(SQLException e) { logger.severe(e.getMessage()); }
        return 0;
    }

    protected void checkAdminPermission() {
        var u = Session.getCurrentUser();
        if(u==null || !"admin".equalsIgnoreCase(u.getRole())) throw new SecurityException("Admin only");
    }
}