
package com.htc.lib1.cc.popupWindow.activityhelper.popupmenu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.ListPopupWindow;

import java.util.ArrayList;

public class ListPopupWindowDemo extends ActivityBase {
    private Context mContext;
    private ArrayAdapter<?> arrayAdapter;
    private ArrayAdapter<?> arrayAdapterFont;
    private ActionBarItemView actionBarItemView;
    private ActionBarItemView actionBarItemViewFont;
    ListPopupWindow mListPopWindow = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        initActionBar();
        initSkinPopup();

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(new ColorDrawable(0xffff0000));

    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    private void initActionBar() {
        ActionBarExt actionBarExt = new ActionBarExt(this, getActionBar());

        ActionBarText actionBarText = new ActionBarText(this);
        actionBarText.setPrimaryText(getApplicationContext().getResources().getString(mContext.getApplicationInfo().labelRes));

        ActionBarContainer actionBarContainer = actionBarExt.getCustomContainer();
        actionBarContainer.addCenterView(actionBarText);

        actionBarItemView = new ActionBarItemView(this);
        actionBarItemView.setIcon(R.drawable.skin);

        initialAdapter();

        actionBarItemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view != null && view instanceof ActionBarItemView) {
                    final ListPopupWindow lpw = new ListPopupWindow(ListPopupWindowDemo.this, android.R.attr.popupMenuStyle);
                    lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            lpw.dismiss();
                        }
                    });
                    lpw.setAnchorView(view);
                    lpw.setAdapter(arrayAdapter);
                    lpw.show();
                    mListPopWindow = lpw;
                }
            }
        });

        actionBarContainer.addRightView(actionBarItemView);

        actionBarItemViewFont = new ActionBarItemView(this);
        actionBarItemViewFont.setIcon(R.drawable.font);

        actionBarItemViewFont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view != null && view instanceof ActionBarItemView) {
                    final ListPopupWindow lpw = new ListPopupWindow(ListPopupWindowDemo.this, -1);
                    lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            lpw.dismiss();
                        }
                    });
                    lpw.setAnchorView(view);
                    lpw.setAdapter(arrayAdapterFont);

                    lpw.show();
                    mListPopWindow = lpw;
                }
            }
        });

        actionBarContainer.addRightView(actionBarItemViewFont);
    }

    public void dismissWindow() {
        if (null != mListPopWindow) {
            mListPopWindow.dismiss();
        }
    }
    private void initSkinPopup() {
        initialAdapter();
    }

    private void initialAdapter() {
        ArrayList<String> installedSkinPackageNameAry = new ArrayList<String>();//HtcSkinUtil.getSkinPackageName(mContext);
        installedSkinPackageNameAry.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault));
        installedSkinPackageNameAry.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryOne));
        installedSkinPackageNameAry.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryTwo));
        installedSkinPackageNameAry.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryThree));
        installedSkinPackageNameAry.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault_CategoryFour));
        for(int i=1; i < 31; i++){
            installedSkinPackageNameAry.add(i+"");
        }
        arrayAdapter = new ArrayAdapter<String>(ListPopupWindowDemo.this, android.R.layout.simple_dropdown_item_1line, installedSkinPackageNameAry);

        ArrayList<String> nArray = new ArrayList<String>();
        nArray.add(getResources().getResourceEntryName(R.style.HtcDeviceDefault));
        arrayAdapterFont = new ArrayAdapter<String>(ListPopupWindowDemo.this, android.R.layout.simple_dropdown_item_1line, nArray);
    }

    public ActionBarItemView getActionBarItemView() {
        return actionBarItemView;
    }

    public ActionBarItemView getActionBarItemViewFont() {
        return actionBarItemViewFont;
    }
}
