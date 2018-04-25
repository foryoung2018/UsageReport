
package com.htc.sense.commoncontrol.demo.htcdrawer;

import com.htc.lib1.cc.widget.HtcDrawer;
import com.htc.sense.commoncontrol.demo.R;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.FrameLayout;

public class HtcDrawerDemo extends Activity {

    private FrameLayout mContainer;

    private HtcDrawer mDrawer;

    private ObjectAnimator mLinkedViewAnimator;

    private static final String TRANSLATION_TYPE_Y = "translationY";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.htcdrawer_main);
        mContainer = (FrameLayout) findViewById(R.id.container);

        initDrawer();
        initLinkedView();

    }

    private void initDrawer() {
        mDrawer = new HtcDrawer(this);
        mDrawer.setContentView(getLayoutInflater().inflate(R.layout.htcdrawer_content_view,
                mDrawer,
                false));
        mContainer.addView(mDrawer);
    }

    private void initLinkedView() {
        final View linkedView = getLayoutInflater().inflate(R.layout.htcdrawer_linked_view,
                mContainer,
                false);

        linkedView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft,
                    int oldTop, int oldRight, int oldBottom) {
                linkedView.setTranslationY(0);
            }
        });

        mDrawer.setOnBarClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLinkedViewAnimator && mLinkedViewAnimator.isRunning()) {
                    mLinkedViewAnimator.end();
                }

                if ((mDrawer.isOpen() && mDrawer.isToggleFinish())
                        || (!mDrawer.isOpen() && !mDrawer.isToggleFinish())) {
                    mLinkedViewAnimator = ObjectAnimator.ofFloat(
                            linkedView,
                            TRANSLATION_TYPE_Y,
                            0);
                } else {
                    mLinkedViewAnimator = ObjectAnimator.ofFloat(
                            linkedView,
                            TRANSLATION_TYPE_Y,
                            linkedView.getHeight() + mDrawer.getBarSize());
                }

                mLinkedViewAnimator.setDuration(mDrawer.getDuration());
                mLinkedViewAnimator.start();
            }
        });

        mContainer.addView(linkedView, linkedView.getLayoutParams());
    }
}
