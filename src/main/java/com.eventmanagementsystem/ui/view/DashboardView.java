package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.*;
import com.eventmanagementsystem.model.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Map;
import java.util.List;

public class DashboardView extends BorderPane {
    private final StatsDAO stats = new StatsDAO();
    private final GuestEventDAO geDao = new GuestEventDAO();
    private final Label[] lbls = {new Label("0"), new Label("0"), new Label("0"), new Label("0")};
    private final TableView<Event> tblRec = new TableView<>();
    private final StackPane[] charts = {new StackPane(), new StackPane(), new StackPane(), new StackPane()};
    private final ComboBox<Event> cb = new ComboBox<>();
    private final TableView<Guest> tblG = new TableView<>();
    private final ProgressIndicator pi = new ProgressIndicator();

    public DashboardView() {
        setPadding(new Insets(10)); pi.setVisible(false); pi.setMaxSize(20,20);
        VBox left = new VBox(10); left.getChildren().add(topCards());
        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(10);
        grid.add(box("TRẠNG THÁI", charts[0]), 0, 0); grid.add(box("KHÁCH MỜI", charts[1]), 1, 0);
        grid.add(box("CHECK-IN", charts[2]), 0, 1); grid.add(box("NHÂN SỰ", charts[3]), 1, 1);
        ColumnConstraints c = new ColumnConstraints(); c.setPercentWidth(50); grid.getColumnConstraints().addAll(c,c);
        left.getChildren().add(grid); VBox.setVgrow(grid, Priority.ALWAYS);

        VBox right = rightPane();
        HBox main = new HBox(15, left, right); HBox.setHgrow(left, Priority.ALWAYS);
        setCenter(main);
        com.eventmanagementsystem.util.EventManager.addEventChangeListener(() -> Platform.runLater(this::reload));
        reload();
    }

    private VBox rightPane() {
        VBox v = new VBox(10); v.setPrefWidth(320);
        TableColumn<Event,String> c1=new TableColumn<>("Event"); c1.setCellValueFactory(d->d.getValue().nameProperty());
        tblRec.getColumns().add(c1); tblRec.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblRec.getSelectionModel().selectedItemProperty().addListener((o,old,val)->{if(val!=null) cb.setValue(val);});

        TableColumn<Guest,String> c2=new TableColumn<>("Guest"); c2.setCellValueFactory(d->d.getValue().nameProperty());
        tblG.getColumns().add(c2); tblG.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblG.setPlaceholder(new Label("No data"));

        cb.setMaxWidth(Double.MAX_VALUE);
        cb.valueProperty().addListener((o,old,val)->{
            if(val!=null) { updateSubCharts(val); loadG(val); } else tblG.getItems().clear();
        });

        Button btn = new Button("Refresh"); btn.setMaxWidth(Double.MAX_VALUE); btn.setOnAction(e->reload());
        v.getChildren().addAll(new HBox(5, new Label("Recent"), pi), tblRec, new Separator(), new Label("Filter"), cb, tblG, btn);
        VBox.setVgrow(tblRec, Priority.ALWAYS); VBox.setVgrow(tblG, Priority.ALWAYS);
        return v;
    }

    private HBox topCards() {
        String[] colors = {"#3b82f6", "#10b981", "#f59e0b", "#6366f1"};
        String[] titles = {"Events", "Guests", "Participants", "Employees"};
        HBox h = new HBox(10);
        for(int i=0;i<4;i++) {
            VBox b = new VBox(5, new Label(titles[i]), lbls[i]);
            b.setStyle("-fx-background-color:white;-fx-padding:10;-fx-border-color:"+colors[i]+";-fx-border-width:0 0 0 4");
            b.setPrefWidth(150); lbls[i].setStyle("-fx-font-size:18px;-fx-font-weight:bold");
            h.getChildren().add(b);
        }
        return h;
    }

    private VBox box(String t, StackPane p) {
        VBox v = new VBox(5, new Label(t), p);
        v.setStyle("-fx-background-color:white;-fx-padding:10;-fx-background-radius:5");
        v.setAlignment(Pos.CENTER); VBox.setVgrow(p, Priority.ALWAYS); v.setMinHeight(180);
        return v;
    }

    public void reload() {
        pi.setVisible(true);
        Task<Void> t = new Task<>() {
            int e,g,p,emp; ObservableList<Event> rec, all;
            Map<String,Integer> s1,s4,s2,s3;
            @Override protected Void call() {
                e=stats.countEvents(); g=stats.countGuests(); p=stats.countParticipants(); emp=stats.countEmployees();
                rec=FXCollections.observableArrayList(stats.recentEvents(15));
                all=FXCollections.observableArrayList(); all.add(new Event(-1,"-- ALL --",null,null,null,null));
                all.addAll(new com.eventmanagementsystem.dao.EventDAO().getAllEvents());

                s1=stats.getEventStatusStats();
                s4=stats.getEmployeeRoleStats();
                s2=stats.getGuestTypeStats();
                s3=stats.getCheckinStats();
                return null;
            }
            @Override protected void succeeded() {
                lbls[0].setText(e+""); lbls[1].setText(g+""); lbls[2].setText(p+""); lbls[3].setText(emp+"");
                tblRec.setItems(rec);
                Event sel = cb.getValue(); cb.setItems(all);
                if(sel!=null) for(Event ev:all) if(ev.getEventId()==sel.getEventId()) cb.setValue(ev);
                if(cb.getValue()==null) cb.getSelectionModel().selectFirst();

                draw(charts[0], s1);

                if(cb.getValue().getEventId()==-1) {
                    draw(charts[1], s2);
                    draw(charts[2], s3);
                    draw(charts[3], s4);
                }
                else updateSubCharts(cb.getValue());
                pi.setVisible(false);
            }
        }; new Thread(t).start();
    }

    private void updateSubCharts(Event e) {
        if(e==null) return;
        Task<Void> t = new Task<>() {
            Map<String,Integer> s2,s3,s4;
            @Override protected Void call() {
                if(e.getEventId()>0) {
                    s2=stats.getGuestTypeStatsByEvent(e.getEventId());
                    s3=stats.getCheckinStatsByEvent(e.getEventId());
                    s4=stats.getEmployeeRoleStatsByEvent(e.getEventId());
                } else {
                    s2=stats.getGuestTypeStats();
                    s3=stats.getCheckinStats();
                    s4=stats.getEmployeeRoleStats();
                }
                return null;
            }
            @Override protected void succeeded() {
                draw(charts[1], s2);
                draw(charts[2], s3);
                draw(charts[3], s4);
            }
        }; new Thread(t).start();
    }

    private void loadG(Event e) {
        if(e==null||e.getEventId()<=0) { tblG.getItems().clear(); return; }
        new Thread(new Task<List<Guest>>() {
            @Override protected List<Guest> call() { return geDao.getGuestsByEvent(e.getEventId()); }
            @Override protected void succeeded() { tblG.setItems(FXCollections.observableArrayList(getValue())); }
        }).start();
    }

    private void draw(StackPane p, Map<String,Integer> m) {
        p.getChildren().clear();
        if(m==null || m.isEmpty() || m.values().stream().mapToInt(Integer::intValue).sum()==0) {
            Label l = new Label("No data"); l.setStyle("-fx-text-fill:#cbd5e1");
            p.getChildren().add(l); return;
        }
        PieChart pc = new PieChart();
        m.forEach((k,v)->{if(v>0) pc.getData().add(new PieChart.Data(k+" ("+v+")",v));});
        pc.setLegendVisible(false); p.getChildren().add(pc);
    }
}