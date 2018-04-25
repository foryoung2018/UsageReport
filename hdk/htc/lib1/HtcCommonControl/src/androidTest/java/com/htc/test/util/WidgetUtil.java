
package com.htc.test.util;

import android.graphics.drawable.Drawable;
import android.test.InstrumentationTestCase;
import android.widget.ProgressBar;

import java.lang.reflect.Field;

public class WidgetUtil {
    /**
     * This method must not be called from the UI thread
     *
     * @param instrumentationTestCase
     * @param progressbar
     */
    public static void setProgressBarIndeterminatedStopRunning(InstrumentationTestCase instrumentationTestCase, final ProgressBar progressbar) {
        instrumentationTestCase.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                setProgressBarIndeterminatedStopRunning(progressbar);
            }
        });
    }

    /**
     * This method must be called from the UI thread.
     *
     * @param progressbar
     */
    public static void setProgressBarIndeterminatedStopRunning(final ProgressBar progressbar) {
        Field f = null;
        try {
            f = ProgressBar.class.getDeclaredField("mOnlyIndeterminate");
            f.setAccessible(true);
            f.setBoolean(progressbar, false);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Drawable indeterminateDrawable = progressbar.getIndeterminateDrawable();
        progressbar.setIndeterminate(false);
        indeterminateDrawable.setLevel(0);
        progressbar.setProgressDrawable(indeterminateDrawable);
    }

}
