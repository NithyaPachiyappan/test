package com.hm.demoapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hm.demoapp.service.PhoneStateListenerService;

/**
 * Created by Nithya Pachiyappan on 3/7/2017.
 */

public class PhoneStateListener extends BroadcastReceiver {

    TextToSpeech tts = null;
    private String phoneNr;

    @Override
    public void onReceive(Context context, Intent intent) {


        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);



        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (tm.getCallState()) {

            case TelephonyManager.CALL_STATE_RINGING:
                 phoneNr= intent.getStringExtra("incoming_number");
                Log.e("number",""+phoneNr);
                break;
        }

        Intent service_intent = new Intent(context, PhoneStateListenerService.class);
        service_intent.putExtra("phone_no",phoneNr);
        context.startService(service_intent);



    }
}
