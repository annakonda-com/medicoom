package com.medicoom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
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
import com.medicoom.javaClasses.HistoryAppointmentAdapter;
import com.medicoom.utils.myUtils;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    //ArrayList<Appointment> appointments = new ArrayList<>();
    DatabaseReference baseReferense = FirebaseDatabase.getInstance(myUtils.dbPath).getReference("users");

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CalendarView calendarView = view.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                Log.d("MAYTAG", date);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        /*
        ListView viewList = view.findViewById(R.id.history_med_list);
        HistoryAppointmentAdapter medsAdapter = new HistoryAppointmentAdapter(getActivity(), appointments);
        viewList.setAdapter(medsAdapter);
        FirebaseUser curr_usr = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference allAppoints = baseReferense.child(curr_usr.getUid())
                .child("appointments");
        allAppoints.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot appointmentDS) {
                if (!appointments.isEmpty()){
                    appointments.clear();
                }
                for (DataSnapshot child: appointmentDS.getChildren()){
                    appointments.add(child.getValue(Appointment.class));
                }
                medsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/ // На будущее
        return view;
    }
}