package com.eventmanagementsystem.ui.util;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class AlertUtil {
    public static void success(String t, String m) { show(Alert.AlertType.INFORMATION, t, m); }
    public static void error(String m) { show(Alert.AlertType.ERROR, "Error", m); }
    public static void info(String m) { show(Alert.AlertType.INFORMATION, "Info", m); }
    public static boolean confirm(String m) {
        return new Alert(Alert.AlertType.CONFIRMATION, m, ButtonType.YES, ButtonType.NO).showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
    private static void show(Alert.AlertType type, String t, String m) {
        Alert a = new Alert(type); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}