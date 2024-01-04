package com.workschedule.appDevelopmentProject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupViewActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvGroupName;
    private ListView listView;
    private TextView btnAddGroupPlan;
    GroupFragment context;
    private ArrayList<GroupPlan> groupPlans = new ArrayList<>();
    private GroupPlanAdapter groupPlanAdapter;
    private RecyclerView groupPlanRecyclerView;
    private int hour, minute;
    private RelativeLayout rootView;
    private LocalTime time;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference groupReference = userReference.child(uid).child("Group");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        btnBack = findViewById(R.id.btn_back_to_groupview);
        btnAddGroupPlan = findViewById(R.id.tv_add_group_plan);
        tvGroupName = findViewById(R.id.tv_groupview_name);
        listView = findViewById(R.id.lv_group_member);
        groupPlanRecyclerView = findViewById(R.id.lv_group_plan);

        groupPlanRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GroupViewActivity.this, LinearLayoutManager.VERTICAL, false);
        groupPlanRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(groupPlanRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        groupPlanRecyclerView.addItemDecoration(itemDecoration);
        groupPlanRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new GroupPlanTouchHelper(0, ItemTouchHelper.LEFT, this::onSwipe);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(groupPlanRecyclerView);

        rootView = findViewById(R.id.group_plan_root_view);

        String groupID = getIntent().getStringExtra("groupID");
        DatabaseReference planReference = groupReference.child(groupID).child("GroupPlan");

        planReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroupPlan.groupPlanArrayList.clear();
                for (DataSnapshot planSnapshot: snapshot.getChildren()) {
                    String planDate = planSnapshot.child("groupPlanDate").getValue(String.class);
                    String planMota = planSnapshot.child("groupPlanMota").getValue(String.class);
                    String planName = planSnapshot.child("groupPlanName").getValue(String.class);
                    String planTime = planSnapshot.child("groupPlanTime").getValue(String.class);
                    boolean planIsImportant = planSnapshot.child("important").getValue(boolean.class);
                    String planKey = planSnapshot.getKey();
                    GroupPlan plan = new GroupPlan(planKey, planName, planMota, planDate, planTime, planIsImportant);
                    GroupPlan.groupPlanArrayList.add(plan);
                }
                setPlanAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvGroupName.setText("Tên nhóm: " + getIntent().getStringExtra("groupName"));
        String groupMember = getIntent().getStringExtra("groupMember");
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(groupMember);
        ArrayList<String> members = new ArrayList<>();
        while (matcher.find()) {
            members.add(matcher.group());
        }
        Set<String> set = new HashSet<>(members);
        members.clear();
        members.addAll(set);
        ArrayList<User> userList = new ArrayList<>();
        for (String member : members){
            for (User user : User.userArrayList){
                if (member.equals(user.getUserEmail())){
                    userList.add(user);
                }
            }
        }
        GroupViewAdapter adapter = new GroupViewAdapter(this, R.layout.row_groupview, userList);

        listView.setAdapter(adapter);
        btnAddGroupPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddGroupPlan();
            }
        });
    }

    private void openDialogAddGroupPlan() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_event_edit);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        String groupID = getIntent().getStringExtra("groupID");
        DatabaseReference planReference = groupReference.child(groupID).child("GroupPlan");

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
                GroupPlan groupPlan = new GroupPlan(planKey, planName, planMota, planDate, planTime, planIsImportant);
                Log.d(TAG, "onClick() called with: v = [" + groupPlan.getGroupPlanID() + "]");
                GroupPlan.groupPlanArrayList.add(groupPlan);
                planReference.child(planKey).setValue(groupPlan);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(GroupViewActivity.this, style, onTimeSetListener, hour, minute, true);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(GroupViewActivity.this, style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
        dialog.show();
    }

    public void setPlanAdapter()
    {
        groupPlans = GroupPlan.groupPlanArrayList;
        groupPlanAdapter = new GroupPlanAdapter(GroupViewActivity.this, GroupPlan.groupPlanArrayList, user);
        groupPlanRecyclerView.setAdapter(groupPlanAdapter);
    }

    public void openDialogEditGroupPlan(String id, String name, String mota, String date, String time, boolean important) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_event_edit);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        String groupID = getIntent().getStringExtra("groupID");
        DatabaseReference planReference = groupReference.child(groupID).child("GroupPlan");

        EditText planNameET = (EditText) dialog.findViewById(R.id.eventNameET);
        Button planDateTV = (Button) dialog.findViewById(R.id.eventDateTV);
        Button planTimeTV =(Button) dialog.findViewById(R.id.btn_eventTime);
        EditText planMotaET = (EditText) dialog.findViewById(R.id.et_mo_ta);
        Button planEditTV = (Button) dialog.findViewById(R.id.btn_xacnhan);
        Button planExitBT = (Button) dialog.findViewById(R.id.btn_exit);
        CheckBox chbImportant = dialog.findViewById(R.id.chb_important),
                chbNotImportant = dialog.findViewById(R.id.chb_not_important);

        if(important) {
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
                String planKey = planReference.push().getKey();
                boolean planIsImportant = chbImportant.isChecked();
                GroupPlan groupPlan = new GroupPlan(planKey, planName, planMota, planDate, planTime, planIsImportant);
                groupPlans.add(groupPlan);
                planReference.child(planKey).setValue(groupPlan);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(GroupViewActivity.this, style, onTimeSetListener, hour, minute, true);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(GroupViewActivity.this, style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
        dialog.show();
    }
    public void onSwipe(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof GroupPlanAdapter.ViewHolder) {
            GroupPlan deletedPlan = GroupPlan.groupPlanArrayList.get(viewHolder.getAbsoluteAdapterPosition());

            int index = viewHolder.getAbsoluteAdapterPosition();
            Snackbar snackbar = Snackbar.make(rootView, "Delete", 5000);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupPlanAdapter.undoItem(deletedPlan, index);
                }
            }).show();
            deletePlan(deletedPlan.getGroupPlanID());
        }
    }

    public void deletePlan(String id){
        GroupPlan planDelete = GroupPlan.groupPlanArrayList.get(0);
        String namePlanDeleted = planDelete.getGroupPlanName();
        for (int i = 0; i < GroupPlan.groupPlanArrayList.size(); i++) {
            GroupPlan planItem = GroupPlan.groupPlanArrayList.get(i);
            if (planItem.getGroupPlanID() == id) {
                planDelete = planItem;
                namePlanDeleted = planItem.getGroupPlanName();
                GroupPlan.groupPlanArrayList.remove(planItem);
                break;
            }
        }

        String groupID = getIntent().getStringExtra("groupID");
        DatabaseReference planReference = groupReference.child(groupID).child("GroupPlan");

        String message = getText(R.string.task) + " " + namePlanDeleted + " bị xóa.";
        Snackbar snackbar = Snackbar.make(rootView, message, 5000);
        GroupPlan finalPlanDelete = planDelete;
        snackbar.setTextColor(GroupViewActivity.this.getColor(R.color.text_color))
                .setBackgroundTint(GroupViewActivity.this.getColor(R.color.milk_white))
                .setActionTextColor(getResources().getColor(R.color.text_color))
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupPlans.add(finalPlanDelete);
                        planReference.child(id).setValue(finalPlanDelete);
                        setPlanAdapter();
                    }
                });
        snackbar.show();
        planReference.child(id).removeValue();
        setPlanAdapter();
    }
}