package com.htc.lib1.cc.reminderview.activityhelper;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.TextView;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.ui.ReminderView;

public class TestViewVideoCall extends ReminderView {

    private Context mContext;
    private Button mTesting1;
    private Button mTesting2;
    private Button mTesting3;

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;

    ReminderTile mTile;
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
        if (mTile == null) {
            return;
        }
        mTile.setButtonAccessibilityEnabled(true);
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
        mTextView1 = (TextView) this.findViewById(R.id.text11);
        mTextView2 = (TextView) this.findViewById(R.id.text2);
        mTextView3 = (TextView) this.findViewById(R.id.text3);
        updateUI();
    }

    public void cleanUp() {
        super.cleanUp();
    }

    public void updateUI() {
        super.updateUI();
        setMessage();
    }

    private void setMessage() {
        String line1 = " Line 111";
        String line2 = " Line 222";
        String line3 = " Line 333";
        if (mTextView1 != null) {
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
            if (tile == getTile(1)) {
            }
            if (mCallback != null) {
                mCallback.onTileDrop();
            }
        }
    }

    public void onTileDropEnd(ReminderTile tile) {
        super.onTileDropEnd(tile);
        if (tile != null) {
            if (tile == getTile(1)) {
            }
            if (mCallback != null) {
                mCallback.onTileDropEnd();
            }
        }
    }

    public void onButtonDrop(Button button) {
        super.onButtonDrop(button);
        if (button != null) {
            if (button == mTesting1) {
            } else if (button == mTesting2) {
            } else if (button == mTesting3) {
            }
            if (mCallback != null) {
                mCallback.onButtonDrop();
            }
        }
    }

    public void onButtonDropEnd(Button button) {
        super.onButtonDropEnd(button);
        if (button != null) {
            if (button == mTesting1) {
            } else if (button == mTesting2) {
            } else if (button == mTesting3) {
            }
            if (mCallback != null) {
                mCallback.onButtonDropEnd();
            }
        }
    }

    public int getButtonCount() {
        return 3;
    }

    public Button getButton(int index) {
        if (index == 0) {
            return mTesting1;
        } else if (index == 1) {
            return mTesting2;
        } else if (index == 2) {
            return mTesting3;
        }
        return null;
    }

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
