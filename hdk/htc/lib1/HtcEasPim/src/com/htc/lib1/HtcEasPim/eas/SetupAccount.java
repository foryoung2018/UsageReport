/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * HTC Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 */
package com.htc.lib1.HtcEasPim.eas;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Provide function to access setup email command and parameter for HTC internal app use.
 *
 * @exthide {@exthide}
 */
public class SetupAccount {
    /**
     * Constants
     */
    private static final String TAG = "SetupAccount";

    /**
     * Actions command - ACTION_EAS_MDM_CREATE_ACCOUNT.
     */
    public static final String ACTION_EAS_MDM_CREATE_ACCOUNT = "com.htc.android.mail.eassvc.account.mdm.CREATE";

    /**
     * Actions command - ACTION_EAS_MDM_DELETE_ACCOUNT.
     */
    public static final String ACTION_EAS_MDM_DELETE_ACCOUNT = "com.htc.android.mail.eassvc.account.mdm.DELETE";

    /**
     * Actions command - ACTION_EAS_MDM_GET_DEVICE_ID.
     */
    public static final String ACTION_EAS_MDM_GET_DEVICE_ID = "com.htc.android.mail.eassvc.device.mdm.GET_ID";

    /**
     * Actions command - ACTION_EAS_DELETE_ACCOUNT.
     */
    public static final String ACTION_EAS_DELETE_ACCOUNT = "com.htc.android.mail.eassvc.account.DELETE";

    /**
     * Actions command - ACTION_EAS_DELETE_ACCOUNT.
     */
    public static final String ACTION_EAS_GET_DEVICE_ID = "com.htc.android.mail.eassvc.device.GET_ID";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_ACCOUNT_TYPE.
     */
    private static final String EXTRA_EAS_ACCOUNT_TYPE = "accountType";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_DISPLAY_NAME.
     */
    private static final String EXTRA_EAS_DISPLAY_NAME = "displayName";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_EMAIL_ADDRESS.
     */
    private static final String EXTRA_EAS_EMAIL_ADDRESS = "emailAddr";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SERVER_ADDR.
     */
    private static final String EXTRA_EAS_SERVER_ADDR = "serverAddr";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_DOMAIN.
     */
    private static final String EXTRA_EAS_DOMAIN = "domain";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_USERNAME.
     */
    private static final String EXTRA_EAS_USERNAME = "username";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_PASSWORD.
     */
    private static final String EXTRA_EAS_PASSWORD = "password";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_USE_SSL.
     */
    private static final String EXTRA_EAS_USE_SSL = "useSSL";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SYNC_SCHEDULE.
     */
    private static final String EXTRA_EAS_SYNC_SCHEDULE = "syncSchedule";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SYNC_MAIL.
     */
    private static final String EXTRA_EAS_SYNC_MAIL = "syncMail";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SYNC_CONTACTS.
     */
    private static final String EXTRA_EAS_SYNC_CONTACTS = "syncContacts";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SYNC_CALENDAR.
     */
    private static final String EXTRA_EAS_SYNC_CALENDAR = "syncCalendar";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SYNC_TASKS.
     */
    private static final String EXTRA_EAS_SYNC_TASKS = "syncTasks";

    /**
     * Key Names of EAS Extra Data - EXTRA_EAS_SET_DEFAULT_ACCOUNT.
     */
    private static final String EXTRA_EAS_SET_DEFAULT_ACCOUNT = "setDefaultAccount";


