package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicoom.fragments.InputMedicineFragment;
import com.medicoom.javaClasses.Medicine;
import com.medicoom.javaClasses.MedicineAdapter;
import com.medicoom.javaClasses.MedicinePost;

import java.util.ArrayList;

public class FarmacyActivity extends BasicActivity {
    public void setBottomNavigation(){
        findViewById(R.id.menuToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmacyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menuFarmacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmacyActivity.this, FarmacyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menuTreatment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmacyActivity.this, TreatmentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menuHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmacyActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmacy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setBottomNavigation();
        findViewById(R.id.add_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*BottomAppBar bottomBar = findViewById(R.id.bottomBar);
                bottomBar.setVisibility(View.GONE);

                Toolbar tlbt = findViewById(R.id.my_toolbar);
                tlbt.setVisibility(View.GONE);

                FloatingActionButton ftb = findViewById(R.id.add_medicine);
                ftb.setVisibility(View.GONE);

                FloatingActionButton nftb = findViewById(R.id.fab);
                nftb.setVisibility(View.GONE);*/

               /* FrameLayout frame = findViewById(R.id.fragment_container);
                frame.setVisibility(View.);*/

                RelativeLayout body = findViewById(R.id.body);
                body.setVisibility(View.GONE);

                InputMedicineFragment inpfr = new InputMedicineFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, inpfr);
                ft.addToBackStack("name");
                ft.commit();
            }
        });
        ListView listView = findViewById(R.id.medicine_list);
        ArrayList<MedicinePost> med_list = new ArrayList<>();

        MedicineAdapter listAdapter = new MedicineAdapter(FarmacyActivity.this, med_list);
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

    }
}