package com.wolframalpha;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

import robot.xyz.com.xyzemotionlib.EmotionFragment;


public class MainActivity extends FragmentActivity {

    String TAG="Brian";
    private final String TURING_APIKEY = "d6afe814cb5b8407813212ae9c6e230d";
    private final String TURING_SECRET = "ed9c9ee478120b3c";
    //private final String TURING_SECRET = "";
    private final String UNIQUEID = "131313131";
    private TuringApiManager mTuringApiManager;

    private String inputStr;

    public Handler sttHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            inputStr = (String) msg.obj;
            Log.d("Brian", "input string=" + inputStr);

            //Brian+: Send request to turling engine
            mTuringApiManager.requestTuringAPI(inputStr);
        }
    };
    private TTS tts;
    private STT stt;
    private static MainActivity activity;

    public static FragmentActivity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        tts = TTS.getInstance();
        stt = STT.getInstance();
        gotoEmotion(this, EmotionFragment.getInstance(), null);
        EmotionFragment.getInstance().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stt.record();
                EmotionFragment.getInstance().showBlinkEye();

            }
        });
        //Init turling SDK
        init();
    }

    private void init() {
        SDKInitBuilder builder = new SDKInitBuilder(this)
                .setSecret(TURING_SECRET).setTuringKey(TURING_APIKEY).setUniqueId(UNIQUEID);
        SDKInit.init(builder,new InitListener() {
            @Override
            public void onFail(String error) {
                Log.d(TAG, error);
            }
            @Override
            public void onComplete() {
                mTuringApiManager = new TuringApiManager(MainActivity.this);
                mTuringApiManager.setHttpListener(myHttpConnectionListener);

            }
        });
    }

    HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

        @Override
        public void onSuccess(RequestResult result) {
            if (result != null) {
                try {
                    Log.d(TAG, result.getContent().toString());
                    JSONObject result_obj = new JSONObject(result.getContent()
                            .toString());
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        tts.speak(result_obj.get("text").toString());
                        EmotionFragment.getInstance().showMoveEye();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onError(ErrorMessage errorMessage) {
            Log.d(TAG, errorMessage.getMessage());
        }
    };


    public static void gotoEmotion (FragmentActivity activity, Fragment fragment, Bundle args) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (args != null) {
            fragment.setArguments(args);
        }
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.frameLayout, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        tts.destroy();
        stt.destroy();
        super.onDestroy();
    }
}
