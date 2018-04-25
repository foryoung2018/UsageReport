package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.preference.HtcPreference.ReferenceViewCreater;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

public class HtcMultiSelectListPreference extends MultiSelectListPreference {
    public HtcMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
    }

    public HtcMultiSelectListPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
    }

    private int mCustomLayoutResId = 0;

    /**
     * @hide
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        return HtcPreference.adjustCreateView(getContext(), parent, mCustomLayoutResId, new ReferenceViewCreater() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return HtcMultiSelectListPreference.super.onCreateView(parent);
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
