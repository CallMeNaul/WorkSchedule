package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = findViewById(R.id.btn_reg);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        auth = FirebaseAuth.getInstance();
        register.setBackgroundColor(android.graphics.Color.parseColor("#80FFBF"));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String psw = password.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(psw))
                {
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(psw.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password quite short", Toast.LENGTH_SHORT).show();
                }
                registerUser(email, psw);
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
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Fail to register your email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}