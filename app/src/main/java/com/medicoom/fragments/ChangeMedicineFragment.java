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
import android.widget.Button;
import android.widget.EditText;
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
            public void onClick(View view) {
                EditText name, dosage;
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
                valid = false;
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_medicine, container, false);
    }

}