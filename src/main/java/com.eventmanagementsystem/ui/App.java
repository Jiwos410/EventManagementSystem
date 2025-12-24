package com.eventmanagementsystem.ui;
import com.eventmanagementsystem.model.User;
import com.eventmanagementsystem.ui.view.*;
import com.eventmanagementsystem.util.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private Stage stg;
    @Override public void start(Stage s) {
        this.stg=s;
        if(!DBUtil.testConnection()){ new Alert(Alert.AlertType.ERROR,"Database Error").showAndWait(); return; }
        login();
    }
    private void login() {
        EventManager.clearListeners();
        stg.setScene(new Scene(new LoginView(u->{Session.setCurrentUser(u); main(u);}), 400, 300));
        stg.setTitle("Event System"); stg.centerOnScreen(); stg.show();
    }
    private void main(User u) {
        BorderPane root = new BorderPane();
        HBox head = new HBox(15); head.setStyle("-fx-background-color:#2c3e50;-fx-padding:10"); head.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("EVENT MANAGER"); title.setStyle("-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:16");
        Label user = new Label(u.getUsername()+" ["+u.getRole()+"]"); user.setStyle("-fx-text-fill:#bdc3c7;-fx-font-style:italic");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button out = new Button("Logout"); out.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white");
        out.setOnAction(e->{Session.clear(); login();});
        head.getChildren().addAll(title, sp, user, out);

        TabPane tabs = new TabPane();
        DashboardView db = new DashboardView();
        Tab t1=new Tab("Dashboard", db); t1.setClosable(false);
        Tab t2=new Tab("Event", "admin".equals(u.getRole())?new EventView():new StaffEventView()); t2.setClosable(false);
        Tab t3=new Tab("Guest", "admin".equals(u.getRole())?new GuestView():new StaffGuestView()); t3.setClosable(false);
        Tab t4=new Tab("Participant", "admin".equals(u.getRole())?new ParticipantView():new StaffParticipantView()); t4.setClosable(false);
        tabs.getTabs().addAll(t1,t2,t3,t4);
        if("admin".equals(u.getRole())){ Tab t5=new Tab("Employee", new EmployeeView()); t5.setClosable(false); tabs.getTabs().add(t5); }

        tabs.getSelectionModel().selectedItemProperty().addListener((o,old,nw)->{if(nw==t1)db.reload();});
        root.setTop(head); root.setCenter(tabs);
        stg.setScene(new Scene(root, 1100, 700)); stg.centerOnScreen();
    }
    public static void main(String[] args) { launch(args); }
}