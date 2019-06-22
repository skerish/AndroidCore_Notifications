package com.example.androidcore_notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button notifyButton;
    private Button updateButton;
    private Button cancelButton;

    private static final int NOTIFICATION_ID = 0;  // for future use
    private static final String PRIMARY_CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private NotificationManager notificationManager;

    private static final String ACTION_UPDATE_NOTIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_UPDATE_NOTIFICATION";
    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifyButton = findViewById(R.id.notifyMe);
        updateButton = findViewById(R.id.update);
        cancelButton = findViewById(R.id.cancel);

        setNotificationButtonState(true, false, false);

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
                setNotificationButtonState(false, true, true);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
                setNotificationButtonState(false, false, true);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
                setNotificationButtonState(true, false, false);
            }
        });

        createNotificationChannel();

        registerReceiver(notificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    private void sendNotification() {
        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                                                    intent, PendingIntent.FLAG_ONE_SHOT);

        // For adding action button which will work without opening the app.
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }


    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(androidImage)
                    .setBigContentTitle("Notification Updated!"));
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }


    private NotificationCompat.Builder getNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been Notified!")
                .setContentText("This is your Notification text.")
                .setSmallIcon(R.drawable.my_icon)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setDeleteIntent(notificationPendingIntent)
                // For backward compatibility (both should be declared for working in this case 'HIGH')
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        //Setting auto-cancel to true closes the notification when user taps on it.
        return  notifyBuilder;
    }

    public void createNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    void setNotificationButtonState(Boolean isNotifyEnabled,
                                     Boolean isUpdateEnabled,
                                     Boolean isCancelEnabled){
        notifyButton.setEnabled(isNotifyEnabled);
        updateButton.setEnabled(isUpdateEnabled);
        cancelButton.setEnabled(isCancelEnabled);
    }


    public class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }

    }

}
