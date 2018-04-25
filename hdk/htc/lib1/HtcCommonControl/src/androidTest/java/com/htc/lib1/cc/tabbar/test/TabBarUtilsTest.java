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

import android.test.ActivityInstrumentationTestCase2;

import com.htc.lib1.cc.view.tabbar.TabBarUtils;
import com.htc.lib1.cc.tabbar.activityhelper.TabBarAutActivity;

public class TabBarUtilsTest extends ActivityInstrumentationTestCase2<TabBarAutActivity> {

    public TabBarUtilsTest() {
        super(TabBarAutActivity.class);
    }

    public void testValue() {
        TabBarUtils tabBarUtils = new TabBarUtils();
        TabBarUtils.value(getInstrumentation().getTargetContext(), 100, 200);
    }

}
