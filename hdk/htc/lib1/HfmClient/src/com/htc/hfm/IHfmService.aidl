package com.htc.hfm;

import com.htc.hfm.IHfmServiceCallback;
import com.htc.hfm.Speech;
import android.os.Bundle;

/** {@hide} */
interface IHfmService {

    int reserveService(String sessionId, String actName, in Bundle appInfo,
        int timeout, int priority);

    int releaseService(String sessionId);

    int cancelReservation(String sessionId);

    void speak(String sessionId, in Speech[] speeches, boolean enableFlipAbort);

    void selectCommand(String sessionId, in Speech[] question, in Speech[] commands, boolean enableFlipAbort);

    void abort(String sessionId);

    void startWakeUpMode(String sessionId, in Speech command, int mode);
    
    void stopWakeUpMode(String sessionId);

    void testWakeUpPhrase(String sessionId, String phrase);

    float getAudioPower();

    String connect(String pkgName, IHfmServiceCallback cb);

    void disconnect(String sessionId);

    void resetTimeout(String sessionId);

    void setNotificationSoundEnabled(String sessionId, boolean enabled);

    void setDefaultRetryEnabled(String sessionId, boolean enabled);

    void setConfidenceLevel(String sessionId, int confidence);

    void selectWakeupCommand(String sessionId, in Speech[] commands);
    
    void setDefaultBluetoothScoEnabled(String sessionId, boolean enabled);
}
