package com.htc.hfm;

import com.htc.hfm.IHfmServiceCallback;
import com.htc.hfm.Speech;
import android.os.Bundle;

/** {@hide} */
interface IHfmServiceHMS {

    int reserveService(String sessionId, String actName, in Bundle appInfo,
        int timeout, int priority); //Level 0

    int releaseService(String sessionId); //Level 0

    int cancelReservation(String sessionId); //Level 0

    void speak(String sessionId, in Speech[] speeches, boolean enableFlipAbort); //Level 0

    void selectCommand(String sessionId, in Speech[] question, in Speech[] commands, boolean enableFlipAbort); //Level 0

    void abort(String sessionId); //Level 0

    void startWakeUpMode(String sessionId, in Speech command, int mode); //Level 0
    
    void stopWakeUpMode(String sessionId); //Level 0

    void testWakeUpPhrase(String sessionId, String phrase); //Level 0

    float getAudioPower(); //Level 0

    String connect(String pkgName, IHfmServiceCallback cb); //Level 0

    void disconnect(String sessionId); //Level 0

    void resetTimeout(String sessionId); //Level 0

    void setNotificationSoundEnabled(String sessionId, boolean enabled); //Level 0

    void setDefaultRetryEnabled(String sessionId, boolean enabled); //Level 0

    void setConfidenceLevel(String sessionId, int confidence); //Level 0

    void selectWakeupCommand(String sessionId, in Speech[] commands); //Level 0
    
    void setDefaultBluetoothScoEnabled(String sessionId, boolean enabled); //Level 1

    void setCheckVersionEnabled(String sessionId, boolean enabled); //Level 2
}
