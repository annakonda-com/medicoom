package com.medicoom.fragments;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicoom.R;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicinePost;
import com.medicoom.utils.myUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChangeMedicineFragment extends BaseFragment {

    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());
    public int startDate = -1;
    public int int_num_of_tablets = -1;
    public int int_remind_when = -1;

    private String name;
    private String dosage;
    private int num_of_tablets;
    private int good_until;
    private int remind_when;
    private String post_id;

    private final String NAME = "name";
    private final String DOSAGE = "dosage";
    private final String NUM_OF_TABLETS = "num_of_tablets";
    private final String GOOD_UNTIL = "good_until";
    private final String REMIND_WHEN = "remind_when";
    private final String POST_ID = "post_id";


    public ChangeMedicineFragment() {
        // Required empty public constructor
    }

    @Override
    public void selfKill() {
        super.selfKill();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main, new FarmacyFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME);
            dosage = getArguments().getString(DOSAGE);
            num_of_tablets = getArguments().getInt(NUM_OF_TABLETS);
            good_until = getArguments().getInt(GOOD_UNTIL);
            remind_when = getArguments().getInt(REMIND_WHEN);
            post_id = getArguments().getString(POST_ID);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showCalendarDialog(view.findViewById(R.id.input_date), view.findViewById(R.id.input_date_layout));
        Toolbar my_toolbar = view.findViewById(R.id.my_toolbar_input_med);
        my_toolbar.setNavigationIcon(R.drawable.arrow_back);
        my_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfKill();
            }
        });

        EditText et = view.findViewById(R.id.name);
        et.setText(name);

        if (dosage != null) {
            EditText etd = view.findViewById(R.id.dosage);
            etd.setText(dosage);
        }
        if (num_of_tablets != -1) {
            EditText etn = view.findViewById(R.id.num_of_tablets);
            etn.setText(String.valueOf(num_of_tablets));
        }
        if (good_until != -1) {
            TextView etg = view.findViewById(R.id.input_date);
            etg.setText(dateFormat.format(good_until * 1000L));
        }
        if (remind_when != -1) {
            EditText etr = view.findViewById(R.id.min_tablets);
            etr.setText(String.valueOf(remind_when));
        }

        Button save = view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                EditText edit_name, dosage;
                edit_name = view.findViewById(R.id.name);
                dosage = view.findViewById(R.id.dosage);
                if (validateForm(view)) {
                    CheckBox remove_date = view.findViewById(R.id.without_date_chb);
                    if (remove_date.isChecked()){
                        startDate = -1;
                    }
                    Medicine newPost = new Medicine(edit_name.getText().toString(),
                            dosage.getText().toString(), int_num_of_tablets,
                            startDate, int_remind_when);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance
                                    ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("users");
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    mDatabase.child(currentUser.getUid()).child("medicines").child(post_id)
                            .setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Успешно обновлено!", Toast.LENGTH_LONG).show();
                                    selfKill();
                                }
                            });
                }

            }
        });


    }

    void showCalendarDialog(TextView myview, RelativeLayout layout) {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
                Calendar loc_startDate = Calendar.getInstance();
                loc_startDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                myview.setText(dateFormat.format(loc_startDate.getTime()));
                startDate = (int) (loc_startDate.getTimeInMillis() / 1000L);
            }
        };
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(), dateListener,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    boolean validateForm(View view) {
        boolean valid = true;
        EditText edit_name, edit_dosage, edit_num_of_tablets, edit_remind_when;
        edit_name = view.findViewById(R.id.name);
        edit_dosage = view.findViewById(R.id.dosage);
        edit_num_of_tablets = view.findViewById(R.id.num_of_tablets);
        edit_remind_when = view.findViewById(R.id.min_tablets);
        if (edit_name.getText().toString().isEmpty()) {
            edit_name.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(edit_name.getText().toString())) {
                edit_name.setError("Поле пустое");
                valid = false;
            } else {
                edit_name.setError(null);
            }
        }
        if (!edit_dosage.getText().toString().isEmpty() && myUtils.isSpace(edit_name.getText().toString())) {
            edit_dosage.setError("Поле пустое");
            valid = false;
        } else {
            edit_dosage.setError(null);
        }
        if (!edit_num_of_tablets.getText().toString().isEmpty() && !myUtils.isSpace(edit_remind_when.getText().toString())) {
            try {
                int_num_of_tablets = Integer.parseInt(edit_num_of_tablets.getText().toString());
                edit_num_of_tablets.setError(null);
            } catch (NumberFormatException e) {
                edit_num_of_tablets.setError("Введите число!");
                valid = false;
            }
        }
        if (!edit_remind_when.getText().toString().isEmpty() && !myUtils.isSpace(edit_remind_when.getText().toString())) {
            try {
                int_remind_when = Integer.parseInt(edit_remind_when.getText().toString());
                edit_remind_when.setError(null);
            } catch (NumberFormatException e) {
                edit_remind_when.setError("Введите число!");
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_medicine, container, false);
    }

}