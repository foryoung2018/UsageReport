package com.htc.zero;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


public class ProviderDemo {


     /* **************************************
     *
     *            Ranks & Highlights
     *
     ************************************** */


    public static final String[] rankProjection = {
            Shoebox.Rank.Columns.GROUP_TYPE,                // MCM Source   (to create intent)
            Shoebox.Rank.Columns.GROUP_ID,                  // MCM collection id (to create intent)
            Shoebox.Rank.Columns._ID,
            MediaStore.Files.FileColumns.DATA,
            Shoebox.Rank.Columns.RANK,
            Shoebox.Rank.Columns.GROUP_RANK,
            //"( "+Shoebox.Rank.Columns.GROUP_RANK + " > 0.58) as isHighlight"
    };

    /**
     * This shows the basic treatment :
     * - query for a memory
     * - query for recommended items
     * - query by seed
     * <p/>
     * And a more complex query
     */
    public static void rankDemo(Context ctx) {
        rankProvider(ctx, "RankPerCollection",
                Uri.parse(Shoebox.Rank.CONTENT_URI + "/" + Shoebox.Rank.MEMORY + "/128"));

        rankProvider(ctx, "HighlightPerCollection",
                Uri.parse(Shoebox.Rank.CONTENT_URI + "/" + Shoebox.Rank.MEMORY + "/128?highlight"));

        rankProvider(ctx, "prism_recommend",
                Uri.parse(Shoebox.Rank.CONTENT_URI + "/" + Shoebox.Rank.MEMORY + "?recommend"));

        rankProvider(ctx, "prism_seed",
                Uri.parse(Shoebox.Rank.CONTENT_URI + "/" + Shoebox.Rank.MEMORY + "?seed=140,123"));

        /* custom query can be performed, for instance to select among a set of ids*/
        Cursor cur = null;
        try{
            cur = ctx.getContentResolver().query(
                    // the base uri will access all the
                    Uri.parse(Shoebox.Rank.CONTENT_URI + "/" + Shoebox.Rank.MEMORY),
                    rankProjection,
                    Shoebox.Rank.Columns._ID + " IN (3829,2871,1836,4983,5920,293,22,32,128,129,3293,2301)", new String[]{},
                    Shoebox.Rank.Columns.RANK + " DESC" // latest memory first
            );
            Log.i("shoebox_re_test", "customSearch : query  " + cur.getCount());
            printCursor("customSearch", cur);
        } finally {
            if (cur != null) cur.close();
        }

    }

    /**
     * Helper to query android's content provider interface
     */
    public static void rankProvider(Context ctx, String name, Uri contentUri) {
        Log.i("shoebox_re_test", name + " : query for " + contentUri);
        Cursor cur = null;
        try{
            cur = ctx.getContentResolver().query(
                    contentUri,
                    rankProjection,
                    null, null,
                    Shoebox.Rank.Columns.RANK + " DESC" // latest memory first
            );
            printCursor(name, cur);
        } finally {
            if (cur != null) cur.close();
        }
    }

    public static void printCursor(String name, Cursor cur) {
        try {
            if ((cur != null) && (cur.getCount() > 0)) {
                ContentValues cv = new ContentValues();
                while (cur.moveToNext()) {
                    android.database.DatabaseUtils.cursorRowToContentValues(cur, cv);
                    //Search for "adb logcat | grep shoebox_re_test
                    Log.i("shoebox_re_test", name + " : " + cv);
                }
            }
        } finally {
            if (cur != null) cur.close();
        }
    }


    /* **************************************
     *
     *              Memory Feed
     *
     ************************************** */
    // Memory feed return the contextual selection to display on prism

    private static final String[] memFeedProjection = {
            Shoebox.Rank.Columns.GROUP_TYPE,                // MCM Source   (to create intent)
            Shoebox.Rank.Columns.GROUP_ID,                  // MCM collection id (to create intent)
            Shoebox.MemoryFeed.Columns.RECALL_TYPE_STRING,  // description (secondary text)
            Shoebox.MemoryFeed.Columns.COVER_ID,            // MediaStore cover id
            Shoebox.MemoryFeed.Columns.COVER_PATH,          // Cover path from media Store
            Shoebox.MemoryFeed.Columns.LAST_TIMESTAMP,      // Timestamp
            Shoebox.MemoryFeed.Columns.TITLE                // Caption (main text)
    };

    public static void fetchMemFeed(Context ctx) {
        //Pretend to fetchupdate
        Cursor cur = ctx.getContentResolver().query(
                Shoebox.MemoryFeed.CONTENT_URI_WITH_UPDATE,
                memFeedProjection,
                null, null,
                Shoebox.MemoryFeed.Columns.LAST_TIMESTAMP + " DESC" // latest memory first
        );
        try {

            if ((cur != null) && (cur.getCount() > 0)) {
                ContentValues cv = new ContentValues();
                while (cur.moveToNext()) {
                    android.database.DatabaseUtils.cursorRowToContentValues(cur, cv);
                    //Search for "adb logcat | grep shoebox_re_test
                    Log.i("shoebox_re_test", "Memfeed : " + cv);
                }
            } else Log.i("shoebox_re_test", "Memfeed has currently not data");
        } finally {
            if (cur != null) cur.close();
        }
    }
    
    public static void moveMedias(Context ctx){
    	Intent i = new Intent();
    	i.setAction(Shoebox.Intents.ACTION_SUBMIT);
    	i.putExtra(Shoebox.Intents.EXTRA_COMMAND,Shoebox.Intents.EXTRA_COMMAND_UPDATE_IDS /* 0x3 */);
    	i.putExtra(Shoebox.Intents.KEY_SOURCE /* "source" */, new long[]{1,2,3});
    	i.putExtra(Shoebox.Intents.KEY_CLONE /* "clone" */, new long[]{4,5,6});
    	ctx.startService(i);
    }
}
