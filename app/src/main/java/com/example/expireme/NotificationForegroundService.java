package com.example.expireme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationForegroundService extends Service {
    String CHANNEL_ID = "1";

    int notificationId = 9;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel2";
            String description = "tty";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        // Intent to spec ify 6yhe action
        //Intent notificationIntent = new Intent(this, HomeActivity.class);

        // USe pending intent to trigger my mainactivity upon user click
        //PendingIntent pendingIntent = PendingIntent
          //      .getActivity(this,0,notificationIntent,0);
/*
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Foreground service notification");
        builder.setContentText("Serice is running now!");
        builder.setSmallIcon(R.drawable.notificationimg);
        builder.setContentIntent(pendingIntent);
        builder.build();
        //builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
*/

        // create a notification bar
    //    Notification notificationBar = new Notification.Builder(this, CHANNEL_ID)
      //          .setContentTitle("Foreground service notification")
        //        .setContentText("Serice is running now!")
          //      .setSmallIcon(R.drawable.notificationimg)
            //    .setContentIntent(pendingIntent)
              //  .build();

        // send the data about notification to the foreground
        //startForeground(1,notificationBar);
        // Create an explicit intent for an Activity in your app
        Intent new_intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new_intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationimg)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        //notificationManager.notify(notificationId, builder.build());
        return START_STICKY;

    }

}
