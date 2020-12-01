package com.pradeesh.knowcovid.ui.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.pradeesh.knowcovid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ShowEventsFragment extends Fragment {

    private ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_show_events, container, false);

        listView = root.findViewById(R.id.listView);

        getEventsFromDB();
        return root;
    }

    public void getEventsFromDB(){
        Databasehelper databasehelper= new Databasehelper(getActivity());

        ArrayList<CustomModel> events=databasehelper.getEvents();
        EventsListAdapter myAdapter=new EventsListAdapter(getActivity(),events);
        listView.setAdapter(myAdapter);
//        Toast.makeText(getActivity(),events.toString(),Toast.LENGTH_LONG).show();
    }
}