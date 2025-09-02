package com.baghdadhomes.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.baghdadhomes.Activities.HomeActivity;
import com.baghdadhomes.R;

public class Service extends FirebaseMessagingService {

    private String CHANNEL_ID = "messaging_id";
    private NotificationChannel mChannel;
    private int importance;
    public static final int NOTIFICATION_ID_CHAT = 26;
    private String title = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("TAG", " notification " + remoteMessage.getData().toString());
        if (remoteMessage.getNotification().getTitle() != null) {
            title = remoteMessage.getNotification().getTitle();
        } else {
            title = "نجف هوم";
        }

        String type = "Other";
        String sendTo = "";
        String sendBy = "";
        String postData = "";
        String postId = "";
        if (remoteMessage.getData() != null){
            if (remoteMessage.getData().get("type") != null) {
                type = remoteMessage.getData().get("type");
            }

            if (remoteMessage.getData().get("sendBy") != null){
                sendBy = remoteMessage.getData().get("sendBy");
            }

            if (remoteMessage.getData().get("sendBy") != null){
                sendTo = remoteMessage.getData().get("sendBy");
            }
            if (remoteMessage.getData().get("postData") != null){
                postData = remoteMessage.getData().get("postData");
            }
            if (remoteMessage.getData().get("property_id") != null){
                postId = remoteMessage.getData().get("property_id");
            }
        }
        Log.d("typeNotification", type);
        showNotification(title, remoteMessage.getNotification().getBody(),type,sendTo,sendBy, postData, postId);

    }

    private void showNotification(String title, String messageBody, String type, String sendTo, String sendBy, String postData, String postId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("sendTo", sendTo);
        intent.putExtra("sendBy", sendBy);
        intent.putExtra("postData", postData);
        intent.putExtra("propertyId", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
       /* PendingIntent pendingIntent = PendingIntent.getActivity(this, 0  , intent,
                PendingIntent.FLAG_IMMUTABLE);*/
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getPackageName();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setTicker(title)
                .setColor(getResources().getColor(R.color.purple_200))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(channelId)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(bitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.app_name);
            String description = "نجف هوم";
            final int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            channel.setSound(sound, audioAttributes);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
            // Register the channel with the system
        }
        notificationManager.notify(NOTIFICATION_ID_CHAT , notificationBuilder.build());
    }
}





/*public class Service extends FirebaseMessagingService {


    private String CHANNEL_ID = "messaging_id";
    private NotificationChannel mChannel;
    private int importance;
    public static final int NOTIFICATION_ID_CHAT = 26;
    private String title = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("TAG", " notification " + remoteMessage.getNotification().getBody());
        if (remoteMessage.getNotification().getTitle() != null) {
            title = remoteMessage.getNotification().getTitle();
        } else {
            title = "نجف هوم";
        }
        showNotification(title, remoteMessage.getNotification().getBody());

    }

    private void showNotification(String title, String messageBody) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0  , intent,
                PendingIntent.FLAG_IMMUTABLE);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getPackageName();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setTicker(title)
                .setColor(getResources().getColor(R.color.purple_200))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(channelId)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(bitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.app_name);
            String description = "نجف هوم";
            final int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            channel.setSound(sound, audioAttributes);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
            // Register the channel with the system
        }
        notificationManager.notify(NOTIFICATION_ID_CHAT , notificationBuilder.build());
    }
}*/
