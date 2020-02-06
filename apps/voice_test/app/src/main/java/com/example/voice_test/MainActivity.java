package com.example.voice_test;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myLogs";
    private TextToSpeech tts;
    private SpeechRecognizer recognizer;
    private Button button;
    private TextView textView;

    protected void onStart() {
        super.onStart();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,  Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},1);
                }
            } else {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ru_RU");
                recognizer.startListening(intent);
            }
            }
        });

        initializeTextToSpeech();
        initializeSpeechRecognizer();
        Toast.makeText(MainActivity.this, "Ready",Toast.LENGTH_LONG).show();
    }

    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    protected void onResume() {
        super.onResume();
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();
        if(result_message.contains("проверка")) {
            speak("Тест пройден");
        }
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                public void onReadyForSpeech(Bundle params) {
                    Log.i(TAG,"onReadyForSpeech");
                }
                public void onBeginningOfSpeech() {
                    Log.i(TAG,"onBeginningOfSpeech");
                }
                public void onRmsChanged(float rmsdB) {
//                    Log.i(TAG,"rmsdB changed to "+rmsdB);
                }
                public void onBufferReceived(byte[] buffer) {
                    Log.i(TAG,"onBufferReceived");
                }
                public void onEndOfSpeech() {
                    Log.i(TAG,"onEndOfSpeech");
                }
                public void onError(int error) {
                    Log.i(TAG,"ERROR "+error);
                }
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(result_arr.get(0));
                    Log.i(TAG, String.format("onResults: %s", result_arr));
                    Toast.makeText(MainActivity.this, String.format("Распознано: %s", result_arr),Toast.LENGTH_LONG).show();
                    textView.setText(result_arr.get(0));
                }
                public void onPartialResults(Bundle partialResults) {
                    Log.i(TAG,"onPartialResults");
                }
                public void onEvent(int eventType, Bundle params) {
                    Log.i(TAG, String.format("onEvent %d %s", eventType, params));
                }
            });
        }
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            //            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(MainActivity.this, "No TTS on device!",Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.getDefault());
                    speak("Загружено");
                }
            }
        });
    }

    private void speak(String message) {
        tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
    }
}
