package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.LocalTime;
import java.util.Locale;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET;
    private TextView eventDateTV, eventTime;
    private LocalTime time;
    private EditText eventMotaET;
    int hour, minute;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTime.setText(CalendarUtils.formattedTime(time));
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTime = findViewById(R.id.btn_eventTime);
        eventMotaET = findViewById(R.id.et_mo_ta);
    }

    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        String eventMota = eventMotaET.getText().toString();
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time, eventMota);
        Event.eventsList.add(newEvent);
        finish();
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
                eventTime.setText(String.format(Locale.getDefault(), text));
            }
        };

         int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}