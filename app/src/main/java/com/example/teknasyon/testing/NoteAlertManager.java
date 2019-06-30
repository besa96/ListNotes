package com.example.teknasyon.testing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NoteAlertManager
{
    private Context context;
    private static final int ALARM_REQUEST_CODE = 1;

    public NoteAlertManager(final Context context)
    {
        this.context = context;
    }

    public void createAlarm(final String contentText, final int hour, final int minute)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        final Intent intent = new Intent(context, NoteAlertReceiver.class);
        intent.putExtra("NOT", contentText);

        final PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingAlarmIntent);
    }

    public void cancelAlarm(final String contentText)//in order to cancel, pendingIntent must be the same with the one created
    {
        final Intent intent = new Intent(context, NoteAlertReceiver.class);
        intent.putExtra("NOT", contentText);

        final PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingAlarmIntent);
    }
}
