
package com.htc.lib1.cc.adapterview.test;

import android.test.TouchUtils;
import android.view.View;
import android.widget.AdapterView;

import com.htc.lib1.cc.adapterview.activityhelper.HtcGridViewDemo;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.adapterview.activityhelper.HtcGridViewDemo.GridViewAdapter;
import com.htc.test.util.ScreenShotUtil;

import java.util.ArrayList;

public class HtcGridViewDemoTest extends HtcActivityTestCaseBase {

    private HtcGridView mGridView;
    OnItemClickListener mOnItemClickListener;
    boolean mClicked = false;
    GridViewAdapter mAdapter;
    ArrayList<Integer> mAddedItem;
    boolean mIsScrollEndBeforeAddItem;
    boolean mIsDeleteAnimationStart;
    boolean mIsDeleteAnimationEnd;

    public HtcGridViewDemoTest() {
        super(HtcGridViewDemo.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        initActivity();
        mGridView = (HtcGridView) getActivity().findViewById(R.id.myGrid);
        mAdapter = (GridViewAdapter) mGridView.getAdapter();
    }

    class OnItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            mClicked = true;
        }
    }

    public final void testInitUI() {
        mSolo.waitForView(mGridView);
        mSolo.sleep(1000);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mGridView, ScreenShotUtil.getScreenShotName(this));
    }

    public void testItemUI() {
        mSolo.waitForView(mGridView);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mGridView.getChildAt(0),
                ScreenShotUtil.getScreenShotName(this));
    }

    public final void testScrolling() {
        assertNotNull(mActivity);
        mSolo.waitForView(mGridView);
        TouchUtils.scrollToTop(this, mActivity, mGridView);
        mSolo.sleep(1000);
        TouchUtils.scrollToBottom(this, mActivity, mGridView);
        mSolo.sleep(1000);
        TouchUtils.scrollToTop(this, mActivity, mGridView);
    }

    public void testAddToStart() {
        mSolo.sleep(1000);

        // Prepare added position list
        mAddedItem = new ArrayList<Integer>();
        mAddedItem.add(0);
        mAddedItem.add(3);
        mIsScrollEndBeforeAddItem = true;

        // Scroll to the first added position
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                // mGridView.scrollToFirstAddedPosition(0);
            }
        });

        mSolo.sleep(2000);
        assertEquals(true, mIsScrollEndBeforeAddItem);
    }

    public void testAddToMiddle() {
        mSolo.sleep(2000);

        // Prepare added position list
        mAddedItem = new ArrayList<Integer>();
        mAddedItem.add(25);
        mAddedItem.add(26);
        mIsScrollEndBeforeAddItem = true;

        // Scroll to the first added position
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                // mGridView.scrollToFirstAddedPosition(30);
            }
        });

        mSolo.sleep(2000);
        assertEquals(true, mIsScrollEndBeforeAddItem);
    }

    public void testAddToEnd() {
        mSolo.sleep(2000);

        // Prepare added position list
        final int size = mAdapter.getCount();
        mAddedItem = new ArrayList<Integer>();
        mAddedItem.add(size - 3);
        mAddedItem.add(size - 4);
        mIsScrollEndBeforeAddItem = true;

        // Scroll to the first added position
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                // mGridView.scrollToFirstAddedPosition(size);
            }
        });
        mSolo.sleep(2000);
        assertEquals(true, mIsScrollEndBeforeAddItem);
    }

    private void delete(final ArrayList<Integer> deletedItems) {
        mSolo.sleep(2000);
        final int size = deletedItems.size() - 1;
        for (int i = size; i >= 0; i--) {
            mAdapter.removeItem(deletedItems.get(i));
        }
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                mGridView.setDelPositionsList(deletedItems);
            }
        });
        mSolo.sleep(2000);
    }

    // mOriLastPage == false
    // nowLastPage == false
    // mOriUpperDeleteCount == 0
    public void testDeleteCase1() {
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == false
    // mOriUpperDeleteCount != 0 && mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase2() {
        mSolo.scrollDownList(mGridView);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == false
    // mOriUpperDeleteCount != 0 && mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase3() {
        mSolo.scrollDownList(mGridView);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        final int childCount = mGridView.getChildCount();
        addDeletedItems(deletedItems, 0, childCount + 5);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount == 0
    public void testDeleteCase4() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, HtcGridViewDemo.ITEMCOUNT - 6, HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase5() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount);
        addDeletedItems(deletedItems, HtcGridViewDemo.ITEMCOUNT - 6, HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase6() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, HtcGridViewDemo.ITEMCOUNT - 6, HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase7() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, HtcGridViewDemo.ITEMCOUNT - childCount);
        addDeletedItems(deletedItems, HtcGridViewDemo.ITEMCOUNT - 6, HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase8() {
        final int childCount = mGridView.getChildCount();
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, childCount, HtcGridViewDemo.ITEMCOUNT);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase9() {
        final int childCount = mGridView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount + childCount / 2);
        addDeletedItems(deletedItems, 3 * childCount, HtcGridViewDemo.ITEMCOUNT);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase10() {
        final int childCount = mGridView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, 3 * childCount, HtcGridViewDemo.ITEMCOUNT);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount == 0
    public void testDeleteCase11() {
        final int childCount = mGridView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, 2 * childCount);
        addDeletedItems(deletedItems, 3 * childCount, HtcGridViewDemo.ITEMCOUNT);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase12() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, 2 * childCount);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase13() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount / 2);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount == 0
    // mOriCurDeleteCount != 0
    public void testDeleteCase14() {
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == false
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase15() {
        final int childCount = mGridView.getChildCount();
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount * 2);
        deletedItems.add(HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == false
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase16() {
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        deletedItems.add(HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == true
    public void testDeleteCase17() {
        setSelection(HtcGridViewDemo.ITEMCOUNT - 1);
        final int childCount = mGridView.getChildCount();
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, HtcGridViewDemo.ITEMCOUNT - childCount);
        deletedItems.add(HtcGridViewDemo.ITEMCOUNT - 1);
        delete(deletedItems);
    }

    public void testDeleteRepeatedly() {
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        delete(deletedItems);

        deletedItems.add(1);
        delete(deletedItems);

        deletedItems.add(2);
        delete(deletedItems);
    }

    public final void testImproveCoverage() {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcGridViewDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setSelection(final int position) {
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                mGridView.setSelection(position);
            }
        });
    }

    private void addDeletedItems(ArrayList<Integer> deletedItems, int start, int end) {
        for (int i = start; i < end; i++) {
            deletedItems.add(i);
        }
    }
}
