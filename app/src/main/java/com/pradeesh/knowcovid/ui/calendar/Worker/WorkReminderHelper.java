package com.pradeesh.knowcovid.ui.calendar.Worker;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkReminderHelper {

    public void setReminder(Context context ,String eventID, String eventTitle , String eventBody , long eventTime ){


        Data data =new Data.Builder()
                .putString("eventID",eventID)
                .putString("eventTitle",eventTitle)
                .putString("eventBody",eventBody)
                .putLong("eventTime",eventTime)
                .build();


        long delay=eventTime- Calendar.getInstance().getTimeInMillis();
        Log.d("uri1", String.valueOf(delay));
        Log.d("uri1",eventID);
        OneTimeWorkRequest setReminderRequest =new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(eventID)
                .build();

        WorkManager.getInstance(context).enqueue(setReminderRequest);
    }
}
