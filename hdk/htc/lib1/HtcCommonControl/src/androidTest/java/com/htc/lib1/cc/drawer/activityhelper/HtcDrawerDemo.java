
package com.htc.lib1.cc.drawer.activityhelper;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcDrawer;
import com.htc.lib1.cc.widget.HtcDrawer.Mode;

public class HtcDrawerDemo extends ActivityBase {

    private RelativeLayout mContainer;

    public HtcDrawer mDrawer;

    public static final int MODE_TOP = 0;

    public static final int MODE_LEFT = 1;

    public static final int MODE_RIGHT = 2;

    public static final int MODE_BOTTOM = 3;

    public static final String MODE = "mode";

    public static final String IS_INIT_OPEN = "isInitOpen";

    public static final String IS_NEED_LINKED_VIEW = "isNeedLinkedView";

    public static final int DURATION = 500;

    public static final int TIMES_OF_BAR = 3;

    private static final String TRANSLATION_TYPE_Y = "translationY";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.htcdrawer_main);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        final Intent intent = getIntent();
        if (null != intent) {
            initViews(intent.getIntExtra(MODE, MODE_BOTTOM),
                    intent.getBooleanExtra(IS_INIT_OPEN, false),
                    intent.getBooleanExtra(IS_NEED_LINKED_VIEW, false));
        }
    }

    private View createContentView(int widthContent, int heightContent, int orientation) {
        final LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(orientation);

        final ImageView iv = new ImageView(this);
        iv.setImageResource(android.R.color.white);
        iv.setImageAlpha(50);

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthContent,
                heightContent);
        ll.addView(iv, lp);

        return ll;
    }

    private void setDrawer(int widthDrawer, int heightDrawer, Mode mode, boolean isInitOpen,
            int alignType, int widthContent, int heightContent, int orientation,
            boolean isNeedLinkedView) {
        mDrawer.setMode(mode);
        mDrawer.setContentView(createContentView(widthContent, heightContent, orientation));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                widthContent, heightContent);
        lp.addRule(alignType);

        if (isNeedLinkedView) {
            final ImageView linkedView = new ImageView(this);
            linkedView.setImageResource(android.R.color.holo_orange_light);
            linkedView.setImageAlpha(50);
            linkedView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                        int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    final int height = linkedView.getHeight();
                    if (height > 0) {
                        linkedView.setTranslationY(-mDrawer.getBarSize());
                    }
                }
            });

            mDrawer.setOnBarClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAnimator drawerAnimatorTranslation;
                    if (mDrawer.isOpen()) {
                        drawerAnimatorTranslation = ObjectAnimator.ofFloat(
                                linkedView,
                                TRANSLATION_TYPE_Y,
                                -mDrawer.getBarSize());
                    } else {
                        drawerAnimatorTranslation = ObjectAnimator.ofFloat(
                                linkedView,
                                TRANSLATION_TYPE_Y,
                                linkedView.getHeight());
                    }
                    drawerAnimatorTranslation.setDuration(DURATION);
                    drawerAnimatorTranslation.start();
                }

            });
            mContainer.addView(linkedView, lp);
        }

        lp = new RelativeLayout.LayoutParams(
                widthDrawer, heightDrawer);
        lp.addRule(alignType);
        mContainer.addView(mDrawer, lp);
    }

    private void initViews(int mode, boolean isInitOpen, boolean isNeedLinkedView) {
        mDrawer = new HtcDrawer(this);
        mDrawer.setIsInitOpen(isInitOpen);
        mDrawer.setDuration(DURATION);
        final int size = mDrawer.getBarSize() * TIMES_OF_BAR;

        switch (mode) {
            case MODE_TOP:
                setDrawer(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        Mode.TOP, isInitOpen,
                        RelativeLayout.ALIGN_PARENT_TOP,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        size, LinearLayout.HORIZONTAL, isNeedLinkedView);
                break;
            case MODE_LEFT:
                setDrawer(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        Mode.LEFT, isInitOpen,
                        RelativeLayout.ALIGN_PARENT_LEFT,
                        size,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.VERTICAL, isNeedLinkedView);
                break;
            case MODE_RIGHT:
                setDrawer(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        Mode.RIGHT, isInitOpen,
                        RelativeLayout.ALIGN_PARENT_RIGHT,
                        size,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.VERTICAL, isNeedLinkedView);
                break;
            case MODE_BOTTOM:
            default:
                setDrawer(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        Mode.BOTTOM, isInitOpen,
                        RelativeLayout.ALIGN_PARENT_BOTTOM,
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        size, LinearLayout.HORIZONTAL, isNeedLinkedView);
                break;
        }
    }
}
