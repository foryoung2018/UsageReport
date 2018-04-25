
package com.htc.lib1.cc.layout.activityhelper;

import android.os.Bundle;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcListItemSerialNumber;
import com.htc.lib1.cc.widget.WeekLayout;

public class WeekLayoutActivity extends ActivityBase {
    private int[] mSecondaryIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.week_layout);
        mSecondaryIds = new int[] {
                R.id.secondary1, R.id.secondary2, R.id.secondary3, R.id.secondary4, R.id.secondary5, R.id.secondary6, R.id.secondary7
        };

        WeekLayout weekLayoutDefault = (WeekLayout) findViewById(R.id.weeklayout_default);
        ((TextView) weekLayoutDefault.findViewById(android.R.id.primary)).setText("Wake Up");
        setSecondaryTextDisable(weekLayoutDefault, new int[] {
                0, 3, 6
        });

        WeekLayout weekLayoutCustom = (WeekLayout) findViewById(R.id.weeklayout_custom);
        ((TextView) weekLayoutCustom.findViewById(android.R.id.primary)).setText("Custom");
        weekLayoutCustom.setSecondaryText(new String[] {
                "A", "B", "C", "D", "E", "F", "G"
        });
        setSecondaryTextDisable(weekLayoutCustom, new int[] {
                0, 6
        });
        weekLayoutCustom.setSecondaryTextEndMargin(50);

        WeekLayout weekLayoutInListItem = (WeekLayout) findViewById(R.id.weeklayout_listitem);
        ((TextView) weekLayoutInListItem.findViewById(android.R.id.primary)).setText("Description");
        setSecondaryTextDisable(weekLayoutInListItem, new int[] {
                0, 3, 6
        });
        HtcListItemSerialNumber number = (HtcListItemSerialNumber) findViewById(R.id.number);
        number.setNumber(1);
        number.setDarkMode(true);

    }

    private void setSecondaryTextDisable(WeekLayout weekLayout, int[] position) {

        if (position != null && position.length > 0) {
            final int length = position.length;
            for (int i = 0; i < length; i++) {
                if (position[i] >= 0 && position[i] < 7) {
                    ((TextView) weekLayout.findViewById(mSecondaryIds[position[i]])).setEnabled(false);
                }
            }
        }
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
