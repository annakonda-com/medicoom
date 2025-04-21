package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BasicActivity extends AppCompatActivity {
    public FirebaseAuth auth;
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
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getSupportFragmentManager().getFragments().isEmpty()){
                        Intent intent = new Intent(BasicActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        getSupportFragmentManager().popBackStack();
                        RelativeLayout body = findViewById(R.id.body);
                        body.setVisibility(View.VISIBLE);
                    }

                }
            };
            // Toolbar tlbar = findViewById(R.id.my_toolbar);
            // tlbar.setTitle(R.string.today);
            BasicActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);
           /* if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.today);
            }*/

        }
    }
}