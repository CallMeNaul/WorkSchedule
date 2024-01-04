package com.workschedule.appDevelopmentProject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GroupPlanAdapter extends RecyclerView.Adapter<GroupPlanAdapter.ViewHolder>
{
    GroupViewActivity context;
    ArrayList<GroupPlan> groupPlanArrayList;
    FirebaseUser user;
    public GroupPlanAdapter(GroupViewActivity context, ArrayList<GroupPlan> groupPlanArrayList, FirebaseUser u)
    {
        this.context = context;
        this.groupPlanArrayList = groupPlanArrayList;
        this.user = u;
    }

    @NonNull
    @Override
    public GroupPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from((parent.getContext()));
        View itemView = layoutInflater.inflate(R.layout.plan_cell,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.planNameTV.setText(groupPlanArrayList.get(position).getGroupPlanName());
        holder.planMotaTV.setText(groupPlanArrayList.get(position).getGroupPlanMota());
        holder.planDateTV.setText(groupPlanArrayList.get(position).getGroupPlanDate());
        holder.planTimeTV.setText(groupPlanArrayList.get(position).getGroupPlanTime());
        if (groupPlanArrayList.get(position).getImportant()){
            holder.imgImportant.setVisibility(View.VISIBLE);
        } else {
            holder.imgImportant.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return groupPlanArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView planNameTV, planMotaTV,planDateTV, planTimeTV, planEditTV;
        private ImageView imgImportant;
        private LinearLayout root;
        public LinearLayout foreground;
        public RelativeLayout background;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            planNameTV = itemView.findViewById(R.id.tv_plan_name);
            planDateTV = itemView.findViewById(R.id.tv_date);
            planTimeTV = itemView.findViewById(R.id.tv_time);
            planMotaTV = itemView.findViewById(R.id.tv_mo_ta);
            planEditTV = itemView.findViewById(R.id.tv_edit_plan);
            imgImportant = itemView.findViewById(R.id.img_star);
            root = itemView.findViewById(R.id.layout_foreground);

            foreground = itemView.findViewById(R.id.layout_foreground);
            background = itemView.findViewById(R.id.background_row_recyclerview_plan);

            planEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openDialogEditGroupPlan(
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getGroupPlanID(),
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getGroupPlanName(),
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getGroupPlanMota(),
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getGroupPlanDate(),
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getGroupPlanTime(),
                            groupPlanArrayList.get(getAbsoluteAdapterPosition()).getImportant());
                    context.setPlanAdapter();
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    GroupPlan selectedPlan = groupPlanArrayList.get(getAbsoluteAdapterPosition());

                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.share_dialog);
                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        windowAttributes.gravity = Gravity.CENTER;
                        window.setAttributes(windowAttributes);

                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.cancel();
                            }
                        });

                        TextView namePlan = dialog.findViewById(R.id.tv_name_share),
                                nameDescription = dialog.findViewById(R.id.tv_description_share),
                                deadline = dialog.findViewById(R.id.tv_deadline_share);
                        Button btnExit = dialog.findViewById(R.id.btn_cancel_share),
                                btnSaveShare = dialog.findViewById(R.id.btn_save_share);
                        EditText etMails = dialog.findViewById(R.id.et_receiver);
                        namePlan.setText(selectedPlan.getGroupPlanName());
                        nameDescription.setText(selectedPlan.getGroupPlanMota());
                        deadline.setText(selectedPlan.getGroupPlanDate() + " " + selectedPlan.getGroupPlanTime());
                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                    }
                    return false;
                }
            });
        }
    }
    public void removeItem(int index) {
        groupPlanArrayList.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem (GroupPlan plan, int index) {
        groupPlanArrayList.add(index, plan);
        notifyItemInserted(index);
    }
}