
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.lang.ref.WeakReference;

/**
 * HTC terms and conditions hyperlink.
 */
public class LegalTipsTosClickableSpan extends ClickableSpan {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private WeakReference<Activity> mRefActivity;

    /**
     * Create an instance.
     * 
     * @param activity Activity to operate on.
     */
    public LegalTipsTosClickableSpan(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("'activity' is null.");
        }

        mRefActivity = new WeakReference<>(activity);
    }
    
    /**
     * Remove underline but ensure link color.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        mLogger.verbose("LegalTipsTosClickableSpan: ", widget);
        Activity activity = mRefActivity.get();
        if (activity != null) {
            TermActivity.launchTosActivity(activity);
            return;
        }
    }
}
