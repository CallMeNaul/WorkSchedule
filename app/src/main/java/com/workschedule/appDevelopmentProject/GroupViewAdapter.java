package com.workschedule.appDevelopmentProject;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupViewAdapter extends ArrayAdapter<User> {
    private Activity context;
//
//    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
//    DatabaseReference userReference = database.getReference("User");

    public GroupViewAdapter(Activity context, int layoutID, List<User> objects) {
        super(context, layoutID, objects);
        this.context = context;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_groupview, null, false);
        }

        TextView tvMemberName = convertView.findViewById(R.id.tv_group_member_name);
        TextView tvMemberEmail = convertView.findViewById(R.id.tv_group_member_email);
        ImageView ivMemberAvt = convertView.findViewById(R.id.iv_group_member_avt);

        User member = getItem(position);
        tvMemberName.setText(member.getUserName());
        tvMemberEmail.setText(member.getUserEmail());
        if (member.getUserAvt() != null && !member.getUserAvt().equals(""))
        {
            Picasso.get().load(member.getUserAvt())
                    .resize(150, 150)
                    .transform(new RoundedTransformation())
                    .into(ivMemberAvt);
        }


        return convertView;
    }
}
