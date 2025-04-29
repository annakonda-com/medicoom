package com.medicoom.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.AppointementPost;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentAdapter;

import java.util.ArrayList;

public class ArchiveTreatmentFragment extends Fragment {

    public ArchiveTreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_active_treatment, container, false);
        ListView listView = contentView.findViewById(R.id.appointments_list);
        ArrayList<AppointementPost> app_list = new ArrayList<>();
        AppointmentAdapter listAdapter = new AppointmentAdapter(getActivity(), app_list);
        listView.setAdapter(listAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/appointments");
        ValueEventListener medListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!app_list.isEmpty()) {
                    app_list.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Appointment med = ds.getValue(Appointment.class);
                    if (med != null) {
                        AppointementPost appWithId = new AppointementPost(med.getAmount_at_once(),
                                med.isArchive(), med.getDays(), med.getDays_of_week(),
                                med.isDeleted(), med.getEvery_x_days(), med.getHow_to_get(),
                                med.getMedicine_id(), med.isNotifications(), med.isOn_pause(),
                                med.getStart_date(), med.getTimes(), ds.getKey());
                        if (!appWithId.isDeleted()){
                            app_list.add(appWithId);
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.orderByChild("archive").equalTo(true).addValueEventListener(medListener);
        return contentView;
    }
}