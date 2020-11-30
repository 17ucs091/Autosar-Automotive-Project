package com.pradeesh.knowcovid.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.pradeesh.knowcovid.R;

public class EventDescDialog extends DialogFragment {

    public EditText description;
    public EditText participants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_desc_dialog, container, false);
        description = v.findViewById(R.id.description);
        participants = v.findViewById(R.id.participants);

        return v;
    }

}
