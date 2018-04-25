
package com.htc.sense.commoncontrol.demo.footerbar;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.lib1.cc.widget.HtcFooterIconButton;
import com.htc.lib1.cc.widget.HtcFooterTextButton;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class FooterBarLandscape extends CommonDemoActivityBase implements OnClickListener,
        OnCheckedChangeListener {

    private ActionBarExt actionBarExt = null;
    private RadioButton lightRb, darkRb, transparentRb, darkbkgRb, mediumbkgRb, lightbkgRb;
    private RadioGroup rgroup, bkgrgroup;
    private HtcFooter fb = null;
    private HtcFooterButton fib = null;
    private HtcFooterButton ftb = null;
    private HtcFooterButton fbb = null;
    private boolean showIcon = true;
    private static final int M1 = Menu.FIRST;
    private static final int M2 = Menu.FIRST + 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footerbarlandscape);
        setupFooterButton();
        setupDemoSelection();
        fb = (HtcFooter) findViewById(R.id.footerbar);
    }

    private boolean isHorizontal() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isHorizontal()) {
            fb.enableThumbMode(false);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem thumbModeItem = menu.add(0, M1, Menu.NONE, "EnableThumbMode");
        MenuItem pureModeItem = menu.add(0, M2, Menu.NONE, "EnablePurebMode");
        thumbModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        pureModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        thumbModeItem.setCheckable(true);
        pureModeItem.setCheckable(true);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case M1:
                if (item.isChecked()) {
                    item.setChecked(false);
                    fb.enableThumbMode(false);
                } else {
                    item.setChecked(true);
                    fb.enableThumbMode(true);
                }
                break;
            case M2:
                if (item.isChecked()) {
                    item.setChecked(false);
                    fb.setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
                } else {
                    item.setChecked(true);
                    fb.setBackgroundStyleMode(HtcFooter.STYLE_MODE_PURELIGHT);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void setupDemoSelection() {
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(this);
        bkgrgroup = (RadioGroup) findViewById(R.id.bkgrgroup);
        bkgrgroup.setOnCheckedChangeListener(this);
    }

    private void setRadioButtonStyle(int styleId) {
        lightRb = (RadioButton) findViewById(R.id.lightrb);
        lightRb.setTextAppearance(this, styleId);
        darkRb = (RadioButton) findViewById(R.id.darkrb);
        darkRb.setTextAppearance(this, styleId);
        transparentRb = (RadioButton) findViewById(R.id.transparentrb);
        transparentRb.setTextAppearance(this, styleId);
        lightbkgRb = (RadioButton) findViewById(R.id.lightbkgrb);
        lightbkgRb.setTextAppearance(this, styleId);
        mediumbkgRb = (RadioButton) findViewById(R.id.mediumbkgrb);
        mediumbkgRb.setTextAppearance(this, styleId);
        darkbkgRb = (RadioButton) findViewById(R.id.darkbkgrb);
        darkbkgRb.setTextAppearance(this, styleId);
    }

    private void setupFooterButton() {
        fb = (HtcFooter) findViewById(R.id.footerbar);
        fib = (HtcFooterButton) findViewById(R.id.footericonbutton);
        ftb = (HtcFooterButton) findViewById(R.id.showhideicon);
        fbb = (HtcFooterButton) findViewById(R.id.footerbutton);
        ftb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        showIcon = !showIcon;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        int gpid = group.getId();
        // Log.e("Bill Test FooterBar", Integer.toString(checkedId));
        if (R.id.rgroup == gpid) {
            switch (checkedId) {
                case R.id.darkrb:
                    fb.setBackgroundStyleMode(HtcFooter.STYLE_MODE_DARK);
                    break;
                case R.id.lightrb:
                    fb.setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
                    break;
                case R.id.transparentrb:
                    fb.setBackgroundStyleMode(HtcFooter.STYLE_MODE_TRANSPARENT);
                    break;
                default:
                    break;
            }
        } else {
            switch (checkedId) {
                case R.id.darkbkgrb:
                    getWindow().setBackgroundDrawableResource(android.R.color.darker_gray);
                    setRadioButtonStyle(R.style.b_button_primary_m);
                    break;
                case R.id.mediumbkgrb:
                    getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
                    setRadioButtonStyle(R.style.button_primary_m);
                    break;
                case R.id.lightbkgrb:
                    getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
                    setRadioButtonStyle(R.style.button_primary_m);
                    break;
                default:
                    break;
            }
        }
    }

}
