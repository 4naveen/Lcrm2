package com.project.naveen.lcrm.models.submodels;

/**
 * Created by Guest on 11/18/2016.
 */

public class OppMeeting {
int meeting_id;
String meeting_subject,start_date,end_date,responsible;

    public String getMeeting_subject() {
        return meeting_subject;
    }

    public void setMeeting_subject(String meeting_subject) {
        this.meeting_subject = meeting_subject;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public int getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(int meeting_id) {
        this.meeting_id = meeting_id;
    }
}
