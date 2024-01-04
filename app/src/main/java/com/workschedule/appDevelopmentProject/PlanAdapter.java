package com.workschedule.appDevelopmentProject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    HomeFragment context;
    ArrayList<Plan> planArrayList;
    FirebaseUser user;
    public PlanAdapter(HomeFragment context, ArrayList<Plan> planArrayList, FirebaseUser u)
    {
        this.context = context;
        this.planArrayList = planArrayList;
        this.user = u;
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
                    Plan selectedPlan = planArrayList.get(getAbsoluteAdapterPosition());

                    Dialog dialog = new Dialog(context.getContext());
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
                            public void onCancel(DialogInterface dialog) { dialog.cancel(); }
                        });

                        TextView namePlan = dialog.findViewById(R.id.tv_name_share),
                                nameDescription = dialog.findViewById(R.id.tv_description_share),
                                deadline = dialog.findViewById(R.id.tv_deadline_share);
                        Button btnExit = dialog.findViewById(R.id.btn_cancel_share),
                                btnSaveShare = dialog.findViewById(R.id.btn_save_share);
                        EditText etMails = dialog.findViewById(R.id.et_receiver);
                        namePlan.setText(selectedPlan.getName());
                        nameDescription.setText(selectedPlan.getMota());
                        deadline.setText(selectedPlan.getDate() + " " + selectedPlan.getTime());
                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { dialog.cancel(); }
                        });
                        btnSaveShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] receivers = etMails.getText().toString().split("\n");
                                if(receivers.length > 0) {
                                    for (int i = 0; i < receivers.length; i++) {
                                        try {
                                            String stringSenderEmail = "noreply.workschedule@gmail.com";
                                            String stringPasswordSenderEmail = "iqakrwecdurlfiou";

                                            String stringHost = "smtp.gmail.com";

                                            Properties properties = System.getProperties();

                                            properties.put("mail.smtp.host", stringHost);
                                            properties.put("mail.smtp.port", "465");
                                            properties.put("mail.smtp.ssl.enable", "true");
                                            properties.put("mail.smtp.auth", "true");

                                            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                                                @Override
                                                protected PasswordAuthentication getPasswordAuthentication() {
                                                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                                                }
                                            });

                                            MimeMessage mimeMessage = new MimeMessage(session);
                                            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers[i]));
                                            mimeMessage.setSubject("WorkSchedule: Bạn được chia sẻ một kế hoạch từ " + user.getEmail());

                                            MimeMultipart multipart = new MimeMultipart("related");
                                            MimeBodyPart mimeBodyPart = new MimeBodyPart();
                                            mimeBodyPart.setContent("<!DOCTYPE html>\n" +
                                                    "<html>\n" +
                                                    "<head>\n" +
                                                    "\t<title>Kế hoạch</title>\n" +
                                                    "\t<style type=\"text/css\">\n" +
                                                    "\t\theader { background-color: #04247C; color: #fff; padding: 10px; }\n" +
                                                    "\t\tmain { max-width: 800px; margin: 0 auto; padding: 20px; }\n" +
                                                    "\t\tsection { margin-bottom: 20px; }\n" +
                                                    "\t\t.date { border-bottom: 1px solid #ccc; }\n" +
                                                    "\t\t.project { border-bottom: 1px solid #ccc; }\n" +
                                                    "\t\t.description { border-bottom: 1px solid #ccc; }\n" +
                                                    "\t\tfooter { background-color: #04247C; color: #fff; padding: 10px; text-align: center; }\n" +
                                                    "\t\tul { list-style: none; margin: 0; padding: 0; }\n" +
                                                    "\t\tli { margin-bottom: 5px; }\n" +
                                                    "\t\tstrong { display: inline-block; width: 150px; } /* Set width for strong tag to align with other elements */\n" +
                                                    "\t</style>\n" +
                                                    "</head>\n" +
                                                    "<body>\n" +
                                                    "\t<header>\n" +
                                                    "\t\t<h1>Kế hoạch</h1>\n" +
                                                    "\t</header>\n" +
                                                    "\t<main>\n" +
                                                    "\t\t<section class=\"date\">\n" +
                                                    "\t\t\t<h2>Ngày tháng</h2>\n" +
                                                    "\t\t\t<ul>\n" +
                                                    "\t\t\t\t<li><strong>Ngày tháng:</strong> " + selectedPlan.getDate() + "</li>\n" +
                                                    "\t\t\t\t<li><strong>Thời gian:</strong> " + selectedPlan.getTime() + "</li>\n" +
                                                    "\t\t\t</ul>\n" +
                                                    "\t\t</section>\n" +
                                                    "\t\t<section class=\"project\">\n" +
                                                    "\t\t\t<h2>Tên kế hoạch</h2>\n" +
                                                    "\t\t\t<p><strong>Tên:</strong> " + selectedPlan.getName() + "</p>\n" +
                                                    "\t\t</section>\n" +
                                                    "\t\t<section class=\"description\">\n" +
                                                    "\t\t\t<h2>Mô tả kế hoạch</h2>\n" +
                                                    "\t\t\t<p><strong>Mô tả:</strong>" + selectedPlan.getMota() + "</p>\n" +
                                                    "\t\t</section>\n" +
                                                    "    </main>\n" +
                                                    "    <footer></footer>\n" +
                                                    "</body>\n" +
                                                    "</html>", "text/html; charset=UTF-8");
                                            multipart.addBodyPart(mimeBodyPart);
                                            mimeMessage.setContent(multipart);

                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Transport.send(mimeMessage);
                                                    } catch (MessagingException e) {

                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            thread.start();
                                        } catch (AddressException e) {
                                            e.printStackTrace();
                                        } catch (MessagingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                dialog.cancel();
                                Toast.makeText(context.getContext(), "Đã chia sẻ kế hoạch đến " + etMails.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                    }
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
