/**
 *
 */

package com.htc.sense.commoncontrol.demo.util;

import com.htc.sense.commoncontrol.demo.Htc1LineListAdapter;

import android.content.Context;
import android.widget.AutoCompleteTextView;

/**
 * @author felka
 */
public class WidgetDataPreparer {

    public static void prepareAdapater(Context c, AutoCompleteTextView mAutoCompleteTextView) {
        if (null == mAutoCompleteTextView) return;

        CharSequence[] mData = {
                "AAAAAAAAAAAAAAAAA", "AABBBBBBBBBBBBBBB",
                "AACCCCCCCCCCCCCCC", "AADDDDDDDDDDDDDDD", "AAEEEEEEEEEEEEEEE",
                "AAFFFFFFFFFFFFFFF", "AAGGGGGGGGGGGGGGG", "AAHHHHHHHHHHHHHHH",
                "AAIIIIIIIIIIIIIII", "AAJJJJJJJJJJJJJJJ", "AAKKKKKKKKKKKKKKK",
                "AALLLLLLLLLLLLLLL", "AAMMMMMMMMMMMMMMM", "AANNNNNNNNNNNNNNN"
        };

        Htc1LineListAdapter adapter = new Htc1LineListAdapter(c, mData);
        mAutoCompleteTextView.setAdapter(adapter);
    }

}
