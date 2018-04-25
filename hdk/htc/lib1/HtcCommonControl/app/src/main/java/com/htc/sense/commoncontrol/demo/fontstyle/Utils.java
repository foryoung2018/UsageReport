package com.htc.sense.commoncontrol.demo.fontstyle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;

import com.htc.sense.commoncontrol.demo.R;

public class Utils {
    private static int sTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WHITE = 1;
    public final static int THEME_DARK = 2;

    public final static String TYPE_STYLE = "style";
    public final static String TYPE_DRAWABLE = "drawable";

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity
     * of the same type.
     */
    public static void changeToTheme(Activity activity, Intent intent) {
        activity.finish();
        activity.startActivity(intent);
    }

    public static void changeToTheme(Activity activity, int theme, Class target) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, target));
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity) {
        activity.setTheme(sTheme);
    }

    public static boolean isFontStyle(Context packageContext, int targetResId) {
        TypedArray a;
        int size = 0, color = 0, style = 0;
        String typeface = null;
        try {
            a = packageContext.obtainStyledAttributes(targetResId,
                    R.styleable.myInfoPanel);
            size = a.getDimensionPixelSize(
                    R.styleable.myInfoPanel_android_textSize, 0);
            color = a.getColor(R.styleable.myInfoPanel_android_textColor, 0);
            style = a.getInteger(R.styleable.myInfoPanel_android_textStyle, 0);
            typeface = a.getString(R.styleable.myInfoPanel_android_fontFamily);
            a.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (0 != size || 0 != color || 0 != style || null != typeface)
            return true;
        else
            return false;
    }

    public static boolean hasFontStyle(Context targetContext,
            String targetPackageName) {
        if (getResIdByType(targetPackageName, Utils.TYPE_STYLE,
                targetContext.getResources()) != 0) {
            return true;
        }
        return false;
    }

    public static String[] obtainStringAarray(Context context, int arrayId) {
        String[] stringArray = null;
        stringArray = context.getResources().getStringArray(arrayId);
        return stringArray;
    }

    public static Map<String, String> sortMap(Map<String, String> source) {
        Comparator<String> secondCharComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.substring(0, 2).compareTo(s2.substring(2, 2));
            }
        };
        return source;
    }

    public static Map<String, String> obtainPackageInfo(Context context) {
        int packageCount = 0;
        String[] blackList = obtainStringAarray(context, R.array.pkgBlackList);
        String[] hiddenList = obtainStringAarray(context, R.array.pkgHiddenList);
        String[] hiddenListLabel = obtainStringAarray(context,
                R.array.pkgHiddenListLabel);
        PackageManager pmPack;
        pmPack = context.getPackageManager();
        List<PackageInfo> packinfo = pmPack
                .getInstalledPackages(PackageManager.GET_ACTIVITIES);
        packageCount = packinfo.size();
        Comparator<String> secondCharComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.substring(0, 2).compareTo(s2.substring(2, 2));
            }
        };
        // Map<String,String> packageInfoMap = new TreeMap<String,
        // String>(secondCharComparator);
        Map<String, String> packageInfoMap = new HashMap<String, String>();
        for (int i = 0; i < hiddenList.length; i++) {
            packageInfoMap.put(hiddenList[i], hiddenListLabel[i]);
        }

        for (int i = 0; i < packageCount; i++) {
            String candidateName = packinfo.get(i).packageName;
            boolean skipItem = false;
            for (int j = 0; j < blackList.length; j++) {
                if (candidateName.contains(blackList[j]))
                    skipItem = true;
            }
            if (!skipItem) {
                packageInfoMap.put(candidateName,
                        packinfo.get(i).applicationInfo.loadLabel(pmPack)
                                .toString());
            }
        }
        System.out.println(packageInfoMap);
        return packageInfoMap;
    }

    public static String getApplicationName(Context targetPackageContext) {
        int stringId = targetPackageContext.getApplicationInfo().labelRes;
        return targetPackageContext.getString(stringId);
    }

    // private static ArrayList<String>
    // filterUnknownPackageName(ArrayList<String> originalArrayList){
    // ArrayList<String> filteredArrayList = originalArrayList;
    // ArrayList<String> filter = new ArrayList<String>(Arrays.asList(
    // new String[] {"com.google.android",
    // "com.htc.android.inputset",
    // "com.android.providers",
    // "com.broadcom",
    // "com.android.chrome",
    // "com.dropbox.android",
    // "com.android.development"}));
    // for (Iterator<String> itData = filteredArrayList.iterator();
    // itData.hasNext();){
    // for (Iterator<String> itFilter = filter.iterator(); itFilter.hasNext();){
    // String temp = itFilter.next().toString();
    // if(itData.next().contains(itFilter.next().toString())){
    // itData.remove();
    // break;
    // }
    // }
    // }
    // System.out.println(filteredArrayList);
    // return filteredArrayList;
    // }

    // public class DefaultHashMap<K,V> extends HashMap<K, V> {
    // protected V defaultValue;
    // public DefaultHashMap(V defaultValue) {
    // this.defaultValue = defaultValue;
    // }
    // @Override
    // public V get(Object key) {
    // V v = super.get(key);
    // return ((null == v) && !this.containsKey(key)) ? this.defaultValue : v;
    // }
    // }
    //
    // private SparseIntArray preloadPackageMap(){
    // SparseIntArray packageMapArray = new SparseIntArray();
    // packageMapArray.append(R.id.FrameworkResource, 0x01FFFFFF);
    // packageMapArray.append(R.id.CommonResource, 0x02FFFFFF);
    // packageMapArray.append(R.id.HtcFrameworkResource, 0x04FFFFFF);
    // packageMapArray.append(R.id.WeatherResource, 0x05FFFFFF);
    // packageMapArray.append(R.id.VideoWidgetResource, 0x06FFFFFF);
    // packageMapArray.append(R.id.SocialNetworkResource, 0x07FFFFFF);
    // return packageMapArray;
    // }

    public static Map<String, Integer> packageResIdMap() {
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("android", 0x01FFFFFF);
        myMap.put("com.htc", 0x02FFFFFF);
        myMap.put("com.htc.framework", 0x04FFFFFF);
        myMap.put("com.htc.weather.res", 0x05FFFFFF);
        myMap.put("com.htc.videowidget", 0x06FFFFFF);
        myMap.put("com.htc.socialnetwork.res", 0x07FFFFFF);
        return myMap;
    }

    public static int getResIdByType(String targetPackageName,
            String targetResType, Resources targetRes) {
        if (null == targetPackageName || null == targetResType || null == targetRes){
            return 0;
         }

        Map<String, Integer> packageMap = Utils.packageResIdMap();
        String resType;
        int resTypeId = 0;
        int packageResId = (null == packageMap.get(targetPackageName)) ? 0x7FFFFFFF
                : packageMap.get(targetPackageName);
        int candidateType[] = { 0xFF010000, 0xFF020000, 0xFF030000, 0xFF040000,
                0xFF050000, 0xFF060000, 0xFF070000, 0xFF080000, 0xFF090000,
                0xFF0a0000, 0xFF0b0000, 0xFF0c0000, 0xFF0d0000, 0xFF0e0000,
                0xFF0f0000 };
        for (int i = 0; i < candidateType.length; i++) {
            try {
                resType = targetRes.getResourceTypeName(packageResId
                        & candidateType[i]);
                if (resType.equals(targetResType))
                    return (packageResId & candidateType[i]);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return resTypeId;
    }

    // public static HashMap<String, ArrayList> obtainStyleNamesByType(Context
    // mPackageContext, int styleTypeId, Resources targetRes){
    // int MaxCount = 1000;
    // int tempMask = 0;
    // HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
    // ArrayList<String> mfontStyleName = new ArrayList<String>();
    // ArrayList<Integer> mfontStyleId = new ArrayList<Integer>();
    // String tempString;
    // for (int i=0 ; i < MaxCount ; i++){
    // try{
    // tempMask = styleTypeId|i;
    // tempString = targetRes.getResourceEntryName(tempMask);
    // if(Utils.isFontStyle(mPackageContext, tempMask)){
    // mfontStyleName.add(tempString);
    // mfontStyleId.add(tempMask);
    // }
    // }catch(NotFoundException e){
    // System.out.println("Style ResourceID Not found"+Integer.toString(tempMask));
    // break;
    // }
    // }
    // map.put("fontStyleNameArray", mfontStyleName);
    // map.put("fontStyleIdArray", mfontStyleId);
    // return map;
    // }

}
