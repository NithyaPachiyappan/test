package com.hm.demoapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.hm.demoapp.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements RecognitionListener{

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mClick_me;
    private EditText mText;
    private SpeechRecognizer mSpeech;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent service = new Intent(this, PhoneStateListenerService.class);
        startService(service);*/
      //  NetworkRequestQueue nq = new NetworkRequestQueue(MainActivity.this);

        mClick_me = (TextView)findViewById(R.id.click_me);
        mText = (EditText)findViewById(R.id.text) ;
        mClick_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenSpeech();
    }

    private void listenSpeech() {

       // mSpeech = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
       // mSpeech.setRecognitionListener(MainActivity.this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,2);
      //  startActivityForResult(intent, SPEECH_REQUEST_CODE);
      //  mSpeech.startListening(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSpeech != null){
            mSpeech.destroy();
        }
    }

    private void displaySpeechRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.i(TAG, "on res"+spokenText);
            mText.setText(spokenText);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onbeginningofspeech");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onendofspeech");
    }

    @Override
    public void onError(int error) {
        Log.i(TAG, "error code: " + error);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.i(TAG, "onresults");
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String s = "";
        for (String result:matches){
            if (result.equalsIgnoreCase("disconnect")){
                disConnectCall();
            }
            s += result + "\n";

        }

        mText.setText(s);
        Log.i(TAG, "onresults"+s);

        mSpeech.startListening(intent);
    }

    private void disConnectCall() {
        TelephonyManager telephonyManager = (TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz = null;
        try {
            clazz = Class.forName(telephonyManager.getClass().getName());
            Method method = clazz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
            Log.i(TAG, "call disconnected");
            telephonyService.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(TAG, "onpartialresults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(TAG, "onevent");
    }
}
