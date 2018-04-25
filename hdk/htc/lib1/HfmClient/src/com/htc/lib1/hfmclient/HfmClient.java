package com.htc.lib1.hfmclient;

import java.lang.reflect.Method;
import java.util.Locale;

import android.Manifest;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.htc.hfm.IHfmServiceCallback;
import com.htc.hfm.IHfmServiceHMS;
import com.htc.hfm.Speech;

import dalvik.system.PathClassLoader;

/**
 * This class provides hand-free mode (HFM) speech service. Due to the
 * nature of this service, only one application can use it at a time.
 * Each application must acquire the ownership of this service before
 * using the service. If the service is currently occupied, the
 * application can make a reservation and wait for notice when the
 * service becomes available later. It is to be noted that all methods
 * are asynchronous. An application must wait for the callback event
 * for the actual response.
 *   
 * <p>Below are the steps must be taken in order to use HFM service:</p>
 * 
 * <b>1. Create an HfmClient object {@link #HfmClient(Callback, Context, Bundle, String, String, int, int)}</b>
 * <p>All seven parameters must not be null. First parameter is
 * {@link Callback} object that allows HFM service to notify the
 * application about method completion or error condition. Second
 * parameter is the Context object of the application. Third parameter
 * is application-specific data to be passed back to the application
 * by {@link Intent} when HFM service is ready. Fourth parameter is to
 * uniquely identify the application for HFM service to arrange
 * ownership. Fifth parameter is to construct an Intent to notify the
 * application for service ready later if the service is occupied at
 * the moment. Sixth parameter is timeout in milliseconds for
 * protection purpose. If an application does not perform any
 * operation after acquiring the ownership, after the specified
 * timeout passes, the ownership is ceased automatically. The last
 * parameter is to specify the priority of application. Applications
 * with higher priority can obtain service ownership immediately from
 * applications with lower priority.</p>
 * 
 * <b>2. Call {@link #reserveService()}.</b>
 * <p>Inform HFM service to acquire the service ownership. If the
 * service is immediately available, {@link #SUCCESS_SERVICE_READY}
 * will be passed to {@link Callback#onReserveServiceComplete(int)},
 * and the application can proceed to 3. using the service.
 * Otherwise, {@link #SUCCESS_SERVICE_NOT_READY} will be passed, and
 * the application may proceed to 5. HFM service will send an Intent
 * to the application, using the actionName given in constructor, with
 * {@link Bundle} appInfo, when the service is available later. If the
 * application still holds an instance of HfmClient when HFM service
 * becomes available, {@link Callback#onReserveServiceComplete(int)}
 * will be called with {@link #SUCCESS_SERVICE_READY} status, instead
 * of sending intent. To be noted that the timeout is started when the
 * ownership is acquire.</p>
 *
 * <b>3. If HFM service ownership is acquired, speak(), 
 * selectCommand(), abort() can be used.</b>
 * <p><code>abort()</code> can abort <code>speak()</code> and <code>
 * selectCommand()</code> if they are currently running. Calls to any
 * of them will reset the timeout.
 *
 * <b>4. Call {@link #releaseService()}</b>
 * <p>If an application finishes with HFM service, it can call
 * <code>releaseService()</code> to give away the ownership and allow
 * other applications to proceed. HFM service will cease ownership
 * when timeout event fires, even if this method is not called.</p>
 * 
 * <b>5. Call {@link #close()}</b>
 * <p>Clean up the resource before exiting <code>HfmClient</code>.</p>
 * 
 * <p>There are a few important features worth mentioning again:</p>
 *
 * <ul>
 *
 * <li>All methods are asynchronous.
 *
 * <li>Calls to {@link #reserveService()} only makes a reservation to
 * HFM service. The service may not be available immediately.
 *
 * <li>Calls to {@link #releaseService()} will only cease 
 * application's ownership towards the service. It will not remove the
 * application's reservation request in the queue. Call
 * {@link #cancelReservation()} for removing reservation request from
 * the queue.
 *
 * <li>Do not call {@link #releaseService()} if an application still
 * wants to use HFM service. For example, when switch from one
 * Activity to another, or switch from Activity to Service. Call
 * {@link #close()} if the application finishes using the
 * {@link HfmClient} object.
 *
 * <li>If the application does not perform any operation for the
 * duration specified as timeout in the constructor parameter,
 * {@link HfmClient.Callback#onTimeout()} will be called, and HFM
 * service will be dedicated to next application in queue.
 *
 * </ul>
 * 
 * {@exthide}
 */
public class HfmClient {

    // Status codes
    /** Status code for successful method call. */
    public static final int SUCCESS = 0;
    /** 
     * Status code for successful HFM service reservation and the
     * service is ready for use.
     */
    public static final int SUCCESS_SERVICE_READY = 1;
    /**
     * Status code for successful HFM service reservation but the
     * service is held by another application.
     */
    public static final int SUCCESS_SERVICE_NOT_READY = 2;
    /** Status code for reservation that needs callback confirmation */
    public static final int SUCCESS_WAIT_FOR_CALLBACK = 3;
    /**
     * Status code for user aborting action by pressing power key,
     * flipping device, etc.
     */
    public static final int SUCCESS_USER_ABORT = 4;
    /** Status code for phrase accepted. */
    public static final int SUCCESS_PHRASE_ACCEPTED = 5;

    /** Status code for client busy. */
    public static final int ERROR_CLIENT_BUSY = -1;
    /** Status code for HFM service not enabled. */
    public static final int ERROR_HFM_NOT_ENABLED = -2;
    /**  Status code for HfmClient not connected to the HFM service. */
    public static final int ERROR_CONNECT_FAILED = -3;
    /** Status code when RemoteException occurred. */
    public static final int ERROR_REMOTE_EXCEPTION_OCCURRED = -4;
    /** Status code for including audio commands. */
    public static final int ERROR_AUDIO_COMMAND_NOT_SUPPORTED = -5;
    /** Status code for unknown session ID. */
    public static final int ERROR_UNKNOWN_SESSION_ID = -6;
    /** Status code for application already in reservation queue. */
    public static final int ERROR_RESERVE_FAILED_DUPLICATE_RESERVATION = -11;
    /** Status code for using HFM service without reservation. */
    public static final int ERROR_RESERVE_NOT_CALLED = -21;
    /** Status code for using HFM service held by another application. */
    public static final int ERROR_SERVICE_ACQUIRED_BY_OTHER = -22;
    /** Status code for HFM service not finishing current operation. */
    public static final int ERROR_SERVICE_BUSY = -23;
    /** Status code for unknown error from underlying application. */
    public static final int ERROR_NUANCE_CLIENT_ERROR = -31;
    /** Status code for action being aborted. */
    public static final int ERROR_ABORTED = -41;
    /** Status code for not able to identify the command from speech. */
    public static final int ERROR_CANNOT_IDENTIFY_COMMAND = -51;
    /** Status code for timeout when selecting command. */
    public static final int ERROR_SELECT_COMMAND_TIMEOUT = -52;
    /** Status Code for phrase accepted */
    public static final int ERROR_PHRASE_NOT_ACCEPTED = -61;
    /** Status Code for unsupported API */
    public static final int ERROR_UNSUPPORTED_API = -71;
    //{ Simon_Wu (Check dangerous permissions for Android M) start
    /** Status Code for permission need request*/
    public static final int ERROR_PERMISSION_NEED_REQUEST = -72;
    /** Status Code for permission cannot not be granted*/
    public static final int ERROR_PERMISSION_GRANT_FAIL = -73;
    /** Status Code for reserve mode is invalid*/
    public static final int ERROR_RESERVE_MODE_INVALID = -74;
    //} Simon_Wu (Check dangerous permissions for Android M) end
    
