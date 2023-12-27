package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;

public class PomodoroSettingActivity extends AppCompatActivity {
    private EditText etShortBreakHours, etShortBreakMinutes, etShortBreakSeconds, etLongBreakHours, etLongBreakMinutes, etLongBreakSeconds;
    private ImageButton btnUpHourShort, btnDownHourShort, btnUpMinuteShort, btnDownMinuteShort, btnUpSecondShort, btnDownSecondShort,
            btnUpHourLong, btnDownHourLong, btnUpMinuteLong, btnDownMinuteLong, btnUpSecondLong, btnDownSecondLong;
    private ImageView btnExit;
    private Button btnSave;
    Switch swAutoBreak, swAutoPomodoro, swAutoTickCompletedTask, swAutoChangeTask;
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
        btnUpMinuteShort = findViewById(R.id.raise_minute_short);;
        btnDownMinuteShort = findViewById(R.id.reduce_minute_short);;
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
        /*InitOnListener(pomodoroFragment);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pomodoroFragment.setBreak(etLongBreakHours.getText().toString(), etLongBreakMinutes.getText().toString(), etLongBreakSeconds.getText().toString(),
                        etShortBreakHours.getText().toString(), etShortBreakMinutes.getText().toString(), etShortBreakSeconds.getText().toString());
            }
        });

        String longg = pomodoroFragment.getLongBreak(),
                shortt = pomodoroFragment.getShortBreak();
        String[] longgg = longg.split(":"),
                shorttt = shortt.split(":");
        etLongBreakHours.setText(longgg[0]);
        etLongBreakMinutes.setText(longgg[1]);
        etLongBreakSeconds.setText(longgg[2]);
        etShortBreakHours.setText(shorttt[0]);
        etShortBreakMinutes.setText(shorttt[1]);
        etShortBreakSeconds.setText(shorttt[2]);*/

        pomodoroFragment.AddEventForDuration(etShortBreakHours, etShortBreakMinutes, etShortBreakSeconds,
                btnUpHourShort, btnDownHourShort, btnUpMinuteShort, btnDownMinuteShort, btnUpSecondShort, btnDownSecondShort);
        pomodoroFragment.AddEventForDuration(etLongBreakHours, etLongBreakMinutes, etLongBreakSeconds,
                btnUpHourLong, btnDownHourLong, btnUpMinuteLong, btnDownMinuteLong, btnUpSecondLong, btnDownSecondLong);
        /*boolean[] auto = pomodoroFragment.GetAutoVariables();

        swAutoBreak.setChecked(auto[0]);
        swAutoPomodoro.setChecked(auto[1]);
        swAutoTickCompletedTask.setChecked(auto[2]);
        swAutoChangeTask.setChecked(auto[3]);
        swAutoBreak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pomodoroFragment.SetAutoVariables(isChecked, null, null, null);
            }
        });
        swAutoPomodoro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pomodoroFragment.SetAutoVariables(null, isChecked, null, null);
            }
        });
        swAutoTickCompletedTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pomodoroFragment.SetAutoVariables(null, null, isChecked, null);
            }
        });
        swAutoChangeTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pomodoroFragment.SetAutoVariables(null, null, null, isChecked);
            }
        });*/
    }
}