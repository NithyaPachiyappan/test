package com.android.internal.telephony;

/**
 * Created by Nithya Pachiyappan on 3/20/2017.
 */

public interface ITelephony {

    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
