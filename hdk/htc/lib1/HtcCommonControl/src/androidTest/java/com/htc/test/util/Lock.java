package com.htc.test.util;

public class Lock {
    private boolean mIsLock = true;
    private static final long MAXTIME = 10000;

    public synchronized void lock() {
        mIsLock = true;
    }

    public synchronized void unlockAndNotify() {
        mIsLock = false;
        notifyAll();
    }

    /**
     * Wait unlock
     *
     * @param maxtime The Maximum Time is 10s,Maximum waiting time
     */
    public synchronized void waitUnlock(long maxtime) {
        try {
            maxtime = (maxtime > MAXTIME) ? MAXTIME : maxtime;

            if (mIsLock) {
                wait(maxtime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
