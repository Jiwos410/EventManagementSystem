package com.eventmanagementsystem.model;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Event {
    private final IntegerProperty eventId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDate = new SimpleObjectProperty<>();
    private final StringProperty location = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Event() {}
    public Event(int id, String name, LocalDateTime d, LocalDateTime ed, String loc, String desc) {
        setEventId(id); setName(name); setDate(d); setEndDate(ed); setLocation(loc); setDescription(desc);
    }
    public Event(String name, LocalDateTime d, LocalDateTime ed, String loc, String desc) {
        setName(name); setDate(d); setEndDate(ed); setLocation(loc); setDescription(desc);
    }

    private void updSts() {
        LocalDateTime n = LocalDateTime.now(), s = getDate(), e = getEndDate();
        if(s==null) status.set("CHƯA LỊCH");
        else {
            if(e==null) e=s.plusHours(2);
            if(n.isBefore(s)) status.set("SẮP DIỄN RA");
            else if(n.isAfter(e)) status.set("ĐÃ QUA");
            else status.set("ĐANG DIỄN RA");
        }
    }

    public int getEventId(){return eventId.get();} public void setEventId(int v){eventId.set(v);} public IntegerProperty eventIdProperty(){return eventId;}
    public String getName(){return name.get();} public void setName(String v){name.set(v);} public StringProperty nameProperty(){return name;}
    public LocalDateTime getDate(){return date.get();} public void setDate(LocalDateTime v){date.set(v); updSts();} public ObjectProperty<LocalDateTime> dateProperty(){return date;}
    public LocalDateTime getEndDate(){return endDate.get();} public void setEndDate(LocalDateTime v){endDate.set(v); updSts();} public ObjectProperty<LocalDateTime> endDateProperty(){return endDate;}
    public String getLocation(){return location.get();} public void setLocation(String v){location.set(v);} public StringProperty locationProperty(){return location;}
    public String getDescription(){return description.get();} public void setDescription(String v){description.set(v);} public StringProperty descriptionProperty(){return description;}
    public String getStatus(){updSts(); return status.get();} public StringProperty statusProperty(){updSts(); return status;}
    @Override public String toString(){return getName();}
}