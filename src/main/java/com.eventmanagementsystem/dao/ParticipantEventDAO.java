package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.Participant;
import java.sql.*;
import java.util.*;

public class ParticipantEventDAO extends BaseDAO {
    public boolean assignParticipantToEvent(int pid, int eid) {
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("INSERT IGNORE INTO participant_event(participant_id,event_id) VALUES(?,?)")){
            ps.setInt(1,pid); ps.setInt(2,eid); return ps.executeUpdate()>0;
        } catch(SQLException e){logger.severe(e.getMessage());} return false;
    }
    public List<Participant> getParticipantsByEvent(int eid) {
        List<Participant> l = new ArrayList<>();
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("SELECT p.* FROM participants p JOIN participant_event pe ON p.participant_id=pe.participant_id WHERE pe.event_id=?")){
            ps.setInt(1,eid); ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Participant p = new Participant(); p.setParticipantId(rs.getInt("participant_id")); p.setName(rs.getString("name"));
                Timestamp i=rs.getTimestamp("check_in"), o=rs.getTimestamp("check_out");
                if(i!=null) p.setCheckIn(i.toLocalDateTime()); if(o!=null) p.setCheckOut(o.toLocalDateTime());
                l.add(p);
            }
        } catch(SQLException e){logger.severe(e.getMessage());} return l;
    }
}