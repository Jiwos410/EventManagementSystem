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

public class EmployeeView extends BorderPane {
    private final EmployeeDAO dao = new EmployeeDAO();
    private final EventDAO evDao = new EventDAO();
    private final EventEmployeeDAO eeDao = new EventEmployeeDAO();

    private final TableView<Employee> table = new TableView<>();
    private final TextField tfName = new TextField();
    private final TextField tfRole = new TextField();
    private final ComboBox<Event> cb = new ComboBox<>();

    public EmployeeView() {
        var u = com.eventmanagementsystem.util.Session.getCurrentUser();
        if (u != null && "staff".equalsIgnoreCase(u.getRole())) {
            setCenter(new Label("Access Denied")); return;
        }

        TableColumn<Employee, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<Employee, String> cRole = new TableColumn<>("Role");
        cRole.setCellValueFactory(d -> d.getValue().roleProperty());

        table.getColumns().addAll(cName, cRole);
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
        tfRole.setPromptText("Role");

        cb.setPromptText("Select Event");
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setConverter(new StringConverter<>() {
            public String toString(Event e) { return e == null ? "" : e.getName(); }
            public Event fromString(String s) { return null; }
        });

        Button btnAdd = new Button("Add");
        btnAdd.setOnAction(e -> add());

        Button btnUpd = new Button("Update");
        btnUpd.setOnAction(e -> update());

        Button btnDel = new Button("Delete");
        btnDel.setOnAction(e -> delete());

        Button btnAsn = new Button("Assign");
        btnAsn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
        btnAsn.setOnAction(e -> assign());

        Button btnFlt = new Button("Filter");
        btnFlt.setOnAction(e -> filter());

        Button btnRst = new Button("Reset");
        btnRst.setOnAction(e -> { cb.getSelectionModel().selectFirst(); filter(); });

        HBox box1 = new HBox(5, btnAdd, btnUpd, btnDel);
        HBox box2 = new HBox(5, btnFlt, btnRst);

        v.getChildren().addAll(
                new Label("Info:"), tfName, tfRole, box1,
                new Separator(),
                new Label("Event:"), cb, box2, btnAsn
        );
        return v;
    }

    private void filter() {
        Event e = cb.getValue();
        List<Employee> data;
        if (e != null && e.getEventId() > 0) {
            data = eeDao.getEmployeesByEvent(e.getEventId());
        } else {
            data = dao.getAllEmployees();
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
            boolean found = false;
            for (Event ev : l) {
                if (ev.getEventId() == old.getEventId()) { cb.setValue(ev); found = true; break; }
            }
            if (!found) cb.getSelectionModel().selectFirst();
        }
    }

    private void add() {
        if (tfName.getText().trim().isEmpty()) { AlertUtil.error("Name required!"); return; }
        if (dao.addEmployee(new Employee(tfName.getText(), tfRole.getText()))) {
            filter(); clr(); AlertUtil.success("OK", "Added");
            com.eventmanagementsystem.util.EventManager.notifyEventChanged();
        }
    }

    private void update() {
        Employee e = table.getSelectionModel().getSelectedItem();
        if (e != null) {
            e.setName(tfName.getText());
            e.setRole(tfRole.getText());
            if (dao.updateEmployee(e)) { filter(); AlertUtil.success("OK", "Updated"); }
        }
    }

    private void delete() {
        Employee e = table.getSelectionModel().getSelectedItem();
        if (e != null && AlertUtil.confirm("Delete?")) {
            dao.deleteEmployee(e.getEmployeeId()); filter(); clr();
            com.eventmanagementsystem.util.EventManager.notifyEventChanged();
        }
    }

    private void assign() {
        Employee em = table.getSelectionModel().getSelectedItem();
        Event ev = cb.getValue();
        if (em != null && ev != null && ev.getEventId() > 0) {
            if (eeDao.assignEmployeeToEvent(ev.getEventId(), em.getEmployeeId())) AlertUtil.success("OK", "Assigned");
            else AlertUtil.error("Already assigned");
        } else {
            AlertUtil.error("Select Employee & Event");
        }
    }

    private void fill(Employee e) {
        tfName.setText(e.getName());
        tfRole.setText(e.getRole());
    }

    private void clr() {
        tfName.clear();
        tfRole.clear();
        table.getSelectionModel().clearSelection();
    }
}