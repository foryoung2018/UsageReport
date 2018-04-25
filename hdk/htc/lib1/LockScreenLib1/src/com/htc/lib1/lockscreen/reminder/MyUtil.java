package com.htc.lib1.lockscreen.reminder;

import android.os.Handler;
import android.os.Message;


/** @hide */
class MyUtil {
    
    static void sendMessage(Handler handler, int what) {
        sendMessage(handler, what, 0);
    }
    
    static void sendMessage(Handler handler, int what, long delay) {
        if (handler == null) {
            return;
        }
        if (delay > 0) {
            handler.sendEmptyMessageDelayed(what, delay);
        }
        else {
            handler.sendEmptyMessage(what);
        }
    }
    
    static void sendMessage(Handler handler, Message msg) {
        sendMessage(handler, msg, 0);
    }
    
    static void sendMessage(Handler handler, Message msg, long delay) {
        if (handler == null) {
            return;
        }
        if (delay > 0) {
            handler.sendMessageDelayed(msg, delay);
        }
        else {
            handler.sendMessage(msg);
        }
    }
    
    static void removeMessage(Handler handler, int what) {
        if (handler == null) {
            return;
        }
        handler.removeMessages(what);
    }    
  }
