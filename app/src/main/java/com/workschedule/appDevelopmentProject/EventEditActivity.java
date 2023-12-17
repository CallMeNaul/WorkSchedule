package com.workschedule.appDevelopmentProject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    private LocalTime time;
    private EditText eventMotaET;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText(CalendarUtils.formattedTime(time));
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
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
}