/*
 * Copyright (C) 2008 HTC Corporation.
 *
 */

package com.htc.lib1.HtcEasPim.eas;

import android.net.Uri;

/**
 * {@exthide}
 */
public class EASManager {
    /**
     * Exchange account have not configured.
     */
    public static final int ACNT_NOT_CONFIGURED = 0;

    /**
     * Exchange account already configured.
     */
    public static final int ACNT_CONFIGURED = 1;

    /**
     * Default Exchange protocol version
     */
    public static final String PROTOCOL_UNKNOWN = "Unknown";

    /**
     * The provider name of Exchange account type
     */
    public static final String EMAIL_PROVIDER_NAME = "Exchange";

    /**
     * The URI of Exchange account
     */
    public static final Uri EAS_ACCOUNTS_URI
            = Uri.parse("content://mail/accounts");
    /**
     * Intent for synchronizing operation start
     */
    public static final String INTENT_ALL_SYNC_START
            = "com.htc.eas.intent.all_sync_start";

    /**
     * Intent for synchronizing operation finish 
     */
    public static final String INTENT_ALL_SYNC_FINISH
            = "com.htc.eas.intent.all_sync_finish";

    /**
     * Intent action for deleting Exchange account finish
     */
    public static final String INTENT_DELETE_EXCHG_ACCOUNT
            = "com.htc.mail.eas.intent.delete_exchg_account";

    /**
     * Intent action for deleting mail finish
     */
    public static final String INTENT_DELETE_MAIL_FINISH
            = "com.htc.eas.intent.delete_mail_finish";

    /**
     * Intent for meeting invitation
     */
    public static final String INTENT_MEETING_INVITATION
            = "intent.eas.meeting_invitation";

    /**
     * Intent for trigger synchronizing contacts
     */
    public static final String INTENT_SYNC_CONTACTS
            = "com.htc.android.eas.syncContacts";

    /**
     * Intent for trigger synchronizing calendar
     */
    public static final String INTENT_SYNC_CALENDAR
            = "com.htc.android.eas.syncCalendar";

    /**
     * Intent for pause synchronizing operation
     */
    public static final String INTENT_PAUSE_SYNC 
            = "com.htc.eas.intent.pauseSync";

    /**
     * Intent for resume synchronizing operation
     */
    public static final String INTENT_RESUME_SYNC
            = "com.htc.eas.intent.resumeSync";

    /**
     * Intent parameter: account name;
     */
    public static final String EXTRA_ACCOUNT_NAME = "extra.eas.account_name";

    /**
     * Intent parameter: tag
     */
    public static final String EXTRA_TAG = "com.htc.eas.extra.tag";

    /**
     * Intent parameter: delay time
     */
    public static final String EXTRA_DELAY_TIME = "com.htc.eas.extra.delayTime";

    /**
     * Intent parameter: calendar event ID
     */
    public static final String EXTRA_CALENDAR_EVENT_ID
            = "com.htc.eas.extra.calendar.event_id";

    /**
     * The message class type of meeting accept.
     */
    public static final String MAIL_MESSAGE_CLASS_ACCEPT
            = "IPM.Schedule.Meeting.Resp.Pos";

    /**
     * The message class type of meeting tentative.
     */
    public static final String MAIL_MESSAGE_CLASS_TENTATIVE
            = "IPM.Schedule.Meeting.Resp.Tent";

    /**
     * The message class type of meeting decline.
     */
    public static final String MAIL_MESSAGE_CLASS_DECLINE 
            = "IPM.Schedule.Meeting.Resp.Neg";

    /**
     * Default meeting operation
     */
    public static final int MEETING_CMD_UNKNOWN = 0;

    /**
     * Meeting operation: Accept
     */
    public static final int MEETING_CMD_ACCEPT = 1;

    /**
     * @deprecated [Alternative solution]
     */
    /**@hide*/ 
    public static final int MEETING_CMD_TENTATICE = 2;

    /**
     * Meeting operation: Tentative
     */
    public static final int MEETING_CMD_TENTATIVE = 2;

    /**
     * Meeting operation: Decline
     */
    public static final int MEETING_CMD_DECLINE = 3;

    /**
     * Meeting operation: Forward meeting
     */
    public static final int MEETING_CMD_FORWARD_MEETING = 4;

    /**
     * Meeting operation: Propose time
     */
    public static final int MEETING_CMD_PROPOSE_NEW_TIME = 5;

    /**
     * Meeting operation: Invite
     */
    public static final int MEETING_CMD_INVITATION = 6;

    /**
     * Meeting operation: Cancel invite
     */
    public static final int MEETING_CMD_CANCEL_INVITE = 7;

}

