package com.pradeesh.knowcovid.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pradeesh.knowcovid.R;

import java.util.ArrayList;

public class EventsListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<CustomModel> arrayList;
    private TextView eventTitle, date, startTime, endTime, participants;
    public EventsListAdapter(Context context, ArrayList<CustomModel> arrayList) {
        super(context,R.layout.fragment_show_events);
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

        return convertView;
    }
}
