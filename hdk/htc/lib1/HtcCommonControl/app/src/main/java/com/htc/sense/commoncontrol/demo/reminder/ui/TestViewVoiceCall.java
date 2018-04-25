package com.htc.sense.commoncontrol.demo.reminder.ui;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
//WeatherColock-ChrisWang-00+[
//import com.htc.android.home.view.Masthead;
//WeatherColock-ChrisWang-00+]
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.drag.DragAnimation;
import com.htc.lib1.cc.widget.reminder.drag.DragAnimationBase;
import com.htc.lib1.cc.widget.reminder.drag.KeySplineInterpolator;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.sense.commoncontrol.demo.R;

public class TestViewVoiceCall extends ReminderView {

    private static final String TAG = "TestViewCall";

    private Context mContext;
    private Button mDecline1;
    private Button mAnswer1;
    private TextView mTextView11_1;
    private TextView mTextView12_1;
    private ImageView mImageView1_1;

    ReminderTile mTile1;
    ReminderTile mTile2;
//WeatherColock-ChrisWang-00+[
//    private Masthead mMasthead;
//WeatherColock-ChrisWang-00+]
    public TestViewVoiceCall(Context context) {
        super(context);
        onContractor(context);
    }

    public TestViewVoiceCall(Context context, AttributeSet attrs) {
        super(context, attrs);
        onContractor(context);
    }

