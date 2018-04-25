package com.htc.sense.commoncontrol.demo.reminder.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
//WeatherColock-ChrisWang-00+[
//import com.htc.android.home.view.Masthead;
//WeatherColock-ChrisWang-00+]
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView.ReminderTile;
import com.htc.sense.commoncontrol.demo.R;

public class TestViewDualCall extends ReminderView {

    private static final String TAG = "DUAL CALL";

    private Context mContext;
    private Button mDismiss1;
    private Button mSetting1;
    private TextView mTextView11;
    private TextView mTextView12;
    private ImageView mImageView1;

    private Button mDismiss2;
    private Button mSetting2;
    private TextView mTextView21;
    private TextView mTextView22;
    private ImageView mImageView2;

    ReminderTile mTile;
    ReminderTile mSubtile;
//WeatherColock-ChrisWang-00+[
//    private Masthead mMasthead;
//WeatherColock-ChrisWang-00+]
    public TestViewDualCall(Context context) {
        super(context);
        initView(context);
    }

    public TestViewDualCall(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TestViewDualCall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        // TODO: initial View.
        // setReminderTile(Layout ID, Index)
        // Index: 1 (1st tile) or 2 (2nd tile)
        mTile = setReminderTile(R.layout.specific_lockscreen_incoming_call_view, 1);
        mSubtile = setReminderTile(R.layout.specific_lockscreen_incoming_call_view, 2);
        // Error Handling
        if (mTile == null || mSubtile == null) {
            MyLog.w(TAG, "initView Failed: tile || subtile");
            return;
        }
        mTile.setButtonAccessibilityEnabled(true);
        mSubtile.setButtonAccessibilityEnabled(true);
        // Buttons.
        Resources  res = (mContext != null)? mContext.getResources(): null;
        if (res != null) {
            mDismiss1 = new Button(mTile);
            if (mDismiss1 != null) {
                mDismiss1.setTitle(res.getString(R.string.lockscreen_dismiss));
                mDismiss1.setIcon(res.getDrawable(R.drawable.icon_btn_lockscreen_cancel_dark_xl));
            }
            mSetting1 = new Button(mTile);
            if (mSetting1 != null) {
                mSetting1.setTitle(res.getString(R.string.lockscreen_setting));
                mSetting1.setIcon(res.getDrawable(R.drawable.icon_btn_settings_dark_xl));
            }
            mDismiss2 = new Button(mSubtile);
            if (mDismiss2 != null) {
                mDismiss2.setTitle(res.getString(R.string.lockscreen_dismiss));
                mDismiss2.setIcon(res.getDrawable(R.drawable.icon_btn_lockscreen_cancel_dark_xl));
            }
            mSetting2 = new Button(mSubtile);
            if (mSetting2 != null) {
                mSetting2.setTitle(res.getString(R.string.lockscreen_setting));
                mSetting2.setIcon(res.getDrawable(R.drawable.icon_btn_settings_dark_xl));
            }
        }
        // Tile UI.
        mTextView11 = (TextView) mTile.findViewById(R.id.text11);
        mTextView12 = (TextView) mTile.findViewById(R.id.text2);
        mTextView21 = (TextView) mSubtile.findViewById(R.id.text11);
        mTextView22 = (TextView) mSubtile.findViewById(R.id.text2);
        // Call Id
        mImageView1 = (ImageView) mTile.findViewById(R.id.call_id);
        mImageView2 = (ImageView) mSubtile.findViewById(R.id.call_id);
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
        setPhoto();
    }

    private void setMessage() {
        String line1  = TAG + " Line 11";
        String line2  = TAG + " Line 22";
        String line21 = TAG + " Line 1111";
        String line22 = TAG + " Line 2222";
        if (mTextView11 != null) {
            setTitle(mTextView11, line1);
        }
        if (mTextView12 != null) {
            mTextView12.setText(line2);
        }
        if (mTextView21 != null) {
            setTitle(mTextView21, line21);
        }
        if (mTextView22 != null) {
            mTextView22.setText(line22);
        }
        if (mTile != null) {
            mTile.resetStringForAccessibility();
            mTile.addStringForAccessibility(line1);
            mTile.addStringForAccessibility(line2);
        }
        if (mSubtile != null) {
            mSubtile.resetStringForAccessibility();
            mSubtile.addStringForAccessibility(line21);
            mSubtile.addStringForAccessibility(line22);
        }
    }

    private void setPhoto() {
        if (mContext != null) {
            Resources res = mContext.getResources();
            if (res != null) {
                Drawable dPhone = res.getDrawable(R.drawable.people_icon_photo);
                if (mImageView1 != null) {
                    mImageView1.setImageDrawable(dPhone);
                }
                if (mImageView2 != null) {
                    mImageView2.setImageDrawable(dPhone);
                }
            }
        }
    }

    public void onTileDrop(ReminderTile tile) {
        super.onTileDrop(tile);
        if (tile != null) {
            // TODO: do something when tile is drop.
            if (tile == getTile(1)) {
                MyLog.v(TAG, "onTileDrop: main Tile");
            } else if (tile == getTile(2)) {
                MyLog.v(TAG, "onTileDrop: sub Tile");
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
                MyLog.v(TAG, "onTileDropEnd: main Tile");
            } else if (tile == getTile(2)) {
                MyLog.v(TAG, "onTileDropEnd: sub Tile");
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
            if (button == mDismiss1) {
                MyLog.i(TAG, "onButtonDrop: Dismiss1");
            } else if (button == mSetting1) {
                MyLog.i(TAG, "onButtonDrop: Setting1");
            } else if (button == mDismiss2) {
                MyLog.i(TAG, "onButtonDrop: Dismiss2");
            } else if (button == mSetting2) {
                MyLog.i(TAG, "onButtonDrop: Setting2");
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
            if (button == mDismiss1) {
                MyLog.i(TAG, "onButtonDrop: Dismiss1");
            } else if (button == mSetting1) {
                MyLog.i(TAG, "onButtonDrop: Setting1");
            } else if (button == mDismiss2) {
                MyLog.i(TAG, "onButtonDrop: Dismiss2");
            } else if (button == mSetting2) {
                MyLog.i(TAG, "onButtonDrop: Setting2");
            }
            if (mCallback != null) {
                mCallback.onButtonDropEnd();
            }
        }
    }

    public int getButtonCount() {
        // TODO: define the button count you need.
        // Button Count: 2 or 3 or 4
        return 4;
    }

    public Button getButton(int index) {
        // TODO: return the button you define.
        // UI & Index :
        // | Dismiss1 | Setting1 | Dismiss2 | Setting2 |
        // |     0    |     1    |     2    |     3    |
        if (index == 0) {
            return mDismiss1;
        } else if (index == 1) {
            return mSetting1;
        } else if (index == 2) {
            return mDismiss2;
        } else if (index == 3) {
            return mSetting2;
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
