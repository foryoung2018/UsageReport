
package com.htc.sense.commoncontrol.demo.footerbar;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase2;

public class FooterBarDemoApActivity extends CommonDemoActivityBase2 {

    @Override
    protected Class[] getActivityList() {
        return new Class[] {
                FooterBarPortrait.class, FooterbarSpeical.class, FooterbarOverlay.class, FooterBarLandscape.class
        };
    }
}
