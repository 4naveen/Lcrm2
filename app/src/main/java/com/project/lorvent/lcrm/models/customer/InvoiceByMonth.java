package com.project.lorvent.lcrm.models.customer;

/**
 * Created by User on 5/12/2017.
 */

public class InvoiceByMonth {

    String month,year;
    int invoices,contracts,opportunity,leads;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getInvoices() {
        return invoices;
    }

    public void setInvoices(int invoices) {
        this.invoices = invoices;
    }

    public int getContracts() {
        return contracts;
    }

    public void setContracts(int contracts) {
        this.contracts = contracts;
    }

    public int getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(int opportunity) {
        this.opportunity = opportunity;
    }

    public int getLeads() {
        return leads;
    }

    public void setLeads(int leads) {
        this.leads = leads;
    }
}
