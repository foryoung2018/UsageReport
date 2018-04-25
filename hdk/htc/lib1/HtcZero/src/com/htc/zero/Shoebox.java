package com.htc.zero;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Collection;

/**
 * Contract class for shoebox engines
 */
public interface Shoebox {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean RECALL_DAY_TIME = true;
    public static final boolean RECALL_LOCATION_FAR = false;
    public static final boolean RECALL_ANNIVERSARY_3M = false;
    public static final boolean RECALL_ANNIVERSARY_6M = false;
    public static final String L_TAG = "ShoeBox_LGG";

    public interface Intents {
        /**
         * Extra defining a command for an intent sent to Shoebox engine. Action should be null.
         */
        public static final String EXTRA_COMMAND = "command";

        /**
         * @see #ACTION_SUBMIT
         */
        public static final int EXTRA_COMMAND_ADD_WEIGHTS = 2;
        /**
         * this command notify that some object has been cloned, with the ordered list of old (source) and new (clone) ids.<br><br>
         * 
         * In the following sample, items 1,2,3 has been moved and their respective new ids are 5,4,6. ie (1->5, 2->4, 3->6)
         * <pre>
         * Intent i = new Intent();
    	 * i.setAction(Shoebox.Intents.ACTION_SUBMIT);
    	 * i.putExtra(Shoebox.Intents.EXTRA_COMMAND,Shoebox.Intents.EXTRA_COMMAND_UPDATE_IDS /* 0x3 *\/);
    	 * i.putExtra(Shoebox.Intents.KEY_SOURCE /* "source" *\/, new long[]{1,2,3});
    	 * i.putExtra(Shoebox.Intents.KEY_CLONE /* "clone" *\/, new long[]{5,4,6});
    	 * ctx.startService(i);
    	 * </pre>
    	 * 
    	 * Multiple intent can be sent simultaneously but each one must be consistent. all the old and new id for a given set of file must be present in a single intent and be ordered accordingly. <br>
    	 * If the source and clone array's size is not equal, the intent will be ignored.<br>
    	 * For multimedia items like burst shot, or zoes, only the main item should be updated. it should have no significant impact if it was otherwise beside possible weight lost and negative performance impact.<br>
         *
         */
        public static final int EXTRA_COMMAND_UPDATE_IDS =  0x03;	
        public static final String KEY_WEIGHTS = "weights";
        public static final String KEY_CLONE = "clone";
        public static final String KEY_SOURCE = "source";


        /**
         * Sumbit action is used as main intent to contact <code>ShoeboxEngine</code>.
         * Combined with <code>EXTRA_COMMAND</code> it can add a weight using columns defined in <code>WeightsImport</code>
         * <pre>
         * Context c = getContext();
         * Intent i = new Intent(Shoebox.ACTION_SUBMIT);
         * i.putExtra(Shoebox.Intents.EXTRA_COMMAND,Shoebox.Intents.EXTRA_COMMAND_ADD_WEIGHT);
         * i.putExtra(Shoebox.WeightsImport.SOURCE, source); // as ShoeboxItem.TYPE_*
         * i.putExtra(Shoebox.WeightsImport.FOREIGN_ID, foreignId);
         * i.putExtra(Shoebox.WeightsImport.WEIGHT_TYPE, weightType);
         * i.putExtra(Shoebox.WeightsImport.WEIGHT_VALUE, weightValue);
         * c.startService(i);
         * </pre>
         * alone is used to bind an application to the ShoeboxEngine service through aidl.ShoeboxEngineAIDL.
         */
        public static final String ACTION_SUBMIT = "com.htc.zero.SUBMIT";
        
        /**
         *	From Android L, AP must use explicit intent to start RelevanceEngine service.
         *  Add ACTION_SUBMIT_COMPONENT to ease AP's usage.
         */
        public static final ComponentName ACTION_SUBMIT_COMPONENT = new ComponentName("com.htc.zero", "com.htc.zero.engine.RelevanceEngine");
        
        /* *
         * Start asynchronous memory computing.
         * Otherwise this action is run 5 to 60 minutes after the user plug the phone on battery.
         */
        public static final String ACTION_COMPUTE_RANK = "com.htc.zero.ACTION_COMPUTE_RANK";
    }

    public static final String RANK_AUTHORITY = "com.htc.zero.engine.provider.rank";

    /**
     * Provides rank for v_folder or individual media
     */
    public class Rank {
        @Deprecated
        public static class RankUri {
            private String mCollection;
            private String mCollectionId;
            private boolean mHighlight;
            private boolean mSignature;
            private String mSeed;
            private boolean mCover;
            private String mExtra;

            protected RankUri() {
            }

            @Deprecated
            public RankUri withCollection(String collection) {
                mCollection = collection;
                return this;
            }

            @Deprecated
            public RankUri withCollection(int collection) {
                mCollection = Integer.toString(collection);
                return this;
            }