    /** Checking the given locale is supported */
    public static final int RESULT_SUPPORT = 0;
    /** Checking the given locale is supported but not installed */
    public static final int RESULT_NOT_INSTALL = 1;
    /** Checking the given locale is not supported */
    public static final int RESULT_NOT_SUPPORT = 2;
    //SelyLan 20141118 begin
    /** Checking the given locale is supported but need update new version */
    public static final int RESULT_GOT_NEW_VERSION = 3;
    /** Checking the given locale is supported and new version is downloading */
    public static final int RESULT_LANGPACK_DOWNLOADING = 4;
    //SelyLan 20141118 end
    //{ Simon_Wu (Check dangerous permissions for Android M) begin
    /** Checking the given locale is supported but storage permission needs request*/
    public static final int RESULT_SUPPORT_WITHOUT_PERMISSION = 5;
    //} Simon_Wu (Check dangerous permissions for Android M) end

    /** Checking the given locale input the wrong argument */
    public static final int RESULT_WRONG_ARGUMENT = -1;
    /** Checking the given locale can not find the engine */
    public static final int RESULT_NOT_INSTALL_ENGINE = -2;
    
    /** Level 1 priority. (Highest) */
    public static final int PRIORITY_LEVEL_1 = 1001;
    /** Level 2 priority. */
    public static final int PRIORITY_LEVEL_2 = 1002;
    /** Level 3 priority. */
    public static final int PRIORITY_LEVEL_3 = 1003;
    /** Level 4 priority. */
    public static final int PRIORITY_LEVEL_4 = 1004;
    /** Level 5 priority. */
    public static final int PRIORITY_LEVEL_5 = 1005;
    /** Level 6 priority. */
    public static final int PRIORITY_LEVEL_6 = 1006;
    /** Level 7 priority. */
    public static final int PRIORITY_LEVEL_7 = 1007;
    /** Level 8 priority. */
    public static final int PRIORITY_LEVEL_8 = 1008;
    /** Level 9 priority. */
    public static final int PRIORITY_LEVEL_9 = 1009;
    /** Level 10 priority. (Lowest so far) */
    public static final int PRIORITY_LEVEL_10 = 1010;

    private static class Action {
        private int actionCode;
        private Speech arg0;
        private Speech[] arg1;
        private Speech[] arg2;
        private boolean arg3;
        private int arg4;
        private String arg5;
        private boolean arg6;
        private Action(int actionCode) {
            this(actionCode, null, null, null, false, -1);
        }
        private Action(int actionCode, String arg5) {
            Action.this.actionCode = actionCode;
            Action.this.arg5 = arg5;
        }
        private Action(int actionCode, boolean arg3, boolean arg6) {
            Action.this.actionCode = actionCode;
            Action.this.arg3 = arg3;
            Action.this.arg6 = arg6;
        }
        private Action(int actionCode, Speech arg0, int arg4) {
            this(actionCode, arg0, null, null, false, arg4);
        }
        private Action(int actionCode, Speech[] arg1, boolean arg3) {
            this(actionCode, null, arg1, null, arg3, -1);
        }
        private Action(int actionCode, Speech arg0, Speech[] arg1, boolean arg3) {
            this(actionCode, arg0, arg1, null, arg3, -1);
        }
        private Action(int actionCode, Speech[] arg1, Speech[] arg2, boolean arg3) {
            this(actionCode, null, arg1, arg2, arg3, -1);
        }
        private Action(int actionCode, Speech arg0, Speech[] arg1, Speech[] arg2, boolean arg3, int arg4) {
            Action.this.actionCode = actionCode;
            Action.this.arg0 = arg0;
            Action.this.arg1 = arg1;
            Action.this.arg2 = arg2;
            Action.this.arg3 = arg3;
            Action.this.arg4 = arg4;
        }
    }

    private static final String TAG = HfmClient.class.getSimpleName();
    
    private static final String HTCSPEAK_PACKAGE_NAME = "com.htc.HTCSpeaker";
    private static final String HTCSPEAK_HFM_PACKAGE_NAME = "com.htc.hfm";
    private static final String IHFMSERVICEHMS_METADATA_KEY_API_LEVEL = "com.htc.hfm.HfmService.IHfmServiceHMSApiLevel";
    private int mIHfmServiceHMSApiLevel = 0;
    
    private static final String ACTION_HFM_SERVICE_HMS = "com.htc.hfm.HfmService.HMS";
    private static final String CATEGORY_HFM_SERVICE = "com.htc.hfm";
    private static final String PRIORITY_LEVEL_2_PACKAGE_NAME = "com.htc.htcspeak";

    private static final int ACTION_RESERVE_SERVICE = 2001;
    private static final int ACTION_RELEASE_SERVICE = 2002;
    private static final int ACTION_SPEAK = 2003;
    private static final int ACTION_SELECT_COMMAND = 2004;
    private static final int ACTION_ABORT = 2005;
    private static final int ACTION_CANCEL_RESERVATION = 2006;
//    private static final int ACTION_START_WAKEUP_MODE = 2007;
//    private static final int ACTION_STOP_WAKEUP_MODE = 2008;
//    private static final int ACTION_TEST_WAKEUP_PHRASE = 2009;
    private static final int ACTION_RESET_TIMEOUT = 2010;
    private static final int ACTION_SET_NOTIFICAION_SOUND_ENABLED = 2011;
    private static final int ACTION_SET_DEFAULT_RETRY_ENABLED = 2012;
    private static final int ACTION_SET_CONFIDENCE_LEVEL = 2013;
    private static final int ACTION_SELECT_WAKEUP_COMMAND = 2014;

    private static final String AUTOMOTIVE_ACTION_MODE_CHANGE = "com.htc.AutoMotive.Service.ModeChange";
    private static final String AUTOMOTIVE_CURRENT_MODE = "AutoMotive_Current_Mode";
    
    private static final int AUTOMOTIVE_ENABLED = 0;
    private static final int AUTOMOTIVE_DISABLED = 1;
    
    private static PathClassLoader mPathClassLoader = null; // 2013/10/14 Simon_Wu (PathClassLoader keeps one instance)
    private static PathClassLoader mIHFMServiceHMSClassLoader = null;
    
    private boolean mbCheckCarmode = true;

    /**
     * Interface that must be registered to HfmClient.
     */
    private interface ICallback {
        void onReserveServiceComplete(int statusCode);
        void onReleaseServiceComplete(int statusCode);
        void onSpeakComplete(int statusCode);
        void onSelectCommandComplete(int statusCode, String command);
        void onAbortComplete(int statusCode);
        void onCancelReservationComplete(int statusCode);
        void onTimeout();
        void onInterrupt();
        void onHfmShutdown();
        void onWakeUpModeComplete(int statusCode);
        void onTestWakeUpPhraseComplete(int statusCode);
        void onStartRecording();
        void onStopRecording();
    }

