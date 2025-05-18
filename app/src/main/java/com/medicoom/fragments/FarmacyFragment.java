package com.medicoom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

import java.util.ArrayList;

public class FarmacyFragment extends Fragment {

    private final String NAME = "name";
    private final String DOSAGE = "dosage";
    private final String NUM_OF_TABLETS = "num_of_tablets";
    private final String GOOD_UNTIL = "good_until";
    private final String REMIND_WHEN = "remind_when";
    private final String POST_ID = "post_id";

    final String FULL_SCREEN = "full_screen";

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
                InputMedicineFragment inpfr = new InputMedicineFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.remove(FarmacyFragment.this);
                ft.replace(R.id.main, inpfr, FULL_SCREEN);
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
        ArrayList<Medicine> med_list = new ArrayList<>();

        MedicineAdapter listAdapter = new MedicineAdapter(getActivity(), med_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicine curr_med = listAdapter.getItem(position);

                ChangeMedicineFragment chfr = new ChangeMedicineFragment();

                Bundle bund = new Bundle();
                bund.putString(NAME, curr_med.getName());
                bund.putString(DOSAGE, curr_med.getDosage());
                bund.putInt(NUM_OF_TABLETS, curr_med.getNum_of_tablets());
                bund.putInt(REMIND_WHEN, curr_med.getRemind_when());
                bund.putInt(GOOD_UNTIL, curr_med.getGood_until());
                bund.putString(POST_ID, curr_med.getPostId());

                chfr.setArguments(bund);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.remove(FarmacyFragment.this);
                ft.replace(R.id.main, chfr, FULL_SCREEN);
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
                        medicine.setPostId(ds.getKey());
                        med_list.add(medicine);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "loadMedicine:onCancelled", databaseError.toException());
            }
        };
        mDatabase.orderByChild("deleted").equalTo(false).addValueEventListener(medListener);
        return contentView;
    }
}