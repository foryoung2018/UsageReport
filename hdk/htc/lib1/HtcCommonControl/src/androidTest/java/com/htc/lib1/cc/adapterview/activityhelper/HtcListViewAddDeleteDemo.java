
package com.htc.lib1.cc.adapterview.activityhelper;

import java.util.ArrayList;
import com.htc.lib1.cc.test.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcListView.DeleteAnimationListener;

public class HtcListViewAddDeleteDemo extends ActivityBase {
    /**
     * Our data which will be displayed
     */
    public static final String[] ENTRIES = {

            "ABBAYE DE BELLOC", "ABBAYE DU MONT DES CATS", "ABERTAM", "ABONDANCE", "ACKAWI",
            "ACORN", "ADELOST", "AFFIDELICE AU CHABLIS", "AFUEGA'L PITU", "AIRAG", "AIREDALE",
            "AISY CENDRE", "ALLGAUER EMMENTALER", "ALVERCA", "AMBERT", "AMERICAN CHEESE",
            "AMI DU CHAMBERTIN", "ANEJO ENCHILADO", "ANNEAU DU VIC-BILH", "ANTHORIRO", "APPENZELL",
            "ARAGON", "ARDI GASNA", "ARDRAHAN", "ARMENIAN STRING", "AROMES AU GENE DE MARC",
            "ASADERO", "ASIAGO", "AUBISQUE PYRENEES", "AUTUN", "AVAXTSKYR", "BABY SWISS",
            "BABYBEL", "BAGUETTE LAONNAISE", "BAKERS", "BALADI", "BALATON", "BANDAL", "BANON",
            "BARRY'S BAY CHEDDAR", "BASING", "BASKET CHEESE", "BATH CHEESE", "BAVARIAN BERGKASE",
            "BAYLOUGH", "BEAUFORT", "BEAUVOORDE", "BEENLEIGH BLUE", "BEER CHEESE", "BEL PAESE",
            "BERGADER", "BERGERE BLEUE", "BERKSWELL", "BEYAZ PEYNIR", "BIERKASE", "BISHOP KENNEDY",
            "BLARNEY", "BLEU D'AUVERGNE", "BLEU DE GEX", "BLEU DE LAQUEUILLE",
            "BLEU DE SEPTMONCEL", "BLEU DES CAUSSES", "BLUE", "BLUE CASTELLO", "BLUE RATHGORE",
            "BLUE VEIN (AUSTRALIAN)", "BLUE VEIN CHEESES", "BOCCONCINI", "BOCCONCINI (AUSTRALIAN)",
            "BOEREN LEIDENKAAS", "BONCHESTER", "BOSWORTH", "BOUGON", "BOULE DU ROVES",
            "BOULETTE D'AVESNES", "BOURSAULT", "BOURSIN", "BOUYSSOU", "BRA", "BRAUDOSTUR",
            "BREAKFAST CHEESE", "BREBIS DU LAVORT", "BREBIS DU LOCHOIS", "BREBIS DU PUYFAUCON",
            "BRESSE BLEU", "BRICK", "BRIE", "BRIE DE MEAUX", "BRIE DE MELUN", "BRILLAT-SAVARIN",
            "BRIN", "BRIN D' AMOUR", "BRIN D'AMOUR", "BRINZA (BURDUF BRINZA)",
            "BRIQUETTE DE BREBIS", "BRIQUETTE DU FOREZ", "BROCCIO", "BROCCIO DEMI-AFFINE",
            "BROUSSE DU ROVE", "BRUDER BASIL", "BRUSSELAE KAAS (FROMAGE DE BRUXELLES)", "BRYNDZA",
            "BUCHETTE D'ANJOU", "BUFFALO", "BURGOS", "BUTTE", "BUTTERKASE", "BUTTON (INNES)",
            "BUXTON BLUE", "CABECOU", "CABOC", "CABRALES", "CACHAILLE", "CACIOCAVALLO", "CACIOTTA",
            "CAERPHILLY", "CAIRNSMORE", "CALENZANA", "CAMBAZOLA", "CAMEMBERT DE NORMANDIE",
            "CANADIAN CHEDDAR", "CANESTRATO", "CANTAL", "CAPRICE DES DIEUX", "CAPRICORN GOAT",
            "CAPRIOLE BANON", "CARRE DE L'EST", "CASCIOTTA DI URBINO", "CASHEL BLUE", "CASTELLANO",
            "CASTELLENO", "CASTELMAGNO", "CASTELO BRANCO", "CASTIGLIANO", "CATHELAIN",
            "CELTIC PROMISE", "CENDRE D'OLIVET", "CERNEY", "CHABICHOU", "CHABICHOU DU POITOU",
            "CHABIS DE GATINE", "CHAOURCE", "CHAROLAIS", "CHAUMES", "CHEDDAR"
    };

    /**
     * the adapter for HTC list view
     */
    private ListViewAdapter mAdapter = null;

    /**
     * The HTC list view
     */
    private HtcListView mHtcListView = null;

    private Button mAddItem = null;
    private Button mDelItem = null;
    private int mAddedPos = 0;
    private ActionBarExt actionBarExt = null;
    private ActionBarContainer actionBarContainer = null;
    private boolean mIsDelStartCalled = false;
    private boolean mIsDelEndCalled = false;
    private boolean mIsAnimationRunning = false;

