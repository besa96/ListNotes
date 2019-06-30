package com.example.teknasyon.testing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NoteAlertReceiver extends BroadcastReceiver
{
    //private static final int ALARM_REQUEST_CODE = 1;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        final NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //Android O Requires Notification Channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            final CharSequence name = "Mah Notes";
            final NotificationChannel notificationChannel = new NotificationChannel("ALARM", name, NotificationManager.IMPORTANCE_DEFAULT);

            //String description = getString(R.string.channel_description);
            //notificationChannel.setDescription(description);

            //register channel to the system
            notificationManager.createNotificationChannel(notificationChannel);
        }//end if

        //build notification
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "ALARM")
                .setSmallIcon(R.drawable.ic_note)
                .setContentTitle("Zamanlı Not")
                .setContentText(intent.getStringExtra("NOT"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("NOT")))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)//onClick behaviour
                .setAutoCancel(true);

        notificationManager.notify(2, notificationBuilder.build());//show notification
    }
}//end class
