/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.lib1.cc.tabbar.test;

import com.htc.lib1.cc.view.viewpager.HtcPagerFragment;
import com.htc.lib1.cc.tabbar.activityhelper.HtcPagerFragmentAutActivity;
import com.htc.test.HtcActivityTestCaseBase;

public class TabReorderFragmentTest extends HtcActivityTestCaseBase {

    public TabReorderFragmentTest() {
        super(HtcPagerFragmentAutActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testStartEditing() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcPagerFragment htcPagerFragment = (HtcPagerFragment) getActivity().getFragmentManager().findFragmentByTag("Pager1");
                htcPagerFragment.startEditing();
            }
        });
        mSolo.sleep(2000);
        mSolo.goBack();
        mSolo.sleep(2000);
    }

    public void testStartEditing_Done() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcPagerFragment htcPagerFragment = (HtcPagerFragment) getActivity().getFragmentManager().findFragmentByTag("Pager1");
                htcPagerFragment.startEditing();
            }
        });
        mSolo.sleep(2000);
        mSolo.clickInList(2);
        mSolo.clickOnButton(1);
        mSolo.sleep(2000);
    }

    public void testStartEditing_Cancel() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcPagerFragment htcPagerFragment = (HtcPagerFragment) getActivity().getFragmentManager().findFragmentByTag("Pager1");
                htcPagerFragment.startEditing();
            }
        });
        mSolo.sleep(2000);
        mSolo.clickOnButton(0);
        mSolo.sleep(2000);
    }

    public void testStopEditing() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcPagerFragment htcPagerFragment = (HtcPagerFragment) getActivity().getFragmentManager().findFragmentByTag("Pager1");
                htcPagerFragment.startEditing();
            }
        });
        mSolo.sleep(2000);

        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                HtcPagerFragment htcPagerFragment = (HtcPagerFragment) getActivity().getFragmentManager().findFragmentByTag("Pager1");
                htcPagerFragment.stopEditing();
            }
        });

        mSolo.sleep(2000);
    }

}