            @Deprecated
            public RankUri withCollection(int collection, long id) {
                mCollection = Long.toString(collection);
                mCollectionId = Long.toString(id);
                return this;
            }

            @Deprecated
            public RankUri withCollection(String collection, long id) {
                mCollection = collection;
                mCollectionId = Long.toString(id);
                return this;
            }

            @Deprecated
            public RankUri withCollection(String collection, String id) {
                mCollection = collection;
                mCollectionId = id;//(long) mcmIdToInt(id);
                return this;
            }

            @Deprecated
            public RankUri withExtra(String s) {
                mExtra = s;
                return this;
            }

            @Deprecated
            public RankUri highlightOnly() {
                mHighlight = true;
                return this;
            }

            @Deprecated
            public RankUri withSignature() {
                mSignature = true;
                return this;
            }

            @Deprecated
            public RankUri cover() {
                mCover = true;
                return this;
            }

            @Deprecated
            public RankUri recommend() {
                mSeed = "recommend";
                return this;
            }

            @Deprecated
            public RankUri withSeed(String s) {
                mSeed = "seed=" + s;
                return this;
            }

            @Deprecated
            public RankUri withSeed(Collection<Long> s) {
                return withSeed(s.toArray(new Long[s.size()]));
            }

            @Deprecated
            public RankUri withSeed(Integer[] s) {
                throw new IllegalStateException("please use withSeed(Long[]) from now on.");
            }

            @Deprecated
            public RankUri withSeed(Long[] s) {
                if (s.length < 1)
                    throw new IllegalArgumentException("seed array must not be empty");
                StringBuilder sb = new StringBuilder(s.length * 5);
                sb.append("seed=");
                sb.append(s[0]);
                if (s.length > 1) {
                    for (int i = 1; i < s.length; i++) sb.append(',').append(s[i]);
                }
                mSeed = sb.toString();
                return this;
            }

            @Deprecated
            public Uri build() {
                StringBuilder sb = new StringBuilder(100);
                sb.append("content://").append(RANK_AUTHORITY).append("/rank");
                if (mCollection != null) sb.append("/").append(mCollection);
                if (mCollectionId != null) sb.append("/").append(mCollectionId);
//                if(mHighlight||mSignature||mSeed!=null||mExtra!=null)
                sb.append('?');
                boolean ampersand = false;
                if (mHighlight) {
                    ampersand = true;
                    sb.append("highlight");
                }
                if (mSignature) {
                    if (ampersand) sb.append('&');
                    sb.append("signature");
                    ampersand = true;
                }
                if (mSeed != null) {
                    if (ampersand) sb.append('&');
                    sb.append(mSeed);
                }
                if (mCover) {
                    if (ampersand) sb.append('&');
                    sb.append("cover");
                }
                if (mExtra != null) {
                    if (ampersand) sb.append('&');
                    sb.append(mExtra);
                }

                return Uri.parse(sb.toString());
            }
        }

        @Deprecated
        public static RankUri rankUri() {
            return new RankUri();
        }

        /**
         * @since 1.0
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + RANK_AUTHORITY + "/rank");

        @Deprecated
        public static final Uri getCollectionUri(String collectionName) {
            return Uri.withAppendedPath(CONTENT_URI, collectionName);
        }

        @Deprecated
        public static final Uri getCollectionUri(String collectionName, long id) {
            return Uri.parse(String.format("content://%s/rank/%s/%d", Shoebox.RANK_AUTHORITY, collectionName, id));
        }

        @Deprecated
        public static final String MCM_EVENT = "mcm_event";
        @Deprecated
        public static final String MCM_ALBUM = "mcm_album";
        public static final String MEMORY = "memory";

        public static class MediaType {
            public static final int UNKNOWN = -1;
            public static final int PICTURE = 0;
            public static final int VIDEO = 1;
            public static final int BURST = 2;
            public static final int BURST_DATA = -2;
            public static final int ZOE = 3;
            public static final int ZOE_DATA = -3;
            public static final int PANORAMA_PLUS = 4;
            public static final int SLOW_MOTION = 5;
            public static final int GIF = 6;
        }

        /**
         * Defines provider's columns
         */
        public interface Columns {
            /**
             * id from media store <br> TYPE: INTEGER
             *
             * @since 1.0
             */
            public static final String _ID = MediaStore.MediaColumns._ID;

            /**
             * expressed rank for the current media <br> TYPE: FLOAT
             *
             * @since 1.0
             */
            public static final String RANK = "rank";
            public static final String SOURCE_TYPE = "source_type";
            public static final String MEDIA_TYPE = "htc_media_type";
            public static final String M_COMPUTE = "m_compute";
            public static final String MEDIA_GROUP = "m_group";
            public static final String SIMILARITY_GROUP = "similarity_id";
            public static final String LAST_UPDATE = "last_update";

            public static final String GROUP_TYPE = "g_typ";
            public static final String GROUP_ID = "g_id";
            public static final String GROUP_RANK = "g_rank";

