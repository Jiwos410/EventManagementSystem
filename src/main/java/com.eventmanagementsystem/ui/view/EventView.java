package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.EventDAO;
import com.eventmanagementsystem.model.Event;
import com.eventmanagementsystem.ui.util.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EventView extends BorderPane {
    private final EventDAO dao = new EventDAO();
    private final TableView<Event> table = new TableView<>();
    private final TextField txtName = new TextField(), txtLoc = new TextField(), txtDesc = new TextField();
    private final DatePicker d1 = new DatePicker(), d2 = new DatePicker();
    private final Spinner<Integer> h1=sp(8), m1=sp(0), h2=sp(10), m2=sp(0);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EventView() {
        setPadding(new Insets(10));

        TableColumn<Event,String> c1=new TableColumn<>("Name");
        c1.setCellValueFactory(d->d.getValue().nameProperty());

        TableColumn<Event,String> c2=new TableColumn<>("Time");
        c2.setCellValueFactory(d->{
            if(d.getValue().getDate()==null) return null;
            return new SimpleStringProperty(d.getValue().getDate().format(dtf) + (d.getValue().getEndDate()!=null ? " -> "+d.getValue().getEndDate().format(dtf) : ""));
        });

        TableColumn<Event,String> c3=new TableColumn<>("Status");
        c3.setCellValueFactory(d->d.getValue().statusProperty());
        c3.setCellFactory(c->new TableCell<>(){
            @Override protected void updateItem(String s, boolean e){
                super.updateItem(s,e);
                if(s==null || e) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(s);
                    String color = "green";
                    if("ĐÃ QUA".equals(s)) color = "red";
                    else if("ĐANG DIỄN RA".equals(s)) color = "#f59e0b"; // Cam
                    setStyle("-fx-text-fill:"+color+";-fx-font-weight:bold");
                }
            }
        });

        table.getColumns().addAll(c1,c2,c3);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedItemProperty().addListener((o,old,v)->{if(v!=null)fill(v);else clr();});
        setCenter(table); setRight(form());
        com.eventmanagementsystem.util.EventManager.addEventChangeListener(this::load);
        load();
    }

    private Spinner<Integer> sp(int v) { Spinner<Integer> s=new Spinner<>(0, v==8||v==10?23:59, v); s.setEditable(true); s.setPrefWidth(60); return s; }

    private VBox form() {
        VBox v = new VBox(10); v.setPadding(new Insets(0,0,0,10)); v.setPrefWidth(300);
        Button add=new Button("Add"), upd=new Button("Update"), del=new Button("Delete"), clr=new Button("Clear");
        HBox t1=new HBox(5, new Label("Start:"), h1, m1), t2=new HBox(5, new Label("End:"), h2, m2);

        add.setOnAction(e->save(true)); upd.setOnAction(e->save(false));
        del.setOnAction(e->{
            Event ev=table.getSelectionModel().getSelectedItem();
            if(ev!=null && AlertUtil.confirm("Delete?")) { dao.deleteEvent(ev.getEventId()); load(); clr(); com.eventmanagementsystem.util.EventManager.notifyEventChanged(); }
        });
        clr.setOnAction(e->table.getSelectionModel().clearSelection());

        v.getChildren().addAll(new Label("Name"), txtName, new Label("Date"), d1, t1, d2, t2, new Label("Loc"), txtLoc, new Label("Desc"), txtDesc, new Separator(), new HBox(5, add, upd, del, clr));
        return v;
    }

    private void save(boolean isAdd) {
        if(txtName.getText().isEmpty() || d1.getValue()==null || d2.getValue()==null) { AlertUtil.error("Nhập đủ thông tin"); return; }
        LocalDateTime start=LocalDateTime.of(d1.getValue(), LocalTime.of(h1.getValue(), m1.getValue()));
        LocalDateTime end=LocalDateTime.of(d2.getValue(), LocalTime.of(h2.getValue(), m2.getValue()));
        if(end.isBefore(start)) { AlertUtil.error("Ngày kết thúc sai"); return; }

        Event e = isAdd ? new Event() : table.getSelectionModel().getSelectedItem();
        if(!isAdd && e==null) return;
        e.setName(txtName.getText()); e.setDate(start); e.setEndDate(end); e.setLocation(txtLoc.getText()); e.setDescription(txtDesc.getText());

        if(isAdd ? dao.addEvent(e) : dao.updateEvent(e)) {
            load(); if(isAdd) clr(); AlertUtil.success("OK", "Saved"); com.eventmanagementsystem.util.EventManager.notifyEventChanged();
        }
    }

    private void fill(Event e) {
        txtName.setText(e.getName()); txtLoc.setText(e.getLocation()); txtDesc.setText(e.getDescription());
        if(e.getDate()!=null) { d1.setValue(e.getDate().toLocalDate()); h1.getValueFactory().setValue(e.getDate().getHour()); m1.getValueFactory().setValue(e.getDate().getMinute()); }
        if(e.getEndDate()!=null) { d2.setValue(e.getEndDate().toLocalDate()); h2.getValueFactory().setValue(e.getEndDate().getHour()); m2.getValueFactory().setValue(e.getEndDate().getMinute()); }
    }
    private void clr() { txtName.clear(); txtLoc.clear(); txtDesc.clear(); d1.setValue(null); d2.setValue(null); }
    private void load() { Platform.runLater(()->table.getItems().setAll(dao.getAllEvents())); }
}