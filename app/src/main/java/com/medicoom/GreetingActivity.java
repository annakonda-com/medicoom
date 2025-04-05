package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GreetingActivity extends AppCompatActivity {
    LinearLayout to_reg;
    TextView no_reg;
    private boolean validateForm() {
        boolean valid = true;
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        if (email.getText() == null) {
            email.setError("Обязательное поле");
            valid = false;
        } else {
            email.setError(null);
        }
        if (password.getText() == null) {
            password.setError("Обязательное поле");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }
    private  void logIn(String email, String password){
        FirebaseAuth nAuth = FirebaseAuth.getInstance();
        nAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(GreetingActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(GreetingActivity.this, "Что-то пошло не так",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_greeting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.greeting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        to_reg = findViewById(R.id.to_reg_layout); // Зарегистрироваться
        to_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GreetingActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        no_reg = findViewById(R.id.no_reg); // Без регистрации
        no_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInAnonymously()
                        .addOnCompleteListener(GreetingActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(GreetingActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(GreetingActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        Button login = findViewById(R.id.login);    // Войти
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.email);
                EditText password = findViewById(R.id.password);
                if (validateForm()){
                    logIn(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }
}