package com.project.lorvent.lcrm.models;

/**
 * Created by Guest on 11/15/2016.
 */

public class LoggedCalls {
     int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String company,responsible,call_summary,date;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCall_summary() {
        return call_summary;
    }

    public void setCall_summary(String call_summary) {
        this.call_summary = call_summary;
    }
}
