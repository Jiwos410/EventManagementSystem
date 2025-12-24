package com.eventmanagementsystem.dao;
import com.eventmanagementsystem.model.Employee;
import java.sql.*;
import java.util.*;

public class EventEmployeeDAO extends BaseDAO {
    public boolean assignEmployeeToEvent(int eid, int empid) {
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("INSERT IGNORE INTO event_employee(event_id,employee_id) VALUES(?,?)")){
            ps.setInt(1,eid); ps.setInt(2,empid); return ps.executeUpdate()>0;
        } catch(SQLException e){logger.severe(e.getMessage());} return false;
    }
    public List<Employee> getEmployeesByEvent(int eid) {
        List<Employee> l = new ArrayList<>();
        try(Connection c=getConnection(); PreparedStatement ps=c.prepareStatement("SELECT e.* FROM employees e JOIN event_employee ee ON e.employee_id=ee.employee_id WHERE ee.event_id=?")){
            ps.setInt(1,eid); ResultSet rs = ps.executeQuery();
            while(rs.next()) l.add(new Employee(rs.getInt("employee_id"), rs.getString("name"), rs.getString("role")));
        } catch(SQLException e){logger.severe(e.getMessage());} return l;
    }
}