
package com.htc.lib1.cc.view.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ProgressBar;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;

/**
* @hide
* @deprecated [Module internal use]
*/
public class HtcProgressBarUtil {

    public static final int DISPLAY_MODE_LIGHT = 0;

    public static final int DISPLAY_MODE_DARK = 1;

    public static final int DISPLAY_MODE_FULL = 2;

    private static final int LAYER_INDEX_TRACK = 0;

    private static final int LAYER_INDEX_BUFFER = 1;

    private static final int LAYER_INDEX_PROGRESS = 2;

    /**
     * set HtcProgressBar Mode -- light, dark, full
     * @param htcProgressBar
     * @param Mode
     * @hide
     * @deprecated [Module internal use]
     */
    @Deprecated
    public static void setProgressBarMode(Context context, ProgressBar progressBar, int displayMode) {
        if (null == context || null == progressBar || null == context.getResources()) {
            return;
        }

        Resources res = context.getResources();
        LayerDrawable layerDrawable = (LayerDrawable) res.getDrawable(R.drawable.htcprogress);
        if (null == layerDrawable) {
            return;
        }

        LayerDrawable progress = getThemeDrawable(context, layerDrawable, displayMode);
        progressBar.setProgressDrawable(progress);
    }

    private static LayerDrawable getThemeDrawable(Context context, LayerDrawable layer, int mode) {
        int colorTrack = 0, colorProgress, colorBuffer;
        if (mode == DISPLAY_MODE_DARK) {
            colorTrack = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_progress_track_end_color);
        } else {
            colorTrack = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_progress_track_start_color);
        }

        if (DISPLAY_MODE_FULL == mode) {
            colorProgress = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_light_category_color);
        } else {
            colorProgress = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_category_color);
        }

        colorBuffer = HtcCommonUtil.getCommonThemeColor(context,
                R.styleable.ThemeColor_progress_track_center_color);
        if (null != layer) {
            Drawable track = layer.getDrawable(LAYER_INDEX_TRACK);
            Drawable buffer = layer.getDrawable(LAYER_INDEX_BUFFER);
            Drawable progress = layer.getDrawable(LAYER_INDEX_PROGRESS);

            (track.mutate()).setColorFilter(colorTrack, PorterDuff.Mode.SRC_ATOP);
            (buffer.mutate()).setColorFilter(colorBuffer, PorterDuff.Mode.SRC_ATOP);
            (progress.mutate()).setColorFilter(colorProgress, PorterDuff.Mode.SRC_ATOP);
        }

        return layer;
    }
}
