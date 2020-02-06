package com.example.voice_test;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

class TextToSpeecher {
    private TextToSpeech tts;

    TextToSpeecher(Context context, Float speechRate) {
        this.tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
//                System.out.println("Init");
            }
        });
        tts.setSpeechRate(speechRate);
    }

    void initialize(Context context) {
        if (this.tts.getEngines().size() == 0 ){
            if (Locale.getDefault()==Locale.ENGLISH) {
                Toast.makeText(context, "No TextToSpeech on device!",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, "На устройстве отсутвует приложение с TextToSpeech!",Toast.LENGTH_LONG).show();
            }
        } else {
            this.tts.setLanguage(Locale.getDefault());
        }
    }

    void speak(String message) {
        this.tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
    }

    void shutdown() {
        this.tts.shutdown();
    }
}
