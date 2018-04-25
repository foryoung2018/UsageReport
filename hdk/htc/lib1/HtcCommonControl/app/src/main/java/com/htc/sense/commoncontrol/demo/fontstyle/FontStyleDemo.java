package com.htc.sense.commoncontrol.demo.fontstyle;

import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class FontStyleDemo extends CommonDemoActivityBase implements OnClickListener,
        OnCheckedChangeListener {
    Hashtable mButtonLayoutMap = new Hashtable(32) {
        {
            put(R.id.b_button_primary, R.layout.font_b_button_primary);
            put(R.id.b_separator_primary, R.layout.font_b_separator_primary);
            put(R.id.b_separator_secondary, R.layout.font_b_separator_secondary);
            put(R.id.button_primary, R.layout.font_button_primary);
            put(R.id.darklist_primary, R.layout.font_darklist_primary);
            put(R.id.darklist_secondary, R.layout.font_darklist_secondary);
            put(R.id.info_primary, R.layout.font_info_primary);
            put(R.id.input_default, R.layout.font_input_default);
            put(R.id.list_body, R.layout.font_list_body);
            put(R.id.list_primary, R.layout.font_list_primary);
            put(R.id.list_secondary, R.layout.font_list_secondary);
            put(R.id.separator_primary, R.layout.font_separator_primary);
            put(R.id.separator_secondary, R.layout.font_separator_secondary);
            put(R.id.title_primary, R.layout.font_title_primary);
            put(R.id.title_secondary, R.layout.font_title_secondary);
            put(R.id.automotive, R.layout.font_automotive);
            put(R.id.others, R.layout.font_others);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int nLayoutID = getIntent().getIntExtra("layoutid",
                R.layout.font_main);

        setContentView(nLayoutID);

        hookOnClickListener();
    }

    private void hookOnClickListener() {
        for (Enumeration allViewID = mButtonLayoutMap.keys(); allViewID
                .hasMoreElements();) {
            int id = (Integer) allViewID.nextElement();
            Button b = (Button) findViewById(id);
            if (null != b)
                b.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mButtonLayoutMap.containsKey(v.getId())) {
            int layoutid = (Integer) mButtonLayoutMap.get(v.getId());
            Intent intent = new Intent(getApplicationContext(),
                    FontStyleDemo.class);
            intent.putExtra("layoutid", layoutid);
            startActivity(intent);
        } else {
        }
    }

    private void setEditTextEnable(int id, boolean enabled) {
        EditText et = (EditText) findViewById(id);
        if (null == et)
            return;

        et.setEnabled(enabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        if (buttonView.getId() == R.id.toggleButton1) {
            View cv = getWindow().findViewById(android.R.id.content);
            setEditTextEnable(R.id.input_default_xl, isChecked);
            setEditTextEnable(R.id.input_default_l, isChecked);
            setEditTextEnable(R.id.input_default_m, isChecked);
            setEditTextEnable(R.id.input_default_s, isChecked);
            setEditTextEnable(R.id.input_default_xs, isChecked);
            setEditTextEnable(R.id.hint_input_default_xl, isChecked);
            setEditTextEnable(R.id.hint_input_default_l, isChecked);
            setEditTextEnable(R.id.hint_input_default_m, isChecked);
            setEditTextEnable(R.id.hint_input_default_s, isChecked);
            setEditTextEnable(R.id.hint_input_default_xs, isChecked);
        } else if (buttonView.getId() == R.id.toggleButton2) {
            getWindow().findViewById(android.R.id.content).setBackgroundColor(
                    (isChecked) ? 0xff000000 : 0xffffffff);
        }
    }
}