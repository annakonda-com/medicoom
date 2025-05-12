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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class TodayAppointmentAdapter extends ArrayAdapter<Map.Entry<Integer, AppointmentOnDate>> {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String med_name_dosage;

    public TodayAppointmentAdapter(Context context, ArrayList<Map.Entry<Integer, AppointmentOnDate>> arr) {
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
                        Log.d("MAYTAG", app.toString());
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });

        if (curr_app.getValue().isIs_got()){
            convertView.findViewById(R.id.radio_btn).setBackgroundResource(R.drawable.baseline_check_circle_outline_24);
        }
        return convertView;
    }


}
