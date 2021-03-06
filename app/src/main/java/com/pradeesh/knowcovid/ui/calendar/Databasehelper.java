package com.pradeesh.knowcovid.ui.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Databasehelper extends SQLiteOpenHelper {

    public static final String EVENTS_TABLE="EVENTS_TABLE";
    public static final String EVENT_ID="EVENT_ID";
    public static final String EVENT_TITLE="EVENT_TITLE";
    public static final String EVENT_DATE="EVENT_DATE";
    public static final String EVENT_PARTICIPANTS="EVENT_PARTICIPANTS";
    public static final String EVENT_START_TIME="EVENT_START_TIME";
    public static final String EVENT_END_TIME="EVENT_END_TIME";


    public Databasehelper(@Nullable Context context) {
        super(context, "events.db" , null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement= "CREATE TABLE " + EVENTS_TABLE+ " (" + EVENT_ID + " TEXT PRIMARY KEY , " + EVENT_TITLE
                + " TEXT, "+ EVENT_DATE + " TEXT, " + EVENT_PARTICIPANTS + " TEXT, " + EVENT_START_TIME + " INTEGER, " + EVENT_END_TIME + " INTEGER )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+EVENTS_TABLE);
        onCreate(db);
    }

    public boolean addEvent(CustomModel customModel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(EVENT_ID,customModel.getEventID());
        cv.put(EVENT_TITLE , customModel.getTitle());
        cv.put(EVENT_DATE,customModel.getDate());
        cv.put(EVENT_PARTICIPANTS , customModel.getParticipants());
        cv.put(EVENT_START_TIME, customModel.getStartTime());
        cv.put(EVENT_END_TIME,customModel.getEndTime());


        long insert= db.insert(EVENTS_TABLE , null , cv);
        return insert != -1;
    }

    public ArrayList<CustomModel> getEvents(){
        ArrayList<CustomModel> eventsList = new ArrayList<CustomModel>();

        String getAllQuery= "SELECT * FROM "+ EVENTS_TABLE;
        SQLiteDatabase db= this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getAllQuery, null);

        while(cursor.moveToNext()){
            String eventID= cursor.getString(0);
            String title = cursor.getString(1);
            String date=cursor.getString(2);
            String participants = cursor.getString(3);
            long startTime= cursor.getLong(4);
            long endTime = cursor.getLong(5);

            CustomModel event = new CustomModel(eventID,title, date ,participants,startTime,endTime);
            eventsList.add(event);
        }
        return eventsList;
    }
    public void deleteEventByID(String eventID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EVENTS_TABLE, EVENT_ID + "='"+eventID+"'", null);
    }
}
