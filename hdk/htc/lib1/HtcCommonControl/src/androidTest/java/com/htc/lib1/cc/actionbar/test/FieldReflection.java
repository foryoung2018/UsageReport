
package com.htc.lib1.cc.actionbar.test;

import java.lang.reflect.Field;

public class FieldReflection {

    private Field mField;
    private boolean mIsPublic;

    public FieldReflection(Class<?> tClass, boolean isPubic, String fieldName) throws NoSuchFieldException {
        mIsPublic = isPubic;
        mField = tClass.getDeclaredField(fieldName);
    }

    public void set(Object clsInstance, Object value) throws IllegalAccessException, IllegalArgumentException {
        if (!mIsPublic) {
            mField.setAccessible(true);
        }
        mField.set(clsInstance, value);
    }

    public Object get(Object clsInstance) throws IllegalAccessException, IllegalArgumentException {
        if (!mIsPublic) {
            mField.setAccessible(true);
        }
        return mField.get(clsInstance);
    }

}
