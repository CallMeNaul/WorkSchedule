package com.workschedule.appDevelopmentProject;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

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
        userName.setText(user.getUserName());
        ImageView userAvt = (ImageView) convertView.findViewById(R.id.iv_user_avt);
        Picasso.get().load(user.getUserAvt())
                .resize(150, 150)
                .transform(new RoundedTransformation())
                .into(userAvt);

        Log.d(TAG, "CALL() " + user.getUserAvt());
        return convertView;
    }
}
