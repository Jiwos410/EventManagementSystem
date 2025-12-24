package com.eventmanagementsystem.model;
public class User {
    private int userId; private String username, role;
    public User(int id, String u, String r){this.userId=id;this.username=u;this.role=r;}
    public int getUserId(){return userId;}
    public String getUsername(){return username;}
    public String getRole(){return role;}
}