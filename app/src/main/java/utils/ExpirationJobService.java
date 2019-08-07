package utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.expireme.HomeActivity;
import com.example.expireme.R;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ExpirationJobService extends JobService {
    String CHANNEL_ID = "1";
    int intentId = 100;
    NotificationManager notificationManager;
    int notificationId = 9;
    DatabaseHelper dbHelper;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ExpireMe! Channel";
            String description = "tty";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void myJobNotify() {
        createNotificationChannel();
        Intent new_intent = new Intent(this, HomeActivity.class);
        new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, intentId, new_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationimg)
                .setContentTitle("You have expired items!")
                .setContentText("Open the app to find where to buy new items!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notificationBar = builder.build();
        Log.e("onStartCommand", "notificationBar.flags=" + notificationBar.flags);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBar);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        Log.i("ExpirationJobService", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ExpirationJobService", "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.e("onStartJob", "postDelayed run");
        List<FoodItem> foodItems = dbHelper.getAllItems();
        Date currentDate = new Date();

        for (FoodItem item: foodItems) {
            long diffInMillies = item.getDateExpiration().getTime() - currentDate.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diffInDays < 0) {
                Log.e("onStartJob", "found expired items, sending notification");
                myJobNotify();
                break;
            }
        }
        jobFinished(params, false);
        Log.i("ExpirationJobService", "onStopJob");
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("ExpirationJobService", "onStopJob");
        return false;
    }


}
