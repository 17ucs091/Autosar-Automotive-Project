package com.pradeesh.knowcovid.ui.calendar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pradeesh.knowcovid.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.pradeesh.knowcovid.utils.Constant.MAPURL;
    public class CalendarFragment extends Fragment {
        public static final Integer RecordAudioRequestCode = 1;
        private SpeechRecognizer speechRecognizer;
        private TextToSpeech textToSpeech;
        private TextView editText;
        private Button micButton,showEvents;
        private String description;
        private long startTime=1606846786486L;
        private long endTime=1606856786486L;

        private ArrayList<String> participants_G;
        Cursor cursor;
        EventDescDialog dialogFragment;
        private String purpose = null;
        private static final int REQUEST_CODE_SPEECH_INPUT=100;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_cal, container, false);
            setUI(root);

            if (ContextCompat.checkSelfPermission(root.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
            participants_G=new ArrayList<String>();
            editText = root.findViewById(R.id.text);
            micButton = root.findViewById(R.id.voice_button);
            showEvents = root.findViewById(R.id.show_events);

            textToSpeech = new TextToSpeech(getContext().getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i == TextToSpeech.SUCCESS){
                        // select language
                        int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });


            showEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Databasehelper databasehelper= new Databasehelper(getActivity());
                    List<CustomModel> events=databasehelper.getEvents();

                    Toast.makeText(getActivity(),events.toString(),Toast.LENGTH_LONG).show();


//                    if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        Log.d("uri","Inside BuilVersion");
//                        cursor = getActivity().getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null);
//
//                        Log.d("uri",CalendarContract.Events.CONTENT_URI.toString());
//                    }
//                    if(cursor.moveToFirst())
//                        Log.d("uri","cursor is not empty");
//                    else
//                        Log.d("uri","is empty");
//
//                    while (cursor.moveToNext()) {
//                        Log.d("dsd","DownLog");
//                        if (cursor != null) {
//
//                            int val1 = cursor.getColumnIndex(CalendarContract.Events._ID);
//                            int val2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
//                            int val3 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
//                            int val4 = cursor.getColumnIndex(CalendarContract.Events.DTEND);
//
//                            String ID_val = cursor.getColumnName(val1);
//                            String title_val = cursor.getColumnName(val2);
//                            String sTime_val = cursor.getColumnName(val3);
//                            String eTime = cursor.getColumnName(val4);
//
//                            Toast.makeText(getContext(), ID_val + " " + title_val + " " + sTime_val + " " + eTime, Toast.LENGTH_LONG).show();
//
//                        } else {
//                            Toast.makeText(getContext(), "Event is not Present", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    Log.d("dsd","DownLog");
                }
            });

            micButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    purpose="detectHotwords";
                    getSpeechFromUser();
                }
            });
            return root;
        }


        private void getSpeechFromUser(){
            Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT , "Give Event Description");

            try{
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch(Exception e){
                Toast.makeText(getContext()," "+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch(requestCode){
                case REQUEST_CODE_SPEECH_INPUT:{
                    if(resultCode== RESULT_OK && null!=data){
                        String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);

                        switch(purpose){
                            case "detectHotwords":{
                                detectHotwords(result);
                                break;
                            }
                            case "getDescription":{
                                dialogFragment.description.setText(result);
                                description=result;
                                initiateConversation("Do you want to add any participants for the event?", "askForParticipants", 3000);
                                break;
                            }
                            case "askForParticipants":{
                                if(result.toLowerCase().contains("yes") || result.toLowerCase().contains("yeah")){
                                    initiateConversation("Please speak the participant name", "getParticipants", 3000);
                                } else {
                                    speak("Okay, Finished.");

                                    addToDatabase();

                                    description="";
                                    participants_G.clear();

                                }

                                break;
                            }
                            case "getParticipants":{
                                String previousParticipants = dialogFragment.participants.getText().toString();
                                dialogFragment.participants.setText(((previousParticipants == "")?previousParticipants:(previousParticipants + ", ")) + result);
                                participants_G.add(result);
                                initiateConversation("Do you want to add more participants ?", "askForParticipants", 3000);

                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        private void addToDatabase() {
            CustomModel event;
            try {
                event = new CustomModel(-1,description, TextUtils.join(", ", participants_G), startTime, endTime);
                Toast.makeText(getActivity(), event.toString(), Toast.LENGTH_LONG).show();

                Databasehelper databasehelper=new Databasehelper(getActivity());
                boolean success = databasehelper.addEvent(event);
                if(success)
                Toast.makeText(getActivity() , "EVENT IS SUCCESSFULLY ADDED" ,Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "Error adding event", Toast.LENGTH_SHORT).show();
            }


        }

        private void setUI(View root) {
            micButton = root.findViewById(R.id.voice_button);
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            speechRecognizer.destroy();
        }

        private void checkPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getActivity(),"Permission Granted",Toast.LENGTH_SHORT).show();
            }
        }

        public void speak(String text){
            int speech = textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null );

        }

        public void initiateConversation(String textToSpeak, String latestPurpose, int delay){
            speak(textToSpeak);
            purpose = latestPurpose;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSpeechFromUser();
                }
            }, delay);
        }

        public void detectHotwords(String command){
            HashMap<String, Runnable> hotwords = new HashMap();
//            hotwords.put("schedule", "adding an event");
//            hotwords.put("show", "showing today's events");
//            hotwords.put("read", "Reading today's events");

            hotwords.put("schedule", () -> {
                editText.setText(command);

                //Calling Event Description Fragment
                dialogFragment=new EventDescDialog();
                dialogFragment.show(getActivity().getSupportFragmentManager(),"dialog box");

                initiateConversation("Please give a description for the event","getDescription", 3000);

                // Making an event
//                ContentResolver cr= getActivity().getContentResolver();
//                ContentValues cv= new ContentValues();
//                cv.put(CalendarContract.Events.TITLE,"Event for Car Service");
//                cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis()+30*1000);
//                cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis()+60*60*1000);
//                cv.put(CalendarContract.Events.CALENDAR_ID,15);
//                cv.put(CalendarContract.Events.EVENT_TIMEZONE,Calendar.getInstance().getTimeZone().getID());
//
//                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI,cv);
//                Log.d("uri",uri.toString());
//                Toast.makeText(getContext(), "Event is successfully added", Toast.LENGTH_SHORT).show();

            });

            for(String hotword: hotwords.keySet()){
                if (command.toLowerCase().contains(hotword)){
                    hotwords.get(hotword).run();
                }
            }
        }
    }