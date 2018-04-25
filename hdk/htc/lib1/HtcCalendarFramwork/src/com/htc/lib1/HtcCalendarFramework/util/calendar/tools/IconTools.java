package com.htc.lib1.HtcCalendarFramework.util.calendar.tools;

import java.util.HashMap;
import java.util.Map;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;

/**
  * Icon Tools Class
  * {@exthide}
  */
public class IconTools {
    private static final String TAG = "IconTools";
    
    /**
      * Define the account name pcsync string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String ACCOUNT_NAME_PCSYNC = "PC Sync";

    /**
      * Define the account name exchange string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String ACCOUNT_NAME_EXCHANGE = "Exchange";

    /**
      * Define the account name Google string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String ACCOUNT_NAME_GOOGLE = "Google";

    /**
      * Define the account name Facebook string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String ACCOUNT_NAME_FACEBOOK = "Facebook";  

    /**
      * Define the account people string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String ACCOUNT_PEOPLE = "HTC_BirthdayEvent";

    /**
      * Define the account name hotmail string
      */
    public static final String ACCOUNT_NAME_HOTMAIL = "Hotmail";

    /**
      * Define the package name pcsc string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String PACKAGE_NAME_PCSC = "com.htc.android.psclient"; 

    /**
      * Define the package name people string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String PACKAGE_NAME_PEOPLE = "com.htc.contacts"; 

    /**
      * Define the package name task string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static final String PACKAGE_NAME_TASK = "com.htc.task"; 

    /**
     * Define the package name Mail string
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */ 
    public static final String PACKAGE_NAME_MAIL = "com.htc.android.mail"; 

    /**
     * Define the package name Family string
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public static final String PACKAGE_NAME_FAMILY = "com.htc.family"; 

    
    private static Map<String, AuthenticatorDescription> mTypeToAuthDescription = null;
    private static HashMap<String, Drawable> mCachedIcon= new HashMap<String, Drawable>();
    
    private static Drawable getIconByPackageName(final PackageManager pm, final String pkgName) {
        Drawable d = null;
        try {
            d = pm.getApplicationIcon(pkgName);
        } catch(NameNotFoundException nnfe) {
            Log.w(TAG, "pkgName:"+pkgName +" icon not found");
        }
        return d;
    }

    /**
      * Create the package context
      * @param context the Context
      * @param packageName The package name
      * @param flags The flag number
      * @return the Context
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static Context createPackageContext(Context context, String packageName, int flags)
            throws PackageManager.NameNotFoundException {
        return context.createPackageContext(packageName, flags);
    }
    
    /**
      * Get the type icon by the account type
      * @param context Contet The Context
      * @param accountType String The account type
      * @return the Drawable
      */
    public static Drawable getTypeIconByAccountType(Context context, String accountType) {
         Log.v(TAG, "getTypeIconByAccountType accountType: " + accountType);
        if (mCachedIcon != null && mCachedIcon.containsKey(accountType)) {
            return (Drawable) mCachedIcon.get(accountType);
        }

        // Only init. one time or other account type add/remove
        if (mTypeToAuthDescription == null) {
            mTypeToAuthDescription = new HashMap<String, AuthenticatorDescription>();
            AuthenticatorDescription[] authDescs;
            authDescs = AccountManager.get(context).getAuthenticatorTypes();
            for (int i = 0; i < authDescs.length; i++) {
                mTypeToAuthDescription.put(authDescs[i].type, authDescs[i]);
            }
        } else {
            AuthenticatorDescription[] currAuthDescs;
            currAuthDescs = AccountManager.get(context).getAuthenticatorTypes();
            if (currAuthDescs.length != mTypeToAuthDescription.size()) {
                // clear first
                mTypeToAuthDescription.clear();

                // refill it
                for (int i = 0; i < currAuthDescs.length; i++) {
                    mTypeToAuthDescription.put(currAuthDescs[i].type, currAuthDescs[i]);
                }
            }
        }

        Drawable icon = null;
        PackageManager pm = context.getPackageManager();
        
        // PCSC dont have SyncAdapter
        if (TextUtils.equals(accountType, HtcExCalendar.getHtcPcSyncAccountType()) && pm != null) {
            // PC Sync
            icon = getIconByPackageName(pm, PACKAGE_NAME_PCSC);
            mCachedIcon.put(accountType, icon);
            return icon;
        }
        
        // Cherry + 20120111
        // Exchange Task
        if( TextUtils.equals(accountType,HtcExCalendar.getHtcTaskAccountType())){
            accountType=HtcExCalendar.getHtcEasAccountType();
        }
        
        // Windows live task
        if (!TextUtils.isEmpty(accountType) 
        		&& accountType.contains(HtcExCalendar.getHtcWindowsLiveAccountType())&& pm != null) {
            accountType=HtcExCalendar.getHtcWindowsLiveAccountType();
        }
      
        // Google task
        if (!TextUtils.isEmpty(accountType) 
        		&& accountType.contains(HtcExCalendar.getGoogleAccountType())&& pm != null) {
        	accountType=HtcExCalendar.getGoogleAccountType();
        }
        // Local Task icon
        if( TextUtils.equals(accountType, HtcExCalendar.getHtcLocalTaskAccountType()) && pm != null) {
        	 // PC Sync
            icon = getIconByPackageName(pm, PACKAGE_NAME_TASK);
            mCachedIcon.put(accountType, icon);
            return icon;
        }

        // People icon
        if(TextUtils.equals(accountType, ACCOUNT_PEOPLE) && pm != null) {
        	 // PC Sync
            icon = getIconByPackageName(pm, PACKAGE_NAME_PEOPLE);
            mCachedIcon.put(accountType, icon);
            return icon;
        }
        // Cherry + 20120111
        
        if (mTypeToAuthDescription.containsKey(accountType)) {
            try {
                AuthenticatorDescription desc = (AuthenticatorDescription) mTypeToAuthDescription
                        .get(accountType);
                Context authContext = createPackageContext(context, desc.packageName, 0);
                icon = authContext.getResources().getDrawable(desc.iconId);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "No icon for account type " + accountType);
            } catch (Resources.NotFoundException e) {
                Log.w(TAG, "Icon resource not found for account type " + accountType);
            } finally {
                // do nothing...
            }
        }

        if (icon == null) {
            icon = getIconByPackageName(pm, context.getPackageName());
        } else {
            // only put valid icon to cache
            mCachedIcon.put(accountType, icon);
        }
        return icon;
    }
    
    /**
      * Get the app name by the account type
      * @param context the Context
      * @param type of string
      * @return the app name
      */
    public static String getAppNameByAccountType(Context context, String type) {
        String appName = "";
        if(TextUtils.equals(type, HtcExCalendar.getHtcPcSyncAccountType())) {
            appName = ACCOUNT_NAME_PCSYNC;
        }
        if(TextUtils.equals(type, HtcExCalendar.getHtcEasAccountType())) {
            appName = ACCOUNT_NAME_EXCHANGE;
        }
        if(TextUtils.equals(type, HtcExCalendar.getGoogleAccountType())) {
            appName = ACCOUNT_NAME_GOOGLE;
        }
        if(TextUtils.equals(type, HtcExCalendar.getHtcFacebookAccountType(context))) {//if(TextUtils.equals(type, HtcExCalendar.getHtcFacebookAccountType())) {
            appName = ACCOUNT_NAME_FACEBOOK;
        }
        if(TextUtils.equals(type, HtcExCalendar.getHtcWindowsLiveAccountType())) {
            appName = ACCOUNT_NAME_HOTMAIL;
        }
        return appName;
    }
}
