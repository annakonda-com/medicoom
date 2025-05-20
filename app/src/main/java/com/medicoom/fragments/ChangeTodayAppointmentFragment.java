package com.medicoom.fragments;

import static com.medicoom.utils.myUtils.timeFormat;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentOnDate;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.utils.myUtils;

import java.util.Calendar;


public class ChangeTodayAppointmentFragment extends Fragment {

    AppointmentOnDate appointment;
    int time = -1;
    int givenTime;
    int day;
    String where;

    public ChangeTodayAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointment = new AppointmentOnDate(getArguments().getBundle("appointment"));
            givenTime = getArguments().getInt("time");

            day = getArguments().getInt("date");
            where = getArguments().getString("where");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar my_toolbar = view.findViewById(R.id.my_toolbar);
        FirebaseUser curr_usr = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference medUnit = FirebaseDatabase.getInstance(myUtils.dbPath)
                .getReference("users").child(curr_usr.getUid())
                .child("medicines").child(appointment.getMed_id());
        medUnit.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                my_toolbar.setTitle(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadMedicine:onCancelled", error.toException());
            }
        });
        my_toolbar.setNavigationIcon(R.drawable.arrow_back);
        my_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        RelativeLayout timeInclude = view.findViewById(R.id.got_time);
        TextView timeView = timeInclude.findViewById(R.id.input_time);
        if (appointment.getGot_time() != -1) {
            timeView.setText(myUtils.secToTime(appointment.getGot_time()));
        }
        if (where == null) {
            timeInclude.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDialog(timeView);
                }
            });
        }
        RelativeLayout commInclude = view.findViewById(R.id.fake_comment);
        TextView commView = timeInclude.findViewById(R.id.input_text);
        EditText comm = view.findViewById(R.id.comment);
        if (where == null) {
            commInclude.setVisibility(View.GONE);
        } else {
            comm.setVisibility(View.GONE);
        }
        if (appointment.getComment() != null && !appointment.getComment().isEmpty()) {
            if (where == null) {
                comm.setText(appointment.getComment());
            } else {
                commView.setText(appointment.getComment());
            }
        }

        CheckBox isGot = view.findViewById(R.id.is_got);
        if (where == null) {
            isGot.setChecked(appointment.isIs_got());

            view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = ((EditText) view.findViewById(R.id.comment)).getText().toString();
                    AppointmentOnDate newApp = new AppointmentOnDate(appointment);
                    if (time != -1) {
                        newApp.setGot_time(time);
                    }
                    if (!comment.isEmpty()) {
                        newApp.setComment(comment);
                    }

                    if (isGot.isChecked()) {
                        if (newApp.isIs_got() != isGot.isChecked()) {
                            Context context = getContext();
                            myUtils.change_num_of_tablets(isGot.isChecked(), curr_usr, newApp, context);
                            newApp.setIs_got(true);
                        }
                    } else {
                        if (newApp.isIs_got() != isGot.isChecked()) {
                            Context context = getContext();
                            myUtils.change_num_of_tablets(isGot.isChecked(), curr_usr, newApp, context);
                            newApp.setIs_got(false);
                        }
                    }
                    newApp.setPost_id(null);
                    DatabaseReference appUnit = FirebaseDatabase.getInstance(myUtils.dbPath).getReference("users")
                            .child(curr_usr.getUid()).child("appointments_on_dates")
                            .child(String.valueOf(day)).child(String.valueOf(givenTime)).child(appointment.getPost_id());
                    appUnit.setValue(newApp);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } else {
            isGot.setVisibility(View.GONE);
            TextView warning = view.findViewById(R.id.warning);
            if (appointment.isIs_got()) {
                warning.setText("Лекарство было принято");
            } else {
                warning.setText("Лекарство не было принято");
            }
            view.findViewById(R.id.save).setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_today_appointment, container, false);
    }

    void showTimeDialog(TextView view) {
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker v, int hourOfDay, int minute) {
                Calendar res = Calendar.getInstance();
                res.set(0, 0, 0, hourOfDay, minute);
                time = hourOfDay * 3600 + minute * 60;
                view.setText(timeFormat.format(res.getTime()));
            }
        };
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), timeListener,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), true)
                .show();

    }
}