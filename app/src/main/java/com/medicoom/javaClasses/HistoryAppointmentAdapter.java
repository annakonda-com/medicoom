package com.medicoom.javaClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.medicoom.utils.myUtils;

import java.util.ArrayList;
// Это на будущее

public class HistoryAppointmentAdapter extends ArrayAdapter<Appointment> {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public HistoryAppointmentAdapter(Context context, ArrayList<Appointment> arr) {
        super(context, R.layout.archive_item, arr);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Appointment curr_app = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.archive_item, null);
        }
        TextView name = convertView.findViewById(R.id.appointment_name);

        DatabaseReference medUnit = FirebaseDatabase.getInstance
                        (myUtils.dbPath)
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        medUnit.child(curr_app.getMedicine_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine med = dataSnapshot.getValue(Medicine.class);
                        String appointment_name = med.getName() + " " + med.getDosage();
                        name.setText(appointment_name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });

        return convertView;
    }
}