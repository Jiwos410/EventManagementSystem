package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.EventDAO;
import com.eventmanagementsystem.model.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;

public class StaffEventView extends BorderPane {
    private final TableView<Event> table = new TableView<>();
    private final EventDAO dao = new EventDAO();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public StaffEventView() {
        setPadding(new Insets(10));

        TableColumn<Event,String> c1=new TableColumn<>("Name"); c1.setCellValueFactory(d->d.getValue().nameProperty());
        TableColumn<Event,String> c2=new TableColumn<>("Time"); c2.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getDate()!=null?d.getValue().getDate().format(dtf)+" -> "+(d.getValue().getEndDate()!=null?d.getValue().getEndDate().format(dtf):""):""));
        TableColumn<Event,String> c3=new TableColumn<>("Status"); c3.setCellValueFactory(d->d.getValue().statusProperty());
        c3.setCellFactory(c->new TableCell<>(){@Override protected void updateItem(String s, boolean e){super.updateItem(s,e); if(s!=null){setText(s); setStyle("-fx-text-fill:"+(s.equals("ĐÃ QUA")?"red":s.equals("ĐANG DIỄN RA")?"#f59e0b":"green")+";-fx-font-weight:bold");}}});

        table.getColumns().addAll(c1,c2,c3);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(dao.getAllEvents()));

        VBox info = new VBox(10, new Label("STAFF ROLE"), new Label("View Only"));
        info.setStyle("-fx-background-color:#f4f4f4;-fx-padding:10");
        info.setPrefWidth(180);

        setCenter(table);
        setRight(info);
        BorderPane.setMargin(info, new Insets(0,0,0,10));
    }
}