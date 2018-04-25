
package com.htc.lib1.cs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom view for clickable text view with link.
 * 
 * @author autosun_li@htc.com
 */
@SuppressLint("AppCompatCustomView")
public class LinkableTextView extends TextView {
    public LinkableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Data structure for link text.
     */
    public static class LinkText {
        private String mLinkString;
        private ClickableSpan mCallback;

        public LinkText(String linkString, ClickableSpan callback) {
            mLinkString = linkString;
            mCallback = callback;
        }

        public String getLinkString() {
            return mLinkString;
        }

        public ClickableSpan getClickableSpan() {
            return mCallback;
        }

        public String toString() {
            return getLinkString();
        }
    }

    /**
     * Set the text of view.
     * 
     * @param baseString Base string with %s.
     * @param linkTexts The string to be formated and it link targets.
     */
    public void setLinkableText(String baseString, LinkText... linkTexts) {
        if (!TextUtils.isEmpty(baseString) && linkTexts != null) {
            // Formating string.
            String text = String.format(baseString, (Object[]) linkTexts);
            SpannableString spanStr = new SpannableString(text);

            int start = -1;
            for (LinkText linkText : linkTexts) {
                if (linkText.getClickableSpan() != null) {
                    // Find link string start/end positions.
                    start = text.indexOf(linkText.getLinkString(), start + 1);
                    int end = start + linkText.getLinkString().length();
                    // Set ClickableSpan.
                    spanStr.setSpan(linkText.getClickableSpan(), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            // Set text.
            setText(spanStr);
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * Set the text of view.
     * 
     * @param baseResId Resource id of base string with %s.
     * @param linkTexts The string to be formated and it link targets.
     */
    public void setLinkableText(int baseResId, LinkText... linkTexts) {
        setLinkableText(this.getContext().getString(baseResId), linkTexts);
    }
}
