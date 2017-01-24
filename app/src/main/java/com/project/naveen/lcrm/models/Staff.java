package com.project.naveen.lcrm.models;

/**
 * Created by Guest on 9/28/2016.
 */
public class Staff {
    private String name;
    private int id;
    private String email;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}