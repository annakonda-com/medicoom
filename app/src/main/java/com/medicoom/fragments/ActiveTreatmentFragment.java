package com.medicoom.fragments;

import static com.medicoom.utils.myUtils.dateFormat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.Appointment;
import com.medicoom.javaClasses.AppointmentAdapter;

import java.util.ArrayList;

public class ActiveTreatmentFragment extends Fragment {

    final String NAME = "name";
    final String START_DATE = "start_date";
    final String REST_TO_GET = "rest_to_get";
    final String YOU_ARE_GETTING = "you_are_getting";
    final String TIMES = "times";
    final String GRAPHIC = "graphic";
    final String HOW_TO_GET = "how_to_get";


    final String MAIN_FRAGMENT = "main_fragment";
    final String FULL_SCREEN = "full_screen";


    public ActiveTreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_active_treatment, container, false);
        ListView listView = contentView.findViewById(R.id.appointments_list);
        ArrayList<Appointment> app_list = new ArrayList<>();
        AppointmentAdapter listAdapter = new AppointmentAdapter(getActivity(), app_list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment curr_app = listAdapter.getItem(position);

                ChangeAppointmentFragment chfr = new ChangeAppointmentFragment();

                Bundle bund = new Bundle();
                bund.putString(NAME, ((TextView) view.findViewById(R.id.appointment_name)).getText().toString());
                bund.putString(START_DATE, "с " + dateFormat.format(curr_app.getStart_date() * 1000L));
                String rest_to_get;
                if (curr_app.getStart_date() != -1) {
                    rest_to_get = "Принимаете постоянно";
                } else {
                    //TODO: Добавить получение оставшихся дней из appointments_on_dates
                    rest_to_get = "Осталось принимать ещё";
                }
                bund.putString(REST_TO_GET, rest_to_get);
                String you_are_getting = "Сколько раз вы принимаете в день: " +
                        curr_app.getTimes().size();
                bund.putString(YOU_ARE_GETTING, you_are_getting);
                ArrayList<Integer> times_list = new ArrayList<>(curr_app.getTimes());
                bund.putIntegerArrayList(TIMES, times_list);
                if (curr_app.getDays_of_week() != null) {
                    StringBuilder graphic = new StringBuilder("По дням недели: ");
                    for (int x : curr_app.getDays_of_week()) {
                        switch (x) {
                            case 0:
                                graphic.append("понедельник ");
                                break;
                            case 1:
                                graphic.append("вторник ");
                                break;
                            case 2:
                                graphic.append("среда ");
                                break;
                            case 3:
                                graphic.append("четверг ");
                                break;
                            case 4:
                                graphic.append("пятница ");
                                break;
                            case 5:
                                graphic.append("суббота ");
                                break;
                            case 6:
                                graphic.append("воскресенье ");
                                break;
                        }

                    }
                    bund.putString(GRAPHIC, graphic.toString());
                } else if (curr_app.getEvery_x_days() == 1) {
                    String graphic = "Каждый день";
                    bund.putString(GRAPHIC, graphic);
                } else {
                    String graphic = "Раз в несколько дней: каждые " + curr_app.getEvery_x_days();
                    bund.putString(GRAPHIC, graphic);
                }

                String how_to_get = "Принимать нужно " + curr_app.getHow_to_get();
                bund.putString(HOW_TO_GET, how_to_get);
                chfr.setArguments(bund);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.main, chfr, FULL_SCREEN);
                ft.addToBackStack(null);
                ft.remove(getParentFragment());
                ft.commit();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid() + "/appointments");
        ValueEventListener medListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!app_list.isEmpty()) {
                    app_list.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Appointment med = ds.getValue(Appointment.class);
                    if (med != null) {
                        med.setPost_id(ds.getKey());
                        if (!med.isDeleted()) {
                            app_list.add(med);
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.orderByChild("archive").equalTo(false).addValueEventListener(medListener);
        return contentView;
    }
}