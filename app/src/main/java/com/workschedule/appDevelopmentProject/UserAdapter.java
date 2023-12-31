package com.workschedule.appDevelopmentProject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    Activity context;
    public UserAdapter(Activity context, int layoutID, List<User> userArrayList){
        super(context, layoutID, userArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.member_circle, null, false);
        }
        User user = getItem(position);
        TextView userName = (TextView) convertView.findViewById(R.id.tv_member_name);
        if (user.getUserName().isEmpty()){
            userName.setText(user.getUserEmail());
        } else {
            userName.setText(user.getUserName());
        }
        return convertView;
    }
}
