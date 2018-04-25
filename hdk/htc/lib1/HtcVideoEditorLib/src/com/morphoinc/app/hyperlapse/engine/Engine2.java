package com.morphoinc.app.hyperlapse.engine;

import java.nio.ByteBuffer;

import android.content.Context;
import android.view.Surface;

import com.htc.lib1.hyperlapse.morpho.util.EngineWithGyroAndAccelerometer;
import com.htc.lib1.hyperlapse.util.MyLog;

/**
 * @author Morpho
 *
 */
public class Engine2 extends EngineWithGyroAndAccelerometer {
    public static final int MODE_SOFT = 0;
    public static final int MODE_HYBRID = 1;
    public static final int MODE_HARD = 2;

    public static final int ACCURACY_HIGH = 0;
    public static final int ACCURACY_MIDDLE = 1;
    public static final int ACCURACY_LOW = 2;

    private boolean mIsOisMode = false;

    private boolean mIsStarted = false;

    static {
        System.loadLibrary("morpho_hyperlapse_jni_v6");
    }

    public Engine2(Context context) {
        super(context);
    }

    private long mHandler;

    public void setMode(int mode) {
        synchronized (this) {
            MyLog.d("" + setModeJni(mHandler, mode));
        }
    }

    public void setOISMode(boolean on) {
        synchronized (this) {
            MyLog.d("" + activateOisModeJni(mHandler, on ? 1 : 0));
            mIsOisMode = on;
        }
    }

    public void setGyroTimeLag(long lag) {
        synchronized (this) {
            MyLog.d("" + setGyroTimeLagJni(mHandler, lag));
        }
    }

    public void setVerticalViewAngle(double angle) {
        synchronized (this) {
            MyLog.d("" + setVerticalViewAngleJni(mHandler, angle));
        }
    }

    public void setRollingShutterCoeff(int coeff, int orien) {
        synchronized (this) {
            MyLog.d("" + setRollingShutterCoeffJni(mHandler, coeff, orien));
        }
    }

    public void setUnreliableLevel(int level) {
        synchronized (this) {
            MyLog.d("" + setUnreliableLevelJni(mHandler, level));
        }
    }

    public void setNoMovementLevel(int level) {
        synchronized (this) {
            MyLog.d("" + setNoMovementLevelJni(mHandler, level));
        }
    }

    public void setFixLevel(int level) {
        synchronized (this) {
            MyLog.d("" + setFixLevelJni(mHandler, level));
        }
    }

    public void initializePreprocess(int width, int height, int format,
            int accuracy) {
        synchronized (this) {
            if (mHandler == 0) {
                mHandler = initializePreprocessJni(width, height, format,
                        accuracy);
            }
            mIsStarted = false;
        }
    }
    
    public void initializePreprocessEx(int width, int height, int decWidth, int decHeight, int format,
            int accuracy) {
        MyLog.d("width = " + width + " height = " + height + " decWidth = "
                + decWidth + " decHeight = " + decHeight + " format = "
                + format + " accuracy = " + accuracy);
        synchronized (this) {
            if (mHandler == 0) {
                mHandler = initializePreprocessExJni(width, height, decWidth, decHeight, format,
                        accuracy);
            }
            mIsStarted = false;
        }
    }

    public void startPreprocess() {
        synchronized (this) {
            if (mHandler != 0) {
                MyLog.d("" + startPreprocessJni(mHandler));
                mIsStarted = true;
            }
        }
    }

    public void preprocess(ByteBuffer src, int padding, long time) {
        synchronized (this) {
            if (mHandler != 0) {
                preprocessJni(mHandler, src, padding, time);
            }
        }
    }
    
    public void preprocessEx(ByteBuffer src, long time) {
        synchronized (this) {
            if (mHandler != 0) {
                preprocessExJni(mHandler, src, time);
            }
        }
    }

    public ByteBuffer getPreprocessData() {
        ByteBuffer data = null;
        synchronized (this) {
            if (mHandler != 0) {
                int size = getDataSizeJni(mHandler);
                data = ByteBuffer.allocateDirect(size);
                MyLog.d("" + getDataJni(mHandler, data));
                data.rewind();
            }
        }
        return data;
    }

    public void finishPreprocess() {
        synchronized (this) {
            if (mHandler != 0) {
                MyLog.d("" + finishPreprocessJni(mHandler));
                mHandler = 0;
                mIsStarted = false;
            }
        }
    }

    @Override
    public void addGyroData(float x, float y, float z, long t, int rotation) {
        synchronized (this) {
            if (mHandler != 0 && mIsStarted) {
                if (mIsOisMode) {
                    x = y = 0;
                }

                switch (rotation) {
                case Surface.ROTATION_0:
                    MyLog.d("" + addGyroDataJni(mHandler, x, -y, -z, t));
                    break;
                default:
                    MyLog.d("" + addGyroDataJni(mHandler, -y, -x, -z, t));
                    break;
                }
            }
        }
    }

