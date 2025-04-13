package com.medicoom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicoom.utils.myUtils;

public class RegistrationActivity extends AppCompatActivity {
    private boolean validateForm() {
        boolean valid = true;
        EditText nameSurname, email, password1, password2;
        nameSurname = findViewById(R.id.reg_name_input);
        email = findViewById(R.id.registration_email);
        password1 = findViewById(R.id.reg_password);
        password2 = findViewById(R.id.reg_password1);
        if (email.getText().toString().isEmpty()) {
            email.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(email.getText().toString())) {
                email.setError("Поле пустое");
                valid = false;
            }else{
                email.setError(null);
            }
        }
        if (password1.getText().toString().isEmpty()) {
            password1.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(password1.getText().toString())) {
                password1.setError("Поле пустое");
                valid = false;
            }else{
                password1.setError(null);
            }
        }
        if (password2.getText().toString().isEmpty()) {
            password2.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(password2.getText().toString())) {
                password2.setError("Поле пустое");
                valid = false;
            }else{
                password2.setError(null);
            }
        }
        if (nameSurname.getText().toString().isEmpty()) {
            nameSurname.setError("Обязательное поле");
            valid = false;
        } else {
            if (myUtils.isSpace(nameSurname.getText().toString())) {
                nameSurname.setError("Поле пустое");
                valid = false;
            }else{
                nameSurname.setError(null);
            }
        }
        if (!password1.getText().toString().equals(password2.getText().toString())) {
            password1.setError("Пароли должны совпадать!");
            password2.setError("Пароли должны совпадать!");
            valid = false;
        } else {
            password1.setError(null);
            password2.setError(null);
        }
        return valid;
    }

    private void do_registration(String name, String email, String password1, String password2) {
        FirebaseAuth nAuth = FirebaseAuth.getInstance();
        nAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    nAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("MY_TAG", "Письмо отправлено");
                                        DatabaseReference mDatabase = FirebaseDatabase
                                                .getInstance("https://medicoom-abc-default-rtdb.europe-west1.firebasedatabase.app/")
                                                .getReference("users");
                                        mDatabase.child(nAuth.getCurrentUser().getUid()).child("name").setValue(name);
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(RegistrationActivity.this, WaitVerifideActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("MY_TAG", task.getException().getMessage().toString());
                                        Toast.makeText(RegistrationActivity.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    if (task.getException().getMessage() != null) {
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btn = findViewById(R.id.registration);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameSurname, email, password1, password2;
                nameSurname = findViewById(R.id.reg_name_input);
                email = findViewById(R.id.registration_email);
                password1 = findViewById(R.id.reg_password);
                password2 = findViewById(R.id.reg_password1);
                if (validateForm()) {
                    do_registration(nameSurname.getText().toString(), email.getText().toString(),
                            password1.getText().toString(), password2.getText().toString());
                }
            }
        });

    }
}