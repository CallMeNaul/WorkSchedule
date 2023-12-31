package com.workschedule.appDevelopmentProject.NavigationFragment;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workschedule.appDevelopmentProject.MainActivity;
import com.workschedule.appDevelopmentProject.PomodoroTaskAdapter;
import com.workschedule.appDevelopmentProject.PoromodoTask;
import com.workschedule.appDevelopmentProject.R;
import com.workschedule.appDevelopmentProject.WorkSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PomodoroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PomodoroFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PomodoroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PomodoroFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PomodoroFragment newInstance(String param1, String param2) {
        PomodoroFragment fragment = new PomodoroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private static double pomodoroHours;
    private TextView tvPomodoroCounter, tvShortBreakCounter, tvLongBreakCounter;
    private Button btnPomo, btnLongBreak, btnShortBreak, btnAddTask, btnStartPomodoro, btnResetPomodoro;
    private ListView lvTaskPomo;
    private ArrayList<PoromodoTask> tasks;
    private PomodoroTaskAdapter adapter;
    private long hours, mins, seconds;
    private boolean timerRunning = false, isRefresh = false;
    private static String longg, shortt, pomoo;
    private boolean isNew = true;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference poromodoReference = userReference.child(uid).child("Poromodo");
    private int globalValue;
    private boolean autoBreak = false, autoPomodoro = false, autoTickCompletedTask = false, autoChangeTask = false;
    private int selectedListviewPosition;
    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        poromodoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isNew) {
                    for (DataSnapshot poromodoSnapshot: snapshot.getChildren()) {
                        String taskName = poromodoSnapshot.child("name").getValue(String.class);
                        String taskNote = poromodoSnapshot.child("note").getValue(String.class);
                        String taskTime = poromodoSnapshot.child("time").getValue(String.class);
                        String taskKey = poromodoSnapshot.child("taskID").getValue(String.class);
                        boolean taskIsTick = poromodoSnapshot.child("tick").getValue(boolean.class);
                        PoromodoTask poromodoTask = new PoromodoTask(taskKey,taskName, taskNote, taskTime, taskIsTick);
                        tasks.add(poromodoTask);
                    }
                    isNew = false;
                }
                adapter.notifyDataSetChanged();
                lvTaskPomo.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void initWidgets(View v)
    {
        tvPomodoroCounter = v.findViewById(R.id.tv_poro_counter);
        tvLongBreakCounter = v.findViewById(R.id.tv_longbrk_counter);
        tvShortBreakCounter = v.findViewById(R.id.tv_shbrk_counter);
        btnPomo = v.findViewById(R.id.btn_poro);
        btnShortBreak = v.findViewById(R.id.btn_shortbreak);
        btnLongBreak = v.findViewById(R.id.btn_longbreak);
        btnAddTask = v.findViewById(R.id.btn_add_task);
        lvTaskPomo = v.findViewById(R.id.lv_task_pomo);
        btnStartPomodoro = v.findViewById(R.id.btn_start_pomo);
        btnResetPomodoro = v.findViewById(R.id.btn_reset_pomo);
        btnResetPomodoro.setEnabled(false);

        tasks = new ArrayList<>();
        adapter = new PomodoroTaskAdapter(this,getContext(), R.layout.row_lv_task_pomo, tasks);
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
            public void onClick(View v) { AddTaskDialog(); }
        });

        btnStartPomodoro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvView;
                if(tvPomodoroCounter.getVisibility() == View.VISIBLE)
                    tvView = tvPomodoroCounter;
                else if (tvShortBreakCounter.getVisibility() == View.VISIBLE)
                    tvView = tvShortBreakCounter;
                else
                    tvView = tvLongBreakCounter;
                if(tvView.getText().toString().equals("00:00:00")) return;
                btnResetPomodoro.setEnabled(true);
                if(timerRunning) {
                    btnStartPomodoro.setText(R.string.start);
                    EnableTaskPomodoroListView();
                    timerRunning = false;
                    setTotalPomodoroTime();
                    Log.i("Pause", "Pause By User");
                }
                else {
                    String duration = tvView.getText().toString();
                    long period = ChangeTimeFormatToSecond(duration);
                    DisableTaskPomodoroListView();
                    globalValue = 0;
                    InitCountDownThread(period);
                }
            }
        });
        btnResetPomodoro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRunning = false;
                isRefresh = true;
                btnStartPomodoro.setText(R.string.start);
                setTotalPomodoroTime();
                EnableTaskPomodoroListView();
                LinearLayout backgr;
                for (int i = 0; i < lvTaskPomo.getChildCount(); i++) {
                    backgr = lvTaskPomo.getChildAt(i).findViewById(R.id.row_parent_linear_layout);
                    backgr.setBackgroundColor(getResources().getColor(R.color.main_background_color));
                }
                tvPomodoroCounter.setText(pomoo);
                tvLongBreakCounter.setText(longg);
                tvShortBreakCounter.setText(shortt);
            }
        });
    }
    private void setTotalPomodoroTime() {
        if(tvPomodoroCounter.getVisibility() == View.VISIBLE) {
            pomodoroHours += (double) globalValue / 3600000;
            Log.i("tích lũy", String.valueOf(pomodoroHours));
            mainActivity.setPomodoroCounter(pomodoroHours);
        }
    }
    public void DisableTaskPomodoroListView() {
        btnStartPomodoro.setText(R.string.pause);
        LinearLayout backgr;
        for (int i = 0; i < lvTaskPomo.getChildCount(); i++) {
            backgr = lvTaskPomo.getChildAt(i).findViewById(R.id.row_parent_linear_layout);
            backgr.setEnabled(false);
        }
    }
    public void EnableTaskPomodoroListView() {
        btnStartPomodoro.setText(R.string.start);
        LinearLayout backgr;
        for (int i = 0; i < lvTaskPomo.getChildCount(); i++) {
            backgr = lvTaskPomo.getChildAt(i).findViewById(R.id.row_parent_linear_layout);
            backgr.setEnabled(true);
        }
    }
    public void setSelectedItemListviewPomodoro(int pos) {
        selectedListviewPosition = pos;
        LinearLayout backgr;
        for (int i = 0; i < lvTaskPomo.getChildCount(); i++) {
            backgr = lvTaskPomo.getChildAt(i).findViewById(R.id.row_parent_linear_layout);
            backgr.setBackgroundColor(getResources().getColor(R.color.main_background_color));
        }
        backgr = lvTaskPomo.getChildAt(pos).findViewById(R.id.row_parent_linear_layout);
        backgr.setBackgroundColor(getResources().getColor(R.color.text_color));
    }
    public void removePomodoroTask(String id) {
        for (PoromodoTask taskItem : tasks){
            if (taskItem.getTaskID().equals(id)){
                tasks.remove(taskItem);
                break;
            }
        }
        poromodoReference.child(id).removeValue();
        adapter.notifyDataSetChanged();
    }

    public void AddTaskDialog() {
        final Dialog dialog = new Dialog(getContext());
        customDialog(dialog, R.layout.add_pomodoro_task_dialog);

        EditText etName, etNote;
        Button btnCancel, btnOK;
        EditText etHour, etMinute, etSecond;
        ImageButton btnUpHour, btnDownHour, btnUpMinute, btnDownMinute, btnUpSecond, btnDownSecond;

        etName = dialog.findViewById(R.id.add_pomotask_et_task_name);
        etNote = dialog.findViewById(R.id.add_pomotask_et_note);
        btnOK = dialog.findViewById(R.id.btn_save_add_pomotask);
        btnCancel = dialog.findViewById(R.id.btn_cancel_add_pomotask);
        btnUpHour = dialog.findViewById(R.id.raise_hour);
        btnUpMinute = dialog.findViewById(R.id.raise_minute);
        btnUpSecond = dialog.findViewById(R.id.raise_second);
        btnDownHour = dialog.findViewById(R.id.reduce_hour);
        btnDownMinute = dialog.findViewById(R.id.reduce_minute);
        btnDownSecond = dialog.findViewById(R.id.reduce_second);
        etHour = dialog.findViewById(R.id.et_hour);
        etMinute = dialog.findViewById(R.id.et_minute);
        etSecond = dialog.findViewById(R.id.et_second);

        AddEventForDuration(etHour, etMinute, etSecond, btnUpHour, btnDownHour, btnUpMinute,
                btnDownMinute, btnUpSecond, btnDownSecond);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.cancel(); }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = etName.getText().toString(),
                        taskNote = etNote.getText().toString(),
                        taskTime = etHour.getText().toString() + ":" + etMinute.getText().toString() + ":" + etSecond.getText().toString();
                boolean taskIsTick = false;
                String taskKey = poromodoReference.push().getKey();
                PoromodoTask newTask = new PoromodoTask(taskKey, taskName, taskNote, taskTime, taskIsTick);
                tasks.add(newTask);
                poromodoReference.child(taskKey).setValue(newTask);
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void EditTaskDialog(PoromodoTask poromodoTask) {
        final Dialog dialog = new Dialog(getContext());
        customDialog(dialog, R.layout.add_pomodoro_task_dialog);

        EditText etName, etNote;
        Button btnCancel, btnOK;
        EditText etHour, etMinute, etSecond;
        ImageButton btnUpHour, btnDownHour, btnUpMinute, btnDownMinute, btnUpSecond, btnDownSecond;

        etName = dialog.findViewById(R.id.add_pomotask_et_task_name);
        etNote = dialog.findViewById(R.id.add_pomotask_et_note);
        btnOK = dialog.findViewById(R.id.btn_save_add_pomotask);
        btnCancel = dialog.findViewById(R.id.btn_cancel_add_pomotask);

        btnUpHour = dialog.findViewById(R.id.raise_hour);
        btnUpMinute = dialog.findViewById(R.id.raise_minute);
        btnUpSecond = dialog.findViewById(R.id.raise_second);
        btnDownHour = dialog.findViewById(R.id.reduce_hour);
        btnDownMinute = dialog.findViewById(R.id.reduce_minute);
        btnDownSecond = dialog.findViewById(R.id.reduce_second);
        etHour = dialog.findViewById(R.id.et_hour);
        etMinute = dialog.findViewById(R.id.et_minute);
        etSecond = dialog.findViewById(R.id.et_second);

        etName.setText(poromodoTask.getName());
        etNote.setText(poromodoTask.getNote());
        String[] component = poromodoTask.getTime().split(":");
        etHour.setText(component[0]);
        etMinute.setText(component[1]);
        etSecond.setText(component[2]);

        AddEventForDuration(etHour, etMinute, etSecond, btnUpHour, btnDownHour, btnUpMinute,
                btnDownMinute, btnUpSecond, btnDownSecond);

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
                String taskName = etName.getText().toString(),
                        taskNote = etNote.getText().toString(),
                        taskTime = etHour.getText().toString() + ":" + etMinute.getText().toString() + ":" + etSecond.getText().toString();
                boolean taskIsTick = false;
                String taskKey = poromodoTask.getTaskID();
                PoromodoTask newTask = new PoromodoTask(taskKey, taskName, taskNote, taskTime, taskIsTick);
                tasks.set(index, newTask);
                poromodoReference.child(taskKey).setValue(newTask);
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void AddEventForDuration(EditText etHour, EditText etMinute, EditText etSecond,
                                    ImageButton btnUpHour, ImageButton btnDownHour, ImageButton btnUpMinute,
                                    ImageButton btnDownMinute, ImageButton btnUpSecond, ImageButton btnDownSecond) {
        btnUpHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upEditText(etHour);
                if(Integer.parseInt(etHour.getText().toString()) > 99)  {
                    etHour.setText("00");
                }
            }
        });

        btnUpMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upEditText(etMinute);
                if(Integer.parseInt(etMinute.getText().toString()) > 59)  {
                    etMinute.setText("00");
                    upEditText(etHour);
                }
            }
        });

        btnUpSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upEditText(etSecond);
                if(Integer.parseInt(etSecond.getText().toString()) > 59)  {
                    etSecond.setText("00");
                    upEditText(etMinute);
                }
            }
        });

        btnDownHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downEditText(etHour);
            }
        });

        btnDownMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downEditText(etMinute, etHour);
            }
        });

        btnDownSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downEditText(etSecond, etMinute);
            }
        });

        etHour.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateInput(etHour, true);
                return false;
            }
        });

        etMinute.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateInput(etMinute, false);
                return false;
            }
        });

        etSecond.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateInput(etSecond, false);
                return false;
            }
        });
    }
    public void updateCheckBox(PoromodoTask task){
        int index = tasks.indexOf(task);
        tasks.set(index, task);
        poromodoReference.child(task.getTaskID()).setValue(task);
    }
    public void upEditText(EditText et) {
        int num = Integer.parseInt(et.getText().toString());
        num++;
        String str = String.valueOf(num);
        if(num < 10) str = "0" + str;
        et.setText(str);
    }
    public void downEditText(EditText et) {
        int num = Integer.parseInt(et.getText().toString());
        num--;
        String str = String.valueOf(num);
        if(num < 0) str = "00";
        else if(num < 10) str = "0" + str;
        et.setText(str);
    }
    public void downEditText(EditText et1, EditText et2) {
        int num = Integer.parseInt(et1.getText().toString());
        num--;
        String str = String.valueOf(num);
        if(num < 0) {
            str = "59";
            downEditText(et2);
        }
        else if(num < 10) str = "0" + str;
        et1.setText(str);
    }

    public void validateInput(EditText et, boolean isHour) {
        String input = et.getText().toString().trim();
        if (input.length() > 2)
            input = input.substring(1);
        else if(input.length() == 2)
            input = "0" + input;
        else
            input = "00" + input;
        if(!isHour) {
            if(Integer.parseInt(input) > 59)
                input = "00";
        }
        et.setText(input);
        et.setSelection(et.getText().length());
    }

    public void setCounterLabel(Button a, Button b, Button c, TextView aa, TextView bb, TextView cc){
        a.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pomo_button_color));
        b.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.text_color));
        c.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.text_color));
        aa.setVisibility(View.VISIBLE);
        bb.setVisibility(View.GONE);
        cc.setVisibility(View.GONE);
    }
    public void customDialog(Dialog dialog, int id) {
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) { dialog.cancel(); }
        });
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
    public long ChangeTimeFormatToSecond(String duration) {
        String[] hms = duration.split(":");
        hours = Integer.parseInt(hms[0]);
        mins = Integer.parseInt(hms[1]);
        seconds = Integer.parseInt(hms[2]);
        long period = hours * 3600 + mins * 60 + seconds;
        return period;
    }
    public void changeTextViewPomodoro(String time) {
        tvPomodoroCounter.setText(time);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);
        initWidgets(view);
        globalValue = 0;
        pomodoroHours = 0;
        longg = tvLongBreakCounter.getText().toString();
        shortt = tvShortBreakCounter.getText().toString();
        pomoo = tvPomodoroCounter.getText().toString();
        mainActivity = (MainActivity) getActivity();
        mainActivity.getTextViewCounterArrayList().add(tvLongBreakCounter.getText().toString());
        mainActivity.getTextViewCounterArrayList().add(tvShortBreakCounter.getText().toString());
        return view;
    }

    public void UndoPomodoroTask(PoromodoTask taskDelete) {
        tasks.add(taskDelete);
        poromodoReference.child(taskDelete.getTaskID()).setValue(taskDelete);
        adapter.notifyDataSetChanged();
    }
    private void InitCountDownThread(long period) {
        Handler displayCountDownTime = new Handler();
        Runnable foregroundRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) { globalValue+=1000; }
                    long restTime = period - globalValue / 1000;
                    Log.i("temp", String.valueOf(restTime));
                    hours = (restTime) / 3600;
                    mins = ((restTime) % 3600) / 60;
                    seconds = (restTime) % 60;
                    String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, seconds);
                    TextView tvView;
                    if(tvPomodoroCounter.getVisibility() == View.VISIBLE)
                        tvView = tvPomodoroCounter;
                    else if (tvShortBreakCounter.getVisibility() == View.VISIBLE)
                        tvView = tvShortBreakCounter;
                    else
                        tvView = tvLongBreakCounter;
                    Log.i("Duration:", time);
                    tvView.setText(time);
                    if (restTime == 0) {
                        String message, note;
                        EnableTaskPomodoroListView();
                        timerRunning = false;
                        pomodoroHours += (double) period / 3600;
                        Log.i("tích lũy", String.valueOf(pomodoroHours));
                        if(tvPomodoroCounter.getVisibility() == View.VISIBLE) {
                            PoromodoTask selectedPomodoroTask = tasks.get(selectedListviewPosition);
                            message = getText(R.string.duration) + " " + selectedPomodoroTask.getName() + " đã tới.";
                            note = selectedPomodoroTask.getNote();
                            sendNotification(message, note);
                            if(autoBreak) {
                                btnShortBreak.callOnClick();
                                btnStartPomodoro.callOnClick();
                            }

                            if(autoChangeTask && selectedListviewPosition < tasks.size()) {
                                setSelectedItemListviewPomodoro(selectedListviewPosition + 1);
                                changeTextViewPomodoro(tasks.get(selectedListviewPosition).getTime());
                            }

                            if(autoTickCompletedTask) {
                                CheckBox chb = lvTaskPomo.getChildAt(selectedListviewPosition).findViewById(R.id.chb_onclick);
                                chb.setChecked(true);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            if(tvShortBreakCounter.getVisibility() == View.VISIBLE)
                                message = getString(R.string.time) + " " + getText(R.string.shortbreak) + " đã hết.";
                            else
                                message = getString(R.string.time) + " " + getText(R.string.longbreak) + " đã hết.";
                            note = getString(R.string.back_to_work);
                            sendNotification(message, note);
                            if (autoPomodoro) {
                                btnPomo.callOnClick();
                                btnStartPomodoro.callOnClick();
                            }
                        }
                    }
                    if(isRefresh) {
                        tvPomodoroCounter.setText(pomoo);
                        tvLongBreakCounter.setText(longg);
                        tvShortBreakCounter.setText(shortt);
                        isRefresh = false;
                    }
                } catch (Throwable e) {Log.i("Stop", e.getMessage());}
            }
        };
        Runnable backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    timerRunning = true;
                    for (int i = 0; i < period; i++) {
                        if(!timerRunning) return;
                        Thread.sleep(1000);
                        synchronized (this) { globalValue+=1; }
                        displayCountDownTime.post(foregroundRunnable);
                    }
                } catch (Throwable t) { }
            }
        };
        Thread countDownThread = new Thread(backgroundRunnable);
        countDownThread.start();
    }
    private void sendNotification(String message, String note) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_wsche_round);
        Notification notification = new NotificationCompat.Builder(getContext(), WorkSchedule.CHANNEL_ID)
                .setContentTitle(message)
                .setContentText(note)
                .setSmallIcon(R.drawable.image_tick)
                .setLargeIcon(bitmap)
                .setColor(getResources().getColor(R.color.main_background_color))
                .build();

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.notify((int) new Date().getTime(), notification);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<String> arrayList = getArguments().getStringArrayList("newCounter");
            longg = arrayList.get(0);
            tvLongBreakCounter.setText(longg);
            shortt = arrayList.get(1);
            tvShortBreakCounter.setText(shortt);
            autoBreak = getArguments().getBoolean("newAutoBreak");
            autoPomodoro = getArguments().getBoolean("newAutoPomodoro");
            autoTickCompletedTask = getArguments().getBoolean("newAutoTick");
            autoChangeTask = getArguments().getBoolean("newAutoChangeTask");
            Log.i("auto", autoBreak + " " + autoPomodoro + " " + autoTickCompletedTask + " " + autoChangeTask);
            Log.i("DONE", "");
        }
        catch (Throwable t) {}
    }
}