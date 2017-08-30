package com.project.lorvent.lcrm.models;

/**
 * Created by Guest on 11/21/2016.
 */

public class Invoice {
   int id;
    String customer,invoice_date,status,unpaid_amount,invoice_number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnpaid_amount() {
        return unpaid_amount;
    }

    public void setUnpaid_amount(String unpaid_amount) {
        this.unpaid_amount = unpaid_amount;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }
}
