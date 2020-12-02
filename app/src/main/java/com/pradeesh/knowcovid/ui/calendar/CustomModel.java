package com.pradeesh.knowcovid.ui.calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomModel {

    private String eventID="";
    private String title="";
    private String date="";
    private String participants;
    private long startTime=0;
    private long endTime=0;

    public CustomModel(String eventID, String title, String date, String participants, long startTime, long endTime) {
        Log.d("uri","Inside customModel");
        this.eventID = eventID;
        this.title = title;
        this.date = date;
        this.participants = participants;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CustomModel{" +
                "eventID='" + eventID + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", participants='" + participants + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
