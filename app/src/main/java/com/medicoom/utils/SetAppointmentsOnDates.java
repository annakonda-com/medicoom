package com.medicoom.utils;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.javaClasses.AppointementPost;
import com.medicoom.javaClasses.AppointmentOnDate;
import com.medicoom.javaClasses.Medicine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAppointmentsOnDates implements Runnable {
    AppointementPost appointment;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String med_name_dosage;

    public SetAppointmentsOnDates(AppointementPost appointment) {
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
                    AppointmentOnDate app = new AppointmentOnDate(med_name_dosage,
                            appointment.getDays() - i, appointment.getMedicine_id(),
                            appointment.getPost_id(), is_forever, null, false);
                    writePost((int) startDate.getTimeInMillis() / 1000, time, app);
                }
                startDate.add(Calendar.DATE, 1);
            }
        } else if (appointment.getDays_of_week() != null) { // Принимать по дням недели
            for (int i = 0; i < days; i++) {
                if (isCorrectDayOfWeek(appointment.getDays_of_week(), startDate.get(Calendar.DAY_OF_WEEK))) {
                    for (int time : appointment.getTimes()) {
                        AppointmentOnDate app = new AppointmentOnDate(med_name_dosage,
                                appointment.getDays() - i, appointment.getMedicine_id(),
                                appointment.getPost_id(), is_forever, null, false);
                        writePost((int) startDate.getTimeInMillis() / 1000, time, app);
                    }
                }
                startDate.add(Calendar.DATE, 1);
            }
        } else { // Принимать каждые х дней
            int i = 0;
            while (i < days) {
                for (int time : appointment.getTimes()) {
                    AppointmentOnDate app = new AppointmentOnDate(med_name_dosage,
                            appointment.getDays() - i, appointment.getMedicine_id(),
                            appointment.getPost_id(), is_forever, null, false);
                    writePost((int) startDate.getTimeInMillis() / 1000, time, app);
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
        mDatabase.child(String.valueOf(date)).child(String.valueOf(time)).setValue(new_app)
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

    private void getMedicine(AppointementPost curr_app) {
        DatabaseReference database = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        database.child(curr_app.getMedicine_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine med = dataSnapshot.getValue(Medicine.class);
                        med_name_dosage = med.getName() + med.getDosage();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }
}
