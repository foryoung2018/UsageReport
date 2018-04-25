/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.app;

import java.text.NumberFormat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.ShadowLinearLayout;

/**
 * <p>A dialog showing a progress indicator and an optional text message or view.
 * Only a text message or a view can be used at the same time.</p>
 * <p>The dialog can be made cancelable on back key press.</p>
 * <p>The progress range is 0..10000.</p>
 */
public class HtcProgressDialog extends HtcAlertDialog {

    /** Creates a ProgressDialog with a circular, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;

    /** Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;

    private ProgressBar mProgress;
    private TextView mMessageView;

    private int mProgressStyle = STYLE_SPINNER;
    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;

    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    private boolean mHasStarted;
    private Handler mViewUpdateHandler;

    private boolean mHasMessage = false;
    private boolean mHasTitle = false;
    private long mShowTime;
    private long mDismissTime;
    private boolean mAllCaps;

    /**
     * the constructor
     * @param context see android.app.ProgressDialog
     */
    public HtcProgressDialog(Context context) {
        super(context);
        initFormats();
    }

    /**
     * the constructor
     * @param context see android.app.ProgressDialog
     * @param theme see android.app.ProgressDialog
     */
    public HtcProgressDialog(Context context, int theme) {
        super(context, theme);
        initFormats();
    }

    @SuppressWarnings("deprecation")
    private void initFormats() {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);

