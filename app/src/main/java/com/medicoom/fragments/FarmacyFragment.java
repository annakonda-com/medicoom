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

    public FarmacyFragment() {
        // Required empty public constructor
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
                BottomAppBar bottomBar = getActivity().findViewById(R.id.bottomBar);
                bottomBar.setVisibility(View.GONE);

                Toolbar tlbt = getActivity().findViewById(R.id.my_toolbar);
                tlbt.setVisibility(View.GONE);

                FloatingActionButton ftb = getActivity().findViewById(R.id.add_medicine);
                ftb.setVisibility(View.GONE);

                FloatingActionButton nftb = getActivity().findViewById(R.id.fab);
                nftb.setVisibility(View.GONE);

               /* FrameLayout frame = findViewById(R.id.fragment_container);
                frame.setVisibility(View.);*/

                //RelativeLayout body = findViewById(R.id.body);
                //body.setVisibility(View.GONE);
                InputMedicineFragment inpfr = new InputMedicineFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, inpfr);
                ft.addToBackStack("name");
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