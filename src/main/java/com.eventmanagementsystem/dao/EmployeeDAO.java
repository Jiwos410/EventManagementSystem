package com.eventmanagementsystem.dao;

import com.eventmanagementsystem.model.Employee;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class EmployeeDAO extends BaseDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY name";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    public boolean addEmployee(Employee e) {
        String sql = "INSERT INTO employees(name, role) VALUES(?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getRole() != null ? e.getRole() : "");

            if (ps.executeUpdate() > 0) {
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) e.setEmployeeId(k.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            logger.severe(ex.getMessage());
        }
        return false;
    }

    public boolean updateEmployee(Employee e) {
        String sql = "UPDATE employees SET name=?, role=? WHERE employee_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getRole());
            ps.setInt(3, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.severe(ex.getMessage());
        }
        return false;
    }

    public boolean deleteEmployee(int id) {
        try { checkAdminPermission(); } catch (Exception e) { return false; }
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE employee_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.severe(ex.getMessage());
        }
        return false;
    }
}