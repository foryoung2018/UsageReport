package com.htc.lib1.cc.widget;
import com.htc.lib1.cc.R;

class HtcInputFieldUtil {
    /**
     * The public constant for user to set the mode of this widget.
     * This is used for user to put this widget on bright background.
     * For example: put this widget in a white scene.
     * This is the default value.
     */
    static final int MODE_BRIGHT_BACKGROUND = 0;

    /**
     * The public constant for user to set the mode of this widget.
     * This is used for user to put this widget on dark background.
     * For example: put this widget in a black scene.
     */
    static final int MODE_DARK_BACKGROUND = 1;

    /**
     * The public constant for user to set the mode of this widget.
     * This is used for full background to put this widget on bright background.
     * For example: compose input for message
     */
    static final int MODE_BRIGHT_FULL_BACKGROUND = 2;

    static final int MAX_ALPHA = 255;
    static final int MIN_ALPHA = 0;
    static final int DONW_DURATION = 300;

    final static int NUMBER_MODE = 3;
    final static int NUMBER_BACKGROUND_DRAWABLE = 2;
    final static int INDEX_DARK_BACKGROUND = 0;
    final static int INDEX_LIGHT_BACKGROUND = INDEX_DARK_BACKGROUND + NUMBER_BACKGROUND_DRAWABLE;
    final static int INDEX_LIGHTFULL_BACKGROUND = INDEX_LIGHT_BACKGROUND + NUMBER_BACKGROUND_DRAWABLE;
    final static int INDEX_ASSET_PRESSED = 0;
    final static int INDEX_ASSET_REST = 1;

    final static float REST_ALPHA = 1.0f;
    //>> Sense 60: disable alpha changes
    final static float DISABLED_ALPHA_LIGHT = 0.5f;
    final static float DISABLED_ALPHA_DARK = 0.4f;

    //>> Sense 60: default font style different
    final static int FONT_STYLE_LIGHT = R.style.input_default_m;
    final static int FONT_STYLE_DARK = R.style.b_button_primary_l;
    final static int FONT_STYLE_FULL = R.style.list_body_primary_m;

    static int mapXMLMode(int xmlMode){
        switch (xmlMode){
           case 0:
              return MODE_BRIGHT_BACKGROUND;
           case 1:
              return MODE_DARK_BACKGROUND;
           case 4:
              return MODE_BRIGHT_FULL_BACKGROUND;
           default:
              return MODE_BRIGHT_BACKGROUND;
        }
    }

    //>> Sense 60: default font style according to mode
    static int getDefaultFontStyleByMode(int mode){
        if(mode == MODE_BRIGHT_BACKGROUND){
            return FONT_STYLE_LIGHT;
        } else if(mode == MODE_DARK_BACKGROUND){
            return FONT_STYLE_DARK;
        } else if(mode == MODE_BRIGHT_FULL_BACKGROUND){
            return FONT_STYLE_FULL;
        }

        return FONT_STYLE_LIGHT;
    }
}