package com.pradeesh.knowcovid.ui.calendar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.pradeesh.knowcovid.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.pradeesh.knowcovid.utils.Constant.MAPURL;
    public class CalendarFragment extends Fragment {
        public static final Integer RecordAudioRequestCode = 1;
        private SpeechRecognizer speechRecognizer;
        private TextView editText;
        private Button micButton;

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
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(root.getContext());

            final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {
                    editText.setText("");
                    editText.setHint("Listening...");
                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
//                    micButton.setImageResource(R.drawable.ic_mic_black_off);
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    editText.setText(data.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });

            micButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            });
            return root;
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


        public void detectHotwords(ArrayList<String> data){

        }
    }