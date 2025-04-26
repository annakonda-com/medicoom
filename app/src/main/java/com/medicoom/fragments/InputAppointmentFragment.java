package com.medicoom.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class InputAppointmentFragment extends Fragment {
    final String FULL_SCREEN = "full_screen";
    final String FAKE_ID = "AddNewMed";

    public ArrayList<Integer> times = new ArrayList<>();
    SimpleDateFormat timeFormat = new SimpleDateFormat("H:m", Locale.getDefault());

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

        Spinner how_much_a_day = view.findViewById(R.id.how_much_a_day);
        how_much_a_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (times != null){
                    times.clear();
                }
                Log.d("MAYTAG", "-----");
                for (int x: times){
                    Log.d("MAYTAG", String.valueOf(x));
                }
                Log.d("MAYTAG", "-----");

                LinearLayout container = view.findViewById(R.id.times_inputs_container);
                int inputs_num = container.getChildCount();
                if (inputs_num != 1){
                    for (int i = 0; i < inputs_num; i++) {
                        View input = container.getChildAt(i);
                        container.removeView(input);
                    }
                }
                for (int i = 0; i <= position; i++){
                    View input_time = getLayoutInflater().inflate(R.layout.time_input, container, true);
                    String start_text = getString(R.string.time_of_get) + " " + (i + 1);
                    ((TextView) input_time.findViewById(R.id.input_time)).setText(start_text);
                    input_time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTimeDialog(input_time.findViewById(R.id.input_time));
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    void showTimeDialog(TextView view) {
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker v, int hourOfDay, int minute) {
                Calendar res = Calendar.getInstance();
                res.set(0, 0, 0, hourOfDay, minute);
                times.add(hourOfDay * 3600 + minute * 60);
                view.setText(timeFormat.format(res.getTime()));
            }
        };
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new TimePickerDialog(getContext(), timeListener,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE), true)
                        .show();
            }
        });

    }
}