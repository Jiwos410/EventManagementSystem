package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.Guest;
import java.sql.*;
import java.util.*;

public class GuestEventDAO extends BaseDAO {
    public boolean assignGuestToEvent(int gid, int eid) {
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("INSERT IGNORE INTO guest_event(guest_id,event_id) VALUES(?,?)")){
            ps.setInt(1,gid); ps.setInt(2,eid); return ps.executeUpdate()>0;
        } catch(SQLException e){logger.severe(e.getMessage());} return false;
    }
    public List<Guest> getGuestsByEvent(int eid) {
        List<Guest> l = new ArrayList<>();
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("SELECT g.* FROM guests g JOIN guest_event ge ON g.guest_id=ge.guest_id WHERE ge.event_id=?")){
            ps.setInt(1,eid); ResultSet rs = ps.executeQuery();
            while(rs.next()) l.add(new Guest(rs.getInt("guest_id"), rs.getString("name"), rs.getString("email"), rs.getString("job")));
        } catch(SQLException e){logger.severe(e.getMessage());} return l;
    }
}