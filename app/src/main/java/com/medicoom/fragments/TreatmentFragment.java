package com.medicoom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButtonToggleGroup;
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
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicineAdapter;
import com.medicoom.javaClasses.MedicinePost;
import com.medicoom.javaClasses.MedicineSpinnerAdapter;

import java.util.ArrayList;


public class TreatmentFragment extends Fragment {
    final String FULL_SCREEN = "full_screen";

    public TreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.add_treatment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputAppointmentFragment inpfr = new InputAppointmentFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.remove(TreatmentFragment.this);
                ft.replace(R.id.main, inpfr, FULL_SCREEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_treatment, container, false);
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
                        app_list.add(appWithId);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(medListener);
        return contentView;
    }
}