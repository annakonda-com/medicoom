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
import com.medicoom.javaClasses.Medicine;
import com.medicoom.R;
import com.medicoom.utils.myUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class InputMedicineFragment extends Fragment {

    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());
    public int startDate = -1;
    public int int_num_of_tablets = -1;
    public int int_remind_when = -1;

    public InputMedicineFragment() {
        // Required empty public constructor
    }
    private void selfKill(){
        RelativeLayout body = getActivity().findViewById(R.id.body);
        body.setVisibility(View.VISIBLE);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(InputMedicineFragment.this);
        ft.commit();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar my_toolbar = view.findViewById(R.id.my_toolbar_input_med);
        my_toolbar.setNavigationIcon(R.drawable.arrow_back);
        my_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfKill();
            }
        });
        showCalendarDialog(view.findViewById(R.id.input_date), view.findViewById(R.id.input_date_layout));
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name, dosage, num_of_tablets, remind_when;
                name = view.findViewById(R.id.name);
                dosage = view.findViewById(R.id.dosage);
                if (validateForm(view)) {
                    Medicine newPost = new Medicine(name.getText().toString(),
                            dosage.getText().toString(), int_num_of_tablets,
                            startDate, int_remind_when);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance
                            ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("users");
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    mDatabase.child(currentUser.getUid()).child("medicines").push()
                            .setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Успешно добавлено!", Toast.LENGTH_LONG).show();
                                    selfKill();
                                }
                            });
                }

            }
        });

    }

    boolean validateForm(View view) {
        boolean valid = true;
        EditText name, dosage, num_of_tablets, remind_when;
        name = view.findViewById(R.id.name);
        dosage = view.findViewById(R.id.dosage);
        num_of_tablets = view.findViewById(R.id.num_of_tablets);
        remind_when = view.findViewById(R.id.min_tablets);
        if (name.getText().toString().isEmpty()) {
            name.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(name.getText().toString())) {
                name.setError("Поле пустое");
                valid = false;
            } else {
                name.setError(null);
            }
        }
        if (!dosage.getText().toString().isEmpty() && myUtils.isSpace(name.getText().toString())) {
            dosage.setError("Поле пустое");
            valid = false;
        } else {
            dosage.setError(null);
        }
        if (!num_of_tablets.getText().toString().isEmpty() && !myUtils.isSpace(remind_when.getText().toString())) {
            try {
                int_num_of_tablets = Integer.parseInt(num_of_tablets.getText().toString());
                num_of_tablets.setError(null);
            } catch (NumberFormatException e) {
                num_of_tablets.setError("Введите число!");
            }
        }
        if (!remind_when.getText().toString().isEmpty() && !myUtils.isSpace(remind_when.getText().toString())) {
            try {
                int_remind_when = Integer.parseInt(num_of_tablets.getText().toString());
                remind_when.setError(null);
            } catch (NumberFormatException e) {
                remind_when.setError("Введите число!");
            }
        }
        return valid;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_medicine, container, false);
    }
}