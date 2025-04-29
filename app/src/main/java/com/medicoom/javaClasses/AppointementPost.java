package com.medicoom.javaClasses;

import java.util.List;

public class AppointementPost extends Appointment{
    String post_id;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public AppointementPost(int amount_at_once, boolean archive, int days, List<Integer> days_of_week,
                            boolean deleted, int every_x_days, String how_to_get, String medicine_id,
                            boolean notifications, boolean on_pause, int start_date, List<Integer> times,
                            String post_id) {
        super(amount_at_once, archive, days, days_of_week, deleted, every_x_days, how_to_get,
                medicine_id, notifications, on_pause, start_date, times);
        this.post_id = post_id;
    }
}
