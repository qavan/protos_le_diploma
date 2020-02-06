package com.example.voice_test;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecog;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
//            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,  Manifest.permission.RECORD_AUDIO)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ru_RU");
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en");
                    speechRecog.startListening(intent);
                }
            }
        });

        initializeTextToSpeech();
        initializeSpeechRecognizer();
        Toast.makeText(MainActivity.this, "Ready",Toast.LENGTH_LONG).show();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecog.setRecognitionListener(new RecognitionListener() {
//                @Override
                public void onReadyForSpeech(Bundle params) {
                    System.out.println("onReadyForSpeech");
                }
//                @Override
                public void onBeginningOfSpeech() {
                    System.out.println("onBeginningOfSpeech");
                }
//                @Override
                public void onRmsChanged(float rmsdB) {
//                    System.out.println("rmsdB changed to "+rmsdB);
                }
//                @Override
                public void onBufferReceived(byte[] buffer) {
                    System.out.println("onBufferReceived");
                }
//                @Override
                public void onEndOfSpeech() {
                    System.out.println("onEndOfSpeech");
                }
//                @Override
                public void onError(int error) {
                    System.out.println("ERROR "+error);
                }
//                @Override
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(result_arr.get(0));
                }
//                @Override
                public void onPartialResults(Bundle partialResults) {
                    System.out.println("onPartialResults");
                }
//                @Override
                public void onEvent(int eventType, Bundle params) {
                    System.out.println("onEvent");
                }
            });
        }
    }

    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();
        if(result_message.contains("проверка")) {
//        if(result_message.contains("do")) {
//            if (result_message.contains("you")) {
//                if (result_message.contains("understand")) {
//                    if (result_message.contains("me")) {
            speak("Тест пройден");
//                    }
//                }
//            }
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
//                    tts.setLanguage(Locale.US);
//                    speak("Started");
                    tts.setLanguage(Locale.getDefault());
                    speak("Загружено");
                }
            }
        });
    }

    private void speak(String message) {
//        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
//        } else {
//            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
//        }
    }



//    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }
//    @Override
    protected void onResume() {
        super.onResume();
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }
}
