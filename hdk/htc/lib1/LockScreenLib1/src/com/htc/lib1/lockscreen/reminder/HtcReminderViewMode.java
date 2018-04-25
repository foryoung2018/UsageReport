package com.htc.lib1.lockscreen.reminder;

/**
 * HtcReminderView:
 * View Priority Order
 * 
 * [NOTE] 
 * We can't change the defined value, only can add the new parameter
 * If need to change the original order, we only can change logic of the service.
 */
public class HtcReminderViewMode {
    /** DualCall */
    public static final int DUAL_CALL_MODE       = 1000;
    /** InCall */
    public static final int CALL_MODE            = 2000;
    /** Decline */
    public static final int DECLINE_MODE         = 3000;
    /**CMAS Emergency */
    public static final int CMAS_EMERGENCY_MODE  = 4000;
    /** Alarm */
    public static final int ALARM_MODE           = 5000;
    /** Time */
    public static final int TIMER_MODE           = 6000;
    /** BT */
    public static final int BLUETOOTH_MODE       = 7000;
    /** Calendar */
    public static final int CALENDAR_MODE        = 8000;
    /** Task */
    public static final int TASK_MODE            = 9000;
    /** UnKnown */
    public static final int UNKNOWN_MODE         = 50000;
    /** Invalid */
    public static final int INVALID_MODE         = 999999999;
}
