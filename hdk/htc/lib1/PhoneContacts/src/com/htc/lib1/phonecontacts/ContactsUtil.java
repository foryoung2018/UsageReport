package com.htc.lib1.phonecontacts;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import com.htc.lib1.phonecontacts.R; 

public class ContactsUtil { 


    /**
     * Get the short representation of phone number type end with colon.
     * A performance version
     * @param context The htccontext. to speed up the performance.
     * @param type The phone number type
     * @return The number type label in short form.
     * @author [HTC_PHONE] at Apr 6, 2010
     */
	public static String getPhoneNumberTypeShortString(Context context, int type)
	{
        String result;
        
        if (context == null) {
            return "";
        }
        int stringId = 0;
        switch (type) {
            case CommonDataKinds.Phone.TYPE_HOME:
                stringId =R.string.phone_type_home_short;
                break;

            case CommonDataKinds.Phone.TYPE_MOBILE:
            case CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                stringId = R.string.phone_type_mobile_short;
                break;

            case CommonDataKinds.Phone.TYPE_WORK:
            case CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                stringId = R.string.phone_type_work_short;
                break;

            case CommonDataKinds.Phone.TYPE_FAX_HOME:
            case CommonDataKinds.Phone.TYPE_FAX_WORK:
            case CommonDataKinds.Phone.TYPE_OTHER_FAX:
                stringId = R.string.phone_type_fax_short;
                break;

            case CommonDataKinds.Phone.TYPE_PAGER:
            case CommonDataKinds.Phone.TYPE_WORK_PAGER://                
                stringId = R.string.phone_type_pager_short;
                break;

            case CommonDataKinds.Phone.TYPE_OTHER:
                stringId = R.string.phone_type_other_short;
                break;
            case CommonDataKinds.Phone.TYPE_CALLBACK:
                stringId = R.string.phone_type_callback_short;
                break;
            case CommonDataKinds.Phone.TYPE_CAR:
                stringId = R.string.phone_type_car_short;
                break;
            case CommonDataKinds.Phone.TYPE_ISDN:
                stringId = R.string.phone_type_isdn_short;
                break;
            case CommonDataKinds.Phone.TYPE_MAIN:
                stringId = R.string.phone_type_main_short;
                break;
            case CommonDataKinds.Phone.TYPE_RADIO:
                stringId = R.string.phone_type_radio_short;
                break;
            case CommonDataKinds.Phone.TYPE_TELEX:
                stringId = R.string.phone_type_telex_short;
                break;
            case CommonDataKinds.Phone.TYPE_TTY_TDD:
                stringId = R.string.phone_type_tty_tdd_short;
                break;
            case CommonDataKinds.Phone.TYPE_ASSISTANT:
                stringId = R.string.phone_type_assistant_short;
                break;
            case CommonDataKinds.Phone.TYPE_MMS:
                stringId = R.string.phone_type_mms_short;
                break;

            case CommonDataKinds.Phone.TYPE_CUSTOM:
                stringId = R.string.phone_type_custom_short;
                break;

            default:
                break;
        }
        if(stringId > 0) {
            result = context.getResources().getString(stringId);
        }
        else {
            result = "";
        }
        return result;
	}
	
    public static Intent createUnknownContactIntent(String address) {
        Intent intent = new Intent();
        intent.setData(Uri.withAppendedPath(  Uri.withAppendedPath(Uri.parse("content://" + ContactsContract.AUTHORITY), "unknown") , address));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("number", address);
        intent.putExtra("DefaultTab", "PEOPLE_DETAIL_CALL_HISTORY");
        return intent;
    }


}
