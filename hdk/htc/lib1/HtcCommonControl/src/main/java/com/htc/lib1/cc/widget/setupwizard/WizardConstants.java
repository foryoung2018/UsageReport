/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the
 * Authorized User shall not use this work for any purpose other than the purpose
 * agreed by HTC.  Any and all addition or modification to this work shall be
 * unconditionally granted back to HTC and such addition or modification shall be
 * solely owned by HTC.  No right is granted under this statement, including but not
 * limited to, distribution, reproduction, and transmission, except as otherwise
 * provided in this statement.  Any other usage of this work shall be subject to the
 * further written consent of HTC.
 */

package com.htc.lib1.cc.widget.setupwizard;

import android.app.Activity;

/**
 * Miscellaneous constants used by the WizardLayout
 *
 */
public class WizardConstants {
    /** This class is never instantiated. */
    private WizardConstants() {
    }

    /**
     * define intent attribute, user can get progress bar current page number
     * from this intent attribute
     */
    public static final String INTENT_STRING_PROGRESS_BAR_NUMBER = "ProgressBarNumber";
    /**
     * define intent attribute, user can get progress bar max page number from
     * this intent attribute
     */
    public static final String INTENT_STRING_PROGRESS_BAR_MAX_NUMBER = "ProgressBarMaxNumber";

    /**
     * define activity result if the activity finish by press back key
     */
    public static final int RESULT_BACK_KEY = Activity.RESULT_FIRST_USER + 1;
}
