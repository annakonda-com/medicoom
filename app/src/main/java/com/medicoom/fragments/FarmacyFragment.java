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
import android.widget.ListView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.R;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicineAdapter;
import com.medicoom.javaClasses.MedicinePost;

import java.util.ArrayList;

public class FarmacyFragment extends Fragment {

    private final String NAME = "name";
    private final String DOSAGE = "dosage";
    private final String NUM_OF_TABLETS = "num_of_tablets";
    private final String GOOD_UNTIL = "good_until";
    private final String REMIND_WHEN = "remind_when";
    private final String POST_ID = "post_id";

    public FarmacyFragment() {
        // Required empty public constructor
    }

    private void prepareSpace(){
        BottomAppBar bottomBar = getActivity().findViewById(R.id.bottomBar);
        bottomBar.animate().alpha(0.0f);
        bottomBar.setVisibility(View.GONE);

        Toolbar tlbt = getActivity().findViewById(R.id.my_toolbar);
        tlbt.animate().alpha(0.0f);
        tlbt.setVisibility(View.GONE);

        FloatingActionButton ftb = getActivity().findViewById(R.id.add_medicine);
        ftb.animate().alpha(0.0f);
        ftb.setVisibility(View.GONE);

        FloatingActionButton nftb = getActivity().findViewById(R.id.fab);
        nftb.animate().alpha(0.0f);
        nftb.setVisibility(View.GONE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.add_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSpace();
               /* FrameLayout frame = findViewById(R.id.fragment_container);
                frame.setVisibility(View.);*/

                //RelativeLayout body = findViewById(R.id.body);
                //body.setVisibility(View.GONE);
                InputMedicineFragment inpfr = new InputMedicineFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.fragment_container, inpfr);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_farmacy, container, false);
        ListView listView = contentView.findViewById(R.id.medicine_list);
        ArrayList<MedicinePost> med_list = new ArrayList<>();

        MedicineAdapter listAdapter = new MedicineAdapter(getActivity(), med_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prepareSpace();

                MedicinePost curr_med = listAdapter.getItem(position);

                ChangeMedicineFragment chfr = new ChangeMedicineFragment();

                Bundle bund = new Bundle();
                bund.putString(NAME, curr_med.getName());
                bund.putString(DOSAGE, curr_med.getDosage());
                bund.putInt(NUM_OF_TABLETS, curr_med.getNum_of_tablets());
                bund.putInt(REMIND_WHEN, curr_med.getRemind_when());
                bund.putInt(GOOD_UNTIL, curr_med.getGood_until());
                bund.putString(POST_ID, curr_med.getPostId());

                chfr.setArguments(bund);

                Log.d("QWERTY", curr_med.toString());

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.fragment_container, chfr);
                ft.addToBackStack(null);
                ft.commit();
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
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(medListener);
        return contentView;
    }
}