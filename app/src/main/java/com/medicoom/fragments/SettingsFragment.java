package com.medicoom.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicoom.GreetingActivity;
import com.medicoom.MainActivity;
import com.medicoom.R;
import com.medicoom.utils.myUtils;

import java.util.Date;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FirebaseUser curr_usr = FirebaseAuth.getInstance().getCurrentUser();
        if (curr_usr.isAnonymous()){
            ((TextView) view.findViewById(R.id.warning)).setText("Ваши данные хранятся в анонимном аккаунте");
            ((Button) view.findViewById(R.id.logout)).setText("Выйти из аккаунта (данные не сохранятся)");
            view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logoutIntent = new Intent(getActivity(), GreetingActivity.class);
                    startActivity(logoutIntent);
                    getActivity().finish();
                }
            });
        } else {
            String warn = "Вы вошли в аккаунт с почтой " + curr_usr.getEmail();
            ((TextView) view.findViewById(R.id.warning)).setText(warn);
            view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logoutIntent = new Intent(getActivity(), GreetingActivity.class);
                    startActivity(logoutIntent);
                    getActivity().finish();
                }
            });
        }

        Toolbar tlbr = view.findViewById(R.id.my_toolbar);
        tlbr.setNavigationIcon(R.drawable.arrow_back);
        tlbr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }
}