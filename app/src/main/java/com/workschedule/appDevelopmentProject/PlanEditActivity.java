//package com.workschedule.appDevelopmentProject;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.AlertDialog;
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.time.LocalTime;
//import java.util.Locale;
//
//public class PlanEditActivity extends AppCompatActivity
//{
//    private EditText planNameET, planMotaET;
//    private Button planDateTV, planTimeTV, planAddBT;
//    private LocalTime time;
//    private int hour, minute;
//    private String planName, planMota, planDate, planTime;
//    FirebaseDatabase database;
//    DatabaseReference planReference;
//    Plan plan;
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_edit);
//        initWidgets();
//        time = LocalTime.now();
//        planDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
//        planTimeTV.setText(CalendarUtils.formattedTime(time));
//        database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        planReference = database.getReference().child("Plan");
////        planAddBT.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                planName = planNameET.getText().toString();
////                planMota = planMotaET.getText().toString();
////                planDate = planDateTV.getText().toString();
////                planTime = planTimeTV.getText().toString();
////                plan = new Plan(planName, planMota, planDate, planTime);
////                Plan.plansList.add(plan);
////                planReference.push().setValue(plan);
////                planReference.addValueEventListener(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot snapshot) {
////                        // inside the method of on Data change we are setting
////                        // our object class to our database reference.
////                        // data base reference will sends data to firebase.
////                        planReference.setValue(plan);
////
////                        // after adding this data we are showing toast message.
////                        Toast.makeText(PlanEditActivity.this, "data added", Toast.LENGTH_SHORT).show();
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError error) {
////                        // if the data is not added or it is cancelled then
////                        // we are displaying a failure toast message.
////                        Toast.makeText(PlanEditActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
////                    }
////                });
//
//                Toast.makeText(PlanEditActivity.this, "data added", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
//
//    private void initWidgets()
//    {
//        planNameET = findViewById(R.id.eventNameET);
//        planDateTV = findViewById(R.id.eventDateTV);
//        planTimeTV = findViewById(R.id.btn_eventTime);
//        planMotaET = findViewById(R.id.et_mo_ta);
//        planAddBT = findViewById(R.id.btn_xacnhan);
//    }
//
//    public void backHomeAction(View view){
//        finish();
//    }
//
//    public void popTimePicker(View view)
//    {
//        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
//        {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
//            {
//                hour = selectedHour;
//                minute = selectedMinute;
//                String text = String.format(Locale.getDefault(), "%02d:%02d:00",hour, minute);
//                time = LocalTime.parse(text);
//                planTimeTV.setText(text);
//            }
//        };
//
//         int style = AlertDialog.THEME_HOLO_DARK;
//
//        TimePickerDialog timePickerDialog =
//                new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
//
//        timePickerDialog.setTitle("Select Time");
//        timePickerDialog.show();
//    }
//}