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

import java.util.ArrayList;

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
        Log.d("uri", arrayList.get(position).getDate());
        date.setText(arrayList.get(position).getDate());
        startTime.setText(getDisplayTime(arrayList.get(position).getStartTime()));
        endTime.setText(getDisplayTime(arrayList.get(position).getEndTime()));

        return convertView;
    }

    String getDisplayTime(long millis){
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        String amPm = "AM";
        if(hours>12){
            hours -= 12;
            amPm = "PM";
        } else if(hours == 12){
            amPm = "PM";
        }

        return String.valueOf(hours) + ":" + String.valueOf(minutes) + " " + amPm;
    }
}
