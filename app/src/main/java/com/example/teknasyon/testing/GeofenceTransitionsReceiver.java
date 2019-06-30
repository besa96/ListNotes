package com.example.teknasyon.testing;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GeofenceTransitionsReceiver extends BroadcastReceiver
{
    private static final int ID_LOCATION = 1;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Log.d(TAG, "Geofence onReceive Triggered!");

        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError())
        {
            final String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }//end if

        //Get the transition type and
        //Test that the reported transition was of interest
        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            //Get the geofences that were triggered. A single event can trigger multiple geofences
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            //Get the transition details as a String
            final String geofenceTransitionDetails = "TEST FIRST";//getGeofenceTransitionDetails(geofencingEvent.getGeofenceTransition(), triggeringGeofences);
            sendNotification(context, intent, geofenceTransitionDetails);
        }//end if
        else
        {
            Log.e(TAG, "Geofence Transition Type INVALID! @ GeofenceTransition");
        }//end else
    }

    private void sendNotification(final Context context, final Intent intent, final String message)
    {
        Log.d(TAG, "SendNotification Triggered");

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //final Intent intent = new Intent(this, LoginActivity.class);//which activity on notification click
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //Android O Requires Notification Channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            final CharSequence name = "Mah Notes!";
            final NotificationChannel notificationChannel = new NotificationChannel("NOTE", name, NotificationManager.IMPORTANCE_DEFAULT);

            //String description = getString(R.string.channel_description);
            //notificationChannel.setDescription(description);

            //register channel to the system
            notificationManager.createNotificationChannel(notificationChannel);
        }//end if

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "NOTE")
                .setSmallIcon(R.drawable.ic_note)
                .setContentTitle("Yer Bildirimi")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(ID_LOCATION, notificationBuilder.build());
    }
}
