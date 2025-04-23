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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.medicoom.R;
import com.medicoom.javaClasses.MedicinePost;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChangeMedicineFragment extends BaseFragment {

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

    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_medicine, container, false);
    }

}