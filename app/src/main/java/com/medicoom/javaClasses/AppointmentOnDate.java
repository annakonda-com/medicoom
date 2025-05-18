package com.medicoom.javaClasses;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AppointmentOnDate {
    int rest_to_get;
    String med_id;
    String appointment_id;
    boolean is_forever;
    String comment;
    boolean is_got;
    String post_id;
    int got_time;

    public AppointmentOnDate(int rest_to_get, String med_id, String appointment_id,
                             boolean is_forever, String comment, boolean is_got, int got_time) {
        this.rest_to_get = rest_to_get;
        this.med_id = med_id;
        this.appointment_id = appointment_id;
        this.is_forever = is_forever;
        this.comment = comment;
        this.is_got = is_got;
        this.got_time = got_time;
    }

    public AppointmentOnDate() {
    }
    public AppointmentOnDate(Bundle data) {
        this.rest_to_get = data.getInt("rest_to_get");
        this.med_id = data.getString("med_id");
        this.appointment_id = data.getString("appointment_id");
        this.is_forever = data.getBoolean("is_forever");
        this.comment = data.getString("comment");
        this.is_got = data.getBoolean("is_got");
        this.got_time = data.getInt("got_time");
        this.post_id = data.getString("post_id");
    }

    public Bundle makeBundle () {
        Bundle app_info = new Bundle();
        app_info.putInt("rest_to_get", this.rest_to_get);
        app_info.putString("med_id", this.med_id);
        app_info.putString("appointment_id", this.appointment_id);
        app_info.putBoolean("is_forever", this.is_forever);
        app_info.putString("comment", this.comment);
        app_info.putBoolean("is_got", this.is_got);
        app_info.putInt("got_time", this.got_time);
        app_info.putString("post_id", this.post_id);
        return app_info;
    }

    public AppointmentOnDate(AppointmentOnDate app){
        this.rest_to_get = app.getRest_to_get();
        this.med_id = app.getMed_id();
        this.appointment_id = app.getAppointment_id();
        this.is_forever = app.isIs_forever();
        this.comment = app.getComment();
        this.is_got = app.isIs_got();
        this.got_time = app.getGot_time();
        this.post_id = app.getPost_id();
    }


    public int getGot_time() {
        return got_time;
    }

    public void setGot_time(int got_time) {
        this.got_time = got_time;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isIs_got() {
        return is_got;
    }

    public void setIs_got(boolean is_got) {
        this.is_got = is_got;
    }

    public int getRest_to_get() {
        return rest_to_get;
    }

    public void setRest_to_get(int rest_to_get) {
        this.rest_to_get = rest_to_get;
    }

    public String getMed_id() {
        return med_id;
    }

    public void setMed_id(String med_id) {
        this.med_id = med_id;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public boolean isIs_forever() {
        return is_forever;
    }

    public void setIs_forever(boolean is_forever) {
        this.is_forever = is_forever;
    }

    @Override
    public String toString() {
        return "AppointmentOnDate{" +
                "appointment_id='" + appointment_id + '\'' +
                ", rest_to_get=" + rest_to_get +
                ", med_id='" + med_id + '\'' +
                ", is_forever=" + is_forever +
                ", comment='" + comment + '\'' +
                ", is_got=" + is_got +
                '}';
    }
}
