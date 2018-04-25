package com.htc.lib2.weather;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

public final class Settings {
    static final String TAG = "Settings";
    private static final boolean LOG_FLAG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    public static final String ACTION_LOCATION_SOURCE_SETTINGS = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
    public static final String ACTION_SETTINGS  = android.provider.Settings.ACTION_SETTINGS;
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS  = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
    private static boolean sUseSettings = true;

    public static class SafeSetting {

        public static  <T, E> T get(ContentResolver cr, String name, T def, E settings) {
            T rtnValue = def;
            //if (sUseSettings || isDateSetting(name)) 
            {
                try{
                    Object rtn = null;
                    if (def instanceof Integer) {
                        if (settings instanceof android.provider.Settings.System) {
                            rtn = android.provider.Settings.System.getInt(cr, name, (Integer)def);
                        } else if (settings instanceof android.provider.Settings.Global) {
                            rtn = android.provider.Settings.Global.getInt(cr, name, (Integer)def);
                        } else if (settings instanceof android.provider.Settings.Secure) {
                            rtn = android.provider.Settings.Secure.getInt(cr, name, (Integer)def);
                        }

                    } else if (def instanceof Long) {
                        if (settings instanceof android.provider.Settings.System) {
                            rtn = android.provider.Settings.System.getLong(cr, name, (Long)def);
                        } else if (settings instanceof android.provider.Settings.Global) {
                            rtn = android.provider.Settings.Global.getLong(cr, name, (Long)def);
                        } else if (settings instanceof android.provider.Settings.Secure) {
                            rtn = android.provider.Settings.Secure.getLong(cr, name, (Long)def);
                        }

                    } else if (def instanceof String) {
                        if (settings instanceof android.provider.Settings.System) {
                            rtn = android.provider.Settings.System.getString(cr, name);
                        } else if (settings instanceof android.provider.Settings.Global) {
                            rtn = android.provider.Settings.Global.getString(cr, name);
                        } else if (settings instanceof android.provider.Settings.Secure) {
                            rtn = android.provider.Settings.Secure.getString(cr, name);
                        }

                    }
                    if (rtn != null)
                        rtnValue = (T)rtn;

                    if (LOG_FLAG) Log.i(TAG, getMethodtoString(name, def, settings) + " = " + rtnValue);
                } catch (java.lang.SecurityException e) {
                    sUseSettings = false;
                    if (LOG_FLAG) Log.w(TAG, getMethodtoString(name, def, settings) + " failed, SecurityException");
                } catch (Exception e) {
                    if (LOG_FLAG) Log.w(TAG, getMethodtoString(name, def, settings) + " failed," + e);
                }
            }

            return rtnValue;
        }

        public static <T, E> boolean put(ContentResolver cr, String name, T def, E settings) {
            // fail retry
            if (sUseSettings && writeSettingBySettings(cr, name, def, settings)) {
                return true;
            } else {
                return writeSettingByProvider(cr, name, def, settings);
            }
        }

        private static <T, E> boolean writeSettingBySettings(ContentResolver cr, String name, T def, E settings) {
            boolean rtn = false;
            try {
                if (def instanceof Integer) {
                    if (settings instanceof android.provider.Settings.System) {
                        rtn = android.provider.Settings.System.putInt(cr, name, (Integer) def);
                    } else if (settings instanceof android.provider.Settings.Global) {
                        rtn = android.provider.Settings.Global.putInt(cr, name, (Integer) def);
                    } else if (settings instanceof android.provider.Settings.Secure) {
                        rtn = android.provider.Settings.Secure.putInt(cr, name, (Integer) def);
                    }

                } else if (def instanceof Long) {
                    if (settings instanceof android.provider.Settings.System) {
                        rtn = android.provider.Settings.System.putLong(cr, name, (Long) def);
                    } else if (settings instanceof android.provider.Settings.Global) {
                        rtn = android.provider.Settings.Global.putLong(cr, name, (Long) def);
                    } else if (settings instanceof android.provider.Settings.Secure) {
                        rtn = android.provider.Settings.Secure.putLong(cr, name, (Long) def);
                    }
                } else if (def instanceof String) {
                    if (settings instanceof android.provider.Settings.System) {
                        rtn = android.provider.Settings.System.putString(cr, name, (String) def);
                    } else if (settings instanceof android.provider.Settings.Global) {
                        rtn = android.provider.Settings.Global.putString(cr, name, (String) def);
                    } else if (settings instanceof android.provider.Settings.Secure) {
                        rtn = android.provider.Settings.Secure.putString(cr, name, (String) def);
                    }
                }

                //e.g. "System.setInteger(device_provisioned) = 1"
                if (LOG_FLAG)
                    Log.i(TAG, setMethodtoString(name, def, settings) + " = " + rtn);
            } catch (java.lang.SecurityException e) {
                sUseSettings = false;
                if (LOG_FLAG)
                    Log.w(TAG, setMethodtoString(name, def, settings) + " failed, SecurityException");
            } catch (Exception e) {
                if (LOG_FLAG)
                    Log.w(TAG, setMethodtoString(name, def, settings) + " failed," + e);
            }
            return rtn;
        }

