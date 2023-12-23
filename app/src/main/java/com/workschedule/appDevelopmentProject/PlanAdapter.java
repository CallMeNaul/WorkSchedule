package com.workschedule.appDevelopmentProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlanAdapter extends ArrayAdapter<Plan>
{
    public PlanAdapter(@NonNull Context context, List<Plan> plans)
    {
        super(context, 0, plans);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Plan plan = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_cell, parent, false);

        TextView eventCellTV = convertView.findViewById(R.id.tv_plan_name);
        TextView date = convertView.findViewById(R.id.tv_date);
        TextView time = convertView.findViewById(R.id.tv_time);
        TextView mota = convertView.findViewById(R.id.tv_mo_ta);

        String eventName = plan.getName();
        String eventTime = plan.getTime();
        String eventDate = plan.getDate();
        String eventMota = plan.getMota();

        eventCellTV.setText(eventName);
        date.setText(eventDate);
        time.setText(eventTime);
        mota.setText(eventMota);

        return convertView;
    }
}
