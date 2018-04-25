
package com.htc.sense.commoncontrol.demo.listview;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase2;

public class HtcListViewMainActivity extends CommonDemoActivityBase2 {

    @Override
    protected Class[] getActivityList() {
        return new Class[] {
                HtcListViewDemo.class, PullToRefreshActivity.class, HtcListViewFastScrollerDemo.class, HtcListViewReorderDemo.class, HtcListViewExpandableDemo.class,
                MoreExpandableHtcListViewDemo.class
        };
    }

}
