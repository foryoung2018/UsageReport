
package com.htc.sense.commoncontrol.demo.htclistitem;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase2;

public class DemoListActivity extends CommonDemoActivityBase2 {

    @Override
    protected Class[] getActivityList() {
        return new Class[] {
                HtcListItemActivity1.class, HtcListItemActivity3.class,
                HtcListItemActivity12.class, HtcListItemActivity13.class, HtcListItemSeparatorDemo.class
        };
    }

}
