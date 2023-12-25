package com.workschedule.appDevelopmentProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;

import java.util.ArrayList;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    HomeFragment context;
    ArrayList<Plan> planArrayList;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference planReference = database.getReference("Plan");
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
    }
    public int getItemCount() {
        return planArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView planNameTV, planMotaTV,planDateTV, planTimeTV, planEditTV;
        private ImageView imgImportant;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            planNameTV = (TextView) itemView.findViewById(R.id.tv_plan_name);
            planDateTV = (TextView) itemView.findViewById(R.id.tv_date);
            planTimeTV = (TextView) itemView.findViewById(R.id.tv_time);
            planMotaTV = (TextView) itemView.findViewById(R.id.tv_mo_ta);
            planEditTV = (TextView) itemView.findViewById(R.id.tv_edit_plan);
            imgImportant = itemView.findViewById(R.id.img_star);

            /*if(planArrayList.get(getAdapterPosition()).getImportant())
                imgImportant.setVisibility(View.VISIBLE);
            else
                imgImportant.setVisibility(View.GONE);*/

            planEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openDialogEditPlan(
                            planArrayList.get(getAdapterPosition()).getID(),
                            planArrayList.get(getAdapterPosition()).getName(),
                            planArrayList.get(getAdapterPosition()).getMota(),
                            planArrayList.get(getAdapterPosition()).getDate(),
                            planArrayList.get(getAdapterPosition()).getTime(),
                            planArrayList.get(getAdapterPosition()).getImportant());
                    context.setPlanAdapter();
                }
            });
        }
    }
}
