package com.htc.lib1.cs.push.registrator;

import android.content.Context;

import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.utils.AppComponentSettingUtils;

/**
 * Created by leohsu on 2017/2/22.
 */

public class RegistratorUtil {
    public static void disableBaiduPushComponents(Context context) {
        disableBaiduPushComponents(context, false);
    }

    public static void disableBaiduPushComponents(Context context, boolean disableMore) {
        AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_SERVICE);
        AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_RECEIVER);
        AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_REG_RECEIVER);
        AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_MESSAGE_RECEIVER);
        AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_COMMAND_SERVICE);

        if (disableMore) {
            AppComponentSettingUtils.disable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_INFO_PROVIDER);
        }
    }

    public static void enableBaiduPushComponents(Context context) {
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_SERVICE);
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_RECEIVER);
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_REG_RECEIVER);
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_MESSAGE_RECEIVER);
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_COMMAND_SERVICE);
        AppComponentSettingUtils.enable(context, PnsInternalDefs.CLASS_BAIDU_PUSH_INFO_PROVIDER);
    }
}
