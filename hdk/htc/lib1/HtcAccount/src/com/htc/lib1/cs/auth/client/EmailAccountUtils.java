
package com.htc.lib1.cs.auth.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;

import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * Helper class to find proper email account and launch HTC mail client.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
public class EmailAccountUtils {
    private static HtcLogger sLogger = new AuthLoggerFactory(EmailAccountUtils.class).create();

    /**
     * Get the first preferred account or {@code null} if nothing found.
     * 
     * @param context Context to operate on.
     * @return Preferred account or {@code null} if no proper account found.
     */
    public static Account getPreferredAccount(Context context) {
        HtcAccountManager accntMgr =
                HtcAccountManagerCreator.get().createAsAuthenticator(context);
        Account accounts[] = accntMgr.getAccountsByType(EmailAccountUtilsDefs.MAIL_GMAIL);
        if (accounts.length > 0) {
            return accounts[0];
        } else if ((accounts = accntMgr.getAccountsByType(EmailAccountUtilsDefs.MAIL_GENERIC)).length > 0) {
            return accounts[0];
        } else if ((accounts = accntMgr.getAccountsByType(EmailAccountUtilsDefs.MAIL_HOTMAIL)).length > 0) {
            return accounts[0];
        } else if ((accounts = accntMgr.getAccountsByType(EmailAccountUtilsDefs.MAIL_EXCHANGE)).length > 0) {
            return accounts[0];
        }
        return null;
    }

    /**
     * Find the email account with given email address.
     * 
     * @param context Context to operate on.
     * @param emailAddress Name to looking for.
     * @return Account or {@code null} if nothing found.
     */
    public static Account findEmailAccount(Context context, String emailAddress) {
        sLogger.verboseS(emailAddress);
        AccountManager accntMgr = AccountManager.get(context);
        for (Account accnt : accntMgr.getAccounts()) {
            sLogger.debugS("account:", accnt.name, ", type:", accnt.type);
            if (accnt.name.equals(emailAddress)) {
                if (accnt.type.equals(EmailAccountUtilsDefs.MAIL_HOTMAIL)
                        || accnt.type.equals(EmailAccountUtilsDefs.MAIL_EXCHANGE)
                        || accnt.type.equals(EmailAccountUtilsDefs.MAIL_GENERIC)
                        || accnt.type.equals(EmailAccountUtilsDefs.MAIL_GMAIL)) {
                    return accnt;
                }
            }
        }
        return null;
    }

    /**
     * Generate intent to start with for given email address.
     * 
     * @param context Context to operate on.
     * @param emailAddress Email to use.
     * @return {@link Intent}. Never be {@code null}. However the intent might
     *         not be invalid if the app is running on a non-HTC device and HTC
     *         mail client doesn't exist, or the implementation of mail clients
     *         have been changed. It's caller's responsibility to handle the
     *         exception.
     */
    public static Intent getEmailActivityIntent(Context context, String emailAddress) {
        sLogger.debugS(emailAddress);
        Account userAccount = findEmailAccount(context, emailAddress);
        Intent mailIntent;

        if (userAccount == null) {
            sLogger.debug("No match account found.");
            mailIntent = new Intent(Intent.ACTION_VIEW);
            mailIntent.setClassName(EmailAccountUtilsDefs.MAIL_GENERIC,
                    EmailAccountUtilsDefs.MAIL_GENERIC + ".ProviderListScreen");
            mailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            if (userAccount.type.equals(EmailAccountUtilsDefs.MAIL_GMAIL)) {
                sLogger.debug("Found corresponding gmail account.");
                mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri mailUri = Uri.parse("content://gmail-ls/account/" + emailAddress);
                mailIntent.setDataAndType(mailUri, "application/gmail-ls");
                mailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            } else {
                sLogger.debug("Found corresponding mail account.");

                mailIntent = new Intent(Intent.ACTION_MAIN);
                mailIntent.addCategory("android.intent.category.LAUNCHER");
                mailIntent.setClassName(EmailAccountUtilsDefs.MAIL_GENERIC,
                        EmailAccountUtilsDefs.MAIL_GENERIC + ".MultipleActivitiesMain");
                mailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mailIntent.putExtra("accountId", findAccountId(context, emailAddress));
            }

        }

        mailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        sLogger.verboseS(mailIntent);
        return mailIntent;
    }

