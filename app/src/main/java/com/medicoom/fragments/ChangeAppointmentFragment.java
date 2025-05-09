package com.medicoom.fragments;

import static com.medicoom.utils.myUtils.timeFormat;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medicoom.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;


public class ChangeAppointmentFragment extends Fragment {

    final String NAME = "name";
    final String START_DATE = "start_date";
    final String REST_TO_GET = "rest_to_get";
    final String YOU_ARE_GETTING = "you_are_getting";
    final String TIMES = "times";
    final String GRAPHIC = "graphic";
    final String HOW_TO_GET = "how_to_get";

    Bundle args;

    public ChangeAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            args = getArguments();
        }
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
        ((TextView) view.findViewById(R.id.med_name)).setText(args.getString(NAME));
        ((TextView) view.findViewById(R.id.date)).setText(args.getString(START_DATE));
        ((TextView) view.findViewById(R.id.rest_to_get)).setText(args.getString(REST_TO_GET));
        ((TextView) view.findViewById(R.id.you_are_getting_it)).setText(args.getString(YOU_ARE_GETTING));
        ArrayList<Integer> times = args.getIntegerArrayList(TIMES);
        Collections.sort(times);
        LinearLayout times_container = view.findViewById(R.id.times_container);
        for (int i = 0; i < times.size(); i++){
            View time_view = times_container.getChildAt(i);
            if (times.get(i) == -1){
                String text = (i + 1) + ") Без привязки ко времени";
                time_view.setVisibility(View.VISIBLE);
                ((TextView) time_view).setText(text);
            }else{
                String hour;
                String minute;
                if (times.get(i) / 3600 % 60 < 10){
                    hour = "0" + times.get(i) / 3600 % 60;
                } else {
                    hour = String.valueOf(times.get(i) / 3600 % 60);
                }
                if (times.get(i) % 3600 / 60 < 10){
                    minute = "0" + times.get(i) % 3600 / 60;
                } else {
                    minute = String.valueOf(times.get(i) % 3600 / 60);
                }
                String text = (i + 1) + ") " + hour + ":" + minute;
                time_view.setVisibility(View.VISIBLE);
                ((TextView) time_view).setText(text);
            }

        }
        ((TextView) view.findViewById(R.id.graphic)).setText(args.getString(GRAPHIC));
        ((TextView) view.findViewById(R.id.how_to_get)).setText(args.getString(HOW_TO_GET));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_appointment, container, false);
    }
}