package com.htc.lib1.theme.test.navi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.htc.lib1.theme.test.R;

public class TestNaviMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_navi_main);

        final ViewGroup vp1 = (ViewGroup) findViewById(R.id.group1);
        final ViewGroup vp2 = (ViewGroup) findViewById(R.id.group2);
        final ViewGroup vp3 = (ViewGroup) findViewById(R.id.group3);
        final ViewGroup vp4 = (ViewGroup) findViewById(R.id.group4);
        final ViewGroup vp5 = (ViewGroup) findViewById(R.id.group5);


        Button button = (Button) findViewById(R.id.startact);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(TestNaviMain.this, TestNaviDisplay.class);
                CheckBox cb = findCheckBox(vp1);
                if (cb.isChecked()) {
                    intent.putExtra(TestNaviDisplay.FULL_SCREEN, true);
                } else {
                    intent.putExtra(TestNaviDisplay.FULL_SCREEN, false);
                }


                cb = findCheckBox(vp2);
                if (cb.isChecked()) {
                    intent.putExtra(TestNaviDisplay.NAVI_COLOR, TestNaviDisplay.NAVIGATION_BAR_TRANSPARENT);
                }

                cb = findCheckBox(vp3);
                if (cb.isChecked()) {
                    intent.putExtra(TestNaviDisplay.NAVI_COLOR, TestNaviDisplay.NAVIGATION_BAR_TRANSLUCENT);
                }

                cb = findCheckBox(vp4);
                if (cb.isChecked()) {
                    intent.putExtra(TestNaviDisplay.NAVI_COLOR, TestNaviDisplay.NAVIGATION_BAR_YELLOW);
                }

                cb = findCheckBox(vp5);
                if (cb.isChecked()) {
                    intent.putExtra(TestNaviDisplay.NAVI_COLOR, TestNaviDisplay.NAVIGATION_BAR_ACC);
                }

                TestNaviMain.this.startActivity(intent);
            }
        });
    }

    private CheckBox findCheckBox(ViewGroup vp) {
        int count = vp.getChildCount();
        for(int i = 0; i < count; i ++) {
            if (vp.getChildAt(i) instanceof CheckBox)
                return (CheckBox) vp.getChildAt(i);
        }
        return null;
    }
}
