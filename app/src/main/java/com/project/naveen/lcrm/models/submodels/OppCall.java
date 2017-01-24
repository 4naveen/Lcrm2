package com.project.naveen.lcrm.models.submodels;

/**
 * Created by Guest on 11/18/2016.
 */

public class OppCall{
String company,call_summary,date,responsible;
int call_id;
    public String getCall_summary() {
        return call_summary;
    }

    public void setCall_summary(String call_summary) {
        this.call_summary = call_summary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getCall_id() {
        return call_id;
    }

    public void setCall_id(int call_id) {
        this.call_id = call_id;
    }
}
