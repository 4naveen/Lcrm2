package com.project.lorvent.lcrm.models;

/**
 * Created by Guest on 10/21/2016.
 */

public class Opportunity {
    int id;
    private String opportunity,company,next_action_date,stages,expected_revenue,probability,salesTeam,calls,meetings;

    public String getExpected_revenue() {
        return expected_revenue;
    }

    public void setExpected_revenue(String expected_revenue) {
        this.expected_revenue = expected_revenue;
    }

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNext_action_date() {
        return next_action_date;
    }

    public void setNext_action_date(String next_action_date) {
        this.next_action_date = next_action_date;
    }

    public String getStages() {
        return stages;
    }

    public void setStages(String stages) {
        this.stages = stages;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getSalesTeam() {
        return salesTeam;
    }

    public void setSalesTeam(String salesTeam) {
        this.salesTeam = salesTeam;
    }

    public String getCalls() {
        return calls;
    }

    public void setCalls(String calls) {
        this.calls = calls;
    }

    public String getMeetings() {
        return meetings;
    }

    public void setMeetings(String meetings) {
        this.meetings = meetings;
    }





}
