package com.example.voice_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.Collections;
import java.util.List;

class SpeechToTexter {
    private static final String TAG = "SpeechToTexter LOG";
    private SpeechRecognizer recognizer = null;
    private Context context;
    private List<String> rezults = null;
    private Callback onResultCallback;

    SpeechToTexter(Context context) {
        this.context = context;
    }

    public SpeechToTexter(Context context, Callback onResultCallback) {
        this.context = context;
        this.onResultCallback = onResultCallback;
    }

    void setOnResultCallback(Callback onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    List<String> getRezults() {
        if (rezults != null) {
            return rezults;
        }
        else
            return Collections.singletonList((""));
    }

    public String getFirstRezult() {
        if (rezults != null) {
            return rezults.get(0);
        }
        else
            return "";
    }

    void initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(this.context)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
            recognizer.setRecognitionListener(new RecognitionListener() {
                public void onReadyForSpeech(Bundle params) {
                    Log.i(TAG, "onReadyForSpeech");
                }

                public void onBeginningOfSpeech() {
                    Log.i(TAG, "onBeginningOfSpeech");
                }

                public void onRmsChanged(float rmsdB) {
                    //                Log.i(TAG,"rmsdB changed to "+rmsdB);
                }

                public void onBufferReceived(byte[] buffer) {
                    Log.i(TAG, "onBufferReceived");
                }

                public void onEndOfSpeech() {
                    Log.i(TAG, "onEndOfSpeech");
                }

                public void onError(int error) {
                    Log.i(TAG, "ERROR " + error);
                }

                public void onResults(Bundle results) {
                    rezults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Log.i(TAG, String.format("Result: %s", rezults));
                    onResultCallback.callingBack();
                }

                public void onPartialResults(Bundle partialResults) {
                    Log.i(TAG, "onPartialResults");
                }

                public void onEvent(int eventType, Bundle params) {
                    Log.i(TAG, String.format("onEvent %d %s", eventType, params));
                }
            });
        }
    }

    void startListeningMethod(Intent intent) {
        recognizer.startListening(intent);
    }

    private void speak(String message) {
//        tts.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
    }

    interface Callback{
        void callingBack();
    }

    void doSomething(){
        onResultCallback.callingBack();
    }
}