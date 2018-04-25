
package com.htc.lib1.cc.checkablebutton.activityhelper;

import android.os.Bundle;
import android.util.Log;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcSwitch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HtcSwitchDemo extends ActivityBase {
    final static String TAG = "HtcSwitchTest";
    HtcSwitch htcSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setContentView(R.layout.htcbutton_demos_switch);
        htcSwitch = (HtcSwitch) findViewById(R.id.myhtcswitch);
    }

    public void changeSwitchLocation(float percent) {
        Method setOffsetXMethod = null;
        try {
            setOffsetXMethod = HtcSwitch.class.getDeclaredMethod("setOffsetX", float.class);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        Method setTrackPosotionMethod = null;
        try {
            setTrackPosotionMethod = HtcSwitch.class.getDeclaredMethod("setTrackPosition", float.class);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }

        Method method = null;
        if (setOffsetXMethod != null) {
            try {
                Field f = HtcSwitch.class.getDeclaredField("mDrawableWidth");
                f.setAccessible(true);
                int width = f.getInt(htcSwitch);
                percent = (float) (width / 2);
            } catch (NoSuchFieldException e) {
                Log.d(TAG,
                        "[HtcSwitchTest] setOffsetX(int) in com.htc.lib1.cc.widget.HtcSwitch class  is not found");
            } catch (IllegalAccessException e) {
                Log.d(TAG, "[HtcSwitchTest] IllegalAccessException");
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "[HtcSwitchTest] IllegalArgumentException");
            }
            method = setOffsetXMethod;
        } else if (setTrackPosotionMethod != null) {
            method = setTrackPosotionMethod;
        } else {
            return;
        }

        method.setAccessible(true);
        try {
            method.invoke(htcSwitch, percent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
