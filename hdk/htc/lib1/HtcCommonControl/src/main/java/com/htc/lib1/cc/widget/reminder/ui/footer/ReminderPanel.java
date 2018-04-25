package com.htc.lib1.cc.widget.reminder.ui.footer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.BaseTile.Button;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class ReminderPanel extends Panel {

    private static final String TAG = "RemiPanel";

    private int mButtonSize = 0;
    public static final int BUTTON_SIZE_2 = 2;
    public static final int BUTTON_SIZE_3 = 3;
    public static final int BUTTON_SIZE_4 = 4;

    private boolean mInflated = false;
    private List<ReminderSphere> mButtonSphereLists = new ArrayList<ReminderSphere>();
    private List<Button> mButtonInfos;
    private View mButtonPanelMiddleGap;
    private int mMiddleGapVisibility = View.GONE;

    private int mOriginalPaddingLeft;
    private int mOriginalPaddingRight;
    private int mOriginalPaddingTop;
    private int mOriginalPaddingBottom;

    private Context mContext;

    public ReminderPanel(Context context) {
        super(context);
        init();
    }

    public ReminderPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReminderPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /** @hide */
    public void init() {
        super.init();
        mContext = getContext();
        getLayoutPadding();
    }

    private void getLayoutPadding() {
        mOriginalPaddingBottom = getPaddingBottom();
        mOriginalPaddingTop = getPaddingTop();
        mOriginalPaddingLeft = getPaddingLeft();
        mOriginalPaddingRight = getPaddingRight();
    }

    /** @hide */
    public void relayoutPanel(boolean isNavigationTransparent) {
        if (MyUtil.showHtcNavigationBarWrap(mContext)) {
            MyLog.i(TAG, "relayoutPanel: " + isNavigationTransparent);
            int paddingBottom = mOriginalPaddingBottom;
            if (isNavigationTransparent) {
                paddingBottom += MyUtil.getNavigationBarHeight(mContext);
            }
            int paddingTop = mOriginalPaddingTop;
            int paddingLeft = mOriginalPaddingLeft;
            int paddingRight = mOriginalPaddingRight;
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }

    /** @hide */
    public void cleanUp() {
        uninitView();
        super.cleanUp();
    }

    private void initView() {
        int layoutID = 0;
        int newSize = (mButtonInfos != null)? mButtonInfos.size():0;
        if (newSize != mButtonSize) {
            MyLog.i(TAG, "initV newS:" + newSize
                    + " curS:" + mButtonSize);
            int[] resId = null;
            if (newSize == BUTTON_SIZE_2) {
//                layoutID = MyUtil.getIdFromRes(mContext, ReminderResWrap.LAYOUT_BUTTONPANEL_2);
//                resId = new int[]{
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_L1),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_R1)
//                };
                layoutID = R.layout.specific_lockscreen_buttonpanel_2;
                resId = new int[]{
                                R.id.buttonpanel_left_1,
                                R.id.buttonpanel_right_1
                };
            } else if (newSize == BUTTON_SIZE_3) {
//                layoutID = MyUtil.getIdFromRes(mContext, ReminderResWrap.LAYOUT_BUTTONPANEL_3);
//                resId = new int[]{
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_L1),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_M1),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_R1)
//                };
                layoutID = R.layout.specific_lockscreen_buttonpanel_3;
                resId = new int[]{
                                R.id.buttonpanel_left_1,
                                R.id.buttonpanel_middle_1,
                                R.id.buttonpanel_right_1
                };
            } else if (newSize == BUTTON_SIZE_4) {
//                layoutID = MyUtil.getIdFromRes(mContext, ReminderResWrap.LAYOUT_BUTTONPANEL_4);
//                resId = new int[]{
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_L1),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_L2),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_R1),
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONSPHERE_R2)
//                };
                layoutID = R.layout.specific_lockscreen_buttonpanel_4;
                resId = new int[]{
                                R.id.buttonpanel_left_1,
                                R.id.buttonpanel_left_2,
                                R.id.buttonpanel_right_1,
                                R.id.buttonpanel_right_2
                };
            } else {
                return;
            }
            removeAllButtons();
            Context context = getContext();
            if (layoutID > 0 && context != null && !mInflated) {
                // Inflate Layout
                LayoutInflater inflater = MyUtil.getLayoutInflaterFromResApp(mContext);
                if (inflater != null) {
                    inflater.inflate(layoutID, this);
                }
                mButtonSize = newSize;
                mInflated = true;
                if (mButtonSphereLists != null) {
                    mButtonSphereLists.clear();
                    ReminderSphere button;
                    int resIdSize = (resId != null)? resId.length:0;
                    MyLog.i(TAG, "initV rIdS: " + resIdSize);
                    for (int i = 0; i < resIdSize; ++i) {
                        button = (ReminderSphere)findViewById(resId[i]);
                        mButtonSphereLists.add(button);
                        if (button != null) {
                            button.initView();
                        } else {
                            MyLog.w(TAG, "initV fail id: " + resId[i]);
                        }
                    }
                }
//                mButtonPanelMiddleGap = findViewById(
//                        MyUtil.getIdFromRes(mContext, ReminderResWrap.ID_BUTTONPANEL_MIDDLE_GAP));
                mButtonPanelMiddleGap = findViewById(R.id.buttonpanel_middle_gap);
                updateMiddleGapVisibility(mMiddleGapVisibility);
            } else {
                MyLog.w(TAG, "initV: Invaild Resource ID or Context.");
            }
        } else {
            MyLog.i(TAG, "initV: same size");
        }
    }

    private void uninitView() {
        MyLog.i(TAG, "uninitV");
        removeAllButtons();
        removeInfos();
    }

    private void removeInfos() {
        mButtonSize = 0;
        if (mButtonInfos != null) {
            mButtonInfos.clear();
            mButtonInfos = null;
        }
    }

    private void removeAllButtons() {
        for (ReminderSphere sphere: mButtonSphereLists) {
            if (sphere != null) {
                sphere.uninitView();
            }
        }
        if (mButtonSphereLists != null) {
            mButtonSphereLists.clear();
        }
        if (mInflated) {
            removeAllViewsInLayout();
            mInflated = false;
        }
    }

    /** @hide */
    public void update(List<Button> buttons) {
        /** Inflate UI by button list. */
        mButtonInfos = buttons;
        // UI
        initView();
        // Set Button Info
        int size = (mButtonSphereLists != null)? mButtonSphereLists.size():0;
        int buttonSize = (buttons != null)? buttons.size() : 0;
        if (size != buttonSize) {
            MyLog.i(TAG, "upd sphS: " + size + ", butS: " + buttonSize);
        } else {
            MyLog.i(TAG, "upd: " + size);
        }
        ReminderSphere sphere;
        Button button;
        for (int i=0; i<size; i++) {
            sphere = mButtonSphereLists.get(i);
            if (sphere != null) {
                if (i < buttonSize) {
                    button = buttons.get(i);
                } else {
                    button = null;
                }
                sphere.setButtonInfo(button);
            }
        }
    }

    /** @hide */
    public void updateMiddleGapVisibility(int visibility) {
        mMiddleGapVisibility = visibility;
        if (mButtonPanelMiddleGap != null) {
            mButtonPanelMiddleGap.setVisibility(mMiddleGapVisibility);
        }
    }
}
