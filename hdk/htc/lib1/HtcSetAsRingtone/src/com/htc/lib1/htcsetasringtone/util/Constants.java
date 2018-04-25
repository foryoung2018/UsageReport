package com.htc.lib1.htcsetasringtone.util;

public class Constants
{
    // Time Related Constants :
    public static final long MINIMUM_TRIM_TIME = 3000;
    
    public static final String MODE_CDMA = "mode_cdma";
    public static final String MODE_GSM = "mode_gsm";
    public static final String MODE_WCDMA = "mode_wcdma";
    
    public static final int SLOT_TYPE_CDMA = 0;
    public static final int SLOT_TYPE_GSM = 1;
    public static final int SLOT_TYPE_BOTH = 2;
    
    public static final String INTENT_PICK_CONTACT = "com.htc.contacts.PICK_CONTACT_MSG";
    public static final String INTENT_PICK_MULIT_CONTACT = "com.htc.contacts.ACTION_PICK_MULTIPLE";
    
    public static final int SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_PEOPLE_AND_RINGTONTRIMMER_CAN_TRIM = 4;
    public static final int SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_PEOPLE_BUT_NO_RINGTONTRIMMER_OR_CANT_TRIM = 3;
    public static final int SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_RINGTONTRIMMER_AND_CAN_TRIM_BUT_NO_PEOPLE = 3;
    public static final int SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_NO_PEOPLE_AND_NO_RINGTONTRIMMER_OR_CANT_TRIM = 2;
    
    public static final int SET_AS_RINGTONE_DIALOG_INFO = 0;
    public static final int SET_AS_RINGTONE_DIALOG_PHONE_RINGTONE = 1;
    public static final int SET_AS_RINGTONE_DIALOG_CONTACT_RINGTONE = 2;
    public static final int SET_AS_RINGTONE_DIALOG_TRIM_THE_RINGTONE = 3;
}