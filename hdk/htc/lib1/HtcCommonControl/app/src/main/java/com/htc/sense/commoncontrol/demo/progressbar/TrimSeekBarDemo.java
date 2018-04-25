
package com.htc.sense.commoncontrol.demo.progressbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.htc.lib1.cc.widget.TrimSeekBar;
import com.htc.lib1.cc.widget.TrimSeekBar.OnTrimChangeListener;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class TrimSeekBarDemo extends CommonDemoActivityBase {
    private TrimSeekBar mTrimSeekBar;
    private TextView mTextViewStart, mTextViewEnd;
    private Button mBtnControl;
    private int mOffset;
    private int mScreenWidth;
    private int mPopupViewWidth;
    private boolean mIsClicked = false;
    private Handler handler = new Handler();
    private ViewGroup mRootView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_seek_bar_layout);

        Resources res = getResources();
        mScreenWidth = getScreenWidth(res);
        mPopupViewWidth = res.getDimensionPixelOffset(R.dimen.popup_view_width);
        mOffset = res.getDimensionPixelOffset(R.dimen.trim_seek_bar_popup_offset);

        mTrimSeekBar = (TrimSeekBar) findViewById(R.id.trim_seek_bar);
        mTextViewStart = createPopupView(this, R.id.popup_view_start);
        mTextViewEnd = createPopupView(this, R.id.popup_view_end);
        mBtnControl = (Button) findViewById(R.id.btn_control);
        mRootView = (ViewGroup) mBtnControl.getRootView();

        mRootView.addView(mTextViewStart);
        mRootView.addView(mTextViewEnd);

        mBtnControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsClicked) {
                    pause();
                } else {
                    start();
                }
            }
        });

        mTrimSeekBar.setOnTrimChangeListener(new OnTrimChangeListener() {

            @Override
            public void onTrimEnd(TrimSeekBar seekBar, boolean isStartTrim) {
            }

            @Override
            public void onTrimStart(TrimSeekBar seekBar, boolean isStartTrim) {
                pause();
            }

            @Override
            public void onTrimChanged(TrimSeekBar seekBar, int startTrimProgress,
                    int endTrimProgress, Point startTrimPoint, Point endTrimPoint, boolean fromUser) {
                if (mTextViewStart.getVisibility() != View.VISIBLE) {
                    mTextViewStart.setVisibility(View.VISIBLE);
                }
                if (mTextViewEnd.getVisibility() != View.VISIBLE) {
                    mTextViewEnd.setVisibility(View.VISIBLE);
                }

                int startLeft = (startTrimPoint.x - mTextViewStart.getWidth());
                startLeft = startLeft < 0 ? 0 : startLeft;
                int startTop = startTrimPoint.y - mTextViewStart.getHeight() - mOffset;
                startTop = startTop < 0 ? 0 : startTop;
                mTextViewStart.setX(startLeft);
                mTextViewStart.setY(startTop);
                mTextViewStart.setText(String.valueOf(startTrimProgress));

                int MaxLeft = mScreenWidth - mTextViewEnd.getWidth();
                int left = endTrimPoint.x;
                left = left > MaxLeft ? MaxLeft : left;
                int top = endTrimPoint.y - mTextViewStart.getHeight() - mOffset;
                top = top < 0 ? 0 : top;
                mTextViewEnd.setX(left);
                mTextViewEnd.setY(top);
                mTextViewEnd.setText(String.valueOf(endTrimProgress));

                seekBar.setStartProgress(startTrimProgress);
                seekBar.setSecondaryProgress(endTrimProgress);
                seekBar.setProgress(seekBar.getStartTrimProgress());
            }

        });
    }

    private void pause() {
        mBtnControl.setText("Start");
        handler.removeCallbacks(runnable);
        mIsClicked = false;
    }

    private void start() {
        mBtnControl.setText("Pause");
        handler.post(runnable);
        mIsClicked = true;
    }

    private void stop() {
        mBtnControl.setText("Stop");
        handler.removeCallbacks(runnable);
        mIsClicked = false;
    }

    private TextView createPopupView(Context context, int id) {
        TextView tv = new TextView(context);
        tv.setId(id);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(mPopupViewWidth, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setBackgroundResource(R.drawable.common_dialogbox_full_dark);
        return tv;
    }

    private int getScreenWidth(Resources res) {
        DisplayMetrics screen = res.getDisplayMetrics();
        return screen.widthPixels;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenWidth = getScreenWidth(getResources());
        mTextViewEnd.setVisibility(View.INVISIBLE);
        mTextViewStart.setVisibility(View.INVISIBLE);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (mTrimSeekBar == null) {
                return;
            }
            int currentProgress = mTrimSeekBar.getProgress();
            if (currentProgress >= mTrimSeekBar.getEndTrimProgress()) {
                stop();
                return;
            }
            mTrimSeekBar.setProgress(currentProgress + 1);
            handler.postDelayed(runnable, 100);
        }
    };
}
