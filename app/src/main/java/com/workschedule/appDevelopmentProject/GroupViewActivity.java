package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupViewActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        btnBack = findViewById(R.id.btn_back_to_groupview);
        tvGroupName = findViewById(R.id.tv_groupview_name);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvGroupName.setText(getIntent().getStringExtra("groupName"));
    }
}