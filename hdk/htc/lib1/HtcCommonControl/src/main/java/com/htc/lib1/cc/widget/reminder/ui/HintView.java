package com.htc.lib1.cc.widget.reminder.ui;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.reminder.Const;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class HintView extends LinearLayout {

    private static final String TAG = "HintView";
    private boolean isUninit;

    private SWLayerTextView mHintLabel;

    private AnimatorSet mHintAnime;
    private enum ViewState {
        NORMAL,
        PLAY_HINT_ANIMATION
    }
    private ViewState mViewState = ViewState.NORMAL;

    private float[] mHintOpacity = {0, 0.01f, 0.05f, 0.1f, 0.18f, 0.26f, 0.35f, 0.45f, 0.55f,
            0.65f, 0.74f, 0.82f, 0.90f, 0.95f, 0.99f, 1.0f};

    public HintView(Context context) {
        super(context);
        init(context);
    }

    public HintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        isUninit =false;
    }

    /** @hide */
    public void cleanUp() {
        isUninit = true;
    }

    /** @hide */
    public void initView() {
//        mHintLabel = (SWLayerTextView) this.findViewById(
//                MyUtil.getIdFromRes(
//                        mContext,
//                        ReminderResWrap.ID_HINT_LABEL));
        mHintLabel = (SWLayerTextView) this.findViewById(R.id.hintlabel);
        if (mHintLabel == null) {
            MyLog.w(TAG, "mHintLabel: NULL");
        }
    }

    private UIHandler mUIHandler = new UIHandler();
    private static final int WHAT_UI_CHANGE_HINT = 1000;
    private class UIHandler extends Handler {
        public UIHandler() {
        }
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            switch(msg.what) {
                case WHAT_UI_CHANGE_HINT:
                    playHintAnimationDirect();
                    break;
            }
        }
    }

    /** @hide */
    public void showUnlockHint() {
        if (mHintLabel == null) {
            return;
        }
        if (mViewState == ViewState.NORMAL) {
            playHintAnimation();
        } else if (mViewState == ViewState.PLAY_HINT_ANIMATION) {
            changeHint();
        }
    }

    /** @hide */
    public void cancelUnlockHint() {
        MyLog.d(TAG, "cancelUnlockHint");
        cancelHintAnimation();
    }

    /** @hide */
    public void setNextUnlockHint(String hint) {
        if (mHintLabel == null) {
            return;
        }
        if (hint != null) {
            hint = HtcResUtil.isInAllCapsLocale(getContext())? hint.toUpperCase():hint;
        } else {
            hint = "";
        }
        mHintLabel.setText(hint);
    }

    private void changeHint() {
        MyUtil.removeMessage(mUIHandler, WHAT_UI_CHANGE_HINT);
        MyUtil.sendMessage(mUIHandler, WHAT_UI_CHANGE_HINT, 50);
    }

    /*
     *             play unlock hint when drag item other than MastHead.
     *
     *           Hint fade in     Hint show        Hint fade out
     *          |-----------|------------------|-----------|
     *              500ms           1.04s            400ms
     *                           arrow animation
     *                      |------------------------------|
     *                              1.44s
     */
    private void playHintAnimation() {
        //don't play any animation after view un-init;
        if (isUninit) {
            return;
        }
        MyLog.d(TAG, "playHintAnima");
        cancelHintAnimation();
        mViewState = ViewState.PLAY_HINT_ANIMATION;

        // Hint Fade-in
        Animator defaultHintFadeIn = getHintFadeInAnimation();
        // Hint Show
        Animator defaultHintShow = FadeAnimator.ofFloat(
                this /*mHintView*/,
                "myAlpha",
                Const.UNLOCK_HINT_FADEIN_ALPHA,
                Const.UNLOCK_HINT_FADEIN_ALPHA);
        if (defaultHintShow != null) {
            defaultHintShow.setDuration(Const.UNLOCK_HINT_SHOW_TIME);
        }
        // Hint Fade-out
        Animator defaultHintFadeOut = FadeAnimator.ofFloat(
                this /*mHintView*/,
                "myAlpha",
                Const.UNLOCK_HINT_FADEIN_ALPHA,
                Const.UNLOCK_HINT_FADEOUT_ALPHA);
        if (defaultHintFadeOut != null) {
            defaultHintFadeOut.setDuration(Const.UNLOCK_HINT_FADEOUT_TIME);
        }
        // Hint Animation Set
        AnimatorSet hintviewAnime = new AnimatorSet();
        if (hintviewAnime != null) {
            if (defaultHintFadeIn != null && defaultHintShow != null) {
                updateHintVisible();
                hintviewAnime.play(defaultHintFadeIn).before(defaultHintShow);
            }
            if (defaultHintFadeOut != null && defaultHintShow != null) {
                hintviewAnime.play(defaultHintFadeOut).after(defaultHintShow);
            }
            mHintAnime = new AnimatorSet();
            if (mHintAnime != null) {
                mHintAnime.play(hintviewAnime);
                mHintAnime.start();
            }
        }
    }

    /*            play unlock hint when unlock animation playing.
     *
     *               Hint show        Hint fade out
     *          |------------------|-----------|
     *                 1.04s            400ms
     *                  arrow animation
     *          |------------------------------|
     *                      1.44s
     */
    private void playHintAnimationDirect() {
        //don't play any animation after view un-init;
        if (isUninit) {
            return;
        }
        MyLog.d(TAG, "playHintAnimationDirect");
        cancelHintAnimation();
        mViewState = ViewState.PLAY_HINT_ANIMATION;

        // Directly Show Hint
        Animator defaultHintShow = FadeAnimator.ofFloat(
                this /*mHintView*/,
                "myAlpha",
                Const.UNLOCK_HINT_FADEIN_ALPHA,
                Const.UNLOCK_HINT_FADEIN_ALPHA);
        if (defaultHintShow != null) {
            defaultHintShow.setDuration(Const.UNLOCK_HINT_SHOW_TIME);
        }
        // Hint Fade-out
        Animator defaultHintFadeOut = FadeAnimator.ofFloat(
                this /*mHintView*/,
                "myAlpha",
                Const.UNLOCK_HINT_FADEIN_ALPHA,
                Const.UNLOCK_HINT_FADEOUT_ALPHA);
        if (defaultHintFadeOut != null) {
            defaultHintFadeOut.setDuration(Const.UNLOCK_HINT_FADEOUT_TIME);
        }
        // Hint Animation Set
        AnimatorSet hintviewAnime = new AnimatorSet();
        updateHintVisible();
        if (hintviewAnime != null) {
            hintviewAnime.play(defaultHintShow);
            hintviewAnime.play(defaultHintFadeOut).after(defaultHintShow);
            mHintAnime = new AnimatorSet();
            if (mHintAnime != null) {
                mHintAnime.play(hintviewAnime);
                mHintAnime.start();
            }
        }
    }

    private void cancelHintAnimation() {
        if (mViewState != ViewState.NORMAL) {
            MyLog.d(TAG, "cancelHintAnima: " + mViewState);
            MyUtil.removeMessage(mUIHandler, WHAT_UI_CHANGE_HINT);
            mViewState = ViewState.NORMAL;
            if (mHintAnime != null) {
                mHintAnime.cancel();
            }
            updateHintVisible();
        }
    }

    private void updateHintVisible() {
        MyLog.d(TAG, "updHintVisible: " + mViewState);
        if (mViewState == ViewState.NORMAL) {
            this.setVisibility(View.INVISIBLE);
        } else {
            this.setVisibility(View.VISIBLE);
        }
    }

    /*
     * play when mast drag back or other drag view drag back or clicked
     */
    private ObjectAnimator getHintFadeInAnimation() {
        ObjectAnimator fadeInAnime = getFadeAnimation(this, mHintOpacity, Const.UNLOCK_HINT_FADEIN_TIME);
        return fadeInAnime;
    }

    private ObjectAnimator getFadeAnimation(View tartgetView, float[] fadeAnime, int duration) {
        if (tartgetView == null || fadeAnime == null || fadeAnime.length == 0) {
            return null;
        }
        String keyAlpha = "myAlpha";
        ObjectAnimator fadeOutAnime =
                FadeAnimator.ofFloat(tartgetView, keyAlpha, fadeAnime);
        if (fadeOutAnime != null) {
            fadeOutAnime.setDuration(duration);
        }
        return fadeOutAnime;
    }
}
