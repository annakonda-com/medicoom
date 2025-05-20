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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.medicoom.javaClasses.TodayAppointmentAdapter;
import com.medicoom.utils.myUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class MainFragment extends Fragment {
    final String MAIN_FRAGMENT = "main_fragment";
    final String FULL_SCREEN = "full_screen";
    ArrayList<Map.Entry<Integer, AppointmentOnDate>> appointments_on_times = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar tlbr = getActivity().findViewById(R.id.my_toolbar);
                tlbr.setTitle(R.string.treatment);

                InputAppointmentFragment input_fr = new InputAppointmentFragment();
                TreatmentFragment treatment = new TreatmentFragment();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.add(R.id.fragment_container, treatment, MAIN_FRAGMENT);
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                transaction1.remove(treatment);
                transaction1.replace(R.id.main, input_fr, FULL_SCREEN);
                transaction1.addToBackStack(null);
                transaction1.commit();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid());
        mDatabase.child("appointments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    fab.animate().alpha(1.0f);
                    fab.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.send_comment_layout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        DatabaseReference comment = FirebaseDatabase.getInstance(myUtils.dbPath)
                .getReference("users").child(currentUser.getUid())
                .child("appointments_on_dates")
                .child(String.valueOf((int) (today.getTimeInMillis() / 1000L))).child("comment");
        EditText inputComment = view.findViewById(R.id.send_comment);
        TextInputLayout sendComment = view.findViewById(R.id.send_comment_layout);
        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    inputComment.setText(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendComment.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dayComment = inputComment.getText().toString();
                inputComment.clearFocus();
                if (!dayComment.isEmpty()) {
                    comment.setValue(dayComment);
                } else {
                    comment.setValue(null);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        TodayAppointmentAdapter myAdapter = new TodayAppointmentAdapter(getContext(), appointments_on_times);
        ListView today_app = view.findViewById(R.id.today_list);
        today_app.setAdapter(myAdapter);
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        getTodayAppointments((int) (today.getTimeInMillis() / 1000L), myAdapter);

        today_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<Integer, AppointmentOnDate> item = myAdapter.getItem(position);
                AppointmentOnDate curr_app = item.getValue();

                ChangeTodayAppointmentFragment chfr = new ChangeTodayAppointmentFragment();
                Bundle info = new Bundle();
                info.putBundle("appointment", curr_app.makeBundle());
                info.putInt("date", (int) (today.getTimeInMillis() / 1000L));
                int time = item.getKey();
                info.putInt("time", time);

                chfr.setArguments(info);

                FragmentTransaction ftt = getActivity().getSupportFragmentManager().beginTransaction();
                ftt.replace(R.id.main, chfr, FULL_SCREEN);
                ftt.remove(MainFragment.this);
                getActivity().getSupportFragmentManager().popBackStack();
                ftt.addToBackStack(null);
                ftt.commit();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getTodayAppointments(int today_date, TodayAppointmentAdapter adapter) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance
                        ("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users" + "/" + currentUser.getUid());

        mDatabase.child("appointments_on_dates").child(String.valueOf(today_date))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        appointments_on_times.clear();
                        List<CompletableFuture<Void>> futures = new ArrayList<>();

                        for (DataSnapshot time : dataSnapshot.getChildren()) {
                            for (DataSnapshot child_child : time.getChildren()) {
                                AppointmentOnDate curr_app = child_child.getValue(AppointmentOnDate.class);
                                DatabaseReference appoint = FirebaseDatabase.getInstance(myUtils.dbPath)
                                        .getReference("users").child(currentUser.getUid())
                                        .child("appointments").child(curr_app.getAppointment_id());

                                CompletableFuture<Void> future = new CompletableFuture<>();
                                futures.add(future);

                                appoint.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Appointment firstApp = snapshot.getValue(Appointment.class);
                                        if (!(firstApp.isDeleted() || firstApp.isArchive() || firstApp.isOn_pause())) {
                                            curr_app.setPost_id(child_child.getKey());
                                            appointments_on_times.add(new AbstractMap.SimpleEntry<>(
                                                    Integer.valueOf(time.getKey()), curr_app));
                                        }
                                        future.complete(null);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        future.completeExceptionally(error.toException());
                                    }
                                });
                            }
                        }
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .thenRun(() -> adapter.notifyDataSetChanged());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });

}}