            public static final String TITLE = "gd_title";
            public static final String SIZE = "gd_size";
            public static final String TS_START = "gd_start";
            public static final String TS_END = "gd_end";
            public static final String LATITUDE = "gd_lat";
            public static final String LONGITUDE = "gd_lng";
            public static final String AVG_RANK = "gd_rank";
            public static final String LAST_GROUP_UPDATE = "gd_last_update";

            public static final String SIGNATURE = "signature";
        }
    }


    public static final String MEMORY_FEED_AUTHORITY = "com.htc.zero.engine.provider.memfeed";

    /**
     *
     */
    public interface MemoryFeed {
        public static final Uri CONTENT_URI = Uri.parse("content://" + MEMORY_FEED_AUTHORITY + "/fetch");
        public static final Uri CONTENT_URI_WITH_UPDATE = Uri.parse("content://" + MEMORY_FEED_AUTHORITY + "/update");

        /**
         * In the case of MemoryFeed queries, the recall_type column will define the type of
         * context matched. The priority is also defined here.
         *
         * @see "CONTENT_URI"
         */
        public interface RecallType {
            public static final int RULE_ANNIVERSARY = 1;
            public static final int WEEK_END = 2;
            public static final int ANNIVERSARY_6MONTH = 3;
            public static final int ANNIVERSARY_3MONTH = 4;
            public static final int DAY_MORNING = 5;
            public static final int DAY_AFTERNOON = 6;
            public static final int DAY_EVENING = 7;
            public static final int LOCATION_CLOSE = 8;
            public static final int LOCATION_FAR = 9;
            public static final int RECENT = 10;

            public static final int MAX = 10;

            public static final int[] TYPE_PRIORITY = {
                    -1,//padding
                    1, 2, 3, 4,
                    6, 6, 6, // DAY_*
                    5,
                    9,
                    6
            };
        }

        /**
         * Defines provider's columns
         */
        public interface Columns {
            /**
             * Reason for this item to be recalled in the feed <br> TYPE: INTEGER
             */
            public static final String RECALL_TYPE = "rectype";
            /**
             * Localized string describing the reason for this item to be recalled in the feed <br> TYPE: TEXT
             */
            public static final String RECALL_TYPE_STRING = "rectype_string";
            /**
             * Should this event be returned to prism (0 = not prism): INTEGER
             */
            public static final String PRISM_FEED = "prism_feed";

            public static final String COVER_ID = "cover_id";
            public static final String COVER_PATH = "cover_path";
            public static final String TITLE = "title";
            public static final String LAST_TIMESTAMP = "last_timestamp";
        }
    }


    public static final String INT_WEIGHT_AUTHORITY = "com.htc.zero.engine.provider.weight.int";

    /**
     * Defines temporary storage (append only) for weights.
     * Usage is as follow. An app percieve a user action that maps to a weight, it sends an
     * intent including it <code>SOURCE</code> also known as <code>ShoeboxItem.TYPE_*</code>,
     * as well as the <code>_ID</code> from the remote provider, the weight type and value..
     */
    public interface WeightsImport {
        public static final Uri CONTENT_URI = Uri.parse("content://" + WEIGHT_AUTHORITY + "/weight");

        /**
         * Defines provider's columns
         */
        public interface Columns {
            /**
             * not used. TYPE: INTEGER
             */
            public static final String SOURCE = "source";
            /**
             * id of the file in media store. TYPE: INTEGER or LONG
             */
            public static final String FOREIGN_ID = "fid";
            /**
             * Type of the weight. TYPE: INTEGER
             */
            public static final String WEIGHT_TYPE = "weight";
            /**
             * Value of the weight. TYPE: TEXT
             */
            public static final String WEIGHT_VALUE = "value";
            /**
             * Mark is an internal field used to mark weight during the import process.
             * That way, we guarantee that no new weight will be deleted by accident.
             */
            public static final String MARK = "mark";
        }
    }

    public static final String WEIGHT_AUTHORITY = "com.htc.zero.engine.provider.weight";

    /**
     * Defines the final weight repository. Weight are to be indexed on a stable id.
     */
    public interface Weights {
        /* *
         * Provides
         * <ul>
         * 		<li>- direct insert memory_weight/type/fid {weight_type, value}
         *  	<li>- direct delete memory_weight/type/fid
         *  	<li>- direct query memory_weight/type/fid = {weight_type, value}*
         *  	<li>- basic insert memory_weight {_id , weight_type, value}
         * </ul>
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + INT_WEIGHT_AUTHORITY + "/memory_weight");

        /**
         * Defines provider's columns
         */
        public interface Columns {
            /**
             * id of the file in media store. TYPE: INTEGER
             */
            public static final String MEMENTO_ID = "uid";
            /**
             * Type of the weight. TYPE: INTEGER
             */
            public static final String WEIGHT_TYPE = "weight"; // int weight type
            /**
             * Value of the weight. TYPE: TEXT
             */
            public static final String WEIGHT_VALUE = "value"; // String value
        }
    }


}