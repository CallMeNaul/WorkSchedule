package com.workschedule.appDevelopmentProject.NavigationFragment;

import static com.google.android.material.color.MaterialColors.getColor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.workschedule.appDevelopmentProject.PomodoroActivity;
import com.workschedule.appDevelopmentProject.PomodoroReportActivity;
import com.workschedule.appDevelopmentProject.PomodoroTaskAdapter;
import com.workschedule.appDevelopmentProject.PoromodoTask;
import com.workschedule.appDevelopmentProject.R;

import java.util.ArrayList;

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
    private TextView tvPomodoroCounter, tvShortBreakCounter, tvLongBreakCounter;
    private Button btnPomo, btnLongBreak, btnShortBreak, btnAddTask, btnPomodoroSetting, btnPomodoroReport;
    private ListView lvTaskPomo;
    private ArrayList<PoromodoTask> tasks;
    private PomodoroTaskAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        btnPomodoroSetting = v.findViewById(R.id.btn_setting_pomo);
        btnPomodoroReport = v.findViewById(R.id.btn_report);

        tasks = new ArrayList<>();
        tasks.add(new PoromodoTask("aa", "bb", "00:59:00"));
        tasks.add(new PoromodoTask("cc", "dd", "00:59:30"));
        tasks.add(new PoromodoTask("ee", "ff", "00:59:59"));
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
            public void onClick(View v) {
                AddTaskDialog();
            }
        });
        btnPomodoroSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PomodoroSetting(v);
            }
        });
        btnPomodoroReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PomodoroReport(v);
            }
        });
    }
    public void setSelectedItemListviewPomodoro(int pos, View v) {
        LinearLayout backgr;
        for (int i = 0; i < lvTaskPomo.getChildCount(); i++) {
            backgr = lvTaskPomo.getChildAt(i).findViewById(R.id.row_parent_linear_layout);
            backgr.setBackgroundColor(getResources().getColor(R.color.main_background_color));
        }
        backgr = lvTaskPomo.getChildAt(pos).findViewById(R.id.row_parent_linear_layout);
        backgr.setBackgroundColor(getResources().getColor(R.color.text_color));
    }
    public void setAAdapter(int i) {
        tasks.remove(i);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.add(new PoromodoTask(etName.getText().toString(), etNote.getText().toString(),
                        etHour.getText().toString() + ":" + etMinute.getText().toString() + ":" + etSecond.getText().toString()));
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void AddTaskDialog(PoromodoTask poromodoTask) {
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

        etName.setText(poromodoTask.name);
        etNote.setText(poromodoTask.note);
        String component[] = poromodoTask.time.split(":");
        etHour.setText(component[0]);
        etMinute.setText(component[1]);
        etSecond.setText(component[2]);

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
                if(Integer.parseInt(etSecond.getText().toString()) > 23)  {
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
                tasks.set(index, new PoromodoTask(etName.getText().toString(), etNote.getText().toString(),
                        etHour.getText().toString() + ":" + etMinute.getText().toString() + ":" + etSecond.getText().toString()));
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.show();
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

    private void validateInput(EditText et, boolean isHour) {
        String input = et.getText().toString().trim();
        if (input.length() > 2)
            input = input.substring(1, input.length());
        else if(input.length() == 2)
            input = "0" + input;
        else if (input.length() < 2)
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
    public void PomodoroReport(View view) {
        startActivity(new Intent(getContext(), PomodoroReportActivity.class));
    }

    public void PomodoroSetting(View view) {
        final Dialog dialog = new Dialog(getContext());
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
    public void changeTextViewPomodoro(String time) {
        tvPomodoroCounter.setText(time);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);
        initWidgets(view);
        return view;
    }
}