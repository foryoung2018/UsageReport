package com.htc.lib1.cc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.InflateException;
import android.view.View;
import android.view.ViewStub;

import java.lang.reflect.Constructor;
import java.util.regex.Pattern;

public class LayoutInflaterFactory2 implements Factory2 {
    private static final Pattern P = Pattern.compile("^com.htc.lib1.cc.*");
    private Object[] mArgs = new Object[2];
    static final Class<?>[] mConstructorSignature = new Class[] {
            Context.class, AttributeSet.class
    };

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!P.matcher(name).find()) {
            return null;
        }

        Constructor<? extends View> constructor = null;
        Class<? extends View> clazz = null;
        try {
            clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
            constructor = clazz.getConstructor(mConstructorSignature);
            mArgs[0] = context;
            mArgs[1] = attrs;

            constructor.setAccessible(true);
            final View view = constructor.newInstance(mArgs);
            if (view instanceof ViewStub) {
                // Use the same context when inflating ViewStub later.
                final ViewStub viewStub = (ViewStub) view;
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (null != mInflater) {
                    LayoutInflater cloneInflater = mInflater.cloneInContext(context);
                    viewStub.setLayoutInflater(cloneInflater);
                }
            }
            return view;
        } catch (NoSuchMethodException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + name);
            ie.initCause(e);
            ie.printStackTrace();
        } catch (ClassCastException e) {
            // If loaded class is not a View subclass
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Class is not a View " + name);
            ie.initCause(e);
            ie.printStackTrace();
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription() + ": Error inflating class " + (clazz == null ? "<unknown>" : clazz.getName()));
            ie.initCause(e);
            ie.printStackTrace();
        }
        return null;
    }
}
