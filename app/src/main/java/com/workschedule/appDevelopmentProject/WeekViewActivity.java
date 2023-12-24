package com.workschedule.appDevelopmentProject;

import static com.workschedule.appDevelopmentProject.CalendarUtils.daysInWeekArray;
import static com.workschedule.appDevelopmentProject.CalendarUtils.monthYearFromDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText, tvAll, tvImportant;
    private RecyclerView calendarRecyclerView;
    private ImageView dashboard;
    private RecyclerView planRecyclerView;
    private PlanAdapter planAdapter;
    private Button buttonAddPlan;
    private int hour, minute;
    private LocalTime time;
    ArrayList<Plan> arrayList;
    FirebaseDatabase database;
    DatabaseReference planReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();
        database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
        planReference = database.getReference("Plan");
        planReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot planSnapshot: snapshot.getChildren()) {
                    String planDate = planSnapshot.child("date").getValue(String.class);
                    String planMota = planSnapshot.child("mota").getValue(String.class);
                    String planName = planSnapshot.child("name").getValue(String.class);
                    String planTime = planSnapshot.child("time").getValue(String.class);
                    String planKey = planSnapshot.getKey();
                    Plan plan = new Plan(planKey, planName, planMota, planDate, planTime);
                    Plan.plansList.add(plan);
                }
                setPlanAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        buttonAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddPlan();
            }
        });
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        planRecyclerView = findViewById(R.id.planRecyclerView);
        dashboard = findViewById(R.id.darhboard);
        tvAll = findViewById(R.id.all_);
        tvImportant = findViewById(R.id.important_);

        planRecyclerView = findViewById(R.id.planRecyclerView);
        planRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        planRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(planRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        planRecyclerView.addItemDecoration(itemDecoration);
        planRecyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();
        planAdapter = new PlanAdapter(this, arrayList);
        planRecyclerView.setAdapter(planAdapter);

        buttonAddPlan = findViewById(R.id.btn_new_plan);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setPlanAdapter();
    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setPlanAdapter();
    }

    public void setPlanAdapter()
    {
        ArrayList<Plan> dailyPlans = Plan.plansForDate(CalendarUtils.selectedDate);
        PlanAdapter planAdapter = new PlanAdapter(this, dailyPlans);
        planRecyclerView.setAdapter(planAdapter);
    }
    public void openDialogEditPlan(String keyid, String name, String mota, String date, String time) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_event_edit);
        EditText planNameET = (EditText) dialog.findViewById(R.id.eventNameET);
        Button planDateTV = (Button) dialog.findViewById(R.id.eventDateTV);
        Button planTimeTV =(Button) dialog.findViewById(R.id.btn_eventTime);
        EditText planMotaET = (EditText) dialog.findViewById(R.id.et_mo_ta);
        Button planEditTV = (Button) dialog.findViewById(R.id.btn_xacnhan);
        Button planExitBT = (Button) dialog.findViewById(R.id.btn_exit);

        planNameET.setText(name);
        planDateTV.setText(date);
        planMotaET.setText(mota);
        planTimeTV.setText(time);

        planExitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        planEditTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planName = planNameET.getText().toString();
                String planMota = planMotaET.getText().toString();
                String planDate = planDateTV.getText().toString();
                String planTime = planTimeTV.getText().toString();
                Plan plan = new Plan(keyid, planName, planMota,planDate, planTime);
                planReference.child(keyid).setValue(plan);
                //planReference.child(keyid).setValue(plan);

                for (int i = 0; i < Plan.plansList.size(); i++) {
                    Plan planItem = Plan.plansList.get(i);
                    if (planItem.getID() == keyid) {
                        Plan.plansList.set(i, plan);
                        break; // Thoát khỏi vòng lặp sau khi gán giá trị mới
                    }
                }
                setPlanAdapter();
                dialog.dismiss();
            }
        });
        planTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        String text = String.format(Locale.getDefault(), "%02d:%02d:00",selectedHour, selectedMinute);
                        hour = selectedHour;
                        minute = selectedMinute;
                        planTimeTV.setText(text);
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(WeekViewActivity.this,
                        style, onTimeSetListener, hour, minute, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        dialog.show();
    }

    public void openDialogAddPlan() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_event_edit);
        EditText planNameET = (EditText) dialog.findViewById(R.id.eventNameET);
        Button planDateTV = (Button) dialog.findViewById(R.id.eventDateTV);
        Button planTimeTV =(Button) dialog.findViewById(R.id.btn_eventTime);
        EditText planMotaET = (EditText) dialog.findViewById(R.id.et_mo_ta);
        Button planAddBT = (Button) dialog.findViewById(R.id.btn_xacnhan);
        Button planExitBT = (Button) dialog.findViewById(R.id.btn_exit);
        time = LocalTime.now();
        planDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        planTimeTV.setText(CalendarUtils.formattedTime(time));
        planExitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        planAddBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planName = planNameET.getText().toString();
                String planMota = planMotaET.getText().toString();
                String planDate = planDateTV.getText().toString();
                String planTime = planTimeTV.getText().toString();
                String planKey = planReference.push().getKey();
                Plan plan = new Plan(planKey, planName, planMota, planDate, planTime);
                Plan.plansList.add(plan);
                planReference.child(planKey).setValue(plan);
                setPlanAdapter();
                dialog.dismiss();
            }
        });
        planTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        String text = String.format(Locale.getDefault(), "%02d:%02d:00",selectedHour, selectedMinute);
                        hour = selectedHour;
                        minute = selectedMinute;
                        planTimeTV.setText(text);
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(WeekViewActivity.this, style, onTimeSetListener, hour, minute, true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        dialog.show();
    }
    public void openDashboard(View view) {
        dashboard.setBackgroundColor(getColor(R.color.text_color));
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_sidebar);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.LEFT;
        window.setAttributes(windowAttributes);

        Button btnLogout = dialog.findViewById(R.id.btn_logout);
        ImageView imgExitDashboard = dialog.findViewById(R.id.img_exit_sidebar);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(WeekViewActivity.this, LoginActivity.class));
            }
        });
        imgExitDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dashboard.setBackgroundColor(getColor(R.color.main_background_color));
            }
        });
        dialog.show();
    }
}
