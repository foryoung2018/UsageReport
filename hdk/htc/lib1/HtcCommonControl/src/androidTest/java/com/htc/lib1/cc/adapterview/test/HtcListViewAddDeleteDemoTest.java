
package com.htc.lib1.cc.adapterview.test;

import android.content.pm.ActivityInfo;

import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewAddDeleteDemo;
import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewAddDeleteDemo.ListViewAdapter;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.test.HtcActivityTestCase;
import com.htc.test.util.ScreenShotUtil;

import java.util.ArrayList;

public class HtcListViewAddDeleteDemoTest extends HtcActivityTestCase {
    private HtcListView mListView;
    ListViewAdapter mAdapter;
    public HtcListViewAddDeleteDemoTest() {
        super(HtcListViewAddDeleteDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mListView = (HtcListView) getActivity().findViewById(R.id.htc_list);
        mAdapter = (ListViewAdapter) mListView.getAdapter();
    }

    public final void testRaceConditionOrientation() {
        mSolo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSolo.clickOnText("ABBAYE DE BELLOC");
        mSolo.clickOnText("ABBAYE DU MONT DES CATS");
        mSolo.clickOnText("ABERTAM");
        mSolo.clickOnText("ABONDANCE");
        mSolo.clickOnText("ACKAWI");

        mSolo.clickOnButton("del");
        mSolo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mSolo.sleep(1000);

        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                ((HtcListViewAddDeleteDemo) mActivity).getListView(),
                ScreenShotUtil.getScreenShotName(this));
    }

    public final void testOnCreateBundle() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(HtcListView.class, 0), this);
    }

    private void delete(final ArrayList<Integer> deletedItems) {
        mSolo.sleep(2000);
        final int size = deletedItems.size() - 1;
        for (int i = size; i >= 0; i--) {
            mAdapter.removeItem(deletedItems.get(i));
        }
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                mListView.setDelPositionsList(deletedItems);
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
        mSolo.scrollDownList(mListView);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == false
    // mOriUpperDeleteCount != 0 && mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase3() {
        mSolo.scrollDownList(mListView);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        final int childCount = mListView.getChildCount();
        addDeletedItems(deletedItems, 0, childCount + 5);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount == 0
    public void testDeleteCase4() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, HtcListViewAddDeleteDemo.ENTRIES.length - 6, HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase5() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount);
        addDeletedItems(deletedItems, HtcListViewAddDeleteDemo.ENTRIES.length - 6, HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase6() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, HtcListViewAddDeleteDemo.ENTRIES.length - 6, HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase7() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - childCount - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, HtcListViewAddDeleteDemo.ENTRIES.length - childCount);
        addDeletedItems(deletedItems, HtcListViewAddDeleteDemo.ENTRIES.length - 2, HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount != 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase8() {
        final int childCount = mListView.getChildCount();
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, childCount, HtcListViewAddDeleteDemo.ENTRIES.length);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase9() {
        final int childCount = mListView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount + childCount / 2);
        addDeletedItems(deletedItems, 3 * childCount, HtcListViewAddDeleteDemo.ENTRIES.length);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == false
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase10() {
        final int childCount = mListView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        addDeletedItems(deletedItems, 3 * childCount, HtcListViewAddDeleteDemo.ENTRIES.length);
        delete(deletedItems);
    }

    // mOriLastPage == false
    // nowLastPage == true
    // nowFirstPage == true
    // mOriCurDeleteCount == 0
    public void testDeleteCase11() {
        final int childCount = mListView.getChildCount();
        setSelection(2 * childCount);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, 2 * childCount);
        addDeletedItems(deletedItems, 3 * childCount, HtcListViewAddDeleteDemo.ENTRIES.length);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase12() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, 2 * childCount);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount == 0
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase13() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount / 2);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount == 0
    // mOriCurDeleteCount != 0
    public void testDeleteCase14() {
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == false
    // mOriUpperDeleteCount >= mOriCurLeftCount
    public void testDeleteCase15() {
        final int childCount = mListView.getChildCount();
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, childCount * 2);
        deletedItems.add(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == false
    // mOriUpperDeleteCount < mOriCurLeftCount
    public void testDeleteCase16() {
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        deletedItems.add(0);
        deletedItems.add(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        delete(deletedItems);
    }

    // mOriLastPage == true
    // mOriUpperDeleteCount != 0
    // mOriCurDeleteCount != 0
    // nowFirstPage == true
    public void testDeleteCase17() {
        setSelection(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
        final int childCount = mListView.getChildCount();
        final ArrayList<Integer> deletedItems = new ArrayList<Integer>();
        addDeletedItems(deletedItems, 0, HtcListViewAddDeleteDemo.ENTRIES.length - childCount);
        deletedItems.add(HtcListViewAddDeleteDemo.ENTRIES.length - 1);
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
                    ((HtcListViewAddDeleteDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setSelection(final int position) {
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                mListView.setSelection(position);
            }
        });
    }

    private void addDeletedItems(ArrayList<Integer> deletedItems, int start, int end) {
        for (int i = start; i < end; i++) {
            deletedItems.add(i);
        }
    }
}
