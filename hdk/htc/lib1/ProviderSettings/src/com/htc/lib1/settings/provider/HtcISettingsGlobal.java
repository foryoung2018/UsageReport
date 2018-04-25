package com.htc.lib1.settings.provider;

/**
 * 
 * This interface is used to add HTC's global keys, and add some 
 * related APIs.
 * 
 * @since {@link Build.VERSION_CODES#JELLY_BEAN}, 2013.03.14
 * @author TJ Tsai
 */
/* default */ interface HtcISettingsGlobal {
	
    /**
     * <P>[Flip to mute] Indicates mode of Flip to mute
     * </P>
     * 
     * <H3>Value:</H3>
     * <UL>
     *    <LI> HTC_FLIP_TO_MUTE_ONCE(0): Mute once after flip</LI>
     *    <LI> HTC_FLIP_TO_MUTE_ALWAYS(1): Always mute after flip</LI>
     * </UL>
     * 
     * @author Alan Lee / Mingszu Liang
     * @since Sense 6.0, 2013.12.05
     */
	public static final String HTC_FLIP_TO_MUTE
			= "htc_flip_to_mute";
	public static final int HTC_FLIP_TO_MUTE_ONCE = 0;
	public static final int HTC_FLIP_TO_MUTE_ALWAYS = 1; // this is default value.

	/**
     * <P>[ActiveService] Indicates active engine enabled value
     * </P>
     * @author Alan Lee / Abnerl Wang
     * @since Sense 6.0, 2013.12.10
     */
	public static final String HTC_ACTIVE_ENGINE_ENABLE = "htc_active_engine_enable";
	
    /**
     * <P>[Easy access gesture] Indicates to enable/disable easy access
     * </P>
     * 
     * <H3>Value: 0(disable)/1(enable)</H3>
     * 
     * @author Alan Lee / Fisher Yu
     * @since Sense 6.0, 2013.12.05
     */
    public static final String HTC_EASY_ACCESS_ACTION = "HTC_EASY_ACCESS_ACTION"; 
    
    /**
     * <P>[Extreme Power Saver] Indicate Extreme Power Saver mode is enabled or disabled
     * </P>
     * <H3>Value: (integer)</H3>
     * <UL>
     *    <LI>0 -> Disabled</LI>
     *    <LI>1 -> Enabled</LI>
     * </UL>
     * @author Alan Lee / Diatango Lin
     * @since Sense 6.0, 2014.03.06
     */
    public static final String HTC_EXTREME_POWER_SAVER_STATE = "htc_extreme_power_saver_state";
    public static final int DEFAULT_HTC_EXTREME_POWER_SAVER_STATE = 0;

    /**
     * <P>[Extreme Power Saver] When battery level is under the threshold, if need to 
     * trigger Extreme Power Saver mode automatically
     * </P>
     * <H3>Value: (integer)</H3>
     * <UL>
     *    <LI>0 -> Not</LI>
     *    <LI>1 -> Will trigger Extreme Power Saver mode automatically </LI>
     * </UL>
     * @author Alan Lee / Diatango Lin
     * @since Sense 6.0, 2014.03.06
     */
    public static final String HTC_EXTREME_POWER_SAVER_AUTO = "htc_extreme_power_saver_auto";
    public static final int DEFAULT_HTC_EXTREME_POWER_SAVER_AUTO = 0;
    
    /**
     * <P>[Extreme Power Saver] The battery level to trigger Extreme Power Saver mode 
     * automatically 
     * </P>
     * <H3>Value: (integer)</H3>
     * <UL>
     *    <LI>0 -> Not specified</LI>
     *    <LI>5 -> The battery level threshold is 5%</LI>
     *    <LI>10 -> The battery level threshold is 10%</LI>
     *    <LI>15 -> The battery level threshold is 15%</LI>
     * </UL>
     * @author Alan Lee / Diatango Lin
     * @since Sense 6.0, 2014.03.06
     */
    public static final String HTC_EXTREME_POWER_SAVER_BATTERY_LEVEL = "htc_extreme_power_saver_battery_level";
    public static final int DEFAULT_HTC_EXTREME_POWER_SAVER_BATTERY_LEVEL = 10;
    
    /**
     * <P>[Performance Mode] Indicate the performance mode is enabled or disabled
     * </P>
     * <H3>Value: (integer)</H3>
     * <UL>
     *    <LI>0 -> Disabled</LI>
     *    <LI>1 -> Enabled</LI>
     * </UL>
     * @author Alan Lee / Diatango Lin
     * @since Sense 6.0, 2014.03.14
     */
    public static final String HTC_PERFORMANCE_MODE_STATE = "htc_performance_mode_state";
    public static final int DEFAULT_HTC_PERFORMANCE_MODE_STATE = 0;
}