    /**
     * Find the account id in mail app of given email address.
     * 
     * @param context Context to operate on.
     * @param account Email address to find.
     * @return Id or -1 if not found.
     */
    public static long findAccountId(Context context, String account) {
        // Get client.
        Uri mailUri = Uri.parse("content://mail/accounts");
        ContentProviderClient client;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(mailUri);
        } else {
            client = context.getContentResolver()
                    .acquireContentProviderClient(mailUri);
        }
        if (client == null) {
            sLogger.error("Unable to make content provider connection.");
            return -1;
        }

        // SELECT _id, _name FROM accounts WHERE _name = <emailAddress>
        Cursor c = null;
        try {
            c = client.query(mailUri, new String[] {
                    "_id", "_name"
            }, "_name = ?", new String[] {
                    account
            }, null);
            if (c == null)
                throw new RemoteException("Query returns null cursor.");
        } catch (RemoteException e) {
            sLogger.error(e);
            client.release();
            return -1;
        }

        // Set the id, if any.
        long id = -1;
        if (c.moveToNext()) {
            try {
                id = c.getLong(c.getColumnIndex("_id"));
                String name = c.getString(c.getColumnIndex("_name"));
                sLogger.verboseS("id = ", id, ", name = ", name);
            } catch (RuntimeException e) {
                /*
                 * If the column value is null, an runtime exception will be
                 * thrown. In this case we treat it as id not found and return
                 * -1.
                 */
                sLogger.error(e);
            }
        } else {
            // No account found.
            sLogger.debugS("No record found with given account name ", account);
        }

        // Cleanup.
        c.close();
        client.release();