        private static <T, E> boolean writeSettingByProvider(ContentResolver cr, String name, T def, E settings) {
            ContentProviderClient client = null;
            boolean rtn = false;
            try {
                client = cr.acquireUnstableContentProviderClient(WeatherConsts.CONTENT_URI);
                if (client == null) {
                    return false;
                }
                Bundle bundle = new Bundle();
                bundle.putString("setting_name", name);

                String settingClass = null;
                if (settings instanceof android.provider.Settings.System) {
                    settingClass = android.provider.Settings.System.class.getName();
                } else if (settings instanceof android.provider.Settings.Global) {
                    settingClass = android.provider.Settings.Global.class.getName();
                } else if (settings instanceof android.provider.Settings.Secure) {
                    settingClass = android.provider.Settings.Secure.class.getName();
                }
                bundle.putString("setting_class", settingClass);

                final String valueType = "value_type";
                final String valueName = "setting_value";
                if (def instanceof Integer) {
                    bundle.putString(valueType, "int");
                    bundle.putInt(valueName, ((Integer) def).intValue());
                } else if (def instanceof Long) {
                    bundle.putString(valueType, "long");
                    bundle.putLong(valueName, ((Long) def).longValue());
                } else if (def instanceof String) {
                    bundle.putString(valueType, "string");
                    bundle.putString(valueName, (String) def);
                }
                rtn = client.call("putSystemSetting", null, bundle) != null;
                if (LOG_FLAG) Log.i(TAG, setMethodtoString(name, def, settings) + " by provider = " + rtn);
            } catch (Exception e) {
                if (LOG_FLAG) Log.w(TAG, "fail to write setting by provider", e);
            } finally {
                if (client != null) {
                    client.release();
                }
            }
            return rtn;
        }

        private static <T, E> String getMethodtoString(String name, T def, E settings) {
            StringBuilder sb = new StringBuilder();
            sb.append(settings.getClass().getSimpleName());
            sb.append(".get");
            sb.append(def.getClass().getSimpleName());
            sb.append("(");
            sb.append(name);
            sb.append(")");

            return  sb.toString();
        }
        private static <T, E> String setMethodtoString(String name, T def, E settings) {
            StringBuilder sb = new StringBuilder();
            sb.append(settings.getClass().getSimpleName());
            sb.append(".set");
            sb.append(def.getClass().getSimpleName());
            sb.append("(");
            sb.append(name);
            sb.append(",");
            sb.append(String.valueOf(def));
            sb.append(")");

            return  sb.toString();
        }
        private static boolean isDateSetting(String name) {
            return System.TIME_12_24.equals(name) || System.DATE_FORMAT.equals(name);
        }
    }
    public static final class  System extends SafeSetting{
        public static final String TIME_12_24 = android.provider.Settings.System.TIME_12_24;
        public static final String DATE_FORMAT = android.provider.Settings.System.DATE_FORMAT;
        static android.provider.Settings.System setting = new android.provider.Settings.System();
        public static int getInt(ContentResolver cr, String name) {
            return getInt(cr, name, 0);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return get(cr, name, Integer.valueOf(def), setting);
        }

        public static boolean putInt(ContentResolver cr, String name, int def) {
            return put(cr, name, def, setting);
        }

        public static String getString(ContentResolver cr, String name) {
            return get(cr, name, "", setting);
        }

        public static boolean putString(ContentResolver cr, String name, String def) {
            return put(cr, name, def, setting);
        }

        public static long getLong(ContentResolver cr, String name) throws  android.provider.Settings.SettingNotFoundException {
            return get(cr, name, 0l, setting);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return get(cr, name, def, setting);
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return put(cr, name, value, setting);
        }

        public static  <T, E> T get(ContentResolver cr, String name, T def, E settings) {
            return SafeSetting.get(cr, name, def, settings);
        }
        public static <T, E> boolean put(ContentResolver cr, String name, T def, E settings) {
            return SafeSetting.put(cr, name, def, settings);
        }
    }

    public static final class Global extends SafeSetting{
        public static final String AUTO_TIME_ZONE = android.provider.Settings.Global.AUTO_TIME_ZONE;
        public static final String DEVICE_PROVISIONED = android.provider.Settings.Global.DEVICE_PROVISIONED;
        public static final String AIRPLANE_MODE_ON = android.provider.Settings.Global.AIRPLANE_MODE_ON;
        static android.provider.Settings.Global setting = new android.provider.Settings.Global();

        public static int getInt(ContentResolver cr, String name) throws  android.provider.Settings.SettingNotFoundException {
            return getInt(cr, name, 0);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return get(cr, name, def, setting);
        }
        public static boolean putInt(ContentResolver cr, String name, int def) {
            return put(cr, name, def, setting);
        }
    }

    public static final class Secure extends SafeSetting{
        public static final String LOCATION_PROVIDERS_ALLOWED = android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED;
        public static final String LOCATION_MODE = android.provider.Settings.Secure.LOCATION_MODE;
        public static final int LOCATION_MODE_OFF = android.provider.Settings.Secure.LOCATION_MODE_OFF;
        public static final int LOCATION_MODE_HIGH_ACCURACY = android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
        public static final int LOCATION_MODE_BATTERY_SAVING = android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
        public static final int LOCATION_MODE_SENSORS_ONLY = android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;


        static android.provider.Settings.Secure setting = new android.provider.Settings.Secure();
        public static String getString(ContentResolver cr, String name) {
            return get(cr, name, "", setting);
        }
        public static boolean putString(ContentResolver cr, String name, String def) {
            return put(cr, name, def, setting);
        }
        public static int getInt(ContentResolver cr, String name, int def) {
            return get(cr, name, Integer.valueOf(def), setting);
        }
        public static boolean putInt(ContentResolver cr, String name, int def) {
            return put(cr, name, def, setting);
        }
        public static final boolean isLocationProviderEnabled (ContentResolver cr, String provider) {
            try{
                boolean rtn = android.provider.Settings.Secure.isLocationProviderEnabled(cr, provider);
                if (LOG_FLAG) Log.i(TAG,"Secure.isLocationProviderEnabled(" + provider + ") = " + rtn);
                return rtn;
            } catch (Exception e) {
                if (LOG_FLAG) Log.w(TAG,"Secure.isLocationProviderEnabled(" + provider + ") failed");
                return false;
            }
        }
    }
}
