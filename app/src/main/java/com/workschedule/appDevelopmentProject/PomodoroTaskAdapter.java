package com.workschedule.appDevelopmentProject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PomodoroTaskAdapter extends ArrayAdapter<PoromodoTask> {
    private Context context;
    private PomodoroActivity pa;
    private List<PoromodoTask> list;
    public PomodoroTaskAdapter(PomodoroActivity po,Context context, int resource, @NonNull List<PoromodoTask> objects) {
        super(context, resource, objects);
        this.context = context;
        this.pa = po;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.row_lv_task_pomo, null, false);
        PoromodoTask poromodoTask = list.get(position);
        final boolean[] isVisible = {false};
        if(poromodoTask==null)
            Log.e("Ối dồi ôi", "Pomo này null");
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name_pomotask),
                tvNote = (TextView) convertView.findViewById(R.id.tv_note_pomotask),
                tvTime = (TextView) convertView.findViewById(R.id.tv_time_row_task_pomo);
        tvName.setText(poromodoTask.name);
        tvNote.setText(poromodoTask.note);
        tvTime.setText(poromodoTask.time);
        ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.btn_edit_row_task_pomo);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pa.AddTaskDialog(poromodoTask);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible[0])
                    tvNote.setVisibility(View.VISIBLE);
                else
                    tvNote.setVisibility(View.GONE);
                isVisible[0] = !isVisible[0];
            }
        });
        return convertView;
    }
}
