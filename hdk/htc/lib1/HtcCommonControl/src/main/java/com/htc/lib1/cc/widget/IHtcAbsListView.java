
package com.htc.lib1.cc.widget;

/**
 * Define the animation type and API for HtcListView and HtcGridView.
 */
public interface IHtcAbsListView {
    /**
     * The over-scroll animation.
     */
    public final static int ANIM_OVERSCROLL = 1;

    /**
     * The delete animation.
     */
    public final static int ANIM_DEL = 2;

    /**
     * The intro animation for HtcGridView.
     */
    public final static int ANIM_INTRO = 4;

    /**
     * Enable or disable the animations: {@link #ANIM_OVERSCROLL},
     * {@link #ANIM_DEL} and {@link #ANIM_INTRO}. By default, all animations are
     * enabled. Use the bit operation, e.g.: ANIM_OVERSCROLL | ANIM_INTRO.
     *
     * @param animationType The animation type.
     * @param enable True if enable the animations.
     */
    public void enableAnimation(int animationType, boolean enable);
}
