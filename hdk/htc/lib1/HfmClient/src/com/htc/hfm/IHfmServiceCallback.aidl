package com.htc.hfm;

/** {@hide} */
interface IHfmServiceCallback {

    void onReserveServiceComplete(int statusCode);
    void onSpeakComplete(int statusCode);
    void onSelectCommandComplete(int statusCode, String command);
    void onAbortComplete(int statusCode);
    void onTimeout();
    void onInterrupt();
    void onHfmShutdown();
    void onWakeUpModeComplete(int statusCode);
    void onTestWakeUpPhraseComplete(int statusCode);
    void onStartRecording();
    void onStopRecording();

}
