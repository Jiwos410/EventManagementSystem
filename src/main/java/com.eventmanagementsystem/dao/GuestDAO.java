package com.eventmanagementsystem.dao;

import com.eventmanagementsystem.model.Guest;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class GuestDAO extends BaseDAO {

    public List<Guest> getAllGuests() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY name";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("job")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    public boolean addGuest(Guest g) {
        String sql = "INSERT INTO guests(name, email, job) VALUES(?, ?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getEmail() != null ? g.getEmail() : "");
            ps.setString(3, g.getJob() != null ? g.getJob() : "");

            if (ps.executeUpdate() > 0) {
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) g.setGuestId(k.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public boolean updateGuest(Guest g) {
        String sql = "UPDATE guests SET name=?, email=?, job=? WHERE guest_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getEmail());
            ps.setString(3, g.getJob());
            ps.setInt(4, g.getGuestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
        return false;
    }

    public boolean deleteGuest(int id) {
        try { checkAdminPermission(); } catch (Exception e) { return false; }
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM guests WHERE guest_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
        return false;
    }
}