
package com.htc.lib1.cc.htclistitem.test;

import com.htc.lib1.cc.htclistitem.activityhelper.ListItemActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.test.util.ScreenShotUtil;

import junit.framework.TestCase;

public class ListItemTestCase extends com.htc.lib1.cc.htclistitem.test.HtcListItemTestCase {

    public ListItemTestCase() {
        super(ListItemActivity.class);
    }

    @Override
    protected void scrollToAndScreenShotTest(final int itemIndex, TestCase testcase, final boolean isAutomotiveMode, boolean isResetBubbleCount) {
        scrollTo(itemIndex, isAutomotiveMode);
        final ListItem item = (ListItem) getActivity().findViewById(android.R.id.list);
        if (isResetBubbleCount) {
            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) item
                                .findViewById(R.id.bubble);
                        bubble.setBubbleCount(0);

                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, item, this);
    }

    @Override
    protected void leftIndentAssert() {
        int M1 = (int) getActivity().getResources().getDimension(R.dimen.margin_l);
        int M2 = (int) getActivity().getResources().getDimension(R.dimen.margin_m);
        final ListItem item = (ListItem) getActivity().findViewById(android.R.id.list);
        HtcListItem2LineText text = (HtcListItem2LineText) item.findViewById(R.id.text1);
        assertEquals(M1 + M2 * 3, text.getLeft());
    }

    @Override
    public void testImproveCoverage() {
        initActivity();
    }
}
