package com.workschedule.appDevelopmentProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;

import java.util.List;

public class PomodoroTaskAdapter extends ArrayAdapter<PoromodoTask> {
    private Context context;
    private PomodoroFragment pa;
    private List<PoromodoTask> list;
    public PomodoroTaskAdapter(PomodoroFragment po, Context context, int resource, @NonNull List<PoromodoTask> objects) {
        super(context, resource, objects);
        this.context = context;
        this.pa = po;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.row_lv_task_pomo, null, false);
        PoromodoTask poromodoTask = list.get(position);
        TextView tvName = convertView.findViewById(R.id.tv_name_pomotask),
                tvNote = convertView.findViewById(R.id.tv_note_pomotask),
                tvTime = convertView.findViewById(R.id.tv_time_row_task_pomo);
        tvName.setText(poromodoTask.getName());
        tvNote.setText(poromodoTask.getNote());
        tvTime.setText(poromodoTask.getTime());
        ImageButton btnEdit = convertView.findViewById(R.id.btn_edit_row_task_pomo),
                btnWatchMore = convertView.findViewById(R.id.btn_watch_more);
        CheckBox chbOnClick = convertView.findViewById(R.id.chb_onclick);
        chbOnClick.setChecked(poromodoTask.getTick());
        chbOnClick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                poromodoTask.setTick(b);
                pa.updateCheckBox(poromodoTask);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pa.EditTaskDialog(poromodoTask);
            }
        });
        btnWatchMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvNote.getVisibility()==View.GONE) {
                    tvNote.setVisibility(View.VISIBLE);
                    btnWatchMore.setBackgroundResource(R.drawable.baseline_arrow_drop_up_24);
                }
                else {
                    tvNote.setVisibility(View.GONE);
                    btnWatchMore.setBackgroundResource(R.drawable.baseline_arrow_drop_down_24);
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pa.setSelectedItemListviewPomodoro(position);
                pa.changeTextViewPomodoro(tvTime.getText().toString().trim());
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PoromodoTask taskDelete = list.get(position);
                String message = "Bạn vừa xóa: " + taskDelete.getName();
                String id = taskDelete.getTaskID();
                pa.removePomodoroTask(id);
                Snackbar.make(v, message, 5000)
                        .setBackgroundTint(context.getColor(R.color.milk_white))
                        .setTextColor(context.getColor(R.color.text_color))
                        .setActionTextColor(context.getColor(R.color.text_color))
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pa.UndoPomodoroTask(taskDelete);
                            }
                        }).show();
                return true;
            }
        });
        return convertView;
    }
}
