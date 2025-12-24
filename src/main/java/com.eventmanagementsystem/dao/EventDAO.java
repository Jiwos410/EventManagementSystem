package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.Event;
import java.sql.*;
import java.util.*;

public class EventDAO extends BaseDAO {
    public List<Event> getAllEvents() {
        List<Event> l = new ArrayList<>();
        try(Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM events ORDER BY date DESC"); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) l.add(map(rs));
        } catch(SQLException e){logger.severe(e.getMessage());}
        return l;
    }

    public boolean addEvent(Event e) {
        try(Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO events (name,date,end_date,location,description) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setTimestamp(2, e.getDate()!=null?Timestamp.valueOf(e.getDate()):null);
            ps.setTimestamp(3, e.getEndDate()!=null?Timestamp.valueOf(e.getEndDate()):null);
            ps.setString(4, e.getLocation()); ps.setString(5, e.getDescription());
            if(ps.executeUpdate()>0) { try(ResultSet k=ps.getGeneratedKeys()){if(k.next()) e.setEventId(k.getInt(1));} return true; }
        } catch(SQLException ex){logger.severe(ex.getMessage());}
        return false;
    }

    public boolean updateEvent(Event e) {
        try(Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE events SET name=?,date=?,end_date=?,location=?,description=? WHERE event_id=?")) {
            ps.setString(1, e.getName());
            ps.setTimestamp(2, e.getDate()!=null?Timestamp.valueOf(e.getDate()):null);
            ps.setTimestamp(3, e.getEndDate()!=null?Timestamp.valueOf(e.getEndDate()):null);
            ps.setString(4, e.getLocation()); ps.setString(5, e.getDescription()); ps.setInt(6, e.getEventId());
            return ps.executeUpdate()>0;
        } catch(SQLException ex){logger.severe(ex.getMessage());}
        return false;
    }

    public boolean deleteEvent(int id) {
        try{ checkAdminPermission(); } catch(Exception e){return false;}
        Connection c = null;
        try {
            c = getConnection(); c.setAutoCommit(false);
            try(PreparedStatement p = c.prepareStatement("DELETE FROM guest_event WHERE event_id=?")){p.setInt(1,id);p.executeUpdate();}
            try(PreparedStatement p = c.prepareStatement("DELETE FROM participant_event WHERE event_id=?")){p.setInt(1,id);p.executeUpdate();}
            try(PreparedStatement p = c.prepareStatement("DELETE FROM event_employee WHERE event_id=?")){p.setInt(1,id);p.executeUpdate();}
            try(PreparedStatement p = c.prepareStatement("DELETE FROM events WHERE event_id=?")){p.setInt(1,id);p.executeUpdate();}
            try(Statement s = c.createStatement()) {
                s.executeUpdate("DELETE g FROM guests g LEFT JOIN guest_event ge ON g.guest_id=ge.guest_id WHERE ge.guest_id IS NULL");
                s.executeUpdate("DELETE p FROM participants p LEFT JOIN participant_event pe ON p.participant_id=pe.participant_id WHERE pe.participant_id IS NULL");
                s.executeUpdate("DELETE e FROM employees e LEFT JOIN event_employee ee ON e.employee_id=ee.employee_id WHERE ee.employee_id IS NULL");
            }
            c.commit(); return true;
        } catch(SQLException ex){if(c!=null)try{c.rollback();}catch(Exception e){}; return false;}
        finally{if(c!=null)try{c.setAutoCommit(true);c.close();}catch(Exception e){}}
    }

    private Event map(ResultSet rs) throws SQLException {
        Timestamp s=rs.getTimestamp("date"), e=rs.getTimestamp("end_date");
        return new Event(rs.getInt("event_id"), rs.getString("name"), s!=null?s.toLocalDateTime():null, e!=null?e.toLocalDateTime():null, rs.getString("location"), rs.getString("description"));
    }
}