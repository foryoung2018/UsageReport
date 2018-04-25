package com.htc.sense.commoncontrol.demo.reminder.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.TextView;
//WeatherColock-ChrisWang-00+[
//import com.htc.android.home.view.Masthead;
//WeatherColock-ChrisWang-00+]
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.sense.commoncontrol.demo.R;

public class TestViewVideoCall extends ReminderView {

    private static final String TAG = "UNKNOWN";

    private Context mContext;
    private Button mTesting1;
    private Button mTesting2;
    private Button mTesting3;

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;

    ReminderTile mTile;
//WeatherColock-ChrisWang-00+[
//    private Masthead mMasthead;
  //WeatherColock-ChrisWang-00+]
    public TestViewVideoCall(Context context) {
        super(context);
        initView(context);
    }

    public TestViewVideoCall(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TestViewVideoCall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        // TODO: initial View.
        // setReminderTile(Layout ID, Index)
        // Index: 1 (1st tile) or 2 (2nd tile)
        mTile = setReminderTile(R.layout.specific_lockscreen_3_lines_with_action, 1);
        // Error Handling
        if (mTile == null) {
            MyLog.w(TAG, "initView Failed: tile");
            return;
        }
        mTile.setButtonAccessibilityEnabled(true);
        // 3 Buttons.
        Resources  res = (mContext != null)? mContext.getResources(): null;
        if (res != null) {
                mTesting1 = new Button(mTile);
            if (mTesting1 != null) {
                mTesting1.setTitle("button1 button1 button1 button1 button1 button1 button1 button1 button1 "/*res.getString(R.string.lockscreen_dismiss)*/);
                mTesting1.setIcon(res.getDrawable(R.drawable.icon_btn_lockscreen_cancel_dark_xl));
            }
            mTesting2 = new Button(mTile);
            if (mTesting2 != null) {
                mTesting2.setTitle("button2 button2 button2 button2 button2 button2 button2 button2 button2 "/*res.getString(R.string.lockscreen_setting)*/);
                mTesting2.setIcon(res.getDrawable(R.drawable.icon_btn_settings_dark_xl));
            }
            mTesting3 = new Button(mTile);
            if (mTesting3 != null) {
                mTesting3.setTitle("button3 button3 button3 button3 button3 button3 button3 button3 button3 "/*res.getString(R.string.lockscreen_setting)*/);
                mTesting3.setIcon(res.getDrawable(R.drawable.icon_btn_settings_dark_xl));
            }
        }
        // Tile UI.
        mTextView1 = (TextView) this.findViewById(R.id.text11);
        mTextView2 = (TextView) this.findViewById(R.id.text2);
        mTextView3 = (TextView) this.findViewById(R.id.text3);
//WeatherColock-ChrisWang-00+[
//        mMasthead = new Masthead(mContext);
//        mMasthead.setEnableTextSWLayer(true);   //suggest add this API for reduce memory usage
//        mMasthead.changeAnimationState(-1);  // Disable Animation
//        setMastheadOnTop(mMasthead);
//WeatherColock-ChrisWang-00+]
        updateUI();
    }

    public void cleanUp() {
        super.cleanUp();
//WeatherColock-ChrisWang-00+[
//        mMasthead.stop();
//WeatherColock-ChrisWang-00+]
    }

    public void updateUI() {
        super.updateUI();
        // TODO: update UI
        setMessage();
    }

    private void setMessage() {
        String line1 = TAG + " Line 111";
        String line2 = TAG + " Line 222";
        String line3 = TAG + " Line 333";
        if (mTextView1 != null) {
            // setTitle(view, string):
            // Check the title if all letter should be uppercase.
            // by com.htc.util.res.HtcResUtil.isInAllCapsLocale(context)
            setTitle(mTextView1, line1);
        }
        if (mTextView2 != null) {
            mTextView2.setText(line2);
        }
        if (mTextView3 != null) {
           mTextView3.setText(line3);
        }
        if (mTile != null) {
            mTile.resetStringForAccessibility();
            mTile.addStringForAccessibility(line1);
            mTile.addStringForAccessibility(line2);
            mTile.addStringForAccessibility(line3);
        }
    }

    public void onTileDrop(ReminderTile tile) {
        super.onTileDrop(tile);
        if (tile != null) {
            // TODO: do something when tile is drop.
            if (tile == getTile(1)) {
                MyLog.v(TAG, "onTileDrop: main Tile");
            }
            if (mCallback != null) {
                mCallback.onTileDrop();
            }
        }
    }

    public void onTileDropEnd(ReminderTile tile) {
        super.onTileDropEnd(tile);
        if (tile != null) {
            // TODO: do something when tile is Drop END.
            if (tile == getTile(1)) {
                MyLog.v(TAG, "onTileDrop: main Tile");
            }
            if (mCallback != null) {
                mCallback.onTileDropEnd();
            }
        }
    }

    public void onButtonDrop(Button button) {
        super.onButtonDrop(button);
        if (button != null) {
            // TODO: do something when button is drop.
            if (button == mTesting1) {
                MyLog.i(TAG, "onButtonDrop: 1");
            } else if (button == mTesting2) {
                MyLog.i(TAG, "onButtonDrop: 2");
            } else if (button == mTesting3) {
                MyLog.i(TAG, "onButtonDrop: 3");
            }
            if (mCallback != null) {
                mCallback.onButtonDrop();
            }
        }
    }

    public void onButtonDropEnd(Button button) {
        super.onButtonDropEnd(button);
        if (button != null) {
            // TODO: do something when button is Drop END.
            if (button == mTesting1) {
                MyLog.i(TAG, "onButtonDropEnd: 1");
            } else if (button == mTesting2) {
                MyLog.i(TAG, "onButtonDropEnd: 2");
            } else if (button == mTesting3) {
                MyLog.i(TAG, "onButtonDropEnd: 3");
            }
            if (mCallback != null) {
                mCallback.onButtonDropEnd();
            }
        }
    }

    public int getButtonCount() {
        // TODO: Button Count: 2 or 3 or 4
        return 3;
    }

    public Button getButton(int index) {
        // TODO: UI & Index :
        // | Button1 | Button2 | Button3 |
        // |    0    |    1    |    2    |
        if (index == 0) {
            return mTesting1;
        } else if (index == 1) {
            return mTesting2;
        } else if (index == 2) {
            return mTesting3;
        }
        return null;
    }

    // TODO: Callback to Activity by yourself requirement.
    public interface Callback {
        void onTileDrop();
        void onTileDropEnd();
        void onButtonDrop();
        void onButtonDropEnd();
    };
    Callback mCallback;

    public void setCallback(Callback cb) {
        mCallback = cb;
    }
}
