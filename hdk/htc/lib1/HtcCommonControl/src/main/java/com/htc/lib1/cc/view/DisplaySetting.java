
package com.htc.lib1.cc.view;

import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * control panel 3D display setting
 */
public class DisplaySetting
{
    /**
     * stereoscopic 3D format off
     */
    public static final int STEREOSCOPIC_3D_FORMAT_OFF          = 0;

    /**
     * stereoscopic 3D format side-by-side
     */
    public static final int STEREOSCOPIC_3D_FORMAT_SIDE_BY_SIDE = 1;

    /**
     * stereoscopic 3D format top-bottom
     */
    public static final int STEREOSCOPIC_3D_FORMAT_TOP_BOTTOM   = 2;

    /**
     * stereoscopic 3D format interleaved
     */
    public static final int STEREOSCOPIC_3D_FORMAT_INTERLEAVED  = 3;

    /**
     * enable or disable 3D display
     *
     * @param surface the surface instance from SurfaceView
     * @param format the stereoscopic 3D format constant value
     *
     * @return true for success and false for fail
     */
    static public boolean setStereoscopic3DFormat(Surface surface,int format)
    {
        if(surface!=null&&surface.isValid())
        {
            switch(format)
            {
                case STEREOSCOPIC_3D_FORMAT_OFF:
                    //vincent surface.setExternalDisplay(SurfaceHolder.STEREOSCOPIC_3D_FORMAT_OFF);
                    return true;

                case STEREOSCOPIC_3D_FORMAT_SIDE_BY_SIDE:
                    //vincent surface.setExternalDisplay(SurfaceHolder.STEREOSCOPIC_3D_FORMAT_SIDE_BY_SIDE);
                    return true;

                case STEREOSCOPIC_3D_FORMAT_TOP_BOTTOM:
                    //vincent surface.setExternalDisplay(SurfaceHolder.STEREOSCOPIC_3D_FORMAT_TOP_BOTTOM);
                    return true;

                case STEREOSCOPIC_3D_FORMAT_INTERLEAVED:
                    //vincent surface.setExternalDisplay(SurfaceHolder.STEREOSCOPIC_3D_FORMAT_INTERLEAVED);
                    return true;
            }
        }

        return false;
    }
}
