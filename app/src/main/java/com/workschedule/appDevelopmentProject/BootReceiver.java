package com.workschedule.appDevelopmentProject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    public static int onlineDay;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyApp", "Device booted");

        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        onlineDay = preferences.getInt("launch_count", 1);
        onlineDay++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("launch_count", onlineDay);
        editor.apply();

        // Check if the user has launched the app for the first time today
        Calendar calendar = Calendar.getInstance();
        int todayLaunchCount = preferences.getInt("today_launch_count", 0);
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0 || calendar.get(Calendar.HOUR_OF_DAY) > 23 || todayLaunchCount == 0) {
            // Reset the today launch counter and increment the total launch counter
            editor.putInt("today_launch_count", 1);
            editor.putInt("total_launch_count", onlineDay);
            editor.apply();
            // Notify the user that it's their first launch of the day (if it's early morning or late evening) or that they've launched the app for the first time (if it's their first launch of the day)
            Toast.makeText(context, "Welcome back! This is your first launch of the day.", Toast.LENGTH_SHORT).show();
        } else {
            // Increment the today launch counter and update the total launch counter (if it's not early morning or late evening)
            editor.putInt("today_launch_count", todayLaunchCount + 1);
            editor.apply();
            // Notify the user that they've launched the app again today (if it's not early morning or late evening)
            Toast.makeText(context, "Welcome back! You've launched the app " + todayLaunchCount + " times today.", Toast.LENGTH_SHORT).show();
        }
    }
}
