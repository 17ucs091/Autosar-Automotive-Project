package com.pradeesh.knowcovid.ui.calendar.Worker;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pradeesh.knowcovid.ui.calendar.Databasehelper;
import com.pradeesh.knowcovid.ui.calendar.NotificationHelper;

import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ReminderWorker extends Worker {


    public ReminderWorker(@NonNull  Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {

        Data inputData= getInputData();
        String eventID=inputData.getString("eventID");
        String eventTitle = inputData.getString("eventTitle");
        String eventBody= inputData.getString("eventBody");
        long eventTime= inputData.getLong ("eventTime",-1L);
        if(eventTime==-1L){
            Toast.makeText(getApplicationContext(),"Event Cannot be Scheduled",Toast.LENGTH_LONG).show();
            return  Result.failure();
        }
        Databasehelper databasehelper= new Databasehelper(getApplicationContext());
        databasehelper.deleteEventByID(eventID);

//        val formatter = SimpleDateFormat("h:mm a MMM d", Locale.ENGLISH);
        NotificationHelper notificationHelper=new NotificationHelper(getApplicationContext());
        Notification.Builder notifyBuilder= notificationHelper.getNotification(eventTitle,eventBody);
//        val notifyBuilder = NotificationHelper().getReminderNotificationBuilder(context, note.noteTitle.toString(), formatter.format(reminderTime))
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Log.d("uri","Below NotificationManger");
        notificationManager.notify(Integer.parseInt(eventID), notifyBuilder.build());


        return Result.success();
    }
}
