package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.Event;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class StatsDAO extends BaseDAO {
    public int countEvents() { return executeCount("SELECT COUNT(*) FROM events"); }
    public int countGuests() { return executeCount("SELECT COUNT(*) FROM guests"); }
    public int countParticipants() { return executeCount("SELECT COUNT(*) FROM participants"); }
    public int countEmployees() { return executeCount("SELECT COUNT(*) FROM employees"); }

    public List<Event> recentEvents(int limit) {
        List<Event> l = new ArrayList<>();
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("SELECT * FROM events ORDER BY date DESC LIMIT ?")){
            ps.setInt(1,limit); ResultSet rs=ps.executeQuery();
            while(rs.next()) {
                Timestamp s=rs.getTimestamp("date"), e=rs.getTimestamp("end_date");
                l.add(new Event(rs.getInt("event_id"), rs.getString("name"), s!=null?s.toLocalDateTime():null, e!=null?e.toLocalDateTime():null, rs.getString("location"), rs.getString("description")));
            }
        } catch(SQLException e){} return l;
    }

    public Map<String, Integer> getEventStatusStats() {
        Map<String, Integer> m = new LinkedHashMap<>();
        String sql="SELECT CASE WHEN date>? THEN 'SẮP DIỄN RA' WHEN COALESCE(end_date, DATE_ADD(date, INTERVAL 2 HOUR))<? THEN 'ĐÃ QUA' ELSE 'ĐANG DIỄN RA' END as s, COUNT(*) FROM events GROUP BY s";
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            Timestamp now=Timestamp.valueOf(LocalDateTime.now()); ps.setTimestamp(1,now); ps.setTimestamp(2,now);
            ResultSet rs=ps.executeQuery(); while(rs.next()) m.put(rs.getString(1), rs.getInt(2));
        } catch(SQLException e){} return m;
    }

    public Map<String, Integer> getCheckinStats() {
        return getMap("SELECT COUNT(CASE WHEN check_in IS NOT NULL AND check_out IS NULL THEN 1 END), COUNT(CASE WHEN check_in IS NULL THEN 1 END), COUNT(CASE WHEN check_in IS NOT NULL AND check_out IS NOT NULL THEN 1 END) FROM participants");
    }
    public Map<String, Integer> getCheckinStatsByEvent(int eid) {
        return getMap("SELECT COUNT(CASE WHEN p.check_in IS NOT NULL AND p.check_out IS NULL THEN 1 END), COUNT(CASE WHEN p.check_in IS NULL THEN 1 END), COUNT(CASE WHEN p.check_in IS NOT NULL AND p.check_out IS NOT NULL THEN 1 END) FROM participants p JOIN participant_event pe ON p.participant_id=pe.participant_id WHERE pe.event_id="+eid);
    }

    public Map<String, Integer> getGuestTypeStats() { return getGroup("SELECT COALESCE(job,'N/A'), COUNT(*) FROM guests GROUP BY job"); }

    public Map<String, Integer> getGuestTypeStatsByEvent(int eid) { return getGroup("SELECT COALESCE(g.job,'N/A'), COUNT(*) FROM guests g JOIN guest_event ge ON g.guest_id=ge.guest_id WHERE ge.event_id="+eid+" GROUP BY g.job"); }

    public Map<String, Integer> getEmployeeRoleStats() { return getGroup("SELECT COALESCE(role,'N/A'), COUNT(*) FROM employees GROUP BY role"); }

    public Map<String, Integer> getEmployeeRoleStatsByEvent(int eid) {
        return getGroup("SELECT COALESCE(e.role,'N/A'), COUNT(*) FROM employees e JOIN event_employee ee ON e.employee_id=ee.employee_id WHERE ee.event_id="+eid+" GROUP BY e.role");
    }

    private Map<String, Integer> getMap(String sql) {
        Map<String, Integer> m = new LinkedHashMap<>();
        try(Connection c=getConnection(); ResultSet rs=c.createStatement().executeQuery(sql)){
            if(rs.next()) { m.put("Đã check-in", rs.getInt(1)); m.put("Chưa", rs.getInt(2)); m.put("Đã check-out", rs.getInt(3)); }
        } catch(Exception e){} return m;
    }
    private Map<String, Integer> getGroup(String sql) {
        Map<String, Integer> m = new LinkedHashMap<>();
        try(Connection c=getConnection(); ResultSet rs=c.createStatement().executeQuery(sql)){ while(rs.next()) m.put(rs.getString(1), rs.getInt(2)); } catch(Exception e){} return m;
    }
}