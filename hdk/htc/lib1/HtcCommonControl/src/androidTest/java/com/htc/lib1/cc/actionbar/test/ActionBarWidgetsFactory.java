
package com.htc.lib1.cc.actionbar.test;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarQuickContact;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.test.util.WidgetUtil;

public class ActionBarWidgetsFactory {

    public static ActionBarDropDown createActionBarDropDown(Context context) {
        return createActionBarDropDown(context, ActionBarDropDown.MODE_EXTERNAL);
    }

    public static ActionBarDropDown createActionBarDropDown(Context context, int autoMode) {
        ActionBarDropDown actionBarDropDown = null;
        if (autoMode == ActionBarDropDown.MODE_AUTOMOTIVE) {
            actionBarDropDown = new ActionBarDropDown(context, ActionBarDropDown.MODE_AUTOMOTIVE);
        } else {
            actionBarDropDown = new ActionBarDropDown(context);
        }
        actionBarDropDown.setPrimaryText("DropDownPrimary123456789012345678901234567890123456789012345678901234567890");
        actionBarDropDown.setSecondaryText("DropDownSecondary123456789012345678901234567890123456789012345678901234567890");
        actionBarDropDown.setArrowEnabled(true);
        // actionBarDropDown.setOnClickListener(ActionBarDemoApActivityTest2.this);
        return actionBarDropDown;
    }

    public static ActionBarItemView createActionBarItemView(Context context, boolean left) {
        return createActionBarItemView(context, left, ActionBarItemView.MODE_EXTERNAL);
    }

    public static ActionBarItemView createActionBarItemView(Context context, boolean left, int autoMode) {
        ActionBarItemView actionBarItemView = new ActionBarItemView(context);
        if (left) {
            actionBarItemView.setIcon(android.R.drawable.ic_menu_camera);
        } else {
            actionBarItemView.setIcon(android.R.drawable.ic_menu_help);
        }
        if (autoMode == ActionBarItemView.MODE_AUTOMOTIVE) {
            actionBarItemView.setSupportMode(ActionBarItemView.MODE_AUTOMOTIVE);
        }
        // actionBarItemView.setOnClickListener(ActionBarDemoApActivityTest2.this);
        return actionBarItemView;
    }

    public static ActionBarQuickContact createActionBarQuickContact(Context context) {
        ActionBarQuickContact abqc = new ActionBarQuickContact(context);
        abqc.setImageResource(R.drawable.icon_category_photo);
        abqc.assignContactFromEmail("a@b.c.com", true);
        return abqc;
    }

    public static ActionBarText createActionBarText(Context context) {
        return createActionBarText(context, ActionBarText.MODE_EXTERNAL);
    }

    public static ActionBarText createActionBarText(Context context, int autoMode) {
        ActionBarText actionBarText;
        if (autoMode == ActionBarText.MODE_AUTOMOTIVE) {
            actionBarText = new ActionBarText(context, ActionBarText.MODE_AUTOMOTIVE);
        } else {
            actionBarText = new ActionBarText(context);
        }
        actionBarText.setPrimaryText("TextPrimary123456789012345678901234567890123456789012345678901234567890");
        actionBarText.setSecondaryText("TextSecondary123456789012345678901234567890123456789012345678901234567890");
        return actionBarText;
    }

    public static ActionBarSearch createActionBarSearch(Context context) {
        return createActionBarSearch(context, ActionBarSearch.MODE_EXTERNAL, false);
    }

    public static ActionBarSearch createActionBarSearch(Context context, boolean stopRunningProgressBar) {
        return createActionBarSearch(context, ActionBarSearch.MODE_EXTERNAL, stopRunningProgressBar);
    }

    public static ActionBarSearch createActionBarSearch(Context context, int autoMode) {
        return createActionBarSearch(context, autoMode, false);
    }

    public static ActionBarSearch createActionBarSearch(Context context, int autoMode, boolean stopRunningProgressBar) {
        final ActionBarSearch actionBarSearch;
        if (autoMode == ActionBarSearch.MODE_AUTOMOTIVE) {
            actionBarSearch = new ActionBarSearch(context, ActionBarSearch.MODE_AUTOMOTIVE);
        } else {
            actionBarSearch = new ActionBarSearch(context);
        }
        actionBarSearch.setProgressVisibility(View.VISIBLE);
        actionBarSearch.setClearIconVisibility(View.VISIBLE);
        actionBarSearch.setClearIconOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                actionBarSearch.getAutoCompleteTextView().setText("");
            }
        });
        if (stopRunningProgressBar) {
            final ProgressBar progressBar = (ProgressBar) actionBarSearch.getChildAt(2);
            WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
        }
        return actionBarSearch;
    }
}
