package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.*;
import com.eventmanagementsystem.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.util.ArrayList;

public class StaffParticipantView extends BorderPane {
    private final ParticipantDAO dao = new ParticipantDAO();
    private final ParticipantEventDAO peDao = new ParticipantEventDAO();
    private final EventDAO evDao = new EventDAO();
    private final TableView<Participant> table = new TableView<>();
    private final ComboBox<Event> cb = new ComboBox<>();

    public StaffParticipantView() {
        setPadding(new Insets(10));
        TableColumn<Participant,String> c1=new TableColumn<>("Name"); c1.setCellValueFactory(d->d.getValue().nameProperty());
        TableColumn<Participant,String> c2=new TableColumn<>("In"); c2.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCheckIn()!=null?d.getValue().getCheckIn().toString():""));
        TableColumn<Participant,String> c3=new TableColumn<>("Out"); c3.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCheckOut()!=null?d.getValue().getCheckOut().toString():""));
        table.getColumns().addAll(c1,c2,c3); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cb.setConverter(new StringConverter<>(){public String toString(Event e){return e==null?"":e.getName();}public Event fromString(String s){return null;}});
        cb.setMaxWidth(Double.MAX_VALUE);
        Button in=new Button("In"), out=new Button("Out");
        in.setOnAction(e->{Participant p=table.getSelectionModel().getSelectedItem(); if(p!=null&&dao.checkIn(p.getParticipantId())) refresh();});
        out.setOnAction(e->{Participant p=table.getSelectionModel().getSelectedItem(); if(p!=null&&dao.checkOut(p.getParticipantId())) refresh();});
        cb.setOnAction(e->refresh());

        VBox right = new VBox(10, new Label("Filter"), cb, new Separator(), new Label("Action"), new HBox(5,in,out));
        right.setPrefWidth(180);

        setCenter(table);
        setRight(right);
        BorderPane.setMargin(right, new Insets(0,0,0,10));

        ArrayList<Event> l = new ArrayList<>(); l.add(new Event(-1,"-- ALL --",null,null,null,null)); l.addAll(evDao.getAllEvents());
        cb.setItems(FXCollections.observableArrayList(l)); cb.getSelectionModel().selectFirst();
    }
    private void refresh() {
        Event e = cb.getValue();
        if(e!=null && e.getEventId()>0) table.setItems(FXCollections.observableArrayList(peDao.getParticipantsByEvent(e.getEventId())));
        else table.setItems(FXCollections.observableArrayList(dao.getAllParticipants()));
    }
}