package com.project.lorvent.lcrm.models.customer;

/**
 * Created by Guest on 11/23/2016.
 */

public class Quotation {
    int id;
    String quotations_number,date,customer,final_price,person,status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuotations_number() {
        return quotations_number;
    }

    public void setQuotations_number(String quotations_number) {
        this.quotations_number = quotations_number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getFinal_price() {
        return final_price;
    }

    public void setFinal_price(String final_price) {
        this.final_price = final_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