    /**
     * To create EAS account
     * by caller's parameters, if it is not from @see SetupAccount#addEASAccount(Context, String, Bundle)
     *
     * @param context context
     * @param displayName displayName
     * @param emailAddress emailAddress
     * @param serverAddr serverAddr
     * @param domain domain
     * @param uname uname
     * @param password password
     * @param useSSL useSSL
     * @param syncMail syncMail
     * @param syncContacts syncContacts
     * @param syncCalendar syncCalendar
     * @param syncTasks syncTasks
     * @param setDefaultAccount setDefaultAccount
     * @param syncSchedule syncSchedule
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public static void addEASAccount(Context context, 
                                     String displayName, 
                                     String emailAddress,
                                     String serverAddr, 
                                     String domain, 
                                     String uname, 
                                     String password, 
                                     boolean useSSL, 
                                     Boolean syncMail, 
                                     Boolean syncContacts, 
                                     Boolean syncCalendar, 
                                     Boolean syncTasks, 
                                     Boolean setDefaultAccount, 
                                     Integer syncSchedule) {
        addEASAccount(context,
                    ACTION_EAS_MDM_CREATE_ACCOUNT,
                    displayName,
                    emailAddress,
                    serverAddr,
                    domain,
                    uname,
                    password,
                    useSSL,
                    syncMail,
                    syncContacts,
                    syncCalendar,
                    syncTasks,
                    setDefaultAccount,
                    syncSchedule);
    }

    /**
     * To create EAS account by caller's parameters, if it is not from @see SetupAccount#addEASAccount(Context, String, Bundle)
     *
     * @param context context
     * @param action action
     * @param displayName displayName
     * @param emailAddress emailAddress
     * @param serverAddr serverAddr
     * @param domain domain
     * @param uname uname
     * @param password password
     * @param useSSL useSSL
     * @param syncMail syncMail
     * @param syncContacts syncContacts
     * @param syncCalendar syncCalendar
     * @param syncTasks syncTasks
     * @param setDefaultAccount setDefaultAccount
     * @param syncSchedule syncSchedule
     */
    public static void addEASAccount(Context context,
                                     String action, 
                                     String displayName, 
                                     String emailAddress, 
                                     String serverAddr, 
                                     String domain, 
                                     String uname, 
                                     String password,
                                     boolean useSSL, 
                                     Boolean syncMail, 
                                     Boolean syncContacts, 
                                     Boolean syncCalendar, 
                                     Boolean syncTasks, 
                                     Boolean setDefaultAccount, 
                                     Integer syncSchedule) {
        Bundle extras = new Bundle();
        extras.putString(EXTRA_EAS_DISPLAY_NAME, displayName);
        extras.putString(EXTRA_EAS_EMAIL_ADDRESS, emailAddress);
        extras.putString(EXTRA_EAS_SERVER_ADDR, serverAddr);
        extras.putString(EXTRA_EAS_DOMAIN, domain);
        extras.putString(EXTRA_EAS_USERNAME, uname);
        extras.putString(EXTRA_EAS_PASSWORD, password);
        extras.putBoolean(EXTRA_EAS_USE_SSL, useSSL);
        if (syncMail != null) {
            extras.putBoolean(EXTRA_EAS_SYNC_MAIL, syncMail);
        }
        if (syncContacts != null) {
            extras.putBoolean(EXTRA_EAS_SYNC_CONTACTS, syncContacts);
        }
        if (syncCalendar != null) {
            extras.putBoolean(EXTRA_EAS_SYNC_CALENDAR, syncCalendar);
        }
        if (syncTasks != null) {
            extras.putBoolean(EXTRA_EAS_SYNC_TASKS, syncTasks);
        }
        if (setDefaultAccount != null) {
            extras.putBoolean(EXTRA_EAS_SET_DEFAULT_ACCOUNT, setDefaultAccount);
        }
        if (syncSchedule != null) {
            extras.putInt(EXTRA_EAS_SYNC_SCHEDULE, syncSchedule);
        }
        
        addEASAccount(context, action, extras);
    }

    /**
     * Create EAS account by caller's parameters, if it is not from @see SetupAccount#addEASAccount(Context, String, String, String, String, String, 
     *     String, String, boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Integer)
     *
     * @param context context
     * @param action action
     * @param extras extras
     */
    public static void addEASAccount(Context context, String action, Bundle extras) {
        Log.d(TAG, "addEASAccount");
        Intent intent = new Intent(action);
        intent.putExtras(extras);
        intent.setClassName("com.htc.android.mail", "com.htc.android.mail.eassvc.EASAppSvc");
        context.startService(intent);
    }

    /**
     * Delete EAS account, Deprecated.
     *
     * @param context context
     * @param emailAddress emailAddress
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public static void deleteEASAccount(Context context, String emailAddress) {
        deleteEASAccount(context,ACTION_EAS_MDM_DELETE_ACCOUNT,emailAddress);
    }

    /**
     * To delete EAS account.
     *
     * @param context context
     * @param action action
     * @param emailAddress emailAddress
     */
    public static void deleteEASAccount(Context context, String action, String emailAddress) {
        Log.d(TAG, "deleteEASAccount");
        Intent intent = new Intent(action);
        Bundle extras = new Bundle();
        extras.putString(EXTRA_EAS_EMAIL_ADDRESS, emailAddress);
        intent.putExtras(extras);
        intent.setClassName("com.htc.android.mail", "com.htc.android.mail.eassvc.EASAppSvc");
        context.startService(intent);
    }
    
    /**
     * To get Device id, Deprecated
     *
     * @param context context
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public static void getDeviceId(Context context) {
        getDeviceId(context,ACTION_EAS_MDM_GET_DEVICE_ID);
    }

    /**
     * To get Device id.
     *
     * @param context context
     * @param action action
     */
    public static void getDeviceId(Context context, String action) {
        Log.d(TAG, "getDeviceId");
        Intent intent = new Intent(action);
        intent.setClassName("com.htc.android.mail", "com.htc.android.mail.eassvc.EASAppSvc");
        context.startService(intent);
    }
}
