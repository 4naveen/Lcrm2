package com.project.lorvent.lcrm.models.customer;

/**
 * Created by Guest on 11/24/2016.
 */

public class Invoice {
    int id;
    String invoice_number,invoice_date,customer,due_date,gTotal,Unpaid_amount,status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getgTotal() {
        return gTotal;
    }

    public void setgTotal(String gTotal) {
        this.gTotal = gTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnpaid_amount() {
        return Unpaid_amount;
    }

    public void setUnpaid_amount(String unpaid_amount) {
        Unpaid_amount = unpaid_amount;
    }

}
