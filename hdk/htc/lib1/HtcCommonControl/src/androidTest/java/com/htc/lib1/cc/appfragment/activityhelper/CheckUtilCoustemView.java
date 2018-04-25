
package com.htc.lib1.cc.appfragment.activityhelper;

import android.content.Context;
import android.view.View;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.CheckUtil;

import java.lang.reflect.Field;

public class CheckUtilCoustemView extends View {

    public boolean isUIThread;

    public boolean isContextThemeWrapper;

    public CheckUtilCoustemView(Context context) {
        super(context);

        try {
            Class<?> c = HtcBuildFlag.class;
            Field field = c.getDeclaredField("Htc_DEBUG_flag");
            field.setAccessible(true);
            field.set(null, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        isUIThread = CheckUtil.isUIThread(context);
        isContextThemeWrapper = CheckUtil.isContextThemeWrapper(context);
    }

}
