package com.hm.demoapp.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

public class PhoneStateListenerService extends Service implements TextToSpeech.OnInitListener,RecognitionListener{

    private TextToSpeech tts = null;
    private boolean mIsInit;
    private TelephonyManager mTelephonyManager;
    private String mPhone_no;
    private static final String TAG = PhoneStateListenerService.class.getSimpleName();
    private SpeechRecognizer mSpeech;
    private Intent intent;
    public PhoneStateListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this,this);

        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int mPhoneNumber = mTelephonyManager.getCallState();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() != null){
        mPhone_no = (String)intent.getExtras().get("phone_no");
        Log.e("Receiver",""+mPhone_no);
        if(mIsInit){
            listenSpeech();
            speak(mPhone_no);

        }}

        listenSpeech();
        return this.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
        tts.setLanguage(Locale.US);

        Log.e("Receiver","Incoming call");
            speak(mPhone_no);
        mIsInit = true;
        }
    }

    private void speak(String number) {
        if (tts != null){

            String name = getContactName(this,number);
            if(name != null){
                Log.e("Receiver",""+name);
               // tts.speak("You are getting Call "+name, TextToSpeech.QUEUE_FLUSH, null);
                //disConnectCall();

            }
            else{
                Log.e("Receiver"," null");
            }

        }
    }


    private void listenSpeech() {

        mSpeech = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeech.setRecognitionListener(this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,2);
        //  startActivityForResult(intent, SPEECH_REQUEST_CODE);
        mSpeech.startListening(intent);
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
            if (result.equalsIgnoreCase("Cancel")){
                disConnectCall();
            }
            s += result + "\n";

        }

        //mText.setText(s);
        Log.i(TAG, "onresults"+s);

        mSpeech.startListening(intent);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(TAG, "onpartialresults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(TAG, "onevent");
    }

    public class LocalBinder extends Binder{

        PhoneStateListenerService getService(){

            return PhoneStateListenerService.this;
        }

    }

    private void disConnectCall() {
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
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
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


}
