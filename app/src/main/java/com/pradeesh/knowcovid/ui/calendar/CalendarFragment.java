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
import android.util.Log;
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
import com.pradeesh.knowcovid.ui.calendar.Worker.WorkReminderHelper;

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
        private ArrayList<String> participantsList;
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

            participantsList = new ArrayList<>();

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
                    Navigation.findNavController(root).navigate(R.id.navigation_showEventsFragment);
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
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT , "Give Event description");

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
                            case "getTitle":{
                                dialogFragment.title.setText(result);
                                initiateConversation("What is the date for the event?", "getDate", 3000);
                                break;
                            }
                            case "getDate":{
                                dialogFragment.date.setText(result);
                                initiateConversation("When will the event start?", "getStartTime", 3000);
                                break;
                            }
                            case "getStartTime":{
                                dialogFragment.startTime.setText(result);
                                initiateConversation("When will the event end?", "getEndTime", 3000);
                                break;
                            }
                            case "getEndTime":{
                                dialogFragment.endTime.setText(result);
                                initiateConversation("Do you want to add any participants for the event?", "askForParticipants", 3000);
                                break;
                            }
                            case "askForParticipants":{
                                if(result.toLowerCase().contains("yes") || result.toLowerCase().contains("yeah")){
                                    initiateConversation("Please speak the participant name", "getParticipants", 3000);
                                } else {
                                    if(participantsList.size() == 0){
                                        dialogFragment.participants.setText("No Participants");
                                    }

                                    speak("Okay, Finished.");
                                    addToDatabase();

                                    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog box");
                                    if (prev != null) {
                                        DialogFragment df = (DialogFragment) prev;
                                        df.dismiss();
                                    }
                                }

                                break;
                            }
                            case "getParticipants":{
                                participantsList.add(result);
                                dialogFragment.participants.setText(TextUtils.join(", ", participantsList));
                                initiateConversation("Do you want to add more participants ?", "askForParticipants", 3000);

                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        long extractTimeInMillis(String time, String date){
            int extractedDate=0 ,extractedHour=0 , extractedMinutes=0;

            for(int i=0;i<date.length();i++){
                if(  date.charAt(i)-'0'>=1 && date.charAt(i)-'0'<=9 ){
                    extractedDate=(date.charAt(i)-'0');
                    if(date.charAt(i+1)-'0'>=0 && date.charAt(i+1)-'0'<=9){
                        extractedDate=10*(extractedDate)+(date.charAt(i+1)-'0');
                    }
                    break;
                }
            }
            //extract hours from user given starTime
            if(time.charAt(0)-'0'>=0 && time.charAt(0)-'0'<=9){
                extractedHour=time.charAt(0)-'0';
                if(time.charAt(1)-'0'>=0 && time.charAt(1)-'0'<=9) {
                    extractedHour = extractedHour*10+(time.charAt(1) - '0');
                }
            }
            for(int i=0;i<time.length();i++){
                if(time.charAt(i)=='p') {
                    extractedHour += 12;
                    extractedHour = extractedHour%24;
                    break;
                }
            }
            //extract minutes from user given starTime
            int startOfMinuteIndex=2;
            if(time.charAt(2)-'0'<0 || time.charAt(2)-'0'>9)
                startOfMinuteIndex++;
            if(time.charAt(startOfMinuteIndex)-'0'>=0 && time.charAt(startOfMinuteIndex)-'0'<=9){
                extractedMinutes=time.charAt(startOfMinuteIndex)-'0';
                if(time.charAt(startOfMinuteIndex+1)-'0'>=0 && time.charAt(startOfMinuteIndex+1)-'0'<=9) {
                    extractedMinutes = extractedMinutes*10+(time.charAt(startOfMinuteIndex+1) - '0');
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    extractedDate,
                    extractedHour,
                    extractedMinutes,
                    0);

            return calendar.getTimeInMillis();
        }

        private void addToDatabase() {

             CustomModel event;

             String title=dialogFragment.title.getText().toString();
             String date = dialogFragment.date.getText().toString();
             String startTime = dialogFragment.startTime.getText().toString();
             String endTime = dialogFragment.endTime.getText().toString();
             String participants= dialogFragment.participants.getText().toString();
             String eventID= title+date+startTime+endTime+participants;
             eventID=eventID.replaceAll("\\s+", "");
             eventID=eventID.replaceAll("\\.", "");
             eventID=eventID.replaceAll(":", "");


            try {

                long sTime=extractTimeInMillis(startTime, date);
                long eTime=extractTimeInMillis(endTime, date);
                Log.d("uri", "sTime: "+sTime);


                event = new CustomModel(eventID , title , date , participants , sTime ,  eTime);

                Databasehelper databasehelper=new Databasehelper(getActivity());
                boolean success = databasehelper.addEvent(event);

                WorkReminderHelper reminderHelper = new WorkReminderHelper();
                reminderHelper.setReminder(getActivity(), eventID, title , "Event is Today from "+ startTime + " to "+endTime,sTime);

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
//            speechRecognizer.destroy();
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

                //Calling Event title Fragment
                dialogFragment=new EventDescDialog();
                dialogFragment.show(getActivity().getSupportFragmentManager(),"dialog box");

                initiateConversation("Please give a title for the event","getTitle", 3000);
            });

            for(String hotword: hotwords.keySet()){
                if (command.toLowerCase().contains(hotword)){
                    hotwords.get(hotword).run();
                }
            }
        }
    }