package com.eventmanagementsystem.ui.view;

import com.eventmanagementsystem.dao.*;
import com.eventmanagementsystem.model.*;
import com.eventmanagementsystem.ui.util.AlertUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.util.ArrayList;
import java.util.List;

public class GuestView extends BorderPane {
    private final GuestDAO dao = new GuestDAO();
    private final GuestEventDAO geDao = new GuestEventDAO();
    private final EventDAO evDao = new EventDAO();

    private final TableView<Guest> table = new TableView<>();
    private final TextField tfName = new TextField();
    private final TextField tfMail = new TextField();
    private final TextField tfJob = new TextField();
    private final ComboBox<Event> cb = new ComboBox<>();

    public GuestView() {
        var u = com.eventmanagementsystem.util.Session.getCurrentUser();
        if (u != null && "staff".equalsIgnoreCase(u.getRole())) {
            setCenter(new Label("Access Denied")); return;
        }

        TableColumn<Guest, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Guest, String> cEmail = new TableColumn<>("Email");
        cEmail.setCellValueFactory(d -> d.getValue().emailProperty());

        TableColumn<Guest, String> cJob = new TableColumn<>("Job");
        cJob.setCellValueFactory(d -> d.getValue().jobProperty());

        table.getColumns().addAll(cName, cEmail, cJob);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No Data"));

        table.getSelectionModel().selectedItemProperty().addListener((o, old, v) -> {
            if (v != null) fill(v); else clr();
        });

        com.eventmanagementsystem.util.EventManager.addEventChangeListener(() ->
                Platform.runLater(() -> { loadEvents(); filter(); })
        );

        setCenter(table);
        setRight(form());
        loadEvents();
        filter();
        setPadding(new Insets(10));
    }

    private VBox form() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(0, 0, 0, 10));
        v.setPrefWidth(300);

        tfName.setPromptText("Name");
        tfMail.setPromptText("Email");
        tfJob.setPromptText("Job");

        cb.setPromptText("Select Event");
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setConverter(new StringConverter<>() {
            public String toString(Event e) { return e == null ? "" : e.getName(); }
            public Event fromString(String s) { return null; }
        });

        Button btnAdd = new Button("Add"); btnAdd.setOnAction(e -> add());
        Button btnUpd = new Button("Update"); btnUpd.setOnAction(e -> update());
        Button btnDel = new Button("Delete"); btnDel.setOnAction(e -> delete());
        Button btnAsn = new Button("Assign"); btnAsn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;"); btnAsn.setOnAction(e -> assign());
        Button btnFlt = new Button("Filter"); btnFlt.setOnAction(e -> filter());
        Button btnRst = new Button("Reset"); btnRst.setOnAction(e -> { cb.getSelectionModel().selectFirst(); filter(); });

        HBox box1 = new HBox(5, btnAdd, btnUpd, btnDel);
        HBox box2 = new HBox(5, btnFlt, btnRst);

        v.getChildren().addAll(new Label("Info:"), tfName, tfMail, tfJob, box1, new Separator(), new Label("Event:"), cb, box2, btnAsn);
        return v;
    }

    private void filter() {
        Event e = cb.getValue();
        List<Guest> data;
        if (e != null && e.getEventId() > 0) data = geDao.getGuestsByEvent(e.getEventId());
        else data = dao.getAllGuests();
        table.setItems(FXCollections.observableArrayList(data));
        table.refresh();
    }

    private void loadEvents() {
        Event old = cb.getValue();
        ArrayList<Event> l = new ArrayList<>();
        l.add(new Event(-1, "-- ALL --", null, null, null, null));
        l.addAll(evDao.getAllEvents());
        cb.setItems(FXCollections.observableArrayList(l));
        if (old == null) cb.getSelectionModel().selectFirst();
        else {
            boolean found = false;
            for (Event ev : l) {
                if (ev.getEventId() == old.getEventId()) { cb.setValue(ev); found = true; break; }
            }
            if (!found) cb.getSelectionModel().selectFirst();
        }
    }

    private void add() {
        if (tfName.getText().trim().isEmpty()) { AlertUtil.error("Name required!"); return; }
        if (dao.addGuest(new Guest(tfName.getText(), tfMail.getText(), tfJob.getText()))) {
            filter(); clr(); AlertUtil.success("OK", "Added");
            com.eventmanagementsystem.util.EventManager.notifyEventChanged();
        }
    }

    private void update() {
        Guest g = table.getSelectionModel().getSelectedItem();
        if (g != null) {
            g.setName(tfName.getText()); g.setEmail(tfMail.getText()); g.setJob(tfJob.getText());
            if (dao.updateGuest(g)) { filter(); AlertUtil.success("OK", "Updated"); }
        }
    }

    private void delete() {
        Guest g = table.getSelectionModel().getSelectedItem();
        if (g != null && AlertUtil.confirm("Delete?")) {
            dao.deleteGuest(g.getGuestId()); filter(); clr();
            com.eventmanagementsystem.util.EventManager.notifyEventChanged();
        }
    }

    private void assign() {
        Guest g = table.getSelectionModel().getSelectedItem();
        Event ev = cb.getValue();
        if (g != null && ev != null && ev.getEventId() > 0) {
            if (geDao.assignGuestToEvent(g.getGuestId(), ev.getEventId())) AlertUtil.success("OK", "Assigned");
            else AlertUtil.error("Already assigned");
        } else AlertUtil.error("Select Guest & Event");
    }

    private void fill(Guest g) { tfName.setText(g.getName()); tfMail.setText(g.getEmail()); tfJob.setText(g.getJob()); }
    private void clr() { tfName.clear(); tfMail.clear(); tfJob.clear(); table.getSelectionModel().clearSelection(); }
}