    /**
     * This class defines interface methods that must be registered in
     * HfmClient.
     */
    public static class Callback implements ICallback {
        /**
         * Invokes when {@link #reserveService()} finishes.
         * @param statusCode {@link #SUCCESS_SERVICE_READY},
         *                   {@link #SUCCESS_SERVICE_NOT_READY},
         *                   {@link #ERROR_RESERVE_FAILED_DUPLICATE_RESERVATION},
         */
        @Override
        public void onReserveServiceComplete(int statusCode) { }
        /**
         * Invokes when {@link HfmClient#releaseService()} finishes.
         * @param statusCode {@link #SUCCESS},
         */
        @Override
        public void onReleaseServiceComplete(int statusCode) { }
        /**
         * Invokes when speak() finishes.
         * @param statusCode {@link #SUCCESS},
         */
        @Override
        public void onSpeakComplete(int statusCode) { }
        /**
         * Invokes when selectCommand() finishes.
         * @param statusCode {@link #SUCCESS},
         *                   {@link #ERROR_AUDIO_COMMAND_NOT_SUPPORTED},
         *                   {@link #ERROR_SELECT_COMMAND_TIMEOUT},
         *                   {@link #ERROR_CANNOT_IDENTIFY_COMMAND},
         * @param command the command identified when statusCode is {@link #SUCCESS}
         */
        @Override
        public void onSelectCommandComplete(int statusCode, String command) { }
        /**
         * Invokes when abort() finishes.
         * @param statusCode {@link #SUCCESS},
         *                   {@link #SUCCESS_USER_ABORT}
         */
        @Override
        public void onAbortComplete(int statusCode) { }
        /**
         * Invokes when {@link #cancelReservation()} finishes.
         * @param statusCode {@link #SUCCESS},
         */
        @Override
        public void onCancelReservationComplete(int statusCode) { }
        /**
         * Invokes when the service ownership is ceased due to timeout.
         */
        @Override
        public void onTimeout() { }
        /**
         * Invokes when high priority application intercepts the HFM
         * service.
         */
        @Override
        public void onInterrupt() { }
        /**
         * Invokes when HFM service is switched off by user.
         */
        @Override
        public void onHfmShutdown() { }
        /**
         * Invokes when wake-up mode gets the result.
         * @param statusCode {@link #SUCCESS},
         *                   {@link #ERROR_ABORTED}
         */
        @Override
        public void onWakeUpModeComplete(int statusCode) { }
        /**
         * Invokes when wake-up mode phrase test result returns.
         * @param statusCode {@link #SUCCESS_PHRASE_ACCEPTED},
         *                   {@link #ERROR_PHRASE_NOT_ACCEPTED}
         */
        @Override
        public void onTestWakeUpPhraseComplete(int statusCode) { }
        /**
         * Invokes when the engine starts recording user voice.
         */
        @Override
        public void onStartRecording() { }
        /**
         * Invokes when the engine stops recording user voice.
         */
        @Override
        public void onStopRecording() { }
    }

    private Callback mCallback;
    private Context mContext;
    private Bundle mAppInfo;
    private String mPackageName;
    private String mActionName;
    private int mTimeout;
    private int mPriority;

    //{ Simon_Wu (Check dangerous permissions for Android M) begin
    /** reserve mode to get the dangerous permissions of HFM functions*/
    public static final int RESERVE_MODE_HFM = 0;
    /** reserve mode to get the dangerous permissions of Phone functions*/
    public static final int RESERVE_MODE_PHONE = 1;
    /** reserve mode to get the dangerous permissions of Speak functions*/
    public static final int RESERVE_MODE_SPEAK = 2;

