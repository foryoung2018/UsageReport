
package com.htc.lib1.cs.app;

import android.content.Intent;
import android.os.Bundle;

/**
 * Helper class related to class loader. In Android system, a subprocess
 * will using the framework class loader insteadof application class loader.
 * In this case, it would need to specify the class loader to application
 * class loader when unmarshalling in the subprocess or will cause
 * {@link ClassNotFoundException} if customed {@link Parcelable} data existed.
 * See the following link for more explain. Note that the content is in
 * Simplified Chinese.
 * http://blog.csdn.net/yelangjueqi/article/details/41956731
 *
 * @author autosun_li@htc.com
 */
public class ClassLoaderUtils {
    /**
     * Ensure using application's class loader instead of framework's class loader
     * for {@link Bundle} unmarshalling.
     *
     * @param bundle {@link Bundle} to be ensured.
     */
    public static void ensureWithAppClassLoader(Bundle bundle) {
        // Check bundle itself.
        if (bundle.getClassLoader() == null) {
            bundle.setClassLoader(ClassLoaderUtils.class.getClassLoader());
        }

        // For all key
        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            if (obj instanceof Bundle) {
                ensureWithAppClassLoader((Bundle) obj);
            } else if (obj instanceof Intent) {
                ensureWithAppClassLoader((Intent) obj);
            }
        }
    }

    /**
     * Ensure using application's class loader instead of framework's class loader
     * for {@link Intent} unmarshalling.
     *
     * @param intent {@link Intent} to be ensured.
     */
    public static void ensureWithAppClassLoader(Intent intent) {
        // Ensure Intent extra.
        intent.setExtrasClassLoader(ClassLoaderUtils.class.getClassLoader());

        // Ensure all extra data.
        if (intent.getExtras() != null) {
            ensureWithAppClassLoader(intent.getExtras());
        }
    }
}
