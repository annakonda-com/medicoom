package com.medicoom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import com.medicoom.javaClasses.TodayAppointmentAdapter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment {
    final String MAIN_FRAGMENT = "main_fragment";
    final String FULL_SCREEN = "full_screen";
    ArrayList<Map.Entry<Integer, AppointmentOnDate>> appointments_on_times = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TodayAppointmentAdapter myAdapter = new TodayAppointmentAdapter(getContext(), appointments_on_times);
        ListView today_app = view.findViewById(R.id.today_list);
        today_app.setAdapter(myAdapter);
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        getTodayAppointments((int)(today.getTimeInMillis() / 1000L), myAdapter);
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar tlbr = getActivity().findViewById(R.id.my_toolbar);
                tlbr.setTitle(R.string.treatment);

                InputAppointmentFragment input_fr = new InputAppointmentFragment();
                TreatmentFragment treatment = new TreatmentFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.add(R.id.fragment_container, treatment, MAIN_FRAGMENT);
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                transaction1.remove(treatment);
                transaction1.replace(R.id.main, input_fr, FULL_SCREEN);
                transaction1.addToBackStack(null);
                transaction1.commit();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid());
        mDatabase.child("appointments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    fab.animate().alpha(1.0f);
                    fab.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.comment_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });
        //ArrayList<Map.Entry<Integer, ArrayList<AppointmentOnDate>>> app_on_times = new ArrayList<>(appointments_on_times.entrySet());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getTodayAppointments(int today_date, TodayAppointmentAdapter adapter){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid());

        mDatabase.child("appointments_on_dates").child(String.valueOf(today_date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot time: dataSnapshot.getChildren()){
                    for (DataSnapshot child_child: time.getChildren()){
                        AppointmentOnDate curr_app = child_child.getValue(AppointmentOnDate.class);
                        curr_app.setPost_id(child_child.getKey());
                        appointments_on_times.add(new AbstractMap.SimpleEntry<Integer, AppointmentOnDate>
                                (Integer.valueOf(time.getKey()), curr_app));
                        adapter.notifyDataSetChanged();
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