package com.medicoom.utils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentOnDate;

import java.util.Calendar;
import java.util.List;

public class SetAppointmentsOnDates implements Runnable {
    Appointment appointment;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public SetAppointmentsOnDates(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public void run() {
        boolean is_forever;
        int days;
        if (appointment.getDays() == -1) { // Назначение вечное
            is_forever = true;
            days = 90;
        } else {
            is_forever = false;
            days = appointment.getDays();
        }
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(appointment.getStart_date() * 1000L);
        if (appointment.getEvery_x_days() == 1) { // Принимать каждый день
            for (int i = 0; i < days; i++) {
                for (int time : appointment.getTimes()) {
                    AppointmentOnDate app = new AppointmentOnDate(
                            appointment.getDays() - i, appointment.getMedicine_id(),
                            appointment.getPost_id(), is_forever, null, false);
                    writePost((int) (startDate.getTimeInMillis() / 1000L), time, app);
                }
                startDate.add(Calendar.DATE, 1);
            }
        } else if (appointment.getDays_of_week() != null && !appointment.getDays_of_week().isEmpty()) { // Принимать по дням недели
            for (int i = 0; i < days; i++) {
                if (isCorrectDayOfWeek(appointment.getDays_of_week(), startDate.get(Calendar.DAY_OF_WEEK))) {
                    for (int time : appointment.getTimes()) {
                        AppointmentOnDate app = new AppointmentOnDate(
                                appointment.getDays() - i, appointment.getMedicine_id(),
                                appointment.getPost_id(), is_forever, null, false);
                        writePost((int) (startDate.getTimeInMillis() / 1000L), time, app);
                    }
                }
                startDate.add(Calendar.DATE, 1);
            }
        } else { // Принимать каждые х дней
            int i = 0;
            while (i < days) {
                for (int time : appointment.getTimes()) {
                    AppointmentOnDate app = new AppointmentOnDate(
                            appointment.getDays() - i, appointment.getMedicine_id(),
                            appointment.getPost_id(), is_forever, null, false);
                    writePost((int) (startDate.getTimeInMillis() / 1000L), time, app);
                    startDate.add(Calendar.DATE, appointment.getEvery_x_days());
                    i += appointment.getEvery_x_days();
                }
            }
        }
    }


    private void writePost(int date, int time, AppointmentOnDate new_app) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/appointments_on_dates");
        mDatabase.child(String.valueOf(date)).child(String.valueOf(time)).push().setValue(new_app)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    private boolean isCorrectDayOfWeek(List<Integer> right_dows, int curr_dow) {
        boolean is_correct = false;
        if (curr_dow == 1) {
            curr_dow = 6;
        } else {
            curr_dow -= 2;
        }
        for (Integer x : right_dows) {
            if (x.equals(curr_dow)) {
                is_correct = true;
                break;
            }
        }
        return is_correct;
    }
}
