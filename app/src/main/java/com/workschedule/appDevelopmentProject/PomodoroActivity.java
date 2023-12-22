package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PomodoroActivity extends AppCompatActivity {
    private TextView tvPomodoroCounter, tvShortBreakCounter, tvLongBreakCounter;
    private Button btnPomo, btnLongBreak, btnShortBreak, btnAddTask;
    private ListView lvTaskPomo;
    private ArrayList<PoromodoTask> tasks;
    private PomodoroTaskAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        initWidgets();
    }
    private void initWidgets()
    {
        tvPomodoroCounter = findViewById(R.id.tv_poro_counter);
        tvLongBreakCounter = findViewById(R.id.tv_longbrk_counter);
        tvShortBreakCounter = findViewById(R.id.tv_shbrk_counter);
        btnPomo = findViewById(R.id.btn_poro);
        btnShortBreak = findViewById(R.id.btn_shortbreak);
        btnLongBreak = findViewById(R.id.btn_longbreak);
        btnAddTask = findViewById(R.id.btn_add_task);
        lvTaskPomo = findViewById(R.id.lv_task_pomo);
        tasks = new ArrayList<>();
        tasks.add(new PoromodoTask("aa", "bb", "60:00"));
        adapter = new PomodoroTaskAdapter(PomodoroActivity.this,this, R.layout.row_lv_task_pomo, tasks);
        lvTaskPomo.setAdapter(adapter);
        btnPomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCounterLabel(btnPomo, btnLongBreak, btnShortBreak, tvPomodoroCounter, tvLongBreakCounter, tvShortBreakCounter);
            }
        });
        btnShortBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCounterLabel(btnShortBreak, btnLongBreak, btnPomo, tvShortBreakCounter, tvLongBreakCounter, tvPomodoroCounter);
            }
        });
        btnLongBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCounterLabel(btnLongBreak, btnShortBreak, btnPomo, tvLongBreakCounter, tvShortBreakCounter, tvPomodoroCounter);
            }
        });
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskDialog();
            }
        });
    }

    public void AddTaskDialog() {
        final Dialog dialog = new Dialog(this);
        customDialog(dialog, R.layout.add_pomodoro_task_dialog);

        EditText etName, etNote, etTime;
        Button btnCancel, btnOK;

        etName = dialog.findViewById(R.id.add_pomotask_et_task_name);
        etNote = dialog.findViewById(R.id.add_pomotask_et_note);
        etTime = dialog.findViewById(R.id.add_pomotask_et_time);
        btnOK = dialog.findViewById(R.id.btn_save_add_pomotask);
        btnCancel = dialog.findViewById(R.id.btn_cancel_add_pomotask);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.add(new PoromodoTask(etName.getText().toString(),
                        etNote.getText().toString(), etTime.getText().toString()));
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void AddTaskDialog(PoromodoTask poromodoTask) {
        final Dialog dialog = new Dialog(this);
        customDialog(dialog, R.layout.add_pomodoro_task_dialog);

        EditText etName, etNote, etTime;
        Button btnCancel, btnOK;

        etName = dialog.findViewById(R.id.add_pomotask_et_task_name);
        etNote = dialog.findViewById(R.id.add_pomotask_et_note);
        etTime = dialog.findViewById(R.id.add_pomotask_et_time);
        btnOK = dialog.findViewById(R.id.btn_save_add_pomotask);
        btnCancel = dialog.findViewById(R.id.btn_cancel_add_pomotask);

        etName.setText(poromodoTask.name);
        etNote.setText(poromodoTask.note);
        etTime.setText(poromodoTask.time);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = tasks.indexOf(poromodoTask);
                tasks.set(index, new PoromodoTask(etName.getText().toString(), etNote.getText().toString(), etTime.getText().toString()));
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void setCounterLabel(Button a, Button b, Button c, TextView aa, TextView bb, TextView cc){
        a.setBackgroundColor(ContextCompat.getColor(PomodoroActivity.this, R.color.pomo_button_color));
        b.setBackgroundColor(ContextCompat.getColor(PomodoroActivity.this, R.color.text_color));
        c.setBackgroundColor(ContextCompat.getColor(PomodoroActivity.this, R.color.text_color));
        aa.setVisibility(View.VISIBLE);
        bb.setVisibility(View.GONE);
        cc.setVisibility(View.GONE);
    }
    public void PomodoroReport(View view) {
        startActivity(new Intent(PomodoroActivity.this, PomodoroReportActivity.class));
    }

    public void PomodoroSetting(View view) {
        final Dialog dialog = new Dialog(this);
        customDialog(dialog, R.layout.pomorodo_setting_dialog);

        EditText etShortBreak, etLongBreak;
        ImageView btnExit;
        Button btnSave;

        etShortBreak = dialog.findViewById(R.id.et_shortbreak);
        etLongBreak = dialog.findViewById(R.id.et_longbreak);
        btnExit = dialog.findViewById(R.id.btn_pomo_setting_exit);
        btnSave = dialog.findViewById(R.id.btn_pomo_setting_save);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLongBreakCounter.setText(etLongBreak.getText().toString());
                tvShortBreakCounter.setText(etShortBreak.getText().toString());
                dialog.cancel();
            }
        });

        etShortBreak.setText(tvShortBreakCounter.getText().toString());
        etLongBreak.setText(tvLongBreakCounter.getText().toString());

        dialog.show();
    }
    public void customDialog(Dialog dialog, int id) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(id);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }
}