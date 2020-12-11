package com.pradeesh.knowcovid.ui.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pradeesh.knowcovid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class EventsListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CustomModel> arrayList;
    private TextView eventTitle, date, startTime, endTime, participants;
    public EventsListAdapter(Context context, ArrayList<CustomModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false);
        eventTitle = convertView.findViewById(R.id.eventTitle);
        date = convertView.findViewById(R.id.date);
        startTime = convertView.findViewById(R.id.startTime);
        endTime = convertView.findViewById(R.id.endTime);
        participants = convertView.findViewById(R.id.participants);

        eventTitle.setText(arrayList.get(position).getTitle());
        participants.setText(arrayList.get(position).getParticipants());
        date.setText(arrayList.get(position).getDate());
        startTime.setText(getDisplayTime(arrayList.get(position).getStartTime()));
        endTime.setText(getDisplayTime(arrayList.get(position).getEndTime()));

        return convertView;
    }

    String getDisplayTime(long millis){
//        SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss.SSS");
//
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(millis);

        Log.d("uri", "In event display "+ String.valueOf(millis));
        int minutes = (int) ((millis / (1000*60)) % 60);
        minutes = (minutes+30)%60;
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        String amPm = "AM";
        hours = (hours+6)%24;
        if(hours>12){
            hours -= 12;
            amPm = "PM";
        } else if(hours == 12){
            amPm = "PM";
        }

        return String.valueOf(hours) + ":" + String.valueOf(minutes) + " " + amPm;
    }
}
