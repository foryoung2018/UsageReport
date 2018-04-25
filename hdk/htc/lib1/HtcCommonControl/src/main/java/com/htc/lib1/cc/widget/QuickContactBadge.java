/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.widget;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
// <HTC U55>
import android.graphics.Rect;
// </HTC>
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.QuickContact;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
// <HTC U55>
// </HTC>
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
// <HTC U55>
import android.widget.ImageView;
// </HTC>
// <HTC U55>
import com.htc.lib1.cc.R;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Matrix;
import android.util.Log;
// </HTC>

//animator
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

//end animator

/**
 * Widget used to show an image with the standard QuickContact badge and
 * on-click behavior.
 */
public class QuickContactBadge extends ImageView implements OnClickListener {
    // Animator
    private Bitmap mScreenBitmap = null;
    private Bitmap mCanvasBitmap = null;
    private Paint mScreenPaint = null;
    private ColorMatrix mColorMatrix = new ColorMatrix();
    private float[] mColorArray;
    private boolean mIsAnimating = false;
    private Bundle mExtras = null;

    // end Animator
    private Uri mContactUri;
    private String mContactEmail;
    private String mContactPhone;
    private int mMode;
    private QueryHandler mQueryHandler;

    // For focus handling
    private boolean mGainFocus = false;
    private int mOverlayColor = -1;
    private Drawable mFocusDrawable = null;
    private Drawable mSelectorDrawable = null;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected String[] mExcludeMimes = null;

    static final private int TOKEN_EMAIL_LOOKUP = 0;
    static final private int TOKEN_PHONE_LOOKUP = 1;
    static final private int TOKEN_EMAIL_LOOKUP_AND_TRIGGER = 2;
    static final private int TOKEN_PHONE_LOOKUP_AND_TRIGGER = 3;
    static final private int TOKEN_CONTACT_LOOKUP_AND_TRIGGER = 4;

    static final private String EXTRA_URI_CONTENT = "uri_content";

    static final String[] EMAIL_LOOKUP_PROJECTION = new String[] {
            RawContacts.CONTACT_ID, Contacts.LOOKUP_KEY, };
    static final int EMAIL_ID_COLUMN_INDEX = 0;
    static final int EMAIL_LOOKUP_STRING_COLUMN_INDEX = 1;

    static final String[] PHONE_LOOKUP_PROJECTION = new String[] {
            PhoneLookup._ID, PhoneLookup.LOOKUP_KEY, };
    static final int PHONE_ID_COLUMN_INDEX = 0;
    static final int PHONE_LOOKUP_STRING_COLUMN_INDEX = 1;

    static final String[] CONTACT_LOOKUP_PROJECTION = new String[] {
            Contacts._ID, Contacts.LOOKUP_KEY, };
    static final int CONTACT_ID_COLUMN_INDEX = 0;
    static final int CONTACT_LOOKUPKEY_COLUMN_INDEX = 1;

    private boolean isAutoMotiveMode = false;
    private Drawable maskDrawableBadgeLight = null;

    private Drawable secondaryDrawable = null;

    /**
     * Simple constructor to use when creating a QuickContactBadge from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public QuickContactBadge(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the QuickContactBadge.
     */
    public QuickContactBadge(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.QuickContactBadgeStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the QuickContactBadge.
     * @param defStyle The default style to apply to this QuickContactBadge. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     */
    public QuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.style.QuickContactBadgeStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.QuickContactBadge,
                R.attr.quickContactBadgeStyle, R.style.QuickContactBadgeStyle);
        maskDrawableBadgeLight = (Drawable) ta.getDrawable(R.styleable.QuickContactBadge_android_drawable);

        ta.recycle();

        mMode = QuickContact.MODE_MEDIUM;


        mOverlayColor = context.getResources().getColor(R.color.overlay_color);
        mFocusDrawable = context.getResources().getDrawable(R.drawable.common_focused);
        if (mOverlayColor != -1 && mFocusDrawable != null) {
            mFocusDrawable.setColorFilter(mOverlayColor, PorterDuff.Mode.SRC_ATOP);
        }

        mSelectorDrawable = context.getResources().getDrawable(R.drawable.list_selector_light);

