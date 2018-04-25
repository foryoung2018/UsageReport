package com.htc.sense.commoncontrol.demo.listview;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.sense.commoncontrol.demo.R;


public class HtcListViewReorderAdapter extends BaseAdapter implements
        SectionIndexer {

    private LayoutInflater mInflater;
    private String[] mList;
    private String[] mAlphabet; // A String array of alphabet used as section
    private SparseIntArray mAlphaMap; // Store mapping of the section and it's
                                        // start position
    private java.text.Collator mCollator; //
    private int m_layoutId = R.layout.htclistview_demo_htc_reorderlistview_item_layout;
    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_COUNT = 2;

    /**
     * Constructor with the array of string.
     *
     * @param layoutId
     *            : the resource ID of the layout Default
     *            value:R.layout.htc_listview_item_layout.And you can choose
     *            another one named
     *            R.layout.htc_listview_item_multiplechoice_layout.
     * @param data
     *            : the source to generate the list.
     */
    public HtcListViewReorderAdapter(Context context, int layoutId, String[] data) {

        mInflater = (LayoutInflater) (context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        m_layoutId = layoutId;

        String alphabetString = context.getString(R.string.alphabet);
        mAlphabet = new String[alphabetString.length()];
        for (int i = 0; i < mAlphabet.length; i++) {
            mAlphabet[i] = String.valueOf(alphabetString.charAt(i));
        }
        mAlphaMap = new SparseIntArray(26 /* Optimize for English */);
        mCollator = java.text.Collator.getInstance();
        mCollator.setStrength(java.text.Collator.PRIMARY);
        mList = data;
    }

    /**
     * Grouping by first character of the string.
     *
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     *
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == TYPE_SEPARATOR) {
                v = mInflater.inflate(
                        R.layout.htclistview_htc_list_item_separator, parent,
                        false);
            } else {
                v = mInflater.inflate(m_layoutId, parent, false);
            }
        } else {
            v = convertView;
        }

        HtcListItem2LineText vlable;

        if (viewType == TYPE_SEPARATOR) {
            vlable = (HtcListItem2LineText) v
                    .findViewById(R.id.txt_1x1);
        } else {
            vlable = (HtcListItem2LineText) v
                    .findViewById(R.id.label);
        }

        String str = mList[position];

        if (str != null) {
            vlable.setPrimaryText(str);
            vlable.setSecondaryTextVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList[position].length() == 1)
            return TYPE_SEPARATOR;
        else
            return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == TYPE_SEPARATOR)
            return false;
        else
            return true;

    }

    public final long getItemId(int position) {
        return position;
    }

    public final Object getItem(int position) {
        return mList[position];
    }

    public final int getCount() {
        return mList.length;
    }

    public Object[] getSections() {
        // A String array of alphabet used as section
        return mAlphabet;
    }

    public int getSectionForPosition(int position) {
        return 0;
    }

    public int getPositionForSection(int section) {
        return indexOf(section);
    }

    /**
     * Performs a binary search or cache lookup to find the first row that
     * matches a given section's starting letter.
     *
     * @param sectionIndex
     *            the section to search for
     * @return the row index of the first occurrence, or the nearest next
     *         letter. For instance, if searching for "T" and no "T" is found,
     *         then the first row starting with "U" or any higher letter is
     *         returned. If there is no data following "T" at all, then the list
     *         size is returned.
     */
    public int indexOf(int sectionIndex) {
        if (mAlphabet == null) {
            return 0;
        }
        // Check bounds
        if (sectionIndex <= 0) {
            return 0;
        }
        // Check bounds
        if (sectionIndex >= mAlphabet.length) {
            sectionIndex = mAlphabet.length - 1;
        }
        // prepare for Binary search
        int count = getCount();
        int start = 0;
        int end = count;
        int pos;

        String letter = mAlphabet[sectionIndex];
        letter = letter.toUpperCase();
        // The section character we want to find it's start position
        int key = letter.charAt(0);

        // Get the start position of this key, if not set yet return
        // Integer.MIN_VALUE
        pos = mAlphaMap.get(key, Integer.MIN_VALUE);
        // we have set start position of this key it maybe approximate or not
        if (Integer.MIN_VALUE != (pos = mAlphaMap.get(key, Integer.MIN_VALUE))) {
            // Is it approximate? Using negative value to indicate that it's
            // an approximation and positive value when it is the accurate
            // position.
            if (pos < 0) {
                pos = -pos;
                end = pos;
            } else {
                // Not approximate, this is the confirmed start of section,
                // return it
                return pos;
            }
        }

        // Do we have the position of the previous section?
        // If we have. Set it as the start point of binary search
        if (sectionIndex > 0) {
            int prevLetter = mAlphabet[sectionIndex - 1].toString().charAt(0);
            int prevLetterPos = mAlphaMap.get(prevLetter, Integer.MIN_VALUE);
            if (prevLetterPos != Integer.MIN_VALUE) {
                start = Math.abs(prevLetterPos);
            }
        }

        // Now that we have a possibly optimized start and end, let's binary
        // search
        pos = (end + start) / 2;
        while (pos < end) {
            // Get DataItem at pos
            String curData = getItem(pos).toString();
            if (curData == null) {
                // No data
                if (pos == 0) {
                    break;
                } else {
                    pos--;
                    continue;
                }
            }
            // Get the first char of curData
            int curLetter = Character.toUpperCase(curData.charAt(0));

            if (curLetter != key) {
                int curPos = mAlphaMap.get(curLetter, Integer.MIN_VALUE);
                // Enter approximation in hash if a better solution doesn't
                // exist
                if (curPos == Integer.MIN_VALUE || Math.abs(curPos) > pos) {
                    // Negative pos indicates that it is an approximation
                    mAlphaMap.put(curLetter, -pos);
                }
                // curData is less than key letter search forward
                if (mCollator.compare(curData, letter) < 0) {
                    start = pos + 1;
                    if (start >= count) {
                        pos = count;
                        break;
                    }
                } else {
                    end = pos; // search backward
                }
            } else {
                // curLetter and key are the same, but that doesn't mean it's
                // the start position
                if (start == pos) {
                    // This is it
                    break;
                } else {
                    // Need to go further lower to find the starting pos
                    end = pos;
                }
            }
            pos = (start + end) / 2;
        }// End of while. binary search complete
        mAlphaMap.put(key, pos);
        return pos;
    }

}