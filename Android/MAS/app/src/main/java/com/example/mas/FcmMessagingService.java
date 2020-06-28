package com.example.mas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class FcmMessagingService extends FirebaseMessagingService {
    String type = "";
    private long[] pattern = {300, 300, 300, 300, 300};

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0) {
            type="json";
            sendNotification("", remoteMessage.getData().toString());
        }
        if(remoteMessage.getNotification() != null) {
            type="message";
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageTitle, String messageBody) {
        String id="", message="", title="";

        if(type.equals("json")) {
            try {
                JSONObject jsonObject = new JSONObject(messageBody);
                id = jsonObject.getString("id");
                message = jsonObject.getString("message");
                title = jsonObject.getString("title");
            }
            catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (type.equals("message")) {
            message = messageBody;
        }

        Intent intent = new Intent(this, StudentNewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSound(soundUri)
                .setSmallIcon(R.mipmap.ic_attendance)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_attendance))
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setContentIntent(pendingIntent)
                .setTicker("Latest news")
                .setContentTitle(messageTitle)
                .setContentText(message);

        notificationManager.notify(1, notificationBuilder.build());
    }
}
