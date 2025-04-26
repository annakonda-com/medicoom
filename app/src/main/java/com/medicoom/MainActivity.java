package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.medicoom.fragments.MainFragment;
import com.medicoom.fragments.TreatmentFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public FirebaseAuth auth;
    final String MAIN_FRAGMENT = "main_fragment";
    final String FULL_SCREEN = "full_screen";

    private void setMainFragments(Fragment fr) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fragment_container, fr, MAIN_FRAGMENT);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void setBottomNavigation() {
        Toolbar tlbr = findViewById(R.id.my_toolbar);
        findViewById(R.id.menuToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlbr.setTitle(R.string.today);
                MainFragment fr = new MainFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuFarmacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlbr.setTitle(R.string.farmacy);
                FarmacyFragment fr = new FarmacyFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuTreatment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlbr.setTitle(R.string.treatment);
                TreatmentFragment fr = new TreatmentFragment();
                setMainFragments(fr);
            }
        });
        findViewById(R.id.menuHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlbr.setTitle(R.string.history);
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
            /*if (getSupportFragmentManager().getFragments().isEmpty()) {
                Toolbar tlbr = findViewById(R.id.my_toolbar);
                tlbr.setTitle(R.string.today);
                MainFragment fr = new MainFragment();
                setMainFragments(fr);
            }*/
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    Fragment last_fragment = fragments.get(fragments.size() - 1);
                    Log.d("MAYTAG", "------");
                    for(Fragment x: getSupportFragmentManager().getFragments()){
                        Log.d("MAYTAG", x.toString());
                    }
                    Log.d("MAYTAG", "------");

                    if (last_fragment.getTag().equals(FULL_SCREEN)) {
                        getSupportFragmentManager().popBackStack();
                    } else if (last_fragment instanceof MainFragment) {
                        finish();
                    } else if (last_fragment.getTag().equals(MAIN_FRAGMENT)) {
                        Toolbar tlbr = findViewById(R.id.my_toolbar);
                        tlbr.setTitle(R.string.today);
                        MainFragment fr = new MainFragment();
                        setMainFragments(fr);
                    }
                }
            };
            /*Toolbar tlbar = findViewById(R.id.my_toolbar);
            tlbar.setTitle(R.string.today);*/
            MainActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);
            setBottomNavigation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment current_fragment = getSupportFragmentManager().findFragmentByTag("main_fragment");
        Toolbar tlbr = findViewById(R.id.my_toolbar);
        if (current_fragment != null){
            if (current_fragment instanceof MainFragment){
                tlbr.setTitle(R.string.today);
            } else if (current_fragment instanceof  FarmacyFragment){
                tlbr.setTitle(R.string.farmacy);
            } else if (current_fragment instanceof TreatmentFragment){
                tlbr.setTitle(R.string.treatment);
            } else if (current_fragment instanceof HistoryFragment){
                tlbr.setTitle(R.string.history);
            }
        }else{
            tlbr.setTitle(R.string.today);
            MainFragment fr = new MainFragment();
            setMainFragments(fr);
        }
    }
}