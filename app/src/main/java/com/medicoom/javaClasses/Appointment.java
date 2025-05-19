package com.medicoom.javaClasses;

import java.util.List;

public class Appointment {
    String medicine_id;
    String how_to_get;
    int amount_at_once;
    int start_date;
    int days;
    List<Integer> days_of_week;
    List<Integer> times;
    int every_x_days;
    boolean notifications;
    boolean on_pause;
    boolean archive;
    boolean deleted;
    String post_id;

    public Appointment(int amount_at_once, boolean archive, int days, List<Integer> days_of_week,
                       boolean deleted, int every_x_days, String how_to_get, String medicine_id,
                       boolean notifications, boolean on_pause, int start_date, List<Integer> times) {
        this.amount_at_once = amount_at_once;
        this.archive = archive;
        this.days = days;
        this.days_of_week = days_of_week;
        this.deleted = deleted;
        this.every_x_days = every_x_days;
        this.how_to_get = how_to_get;
        this.medicine_id = medicine_id;
        this.notifications = notifications;
        this.on_pause = on_pause;
        this.start_date = start_date;
        this.times = times;
    }

    public Appointment() {

    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }


    public int getAmount_at_once() {
        return amount_at_once;
    }

    public void setAmount_at_once(int amount_at_once) {
        this.amount_at_once = amount_at_once;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<Integer> getDays_of_week() {
        return days_of_week;
    }

    public void setDays_of_week(List<Integer> days_of_week) {
        this.days_of_week = days_of_week;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getEvery_x_days() {
        return every_x_days;
    }

    public void setEvery_x_days(int every_x_days) {
        this.every_x_days = every_x_days;
    }

    public String getHow_to_get() {
        return how_to_get;
    }

    public void setHow_to_get(String how_to_get) {
        this.how_to_get = how_to_get;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isOn_pause() {
        return on_pause;
    }

    public void setOn_pause(boolean on_pause) {
        this.on_pause = on_pause;
    }

    public int getStart_date() {
        return start_date;
    }

    public void setStart_date(int start_date) {
        this.start_date = start_date;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "amount_at_once=" + amount_at_once +
                ", medicine_id='" + medicine_id + '\'' +
                ", how_to_get='" + how_to_get + '\'' +
                ", start_date=" + start_date +
                ", days=" + days +
                ", days_of_week=" + days_of_week +
                ", times=" + times +
                ", every_x_days=" + every_x_days +
                ", notifications=" + notifications +
                ", on_pause=" + on_pause +
                ", archive=" + archive +
                ", deleted=" + deleted +
                ", post_id='" + post_id + '\'' +
                '}';
    }
}
