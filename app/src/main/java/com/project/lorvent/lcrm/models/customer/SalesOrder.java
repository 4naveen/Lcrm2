package com.project.lorvent.lcrm.models.customer;

/**
 * Created by Guest on 11/24/2016.
 */

public class SalesOrder {
      int id;
    String sales_number,date,customer,person,grand_total,status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSales_number() {
        return sales_number;
    }

    public void setSales_number(String sales_number) {
        this.sales_number = sales_number;
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

    public String getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(String grand_total) {
        this.grand_total = grand_total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
