package com.workschedule.appDevelopmentProject;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    GroupFragment context;
    ArrayList<Group> groupArrayList;
    public GroupAdapter(GroupFragment context, ArrayList<Group> groupArrayList){
        this.context = context;
        this.groupArrayList = groupArrayList;
    }
    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from((parent.getContext()));
        View itemView = layoutInflater.inflate(R.layout.group_cell,parent,false);
        return new GroupAdapter.ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        holder.groupNameTV.setText(groupArrayList.get(position).getGroupName());
        holder.groupDateTV.setText(groupArrayList.get(position).getGroupDate());
        holder.groupTimeTV.setText(groupArrayList.get(position).getGroupTime());
        holder.groupMemberLL.removeAllViews();

        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(groupArrayList.get(position).getGroupMember());
        ArrayList<String> members = new ArrayList<>();
        while (matcher.find()) {
            members.add(matcher.group());
        }
        Set<String> set = new HashSet<>(members);
        members.clear();
        members.addAll(set);
        ArrayList<User> groupMember = new ArrayList<>();
        for (String member : members){
            for (User user : User.userArrayList){
                if (member.equals(user.getUserEmail())){
                    groupMember.add(user);
                }
            }
        }

        for (User user : groupMember){
            View memberView = LayoutInflater.from(holder.groupMemberLL.getContext()).inflate(R.layout.member_circle, holder.groupMemberLL, false);
            TextView memberName = memberView.findViewById(R.id.tv_member_name);
            memberName.setText(user.getUserName());
            ImageView memberAvt = memberView.findViewById(R.id.iv_user_avt);
            Picasso.get().load(user.getUserAvt())
                    .resize(150,150)
                    .transform(new RoundedTransformation())
                    .into(memberAvt);
            holder.groupMemberLL.addView(memberView);
        }
        holder.groupMemberLL.setOrientation(LinearLayout.HORIZONTAL);
    }
    public int getItemCount() {
        return groupArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupNameTV, groupDateTV, groupTimeTV, groupEditTV;
        private LinearLayout groupMemberLL;
        private ConstraintLayout root;
        public ConstraintLayout foreground;
        public RelativeLayout background;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTV = itemView.findViewById(R.id.tv_groupname);
            groupDateTV = itemView.findViewById(R.id.tv_group_date);
            groupTimeTV = itemView.findViewById(R.id.tv_group_time);
            groupEditTV = itemView.findViewById(R.id.btn_edit_group);
            groupMemberLL = itemView.findViewById(R.id.lv_members);
            root = itemView.findViewById(R.id.cstrlo_foreground_row_recyclerview_plan);

            foreground = itemView.findViewById(R.id.cstrlo_foreground_row_recyclerview_plan);
            background = itemView.findViewById(R.id.cstrlo_background_row_recyclerview_plan);

            groupEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openDialogEditGroup(
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupID(),
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupName(),
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupDate(),
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupTime(),
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupMember());
                    context.setGroupAdapter();
                }
            });
            foreground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context.getContext(), GroupViewActivity.class));
                }
            });
        }
    }
    public void removeItem(int index) {
        groupArrayList.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem (Group group, int index) {
        groupArrayList.add(index, group);
        notifyItemInserted(index);
    }
}
