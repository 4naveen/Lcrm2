package com.project.naveen.lcrm.models;

/**
 * Created by Guest on 9/19/2016.
 */
public class Leads {
    String opportunity,email,sales_team;
    int id;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(String opportunity) {
        this.opportunity = opportunity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSales_team() {
        return sales_team;
    }

    public void setSales_team(String sales_team) {
        this.sales_team = sales_team;
    }
}
