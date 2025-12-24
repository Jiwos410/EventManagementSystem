package com.eventmanagementsystem.util;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static final List<Runnable> listeners = new ArrayList<>();
    public static void addEventChangeListener(Runnable l) { if(l!=null && !listeners.contains(l)) listeners.add(l); }
    public static void notifyEventChanged() { Platform.runLater(() -> { for(Runnable l : new ArrayList<>(listeners)) try { l.run(); } catch(Exception e){} }); }
    public static void clearListeners() { listeners.clear(); }
}