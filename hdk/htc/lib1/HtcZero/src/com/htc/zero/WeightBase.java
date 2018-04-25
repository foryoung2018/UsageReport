package com.htc.zero;

/**
 * Defines weight type and name.
 */
public abstract class WeightBase {

    /*
     * WEIGHT TYPES
     */
    public static final class WeightTypes {
        /**
         * Defines a weight depending on the type of memory, a memory of some type can be more relevant
         * than other memories. For instance basic time/space memory is less relevant than a memory about
         * holidays or wife/kids.
         */
        public static final int WEIGHT_TYPE_CLUSTER_TYPE = 1;
        /**
         * Has the item be manually tagged should it have a higher relevance.
         */
        public static final int WEIGHT_TYPE_TAG = 2;
        /**
         * Counts the number of share action on an item. The more an item has been shared, the more relevant.
         */
        public static final int WEIGHT_TYPE_SHARE_COUNT = 3;
        /**
         * Counts the number of zoom action on an item. The more an item has been zoomed, the more relevant.
         */
        public static final int WEIGHT_TYPE_ZOOM = 6;
        /**
         * 1 if manually set to favorite, 0 if manually remove from favorite
         */
        public static final int WEIGHT_TYPE_FAVORITE = 8;
        /**
         * Counts the number of view action on an item. The more an item has been viewed, the more relevant.
         */
        public static final int WEIGHT_TYPE_VIEW_COUNT = 12;
        /**
         * Counts the duration of view action on an item. The longer an item has been viewed, the more relevant.
         * View duration weight should be sent for view over 3s
         * zoe, burst, picture have the same behavior, only the time spent on fullscreen view is counted
         * video count the view duration, which is counted by the player activity.
         */
        public static final int WEIGHT_TYPE_VIEW_DURATION = 13;

        public static final int WEIGHT_TYPE_MEDIA_TYPE = 14;
        public static final int WEIGHT_TYPE_TIME_SAMPLING = 15;
        public static final int WEIGHT_TYPE_COLOR_SPACE = 16;
        public static final int WEIGHT_TYPE_FACE_COUNT = 17;
        public static final int WEIGHT_TYPE_SMILE_FACE = 18;
        public static final int WEIGHT_TYPE_SCENE_TYPE = 19;
        public static final int WEIGHT_TYPE_DIRECT_SELECTION = 20;
        public static final int WEIGHT_TYPE_SIMILARITY_COUNT = 21;//When similar picture, we pick the best of all, but still the rank get a boost for the additional effort from the user into shooting that instant.
        /**
         * an item has been shared to zoe, Counts the number of time it happened
         * from highlight view
         */
        public static final int WEIGHT_TYPE_ZOE_SHARED = 22;
        /**
         * an item that was automatically selected for zoe share, but the user deselect it as a manual explicit action.
         * Counts the number of time it happened
         * from highlight view
         */
        public static final int WEIGHT_TYPE_ZOE_UNSHARED = 23;

        /**
         * value should be the id of the new media, or the old media if overwrite
         * If the edition is not saved, weight don't have to be sent
         */
        public static final int WEIGHT_TYPE_MEDIA_EDITED = 24;

        /**
         * Used for video only, counts the number of play time
         */
        public static final int WEIGHT_TYPE_VIDEO_PLAY = 25;


        /**
         * /!\ not a weight. Contains the v_folder information for this item, needs to be set if v_folder is not defined in media store
         */
        public static final int WEIGHT_TYPE_EVENT_ID = 99;
//		public static final int WEIGHT_TYPE_SHARE_FB 			= 4;
//		public static final int WEIGHT_TYPE_PEOPLE 				= 5;
//		public static final int WEIGHT_TYPE_TITLE 				= 7;
//		public static final int WEIGHT_TYPE_FEEDBACK_P 			= 9;
//		public static final int WEIGHT_TYPE_FEEDBACK_N 			= 10;
//		public static final int WEIGHT_TYPE_MEDIA_COUNT 		= 11;
//		public static final int WEIGHT_TYPE_EVENT_ACCEPTED 		= 14;
//		public static final int WEIGHT_TYPE_EVENT_REJECTED 		= 15;
//		public static final int WEIGHT_TYPE_MEDIA_AVG 			= 16;
//		public static final int WEIGHT_TYPE_L2_CLUSTERIZED 		= 17;
//		public static final int WEIGHT_TYPE_DEFAULT 			= 18;
    }

    /**
     * Returns a log display name for a weight type
     *
     * @param i the weight_type to convert
     * @return
     */
    public static String getName(int i) {
        switch (i) {
//		case WeightTypes.WEIGHT_TYPE_CLUSTER_TYPE     :
//		  return "cluster_type";
//		case WeightTypes.WEIGHT_TYPE_TAG          :
//		  return "tag";
            case WeightTypes.WEIGHT_TYPE_SHARE_COUNT:
                return "share_count";
            case WeightTypes.WEIGHT_TYPE_ZOOM:
                return "zoom";
            case WeightTypes.WEIGHT_TYPE_FAVORITE:
                return "favorite";
            case WeightTypes.WEIGHT_TYPE_VIEW_COUNT:
                return "view_count";
            case WeightTypes.WEIGHT_TYPE_VIEW_DURATION:
                return "view_duration";
//		case WeightTypes.WEIGHT_TYPE_SHARE_FB       :
//		  return "share_fb";
//		case WeightTypes.WEIGHT_TYPE_PEOPLE       :
//		  return "people";
//		case WeightTypes.WEIGHT_TYPE_TITLE          :
//		  return "title";
//		case WeightTypes.WEIGHT_TYPE_FEEDBACK_P     :
//		  return "feedback_p";
//		case WeightTypes.WEIGHT_TYPE_FEEDBACK_N      :
//		  return "feedback_n";
//		case WeightTypes.WEIGHT_TYPE_MEDIA_COUNT       :
//		  return "media_count";
//		case WeightTypes.WEIGHT_TYPE_EVENT_ACCEPTED    :
//		  return "event_acc";
//		case WeightTypes.WEIGHT_TYPE_EVENT_REJECTED    :
//		  return "event_rej";
//		case WeightTypes.WEIGHT_TYPE_MEDIA_AVG       :
//		  return "media_avg";
//		case WeightTypes.WEIGHT_TYPE_L2_CLUSTERIZED  :
//		  return "l2_clusterized";
//		case WeightTypes.WEIGHT_TYPE_DEFAULT :
//		  return "default";
        }
        return "unknown_" + i;
    }
}
