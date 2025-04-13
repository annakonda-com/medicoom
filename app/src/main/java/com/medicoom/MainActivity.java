package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//TODO: custom bottom nav, settings fragment
public class MainActivity extends AppCompatActivity {
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
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            Button btn = findViewById(R.id.button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, GreetingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            SettingsFragment stfr = new SettingsFragment();
            FrameLayout container = findViewById(R.id.fragment_container);
            container.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, stfr);
            ft.commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}