    /**
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after
     *            previously being shut down then this Bundle contains the data
     *            it most recently supplied in onSaveInstanceState(Bundle).
     *            Notice: Otherwise it is null.
     * @see onStart() onSaveInstanceState(Bundle) onRestoreInstanceState(Bundle)
     *      onPostCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        setContentView(R.layout.htclistview_main4);
        mHtcListView = (HtcListView) findViewById(R.id.htc_list);
        if (mHtcListView != null) {
            setAdapter();
            mHtcListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    HtcCheckBox chb = holder.checkBox;
                    if (mAdapter.getCheckState(position) == true) {
                        mAdapter.setCheckState(position, false);
                        chb.setChecked(false);
                    } else {
                        mAdapter.setCheckState(position, true);
                        chb.setChecked(true);
                    }

                    chb.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                }
            });

            // the delete animation listener
            mHtcListView.setDeleteAnimationListener(new DeleteAnimationListener() {
                @Override
                public void onAnimationEnd() {
                    mIsAnimationRunning = false;
                    setDelEndCalled(true);
                }

                @Override
                public void onAnimationStart() {
                    setDelStartCalled(true);
                }

                @Override
                public void onAnimationUpdate() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            mHtcListView.setRecyclerListener(new RecyclerListener() {

                @Override
                public void onMovedToScrapHeap(View view) {

                }
            });
            mHtcListView.setVerticalScrollBarEnabled(false);
        }
        mAddItem = (Button) findViewById(R.id.addItems);

        mDelItem = (Button) findViewById(R.id.delItems);

        mDelItem.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                if (mIsAnimationRunning == false) {
                    mIsAnimationRunning = true;
                    ArrayList<Integer> delItemList = new ArrayList<Integer>();
                    int size = mAdapter.getCount();
                    for (int i = 0; i < size; i++) {
                        if (mAdapter.getCheckState(i) == true) {
                            delItemList.add(i);
                        }
                    }
                    size = delItemList.size();
                    if (delItemList.size() != 0) {
                        if (mHtcListView != null)
                            mHtcListView.disableTouchEventInAnim();
                        for (int i = size - 1; i >= 0; --i) {
                            mAdapter.removeItem(delItemList.get(i));
                        }
                        if (mHtcListView != null)
                            mHtcListView.setDelPositionsList(delItemList);
                    } else {
                        mIsAnimationRunning = false;
                    }
                }
            }
        });
    }

    public boolean isDelStartCalled() {
        return mIsDelStartCalled;
    }

    public boolean isDelEndCalled() {
        return mIsDelEndCalled;
    }

    public void setDelStartCalled(boolean isCalled) {
        mIsDelStartCalled = isCalled;
    }

    public void setDelEndCalled(boolean isCalled) {
        mIsDelEndCalled = isCalled;
    }

    public HtcListView getListView() {
        return mHtcListView;
    }

    /**
     * Set the adapter
     */
    private void setAdapter() {
        mAdapter = new ListViewAdapter(this, R.layout.htclistview_demo_htc_listview_item_layout,
                ENTRIES);
        mHtcListView.setAdapter(mAdapter);
    }

    class ViewHolder {
        HtcListItem2LineText text;
        HtcCheckBox checkBox;
    }

    public class ListViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private int m_layoutId = R.layout.htclistview_demo_htc_listview_item_layout;
        private ArrayList<ListItemData> mList = new ArrayList<ListItemData>();// Lists.newArrayList();
        Context mContext;

        /**
         * Constructor with the array of string.
         * 
         * @param layoutId: the resource ID of the layout Default
         *            value:R.layout.htc_listview_item_layout.And you can choose
         *            another one named
         *            R.layout.htc_listview_item_multiplechoice_layout.
         * @param data: the source to generate the list.
         */
        public ListViewAdapter(Context context, int layoutId, String[] data) {
            mContext = context;
            mInflater = (LayoutInflater) (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            m_layoutId = layoutId;
            setDataList(data);
        }

        public void setDataList(String[] data) {
            for (int i = 0; i < data.length; ++i) {
                mList.add(new ListItemData(data[i], false));
            }
        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder holder;
            if (convertView == null) {
                v = mInflater.inflate(m_layoutId, parent, false);
                holder = new ViewHolder();
                holder.checkBox = (HtcCheckBox) v.findViewById(R.id.chkbox);
                holder.text = (HtcListItem2LineText) v.findViewById(R.id.label);
                holder.text.setSecondaryTextVisibility(View.GONE);
                v.setTag(holder);
            } else {
                v = convertView;
                holder = (ViewHolder) v.getTag();
                holder.checkBox.setChecked(false);
            }

            if (getCheckState(position) == true) {
                holder.checkBox.setChecked(true);
            }

            String str = mList.get(position).Data;

            if (str != null) {
                holder.text.setPrimaryText(str);
            }

            return v;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public boolean getCheckState(int pos) {
            return mList.get(pos).IsChecked;
        }

        public void setCheckState(int pos, boolean isChecked) {
            mList.get(pos).IsChecked = isChecked;
        }

        public void removeItem(int pos) {
            mList.remove(pos);

        }

        public void addItem(int pos, String data) {
            if (pos < mList.size()) {
                mList.add(pos, new ListItemData(data, false));
            } else {
                mList.add(new ListItemData(data, false));
            }
        }

        public final long getItemId(int position) {
            return position;
        }

        public final Object getItem(int position) {
            return mList.get(position);
        }

        public final int getCount() {
            return mList.size();
        }

        public class ListItemData {
            public boolean IsChecked = false;
            public String Data;

            public ListItemData(String data, boolean isChecked) {
                Data = data;
                IsChecked = isChecked;
            }
        }
    }

    public void improveCoverage()
    {
        HtcListView mHtcListViewNew = new HtcListView(this);
        mHtcListViewNew.setAdapter(mAdapter);
        mHtcListViewNew.disableTouchEventInAnim();
        mHtcListViewNew.endDelAnimator();
        mHtcListViewNew.setClipToPadding(true);
        mHtcListViewNew.setOnPullDownListener(null);
        mHtcListViewNew.setVerticalScrollbarPosition(0);
        try {
            mHtcListViewNew.setDelPositionsList(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
