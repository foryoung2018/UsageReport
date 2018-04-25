package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.preference.HtcPreference.ReferenceViewCreater;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

public class HtcEditTextPreference extends EditTextPreference {
    private int mCustomLayoutResId = 0;
    public HtcEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs, defStyle);
    }

    public HtcEditTextPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
    }

    public HtcEditTextPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
    }

    /**
     * @hide
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        return HtcPreference.adjustCreateView(getContext(), parent, mCustomLayoutResId, new ReferenceViewCreater() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return HtcEditTextPreference.super.onCreateView(parent);
            }
        });
    }

    /**
     * @hide
     * Sets the layout resource that is inflated as the {@link View} to be shown
     * for this Preference.
     * Note:If using custom layout, do not set padding/margin in your layout
     * HtcPreference would auto set these space for alignment
     *
     * @param layoutResId The layout resource ID to be inflated and returned as a View
     */
    @Override
    public void setLayoutResource(int layoutResId) {
        mCustomLayoutResId = layoutResId;
    }

    /**
     * @hide
     * Gets the layout resource that will be shown as the {@link View} for this Preference.
     *
     * @return The layout resource ID what you set from setLayoutResource().
     */
    @Override
    public int getLayoutResource() {
        return mCustomLayoutResId;
    }
}