        init();
    }


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // mScreenBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (w > 0 && h > 0 && (w != oldw || h != oldh)) {
            if (mScreenBitmap != null)
                mScreenBitmap.recycle();
            if (mCanvasBitmap != null)
                mCanvasBitmap.recycle();
            mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            // mScreenBitmap = Bitmap.createBitmap(w, h,
            // Bitmap.Config.ARGB_8888);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    void setScreen(float scale) {
        mColorMatrix.reset();
        mColorArray = mColorMatrix.getArray();
        mColorArray[0] = scale;
        mColorArray[6] = scale;
        mColorArray[12] = scale;
        mColorArray[18] = scale;
        mColorMatrix.set(mColorArray);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(mColorMatrix);
        mScreenPaint.setColorFilter(cf);

        invalidate();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public void draw(Canvas canvas) {
        if (isAutoMotiveMode) {
            super.draw(canvas);
            return;
        }
        if (null != mCanvasBitmap && mIsAnimating) {
            Canvas c = new Canvas(mCanvasBitmap);
            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            super.draw(c);
            mScreenBitmap = Bitmap.createBitmap(mCanvasBitmap);
            c.drawBitmap(mScreenBitmap, 0, 0, mScreenPaint);
            canvas.drawBitmap(mCanvasBitmap, 0, 0, null);
            c = null;
        } else {
            super.draw(canvas);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        super.onMeasure(wMeasureSpec, hMeasureSpec);
    }

    private void init() {
        mQueryHandler = new QueryHandler(getContext().getContentResolver());
        setOnClickListener(this);

        mScreenPaint = new Paint();
        mScreenPaint
                .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
    }

    /**
     * Set the QuickContactBadge to be default onClickListener.
     *
     * @param value true to set, false otherwise.
     */
    public void setDefaultOnClickListener(boolean value) {
        if (value == true) {
            setOnClickListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mGainFocus = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mSelectorDrawable != null && mSelectorDrawable.isStateful()) {
            mSelectorDrawable.setState(getDrawableState());
            invalidate();
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable maskDrawable = null;
        if (isAutoMotiveMode) {
            super.onDraw(canvas);
            // For focus handling
            if (mGainFocus == true && mFocusDrawable != null) {
                mFocusDrawable.setBounds(canvas.getClipBounds());
                mFocusDrawable.draw(canvas);
            }
            return;
        }
        if (hasContactInfo()) {
            maskDrawable = maskDrawableBadgeLight;
        } else {
            maskDrawable = null;
        }

        if (null != maskDrawable) {
            super.onDraw(canvas);

            // secondary drawable like facebook
            if (secondaryDrawable != null) {
                secondaryDrawable.setBounds(
                    0,
                    getHeight() - secondaryDrawable.getIntrinsicHeight(),
                    secondaryDrawable.getIntrinsicWidth(),
                    getHeight());

                secondaryDrawable.draw(canvas);
            }

            maskDrawable.setBounds(0, 0, getWidth(), getHeight());
            maskDrawable.draw(canvas);
        } else {
            super.onDraw(canvas);

            // secondary drawable like facebook
            if (secondaryDrawable != null) {
                secondaryDrawable.setBounds(
                    getPaddingLeft(),
                    getHeight() - secondaryDrawable.getIntrinsicHeight() - getPaddingBottom(),
                    getPaddingLeft() + secondaryDrawable.getIntrinsicWidth(),
                    getHeight() - getPaddingBottom());

                secondaryDrawable.draw(canvas);
            }
        }

        // For focus handling
        if (mGainFocus == true && mFocusDrawable != null) {
            mFocusDrawable.setBounds(canvas.getClipBounds());
            mFocusDrawable.draw(canvas);
        }

        if (mSelectorDrawable != null)
        {
            mSelectorDrawable.setBounds(0, 0, getWidth(), getHeight());
            mSelectorDrawable.draw(canvas);
        }
    }

    /**
     * Set the QuickContact window mode. Options are
     * {@link QuickContact#MODE_SMALL}, {@link QuickContact#MODE_MEDIUM},
     * {@link QuickContact#MODE_LARGE}.
     *
     * @param size The mode of QuickContactBadge.
     */
    public void setMode(int size) {
        mMode = size;
    }

    /**
     * Assign the contact uri that this QuickContactBadge should be associated
     * with. Note that this is only used for displaying the QuickContact window
     * and won't bind the contact's photo for you.
     *
     * @param contactUri
     *            Either a {@link Contacts#CONTENT_URI} or
     *            {@link Contacts#CONTENT_LOOKUP_URI} style URI.
     */
    public void assignContactUri(Uri contactUri) {
        mContactUri = contactUri;
        mContactEmail = null;
        mContactPhone = null;
    }

    /**
     * Sets the currently selected tab of the Contacts application. If not set,
     * this is -1 and therefore does not save a tab selection when a phone call
     * is being made
     *
     * @hide
     */
    public void setSelectedContactsAppTabIndex(int value) {

    }

    private boolean isDarkMode = false;
    /**
     * Set the QuickContactBadge dark mode.
     *
     */
    public void setDarkMode() {
        if (!isDarkMode) {
            mSelectorDrawable = getContext().getResources().getDrawable(R.drawable.list_selector_dark);
            isDarkMode = true;
        }
    }

    /**
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public void setAutoMotiveMode(boolean ifEnable) {
        isAutoMotiveMode = ifEnable;
    }

    /**
     * Assign a contact based on an email address. This should only be used when
     * the contact's URI is not available, as an extra query will have to be
     * performed to lookup the URI based on the email.
     *
     * @param emailAddress The email address of the contact.
     * @param lazyLookup If this is true, the lookup query will not be performed
     * until this view is clicked.
     */
    public void assignContactFromEmail(String emailAddress, boolean lazyLookup) {
        assignContactFromEmail(emailAddress, lazyLookup, null);
    }

    /**
     * Assign a contact based on an email address. This should only be used when
     * the contact's URI is not available, as an extra query will have to be
     * performed to lookup the URI based on the email.

     @param emailAddress The email address of the contact.
     @param lazyLookup If this is true, the lookup query will not be performed
     until this view is clicked.
     @param extras A bundle of extras to populate the contact edit page with if the contact
     is not found and the user chooses to add the email address to an existing contact or
     create a new contact. Uses the same string constants as those found in
     {@link android.provider.ContactsContract.Intents.Insert}
    */

    public void assignContactFromEmail(String emailAddress, boolean lazyLookup, Bundle extras) {
        mContactEmail = emailAddress;
        mExtras = extras;
        if (!lazyLookup && mQueryHandler != null) {
            mQueryHandler.startQuery(TOKEN_EMAIL_LOOKUP, null,
                    Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode(mContactEmail)),
                    EMAIL_LOOKUP_PROJECTION, null, null, null);
        } else {
            mContactUri = null;
            onContactUriChanged();
        }
    }

    /**
     * Assign a contact based on a phone number. This should only be used when
     * the contact's URI is not available, as an extra query will have to be
     * performed to lookup the URI based on the phone number.
     *
     * @param phoneNumber The phone number of the contact.
     * @param lazyLookup If this is true, the lookup query will not be performed
     * until this view is clicked.
     */
    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup) {
        assignContactFromPhone(phoneNumber, lazyLookup, new Bundle());
    }

    /**
     * Assign a contact based on a phone number. This should only be used when
     * the contact's URI is not available, as an extra query will have to be
     * performed to lookup the URI based on the phone number.
     *
     * @param phoneNumber The phone number of the contact.
     * @param lazyLookup If this is true, the lookup query will not be performed
     * until this view is clicked.
     * @param extras A bundle of extras to populate the contact edit page with if the contact
     * is not found and the user chooses to add the phone number to an existing contact or
     * create a new contact. Uses the same string constants as those found in
     * {@link android.provider.ContactsContract.Intents.Insert}
     */
    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup, Bundle extras) {
        mContactPhone = phoneNumber;
        mExtras = extras;
        if (!lazyLookup && mQueryHandler != null) {
            mQueryHandler.startQuery(TOKEN_PHONE_LOOKUP, null,
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, mContactPhone),
                    PHONE_LOOKUP_PROJECTION, null, null, null);
        } else {
            mContactUri = null;
            onContactUriChanged();
        }
    }

    private void onContactUriChanged() {
        setEnabled(isAssigned());
    }
    /** True if a contact, an email address or a phone number has been assigned */
    private boolean isAssigned() {
        return mContactUri != null || mContactEmail != null || mContactPhone != null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // If contact has been assigned, mExtras should no longer be null, but do a null check
        // anyway just in case assignContactFromPhone or Email was called with a null bundle or
        // wasn't assigned previously.
        final Bundle extras = (mExtras == null) ? new Bundle() : mExtras;
        if (mContactUri != null) {
            QuickContact.showQuickContact(getContext(), QuickContactBadge.this, mContactUri,
                    QuickContact.MODE_LARGE, mExcludeMimes);
        } else if (mContactEmail != null && mQueryHandler != null) {
            extras.putString(EXTRA_URI_CONTENT, mContactEmail);
            mQueryHandler.startQuery(TOKEN_EMAIL_LOOKUP_AND_TRIGGER, extras,
                    Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode(mContactEmail)),
                    EMAIL_LOOKUP_PROJECTION, null, null, null);
        } else if (mContactPhone != null && mQueryHandler != null) {
            extras.putString(EXTRA_URI_CONTENT, mContactPhone);
            mQueryHandler.startQuery(TOKEN_PHONE_LOOKUP_AND_TRIGGER, extras,
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, mContactPhone),
                    PHONE_LOOKUP_PROJECTION, null, null, null);
        } else {
            // If a contact hasn't been assigned, don't react to click.
            return;
        }
    }

    /**
     * Set a list of specific MIME-types to exclude and not display. For
     * example, this can be used to hide the {@link Contacts#CONTENT_ITEM_TYPE}
     * profile icon.
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setExcludeMimes(String[] excludeMimes) {
        mExcludeMimes = excludeMimes;
    }

    private class QueryHandler extends AsyncQueryHandler {

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Uri lookupUri = null;
            Uri createUri = null;
            boolean trigger = false;
            Bundle extras = (cookie != null) ? (Bundle) cookie : new Bundle();
            try {
                switch(token) {
                    case TOKEN_PHONE_LOOKUP_AND_TRIGGER:
                        trigger = true;
                        createUri = Uri.fromParts("tel", extras.getString(EXTRA_URI_CONTENT), null);

                        //$FALL-THROUGH$
                    case TOKEN_PHONE_LOOKUP: {
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId = cursor.getLong(PHONE_ID_COLUMN_INDEX);
                            String lookupKey = cursor.getString(PHONE_LOOKUP_STRING_COLUMN_INDEX);
                            lookupUri = Contacts.getLookupUri(contactId, lookupKey);
                        }

                        break;
                    }
                    case TOKEN_EMAIL_LOOKUP_AND_TRIGGER:
                        trigger = true;
                        createUri = Uri.fromParts("mailto",
                                extras.getString(EXTRA_URI_CONTENT), null);

                        //$FALL-THROUGH$
                    case TOKEN_EMAIL_LOOKUP: {
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId = cursor.getLong(EMAIL_ID_COLUMN_INDEX);
                            String lookupKey = cursor.getString(EMAIL_LOOKUP_STRING_COLUMN_INDEX);
                            lookupUri = Contacts.getLookupUri(contactId, lookupKey);
                        }
                        break;
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            mContactUri = lookupUri;
            onContactUriChanged();

            if (trigger && lookupUri != null) {
                // Found contact, so trigger QuickContact
                QuickContact.showQuickContact(getContext(), QuickContactBadge.this, lookupUri,
                        QuickContact.MODE_LARGE, mExcludeMimes);
            } else if (createUri != null) {
                // Prompt user to add this person to contacts
                final Intent intent = new Intent(Intents.SHOW_OR_CREATE_CONTACT, createUri);
                if (extras != null) {
                    extras.remove(EXTRA_URI_CONTENT);
                    intent.putExtras(extras);
                }
                getContext().startActivity(intent);
            }
        }
    }

    // <HTC>

    private boolean hasContactInfo() {
        if (mContactUri == null && mContactEmail == null
                && mContactPhone == null) {
            return false;
        }
        return true;
    }

    private QueryCallback mCallback = null;

    /**
     * Set the callback for the end of query.
     *
     * @param callback The callback for the end of query.
     */
    public void setCallback(QueryCallback callback) {
        mCallback = callback;
    }

    /**
     * Interface that should be implemented by QuickContact Activity to handle
     * query nothing case.
     */
    public static interface QueryCallback {
        /** Callback when nothing for query. */
        public void onQueryNothing();
    }

    /**
     * Sets a drawable as the content of this secondary image.
     *
     * @param resId the resource identifier of the the drawable
     */
    public void setSecondaryImageResource(int resId) {
        if (resId != 0) {
            try {
                secondaryDrawable = getContext().getResources().getDrawable(resId);
            } catch (Exception e) {
                Log.w("QuickContactBadge", "Unable to find resource: "
                        + secondaryDrawable, e);
            }
            this.invalidate();
        } else {
            return;
        }
    }

    /**
     * Sets a drawable as the content of this secondary image.
     *
     * @param drawable The drawable to set
     */
    public void setSecondaryImageDrawable(Drawable drawable) {
        secondaryDrawable = drawable;
        this.invalidate();
    }

    /**
     * Sets a Bitmap as the content of this secondary image.
     *
     * @param bm The bitmap to set
     */
    public void setSecondaryImageBitmap(Bitmap bm) {
        setSecondaryImageDrawable(new BitmapDrawable(getContext().getResources(), bm));
    }

    /**
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    protected void setQuickContactInActionBar(boolean isInActionBar) {
    }

    /**
     * If you don't know what "Icon" means here, please don't use this method!
     *
     * <p>If you use QuickContactBadge with recycling mechanism for item view
     * (e.g., in a ListView), please make sure each QuickContactBadge should
     * call this method. (true for the icon, false for others) Otherwise, you
     * could obtain the wrong ScaleType from recycled convertView.
     *
     * <p>Since icon in QuickContactBadge should be scaled and translated
     * with font size change, you can call this method if the image resource
     * was set to an icon. This method will help to set the corresponding
     * ImageMatrix and ScaleType.
     *
     * @param isAnIconForImageRes true if you set an icon as image resource,
     * false will reset the original ImageMatrix and ScaleType.
     *
     * @deprecated [Not use any longer] useless in sense5.0
     */
    /**@hide*/
    public void setIconForImageRes(boolean isAnIconForImageRes) {
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Not use any longer] useless in sense5.0
     */
    /**@hide*/
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Not use any longer] useless in sense5.0
     */
    /**@hide*/
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Not use any longer] useless in sense5.0
     */
    /**@hide*/
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }
}
