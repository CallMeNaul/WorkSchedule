package com.workschedule.appDevelopmentProject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private ArrayList<Plan> plans;
    public AlarmReceiver(ArrayList<Plan> planArrayList) {
        plans = planArrayList;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Plan deadline;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dateTime = formatter.format(now);
        Log.i("Current Date Time", dateTime);
        String[] dateTimeArray = dateTime.split(" ");
        Log.i("Current time", dateTimeArray[1]);
        String[] timeArray = dateTimeArray[1].split(":");
        for(int i = 0; i < plans.size(); i++) {
            deadline = plans.get(i);

            Log.i("Deadline date", deadline.getDate());
            Log.i("Deadline time", deadline.getTime());
            String[] deadlineTime = deadline.getTime().split(":");
            if(dateTimeArray[0].equals(deadline.getDate()) &&  timeArray[0].equals(deadlineTime[0]) && timeArray[1].equals(deadlineTime[1])) {
                Log.i("Compare", "True");
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_wsche_round);
                Notification notification = new NotificationCompat.Builder(context, WorkSchedule.CHANNEL_ID)
                        .setContentTitle(context.getText(R.string.task) + " " + deadline.getName() + " tới hạn.")
                        .setContentText(deadline.getMota())
                        .setSmallIcon(R.drawable.image_tick)
                        .setLargeIcon(bitmap)
                        .setColor(context.getResources().getColor(R.color.main_background_color))
                        .build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null) {
                    notificationManager.notify((int) new Date().getTime(), notification);
                }
                break;
            }
        }
    }
}
