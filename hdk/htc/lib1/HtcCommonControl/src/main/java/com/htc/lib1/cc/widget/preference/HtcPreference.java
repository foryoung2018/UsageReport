package com.htc.lib1.cc.widget.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.R;

public class HtcPreference extends Preference {
    private int mCustomLayoutResId = 0;
    private static int[] sPaddings = null;
    public HtcPreference(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs, defStyle);
    }

    public HtcPreference(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Preference), attrs);
    }

    public HtcPreference(Context context) {
        super(new ContextThemeWrapper(context, R.style.Preference));
    }

    /**
     * @hide
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        return adjustCreateView(getContext(), parent, mCustomLayoutResId, new ReferenceViewCreater() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return HtcPreference.super.onCreateView(parent);
            }
        });
    }

    /**
     * @hide
     */
    interface ReferenceViewCreater {
        View onCreateView(ViewGroup parent);
    }

    /**
     * @hide
     */
    static View adjustCreateView(Context context, ViewGroup parent, int customLayoutResId, ReferenceViewCreater creater) {
        if (customLayoutResId == 0) {
            return creater.onCreateView(parent);
        }

        if (sPaddings == null) {
            ViewGroup layout = (ViewGroup)creater.onCreateView(parent);
            sPaddings = new int[4];
            TextView title = (TextView) layout.findViewById(android.R.id.title);
            if (title != null && title.getParent() != null) {
                ViewGroup textParent = (ViewGroup) title.getParent();
                sPaddings[0] = layout.getPaddingStart() + textParent.getPaddingStart();
                sPaddings[1] = layout.getPaddingTop() + textParent.getPaddingTop();
                sPaddings[2] = layout.getPaddingEnd() + textParent.getPaddingEnd();
                sPaddings[3] = layout.getPaddingBottom() + textParent.getPaddingBottom();
            }
        }
        ViewGroup layout;
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (ViewGroup) layoutInflater.inflate(customLayoutResId, parent, false);
        layout.setPaddingRelative(sPaddings[0], sPaddings[1], sPaddings[2], sPaddings[3]);
        return layout;
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
     * @return The layout resource ID.
     */
    @Override
    public int getLayoutResource() {
        return mCustomLayoutResId;
    }
}
