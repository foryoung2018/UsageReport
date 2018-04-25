/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.lib1.cc.support.provider;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @hide
 * @deprecated [module internal use]
 */
public final class SettingsCompat {
    private static final String TAG = "SettingsCompat";
    private static final boolean DEBUG = HtcBuildFlag.Htc_DEBUG_flag;

    public static final class SystemCompat {

        private static Method sGetStringForUser;
        private static Method sPutStringForUser;

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (sGetStringForUser == null) {
                sGetStringForUser = getDeclaredMethod(Settings.System.class, "getStringForUser", ContentResolver.class, String.class, int.class);
            }
            if (sGetStringForUser == null) return null;

            String retStr = null;
            Object retVal = invokeMethod(sGetStringForUser, null, resolver, name, userHandle);
            if (retVal != null) retStr = retVal.toString();

            if (DEBUG) Log.d(TAG, "[getStringForUser] retStr=" + retStr);
            return retStr;
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            if (sPutStringForUser == null) {
                sPutStringForUser = getDeclaredMethod(Settings.System.class, "putStringForUser", ContentResolver.class, String.class, String.class, int.class);
            }
            if (sPutStringForUser == null) return false;

            boolean ret = false;
            Object retVal = invokeMethod(sPutStringForUser, null, resolver, name, value, userHandle);
            if (retVal != null) ret = (Boolean) retVal;

            if (DEBUG) Log.d(TAG, "[putStringForUser] ret=" + ret);
            return ret;
        }

    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Object invokeMethod(Method method, Object receiver, Object... args) {
        Object result = null;
        try {
            result = method.invoke(receiver, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
