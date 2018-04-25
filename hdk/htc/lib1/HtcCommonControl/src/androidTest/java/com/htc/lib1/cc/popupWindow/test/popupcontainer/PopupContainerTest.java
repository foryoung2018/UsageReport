
package com.htc.lib1.cc.popupWindow.test.popupcontainer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.htc.lib1.cc.widget.HtcPopupContainer;
import com.htc.lib1.cc.popupWindow.activityhelper.popupcontainer.HtcSeekBarPopupWindowDemo;
import com.htc.test.HtcPerformanceTest;
import com.htc.test.HtcPerformanceTestCase;

public class PopupContainerTest extends HtcPerformanceTestCase {
    HtcPopupContainer mContainer = null;
    private int mWidthSpec;
    private int mHeightSpec;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    public PopupContainerTest() {
        super(HtcSeekBarPopupWindowDemo.class);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see android.test.InstrumentationTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        mContainer = null;
        if (null != mBitmap && !mBitmap.isRecycled())
            mBitmap.recycle();
        mBitmap = null;
        mCanvas = null;
        super.tearDown();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        mBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mContainer = new HtcPopupContainer(getInstrumentation().getTargetContext());
        mWidthSpec = View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.UNSPECIFIED);
        mHeightSpec = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.UNSPECIFIED);
    }

    @HtcPerformanceTest(
            iteration = 1000,
            cpuTime = 1000,
            executionTime = 1100
            )
            final public void testConstructor() {
        new HtcPopupContainer(getInstrumentation().getTargetContext());
    }

    @HtcPerformanceTest(
            iteration = 1000,
            cpuTime = 120,
            executionTime = 120
            )
            final public void testMeasure() {
        mContainer.measure(mWidthSpec, mHeightSpec);
    }

    @HtcPerformanceTest(
            iteration = 1000,
            cpuTime = 120,
            executionTime = 120
            )
            final public void testLayout() {
        mContainer.layout(0, 0, 1080, 1920);
    }

    @HtcPerformanceTest(
            iteration = 1000,
            cpuTime = 100,
            executionTime = 100
            )
            final public void testDraw() {
        mContainer.draw(mCanvas);
    }
}
