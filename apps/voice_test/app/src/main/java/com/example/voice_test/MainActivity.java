package com.example.voice_test;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SpeechToTexter.Callback {
    private TextToSpeecher tts;
    private SpeechToTexter recognizer;
    private TextView textView;
    private String state = "done";
    private EditText editText1, editText2, editText3;
    private String signStr;

    protected void onStart() {
        super.onStart();
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        editText1 = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        recognizer = new SpeechToTexter(this);
        tts = new TextToSpeecher(this, (float) 1.7d);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                recognizerIntent();
            }
        });

        recognizer.initialize();
        recognizer.setOnResultCallback(this);
        recognizer.doSomething();
        tts.initialize(this);
        tts.speak("Загружено");

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,  Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},1);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Ready",Toast.LENGTH_LONG).show();
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

//    public void callingBack() {
//        String recognizedMessage = recognizer.getFirstRezult();
//        textView.setText(recognizedMessage);
//        if (recognizedMessage.contains("проверка")) {
//            tts.speak("Тест пройден");
//        }
//    }

    public void recognizerIntent() {
//         else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ru_RU");
            recognizer.startListeningMethod(intent);
//        }
    }

    public void callingBack() {//List
        List<String> result = recognizer.getRezults();
        for (String elem:result
             ) {
            elem = elem.toLowerCase();
        }
        if(result.contains("проверка")) {
            tts.speak("Тест пройден");
        }
        textView.setText(result.toString());
//        state = spotIndications(result);
        state = spotIndicationsSilence(result);
    }

    public String spotIndications(List<String> message) {
        switch (state) {
            case "done":
                if (message.contains("квартира")) {
                    if (message.contains("21")) {
                        tts.speak("Система готова. Показания электричества:");
                        return "electricity";
                    }
                } else if (message.contains("квартира 21")) {
                    tts.speak("Ожидаю. Электричество:");
                    return "electricity";
                }
                break;
            case "electricity":
                if ((message.size() >= 1) && (message.get(0).matches("-?\\d+"))) {
                    Toast.makeText(this, "Электричество: " + message.get(0),Toast.LENGTH_LONG).show();
                    tts.speak(message.get(0) + " приняты. " + "Горячее водоснабжение:");
                    return "hot";
                }
                break;
            case "hot":
                if ((message.size() >= 1) && (message.get(0).matches("-?\\d+"))) {
                    Toast.makeText(this, "Горячее водоснабжение: " + message.get(0),Toast.LENGTH_LONG).show();
                    tts.speak(message.get(0) + " приняты. " + "Холодное водоснабжение:");
                    return "cold";
                }
                break;
            case "cold":
                if ((message.size() >= 1) && (message.get(0).matches("-?\\d+"))) {
                    Toast.makeText(this, "Холодное водоснабжение: " + message.get(0),Toast.LENGTH_LONG).show();
                    tts.speak(message.get(0) + " приняты. ");
                    return "done";
                }
                break;
        }
        return "done";
    }

    public String spotIndicationsSilence(List<String> message) {
        signStr = message.get(0);
        switch (state) {
            case "done":
                if (message.contains("квартира")) {
                    if (message.contains("21")) {
                        tts.speak("Жду:");
                        return "electricity";
                    }
                } else if (message.contains("квартира 21")) {
                    tts.speak("Жду:");
                    recognizerIntent();
                    return "electricity";
                }
                break;
            case "electricity":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Электричество: " + signStr,Toast.LENGTH_LONG).show();
                    tts.speak(signStr);
                    editText1.setText(String.format("Электричество: %s", signStr));
                    recognizerIntent();
                    return "hot";
                }
                break;
            case "hot":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Горячее водоснабжение: " + signStr,Toast.LENGTH_LONG).show();
                    tts.speak(signStr);
                    editText2.setText(String.format("Горячее водоснабжение: %s", signStr));
                    recognizerIntent();
                    return "cold";
                }
                break;
            case "cold":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Холодное водоснабжение: " + signStr,Toast.LENGTH_LONG).show();
                    tts.speak(signStr + " следующий");
                    editText3.setText(String.format("Холодное водоснабжение: %s", signStr));
                    recognizerIntent();
                    return "done";
                }
                break;
        }
        return "done";
    }
}
