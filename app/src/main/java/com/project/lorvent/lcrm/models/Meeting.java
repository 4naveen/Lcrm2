package com.project.lorvent.lcrm.models;

/**
 * Created by Guest on 11/2/2016.
 */

public class Meeting {
    int id;
    String meeting_subject,starting_date,ending_date,responsible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMeeting_subject() {
        return meeting_subject;
    }

    public void setMeeting_subject(String meeting_subject) {
        this.meeting_subject = meeting_subject;
    }

    public String getStarting_date() {
        return starting_date;
    }

    public void setStarting_date(String starting_date) {
        this.starting_date = starting_date;
    }

    public String getEnding_date() {
        return ending_date;
    }

    public void setEnding_date(String ending_date) {
        this.ending_date = ending_date;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }
}
