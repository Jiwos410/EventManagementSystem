package com.eventmanagementsystem.ui.view;
import com.eventmanagementsystem.dao.UserDAO;
import com.eventmanagementsystem.model.User;
import com.eventmanagementsystem.ui.util.AlertUtil;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class LoginView extends BorderPane {
    private final UserDAO dao = new UserDAO();
    private final ProgressIndicator loading = new ProgressIndicator();
    public LoginView(Consumer<User> onSuccess) {
        setPadding(new Insets(30));
        VBox box = new VBox(10); box.setMaxWidth(300); box.setAlignment(Pos.CENTER);
        Label title = new Label("LOGIN SYSTEM"); title.setStyle("-fx-font-size:20px; -fx-font-weight:bold");
        TextField u = new TextField(); u.setPromptText("Username");
        PasswordField p = new PasswordField(); p.setPromptText("Password");
        Button btn = new Button("Login"); btn.setMaxWidth(Double.MAX_VALUE); btn.setDefaultButton(true);
        loading.setVisible(false);

        btn.setOnAction(e -> {
            if(u.getText().isEmpty() || p.getText().isEmpty()) { AlertUtil.error("Nhập đủ thông tin"); return; }
            loading.setVisible(true); btn.setDisable(true);
            Task<User> t = new Task<>() { @Override protected User call() { return dao.login(u.getText(), p.getText()); } };
            t.setOnSucceeded(ev -> {
                loading.setVisible(false); btn.setDisable(false);
                if(t.getValue() != null) onSuccess.accept(t.getValue());
                else AlertUtil.error("Sai tài khoản/mật khẩu");
            });
            t.setOnFailed(ev -> { loading.setVisible(false); btn.setDisable(false); AlertUtil.error("Lỗi DB"); });
            new Thread(t).start();
        });
        box.getChildren().addAll(title, new Label("User:"), u, new Label("Pass:"), p, btn, loading);
        setCenter(box);
    }
}