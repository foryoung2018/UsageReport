package com.htc.lib1.exo.player;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

import android.content.Context;
import android.net.Uri;

public interface DataSourceHandler extends InvocationHandler{
    public void init(Context context, Uri uri, Map<String, String> headers);
    public void release();
}
