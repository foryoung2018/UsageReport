package com.htc.lib1.cc.widget;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcGridView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @hide
 * @deprecated try level not release
 */
public class ColorPickerDialog extends HtcAlertDialog {

    protected ColorPickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShadowLinearLayout) getWindow().findViewById(R.id.parentPanel)).setLayoutArg(ShadowLinearLayout.KEEP_PORTRAIT_WIDTH);
    }

    public static class Builder extends HtcAlertDialog.Builder {

        private final static String LOG_TAG = "ColorPickerDialog";
        private final static int NO_NEED_PADDING_IN_ASSET = 0;
        private ColorSelectedListener mColorSelectedListener;
        private int mNumColumns = 3;
        private int[] mArrayColors = {
                0x00000000, 0x00000000, 0x00000000
        };
        private int mGap = 16;

        public Builder(Context context) {
            super(context);
            mGap = getContext().getResources().getDimensionPixelOffset(R.dimen.leading);
        }

        /**
         * Set the number of columns in the HtcGridView
         *
         * @param numColumns The desired number of columns.
         */
        public Builder setNumColumns(int numColumns) {
            if (!ColorPickerAdapter.checkNumColumnsIllegal(numColumns)) {
                mNumColumns = numColumns;
            }
            return this;
        }

        /**
         * set the arrayColors for ColorPickerAdapter
         *
         * @param arrayColors The desired arraycolors of ColorPickerAdapter.
         * @return
         */
        public Builder setColorArray(int[] arrayColors) {
            if (!ColorPickerAdapter.checkArrayColorsIllegal(arrayColors)) {
                mArrayColors = arrayColors;
            }
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is click
         *
         * @param ColorSelectedListener see android.app.AlertDialog.Builder
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnColorClickListener(final ColorSelectedListener listener) {
            if (null == listener) {
                Log.e(LOG_TAG, "Current listener is null ", new Exception());
                return this;
            }
            mColorSelectedListener = listener;
            return this;
        }

        /**
         * Creates a {@link ColorPickerDialog} with the arguments supplied to this builder. It does
         * not {@link Dialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         *
         * @return see android.app.ColorPickerDialog.Builder
         */
        @Override
        public ColorPickerDialog create() {
            final Context context = getContext();
            final ColorPickerDialog dialog = new ColorPickerDialog(context);
            P.apply(dialog.mAlert);
            dialog.setView(getInitGridView());
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * Creates a {@link ColorPickerDialog} with the arguments supplied to this builder and
         * {@link Dialog#show()}'s the dialog.
         *
         * @return see android.app.ColorPickerDialog.Builder
         */
        @Override
        public ColorPickerDialog show() {
            ColorPickerDialog dialog = create();
            dialog.show();
            return dialog;
        }

        private View getInitGridView() {
            final HtcGridView gridView = new HtcGridView(P.mContext);
            gridView.setId(android.R.id.list);
            gridView.setPadding(mGap, mGap, mGap, NO_NEED_PADDING_IN_ASSET);
            final ColorPickerAdapter adapter = new ColorPickerAdapter(P.mContext,
                    mArrayColors, mNumColumns);
            gridView.setAdapter(adapter);
            gridView.setMode(HtcGridView.MODE_GENERIC);
            gridView.setNumColumns(mNumColumns);
            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (null != mColorSelectedListener) {
                        mColorSelectedListener.setSelectedColor((Integer) adapter.getItem(position));
                    }
                    adapter.setSelectedItemPosition(position);
                    adapter.notifyDataSetChanged();
                }
            });
            return gridView;
        }

    }

    /**
     * Interface used to allow the creator of a dialog to run some code when an item on the dialog
     * is selected.
     */
    public interface ColorSelectedListener {
        /**
         * This method will be invoked when a item in the colorpickerdialog is selected.
         *
         * @param color The dialog that received the color.
         */
        public void setSelectedColor(int color);
    }

}
