package com.workschedule.appDevelopmentProject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;

import java.util.ArrayList;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    HomeFragment context;
    ArrayList<Plan> planArrayList;
    public PlanAdapter(HomeFragment context, ArrayList<Plan> planArrayList)
    {
        this.context = context;
        this.planArrayList = planArrayList;
    }

    @NonNull
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from((parent.getContext()));
        View itemView = layoutInflater.inflate(R.layout.plan_cell,parent,false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.ViewHolder holder, int position) {
        holder.planNameTV.setText(planArrayList.get(position).getName());
        holder.planMotaTV.setText(planArrayList.get(position).getMota());
        holder.planDateTV.setText(planArrayList.get(position).getDate());
        holder.planTimeTV.setText(planArrayList.get(position).getTime());
        if (planArrayList.get(position).getImportant()){
            holder.imgImportant.setVisibility(View.VISIBLE);
        } else {
            holder.imgImportant.setVisibility(View.GONE);
        }
    }
    public int getItemCount() {
        return planArrayList.size();
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
                    context.openDialogEditPlan(
                            planArrayList.get(getAbsoluteAdapterPosition()).getID(),
                            planArrayList.get(getAbsoluteAdapterPosition()).getName(),
                            planArrayList.get(getAbsoluteAdapterPosition()).getMota(),
                            planArrayList.get(getAbsoluteAdapterPosition()).getDate(),
                            planArrayList.get(getAbsoluteAdapterPosition()).getTime(),
                            planArrayList.get(getAbsoluteAdapterPosition()).getImportant());
                    context.setPlanAdapter();
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String id = planArrayList.get(getAbsoluteAdapterPosition()).getID();
                    context.deletePlan(id);
                    return false;
                }
            });
        }
    }

    public void removeItem(int index) {
        planArrayList.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem (Plan plan, int index) {
        planArrayList.add(index, plan);
        notifyItemInserted(index);
    }
}
