package com.medicoom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;

import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentOnDate;
import com.medicoom.javaClasses.HistoryAppointmentAdapter;
import com.medicoom.javaClasses.HistoryCurrApointmentAdapter;
import com.medicoom.javaClasses.TodayAppointmentAdapter;
import com.medicoom.utils.myUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class HistoryAppointmentsFragment extends Fragment {
    int day;
    ArrayList<Map.Entry<Integer, AppointmentOnDate>> appointments_on_times = new ArrayList<>();

    DatabaseReference baseReferense = FirebaseDatabase.getInstance(myUtils.dbPath).getReference("users");

    public HistoryAppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getInt("day");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HistoryCurrApointmentAdapter myAdapter = new HistoryCurrApointmentAdapter(getContext(), appointments_on_times);
        ListView today_app = view.findViewById(R.id.that_day_list);
        today_app.setAdapter(myAdapter);
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        getTodayAppointments((int) (today.getTimeInMillis() / 1000L), myAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_appointments, container, false);

        return view;
    }
    private void getTodayAppointments(int today_date, HistoryCurrApointmentAdapter adapter) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid());

        mDatabase.child("appointments_on_dates").child(String.valueOf(today_date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!appointments_on_times.isEmpty()) {
                    appointments_on_times.clear();
                }
                Log.d("MAYTAG", String.valueOf(today_date));
                for (DataSnapshot time : dataSnapshot.getChildren()) {
                    for (DataSnapshot child_child : time.getChildren()) {
                        AppointmentOnDate curr_app = child_child.getValue(AppointmentOnDate.class);
                        DatabaseReference appoint = FirebaseDatabase.getInstance(myUtils.dbPath)
                                .getReference("users").child(currentUser.getUid())
                                .child("appointments").child(curr_app.getAppointment_id());
                        appoint.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Appointment firstApp = snapshot.getValue(Appointment.class);
                                Log.d("MAYTAG", firstApp.toString());
                                if (!firstApp.isDeleted() && !firstApp.isArchive() && !firstApp.isOn_pause()) {
                                    curr_app.setPost_id(child_child.getKey());
                                    appointments_on_times.add(new AbstractMap.SimpleEntry<Integer, AppointmentOnDate>
                                            (Integer.valueOf(time.getKey()), curr_app));
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
