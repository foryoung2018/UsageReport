package com.htc.lib2.opensense.test;

import junit.framework.Assert;

import com.htc.lib2.Hms;
import com.htc.lib2.Hms.CompatibilityException;
import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib2.opensense.internal.SystemWrapper.HtcCustomizationManager;

import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

public class HmsTestCase extends AndroidTestCase {

    public static final String HSP_PACKAGE = "com.htc.lib2.opensense.tests2";

    private static final String LOG_TAG = "Debug" /* HmsTestCase.class.getSimpleName() */;

    static {
        SystemWrapper.setHspPackageName(HSP_PACKAGE);
        SystemWrapper.setHdkApiPrefix("zz_hdkapi_");
    }

    public void testCheckCompatibilityA1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[A1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityA2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[A2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityA3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[A3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityA4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    public void testCheckCompatibilityB1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_0_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[B1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityB2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_0_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[B2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityB3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_0_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[B3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityB4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_0_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    public void testCheckCompatibilityC1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_1_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[C1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityC2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_1_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[C2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityC3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_1_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[C3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityC4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_1_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    public void testCheckCompatibilityD1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_2_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[D1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityD2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_2_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[D2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityD3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_2_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[D3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityD4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_2_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    public void testCheckCompatibilityE1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_3_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[E1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityE2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_3_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[E2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityE3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_3_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[E3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityE4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_3_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityF1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_4_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[F1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityF2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_4_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[F2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityF3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_4_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[F3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityF4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_4_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityG1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_5_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[G1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityG2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_5_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[G2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityG3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_5_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[G3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityG4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_5_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityH1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_6_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[H1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityH2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_6_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[H2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityH3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_6_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[H3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityH4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_6_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityI1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_7_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[I1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityI2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_7_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[I2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityI3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_7_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[I3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityI4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_7_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityJ1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_8_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[J1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityJ2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_8_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[J2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityJ3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_8_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[J3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityJ4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_8_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityK1() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_9_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[K1] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityK2() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_9_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, false);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[K2] isStockUI: " + isStockUI + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityK3() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        SystemWrapper.setHspApiPrefix("test_9_hdkapi_");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext(), bundle);
        boolean isStockUI = isStockUI(getContext());
        boolean isHtcDevice = isHtcDevice();
        boolean isApiLevel19 = isApiLevel19();
        boolean isSense60 = isSense60();
        Log.e(LOG_TAG, "[K3] isStockUI: " + isStockUI + ", isHtcDevice: " + isHtcDevice
                + ", isApiLevel19: " + isApiLevel19 + ", isSense60: " + isSense60);
        if ( isStockUI ) {
            if ( isHtcDevice && isApiLevel19 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        } else {
            if ( isSense60 ) {
                Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
            } else {
                Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
            }
        }
    }

    public void testCheckCompatibilityK4() throws Throwable {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        SystemWrapper.setHspApiPrefix("test_9_hdkapi_");
        Hms.CompatibilityStatus status = Hms.checkCompatibility(getContext());
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    public void testCheckCompatibilityL1() {
        SystemWrapper.setIgnoreHdkSupportCheck(false);
        boolean isGetException = false;
        try {
            Hms.checkCompatibility(null);
        } catch (IllegalArgumentException e) {
            isGetException = true;
        } catch (CompatibilityException e) {
        }
        Assert.assertTrue(isGetException);
    }

    public void testCheckCompatibilityL2() {
        SystemWrapper.setIgnoreHdkSupportCheck(true);
        boolean isGetException = false;
        try {
            Hms.checkCompatibility(null);
        } catch (IllegalArgumentException e) {
            isGetException = true;
            e.printStackTrace();
        } catch (CompatibilityException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(isGetException);
    }

    private static boolean isStockUI(Context context) {
        return context.getPackageManager().hasSystemFeature("com.google.android.feature.GOOGLE_EXPERIENCE");
    }

    private static boolean isApiLevel19() {
        return android.os.Build.VERSION.SDK_INT == 19;
    }

    private static boolean isSense60() {
        return Float.parseFloat(
                new HtcCustomizationManager().getCustomizationReader(
                        "System",
                        HtcCustomizationManager.READER_TYPE_XML,
                        false
                ).readString("sense_version", "0.0")
        ) >= 6.0f;
    }

    private static boolean isHtcDevice() {
        return android.os.Build.BRAND.equals("HTC");
    }
}
