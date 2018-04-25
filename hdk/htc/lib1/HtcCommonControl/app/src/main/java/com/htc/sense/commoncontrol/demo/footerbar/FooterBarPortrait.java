package com.htc.sense.commoncontrol.demo.footerbar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;


public class FooterBarPortrait extends CommonDemoActivityBase implements OnClickListener{
    private HtcFooter mFooterBar = null;
    private HtcFooter mFooterBar1 = null;
    private HtcFooter mFooterBar2 = null;
    private static final int M1 = Menu.FIRST;
    private static final int M2 = Menu.FIRST + 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
        setContentView(R.layout.footerbarportrait);
        ((HtcFooter)this.findViewById(R.id.nodiv)).setDividerEnabled(false);
        mFooterBar = (HtcFooter) findViewById(R.id.footerbar);
        mFooterBar1 = (HtcFooter) findViewById(R.id.getheighttest);
        mFooterBar2 = (HtcFooter) findViewById(R.id.nodiv);

        View v = findViewById(R.id.showhideicon);
        View dv = findViewById(R.id.showhidedarkicon);
        v.setOnClickListener(this);
        dv.setOnClickListener(this);
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
            mFooterBar.enableThumbMode(false);
            mFooterBar1.enableThumbMode(false);
            mFooterBar2.enableThumbMode(false);
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
                    mFooterBar.enableThumbMode(false);
                    mFooterBar1.enableThumbMode(false);
                    mFooterBar2.enableThumbMode(false);
                } else {
                    item.setChecked(true);
                    mFooterBar.enableThumbMode(true);
                    mFooterBar1.enableThumbMode(true);
                    mFooterBar2.enableThumbMode(true);
                }
                break;
            case M2:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mFooterBar.setBackgroundStyleMode(HtcFooter.STYLE_MODE_DARK);
                    mFooterBar1.setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
                } else {
                    item.setChecked(true);
                    mFooterBar.setBackgroundStyleMode(HtcFooter.STYLE_MODE_PURELIGHT);
                    mFooterBar1.setBackgroundStyleMode(HtcFooter.STYLE_MODE_PURELIGHT);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean show = true;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        show = !show;

        HtcFooterButton hfib = (HtcFooterButton)findViewById(android.R.id.icon);
        HtcFooterButton hfibd = (HtcFooterButton)findViewById(R.id.darkicon);
        if ( show ) {
            hfib.setImageResource(R.drawable.icon_btn_phone_light);
            hfibd.setImageResource(R.drawable.icon_btn_phone_dark);
        } else {
            hfib.setImageDrawable(null);
//            hfib.invalidate();
            hfibd.setImageDrawable(null);
//            hfibd.invalidate();
        }
    }

}
