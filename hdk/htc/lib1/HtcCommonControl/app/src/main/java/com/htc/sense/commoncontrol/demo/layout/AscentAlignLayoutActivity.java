
package com.htc.sense.commoncontrol.demo.layout;

import android.graphics.Rect;
import android.os.Bundle;
import android.widget.TextView;

import com.htc.lib1.cc.widget.AscentAlignLayout;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class AscentAlignLayoutActivity extends CommonDemoActivityBase {
    private Rect mBounds = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ascentalignlayout);
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
