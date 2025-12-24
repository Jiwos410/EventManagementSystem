package com.eventmanagementsystem.model;

import javafx.beans.property.*;

public class Employee {
    private final IntegerProperty employeeId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    public Employee() {}

    public Employee(String name, String role) {
        this.name.set(name);
        this.role.set(role);
    }

    public Employee(int id, String name, String role) {
        this.employeeId.set(id);
        this.name.set(name);
        this.role.set(role);
    }

    public int getEmployeeId() { return employeeId.get(); }
    public void setEmployeeId(int id) { this.employeeId.set(id); }
    public IntegerProperty employeeIdProperty() { return employeeId; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public StringProperty roleProperty() { return role; }

    @Override
    public String toString() { return getName(); }
}