        return id;
    }

    /**
     * Find the message id in mail app with given sender email address and
     * account id. If there are multiple messages from the same sender, it
     * checks the date and returns the latest one.
     * 
     * @param context Context to operate on.
     * @param accountId Receiver account id retrieved from {@link #findAccountId}
     *            .
     * @param senders Sender email address list.
     * @return Message id.
     */
    public static long findMessageIdBySenders(Context context, long accountId, String[] senders) {
        // Get client.
        Uri mailUri = Uri.parse("content://mail/messages");
        ContentProviderClient client;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(mailUri);
        } else {
            client = context.getContentResolver()
                    .acquireContentProviderClient(mailUri);
        }
        if (client == null) {
            sLogger.error("Unable to make content provider connection.");
            return -1;
        }

        // Build selection string.
        StringBuilder selectionBuilder = new StringBuilder("_account = ? AND ( ");
        for (int i = 0; i < senders.length - 1; i++) {
            selectionBuilder.append("_fromEmail = ? OR ");
        }
        selectionBuilder.append("_fromEmail = ? )");
        String selection = selectionBuilder.toString();

        // Build args list.
        ArrayList<String> argsArray = new ArrayList<String>();
        argsArray.add(String.valueOf(accountId));
        for (String sender : senders)
            argsArray.add(sender);
        String[] args = argsArray.toArray(new String[argsArray.size()]);

        sLogger.verboseS("Selection: ", selection);
        sLogger.verboseS("args: ", Arrays.toString(args));

        /*
         * SELECT _id, _account, _fromEmail FROM messages WHERE _account =
         * <accountId> AND (_fromEmail = <senders[0]> OR _fromEmail =
         * <senders[1]>...)
         */
        Cursor c = null;
        try {
            c = client.query(mailUri, new String[] {
                    "_id", "_account", "_fromEmail", "_date"
            }, selection, (String[]) args, null);
            if (c == null)
                throw new RemoteException("Query returns null cursor.");
        } catch (RemoteException e) {
            sLogger.error(e);
            client.release();
            return -1;
        }

        // Find the latest message with given sender.
        long id = -1;
        long date = -1;
        while (c.moveToNext()) {
            try {
                long tmpId = c.getLong(c.getColumnIndex("_id"));
                long tmpDate = c.getLong(c.getColumnIndex("_date"));
                String from = c.getString(c.getColumnIndex("_fromEmail"));
                sLogger.verboseS("Found message id = ", tmpId, ", sender = ", from, ", date = ",
                        new Date(tmpDate));
                if (tmpDate > date) {
                    date = tmpDate;
                    id = tmpId;
                }
            } catch (RuntimeException e) {
                /*
                 * If any of queried columns has null value, a runtime exception
                 * will be thrown. In this case we treat is as not found.
                 */
                sLogger.error(e);
                id = -1;
            }
        }

        if (id == -1) {
            // No account found.
            sLogger.debugS("No record found with given sender ", Arrays.toString(senders));
        }

        // Cleanup.
        c.close();
        client.release();

        return id;
    }

    /**
     * Find the mail message content from mail app with given message id.
     * 
     * @param context Context to operate on.
     * @param messageId Message id from {@link #findMessageIdBySenders}.
     * @return Message content or {@code null} if not found.
     */
    public static String findMessageContentById(Context context, long messageId) {
        // Get client.
        Uri mailUri = Uri.parse("content://mail/parts");
        ContentProviderClient client;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(mailUri);
        } else {
            client = context.getContentResolver()
                    .acquireContentProviderClient(mailUri);
        }
        if (client == null) {
            sLogger.error("Unable to make content provider connection.");
            return null;
        }

        // SELECT _text, _message FROM parts WHERE _message = <messageId>
        Cursor c = null;
        try {
            c = client.query(mailUri, new String[] {
                    "_text", "_message"
            }, "_message = ?", new String[] {
                    String.valueOf(messageId)
            }, null);
            if (c == null)
                throw new RemoteException("Query returns null cursor.");
        } catch (RemoteException e) {
            sLogger.error(e);
            client.release();
            return null;
        }

        // Set the content, if any.
        String text = null;
        if (c.moveToNext()) {
            try {
                text = c.getString(c.getColumnIndex("_text"));
                String msgId = c.getString(c.getColumnIndex("_message"));

                // Log the first 200 characters of the mail content.
                if (text.length() <= 200)
                    sLogger.verboseS("messageId = ", msgId, ", content = ", text);
                else
                    sLogger.verboseS("messageId = ", msgId, ", content = ",
                            text.subSequence(0, 200),
                            "...");
            } catch (RuntimeException e) {
                /*
                 * If either column has null value, a runtime exception will be
                 * thrown. In this case we simply ignore it and and treat it as
                 * message not found.
                 */
                sLogger.error(e);
            }
        } else {
            sLogger.info("No text found with given message id ", messageId);
        }

        // Cleanup.
        c.close();
        client.release();
        return text;
    }

    /**
     * Find the first UUID in the given message.
     * 
     * @param message Message to find. If passing {@code null} or empty string
     *            it takes no effects.
     * @return First UUID or {@code null} if not found.
     */
    public static UUID findFirstUuidFromMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            Matcher matcher = EmailAccountUtilsDefs.PATTERN_UUID.matcher(message);
            if (matcher.find()) {
                String uuid = message.subSequence(matcher.start(), matcher.end()).toString();
                sLogger.debugS("UUID = ", uuid);
                return UUID.fromString(uuid);
            }
        }
        return null;
    }

    /**
     * Find the email verification id with given account name. It searches for
     * the account in mail account list, find the latest message sent from HTC
     * Account service, and retrieve the UUID from the message content.
     * 
     * @param context Context to operate on.
     * @param account Account name (email) of the user.
     * @return Email verification ID or {@code null} if not found.
     */
    public static UUID findEmailVerificationId(Context context, String account) {
        return findFirstUuidFromMessage(findMessageContentById(
                context,
                findMessageIdBySenders(context, findAccountId(context, account),
                        EmailAccountUtilsDefs.EMAIL_VERIFICATION_SENDERS)));
    }
}
