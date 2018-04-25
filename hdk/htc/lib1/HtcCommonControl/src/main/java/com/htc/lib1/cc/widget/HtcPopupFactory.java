package com.htc.lib1.cc.widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * @deprecated
 * @hide
 */
public class HtcPopupFactory {

    public interface HtcPopupBubble {
        void setOnDismissListener(PopupWindow.OnDismissListener listener);
        void setOnItemClickListener(Object listener);
        void setAdapter(ListAdapter adapter);
        void setModal(boolean isMode);
        void setAnchorView(View v);
        void setVerticalOffset(int offset);
        void setContentWidth(int width);
        void setInputMethodMode(int mode);
        void show();
        View getListView();
        boolean isShowing();
        void dismiss();
        void internalDismiss();
        void dismissWithoutAnimation();
    }

    static Constructor s_ListPopupBubbleContructor;
    static {
        try {
            Class c = Class.forName("com.htc.lib1.cc.widget.ListPopupBubbleWindow");
            s_ListPopupBubbleContructor = c.getConstructor(Context.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    static public HtcPopupBubble getHtcPopupBubble(Context context) {
        if ( null == s_ListPopupBubbleContructor )
            return null;
        try {
            Object obj= s_ListPopupBubbleContructor.newInstance(context);
            if(obj instanceof HtcPopupBubble){
                return (HtcPopupBubble) obj;
            }else{
                return null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

