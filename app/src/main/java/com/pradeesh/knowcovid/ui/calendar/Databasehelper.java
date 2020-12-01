package com.pradeesh.knowcovid.ui.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Databasehelper extends SQLiteOpenHelper {

    public static final String EVENTS_TABLE="EVENT_TABLE";
    public static final String EVENT_ID="EVENT_ID";
    public static final String EVENT_DESCRIPTION="EVENT_DESCRIPTION";
    public static final String EVENT_PARTICIPANTS="EVENT_PARTICIPANTS";
    public static final String EVENT_START_TIME="EVENT_START_TIME";
    public static final String EVENT_END_TIME="EVENT_END_TIME";


    public Databasehelper(@Nullable Context context) {
        super(context, "events.db" , null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement= "CREATE TABLE " + EVENTS_TABLE+ " (" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EVENT_DESCRIPTION
                + " TEXT, " + EVENT_PARTICIPANTS + " TEXT, " + EVENT_START_TIME + " INTEGER, " + EVENT_END_TIME + " INTEGER )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addEvent(CustomModel customModel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(EVENT_DESCRIPTION , customModel.getDescription());
        cv.put(EVENT_PARTICIPANTS , customModel.getParticipants());
        cv.put(EVENT_START_TIME, customModel.getStartTime());
        cv.put(EVENT_END_TIME,customModel.getEndTime());

        long insert= db.insert(EVENTS_TABLE , null , cv);
        return insert != -1;
    }

    public List<CustomModel> getEvents(){
        List<CustomModel> eventsList = new ArrayList<CustomModel>();

        String getAllQuery= "SELECT * FROM "+ EVENTS_TABLE;
        SQLiteDatabase db= this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getAllQuery, null);

        while(cursor.moveToNext()){
            int eventID= cursor.getInt(0);
            String description = cursor.getString(1);
            String participants = cursor.getString(2);
            int startTime= cursor.getInt(3);
            int endTime = cursor.getInt(4);

            CustomModel event = new CustomModel(eventID,description,participants,startTime,endTime);
            eventsList.add(event);
        }

        return eventsList;

    }
}
