package com.pradeesh.knowcovid.ui.calendar.Worker;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.pradeesh.knowcovid.ui.calendar.Databasehelper;
import com.pradeesh.knowcovid.ui.calendar.NotificationHelper;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ReminderWorker extends Worker {

    Context context1;
    public ReminderWorker(@NonNull  Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        context1=context;
    }

    @Override
    public Result doWork() {
        Data inputData= getInputData();
        String eventID = inputData.getString("eventID");
        String eventTitle = inputData.getString("eventTitle");
        String eventBody= inputData.getString("eventBody");
        long eventTime= inputData.getLong ("eventTime",-1L);
        if(eventTime==-1L){
            Toast.makeText(context1,"Event Cannot be Scheduled",Toast.LENGTH_LONG).show();
            return  Result.failure();
        }
        Databasehelper databasehelper= new Databasehelper(context1);
        databasehelper.deleteEventByID(eventID);

        NotificationHelper notificationHelper=new NotificationHelper(context1);
        Notification.Builder notifyBuilder= notificationHelper.getNotification(context1,eventTitle,eventBody);

        NotificationManager notificationManager = (NotificationManager) context1.getSystemService(NOTIFICATION_SERVICE);

        int oneTimeID = (int) (SystemClock.uptimeMillis() % 99999999);
        notificationManager.notify(oneTimeID, notifyBuilder.build());

        return Result.success();
    }
}
