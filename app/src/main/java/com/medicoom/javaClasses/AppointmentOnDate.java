package com.medicoom.javaClasses;

public class AppointmentOnDate {
    int rest_to_get;
    String med_id;
    String appointment_id;
    boolean is_forever;
    String comment;
    boolean is_got;
    String post_id;

    public AppointmentOnDate(int rest_to_get, String med_id, String appointment_id,
                             boolean is_forever, String comment, boolean is_got) {
        this.rest_to_get = rest_to_get;
        this.med_id = med_id;
        this.appointment_id = appointment_id;
        this.is_forever = is_forever;
        this.comment = comment;
        this.is_got = is_got;
    }

    public AppointmentOnDate() {
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
