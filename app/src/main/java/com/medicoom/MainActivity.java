package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicoom.fragments.FarmacyFragment;
import com.medicoom.fragments.HistoryFragment;
import com.medicoom.fragments.InputMedicineFragment;
import com.medicoom.fragments.MainFragment;
import com.medicoom.fragments.TreatmentFragment;

//TODO: Прописать логику изменения лекарства в бд
public class MainActivity extends AppCompatActivity {
    public FirebaseAuth auth;
    final String MAIN_FRAGMENT = "main_fragment";

    private void setMainFragments(Fragment fr){
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
         ft.replace(R.id.fragment_container, fr, MAIN_FRAGMENT);
         ft.addToBackStack("name");
         ft.commit();
     }
    public void setBottomNavigation() {
        findViewById(R.id.menuToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.today);
                }
                MainFragment fr = new MainFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuFarmacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.farmacy);
                }
                FarmacyFragment fr = new FarmacyFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuTreatment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentFragment fr = new TreatmentFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryFragment fr = new HistoryFragment();
                setMainFragments(fr);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, GreetingActivity.class);
            startActivity(intent);
            finish();
        } else {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.today);
            }
            MainFragment fr = new MainFragment();
            setMainFragments(fr);
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                            instanceof InputMedicineFragment){
                        InputMedicineFragment fr = (InputMedicineFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fr.selfKill();
                    }else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                            instanceof MainFragment){
                        finish();
                    }else if (getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT) != null){
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.today);
                        }
                        MainFragment fr = new MainFragment();
                        setMainFragments(fr);
                    }

                }
            };
            Toolbar tlbar = findViewById(R.id.my_toolbar);
            tlbar.setTitle(R.string.today);
            MainActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);
            setBottomNavigation();

        }

    }
}