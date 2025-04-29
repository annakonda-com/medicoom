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
import android.widget.CompoundButton;
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
import java.util.List;


public class TreatmentFragment extends Fragment {
    final String FULL_SCREEN = "full_screen";
    final String MAIN_FRAGMENT = "main_fragment";

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
        MaterialButtonToggleGroup switch_btn = view.findViewById(R.id.toggleButton);
        switch_btn.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (switch_btn.getCheckedButtonId() == R.id.active) {
                    ActiveTreatmentFragment inpfr = new ActiveTreatmentFragment();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    if (!getChildFragmentManager().getFragments().isEmpty()) {
                        ft.remove(getChildFragmentManager().getFragments().get((getChildFragmentManager().getFragments()).size() - 1));
                    }
                    ft.replace(R.id.sub_fragment, inpfr, MAIN_FRAGMENT);
                    ft.addToBackStack(null);
                    ft.commit();
                } else if (checkedId == R.id.archive){
                    ArchiveTreatmentFragment inpfr = new ArchiveTreatmentFragment();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    if (!getChildFragmentManager().getFragments().isEmpty()) {
                        ft.remove(getChildFragmentManager().getFragments().get((getChildFragmentManager().getFragments()).size() - 1));
                    }
                    ft.replace(R.id.sub_fragment, inpfr, MAIN_FRAGMENT);
                    ft.addToBackStack(null);
                    ft.commit();
                }
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
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }
}