package com.workschedule.appDevelopmentProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    WeekViewActivity context;
    ArrayList<Plan> planArrayList;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference planReference = database.getReference("Plan");
    public PlanAdapter(WeekViewActivity context, ArrayList<Plan> planArrayList)
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            planNameTV = (TextView) itemView.findViewById(R.id.tv_plan_name);
            planDateTV = (TextView) itemView.findViewById(R.id.tv_date);
            planTimeTV = (TextView) itemView.findViewById(R.id.tv_time);
            planMotaTV = (TextView) itemView.findViewById(R.id.tv_mo_ta);
            planEditTV = (TextView) itemView.findViewById(R.id.tv_edit_plan);

            planEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openDialogEditPlan(
                            planArrayList.get(getAdapterPosition()).getID(),
                            planArrayList.get(getAdapterPosition()).getName(),
                            planArrayList.get(getAdapterPosition()).getMota(),
                            planArrayList.get(getAdapterPosition()).getDate(),
                            planArrayList.get(getAdapterPosition()).getTime());
                    context.setPlanAdapter();

                }
            });


        }
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//    {
//        Plan plan = getItem(position);
//
//        if (convertView == null)
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_cell, parent, false);
//
//        TextView eventCellTV = convertView.findViewById(R.id.tv_plan_name);
//        TextView date = convertView.findViewById(R.id.tv_date);
//        TextView time = convertView.findViewById(R.id.tv_time);
//        TextView mota = convertView.findViewById(R.id.tv_mo_ta);
//
//        String eventName = plan.getName();
//        String eventTime = plan.getTime();
//        String eventDate = plan.getDate();
//        String eventMota = plan.getMota();
//
//        eventCellTV.setText(eventName);
//        date.setText(eventDate);
//        time.setText(eventTime);
//        mota.setText(eventMota);
//
//        return convertView;
//    }
}
