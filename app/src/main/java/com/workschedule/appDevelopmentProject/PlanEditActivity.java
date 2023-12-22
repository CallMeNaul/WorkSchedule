package com.workschedule.appDevelopmentProject;

import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.util.Locale;

public class PlanEditActivity extends AppCompatActivity
{
    private EditText eventNameET, eventMotaET;
    private Button eventDateTV, eventTimeTV, eventAddBT;
    private LocalTime time;
    private int hour, minute;
    private String eventName, eventMota, eventDate, eventTime;
    FirebaseDatabase database;
    DatabaseReference planReference;
    private CheckBox importantCb, disimportantCb;
    Plan plan;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.dark_blue));
        window.setNavigationBarColor(this.getResources().getColor(R.color.dark_blue));
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText(CalendarUtils.formattedTime(time));
        database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        planReference = database.getReference().child("Plan");
        eventAddBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plan = new Plan(eventNameET.getText().toString(),
                        eventMotaET.getText().toString(),
                        eventDateTV.getText().toString(),
                        eventTimeTV.getText().toString());
//                plan.setName(eventNameET.getText().toString());
//                plan.setMota(eventMotaET.getText().toString());
//                plan.setDate(eventDateTV.getText().toString());
//                plan.setTime(eventTimeTV.getText().toString());

                planReference.push().setValue(plan);
//                planReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        // inside the method of on Data change we are setting
//                        // our object class to our database reference.
//                        // data base reference will sends data to firebase.
//                        planReference.setValue(plan);
//
//                        // after adding this data we are showing toast message.
//                        Toast.makeText(PlanEditActivity.this, "data added", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // if the data is not added or it is cancelled then
//                        // we are displaying a failure toast message.
//                        Toast.makeText(PlanEditActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
//                    }
//                });
                Plan.plansList.add(plan);
                finish();
            }
        });
        disimportantCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (disimportantCb.isChecked())
                    importantCb.setChecked(false);
            }
        });
        importantCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (importantCb.isChecked())
                    disimportantCb.setChecked(false);
            }
        });
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.btn_eventTime);
        eventMotaET = findViewById(R.id.et_mo_ta);
        eventAddBT = findViewById(R.id.btn_generate);
        importantCb = findViewById(R.id.chb_important);
        disimportantCb = findViewById(R.id.chb_not_important);
        disimportantCb.setChecked(true);
    }

    public void backHomeAction(View view){
        finish();
    }

    public void popTimePicker(View view)
    {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                String text = String.format(Locale.getDefault(), "%02d:%02d:00",hour, minute);
                time = LocalTime.parse(text);
                eventTimeTV.setText(String.format(Locale.getDefault(), text));
            }
        };

         int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}