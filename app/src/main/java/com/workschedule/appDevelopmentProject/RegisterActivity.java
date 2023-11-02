package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText username;
    private EditText password;
    private EditText password_confirm;

    private CheckBox cbRemember;

    SharedPreferences sharedPreferences;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.btn_register);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.check_remember);
        password_confirm = findViewById(R.id.et_password_confirm);
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String psw = password.getText().toString();
                if (validateEmail() && validatePassword()) {
                    registerUser(email, psw);
                }
            }
        });
    }
    private void registerUser(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                            if (cbRemember.isChecked()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putBoolean("checked", true);
                                editor.commit();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Fail to register your email", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private boolean validateEmail() {
        String email = username.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    private boolean validatePassword() {
        String pass = password.getText().toString().trim();
        String co_password = password_confirm.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(RegisterActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(co_password)) {
            Toast.makeText(RegisterActivity.this, "Enter Your Confirm Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass.length() <= 6) {
            Toast.makeText(RegisterActivity.this, "Password is Too Short", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pass.equals(co_password)){
            Toast.makeText(RegisterActivity.this, "Wrong Confirm Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}