    public TestViewVoiceCall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onContractor(context);
    }

    private void onContractor(Context context) {
        mContext = context;
        initView();
    }

    private void initView() {
        this.setFitsSystemWindows(true);
        initSingleTile();
//WeatherColock-ChrisWang-00+[
//        mMasthead = new Masthead(mContext);
//        mMasthead.setEnableTextSWLayer(true);   //suggest add this API for reduce memory usage
//        mMasthead.changeAnimationState(-1);  // Disable Animation
//        setMastheadOnTop(mMasthead);
//WeatherColock-ChrisWang-00+]
        updateUI();
    }

    private void initSingleTile() {
        // TODO: initial View.
        // setReminderTile(Layout ID, Index)
        // Index: 1 (1st tile) or 2 (2nd tile)
        // 1st
        mTile1 = setReminderTile(R.layout.specific_lockscreen_3_lines_with_action, 1);
        if (mTile1 == null) {
            mTile1.setButtonAccessibilityEnabled(true);
            mTile1.setDragAnimation(new DropDragAnimation());
        }
        // Disable Drag
        setTileDraggable(false, 1);
        // Buttons.
        Resources  res = (mContext != null)? mContext.getResources(): null;
        if (res != null) {
            mDecline1 = new Button(mTile1);
            if (mDecline1 != null) {
                mDecline1.setTitle(res.getString(R.string.lockscreen_incomingcall_decline));
                mDecline1.setIcon(res.getDrawable(R.drawable.icon_btn_lockscreen_cancel_dark_xl));
            }
            mAnswer1 = new Button(mTile1);
            if (mAnswer1 != null) {
                mAnswer1.setTitle(res.getString(R.string.lockscreen_incomingcall_answer));
                mAnswer1.setIcon(res.getDrawable(R.drawable.icon_btn_settings_dark_xl));
                mAnswer1.setHint("Pull icon to answer");
            }
        }
        // Tile UI.
        if (mTile1 != null) {
            mTextView11_1 = (TextView) mTile1.findViewById(R.id.text11);
            mTextView12_1 = (TextView) mTile1.findViewById(R.id.text2);
            mImageView1_1 = (ImageView) mTile1.findViewById(R.id.call_id);
        }
    }

    public void cleanUp() {
        super.cleanUp();
//WeatherColock-ChrisWang-00+[
//        mMasthead.stop();
//WeatherColock-ChrisWang-00+[
    }

    public void updateUI() {
        super.updateUI();
        // TODO: update UI
        setMessage();
        setPhoto();
    }

    private void setMessage() {
        String line1 = "1st Line 1";
        String line2 = "1st Line 2";
        if (mTextView11_1 != null) {
            setTitle(mTextView11_1, line1);
        }
        if (mTextView12_1 != null) {
            mTextView12_1.setText(line2);
        }
        if (mTile1 != null) {
            mTile1.resetStringForAccessibility();
            mTile1.addStringForAccessibility(line1);
            mTile1.addStringForAccessibility(line2);
        }
    }

    private void setPhoto() {
        if (mContext != null) {
            Resources res = mContext.getResources();
            if (res != null) {
                Drawable dPhone = res.getDrawable(R.drawable.people_icon_photo);
                if (mImageView1_1 != null) {
                    mImageView1_1.setImageDrawable(dPhone);
                }
            }
        }
    }

    public void onTileDrop(ReminderTile tile) {
        super.onTileDrop(tile);
        if (tile != null) {
            // TODO: do something when tile is drop.
            if (mCallback != null) {
                mCallback.onTileDrop();
            }
        }
    }

    public void onTileDropEnd(ReminderTile tile) {
        super.onTileDropEnd(tile);
        if (tile != null) {
            // TODO: do something when tile is Drop END.
            if (mCallback != null) {
                mCallback.onTileDropEnd();
            }
        }
    }

    public void onButtonDrop(Button button) {
        super.onButtonDrop(button);
        boolean isDecline = false;
        if (button != null) {
            // TODO: do something when button is drop.
            if (button == mDecline1) {
                isDecline = true;
                MyLog.i(TAG, "onButtonDrop: 1");
            } else if (button == mAnswer1) {
                MyLog.i(TAG, "onButtonDrop: 2");
                // Hide MastHeat and play_Incall_Tile_Animation
                this.setMastHeadVisibility(false);
                this.playIncomingcallAnimation(mTile1);
            }
            if (mCallback != null) {
                mCallback.onButtonDrop(isDecline);
            }
        }
    }

    public void onButtonDropEnd(Button button) {
        super.onButtonDropEnd(button);
        if (button != null) {
            boolean isDeclineCall = true;
            // TODO: do something when button is Drop END.
            if (button == mDecline1) {
                MyLog.i(TAG, "onButtonDropEnd: " + mDecline1);
            } else if (button == mAnswer1) {
                isDeclineCall = false;
                MyLog.i(TAG, "onButtonDropEnd: " + mAnswer1);
            }
            if (mCallback != null) {
                mCallback.onButtonDropEnd(isDeclineCall);
            }
        }
    }

    public int getButtonCount() {
        // TODO: define the button count you need.
        // Button Count: 2 or 4
        return 2;
    }

    public Button getButton(int index) {
        // TODO: return the button you define.
        // UI & Index :
        // | Dismiss1 | Setting1 | Dismiss2 | Setting2 |
        // |     0    |     1    |     2    |     3    |
        if (index == 0) {
            return mDecline1;
        } else if (index == 1) {
            return mAnswer1;
        }
        return null;
    }

    // TODO: Callback to Activity by yourself requirement.
    public interface Callback {
        void onTileDrop();
        void onTileDropEnd();
        void onButtonDrop(boolean isDeclineCall);
        void onButtonDropEnd(boolean isDeclineCall);
    };
    Callback mCallback;

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    /*
     * Define incall view drag limit.
     * @see com.htc.idlescreen.base.ui.reminder.ReminderView#getDragThreshold()
     */
    public int getDragThreshold() {
        int threshhold = 0;
        if (mContext == null) {
            return threshhold;
        }
        int top = 0;//MyUtil.getStatusbarHeight(mContext);
        int marginTop = 0;
        int mhHeight = 0;
        Resources res = null;
        res = mContext.getResources();
        if (res != null) {
            marginTop = res.getDimensionPixelSize(R.dimen.incoming_call_call_id_margin_top);
            //mhHeight = res.getDimensionPixelSize(R.dimen.masthead_minHeight);
        }
        threshhold = top + marginTop - mhHeight;

        //if is the dual phone, it need to consider the slot name title height to Translate animation distance.
        if(MyProjectSettings.isSupportDualPhone()) {
            if(res != null) {
                threshhold -= res.getDimensionPixelSize(R.dimen.incoming_call_slot_name_title_layout_height);
            }
        }
        return threshhold;
    }

    public static long UNLOCK_ANIMATION_SPEED = 4;
    /**
     *  Lock DragAnimation:
     *  Call id on lockscreen should move to same position with call id on phone.
     */
    private class DropDragAnimation extends DragAnimationBase {
        @Override
        public boolean playDropAnimation(View view, AnimationListener listener, Bundle extras) {
            View v = view;
            int top = 0;//MyUtil.getStatusbarHeight(mContext);
            int marginTop = mContext.getResources().getDimensionPixelSize(R.dimen.incoming_call_call_id_margin_top);
            AnimationListener l = listener;
            boolean playanimasuccess = false;
            if (v != null && l != null) {
                int position = extras.getInt(DragAnimation.KEY_DRAGVIEW_TOP);
                int translatedistance = marginTop+top-position;
                playanimasuccess = playIncallTileAnimation(v, l, translatedistance,
                        Math.abs(translatedistance)/(UNLOCK_ANIMATION_SPEED), extras);
            }
            return playanimasuccess;
        }
    }

    /**
     * play unlock animation until phone unlock
     */
    public boolean playIncallTileAnimation(View view, AnimationListener listener,
            int translatedistance, long animationduration, Bundle extras) {
        final View targetView = view;
        final AnimationListener animationLis = listener;
        if (targetView == null || animationLis == null || extras == null) {
            return false;
        }
        int origY = extras.getInt(DragAnimation.KEY_ORIGINAL_Y, 0);
        int viewY = extras.getInt(DragAnimation.KEY_DRAGVIEW_TOP, 0);
        PropertyValuesHolder pvhMoveY =
                PropertyValuesHolder.ofInt("moveY", viewY - origY, getDragThreshold() - origY);
        ObjectAnimator dropAnimator =
                ObjectAnimator.ofPropertyValuesHolder(targetView, pvhMoveY);
        if (dropAnimator == null) {
            return false;
        }
        dropAnimator.setDuration(animationduration);
        KeySplineInterpolator interpolator =
                new KeySplineInterpolator(0.34f, 0.74f, 0f, 1f);
        if (interpolator != null) {
            dropAnimator.setInterpolator(interpolator);
        }
        dropAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator != null && targetView != null) {
                    ViewParent parent = targetView.getParent();
                    if (parent == null) {
                        animator.cancel();
                    } else {
                        int moveY = (Integer) animator.getAnimatedValue("moveY");
                        targetView.scrollTo(0, -moveY);
                    }
                }
            }
        });
        Animator defaultFadeIn = ObjectAnimator.ofFloat(targetView, "alpha", 1.0f, 1.0f);
        defaultFadeIn.setDuration(3000); // TODO: playing the animation, to keep the screen until Phone UI is ready.
        defaultFadeIn.addListener(new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
                animationLis.onAnimationEnd(null);
                MyLog.d(TAG, "playIncallTileAnimation onAnimationCancel");
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                animationLis.onAnimationEnd(null);
                MyLog.d(TAG, "playIncallTileAnimation onAnimationEnd");
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
                animationLis.onAnimationRepeat(null);
            }
            @Override
            public void onAnimationStart(Animator animator) {
                animationLis.onAnimationStart(null);
                MyLog.d(TAG, "playIncallTileAnimation onAnimationStart");
            }
        });
        AnimatorSet unlockAnime = new AnimatorSet();
        unlockAnime.play(dropAnimator).before(defaultFadeIn);
        unlockAnime.start();
        return true;
    }

    private ObjectAnimator mCallidAnimator;
    public static final float MAINCONTAIN_FADE_OUT_START = 1.0f;
    public static final float MAINCONTAIN_FADE_OUT_END   = 0.0f;
    public void playIncomingcallAnimation(BaseTile aniTile) {
        /**
         * Tile (Upward Fly to the TOP of Call Id of Phone APP.)
         */
        final View curTileLayout;
        final View targetLayout;
        try {
            curTileLayout = (View) aniTile.getParent();
            targetLayout =  (View) curTileLayout.getParent();
        } catch (Exception e) {
            MyLog.e(TAG, "playIncomingcallAnimation E:" + e);
            return;
        }
        if (curTileLayout == null || targetLayout == null) {
            return;
        }
        // TODO: If dual phone, it needs to hide another view
        if (MyProjectSettings.isSupportDualPhone()) {
            //hideView(curTile, targetTile);
        }
        KeySplineInterpolator interpolator =
                new KeySplineInterpolator(0.34f, 0.74f, 0f, 1f);
        long delaytime = 0;
        Resources res = (mContext != null)? mContext.getResources():null;
        int marginTopCallId = (res != null)?
                res.getDimensionPixelSize(R.dimen.incoming_call_call_id_margin_top):0;
        // Because Tile's Parent is LinearLayout.
        // So, we can't getTop() from Tile directly.
        int topCurTile = curTileLayout.getTop();
        int mhHeight = 0;//(res != null)? res.getDimensionPixelSize(R.dimen.masthead_minHeight):0;
        int distance = topCurTile - (marginTopCallId - mhHeight);

        //if is the dual phone, it need to consider the slot name title height to Translate animation distance.
        if (res != null && MyProjectSettings.isSupportDualPhone()) {
            distance += res.getDimensionPixelSize(R.dimen.dualsim_incoming_call_slot_name_height);
        }

        int duration = getTranslateDuration(distance);
        MyLog.d(TAG, "playIncomingcallAnimation duration: " + duration
                + " distance: " + distance
                + " marginTopCallId: " + marginTopCallId
                + " topCurTile: " + topCurTile);
        // Tile (Upward Fly to the TOP of Call Id of Phone APP.)
        AnimatorListener callidListener = new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                MyLog.d(TAG, "playIncomingcallAnimation: Translate onAnimationEnd.");
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationStart(Animator animator) {
                MyLog.d(TAG, "playIncomingcallAnimation: Translate onAnimationStart.");
                boolean showing = curTileLayout.getVisibility() == View.VISIBLE;
                if (!showing) {
                    curTileLayout.setVisibility(View.VISIBLE);
                }
                //disableGC(true);
            }
        };
        int curScrollY = targetLayout.getScrollY();
        mCallidAnimator =
                getTranslateAnimator(targetLayout, interpolator,
                        0, 0, curScrollY, distance,
                        callidListener, duration, delaytime);
        if (mCallidAnimator != null) {
            mCallidAnimator.start();
        }
    }

    private ObjectAnimator getTranslateAnimator(final View view, Interpolator interpolator,
            int startX, int endX, int startY, int endY,
            AnimatorListener listener,int duration, long delayTime) {
        if (view == null || view.getParent() == null) {
            MyLog.w(TAG, "getTranslateAnimator Failed: Invaild ViewGroup.");
            return null;
        }
        MyLog.d(TAG, "getTranslateAnimator"
                + " startX: " + startX
                 + " endX: " + endX
                 + " startY: " + startY
                 + " endY: " + endY);
        PropertyValuesHolder pvhLeft =
                PropertyValuesHolder.ofInt("myScrollX", startX, endX);
        PropertyValuesHolder pvhTop =
                PropertyValuesHolder.ofInt("myScrollY", startY, endY);
        ObjectAnimator translateAnimator =
                ObjectAnimator.ofPropertyValuesHolder((Object)view, pvhLeft, pvhTop);
        if (translateAnimator == null) {
            return null;
        }
        translateAnimator.setDuration(duration);
        translateAnimator.setStartDelay(delayTime);
        if (interpolator != null) {
            translateAnimator.setInterpolator(interpolator);
        }
        if (listener != null) {
            translateAnimator.addListener(listener);
        }
        translateAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator != null /*&& !mCancelIncomingcallAnimator*/) {
                    ViewParent parent = view.getParent();
                    if (parent == null) {
                        animator.cancel();
                    } else {
                        int scrollX = (Integer) animator.getAnimatedValue("myScrollX");
                        int scrollY = (Integer) animator.getAnimatedValue("myScrollY");
                        view.scrollTo(scrollX, scrollY);
                    }
                }
            }
        });
        return translateAnimator;
    }

    public static final int MAINCONTAIN_DURATION_TRANSLATE    = 330;  // 10 * 33 = 330
    public static int UNLOCK_ANIMATION_MAX_DURATION = 270;
    public static int  UNLOCK_ANIMATION_MIN_DURATION = 180;
    public int getTranslateDuration(int distance) {
        int duration = MAINCONTAIN_DURATION_TRANSLATE;
        if (distance != 0) {
            duration = (int)((long)Math.abs(distance)/(UNLOCK_ANIMATION_SPEED));
        }
        if (duration > UNLOCK_ANIMATION_MAX_DURATION) {
            duration = UNLOCK_ANIMATION_MAX_DURATION;
        }    else if (duration < UNLOCK_ANIMATION_MIN_DURATION) {
            duration = UNLOCK_ANIMATION_MIN_DURATION;
        }
        MyLog.d(TAG, "getTranslateDuration"
                + " distance: " + distance
                + " duration: " + duration);
        return duration;
    }
}
