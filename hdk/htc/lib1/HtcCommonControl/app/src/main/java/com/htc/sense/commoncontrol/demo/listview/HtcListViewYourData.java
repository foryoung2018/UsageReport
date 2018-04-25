
package com.htc.sense.commoncontrol.demo.listview;

import com.htc.lib1.cc.widget.MoreExpandableItemInfo;

public class HtcListViewYourData extends MoreExpandableItemInfo {

    private String mData = null;

    public HtcListViewYourData(long id, boolean isGroup, String data) {
        super(id, isGroup);
        mData = data;
        // TODO Auto-generated constructor stub
    }

    public String getData() {
        return mData;
    }
}
