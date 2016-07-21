package com.wolframalpha;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TTS {
    private static TTS tts;
    private static final String TAG = "TTS";

    public TextToSpeech textToSpeech;
    public boolean isReady = false;
    public boolean isSpecking = false;

    public static TTS getInstance() {
        if (tts == null) {
            tts = new TTS();
        }
        return tts;
    }

    private TTS() {

//        final Locale locale = new Locale(
//                ConfigManage.getString(ConfigManage.K_TTS_LANGUAGE),
//                ConfigManage.getString(ConfigManage.K_TTS_COUNTRY));
        //final Locale locale = Locale.US;
        final Locale locale = Locale.CHINESE;
        final Resources res = MainActivity.getActivity().getResources();
        final Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, null);

        textToSpeech = new TextToSpeech(MainActivity.getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        //ConvertTextToSpeech();
                        Log.d(TAG, "TTS init OK....");
                        isReady = true;
                    }

                } else {
                    Log.e("error", "Initialization Failed!");
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                //mMediaPlayer.setVolume(1, 1);
                isSpecking = false;
            }

            @Override
            public void onError(String utteranceId) {
            }
        });
    }

    public void speak(String str, boolean interrupt) {
        if(!interrupt) {
            if (isSpecking) {
                Log.d(TAG, "tts.isSpeaking()");
                return;
            }
        }

        Log.d(TAG, "Speak() " + str);
        isSpecking = true;
        //mMediaPlayer.setVolume(0, 0);
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
        textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, params);
    }

    public void speak(String str)
    {
        speak(str,false);
    }

    public void waitReady()
    {
        try {
            while (!isReady) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitSpeaking()
    {
        try {
            while (isSpecking) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        textToSpeech.shutdown();
        tts = null;
    }
}
