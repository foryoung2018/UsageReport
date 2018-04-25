package com.htc.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class HtcUITestCase extends HtcActivityInstrumentationTestCase {
    String mSuggestName = null;

    /**
     * @return the mSuggestName
     */
    public final String getSuggestName() {
        return mSuggestName;
    }

    /**
     * @param mSuggestName
     *            the mSuggestName to set
     */
    public final void setSuggestName(String suggestName) {
        this.mSuggestName = suggestName;
    }

    public HtcUITestCase(Class activityClass) {
        super(activityClass);
        // TODO Auto-generated constructor stub
    }

    /*
     * Run performance testing if there are HtcPerformance annotation.
     *
     * @see android.test.InstrumentationTestCase#runTest()
     */
    @Override
    protected void runTest() throws Throwable {
        // TODO Auto-generated method stub
        String fName = getName();
        assertNotNull(fName);
        Method method = null;
        try {
            // use getMethod to get all public inherited
            // methods. getDeclaredMethods returns all
            // methods of this class but excludes the
            // inherited ones.
            method = getClass().getMethod(fName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + fName + "\" not found");
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            fail("Method \"" + fName + "\" should be public");
        }

        boolean portrait = true;
        boolean landscape = true;
        if (method.isAnnotationPresent(HtcUITest.class)) {
            portrait = method.getAnnotation(HtcUITest.class).portrait();
            landscape = method.getAnnotation(HtcUITest.class).landscape();
        }

        Throwable exception = null;

        if (portrait) {
            mSolo.setActivityOrientation(mSolo.PORTRAIT);
            setSuggestName(getName() + "_Portrait");
            // try {
            super.runTest();
            // } catch(Throwable running) {
            // exception = running;
            // }finally{
            // if(landscape) {
            // try {
            // super.tearDown();
            // }catch(Throwable teardown) {
            // exception = teardown;
            // }
            // super.setUp();
            // }
            // }
        }
        if (landscape) {
            mSolo.setActivityOrientation(mSolo.LANDSCAPE);
            setSuggestName(getName() + "_Landscape");
            try {
                super.runTest();
            } catch (Throwable running) {
                exception = running;
            }
        }

        if (null != exception)
            throw exception;
    }
}
