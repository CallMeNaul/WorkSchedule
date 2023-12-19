package com.workschedule.appDevelopmentProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event>
{
    public EventAdapter(@NonNull Context context, List<Event> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Event event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);
        TextView date = convertView.findViewById(R.id.date);
        TextView time = convertView.findViewById(R.id.time);
        TextView mota = convertView.findViewById(R.id.tv_mo_ta);

        String eventTitle = event.getName();
        String eventTime = CalendarUtils.formattedTime(event.getTime());
        String eventDate = CalendarUtils.formattedDate(event.getDate());
        String eventMota = event.getMota();

        eventCellTV.setText(eventTitle);
        date.setText(eventDate);
        time.setText(eventTime);
        mota.setText(eventMota);

        return convertView;
    }
}
