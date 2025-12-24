package com.eventmanagementsystem.model;

import javafx.beans.property.*;

public class Guest {
    private final IntegerProperty guestId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty job = new SimpleStringProperty();

    public Guest() {}

    public Guest(String name, String email, String job) {
        this.name.set(name);
        this.email.set(email);
        this.job.set(job);
    }

    public Guest(int id, String name, String email, String job) {
        this.guestId.set(id);
        this.name.set(name);
        this.email.set(email);
        this.job.set(job);
    }

    public int getGuestId() { return guestId.get(); }
    public void setGuestId(int id) { this.guestId.set(id); }
    public IntegerProperty guestIdProperty() { return guestId; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getJob() { return job.get(); }
    public void setJob(String job) { this.job.set(job); }
    public StringProperty jobProperty() { return job; }

    @Override
    public String toString() { return getName(); }
}