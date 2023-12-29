package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;

import java.util.ArrayList;

public class PomodoroSettingActivity extends AppCompatActivity {
    private EditText etShortBreakHours, etShortBreakMinutes, etShortBreakSeconds, etLongBreakHours, etLongBreakMinutes, etLongBreakSeconds;
    private ImageButton btnUpHourShort, btnDownHourShort, btnUpMinuteShort, btnDownMinuteShort, btnUpSecondShort, btnDownSecondShort,
            btnUpHourLong, btnDownHourLong, btnUpMinuteLong, btnDownMinuteLong, btnUpSecondLong, btnDownSecondLong;
    private ImageButton btnExit;
    private Button btnSave;
    private Switch swAutoBreak, swAutoPomodoro, swAutoTickCompletedTask, swAutoChangeTask;
    private SharedPreferences autoPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomorodo_setting);

        etShortBreakHours = findViewById(R.id.et_hour_short);
        etShortBreakMinutes = findViewById(R.id.et_minute_short);
        etShortBreakSeconds = findViewById(R.id.et_second_short);
        etLongBreakHours = findViewById(R.id.et_hour_long);
        etLongBreakMinutes = findViewById(R.id.et_minute_long);
        etLongBreakSeconds = findViewById(R.id.et_second_long);
        btnUpHourShort = findViewById(R.id.raise_hour_short);
        btnDownHourShort = findViewById(R.id.reduce_hour_short);
        btnUpMinuteShort = findViewById(R.id.raise_minute_short);
        btnDownMinuteShort = findViewById(R.id.reduce_minute_short);
        btnUpSecondShort = findViewById(R.id.raise_second_short);
        btnDownSecondShort = findViewById(R.id.reduce_second_short);
        btnUpHourLong = findViewById(R.id.raise_hour_long);
        btnDownHourLong = findViewById(R.id.reduce_hour_long);
        btnUpMinuteLong = findViewById(R.id.raise_minute_long);
        btnDownMinuteLong = findViewById(R.id.reduce_minute_long);
        btnUpSecondLong = findViewById(R.id.raise_second_long);
        btnDownSecondLong = findViewById(R.id.reduce_second_long);
        swAutoBreak =  findViewById(R.id.sw_auto_breaks);
        swAutoPomodoro = findViewById(R.id.sw_auto_poro);
        swAutoTickCompletedTask = findViewById(R.id.sw_auto_check_tasks);
        swAutoChangeTask = findViewById(R.id.sw_auto_switch_task);

        btnExit = findViewById(R.id.btn_pomo_setting_exit);
        btnSave = findViewById(R.id.btn_pomo_setting_save);

        PomodoroFragment pomodoroFragment = new PomodoroFragment();
        pomodoroFragment.AddEventForDuration(etShortBreakHours, etShortBreakMinutes, etShortBreakSeconds,
                btnUpHourShort, btnDownHourShort, btnUpMinuteShort, btnDownMinuteShort, btnUpSecondShort, btnDownSecondShort);
        pomodoroFragment.AddEventForDuration(etLongBreakHours, etLongBreakMinutes, etLongBreakSeconds,
                btnUpHourLong, btnDownHourLong, btnUpMinuteLong, btnDownMinuteLong, btnUpSecondLong, btnDownSecondLong);

        autoPreferences = getSharedPreferences("autoSwitches", MODE_PRIVATE);
        etShortBreakHours.setText(autoPreferences.getString("sHour", "00"));
        etShortBreakMinutes.setText(autoPreferences.getString("sMin", "05"));
        etShortBreakSeconds.setText(autoPreferences.getString("sSec", "00"));
        etLongBreakHours.setText(autoPreferences.getString("lHour", "00"));
        etLongBreakMinutes.setText(autoPreferences.getString("lMin", "10"));
        etLongBreakSeconds.setText(autoPreferences.getString("lSec", "00"));
        boolean autoValue = getIntent().getBooleanExtra("autoBreak", false);
        swAutoBreak.setChecked(autoPreferences.getBoolean("autoBreak", autoValue));
        autoValue = getIntent().getBooleanExtra("autoPomodoro", false);
        swAutoPomodoro.setChecked(autoPreferences.getBoolean("autoPomodoro",autoValue));
        autoValue = getIntent().getBooleanExtra("autoTick", false);
        swAutoTickCompletedTask.setChecked(autoPreferences.getBoolean("autoTick", autoValue));
        autoValue = getIntent().getBooleanExtra("autoChangeTask", false);
        swAutoChangeTask.setChecked(autoPreferences.getBoolean("autoChangeTask", autoValue));

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = autoPreferences.edit();

                Intent intent = new Intent();

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(etLongBreakHours.getText().toString() + ":" +
                        etLongBreakMinutes.getText().toString() + ":" +
                        etLongBreakSeconds.getText().toString());
                arrayList.add(etShortBreakHours.getText().toString() + ":" +
                        etShortBreakMinutes.getText().toString() + ":" +
                        etShortBreakSeconds.getText().toString());
                intent.putStringArrayListExtra("returnCounter" ,arrayList);
                editor.putString("lHour", etLongBreakHours.getText().toString());
                editor.putString("lMin", etLongBreakMinutes.getText().toString());
                editor.putString("lSec", etLongBreakSeconds.getText().toString());
                editor.putString("sHour", etShortBreakHours.getText().toString());
                editor.putString("sMin", etShortBreakMinutes.getText().toString());
                editor.putString("sSec", etShortBreakSeconds.getText().toString());

                boolean autoValue = swAutoBreak.isChecked();
                editor.putBoolean("autoBreak", autoValue);
                intent.putExtra("returnAutoBreak", autoValue);
                autoValue = swAutoPomodoro.isChecked();
                editor.putBoolean("autoPomodoro", autoValue);
                intent.putExtra("returnAutoPomodoro", autoValue);
                autoValue = swAutoTickCompletedTask.isChecked();
                editor.putBoolean("autoTick", autoValue);
                intent.putExtra("returnAutoTick", autoValue);
                autoValue = swAutoChangeTask.isChecked();
                editor.putBoolean("autoChangeTask", autoValue);
                intent.putExtra("returnAutoChangeTask", autoValue);

                editor.commit();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}