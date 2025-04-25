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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicinePost;
import com.medicoom.javaClasses.MedicineSpinnerAdapter;

import java.util.ArrayList;


public class InputAppointmentFragment extends Fragment {
    final String FULL_SCREEN = "full_screen";
    final String FAKE_ID = "AddNewMed";

    MedicinePost picked_med;

    public InputAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar my_toolbar = view.findViewById(R.id.my_toolbar);
        my_toolbar.setNavigationIcon(R.drawable.arrow_back);
        my_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        RadioButton not_forever = view.findViewById(R.id.not_forever);
        not_forever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                   View overallview = view.findViewById(R.id.overall_duration_layout);
                    overallview.animate().alpha(1.0f);
                   overallview.setVisibility(View.VISIBLE);
                }
            }
        });
        RadioButton forever = view.findViewById(R.id.forever);
        forever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    View overallview = view.findViewById(R.id.overall_duration_layout);
                    overallview.animate().alpha(0.0f);
                    overallview.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_input_appointment, container, false);

        ArrayList<MedicinePost> med_list = new ArrayList<>();
        MedicineSpinnerAdapter adapter = new MedicineSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, med_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = contentView.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MedicinePost curr_med = adapter.getItem(position);
                if (curr_med.getPostId() != null && curr_med.getPostId().equals(FAKE_ID)){
                    InputMedicineFragment chfr = new InputMedicineFragment();

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.main, chfr, FULL_SCREEN);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    picked_med = curr_med;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                picked_med = null;
            }
        });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/medicines");
        ValueEventListener medListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!med_list.isEmpty()) {
                    med_list.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Medicine medicine = ds.getValue(Medicine.class);
                    if (medicine != null) {
                        MedicinePost medWithId = new MedicinePost(medicine.getName(),
                                medicine.getDosage(), medicine.getNum_of_tablets(),
                                medicine.getGood_until(), medicine.getRemind_when(), ds.getKey());
                        med_list.add(medWithId);
                    }
                }
                med_list.add(new MedicinePost("Добавить новое лекарство", null,
                        -1, -1, -1, FAKE_ID));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(medListener);

        String[] nums = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> how_much_adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, nums);
        how_much_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) contentView.findViewById(R.id.how_much_a_day)).setAdapter(how_much_adapter);

        
        return contentView;
    }
}