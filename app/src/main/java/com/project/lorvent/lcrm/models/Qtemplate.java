package com.project.lorvent.lcrm.models;

/**
 * Created by Guest on 11/4/2016.
 */

public class Qtemplate {
    int id;
    String quatation_template,quatation_duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuatation_template() {
        return quatation_template;
    }

    public void setQuatation_template(String quatation_template) {
        this.quatation_template = quatation_template;
    }

    public String getQuatation_duration() {
        return quatation_duration;
    }

    public void setQuatation_duration(String quatation_duration) {
        this.quatation_duration = quatation_duration;
    }
}
