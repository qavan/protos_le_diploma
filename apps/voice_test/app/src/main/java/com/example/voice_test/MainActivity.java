package com.example.voice_test;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements SpeechToTextUtil.SpeechToTextListener, TextToSpeechUtil.TextToSpeechListener, Button.OnClickListener {
    private static final String TAG = "MAIN_ACTIVITY";

    private TextToSpeechUtil textToSpeech;
    private SpeechToTextUtil speechToText;
    private TextView textView;
    private Intent intent;
    private String state = "done";
    private EditText editText1, editText2, editText3;
    private String signStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Ready", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(MainActivity.this);

        textView = findViewById(R.id.textView);

        editText1 = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);

        textToSpeech = new TextToSpeechUtil(this, (float) 1.7d);

        speechToText = new SpeechToTextUtil(this);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru_RU");
    }

    @Override
    protected void onPause() {
        super.onPause();
        speechToText.onPause();
        textToSpeech.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        speechToText.onResume(intent);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void recognizerIntent() {
        speechToText.onStart(intent);
    }

    public String spotIndicationsSilence(List<String> message) {
        signStr = message.get(0);
        switch (state) {
            case "done":
                if (message.contains("квартира")) {
                    if (message.contains("21")) {
                        textToSpeech.speak("Жду:");
                        return "electricity";
                    }
                } else if (message.contains("квартира 21")) {
                    textToSpeech.speak("Жду:");
                    recognizerIntent();
                    return "electricity";
                }
                break;
            case "electricity":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Электричество: " + signStr,Toast.LENGTH_LONG).show();
                    textToSpeech.speak(signStr);
                    editText1.setText(String.format("Электричество: %s", signStr));
                    recognizerIntent();
                    return "hot";
                }
                break;
            case "hot":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Горячее водоснабжение: " + signStr,Toast.LENGTH_LONG).show();
                    textToSpeech.speak(signStr);
                    editText2.setText(String.format("Горячее водоснабжение: %s", signStr));
                    recognizerIntent();
                    return "cold";
                }
                break;
            case "cold":
                if ((message.size() >= 1) && (signStr.matches("-?\\d+"))) {
                    Toast.makeText(this, "Холодное водоснабжение: " + signStr,Toast.LENGTH_LONG).show();
                    textToSpeech.speak(signStr + " следующий");
                    editText3.setText(String.format("Холодное водоснабжение: %s", signStr));
                    recognizerIntent();
                    return "done";
                }
                break;
        }
        return "done";
    }

    @Override
    public void onResult(String[] results) {
        for (String elem : results
        ) {
            elem = elem.toLowerCase();
        }
        List<String> recognizedResults = Arrays.asList(results);
        if (recognizedResults.contains("проверка")) {
            textToSpeech.speak("Тест пройден");
        }
        textView.setText(recognizedResults.toString());
        state = spotIndicationsSilence(recognizedResults);
    }

    @Override
    public void onError(String message, int code) {
        Log.i(TAG, message);
    }

    @Override
    public void onClick(View v) {
        recognizerIntent();
    }
}
