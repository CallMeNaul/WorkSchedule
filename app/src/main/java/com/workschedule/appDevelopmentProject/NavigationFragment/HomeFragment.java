package com.workschedule.appDevelopmentProject.NavigationFragment;

import static android.content.ContentValues.TAG;
import static com.workschedule.appDevelopmentProject.CalendarUtils.daysInWeekArray;
import static com.workschedule.appDevelopmentProject.CalendarUtils.monthYearFromDate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workschedule.appDevelopmentProject.AlarmReceiver;
import com.workschedule.appDevelopmentProject.CalendarAdapter;
import com.workschedule.appDevelopmentProject.CalendarUtils;
import com.workschedule.appDevelopmentProject.MainActivity;
import com.workschedule.appDevelopmentProject.Plan;
import com.workschedule.appDevelopmentProject.PlanAdapter;
import com.workschedule.appDevelopmentProject.PlanTouchHelper;
import com.workschedule.appDevelopmentProject.PlanTouchListener;
import com.workschedule.appDevelopmentProject.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener, PlanTouchListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private TextView monthYearText, tvAll, tvImportant;
    private RecyclerView calendarRecyclerView, planRecyclerView;
    private ImageView lineAll, lineImportant;
    private ArrayList<Plan> plans;
    private PlanAdapter planAdapter;
    private Button buttonAddPlan, btnNextWeek, btnPreviousWeek;
    private int hour, minute;
    private LocalTime time;
    ArrayList<Plan> planArrayList;
 //   FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
