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
import androidx.navigation.Navigation;

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

            showEvents.setOnClickListener( Navigation.createNavigateOnClickListener(R.id.navigation_showEventsFragment));
//            showEvents.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Navigation.createNavigateOnClickListener(R.id.navigation_showEventsFragment);
//
//                }
//            });

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
                                initiateConversation("Do you want to add any participants for the event?", "askForParticipants", 3000);
                                break;
                            }
                            case "askForParticipants":{
                                if(result.toLowerCase().contains("yes") || result.toLowerCase().contains("yeah")){
                                    initiateConversation("Please speak the participant name", "getParticipants", 3000);
                                } else {
                                    speak("Okay, Finished.");
                                    addToDatabase();
                                }

                                break;
                            }
                            case "getParticipants":{
                                String previousParticipants = dialogFragment.participants.getText().toString();
                                dialogFragment.participants.setText(((previousParticipants == "")?previousParticipants:(previousParticipants + ", ")) + result);
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
             String description=dialogFragment.description.getText().toString();
             long startTime=1606846786486L;
             long endTime=1606856786486L;

             String participants= dialogFragment.description.getText().toString();
            try {

                event = new CustomModel(-1,description,participants, startTime, endTime);
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
            });

            for(String hotword: hotwords.keySet()){
                if (command.toLowerCase().contains(hotword)){
                    hotwords.get(hotword).run();
                }
            }
        }
    }