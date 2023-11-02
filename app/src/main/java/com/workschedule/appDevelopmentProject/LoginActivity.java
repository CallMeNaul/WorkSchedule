package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private CheckBox cbRemember;
    SharedPreferences sharedPreferences = null;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        register = findViewById((R.id.btn_register));
        cbRemember = findViewById(R.id.check_remember);
        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username.setText(sharedPreferences.getString("email", ""));
        password.setText(sharedPreferences.getString("password",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked", false));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString();
                String psw = password.getText().toString();
                setLogin(email, psw);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , RegisterActivity.class));
                finish();
            }
        });
    }
    private void setLogin(String email, String psw)
    {
        auth.signInWithEmailAndPassword(email, psw)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    if (cbRemember.isChecked()){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", psw);
                        editor.putBoolean("checked", true);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("email");
                        editor.remove("password");
                        editor.remove("checked");
                        editor.commit();
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Quá trình đăng nhập thất bại
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }
}