//    DatabaseReference userReference = database.getReference("User");
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String uid = user.getUid();
//    DatabaseReference planReference = userReference.child(uid).child("Plan");
    FirebaseDatabase database;
    DatabaseReference userReference;
    FirebaseUser user;
    String uid;
    DatabaseReference planReference;
    private MainActivity mainActivity;
    private boolean isNew = true;
    private LinearLayout rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.i("New", "HoFR");
        mainActivity = (MainActivity) getActivity();
        database = mainActivity.getDatabase();
        userReference = mainActivity.getUserReference();
        user = mainActivity.getUser();
        uid = user.getUid();
        Log.i("Email new", user.getEmail());
    }
    private void initWidgets(View v)
    {
        calendarRecyclerView = v.findViewById(R.id.calendarRecyclerView);
        monthYearText = v.findViewById(R.id.monthYearTV);
        planRecyclerView = v.findViewById(R.id.planRecyclerView);
        tvAll = v.findViewById(R.id.all_);
        tvImportant = v.findViewById(R.id.important_);
        lineAll = v.findViewById(R.id.line_all_);
        lineImportant = v.findViewById(R.id.line_imp_);
        plans = new ArrayList<>();

        planRecyclerView.findViewById(R.id.planRecyclerView);
        planRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        planRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(planRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        planRecyclerView.addItemDecoration(itemDecoration);
        planRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new PlanTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(planRecyclerView);

        buttonAddPlan = v.findViewById(R.id.btn_new_plan);

        btnNextWeek = v.findViewById(R.id.next_week_action);
        btnPreviousWeek = v.findViewById(R.id.previous_week_action);

        rootView = v.findViewById(R.id.root_view);
    }

    public void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setPlanAdapter();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setPlanAdapter();
    }

    public void setPlanAdapter()
    {
        if (tvImportant.getTypeface().equals(Typeface.defaultFromStyle(Typeface.BOLD))) {
            planArrayList = Plan.importantPlansForDate(CalendarUtils.selectedDate, plans);
        } else {
            planArrayList = Plan.plansForDate(CalendarUtils.selectedDate, plans);
        }
        for (int i = 0; i < planArrayList.size();i++) {
            Log.i("Plan week", planArrayList.get(i).getName());
        }
        planAdapter = new PlanAdapter(HomeFragment.this, planArrayList, user);
        planRecyclerView.setAdapter(planAdapter);
    }
    public void openDialogEditPlan(String keyid, String name, String mota, String date, String time, boolean imp) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_event_edit);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText planNameET = (EditText) dialog.findViewById(R.id.eventNameET);
        Button planDateTV = (Button) dialog.findViewById(R.id.eventDateTV);
        Button planTimeTV =(Button) dialog.findViewById(R.id.btn_eventTime);
        EditText planMotaET = (EditText) dialog.findViewById(R.id.et_mo_ta);
        Button planEditTV = (Button) dialog.findViewById(R.id.btn_xacnhan);
        Button planExitBT = (Button) dialog.findViewById(R.id.btn_exit);
        CheckBox chbImportant = dialog.findViewById(R.id.chb_important),
                chbNotImportant = dialog.findViewById(R.id.chb_not_important);

        if(imp) {
            chbImportant.setChecked(true);
        }
        else {
            chbNotImportant.setChecked(true);
        }
        chbImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chbImportant.isChecked()) {
                    chbNotImportant.setChecked(false);
                }
            }
        });
        chbNotImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chbNotImportant.isChecked()) {
                    chbImportant.setChecked(false);
                }
            }
        });

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
                boolean planIsImportant = chbImportant.isChecked();
                Plan plan = new Plan(keyid, planName, planMota,planDate, planTime, planIsImportant);
                planReference.child(keyid).setValue(plan);

                for (int i = 0; i < plans.size(); i++) {
                    Plan planItem = plans.get(i);
                    if (planItem.getID() == keyid) {
                        plans.set(i, plan);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        style, onTimeSetListener, hour, minute, true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        planDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day)
                    {
                        month = month + 1;
                        String date = day + "-" + month + "-" + year;
                        planDateTV.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                int style = AlertDialog.THEME_HOLO_LIGHT;

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
        dialog.show();
    }
    public void openDialogAddPlan() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_event_edit);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText planNameET = (EditText) dialog.findViewById(R.id.eventNameET);
        Button planDateTV = (Button) dialog.findViewById(R.id.eventDateTV);
        Button planTimeTV =(Button) dialog.findViewById(R.id.btn_eventTime);
        EditText planMotaET = (EditText) dialog.findViewById(R.id.et_mo_ta);
        Button planAddBT = (Button) dialog.findViewById(R.id.btn_xacnhan);
        Button planExitBT = (Button) dialog.findViewById(R.id.btn_exit);
        CheckBox chbImportant = dialog.findViewById(R.id.chb_important),
                chbNotImportant = dialog.findViewById(R.id.chb_not_important);

        chbImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chbImportant.isChecked()) {
                    chbNotImportant.setChecked(false);
                }
            }
        });
        chbNotImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chbNotImportant.isChecked()) {
                    chbImportant.setChecked(false);
                }
            }
        });

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
                boolean planIsImportant = chbImportant.isChecked();
                Plan plan = new Plan(planKey, planName, planMota, planDate, planTime, planIsImportant);
                plans.add(plan);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), style, onTimeSetListener, hour, minute, true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        planDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day)
                    {
                        month = month + 1;
                        String date = day + "-" + month + "-" + year;
                        planDateTV.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                int style = AlertDialog.THEME_HOLO_LIGHT;

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
        dialog.show();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initWidgets(view);
        setWeekView();

        planReference = userReference.child(uid).child("Plan");
        planReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("IsNew", String.valueOf(isNew));
                if(isNew) {
                    for (DataSnapshot planSnapshot: snapshot.getChildren()) {
                        String planDate = planSnapshot.child("date").getValue(String.class);
                        String planMota = planSnapshot.child("mota").getValue(String.class);
                        String planName = planSnapshot.child("name").getValue(String.class);
                        String planTime = planSnapshot.child("time").getValue(String.class);
                        boolean planIsImportant = planSnapshot.child("important").getValue(boolean.class);
                        String planKey = planSnapshot.getKey();
                        Plan plan = new Plan(planKey, planName, planMota, planDate, planTime, planIsImportant);
                        plans.add(plan);
                        Log.i("Plan", planName);
                    }
                    setPlanAdapter();
                    isNew = false;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        buttonAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddPlan();
            }
        });
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAll.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvAll.setTextSize(20);
                lineAll.setVisibility(View.VISIBLE);
                tvImportant.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvImportant.setTextSize(18);
                lineImportant.setVisibility(View.GONE);
                setPlanAdapter();
            }
        });
        tvImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAll.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvAll.setTextSize(18);
                lineAll.setVisibility(View.GONE);
                tvImportant.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvImportant.setTextSize(20);
                lineImportant.setVisibility(View.VISIBLE);
                setPlanAdapter();
            }
        });
        btnPreviousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
                setWeekView();
            }
        });
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
                setWeekView();
            }
        });
        AlarmReceiver alarmReceiver = new AlarmReceiver(plans, user.getEmail());
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(alarmReceiver, intentFilter);
        return view;
    }

    public void deletePlan(String id){
        Plan planDelete = plans.get(0);
        String namePlanDeleted = planDelete.getName();
        for (int i = 0; i < plans.size(); i++) {
            Plan planItem = plans.get(i);
            if (planItem.getID() == id) {
                planDelete = planItem;
                namePlanDeleted = planItem.getName();
                plans.remove(planItem);
                break;
            }
        }
        String message = getText(R.string.task) + " " + namePlanDeleted + " bị xóa.";
        Snackbar snackbar = Snackbar.make(rootView, message, 5000);
        Plan finalPlanDelete = planDelete;
        snackbar.setTextColor(getContext().getColor(R.color.text_color))
                .setBackgroundTint(getContext().getColor(R.color.milk_white))
                .setActionTextColor(getResources().getColor(R.color.text_color))
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plans.add(finalPlanDelete);
                        planReference.child(id).setValue(finalPlanDelete);
                        setPlanAdapter();
                    }
                });
        snackbar.show();
        planReference.child(id).removeValue();
        setPlanAdapter();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof PlanAdapter.ViewHolder) {
            Plan deletedPlan = planArrayList.get(viewHolder.getAbsoluteAdapterPosition());

            int index = viewHolder.getAbsoluteAdapterPosition();
            planAdapter.removeItem(index);
            Snackbar snackbar = Snackbar.make(rootView, "Delete", 5000);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planAdapter.undoItem(deletedPlan, index);
                }
            }).show();
            deletePlan(deletedPlan.getID());
        }
    }
}