    @Override
    public void addAccelerometerData(float x, float y, float z, long t,
            int rotation) {
        synchronized (this) {
            if (mHandler != 0 && mIsStarted) {
                switch (rotation) {
                case Surface.ROTATION_0:
                    MyLog.d("" + addAccelerometerDataJni(mHandler, x, -y, -z, t));
                    break;
                default:
                    MyLog.d("" + addAccelerometerDataJni(mHandler, -y, -x, -z, t));
                    break;
                }
            }
        }
    }

    // for preprocess
    private static native final int setModeJni(long handler, int mode);

    private static native final int activateOisModeJni(long handler, int on);

    private static native final int setGyroTimeLagJni(long handler, long lag);

    private static native final int setVerticalViewAngleJni(long handler,
            double angle);

    private static native final int setRollingShutterCoeffJni(long handler,
            int coeff, int orien);

    private static native final int setUnreliableLevelJni(long handler,
            int level);

    private static native final int setNoMovementLevelJni(long handler,
            int level);

    private static native final int addGyroDataJni(long handler, double x,
            double y, double z, long t);

    private static native final int addAccelerometerDataJni(long handler,
            double x, double y, double z, long t);

    private static native final long initializePreprocessJni(int width,
            int height, int format, int accuracy);
    
    //For padding feature impl.
    private static native final long initializePreprocessExJni(int width,
            int height, int decWidth, int decHeight, int format, int accuracy);

    private static native final int startPreprocessJni(long handler);

    private static native final int preprocessJni(long handler, ByteBuffer src,
            int padding, long time);
    
    private static native final int preprocessExJni(long handler, ByteBuffer src,
            long time);

    private static native final int getDataSizeJni(long handler);

    private static native final int getDataJni(long handler, ByteBuffer data);

    private static native final int finishPreprocessJni(long handler);

    public void initialize(ByteBuffer data, int inWidth, int inHeight,
            int outWidth, int outHeight, int format, int accuracy) {
        synchronized (this) {
            if (mHandler == 0) {
                mHandler = initializeJni(data, inWidth, inHeight, outWidth,
                        outHeight, format, accuracy);
                MyLog.d(""  + mHandler );
            }
        }
    }
    
    public void initializeEx(ByteBuffer data, int inWidth, int inHeight,
            int outWidth, int outHeight,  int decWidth, int decHeight, int format, int accuracy) {
        MyLog.d("inWidth = " + inWidth + " inHeight = " + inHeight
                + " outWidth = " + outWidth + " outHeight = " + outHeight
                + " decWidth = " + decWidth + " decHeight = " + decHeight
                + " format = " + format + " accuracy = " + accuracy);
        synchronized (this) {
            if (mHandler == 0) {
                mHandler = initializeExJni(data, inWidth, inHeight, outWidth,
                        outHeight, decWidth, decHeight, format, accuracy);
                MyLog.d(""  + mHandler );
            }
        }
    }

    public void start() {
        synchronized (this) {
            if (mHandler != 0) {
                MyLog.d("" + startJni(mHandler));
            }
        }
    }

    public void getCroppingSize(int[] size) {
        synchronized (this) {
            if (mHandler != 0) {
                MyLog.d("" + getCroppingSizeJni(mHandler, size));
            }
        }
    }

    public int getFrameNum() {
        synchronized (this) {
            if (mHandler != 0) {
                return getFrameNumJni(mHandler);
            }
        }
        return -1;
    }

    public int process(ByteBuffer src, ByteBuffer dst, int index, int padding) {
        synchronized (this) {
            if (mHandler != 0) {
                return processJni(mHandler, src, dst, index, padding);
            }
            return -1;
        }
    }
    
    public int processEx(ByteBuffer src, ByteBuffer dst, int index) {
        synchronized (this) {
            if (mHandler != 0) {
                return processExJni(mHandler, src, dst, index);
            }
            return -1;
        }
    }

    public void getMatrix(float[] matrix, int index) {
        synchronized (this) {
            if (mHandler != 0) {
                getMatrixJni(mHandler, matrix, index);
            }
        }
    }
    
    public void getMatrixEx(float[] matrix, int index) {
        synchronized (this) {
            if (mHandler != 0) {
                getMatrixExJni(mHandler, matrix, index);
            }
        }
    }

    public void finish() {
        synchronized (this) {
            if (mHandler != 0) {
                MyLog.d(mHandler + " " + finishJni(mHandler));
                mHandler = 0;
            }
        }
    }

    // for hyperlapse
    private static native final int setFixLevelJni(long handler, int level);

    private static native final int getFrameNumJni(long handler);

    private static native final int getCroppingSizeJni(long handler, int[] size);

    private static native final long initializeJni(ByteBuffer data,
            int in_width, int in_height, int out_width, int out_height,
            int format, int accuracy);
    
    private static native final long initializeExJni(ByteBuffer data,
            int in_width, int in_height, int out_width, int out_height,
            int dec_width, int dec_height, int format, int accuracy);

    private static native final int startJni(long handler);

    private static final native int processJni(long handler, ByteBuffer src,
            ByteBuffer dst, int index, int padding);
    
    private static final native int processExJni(long handler, ByteBuffer src,
            ByteBuffer dst, int index);

    private static native final int getMatrixJni(long hadler, float[] matrix,
            int index);
    
    private static native final int getMatrixExJni(long hadler, float[] matrix,
            int index);

    private static native final int finishJni(long handler);
}
