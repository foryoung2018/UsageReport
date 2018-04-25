package com.htc.lib1.cc.fontstyle.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;

public class MainActivity extends ActivityBase {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String styleName = intent.getStringExtra("styleName");
        int styleId = intent.getIntExtra("styleId", 0);

        if (null != styleName && 0 != styleId) {
            tv = (TextView) findViewById(R.id.tv);
            tv.setTextAppearance(this, styleId);
            tv.setText(styleName);
        }
    }

    @Override
    protected boolean isInitCategory() {
        return false;
    }
}
