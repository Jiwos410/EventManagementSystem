package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.*;
import com.eventmanagementsystem.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.util.ArrayList;

public class StaffGuestView extends BorderPane {
    private final GuestDAO dao = new GuestDAO();
    private final GuestEventDAO geDao = new GuestEventDAO();
    private final EventDAO evDao = new EventDAO();
    private final TableView<Guest> table = new TableView<>();
    private final ComboBox<Event> cb = new ComboBox<>();

    public StaffGuestView() {
        setPadding(new Insets(10));
        TableColumn<Guest,String> c1=new TableColumn<>("Name"); c1.setCellValueFactory(d->d.getValue().nameProperty());
        TableColumn<Guest,String> c2=new TableColumn<>("Job"); c2.setCellValueFactory(d->d.getValue().jobProperty());
        table.getColumns().addAll(c1,c2); table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cb.setConverter(new StringConverter<>(){public String toString(Event e){return e==null?"":e.getName();}public Event fromString(String s){return null;}});
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.valueProperty().addListener((o,old,v)->{if(v!=null && v.getEventId()>0)table.setItems(FXCollections.observableArrayList(geDao.getGuestsByEvent(v.getEventId()))); else table.setItems(FXCollections.observableArrayList(dao.getAllGuests()));});

        VBox right = new VBox(10, new Label("Filter"), cb, new Separator(), new Label("STAFF: View Only"));
        right.setPrefWidth(180);

        setCenter(table);
        setRight(right);
        BorderPane.setMargin(right, new Insets(0,0,0,10));

        ArrayList<Event> l = new ArrayList<>(); l.add(new Event(-1,"-- ALL --",null,null,null,null)); l.addAll(evDao.getAllEvents());
        cb.setItems(FXCollections.observableArrayList(l)); cb.getSelectionModel().selectFirst();
    }
}