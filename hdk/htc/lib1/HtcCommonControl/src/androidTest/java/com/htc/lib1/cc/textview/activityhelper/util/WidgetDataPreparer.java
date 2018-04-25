
package com.htc.lib1.cc.textview.activityhelper.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.test.R;

/**
 * @author felka
 *
 */
public class WidgetDataPreparer {

    public static void prepareAdapater(Context c,
            AutoCompleteTextView mAutoCompleteTextView) {
        if (null == mAutoCompleteTextView)
            return;

        CharSequence[] mData = {
                "AAAAAAAAAAAAAAAAA", "AABBBBBBBBBBBBBBB",
                "AACCCCCCCCCCCCCCC", "AADDDDDDDDDDDDDDD", "AAEEEEEEEEEEEEEEE",
                "AAFFFFFFFFFFFFFFF", "AAGGGGGGGGGGGGGGG", "AAHHHHHHHHHHHHHHH",
                "AAIIIIIIIIIIIIIII", "AAJJJJJJJJJJJJJJJ", "AAKKKKKKKKKKKKKKK",
                "AALLLLLLLLLLLLLLL", "AAMMMMMMMMMMMMMMM", "AANNNNNNNNNNNNNNN"
        };

        MyAdapter adapter = new MyAdapter(c, mData);

        mAutoCompleteTextView.setAdapter(adapter);
    }

}

class MyAdapter extends ArrayAdapter<CharSequence> {

    private LayoutInflater myInflater;
    CharSequence[] list = null;

    public MyAdapter(Context ctxt, CharSequence[] list) {
        super(ctxt, 0, list);
        myInflater = LayoutInflater.from(ctxt);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewTag viewTag;

        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.htcautocompletetextview_item_layout, null);

            viewTag = new ViewTag(
                    (HtcListItem1LineCenteredText) convertView.findViewById(
                            R.id.au_text1)
                    );

            convertView.setTag(viewTag);
            viewTag.text.setTextStyle(com.htc.lib1.cc.R.style.list_primary_m);
        }
        else {
            viewTag = (ViewTag) convertView.getTag();
        }

        viewTag.text.setText(list[position]);

        return convertView;
    }

    class ViewTag {
        HtcListItem1LineCenteredText text;

        public ViewTag(HtcListItem1LineCenteredText text) {
            this.text = text;
        }
    }
}