        // Sense5 new UI design
        setInverseBackgroundForced(false);
        mAllCaps = HtcResUtil.isInAllCapsLocale(getContext());
    }

    /**
      * to show the progress dialog
      * @param context see android.app.ProgressDialog
      * @param title see android.app.ProgressDialog
      * @param message see android.app.ProgressDialog
      * @return see android.app.ProgressDialog
      */
    public static HtcProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false);
    }

    /**
      * to show the progress dialog
      * @param context see android.app.ProgressDialog
      * @param title see android.app.ProgressDialog
      * @param message see android.app.ProgressDialog
      * @param indeterminate see android.app.ProgressDialog
      * @return see android.app.ProgressDialog
      */
    public static HtcProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    /**
      * to show the progress dialog
      * @param context see android.app.ProgressDialog
      * @param title see android.app.ProgressDialog
      * @param message see android.app.ProgressDialog
      * @param indeterminate see android.app.ProgressDialog
      * @param cancelable see android.app.ProgressDialog
      * @return see android.app.ProgressDialog
      */
    public static HtcProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    /**
      * to show the progress dialog
      * @param context see android.app.ProgressDialog
      * @param title see android.app.ProgressDialog
      * @param message see android.app.ProgressDialog
      * @param indeterminate see android.app.ProgressDialog
      * @param cancelable see android.app.ProgressDialog
      * @param cancelListener see android.app.ProgressDialog
      * @return see android.app.ProgressDialog
      */
    public static HtcProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
        HtcProgressDialog dialog = new HtcProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    /** {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    /* Update the number and percent */
                    if (mProgressNumberFormat != null) {
                        int progress = (null != mProgress) ? mProgress.getProgress() : 0;
                        int max = (null != mProgress) ? mProgress.getMax() : 100;
                        String format = mProgressNumberFormat;
                        mProgressNumber.setText(String.format(format, progress, max));

                        double percent = (double) progress / (double) max;
                        SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                        tmp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL),
                                0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mProgressPercent.setText(tmp);
                    } else {
                        mProgressNumber.setText("");
                        mProgressPercent.setText("");
                    }
                }
            };
            View view = inflater.inflate(R.layout.alert_dialog_progress, null);
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mProgressNumber = (TextView) view.findViewById(android.R.id.text2);
            mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
            setView(view);
        } else {
            if (!mHasTitle) {
                int layout_progress_dialog = R.layout.progress_dialog;
                View view = inflater.inflate(layout_progress_dialog, null);
                mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
                mMessageView = (TextView) view.findViewById(android.R.id.message);
                setView(view);
            } else {
                mProgress = null;
                mMessageView = null;
                // setup these 2 after super.onCreate()
            }
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);
        adjustLayout();
    }

    /** {@inheritDoc}
     * @deprecated [Module internal use] should marked (at)hide
     */
    /**@hide*/
    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    /** {@inheritDoc}
     * @deprecated [Module internal use] should marked (at)hide
     */
    /**@hide*/
    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    /**
     * set current progress
     * @param value see android.app.ProgressDialog
     */
    public void setProgress(int value) {
        if (mHasStarted) {
            if (null != mProgress) {
                mProgress.setProgress(value);
            }
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    /**
     * set secondary progress
     * @param secondaryProgress see android.app.ProgressDialog
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    /**
     * get current progress
     * @return see android.app.ProgressDialog
     */
    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    /**
     * get secondary progress
     * @return see android.app.ProgressDialog
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    /**
     * get max of progress
     * @return see android.app.ProgressDialog
     */
    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    /**
     * set max of progress
     * @param max see android.app.ProgressDialog
     */
    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    /**
     * increase progress
     * @param diff see android.app.ProgressDialog
     */
    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    /**
     * increase secondary progress
     * @param diff see android.app.ProgressDialog
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    /**
     * set progress bar drawable
     * @param d see android.app.ProgressDialog
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    /**
     * set progress spinner drawable
     * @param d see android.app.ProgressDialog
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    /**
     * set progress style (spinner or horizontal bar)
     * @param indeterminate see android.app.ProgressDialog
     */
    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    /**
     * get progress style
     * @return see android.app.ProgressDialog
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    /** {@inheritDoc}
     */
    @Override
    public void setMessage(CharSequence message) {
        if (null != message) mHasMessage = true;

        if (mProgressStyle == STYLE_HORIZONTAL || mHasTitle) {
            // for horizontal bar style and progress spinner style with title
            super.setMessage(message);
        } else {
            // only for progress spinner style without title
            if (null != mMessageView) {
                mMessageView.setText(message);
            } else {
                mMessage = message;
            }
        }
    }

    /**
     * apply style to progress bar
     * @param style see android.app.ProgressDialog
     */
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    /**
     * Change the format of the small text showing current and maximum units
     * of progress.  The default is "%1d/%2d".
     * Should not be called during the number is progressing.
     * @param format A string passed to {@link String#format String.format()};
     * use "%1d" for the current number and "%2d" for the maximum.  If null,
     * nothing will be shown.
     */
    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged();
    }

    /**
     * Change the format of the small text showing the percentage of progress.
     * The default is
     * {@link NumberFormat#getPercentInstance() NumberFormat.getPercentageInstnace().}
     * Should not be called during the number is progressing.
     * @param format An instance of a {@link NumberFormat} to generate the
     * percentage text.  If null, nothing will be shown.
     * @hide
     */
    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }

    //Mark.SL_Chen bright background when has title Sense35 [
    /** {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mHasTitle = !TextUtils.isEmpty(title);
        if (mHasTitle) {
            setInverseBackgroundForced(true);
        }
    }
    //Mark.SL_Chen bright background when has title Sense35 ]

    /** {@inheritDoc}
     */
    @Override
    public void show() {
       mShowTime = SystemClock.uptimeMillis();
       super.show();
    }

    /** {@inheritDoc}
     */
    @Override
    public void dismiss() {
       int nMinDuration = 300;
       mDismissTime = SystemClock.uptimeMillis();
       long delta = mDismissTime - mShowTime;
       long rest = nMinDuration - delta;
       if ( 0 < delta && 0 < rest ) {
          try {
             java.lang.Thread.sleep(rest);
          } catch (InterruptedException e) {
             e.printStackTrace();
          }
       }

       super.dismiss();
       mShowTime = 0;
       mDismissTime = 0;
    }

    /**
     * adjust customPanel's weight.
     * so that when message in contentPanel is very long,
     * it will not take all the spaces away from customPanel.
     */
    private void adjustLayout() {
        if (STYLE_HORIZONTAL != mProgressStyle && !mHasTitle) {
            // for spinning progress
            View tmp = findViewById(R.id.parentPanel);
            if (tmp instanceof ShadowLinearLayout) {
                ((ShadowLinearLayout) tmp).setLayoutArg(LayoutParams.WRAP_CONTENT);
            }
            if (null != mMessageView) {
                mMessageView.setAllCaps(mAllCaps);
            }
            ViewGroup bodyView = null == mMessageView ? null : (ViewGroup) mMessageView.getParent();
            if (null != bodyView) {
                // change top/bottom padding to M5x2/M4x2
                Resources res = getContext().getResources();
                int leftPad = bodyView.getPaddingLeft();
                int topPad = res.getDimensionPixelOffset(R.dimen.margin_m);
                int rightPad = bodyView.getPaddingRight();
                int bottomPad = topPad;
                bodyView.setPadding(leftPad, topPad, rightPad, bottomPad);
            }
        } else if (STYLE_HORIZONTAL != mProgressStyle && mHasTitle) {
            mProgress = (ProgressBar) findViewById(android.R.id.progress);
            mMessageView = (TextView) findViewById(R.id.message);
            mProgress.setVisibility(View.VISIBLE);
        }
        if (STYLE_HORIZONTAL != mProgressStyle) return;

        if (mHasMessage) {
            // make customPanel no shrink
            View customPanel = findViewById(R.id.customPanel);
            if (null != customPanel) {
                ViewGroup.LayoutParams lp = customPanel.getLayoutParams();
                if (lp instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) lp).weight = 0;
                    customPanel.setLayoutParams(lp);
                }
            }
        }

        // adjust minimum height
        if (mHasMessage) {
            // remove minimum height restrictions on both message and progress panel
            View v = findViewById(R.id.contentPanel);
            v.setMinimumHeight(0);
        } else {
            // set minimum height on progress panel
            View v = findViewById(R.id.custom);
            v.setMinimumHeight(HtcAlertDialog.getDefaultListItemHeight(getContext(), false));
        }

        // adjust paddings
        //View messageView = findViewById(R.id.message);
        if (mHasMessage) {
            // do nothing
        } else {
            // change top margin to M2x2 and bottom to M1 according to UI guideline
            ViewGroup bodyView = null == mProgress ? null : (ViewGroup) mProgress.getParent();
            if (null != bodyView) {
                Resources res = getContext().getResources();
                int leftPad = bodyView.getPaddingLeft();
                int topPad = res.getDimensionPixelOffset(R.dimen.margin_m_2);
                int rightPad = bodyView.getPaddingRight();
                int bottomPad = res.getDimensionPixelOffset(R.dimen.margin_l);
                bodyView.setPadding(leftPad, topPad, rightPad, bottomPad);
            }
        }
    }

    /**
     * @hide
     */
    public void setMessageAllCaps(boolean allCaps) {
        mAllCaps = allCaps;
        if (null != mMessageView) {
            mMessageView.setAllCaps(allCaps);
        }
    }
}
