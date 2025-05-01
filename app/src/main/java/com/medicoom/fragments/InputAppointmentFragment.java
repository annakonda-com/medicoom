package com.medicoom.fragments;

import static com.medicoom.utils.myUtils.dateFormat;
import static com.medicoom.utils.myUtils.timeFormat;

import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicinePost;
import com.medicoom.javaClasses.MedicineSpinnerAdapter;
import com.medicoom.utils.myUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class InputAppointmentFragment extends Fragment {
    final String FULL_SCREEN = "full_screen";
    final String FAKE_ID = "AddNewMed";
    public int startDate = -1;

    public ArrayList<Integer> times = new ArrayList<>();

    MedicinePost picked_med;
    String how_to_get_str = null;

    int num_of_times = 1;
    int every_x_days = -1;


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
        RadioButton not_forever;
        RadioButton by_week;
        RadioButton at_some_of_days;
        RadioButton personal;

        ((CheckBox) view.findViewById(R.id.send_notific)).setChecked(true);

        view.findViewById(R.id.input_date_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog(view.findViewById(R.id.input_date));
            }
        });

        View.OnClickListener timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(v.findViewById(R.id.input_time));
            }
        };
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
                if (times != null) {
                    times.clear();
                }
                num_of_times = position + 1;

                LinearLayout container = view.findViewById(R.id.times_inputs_container);
                for (int i = 0; i < container.getChildCount(); i++) {
                    container.getChildAt(i).setVisibility(View.GONE);
                }
                for (int i = 0; i <= position; i++) {
                    View curr_inp = container.getChildAt(i);
                    curr_inp.setVisibility(View.VISIBLE);
                    String start_text = getString(R.string.time_of_get) + " " + (i + 1);
                    ((TextView) curr_inp.findViewById(R.id.input_time)).setText(start_text);
                    curr_inp.setOnClickListener(timeClickListener);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        not_forever = view.findViewById(R.id.not_forever);
        not_forever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View overallview = view.findViewById(R.id.overall_duration_layout);
                if (isChecked) {
                    overallview.animate().alpha(1.0f);
                    overallview.setVisibility(View.VISIBLE);
                } else {
                    overallview.animate().alpha(0.0f);
                    overallview.setVisibility(View.GONE);
                }
            }
        });

        by_week = view.findViewById(R.id.by_week);
        by_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View days = view.findViewById(R.id.days_of_week);
                if (isChecked) {
                    days.animate().alpha(1.0f);
                    days.setVisibility(View.VISIBLE);
                } else {
                    days.animate().alpha(0.0f);
                    days.setVisibility(View.GONE);
                }
            }
        });
        at_some_of_days = view.findViewById(R.id.at_some_of_days);
        at_some_of_days.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View days = view.findViewById(R.id.for_how_much_days_layout);
                if (isChecked) {
                    days.animate().alpha(1.0f);
                    days.setVisibility(View.VISIBLE);
                } else {
                    days.animate().alpha(0.0f);
                    days.setVisibility(View.GONE);
                }
            }
        });
        personal = view.findViewById(R.id.personal);
        personal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View how_to_get_layout = view.findViewById(R.id.how_to_get_layout);
                if (isChecked) {
                    how_to_get_layout.animate().alpha(1.0f);
                    how_to_get_layout.setVisibility(View.VISIBLE);
                } else {
                    how_to_get_layout.animate().alpha(0.0f);
                    how_to_get_layout.setVisibility(View.GONE);
                }
            }
        });
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment new_post = makePost(view);
                if (makePost(view) != null){
                    writePost(new_post);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_input_appointment, container, false);
        ((RadioButton) contentView.findViewById(R.id.forever)).setChecked(true);
        ((RadioButton) contentView.findViewById(R.id.every_day)).setChecked(true);
        ((RadioButton) contentView.findViewById(R.id.before)).setChecked(true);

        ArrayList<MedicinePost> med_list = new ArrayList<>();
        MedicineSpinnerAdapter adapter = new MedicineSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, med_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = contentView.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MedicinePost curr_med = adapter.getItem(position);
                if (curr_med.getPostId() != null && curr_med.getPostId().equals(FAKE_ID)) {
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

    void showCalendarDialog(TextView myview) {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
                Calendar loc_startDate = Calendar.getInstance();
                loc_startDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                myview.setText(dateFormat.format(loc_startDate.getTime()));
                startDate = (int) (loc_startDate.getTimeInMillis() / 1000L);
            }
        };

        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getActivity(), dateListener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH))
                .show();
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
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), timeListener,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), true)
                .show();

    }

    void writePost(Appointment post) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(currentUser.getUid()).child("appointments").push()
                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Успешно добавлено!", Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
    }

    Appointment makePost(View view) {
        if (validateForm(view)) {
            String medicine_id = picked_med.getPostId();

            if (how_to_get_str == null) {
                RadioButton checked_rb = view.findViewById(((RadioGroup) view.findViewById(R.id.how_get_layout))
                        .getCheckedRadioButtonId());
                how_to_get_str = checked_rb.getText().toString();
            }

            int amount_at_once = 0;
            String am_at_once = ((EditText) view.findViewById(R.id.num_of_tablets)).getText().toString();
            if (!am_at_once.isEmpty()) {
                amount_at_once = Integer.parseInt(am_at_once);
            }

            if (startDate == -1) {
                Calendar loc_startDate = Calendar.getInstance();
                loc_startDate.set(loc_startDate.get(Calendar.YEAR),
                        loc_startDate.get(Calendar.MONTH),
                        loc_startDate.get(Calendar.DAY_OF_MONTH),
                        0, 0, 0);
                startDate = (int) (loc_startDate.getTimeInMillis() / 1000L);
            }

            int overall_duration = -1;
            String ovll_duration = ((EditText) view.findViewById(R.id.overall_duration)).getText().toString();
            if (!ovll_duration.isEmpty()) {
                overall_duration = Integer.parseInt(ovll_duration);
            }

            List<Integer> week_days_arr = null;
            Integer[] raw_days = new Integer[8];
            ChipGroup days_of_week = view.findViewById(R.id.days_of_week);
            List<Integer> selectedIds = days_of_week.getCheckedChipIds();
            if (!selectedIds.isEmpty()) {
                for (int i = 0; i < selectedIds.size(); i++) {
                    if (selectedIds.get(i) == R.id.monday) {
                        raw_days[i] = 0;
                    } else if (selectedIds.get(i) == R.id.tuesday) {
                        raw_days[i] = 1;
                    } else if (selectedIds.get(i) == R.id.wednesday) {
                        raw_days[i] = 2;
                    } else if (selectedIds.get(i) == R.id.thursday) {
                        raw_days[i] = 3;
                    } else if (selectedIds.get(i) == R.id.friday) {
                        raw_days[i] = 4;
                    } else if (selectedIds.get(i) == R.id.saturday) {
                        raw_days[i] = 5;
                    } else if (selectedIds.get(i) == R.id.sunday) {
                        raw_days[i] = 6;
                    }
                }
                week_days_arr = Arrays.asList(raw_days);
            }

            List<Integer> final_times = times;

            String ddays = ((EditText) view.findViewById(R.id.for_how_much_days)).getText().toString();
            if (!ddays.isEmpty()) {
                every_x_days = Integer.parseInt(ddays);
            }
            boolean notification = ((CheckBox) view.findViewById(R.id.send_notific)).isChecked();
            boolean on_pause = false;
            boolean archive = false;
            boolean deleted = false;

            Appointment new_post = new Appointment(amount_at_once, archive,
                    overall_duration, week_days_arr, deleted, every_x_days, how_to_get_str,
                    medicine_id, notification, on_pause, startDate, final_times);
            return new_post;
        }
        return null;
    }

    boolean validateForm(View view) {

        boolean valid = true;
        if (((RadioButton) view.findViewById(R.id.not_forever)).isChecked()) {
            EditText overall_duration = view.findViewById(R.id.overall_duration);
            if (overall_duration.getText().toString().isEmpty()) {
                overall_duration.setError("Обязательное поле");
                valid = false;
            } else {
                if (myUtils.isSpace(overall_duration.getText().toString())) {
                    overall_duration.setError("Поле пустое");
                    valid = false;
                } else {
                    overall_duration.setError(null);
                }
            }
        }
        if (((RadioButton) view.findViewById(R.id.by_week)).isChecked()) {
            ChipGroup days_of_week = view.findViewById(R.id.days_of_week);
            if (days_of_week.getCheckedChipIds().isEmpty()) {
                Toast.makeText(getContext(), "Выберите дни недели!", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        if (((RadioButton) view.findViewById(R.id.at_some_of_days)).isChecked()) {
            EditText for_how_much_days = view.findViewById(R.id.for_how_much_days);
            if (for_how_much_days.getText().toString().isEmpty()) {
                for_how_much_days.setError("Введите количество дней");
                valid = false;
            } else if (myUtils.isSpace(for_how_much_days.getText().toString())) {
                for_how_much_days.setError("Поле пустое");
                valid = false;
            } else {
                for_how_much_days.setError(null);
            }
        }
        if (((RadioButton) view.findViewById(R.id.personal)).isChecked()) {
            EditText how_to_get = view.findViewById(R.id.how_to_get);
            if (how_to_get.getText().toString().isEmpty()) {
                how_to_get.setError("Введите как принимать");
                valid = false;
            } else if (myUtils.isSpace(how_to_get.getText().toString())) {
                how_to_get.setError("Поле пустое");
                valid = false;
            } else {
                how_to_get_str = how_to_get.getText().toString();
                how_to_get.setError(null);
            }
        }

        if(((RadioButton) view.findViewById(R.id.every_day)).isChecked()){
            every_x_days = 1;
        }

        while (times.size() < num_of_times) {
            times.add(-1);
        }
        return valid;
    }
}