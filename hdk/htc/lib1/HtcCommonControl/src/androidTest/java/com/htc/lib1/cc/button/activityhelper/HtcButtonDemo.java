
package com.htc.lib1.cc.button.activityhelper;

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
                        //CompoundButton
                        break;
                    case 1:
                        //Button
                        intent.setClass(HtcButtonDemo.this, HtcButtons.class);
                        break;
                    case 2:
                        //ImageButton
                        break;
                    case 3:
                        //IconButton
                        intent.setClass(HtcButtonDemo.this, HtcIconButtonDemo.class);
                        break;
                    case 4:
                        //RimButton
                        intent.setClass(HtcButtonDemo.this, HtcRimButtonDemo.class);
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