    private final String[] DANGEROUS_PERMISSION_LIST_HFM = new String[]
    {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private final String[] DANGEROUS_PERMISSION_LIST_PHONE = new String[]
    {
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG
    };

    private final String[] DANGEROUS_PERMISSION_LIST_SPEAK = new String[]
    {
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ACCESS_FINE_LOCATION
    };
    //} Simon_Wu (Check dangerous permissions for Android M) end

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: name=" + name);
            mCurrentAction = null;
            mSessionId = null;
            mIsBound = false;
            mIHfmService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder)
        {
            Log.d(TAG, "onServiceConnected: name=" + name);
            mIHfmService = IHfmServiceHMS.Stub.asInterface(binder);
            
            if (mIHfmService != null)
            {
                mIsBound = true;
                next();
            }
            else
            {
                Log.e(TAG, "IHfmService is null");
            }
        }
    };
    private IHfmServiceHMS mIHfmService;
    private IHfmServiceCallback mIHfmServiceCallback = new IHfmServiceCallback.Stub() {
        @Override
        public void onReserveServiceComplete(int statusCode)
                throws RemoteException {
            mCallback.onReserveServiceComplete(statusCode);
        }
        @Override
        public void onSpeakComplete(int statusCode) throws RemoteException {
            mCallback.onSpeakComplete(statusCode);
        }
        @Override
        public void onSelectCommandComplete(int statusCode, String command)
                throws RemoteException {
            mCallback.onSelectCommandComplete(statusCode, command);
        }
        @Override
        public void onAbortComplete(int statusCode) throws RemoteException {
            mCallback.onAbortComplete(statusCode);
        }
        @Override
        public void onTimeout() throws RemoteException {
            mCallback.onTimeout();
        }
        @Override
        public void onInterrupt() throws RemoteException {
            mCallback.onInterrupt();
        }
        @Override
        public void onHfmShutdown() throws RemoteException {
            mSessionId = null;
            mCallback.onHfmShutdown();
        }
        @Override
        public void onWakeUpModeComplete(int statusCode)
                throws RemoteException {
            mCallback.onWakeUpModeComplete(statusCode);
        }
        @Override
        public void onTestWakeUpPhraseComplete(int statusCode) {
            mCallback.onTestWakeUpPhraseComplete(statusCode);
        }
        @Override
        public void onStartRecording() throws RemoteException {
            mCallback.onStartRecording();
        }
        @Override
        public void onStopRecording() throws RemoteException {
            mCallback.onStopRecording();
        }
    };
    private String mSessionId;
    private boolean mIsBound;
    private Action mCurrentAction;

    /**
     * Constructs an instance.
     * @param callback callback methods to receives responses
     * @param context context for bindService and getResources
     * @param appInfo application-specific information
     * @param packageName package name of the application
     * @param actionName action name for HFM service to send Intent
     *                   if it becomes available later
     * @param timeout maximum duration for HFM service to wait for
     *                application response, and 0 for no timeout
     * @param priority priority of the application
     */
    public HfmClient(Callback callback, Context context, Bundle appInfo,
            String packageName, String actionName, int timeout, int priority) {
        this(callback, context, appInfo, packageName, actionName, timeout, priority, true);
    }

    /**
     * Constructs an instance.
     * @param callback callback methods to receives responses
     * @param context context for bindService and getResources
     * @param appInfo application-specific information
     * @param packageName package name of the application
     * @param actionName action name for HFM service to send Intent
     *                   if it becomes available later
     * @param timeout maximum duration for HFM service to wait for
     *                application response, and 0 for no timeout
     * @param priority priority of the application
     * @param bCheckCarmode check inside the Car mode or not
     */
    public HfmClient(Callback callback, Context context, Bundle appInfo,
            String packageName, String actionName, int timeout, int priority, boolean bCheckCarmode) {
        if (callback == null) {
            throw new NullPointerException("callback cannot be null");
        }
        mCallback = callback;

        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        mAppInfo = appInfo;
        mContext = context;

        if (packageName == null) {
            throw new NullPointerException("packageName cannot be null");
        }
        if (priority == PRIORITY_LEVEL_2) {
            mPackageName = PRIORITY_LEVEL_2_PACKAGE_NAME;
        } else {
            mPackageName = packageName;
        }

        if (actionName == null) {
            throw new NullPointerException("actionName cannot be null");
        }
        mActionName = actionName;

        if (timeout < 0) {
            mTimeout = 10000;
        } else {
            mTimeout = timeout;
        }

        if (priority >= PRIORITY_LEVEL_1 && priority <= PRIORITY_LEVEL_10) {
            mPriority = priority;
        } else {
            throw new RuntimeException("Unkown priority level: " + priority);
        }
        
        mbCheckCarmode = bCheckCarmode;
        Log.d(TAG, "bCheckCarmode = " + mbCheckCarmode);

        mIHfmServiceHMSApiLevel = getIHFMServiceHMSApiLevel(mContext);
        Log.d(TAG, "HfmServiceHMS API Level = " + mIHfmServiceHMSApiLevel);
    }

    /**
     * Verifies if HFM service is available on the device.
     * @return true if HFM service is available
     */
    public synchronized boolean isHandFreeModeEnabled()
    {
        if (mContext == null)
        {
            Log.e(TAG, "isHandFreeModeEnabled: context is null");
            return false;
        }
        
        if (!isPackageInstalled(HTCSPEAK_PACKAGE_NAME))
        {
            Log.e(TAG, "isHandFreeModeEnabled: HtcSpeaker does not install");
            return false;
        }
//        if (!isPackageInstalled(HTCSPEAK_HFM_PACKAGE_NAME)) //For HMS APK
//        {
//            Log.e(TAG, "isHandFreeModeEnabled: HtcHFM does not install");
//            return false;
//        }
        if (!isSupportedSystemLocale(mContext))
        {
            Log.e(TAG, "isHandFreeModeEnabled: No supported language");
            return false;
        }
        
        UiModeManager uimm = (UiModeManager)mContext.getSystemService(Context.UI_MODE_SERVICE);
        int modeType = uimm.getCurrentModeType();
        String modeTypeString = "UNKNOWN_UI_MODE_TYPE";
        switch (modeType)
        {
        case Configuration.UI_MODE_TYPE_CAR:
            modeTypeString = "UI_MODE_TYPE_CAR";
            break;
        case Configuration.UI_MODE_TYPE_DESK:
            modeTypeString = "UI_MODE_TYPE_DESK";
            break;
        case Configuration.UI_MODE_TYPE_NORMAL:
            modeTypeString = "UI_MODE_TYPE_NORMAL";
            break;
        case Configuration.UI_MODE_TYPE_TELEVISION:
            modeTypeString = "UI_MODE_TYPE_TELEVISION";
            break;
        }
        Log.d(TAG, "isHandFreeModeEnabled: UI mode type = " + modeTypeString);
        
        boolean ret =  isTTSEnabled();
        Log.d(TAG, "isHandFreeModeEnabled: TTS is enabled = " + ret);
        
        if (ret)
        {
            if (mPackageName.equals("com.andorid.phone") || mPackageName.equals("com.htc.sense.mms"))
            {
                int isSupportedEx = isSupportedLocaleEx(mContext, Locale.getDefault());
                Log.d(TAG, "isHandFreeModeEnabled: isSupportedEx = " + isSupportedEx);
                ret = (isSupportedEx == RESULT_SUPPORT) ? true : false;
            }
        }
        
        return ret;
    }

    private synchronized boolean isPackageInstalled(String strPackageName)
    {
        boolean bPackageInstalled = true;
        try
        {
            mContext.getPackageManager().getApplicationInfo(strPackageName, PackageManager.GET_META_DATA);
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
            bPackageInstalled = false;
        }
        return bPackageInstalled;
    }

    private synchronized boolean isCarMode()
    {
        Log.d(TAG, "isCarMode+++: bCheckCarmode = " + mbCheckCarmode);
        if (!mbCheckCarmode)
        {
            return true;
        }

        boolean ret = false;
        IntentFilter filter = new IntentFilter(AUTOMOTIVE_ACTION_MODE_CHANGE);
        Intent intent = mContext.registerReceiver(null, filter);
        if (intent != null)
        {
            int mode = intent.getIntExtra(AUTOMOTIVE_CURRENT_MODE, -1);
            if (mode == AUTOMOTIVE_ENABLED)
            {
                ret = true;
            }
        }
        return ret;
    }

    private synchronized boolean isTTSEnabled()
    {
        boolean ret = true;
        
        Log.d(TAG, "isTTSEnabled: package is " + mPackageName);
        
        if (mPackageName.equals("com.andorid.phone") || mPackageName.equals("com.htc.sense.mms"))
        {
            boolean bIsCarMode = isCarMode();
            Log.d(TAG, "isTTSEnabled: isCarMode = " + bIsCarMode);
            
            if (bIsCarMode)
            {
                int readout = Settings.System.getInt(mContext.getContentResolver(), "htcspeak_readoutnoti", 1);
                Log.d(TAG, "isTTSEnabled: readout = " + readout);
                
                if (readout != 1)
                {
                    ret = false;
                }
            }
            else
            {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Closes this object if it is no longer used. 
     */
    public synchronized void close() {
        Log.d(TAG, "close:");
        try {
            destroySession();
        } catch (RemoteException re) {
            // Ignore
            Log.e(TAG, re.getMessage(), re);
        }
        Log.d(TAG, "close: mIsBound=" + mIsBound);
        try {
            mContext.unbindService(mServiceConnection);
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
        mIsBound = false;
    }

    /**
     * Cancels the reservation for HFM service. Notice that this
     * method only remove the reservation from queue.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int cancelReservation()
    {
        Log.d(TAG, "cancelReservation:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_CANCEL_RESERVATION));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Reserves the HFM service. HFM service may not immediately
     * available. See {@link Callback#onReserveServiceComplete(int)}.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int reserveService()
    {
        Log.d(TAG, "reserveService:");
        return reserveService(true);
    }

    /**
     * Reserves the HFM service. HFM service may not immediately
     * available. See {@link Callback#onReserveServiceComplete(int)}.
     * @param enableDefaultBluetoothSco enable/disable default Bluetooth Sco
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int reserveService(boolean enableDefaultBluetoothSco)
    {
        return reserveService(enableDefaultBluetoothSco, true);
    }

    /**
     * Reserves the HFM service. HFM service may not immediately
     * available. See {@link Callback#onReserveServiceComplete(int)}.
     * @param enableDefaultBluetoothSco enable/disable default Bluetooth Sco
     * @param enableCheckVersion enable/disable check new version by Internet
     * @return SUCCESS if this API is supported,
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     *                  ERROR_PERMISSION_NEED_REQUEST if permission need request.
     *                  ERROR_PERMISSION_GRANT_FAIL if permission cannot not be granted.
     */
    public synchronized int reserveService(boolean enableDefaultBluetoothSco, boolean enableCheckVersion)
    {
        return reserveService(enableDefaultBluetoothSco, true, RESERVE_MODE_HFM);
    }

    //{ Simon_Wu (Check dangerous permissions for Android M) begin
    /**
     * Reserves the HFM service. HFM service may not immediately
     * available. See {@link Callback#onReserveServiceComplete(int)}.
     * @param enableDefaultBluetoothSco enable/disable default Bluetooth Sco
     * @param enableCheckVersion enable/disable check new version by Internet
     * @param reserveMode reserve mode to get the dangerous permissions of HFM/Phone/Speak functions
     * @return SUCCESS if this API is supported,
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     *                  ERROR_PERMISSION_NEED_REQUEST if permission need request.
     *                  ERROR_PERMISSION_GRANT_FAIL if permission cannot not be granted.
     *                  ERROR_RESERVE_MODE_INVALID if reserve mode is invalid.
     */
    public synchronized int reserveService(boolean enableDefaultBluetoothSco, boolean enableCheckVersion, int reserveMode)
    {
        Log.d(TAG, "reserveService: enableDefaultBluetoothSco = " + enableDefaultBluetoothSco + ", enableCheckVersion = " + enableCheckVersion + ", reserveMode = " + reserveMode);

        PermissionManager permissionMgr = new PermissionManager(mContext);

        String[] deniedList = null;
        switch (reserveMode)
        {
        case RESERVE_MODE_HFM:
            deniedList = permissionMgr.getLostPermissions(DANGEROUS_PERMISSION_LIST_HFM);
            break;
        case RESERVE_MODE_PHONE:
            deniedList = permissionMgr.getLostPermissions(DANGEROUS_PERMISSION_LIST_PHONE);
            break;
        case RESERVE_MODE_SPEAK:
            deniedList = permissionMgr.getLostPermissions(DANGEROUS_PERMISSION_LIST_SPEAK);
            break;
        default:
            return ERROR_RESERVE_MODE_INVALID;
        }

        if (deniedList != null && deniedList.length > 0)
        {
            int retGrant = permissionMgr.grantPermissions(deniedList);
            Log.d(TAG, "retGrant = " + retGrant);

            if (retGrant == PermissionManager.RESULT_ERR_PERMISSION_NEED_REQUEST)
            {
                Log.e(TAG, "permission need request");
                return ERROR_PERMISSION_NEED_REQUEST;
            }
            else if (retGrant == PermissionManager.RESULT_ERR_PERMISSION_GRANT_FAIL)
            {
                Log.e(TAG, "permission cannot not be granted");
                return ERROR_PERMISSION_GRANT_FAIL;
            }
        }

        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_RESERVE_SERVICE, enableDefaultBluetoothSco, enableCheckVersion));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Release the HFM service.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int releaseService()
    {
        Log.d(TAG, "releaseService:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_RELEASE_SERVICE));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Performs text-to-speech for the given speeches.
     * @param speeches speeches to be spoken.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int speak(Speech[] speeches)
    {
        Log.d(TAG, "speak:");
        
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            speak(speeches, false);
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Performs text-to-speech for the given speeches.
     * @param speeches speeches to be spoken.
     * @param enableFlipAbort whether to enable the feature that aborts this method when flipping device.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int speak(Speech[] speeches, boolean enableFlipAbort)
    {
        Log.d(TAG, "speak:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SPEAK, speeches, enableFlipAbort));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Selects commands from the given list and question.
     * @param question question to be asked.
     * @param commands possible commands for selection.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int selectCommand(Speech question, Speech[] commands)
    {
        Log.d(TAG, "selectCommand:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            selectCommand(question, commands, true);
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Selects commands from the given list and question.
     * @param question question to be asked.
     * @param commands possible commands for selection.
     * @param enableFlipAbort whether to enable the feature that aborts this method when flipping device.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int selectCommand(Speech question, Speech[] commands, boolean enableFlipAbort)
    {
        Log.d(TAG, "selectCommand:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            Speech[] speeches = new Speech[1];
            speeches[0] = question;
            selectCommand(speeches, commands, enableFlipAbort);
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Selects commands from the given list and question.
     * @param question question to be asked.
     * @param commands possible commands for selection.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int selectCommand(Speech[] question, Speech[] commands)
    {
        Log.d(TAG, "selectCommand:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            selectCommand(question, commands, true);
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Selects commands from the given list and question.
     * @param question question to be asked.
     * @param commands possible commands for selection.
     * @param enableFlipAbort whether to enable the feature that aborts this method when flipping device.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int selectCommand(Speech[] question, Speech[] commands, boolean enableFlipAbort)
    {
        Log.d(TAG, "selectCommand:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SELECT_COMMAND, question, commands, enableFlipAbort));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Aborts the current operation, in particular, either speak()
     * or selectCommand(), if there is any currently running.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int abort()
    {
        Log.d(TAG, "abort:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_ABORT));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Resets the timeout. Call this method only when needed.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int resetTimeout()
    {
        Log.d(TAG, "resetTimeout:");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_RESET_TIMEOUT));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    /**
     * Enable/disable notification sound
     * @param enabled enable or disable.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int setNotificationSoundEnabled(boolean enabled)
    {
        Log.d(TAG, "setNotificationSoundEnabled: " + enabled);
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SET_NOTIFICAION_SOUND_ENABLED, null, null, null, enabled, -1));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }
    
    /**
     * Enable/disable default retry 
     * @param enabled enable or disable.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int setDefaultRetryEnabled(boolean enabled)
    {
        Log.d(TAG, "setDefaultRetryEnabled: " + enabled);
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SET_DEFAULT_RETRY_ENABLED, null, null, null, enabled, -1));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }
    
    /**
     * Set confidence level
     * @param confidenceLevel confidence level is from 0 to 100.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int setConfidenceLevel(int confidenceLevel)
    {
        Log.d(TAG, "setConfidenceLevel: " + confidenceLevel);
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SET_CONFIDENCE_LEVEL, null, null, null, false, confidenceLevel));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }
    
    /**
     * Selects commands from the given list.
     * @param commands possible commands for selection.
     * @return SUCCESS if this API is supported, 
     *                  ERROR_UNSUPPORTED_API if this API is not supported.
     */
    public synchronized int selectCommand(Speech[] commands)
    {
        Log.d(TAG, "selectCommand: wakeup command");
        if (mIHfmServiceHMSApiLevel >= 0)
        {
            next(new Action(ACTION_SELECT_WAKEUP_COMMAND, null, commands, null, false, -1));
            return SUCCESS;
        }
        else
        {
            Log.e(TAG, "API Level is unspported. Level = " + mIHfmServiceHMSApiLevel);
            return ERROR_UNSUPPORTED_API;
        }
    }

    //{ 2013/08/13 Simon_Wu (M7_UL_JB43_SENSE50_MR#1760) begin
    private void next(Action action)
    {
        Log.d(TAG, "next: action=" + actionToString(action.actionCode));
        if (mCurrentAction != null)
        {
            Log.d(TAG, "Client is busy. Handle this action = " + action.actionCode + ", mIsBound = " + mIsBound);
            if (action.actionCode == ACTION_ABORT || action.actionCode == ACTION_RELEASE_SERVICE)
            {
                mCurrentAction = action;
                next();
            }
            else
            {
                reportError(action.actionCode, ERROR_CLIENT_BUSY);
            }
        }
        else if (!isHandFreeModeEnabled())
        {
            Log.d(TAG, "next: mIsBound=" + mIsBound);
            if (mIsBound && (action.actionCode == ACTION_ABORT || action.actionCode == ACTION_RELEASE_SERVICE))
            {
                mCurrentAction = action;
                next();
            }
            else
            {
                reportError(action.actionCode, ERROR_HFM_NOT_ENABLED);
            }
        }
        else
        {
            mCurrentAction = action;
            next();
        }
    }
    //} 2013/08/13 Simon_Wu (M7_UL_JB43_SENSE50_MR#1760) end

    private void _cancelReservation() throws RemoteException {
        int result = mIHfmService.cancelReservation(mSessionId);
        mCallback.onCancelReservationComplete(result);
    }

    private void _reserveService(boolean enableDefaultBluetoothSco, boolean enableCheckVersion) throws RemoteException {
        
        if (mIHfmServiceHMSApiLevel >= 1)
        {
            mIHfmService.setDefaultBluetoothScoEnabled(mSessionId, enableDefaultBluetoothSco);
        }
        else
        {
            Log.e(TAG, "setDefaultBluetoothScoEnabled: API Level should be 1. Level = " + mIHfmServiceHMSApiLevel);
        }
        
        if (mIHfmServiceHMSApiLevel >= 2)
        {
            mIHfmService.setCheckVersionEnabled(mSessionId, enableCheckVersion);
        }
        else
        {
            Log.e(TAG, "setCheckVersionEnabled: API Level should be 2. Level = " + mIHfmServiceHMSApiLevel);
        }

        int result = mIHfmService.reserveService(mSessionId, mActionName,
                mAppInfo, mTimeout, mPriority);
        if (result == SUCCESS_WAIT_FOR_CALLBACK) {
            // do nothing
        } else {
            mCurrentAction = null;
            mCallback.onReserveServiceComplete(result);
        }
    }

    private void _releaseService() throws RemoteException {
        int result = mIHfmService.releaseService(mSessionId);
        mCurrentAction = null;
        mCallback.onReleaseServiceComplete(result);
        if (result == SUCCESS) {
            close();
        }
    }

    private void _speak() throws RemoteException {
        Speech[] speeches = mCurrentAction.arg1;
        convert(speeches);
        mIHfmService.speak(mSessionId, speeches, mCurrentAction.arg3);
    }

    private void _selectCommand() throws RemoteException {
        Speech[] question = mCurrentAction.arg1;
        Speech[] commands = mCurrentAction.arg2;
        for (Speech command : commands) {
            if (command.getSpeechType() == Speech.SPEECH_TYPE_AUDIO_RESOURCE) {
                mCallback.onSelectCommandComplete(
                        ERROR_AUDIO_COMMAND_NOT_SUPPORTED, null);
                return;
            }
        }
        convert(question);
        convert(commands);
        mIHfmService.selectCommand(mSessionId, question, commands, mCurrentAction.arg3);
    }

    private void _selectWakeupCommand(String sessionId, Speech[] commands) throws RemoteException
    {
        mIHfmService.selectWakeupCommand(mSessionId, commands);
    }
    
    private void _abort() throws RemoteException {
        mIHfmService.abort(mSessionId);
    }

//    private void _startWakeUpMode() throws RemoteException {
//        Speech command = mCurrentAction.arg0;
//        convert(command);
//        mIHfmService.startWakeUpMode(mSessionId, command, mCurrentAction.arg4);
//    }

    private boolean bindService() {
        Log.d(TAG, "bindService:");
        
        int nSupportedLocaleEx = isSupportedLocaleEx(mContext, Locale.getDefault());
        Log.d(TAG, "bindService: nSupportedLocaleEx = " + nSupportedLocaleEx);
        if (nSupportedLocaleEx != RESULT_SUPPORT)
        {
            Log.e(TAG, "System language is not supported");
            return false;
        }
        
        Intent intent = new Intent(ACTION_HFM_SERVICE_HMS);
        intent.addCategory(CATEGORY_HFM_SERVICE);
        
        if (isPackageInstalled(HTCSPEAK_HFM_PACKAGE_NAME)) //For L-release: Use explicit intent to start Service
        {
            Log.d(TAG, "HtcSpeak_HFM installed");
            intent.setPackage(HTCSPEAK_HFM_PACKAGE_NAME);
        }
        else
        {
            Log.d(TAG, "HtcSpeak_HFM does not install");
            intent.setPackage(HTCSPEAK_PACKAGE_NAME);
        }
        
        boolean isBound = false;
        try
        {
            isBound = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        catch (Exception e)
        {
            Log.w(TAG, "bindService: Exception");
            e.printStackTrace();
        }
        return isBound;
    }

    private void createSession() throws RemoteException {
        Log.d(TAG, "createSession:");
        mSessionId = mIHfmService.connect(mPackageName, mIHfmServiceCallback);
        next();
    }

    private void destroySession() throws RemoteException {
        Log.d(TAG, "destroySession: sessionId=" + mSessionId);
        if (mSessionId != null) {
            mIHfmService.disconnect(mSessionId);
            mSessionId = null;
        }
    }

    private void convert(Speech[] speeches) {
        for (Speech speech : speeches) {
            convert(speech);
        }
    }

    private void convert(Speech speech) {
        if (speech == null) {
            return;
        }
        try {
            speech.convert(mContext);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private synchronized void next() {
        try {
            _next();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            mCurrentAction = null;
        }
    }

    private void _next() {
        if (mCurrentAction == null) {
            Log.e(TAG, "current action is null");
            return;
        }
        int actionCode = mCurrentAction.actionCode;
        if (mIsBound && mSessionId != null) {
            try {
                doLastStep();
            } catch (RemoteException re) {
                Log.e(TAG, re.getMessage(), re);
                reportError(actionCode, ERROR_REMOTE_EXCEPTION_OCCURRED);
            } finally {
                mCurrentAction = null;
            }
        } else if (! mIsBound) {
            //{ 2013/08/13 Simon_Wu (M7_UL_JB43_SENSE50_MR#1760) begin
            if (actionCode == ACTION_RESERVE_SERVICE)
            {
                boolean isBound = bindService();
                if (!isBound)
                {
                    reportError(actionCode, ERROR_CONNECT_FAILED);
                    mCurrentAction = null;
                }
            }
            else
            {
                reportError(actionCode, ERROR_CONNECT_FAILED);
                mCurrentAction = null;
            }
            //} 2013/08/13 Simon_Wu (M7_UL_JB43_SENSE50_MR#1760) end
        } else if (mSessionId == null) {
            try {
                createSession();
            } catch (RemoteException re) {
                Log.e(TAG, re.getMessage(), re);
                reportError(actionCode, ERROR_REMOTE_EXCEPTION_OCCURRED);
                mCurrentAction = null;
            }
        } else {
            Log.w(TAG, "Should not happen: mIsBound=" + mIsBound + ", mSessionId=" + mSessionId);
        }
    }

    private void doLastStep() throws RemoteException {
        int actionCode = mCurrentAction.actionCode;
        switch (actionCode) {
        case ACTION_RESERVE_SERVICE:
            boolean enableDefaultBluetoothSco = mCurrentAction.arg3;
            boolean enableCheckVersion = mCurrentAction.arg6;
            _reserveService(enableDefaultBluetoothSco, enableCheckVersion);
            break;
        case ACTION_RELEASE_SERVICE:
            _releaseService();
            break;
        case ACTION_CANCEL_RESERVATION:
            _cancelReservation();
            break;
        case ACTION_SPEAK:
            _speak();
            break;
        case ACTION_SELECT_COMMAND:
            _selectCommand();
            break;
        case ACTION_ABORT:
            _abort();
            break;
//        case ACTION_START_WAKEUP_MODE:
//            _startWakeUpMode();
//            break;
//        case ACTION_STOP_WAKEUP_MODE:
//            mIHfmService.stopWakeUpMode(mSessionId);
//            break;
//        case ACTION_TEST_WAKEUP_PHRASE:
//            mIHfmService.testWakeUpPhrase(mSessionId, mCurrentAction.arg5);
//            break;
        case ACTION_RESET_TIMEOUT:
            mIHfmService.resetTimeout(mSessionId);
            break;
        case ACTION_SET_NOTIFICAION_SOUND_ENABLED:
            mIHfmService.setNotificationSoundEnabled(mSessionId, mCurrentAction.arg3);
            break;
        case ACTION_SET_DEFAULT_RETRY_ENABLED:
            mIHfmService.setDefaultRetryEnabled(mSessionId, mCurrentAction.arg3);
            break;
        case ACTION_SET_CONFIDENCE_LEVEL:
            mIHfmService.setConfidenceLevel(mSessionId, mCurrentAction.arg4);
            break;
        case ACTION_SELECT_WAKEUP_COMMAND:
            Speech[] commands = mCurrentAction.arg1;
            convert(commands);
            _selectWakeupCommand(mSessionId, commands);
            break;
        default:
            Log.d(TAG, "unknown action code: " + actionCode);
            break;
        }
    }

    private void reportError(int actionCode, int statusCode) {
        Log.d(TAG, "reportError: actionCode=" + actionToString(actionCode) + ", statusCode=" + statusCode);
        try {
            switch (actionCode) {
            case ACTION_RESERVE_SERVICE:
                mCallback.onReserveServiceComplete(statusCode);
                break;
            case ACTION_RELEASE_SERVICE:
                mCallback.onReleaseServiceComplete(statusCode);
                break;
            case ACTION_CANCEL_RESERVATION:
                mCallback.onCancelReservationComplete(statusCode);
                break;
            case ACTION_SPEAK:
                mCallback.onSpeakComplete(statusCode);
                break;
            case ACTION_SELECT_COMMAND:
                mCallback.onSelectCommandComplete(statusCode, null);
                break;
            case ACTION_ABORT:
                mCallback.onAbortComplete(statusCode);
                break;
//            case ACTION_START_WAKEUP_MODE:
//            case ACTION_STOP_WAKEUP_MODE:
//                mCallback.onWakeUpModeComplete(statusCode);
//                break;
//            case ACTION_TEST_WAKEUP_PHRASE:
//                mCallback.onTestWakeUpPhraseComplete(statusCode);
//                break;
            default:
                Log.d(TAG, "unknown action code: " + actionCode);
                break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static String actionToString(int action) {
        switch (action) {
        case ACTION_RESERVE_SERVICE:
            return "ACTION_RESERVE_SERVICE";
        case ACTION_RELEASE_SERVICE:
            return "ACTION_RELEASE_SERVICE";
        case ACTION_SPEAK:
            return "ACTION_SPEAK";
        case ACTION_SELECT_COMMAND:
            return "ACTION_SELECT_COMMAND";
        case ACTION_ABORT:
            return "ACTION_ABORT";
        case ACTION_CANCEL_RESERVATION:
            return "ACTION_CANCEL_RESERVATION";
//        case ACTION_START_WAKEUP_MODE:
//            return "ACTION_START_WAKEUP_MODE";
//        case ACTION_STOP_WAKEUP_MODE:
//            return "ACTION_STOP_WAKEUP_MODE";
//        case ACTION_TEST_WAKEUP_PHRASE:
//            return "ACTION_TEST_WAKEUP_PHRASE";
        case ACTION_RESET_TIMEOUT:
            return "ACTION_RESET_TIMEOUT";
        case ACTION_SET_NOTIFICAION_SOUND_ENABLED:
            return "ACTION_SET_NOTIFICAION_SOUND_ENABLED";
        case ACTION_SET_DEFAULT_RETRY_ENABLED:
            return "ACTION_SET_DEFAULT_RETRY_ENABLED";
        case ACTION_SET_CONFIDENCE_LEVEL:
            return "ACTION_SET_CONFIDENCE_LEVEL";
        case ACTION_SELECT_WAKEUP_COMMAND:
            return "ACTION_SELECT_WAKEUP_COMMAND";
        default:
            return "UNKNOWN_ACTION";
        }
    }
    
    //{ 2013/03/18 Simon_Wu (Add API of checking the supported language) begin
    private static Class<?> getNGFServiceClass(Context context)
    {
        Log.d(TAG, "getNGFServiceClass");
        String packagePath = HTCSPEAK_PACKAGE_NAME;
        String classPath = "com.htc.HTCSpeaker.NGFService";
        
        try
        {
            String apkName = context.getPackageManager().getApplicationInfo(packagePath, 0).sourceDir;
            //{ 2013/10/14 Simon_Wu (PathClassLoader keeps one instance) begin
            if (mPathClassLoader == null)
            {
                Log.d(TAG, "new PathClassLoader");
                mPathClassLoader = new PathClassLoader(apkName, ClassLoader.getSystemClassLoader());
            }
            Class<?> clazz = Class.forName(classPath, true, mPathClassLoader);
            //} 2013/10/14 Simon_Wu (PathClassLoader keeps one instance) end
            return clazz;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    private int getIHFMServiceHMSApiLevel(Context context)
    {
        Log.d(TAG, "getIHFMServiceHMSApiLevel: +++");
        
        int level = 0;
        
        try
        {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(HTCSPEAK_HFM_PACKAGE_NAME, PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            level = bundle.getInt(IHFMSERVICEHMS_METADATA_KEY_API_LEVEL);
            if (level == 0)
            {
                Log.e(TAG, "Failed to load meta-data from Manifest");
            }
            else if (level < 0)
            {
                Log.e(TAG, "Failed to load meta-data");
                level = 0;
            }
        }
        catch (NameNotFoundException e)
        {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
            level = 0;
        }
        catch (NullPointerException e)
        {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
            level = 0;
        }
        
        if (level == 0)
        {
            Log.e(TAG, "getIHFMServiceHMSApiLevel: load meta-data from HtcSpeak");
            
            try
            {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(HTCSPEAK_PACKAGE_NAME, PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                level = bundle.getInt(IHFMSERVICEHMS_METADATA_KEY_API_LEVEL);
                if (level == 0)
                {
                    Log.e(TAG, "Failed to load meta-data from Manifest");
                }
                else if (level < 0)
                {
                    Log.e(TAG, "Failed to load meta-data");
                    level = 0;
                }
            }
            catch (NameNotFoundException e)
            {
                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                level = 0;
            }
            catch (NullPointerException e)
            {
                Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                level = 0;
            }
        }
        
        Log.d(TAG, "getIHFMServiceHMSApiLevel: Level = " + level);
        return level;
    }
    
    /**
     * Returns integer status code whether the given locale is supported or not.
     * @param context context for Locale information
     * @param locale check the given locale
     * @return RESULT_SUPPORT if the given locale is supported, 
     *         RESULT_NOT_INSTALL if the given locale is supported but not installed,
     *         RESULT_NOT_SUPPORT if the given locale is not supported
     *         RESULT_GOT_NEW_VERSION if the given locale is supported but need update new version
     *         RESULT_LANGPACK_DOWNLOADING if the given locale is supported and new version is downloading
     *         RESULT_SUPPORT_WITHOUT_PERMISSION if the given locale is supported but storage permission needs request 
     *         RESULT_WRONG_ARGUMENT if input argument is wrong
     *         RESULT_NOT_INSTALL_ENGINE if it can not find the engine
     */
    public static int isSupportedLocaleEx(Context context, Locale locale)
    {
        if (context == null || locale == null)
        {
            Log.d(TAG, "isSupportedLocaleEx: argument is wrong");
            return RESULT_WRONG_ARGUMENT;
        }
        
        try
        {
            Class<?> clazz = getNGFServiceClass(context);
            if (clazz == null)
            {
                Log.d(TAG, "Can not find NGFService");
                return RESULT_NOT_INSTALL_ENGINE;
            }
            
            Class<?>[] param = {Context.class, Locale.class};
            Method method = clazz.getMethod("isSupportedLocaleEx", param);
            if (method == null)
            {
                Log.d(TAG, "Can not find isSupportedLocaleEx");
                return RESULT_NOT_INSTALL_ENGINE;
            }
            
            Object[] paramObj = {context, locale};
            int result = (Integer)method.invoke(null, paramObj);
            Log.d(TAG, "retsult = " + result);

            //{ Simon_Wu (Check dangerous permissions for Android M) begin
            PermissionManager permissionMgr = new PermissionManager(context);
            if (!permissionMgr.isStoragePermissionGranted())
            {
                if (result == RESULT_SUPPORT || result == RESULT_NOT_INSTALL || result == RESULT_GOT_NEW_VERSION || result == RESULT_LANGPACK_DOWNLOADING || result == RESULT_SUPPORT_WITHOUT_PERMISSION)
                {
                    Log.w(TAG, "The given locale is supported but storage permission needs request");
                    result = RESULT_SUPPORT_WITHOUT_PERMISSION;
                }
            }
            //} Simon_Wu (Check dangerous permissions for Android M) end

            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return RESULT_NOT_INSTALL_ENGINE;
        }
    }

    /**
     * Redirect app to the Google Play market to download and install the language package
     * @param context context for Locale information
     * @param locale check the given locale
     */
    public static void goToPlayInstallPage(Context context, Locale locale)
    {
        if (context == null || locale == null)
        {
            Log.d(TAG, "goToPlayInstallPage: argument is wrong");
            return;
        }
        
        try
        {
            Class<?> clazz = getNGFServiceClass(context);
            if (clazz == null)
            {
                Log.d(TAG, "Can not find NGFService");
                return;
            }
            
            Class<?>[] param = {Context.class, Locale.class};
            Method method = clazz.getMethod("goToPlayInstallPage", param);
            if (method == null)
            {
                Log.d(TAG, "Can not find goToPlayInstallPage");
                return;
            }
            
            Object[] paramObj = {context, locale};
            method.invoke(null, paramObj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns true or false whether the given locale is supported or not.
     * @param context context for Locale information
     * @param locale check the given locale
     * @return True if the given locale is supported
     */
    public static boolean isSupportedLocale(Context context, Locale locale)
    {
        int ret = isSupportedLocaleEx(context, locale);
        Log.d(TAG, "isSupportedLocale: ret (Ex) = " + ret);
        return (ret == RESULT_SUPPORT || ret == RESULT_NOT_INSTALL || ret == RESULT_GOT_NEW_VERSION || ret == RESULT_LANGPACK_DOWNLOADING || ret == RESULT_SUPPORT_WITHOUT_PERMISSION) ? true : false;
    }
    
    /**
     * Returns true or false whether the system locale is supported or not. Equivalen to isSupportedLocale(Locale.getDefault()).
     * @param context context for Locale information
     * @return True if the system locale is supported
     */
    public static boolean isSupportedSystemLocale(Context context)
    {
        return isSupportedLocale(context, Locale.getDefault());
    }
    //} 2013/03/18 Simon_Wu (Add API of checking the supported language) end
    
    //{ 2013/04/02 Simon_Wu (Add API to convert TTS with control code) begin
    /**
     * Returns the converted TTS that speech out by Spell mode
     * @param context context for finding TTS control code
     * @param sentence the given sentence
     * @return the converted TTS
     */
    public static String convertToSpellString(Context context, String sentence)
    {
        if (context == null)
        {
            Log.e(TAG, "context is null");
            return sentence;
        }
        
        try
        {
            Class<?> clazz = getNGFServiceClass(context);
            if (clazz == null)
            {
                Log.e(TAG, "Can not find NGFService");
                return sentence;
            }
            
            Class<?>[] param = {String.class};
            Method method = clazz.getMethod("convertToSpellString", param);
            if (method == null)
            {
                Log.e(TAG, "Can not find convertToSpellMode");
                return sentence;
            }
            
            Object[] paramObj = {sentence};
            return (String)method.invoke(null, paramObj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return sentence;
        }
    }
    
    /**
     * Returns the converted TTS that delay given milliseconds then speech out
     * @param context context for finding TTS control code
     * @param sentence the given sentence
     * @param milliseconds the given delay time
     * @return the converted TTS
     */
    public static String convertToDelayString(Context context, String sentence, int milliseconds)
    {
        if (context == null)
        {
            Log.e(TAG, "context is null");
            return sentence;
        }
        
        try
        {
            Class<?> clazz = getNGFServiceClass(context);
            if (clazz == null)
            {
                Log.e(TAG, "Can not find NGFService");
                return sentence;
            }
            
            Class<?>[] param = {String.class, Integer.class};
            Method method = clazz.getMethod("convertToDelayString", param);
            if (method == null)
            {
                Log.e(TAG, "Can not find convertToDelayMode");
                return sentence;
            }
            
            Object[] paramObj = {sentence, milliseconds};
            return (String)method.invoke(null, paramObj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return sentence;
        }
    }
    //} 2013/04/02 Simon_Wu (Add API to convert TTS with control code) end
}
