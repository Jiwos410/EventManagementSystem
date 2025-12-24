package com.eventmanagementsystem.dao;

import com.eventmanagementsystem.model.Participant;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class ParticipantDAO extends BaseDAO {

    public List<Participant> getAllParticipants() {
        List<Participant> list = new ArrayList<>();
        String sql = "SELECT * FROM participants ORDER BY name";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp i = rs.getTimestamp("check_in");
                Timestamp o = rs.getTimestamp("check_out");
                list.add(new Participant(
                        rs.getInt("participant_id"),
                        rs.getString("name"),
                        i != null ? i.toLocalDateTime() : null,
                        o != null ? o.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    public boolean addParticipant(Participant p) {
        String sql = "INSERT INTO participants(name) VALUES(?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            if (ps.executeUpdate() > 0) {
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) p.setParticipantId(k.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
        return false;
    }

    public boolean deleteParticipant(int id) {
        try { checkAdminPermission(); } catch (Exception e) { return false; }
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM participants WHERE participant_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
        return false;
    }

    public boolean checkIn(int id) {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE participants SET check_in=NOW() WHERE participant_id=? AND check_in IS NULL")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { logger.severe(e.getMessage()); }
        return false;
    }

    public boolean checkOut(int id) {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE participants SET check_out=NOW() WHERE participant_id=? AND check_in IS NOT NULL AND check_out IS NULL")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { logger.severe(e.getMessage()); }
        return false;
    }
}