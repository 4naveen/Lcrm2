package com.project.naveen.lcrm.models;

/**
 * Created by Guest on 10/22/2016.
 */

public class SalesTeam {

       int id;
        String salesteam;
            String target;
            String invoice_forecast;
            String actual_invoice;
    String team_leader,quotation,leads,opportunities,notes,userId,created_at,updated_at,deleted_at;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeam_leader() {
        return team_leader;
    }

    public void setTeam_leader(String team_leader) {
        this.team_leader = team_leader;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getLeads() {
        return leads;
    }

    public void setLeads(String leads) {
        this.leads = leads;
    }

    public String getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(String opportunities) {
        this.opportunities = opportunities;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getSalesteam() {
        return salesteam;

    }

    public void setSalesteam(String salesteam) {
        this.salesteam = salesteam;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getInvoice_forecast() {
        return invoice_forecast;
    }

    public void setInvoice_forecast(String invoice_forecast) {
        this.invoice_forecast = invoice_forecast;
    }

    public String getActual_invoice() {
        return actual_invoice;
    }

    public void setActual_invoice(String actual_invoice) {
        this.actual_invoice = actual_invoice;
    }
}
