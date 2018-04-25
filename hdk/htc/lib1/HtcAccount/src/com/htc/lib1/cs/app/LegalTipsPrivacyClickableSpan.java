package com.htc.lib1.cs.app;

import android.app.Activity;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.lang.ref.WeakReference;

/**
 * Privacy policy hyperlink.
 */
public class LegalTipsPrivacyClickableSpan extends ClickableSpan {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private WeakReference<Activity> mRefActivity;

    public LegalTipsPrivacyClickableSpan(Activity activity) {
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
        mLogger.verbose("LegalTipsPrivacyClickableSpan: ", widget);
        Activity activity = mRefActivity.get();
        if (activity != null) {
            TermActivity.launchPrivacyPolicyActivity(activity);
            return;
        }
    }
}