
package com.htc.lib1.cc.checkablebutton.activityhelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;

public class HtcButtonDemo extends ActivityBase {
    private ListView mLv;
    ArrayAdapter<String> mLvSrc;
    private String[] mStrings = new String[] {
            "HtcCompoundButton Series", "HtcButton Series", "HtcImageButton Series", "HtcIconButton", "HtcRimButton"
    };
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int themeid = getIntent().getIntExtra("theme", R.style.HtcDeviceDefault);
        this.setTheme(themeid);
        setContentView(R.layout.htcbutton_demos_main);

        mLv = (ListView) findViewById(R.id.main_lv);
        mLvSrc = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);
        mLv.setAdapter(mLvSrc);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                intent = new Intent();
                intent.putExtra("theme", themeid);
                switch (pos) {
                    case 0:
                        // CompoundButton
                        intent.setClass(HtcButtonDemo.this, HtcCompoundButtons.class);
                        break;

                    default:
                        intent = null;
                }

                if (intent != null)
                    startActivity(intent);
            }
        });
    }
}
