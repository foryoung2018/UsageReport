
package com.htc.lib1.cc.layout.activityhelper;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.AscentAlignLayout;

public class AscentAlignLayoutActivity extends ActivityBase {
    private Rect mBounds = new Rect();
    public static final String LAYOUT_ID = "layoutId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (null == i) {
            return;
        }

        int layoutId = i.getIntExtra(LAYOUT_ID, R.layout.ascentalign_1basic2alignbasic);
        setContentView(layoutId);

        if (layoutId == R.layout.ascentalign_2viewgroup) {
            AscentAlignLayout ascentAlignLayout = (AscentAlignLayout) findViewById(R.id.viewgroup_viewgroup);
            ascentAlignLayout.setTextAscentCallBack(new AscentAlignLayout.TextAscentCallBack() {

                @Override
                public int getTextAscent(TextView tv) {
                    if (null == tv) {
                        return -1;
                    }

                    if (null == mBounds) {
                        mBounds = new Rect();
                    }

                    tv.getLayout().getPaint().getTextBounds("l", 0, 1, mBounds);
                    return mBounds.height();
                }
            });
        }
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
