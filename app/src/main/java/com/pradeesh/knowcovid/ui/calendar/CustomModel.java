package com.pradeesh.knowcovid.ui.calendar;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomModel {

    private int eventID=-1;
    private String description="";
    private String participants;
    private long startTime=0;
    private long endTime=0;


    public CustomModel(int eventID, String description, String participants, long startTime, long endTime) {
        this.eventID = eventID;
        this.description = description;
        this.participants = participants;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CustomModel{" +
                "eventID=" + eventID +
                ", description='" + description + '\'' +
                ", participants=" + participants +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParticipants() {
        return participants.toString();
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