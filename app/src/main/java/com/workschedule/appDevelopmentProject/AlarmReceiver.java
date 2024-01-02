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

public class AlarmReceiver extends BroadcastReceiver {
    private ArrayList<Plan> plans;
    private String userEMail;
    public AlarmReceiver(ArrayList<Plan> planArrayList, String mail) {
        plans = planArrayList;
        userEMail = mail;
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
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEMail));
                    mimeMessage.setSubject("WorkSchedule: Kế hoạch của bạn tới hạn.");

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
                            "\t\t\t\t<li><strong>Ngày tháng:</strong> " + deadline.getDate() + "</li>\n" +
                            "\t\t\t\t<li><strong>Thời gian:</strong> " + deadline.getTime() + "</li>\n" +
                            "\t\t\t</ul>\n" +
                            "\t\t</section>\n" +
                            "\t\t<section class=\"project\">\n" +
                            "\t\t\t<h2>Tên kế hoạch</h2>\n" +
                            "\t\t\t<p><strong>Tên:</strong> " + deadline.getName() + "</p>\n" +
                            "\t\t</section>\n" +
                            "\t\t<section class=\"description\">\n" +
                            "\t\t\t<h2>Mô tả kế hoạch</h2>\n" +
                            "\t\t\t<p><strong>Mô tả:</strong>" + deadline.getMota() + "</p>\n" +
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

                break;
            }
        }
    }
}
