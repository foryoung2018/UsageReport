
package com.htc.lib1.cc.actionbar.test;

import android.view.View;

import com.htc.lib1.cc.widget.ActionBarContainer;

public class ActionBarTestUtil {

    public static void addLeftViewCompat(ActionBarContainer actionBarContainer, View leftView) {
        actionBarContainer.addStartView(leftView);
    }

    public static void addRightViewCompat(ActionBarContainer actionBarContainer, View rightView) {
        actionBarContainer.addEndView(rightView);
    }

    public static void addCenterViewCompat(ActionBarContainer actionBarContainer, View centerView) {
        actionBarContainer.addCenterView(centerView);
    }
}
