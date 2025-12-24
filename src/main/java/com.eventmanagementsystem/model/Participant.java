package com.eventmanagementsystem.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Participant {
    private final IntegerProperty participantId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> checkIn = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> checkOut = new SimpleObjectProperty<>();

    public Participant() {}

    public Participant(String name) {
        this.name.set(name);
    }

    public Participant(int id, String name, LocalDateTime in, LocalDateTime out) {
        this.participantId.set(id);
        this.name.set(name);
        this.checkIn.set(in);
        this.checkOut.set(out);
    }

    public int getParticipantId() { return participantId.get(); }
    public void setParticipantId(int v) { participantId.set(v); }
    public IntegerProperty participantIdProperty() { return participantId; }

    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    public LocalDateTime getCheckIn() { return checkIn.get(); }
    public void setCheckIn(LocalDateTime v) { checkIn.set(v); }
    public ObjectProperty<LocalDateTime> checkInProperty() { return checkIn; }

    public LocalDateTime getCheckOut() { return checkOut.get(); }
    public void setCheckOut(LocalDateTime v) { checkOut.set(v); }
    public ObjectProperty<LocalDateTime> checkOutProperty() { return checkOut; }
}