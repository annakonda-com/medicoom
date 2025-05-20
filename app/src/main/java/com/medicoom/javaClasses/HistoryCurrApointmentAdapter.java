package com.medicoom.javaClasses;

import static com.medicoom.utils.myUtils.dateFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.utils.SetAppointmentsOnDates;
import com.medicoom.utils.myUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HistoryCurrApointmentAdapter extends ArrayAdapter<Map.Entry<Integer, AppointmentOnDate>> {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String med_name_dosage;

    public HistoryCurrApointmentAdapter(Context context, ArrayList<Map.Entry<Integer, AppointmentOnDate>> arr) {
        super(context, R.layout.today_item, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map.Entry<Integer, AppointmentOnDate> curr_app = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.today_item, null);
        }

        String text;
        Integer time = curr_app.getKey();
        if (time != -1) {
            String hour;
            String minute;
            if (time / 3600 % 60 < 10) {
                hour = "0" + time / 3600 % 60;
            } else {
                hour = String.valueOf(time / 3600 % 60);
            }
            if (time % 3600 / 60 < 10) {
                minute = "0" + time % 3600 / 60;
            } else {
                minute = String.valueOf(time % 3600 / 60);
            }
            text = hour + ":" + minute;
            ((TextView) convertView.findViewById(R.id.today_time)).setText(text);
        } else {
            convertView.findViewById(R.id.today_time).setVisibility(View.GONE);
        }

        View radio_btn = convertView.findViewById(R.id.radio_btn);
        TextView name = convertView.findViewById(R.id.today_name);
        DatabaseReference database = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        database.child(curr_app.getValue().getMed_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine med = dataSnapshot.getValue(Medicine.class);
                        String med_name_dosage = med.getName() + " " + med.getDosage();
                        name.setText(med_name_dosage);
                        if (!med.isDeleted()) {
                            if (curr_app.getValue().isIs_got()) {
                                radio_btn.setBackgroundResource(R.drawable.baseline_check_circle_outline_24);
                            } else {
                                radio_btn.setBackgroundResource(R.drawable.radio_button_unchecked);
                            }
                        } else {
                            radio_btn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });
        TextView desc = convertView.findViewById(R.id.today_description);
        DatabaseReference mdatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/appointments");
        mdatabase.child(curr_app.getValue().getAppointment_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Appointment app = dataSnapshot.getValue(Appointment.class);
                        // Log.d("MAYTAG", app.toString());
                        String description;
                        String how_to_get = "";
                        if (app.getHow_to_get() != null) {
                            how_to_get = app.getHow_to_get();
                        }
                        String amount_at_once = "";
                        if (app.getAmount_at_once() != 0) {
                            amount_at_once = String.valueOf(app.getAmount_at_once()) + " " + "шт";
                        }
                        description = how_to_get + " " + amount_at_once;
                        desc.setText(description);

                        if (app.isDeleted() || app.isArchive() || app.isOn_pause()) {
                            radio_btn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });

        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        int today_int = (int) (today.getTimeInMillis() / 1000L);
        radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar noww = Calendar.getInstance(Locale.getDefault());
                int got_time = (noww.get(Calendar.HOUR_OF_DAY) * 3600) + (noww.get(Calendar.MINUTE) * 60);
                AppointmentOnDate new_app = new AppointmentOnDate(curr_app.getValue());
                new_app.setPost_id(null);
                new_app.setIs_got(!new_app.isIs_got());
                new_app.setGot_time(got_time);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference appointments = FirebaseDatabase.getInstance
                                ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("users" + "/" + currentUser.getUid() + "/appointments_on_dates");

                appointments.child(String.valueOf(today_int)).child(String.valueOf(curr_app.getKey()))
                        .child(curr_app.getValue().getPost_id())
                        .setValue(new_app).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (new_app.isIs_got()) {
                                    radio_btn.setBackgroundResource(R.drawable.baseline_check_circle_outline_24);
                                } else {
                                    radio_btn.setBackgroundResource(R.drawable.radio_button_unchecked);
                                }
                            }
                        });
                myUtils.change_num_of_tablets(new_app.isIs_got(), currentUser, new_app, getContext());
            }
        });

        return convertView;
    }


}
