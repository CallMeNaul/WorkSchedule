package com.workschedule.appDevelopmentProject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;

import java.util.ArrayList;

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
    }
    public int getItemCount() {
        return groupArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupNameTV, groupDateTV, groupTimeTV, groupEditTV;
        private ConstraintLayout root;
        public ConstraintLayout foreground;
        public RelativeLayout background;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTV = (TextView) itemView.findViewById(R.id.tv_groupname);
            groupDateTV = (TextView) itemView.findViewById(R.id.tv_group_date);
            groupTimeTV = (TextView) itemView.findViewById(R.id.tv_group_time);
            groupEditTV = (TextView) itemView.findViewById(R.id.btn_edit_group);
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
                            groupArrayList.get(getAbsoluteAdapterPosition()).getGroupTime());
                    context.setGroupAdapter();
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String id = groupArrayList.get(getAbsoluteAdapterPosition()).getGroupID();
                    context.deleteGroup(id);
                    return false;
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
