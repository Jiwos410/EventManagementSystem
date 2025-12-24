package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.User;
import com.eventmanagementsystem.util.PasswordUtil;
import java.sql.*;

public class UserDAO extends BaseDAO {
    public User login(String u, String p) {
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("SELECT * FROM users WHERE username=?")){
            ps.setString(1,u); ResultSet rs=ps.executeQuery();
            if(rs.next() && PasswordUtil.check(p, rs.getString("password")))
                return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role"));
        } catch(SQLException e){logger.severe(e.getMessage());} return null;
    }
}