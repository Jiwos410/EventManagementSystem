package com.eventmanagementsystem.util;
import com.eventmanagementsystem.model.User;
import java.util.concurrent.atomic.AtomicReference;

public final class Session {
    private static final AtomicReference<User> current = new AtomicReference<>(null);
    private Session() {}
    public static void setCurrentUser(User u) { current.set(u); }
    public static User getCurrentUser() { return current.get(); }
    public static void clear() { current.set(null); }
}