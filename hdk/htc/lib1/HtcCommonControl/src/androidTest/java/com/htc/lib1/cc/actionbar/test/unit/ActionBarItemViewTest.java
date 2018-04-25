
package com.htc.lib1.cc.actionbar.test.unit;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ImageButton;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarItemViewTest extends ActivityInstrumentationTestCase2<ActionBarMockActivity> {
    private ActionBarItemView mActionBarItemView;

    public ActionBarItemViewTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActionBarItemView = new ActionBarItemView(getInstrumentation().getTargetContext());
    }

    @UiThreadTest
    public void testSetIconDrawable() {
        Drawable d = getInstrumentation().getTargetContext().getResources().getDrawable(android.R.drawable.ic_delete);
        mActionBarItemView.setIcon(d);
        assertTrue(mActionBarItemView.getIcon().equals(d));
    }

    @UiThreadTest
    public void testSetIconBitMap() {
        Resources res = getInstrumentation().getTargetContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, android.R.drawable.ic_delete);
        mActionBarItemView.setIcon(bitmap);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mActionBarItemView.getIcon();
        assertTrue(bitmapDrawable.getBitmap().sameAs(bitmap));
    }

    public void testOnLongClick() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarItemView.setTitle("This is long click");
                mActionBarItemView.setOnLongClickListener(null);
                mActionBarItemView.performLongClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @UiThreadTest
    public void testSetEnabled() {
        ImageButton imageButton = (ImageButton) mActionBarItemView.findViewById(R.id.imageButton);

        mActionBarItemView.setEnabled(true);

        assertTrue(imageButton.isEnabled());
        assertTrue(1 == imageButton.getAlpha());

        mActionBarItemView.setEnabled(false);

        assertFalse(imageButton.isEnabled());
        assertFalse(1 == imageButton.getAlpha());

    }
}
