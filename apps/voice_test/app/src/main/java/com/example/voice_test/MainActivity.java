package com.example.voice_test;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SpeechToTexter.Callback {
    private TextToSpeecher tts;
    private SpeechToTexter recognizer;
    private TextView textView;

    protected void onStart() {
        super.onStart();
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        recognizer = new SpeechToTexter(this);
        tts = new TextToSpeecher(this);

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
                    recognizer.startListeningMethod(intent);
                }
            }
        });
        recognizer.initialize();
        recognizer.setOnResultCallback(this);
        recognizer.doSomething();
        tts.initialize(this);
        Toast.makeText(this, "Ready",Toast.LENGTH_LONG).show();
        tts.speak("Загружено");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    protected void onResume() {
        super.onResume();
//        recognizer.initialize();
//        tts.initialize(this);
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void callingBack() {
        String recognizedMessage = recognizer.getFirstRezult();
        textView.setText(recognizedMessage);
        if(recognizedMessage.contains("проверка")) {
            tts.speak("Тест пройден");
        }
    }

//    public void callingBack() {//List
//        List<String> result = recognizer.getRezults();
//        for (String elem:result
//             ) {
//            elem = elem.toLowerCase();
//        }
//        if(result.contains("проверка")) {
//            speak("Тест пройден");
//        }
//    }
}
