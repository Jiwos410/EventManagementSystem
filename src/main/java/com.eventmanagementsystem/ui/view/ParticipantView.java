package com.eventmanagementsystem.ui.view;

import com.eventmanagementsystem.dao.*;
import com.eventmanagementsystem.model.*;
import com.eventmanagementsystem.ui.util.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ParticipantView extends BorderPane {
    private final ParticipantDAO dao = new ParticipantDAO();
    private final ParticipantEventDAO peDao = new ParticipantEventDAO();
    private final EventDAO evDao = new EventDAO();

    private final TableView<Participant> table = new TableView<>();
    private final TextField tfName = new TextField();
    private final ComboBox<Event> cb = new ComboBox<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ParticipantView() {
        // CẤU HÌNH CỘT BẢNG
        TableColumn<Participant, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Participant, String> cIn = new TableColumn<>("In");
        cIn.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCheckIn() != null ? d.getValue().getCheckIn().format(dtf) : ""));

        TableColumn<Participant, String> cOut = new TableColumn<>("Out");
        cOut.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCheckOut() != null ? d.getValue().getCheckOut().format(dtf) : ""));

        table.getColumns().addAll(cName, cIn, cOut);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No Data"));

        table.getSelectionModel().selectedItemProperty().addListener((o, old, v) ->
                tfName.setText(v != null ? v.getName() : "")
        );

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

        cb.setConverter(new StringConverter<>() {
            public String toString(Event e) { return e == null ? "" : e.getName(); }
            public Event fromString(String s) { return null; }
        });

        Button add = new Button("Add");
        add.setOnAction(e -> {
            if (dao.addParticipant(new Participant(tfName.getText()))) {
                filter(); tfName.clear();
                com.eventmanagementsystem.util.EventManager.notifyEventChanged();
            }
        });

        Button del = new Button("Delete");
        del.setOnAction(e -> {
            Participant p = table.getSelectionModel().getSelectedItem();
            if (p != null && AlertUtil.confirm("Delete?")) {
                dao.deleteParticipant(p.getParticipantId());
                filter(); tfName.clear();
                com.eventmanagementsystem.util.EventManager.notifyEventChanged();
            }
        });

        Button asn = new Button("Assign");
        asn.setOnAction(e -> {
            Participant p = table.getSelectionModel().getSelectedItem();
            Event ev = cb.getValue();
            if (p != null && ev != null && ev.getEventId() > 0) {
                if(peDao.assignParticipantToEvent(p.getParticipantId(), ev.getEventId())) AlertUtil.success("OK", "Assigned");
                else AlertUtil.error("Error");
            } else AlertUtil.error("Select Participant & Event");
        });

        Button flt = new Button("Filter"); flt.setOnAction(e -> filter());
        Button rst = new Button("Reset"); rst.setOnAction(e -> { cb.getSelectionModel().selectFirst(); filter(); });

        Button in = new Button("In");
        in.setStyle("-fx-background-color:#10b981; -fx-text-fill:white");
        in.setOnAction(e -> {
            Participant p = table.getSelectionModel().getSelectedItem();
            if (p != null && dao.checkIn(p.getParticipantId())) {
                filter(); com.eventmanagementsystem.util.EventManager.notifyEventChanged();
            }
        });

        Button out = new Button("Out");
        out.setStyle("-fx-background-color:#f59e0b; -fx-text-fill:white");
        out.setOnAction(e -> {
            Participant p = table.getSelectionModel().getSelectedItem();
            if (p != null && dao.checkOut(p.getParticipantId())) {
                filter(); com.eventmanagementsystem.util.EventManager.notifyEventChanged();
            }
        });

        var u = com.eventmanagementsystem.util.Session.getCurrentUser();
        if (u != null && "staff".equalsIgnoreCase(u.getRole())) {
            add.setDisable(true); del.setDisable(true);
        }

        v.getChildren().addAll(
                new Label("Info"), tfName, new HBox(5, add, del),
                new Separator(),
                new Label("Event"), cb, new HBox(5, flt, asn, rst),
                new Separator(), new HBox(5, in, out)
        );
        return v;
    }

    private void filter() {
        Event e = cb.getValue();
        List<Participant> data;
        if (e != null && e.getEventId() > 0) {
            data = peDao.getParticipantsByEvent(e.getEventId());
        } else {
            data = dao.getAllParticipants();
        }
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
            for (Event ev : l) if (ev.getEventId() == old.getEventId()) { cb.setValue(ev); break; }
        }
    }
}