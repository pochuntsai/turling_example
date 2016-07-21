package com.wolframalpha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class STT {
    private static STT stt;

    private SpeechRecognizer recognizer;
    public static final int RQS_VOICE_RECOGNITION = 10;
    Intent intent;

    public static STT getInstance() {
        if (stt == null) {
            stt = new STT(MainActivity.getActivity());
        }
        return stt;
    }

    private STT(Activity activity) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity);

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // List<String> resList = params.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                // StringBuffer sb = new StringBuffer();
                // for(String res: resList) {
                //     sb.append(res + "\n");
                // }
                // Log.d("RECOGNIZER", "onResults: " + sb.toString());
                Log.d("RECOGNIZER", "onReadyForSpeech()");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("RECOGNIZER", "onBeginningOfSpeech()");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.v("RECOGNIZER","recieve : " + rmsdB + "dB");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("RECOGNIZER", "onBufferReceived()");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("RECOGNIZER", "onEndOfSpeech()");
            }

            @Override
            public void onError(int error) {
                Log.d("RECOGNIZER", "onError() " + error);
//                ArrayList<String> recData = new ArrayList<String>();
//                DomParserXML.inputStr = recData;

//                recognizer.cancel();
//                recognizer.startListening(intent);
            }

            @Override
            public void onResults(Bundle results) {
                Log.d("RECOGNIZER", "onResults()");

                ArrayList<String> recData = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                Log.d("STT", "onResults()" + recData);
                //DomParserXML.inputStr = recData.get(0);
//                DomParserXML.inputStr = recData;
                Message msg = new Message();
                msg.obj = recData.get(0);
                ((MainActivity)MainActivity.getActivity()).sttHandler.sendMessage(msg);
//                recognizer.stopListening();

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("RECOGNIZER", "onPartialResults()");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("RECOGNIZER", "onEvent()");
            }
        });

    }

    public void record() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.getActivity().getApplication().getPackageName());
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
        recognizer.startListening(intent);

//        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.getActivity().getApplication().getPackageName());
//        recognizer.startListening(intent);
//        MainActivity.getActivity().startActivityForResult(intent, RQS_VOICE_RECOGNITION);
    }

    public void destroy() {
        recognizer.stopListening();
        recognizer.destroy();
        stt = null;
    }
}
