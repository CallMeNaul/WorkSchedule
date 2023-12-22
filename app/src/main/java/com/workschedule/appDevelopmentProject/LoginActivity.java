package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button login;
    private TextView to_register;
    private CheckBox cbRemember;
    private TextView forget_text;
    private ImageButton imgView;
    private ImageButton imgHide;
    SharedPreferences sharedPreferences = null;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        to_register = findViewById(R.id.register_text);
        cbRemember = findViewById(R.id.check_remember);
        forget_text = findViewById(R.id.forget_password_text);
        imgHide = findViewById(R.id.img_btn_hide);
        imgView = findViewById(R.id.img_btn_view);
        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        username.setText(sharedPreferences.getString("email", ""));
        password.setText(sharedPreferences.getString("password",""));
        cbRemember.setChecked(sharedPreferences.getBoolean("checked", false));

        // Kiểm tra mật khẩu và email rồi cho phép đăng nhập
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString();
                String psw = password.getText().toString();
                if (validatePassword() && validateEmail())
                    setLogin(email, psw);
            }
        });

        // Di chuyển đến màn hình đăng ký
        // Vì một lí do bí ẩn nào đó mà cái này nó chứ báo lỗi đối tượng ImageButton chưa được tạo
        // trong khi cái này là textview và đã được khai báo phía trên rồi
        // Đã kiểm tra mọi activity không có cái nào trùng ID với cái này
        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        // Di chuyển đến màn hình quên mật khẩu
        forget_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                finish();
            }
        });

        // Hiện mật khẩu khi nhập
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHide.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.GONE);
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        // Ẩn mật khẩu khi nhập
        imgHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHide.setVisibility(View.GONE);
                imgView.setVisibility(View.VISIBLE);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
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
                    startActivity(new Intent(LoginActivity.this, WeekViewActivity.class));
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

    private boolean validateEmail() {
        String email = username.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    private boolean validatePassword() {
        String pass = password.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(LoginActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}