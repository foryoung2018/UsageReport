
package com.htc.sense.commoncontrol.demo.listview;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcListView.DeleteAnimationListener;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcListViewDemo extends CommonDemoActivityBase {
    /**
     * Our data which will be displayed
     */
    private static final String[] ENTRIES = {

            "ABBAYE DE BELLOC", "ABBAYE DU MONT DES CATS", "ABERTAM", "ABONDANCE", "ACKAWI", "ACORN",
            "ADELOST", "AFFIDELICE AU CHABLIS", "AFUEGA'L PITU", "AIRAG", "AIREDALE", "AISY CENDRE",
            "ALLGAUER EMMENTALER", "ALVERCA", "AMBERT", "AMERICAN CHEESE", "AMI DU CHAMBERTIN",
            "ANEJO ENCHILADO", "ANNEAU DU VIC-BILH", "ANTHORIRO", "APPENZELL", "ARAGON", "ARDI GASNA",
            "ARDRAHAN", "ARMENIAN STRING", "AROMES AU GENE DE MARC", "ASADERO", "ASIAGO",
            "AUBISQUE PYRENEES", "AUTUN", "AVAXTSKYR", "BABY SWISS", "BABYBEL", "BAGUETTE LAONNAISE",
            "BAKERS", "BALADI", "BALATON", "BANDAL", "BANON", "BARRY'S BAY CHEDDAR", "BASING",
            "BASKET CHEESE", "BATH CHEESE", "BAVARIAN BERGKASE", "BAYLOUGH", "BEAUFORT", "BEAUVOORDE",
            "BEENLEIGH BLUE", "BEER CHEESE", "BEL PAESE", "BERGADER", "BERGERE BLEUE", "BERKSWELL",
            "BEYAZ PEYNIR", "BIERKASE", "BISHOP KENNEDY", "BLARNEY", "BLEU D'AUVERGNE", "BLEU DE GEX",
            "BLEU DE LAQUEUILLE", "BLEU DE SEPTMONCEL", "BLEU DES CAUSSES", "BLUE", "BLUE CASTELLO",
            "BLUE RATHGORE", "BLUE VEIN (AUSTRALIAN)", "BLUE VEIN CHEESES", "BOCCONCINI",
            "BOCCONCINI (AUSTRALIAN)", "BOEREN LEIDENKAAS", "BONCHESTER", "BOSWORTH", "BOUGON",
            "BOULE DU ROVES", "BOULETTE D'AVESNES", "BOURSAULT", "BOURSIN", "BOUYSSOU", "BRA", "BRAUDOSTUR",
            "BREAKFAST CHEESE", "BREBIS DU LAVORT", "BREBIS DU LOCHOIS", "BREBIS DU PUYFAUCON",
            "BRESSE BLEU", "BRICK", "BRIE", "BRIE DE MEAUX", "BRIE DE MELUN", "BRILLAT-SAVARIN", "BRIN",
            "BRIN D' AMOUR", "BRIN D'AMOUR", "BRINZA (BURDUF BRINZA)", "BRIQUETTE DE BREBIS",
            "BRIQUETTE DU FOREZ", "BROCCIO", "BROCCIO DEMI-AFFINE", "BROUSSE DU ROVE", "BRUDER BASIL",
            "BRUSSELAE KAAS (FROMAGE DE BRUXELLES)", "BRYNDZA", "BUCHETTE D'ANJOU", "BUFFALO", "BURGOS",
            "BUTTE", "BUTTERKASE", "BUTTON (INNES)", "BUXTON BLUE", "CABECOU", "CABOC", "CABRALES",
            "CACHAILLE", "CACIOCAVALLO", "CACIOTTA", "CAERPHILLY", "CAIRNSMORE", "CALENZANA", "CAMBAZOLA",
            "CAMEMBERT DE NORMANDIE", "CANADIAN CHEDDAR", "CANESTRATO", "CANTAL", "CAPRICE DES DIEUX",
            "CAPRICORN GOAT", "CAPRIOLE BANON", "CARRE DE L'EST", "CASCIOTTA DI URBINO", "CASHEL BLUE",
            "CASTELLANO", "CASTELLENO", "CASTELMAGNO", "CASTELO BRANCO", "CASTIGLIANO", "CATHELAIN",
            "CELTIC PROMISE", "CENDRE D'OLIVET", "CERNEY", "CHABICHOU", "CHABICHOU DU POITOU",
            "CHABIS DE GATINE", "CHAOURCE", "CHAROLAIS", "CHAUMES", "CHEDDAR", "CHEDDAR CLOTHBOUND",
            "CHESHIRE", "CHEVRES", "CHEVROTIN DES ARAVIS", "CHONTALENO", "CIVRAY",
            "COEUR DE CAMEMBERT AU CALVADOS", "COEUR DE CHEVRE", "COLBY", "COLD PACK", "COMTE", "COOLEA",
            "COOLENEY", "COQUETDALE", "CORLEGGY", "CORNISH PEPPER", "COTHERSTONE", "COTIJA",
            "COTTAGE CHEESE", "COTTAGE CHEESE (AUSTRALIAN)", "COUGAR GOLD", "COULOMMIERS", "COVERDALE",
            "CRAYEUX DE RONCQ", "CREAM CHEESE", "CREAM HAVARTI", "CREMA AGRIA", "CREMA MEXICANA",
            "CREME FRAICHE", "CRESCENZA", "CROGHAN", "CROTTIN DE CHAVIGNOL", "CROTTIN DU CHAVIGNOL",
            "CROWDIE", "CROWLEY", "CUAJADA", "CURD", "CURE NANTAIS", "CURWORTHY", "CWMTAWE PECORINO",
            "CYPRESS GROVE CHEVRE", "DANABLU (DANISH BLUE)", "DANBO", "DANISH FONTINA", "DARALAGJAZSKY",
            "DAUPHIN", "DELICE DES FIOUVES", "DENHANY DORSET DRUM", "DERBY", "DESSERTNYJ BELYJ",
            "DEVON BLUE", "DEVON GARLAND", "DOLCELATTE", "DOOLIN", "DOPPELRHAMSTUFEL", "DORSET BLUE VINNEY",
            "DOUBLE GLOUCESTER", "DOUBLE WORCESTER", "DREUX A LA FEUILLE", "DRY JACK", "DUDDLESWELL",
            "DUNBARRA", "DUNLOP", "DUNSYRE BLUE", "DUROBLANDO", "DURRUS", "DUTCH MIMOLETTE (COMMISSIEKAAS)",
            "EDAM", "EDELPILZ", "EMENTAL GRAND CRU", "EMLETT", "EMMENTAL", "EPOISSES DE BOURGOGNE",
            "ESBAREICH", "ESROM", "ETORKI", "EVANSDALE FARMHOUSE BRIE", "EVORA DE L'ALENTEJO", "EXMOOR BLUE",
            "EXPLORATEUR", "FETA", "FETA (AUSTRALIAN)", "FIGUE", "FILETTA", "FIN-DE-SIECLE",
            "FINLANDIA SWISS", "FINN", "FIORE SARDO", "FLEUR DU MAQUIS", "FLOR DE GUIA", "FLOWER MARIE",
            "FOLDED", "FOLDED CHEESE WITH MINT", "FONDANT DE BREBIS", "FONTAINEBLEAU", "FONTAL",
            "FONTINA VAL D'AOSTA", "FORMAGGIO DI CAPRA", "FOUGERUS", "FOUR HERB GOUDA", "FOURME D' AMBERT",
            "FOURME DE HAUTE LOIRE", "FOURME DE MONTBRISON", "FRESH JACK", "FRESH MOZZARELLA",
            "FRESH RICOTTA", "FRESH TRUFFLES", "FRIBOURGEOIS", "FRIESEKAAS", "FRIESIAN", "FRIESLA",
            "FRINAULT", "FROMAGE A RACLETTE", "FROMAGE CORSE", "FROMAGE DE MONTAGNE DE SAVOIE",
            "FROMAGE FRAIS", "FRUIT CREAM CHEESE", "FRYING CHEESE", "FYNBO", "GABRIEL",
            "GALETTE DU PALUDIER", "GALETTE LYONNAISE", "GALLOWAY GOAT'S MILK GEMS", "GAMMELOST",
            "GAPERON A L'AIL", "GARROTXA", "GASTANBERRA", "GEITOST", "GIPPSLAND BLUE", "GJETOST",
            "GLOUCESTER", "GOLDEN CROSS", "GORGONZOLA", "GORNYALTAJSKI", "GOSPEL GREEN", "GOUDA", "GOUTU",
            "GOWRIE", "GRABETTO", "GRADDOST", "GRAFTON VILLAGE CHEDDAR", "GRANA", "GRANA PADANO",
            "GRAND VATEL", "GRATARON D' ARECHES", "GRATTE-PAILLE", "GRAVIERA", "GREUILH", "GREVE",
            "GRIS DE LILLE", "GRUYERE", "GUBBEEN", "GUERBIGNY", "HALLOUMI", "HALLOUMY (AUSTRALIAN)",
            "HALOUMI-STYLE CHEESE", "HARBOURNE BLUE", "HAVARTI", "HEIDI GRUYERE", "HEREFORD HOP",
            "HERRGARDSOST", "HERRIOT FARMHOUSE", "HERVE", "HIPI ITI", "HUBBARDSTON BLUE COW", "HUSHALLSOST",
            "IBERICO", "IDAHO GOATSTER", "IDIAZABAL", "IL BOSCHETTO AL TARTUFO", "ILE D'YEU", "ISLE OF MULL",
            "JARLSBERG", "JERMI TORTES", "JIBNEH ARABIEH", "JINDI BRIE", "JUBILEE BLUE", "JUUSTOLEIPA",
            "KADCHGALL", "KASERI", "KASHTA", "KEFALOTYRI", "KENAFA", "KERNHEM", "KERVELLA AFFINE",
            "KIKORANGI", "KING ISLAND CAPE WICKHAM BRIE", "KING RIVER GOLD", "KLOSTERKAESE", "KNOCKALARA",
            "KUGELKASE", "L'AVEYRONNAIS", "L'ECIR DE L'AUBRAC", "LA TAUPINIERE", "LA VACHE QUI RIT",
            "LAGUIOLE", "LAIROBELL", "LAJTA", "LANARK BLUE", "LANCASHIRE", "LANGRES", "LAPPI", "LARUNS",
            "LAVISTOWN", "LE BRIN", "LE FIUM ORBO", "LE LACANDOU", "LE ROULE", "LEAFIELD", "LEBBENE",
            "LEERDAMMER", "LEICESTER", "LEYDEN", "LIMBURGER", "LINCOLNSHIRE POACHER",
            "LINGOT SAINT BOUSQUET D'ORB", "LIPTAUER", "LITTLE RYDINGS", "LIVAROT", "LLANBOIDY",
            "LLANGLOFAN FARMHOUSE", "LOCH ARTHUR FARMHOUSE", "LODDISWELL AVONDALE", "LONGHORN", "LOU PALOU",
            "LOU PEVRE", "LYONNAIS", "MAASDAM", "MACCONAIS", "MAHOE AGED GOUDA", "MAHON", "MALVERN",
            "MAMIROLLE", "MANCHEGO", "MANOURI", "MANUR", "MARBLE CHEDDAR", "MARBLED CHEESES", "MAREDSOUS",
            "MARGOTIN", "MARIBO", "MAROILLES", "MASCARES", "MASCARPONE", "MASCARPONE (AUSTRALIAN)",
            "MASCARPONE TORTA", "MATOCQ", "MAYTAG BLUE", "MEIRA", "MENALLACK FARMHOUSE", "MENONITA",
            "MEREDITH BLUE", "MESOST", "METTON (CANCOILLOTTE)", "MEYER VINTAGE GOUDA", "MIHALIC PEYNIR",
            "MILLEENS", "MIMOLETTE", "MINE-GABHAR", "MINI BABY BELLS", "MIXTE", "MOLBO", "MONASTERY CHEESES",
            "MONDSEER", "MONT D'OR LYONNAIS", "MONTASIO", "MONTEREY JACK", "MONTEREY JACK DRY", "MORBIER",
            "MORBIER CRU DE MONTAGNE", "MOTHAIS A LA FEUILLE", "MOZZARELLA", "MOZZARELLA (AUSTRALIAN)",
            "MOZZARELLA DI BUFALA", "MOZZARELLA FRESH, IN WATER", "MOZZARELLA ROLLS", "MUNSTER", "MUROL",
            "MYCELLA", "MYZITHRA", "NABOULSI", "NANTAIS", "NEUFCHATEL", "NEUFCHATEL (AUSTRALIAN)", "NIOLO",
            "NOKKELOST", "NORTHUMBERLAND", "OAXACA", "OLDE YORK", "OLIVET AU FOIN", "OLIVET BLEU",
            "OLIVET CENDRE", "ORKNEY EXTRA MATURE CHEDDAR", "ORLA", "OSCHTJEPKA", "OSSAU FERMIER",
            "OSSAU-IRATY", "OSZCZYPEK", "OXFORD BLUE", "P'TIT BERRICHON", "PALET DE BABLIGNY", "PANEER",
            "PANELA", "PANNERONE", "PANT YS GAWN", "PARMESAN (PARMIGIANO)", "PARMIGIANO REGGIANO",
            "PAS DE L'ESCALETTE", "PASSENDALE", "PASTEURIZED PROCESSED", "PATE DE FROMAGE", "PATEFINE FORT",
            "PAVE D'AFFINOIS", "PAVE D'AUGE", "PAVE DE CHIRAC", "PAVE DU BERRY", "PECORINO",
            "PECORINO IN WALNUT LEAVES", "PECORINO ROMANO", "PEEKSKILL PYRAMID", "PELARDON DES CEVENNES",
            "PELARDON DES CORBIERES", "PENAMELLERA", "PENBRYN", "PENCARREG", "PERAIL DE BREBIS",
            "PETIT MORIN", "PETIT PARDOU", "PETIT-SUISSE", "PICODON DE CHEVRE", "PICOS DE EUROPA", "PIORA",
            "PITHTVIERS AU FOIN", "PLATEAU DE HERVE", "PLYMOUTH CHEESE", "PODHALANSKI", "POIVRE D'ANE",
            "POLKOLBIN", "PONT L'EVEQUE", "PORT NICHOLSON", "PORT-SALUT", "POSTEL", "POULIGNY-SAINT-PIERRE",
            "POURLY", "PRASTOST", "PRESSATO", "PRINCE-JEAN", "PROCESSED CHEDDAR", "PROVOLONE",
            "PROVOLONE (AUSTRALIAN)", "PYENGANA CHEDDAR", "PYRAMIDE", "QUARK", "QUARK (AUSTRALIAN)",
            "QUARTIROLO LOMBARDO", "QUATRE-VENTS", "QUERCY PETIT", "QUESO BLANCO",
            "QUESO BLANCO CON FRUTAS --PINA Y MANGO", "QUESO DE MURCIA", "QUESO DEL MONTSEC",
            "QUESO DEL TIETAR", "QUESO FRESCO", "QUESO FRESCO (ADOBERA)", "QUESO IBERICO", "QUESO JALAPENO",
            "QUESO MAJORERO", "QUESO MEDIA LUNA", "QUESO PARA FRIER", "QUESO QUESADILLA", "RABACAL",
            "RACLETTE", "RAGUSANO", "RASCHERA", "REBLOCHON", "RED LEICESTER", "REGAL DE LA DOMBES",
            "REGGIANITO", "REMEDOU", "REQUESON", "RICHELIEU", "RICOTTA", "RICOTTA (AUSTRALIAN)",
            "RICOTTA SALATA", "RIDDER", "RIGOTTE", "ROCAMADOUR", "ROLLOT", "ROMANO", "ROMANS PART DIEU",
            "RONCAL", "ROQUEFORT", "ROULE", "ROULEAU DE BEAULIEU", "ROYALP TILSIT", "RUBENS", "RUSTINU",
            "SAALAND PFARR", "SAANENKAESE", "SAGA", "SAGE DERBY", "SAINTE MAURE", "SAINT-MARCELLIN",
            "SAINT-NECTAIRE", "SAINT-PAULIN", "SALERS", "SAMSO", "SAN SIMON", "SANCERRE", "SAP SAGO",
            "SARDO", "SARDO EGYPTIAN", "SBRINZ", "SCAMORZA", "SCHABZIEGER", "SCHLOSS", "SELLES SUR CHER",
            "SELVA", "SERAT", "SERIOUSLY STRONG CHEDDAR", "SERRA DA ESTRELA", "SHARPAM", "SHELBURNE CHEDDAR",
            "SHROPSHIRE BLUE", "SIRAZ", "SIRENE", "SMOKED GOUDA", "SOMERSET BRIE", "SONOMA JACK",
            "SOTTOCENARE AL TARTUFO", "SOUMAINTRAIN", "SOURIRE LOZERIEN", "SPENWOOD",
            "SRAFFORDSHIRE ORGANIC", "ST. AGUR BLUE CHEESE", "STILTON", "STINKING BISHOP", "STRING",
            "SUSSEX SLIPCOTE", "SVECIAOST", "SWALEDALE", "SWEET STYLE SWISS", "SWISS",
            "SYRIAN (ARMENIAN STRING)", "TALA", "TALEGGIO", "TAMIE", "TASMANIA HIGHLAND CHEVRE LOG",
            "TAUPINIERE", "TEIFI", "TELEMEA", "TESTOURI", "TETE DE MOINE", "TETILLA", "TEXAS GOAT CHEESE",
            "TIBET", "TILLAMOOK CHEDDAR", "TILSIT", "TIMBOON BRIE", "TOMA", "TOMME BRULEE",
            "TOMME D'ABONDANCE", "TOMME DE CHEVRE", "TOMME DE ROMANS", "TOMME DE SAVOIE",
            "TOMME DES CHOUANS", "TOMMES", "TORTA DEL CASAR", "TOSCANELLO", "TOUREE DE L'AUBIER",
            "TOURMALET", "TRAPPE (VERITABLE)", "TROIS CORNES DE VENDEE", "TRONCHON", "TROU DU CRU", "TRUFFE",
            "TUPI", "TURUNMAA", "TYMSBORO", "TYN GRUG", "TYNING", "UBRIACO", "ULLOA",
            "VACHERIN-FRIBOURGEOIS", "VALENCAY", "VASTERBOTTENOST", "VENACO", "VENDOMOIS", "VIEUX CORSE",
            "VIGNOTTE", "VULSCOMBE", "WAIMATA FARMHOUSE BLUE", "WASHED RIND CHEESE (AUSTRALIAN)", "WATERLOO",
            "WEICHKAESE", "WELLINGTON", "WENSLEYDALE", "WHITE STILTON", "WHITESTONE FARMHOUSE", "WIGMORE",
            "WOODSIDE CABECOU", "XANADU", "XYNOTYRO", "YARG CORNISH", "YARRA VALLEY PYRAMID",
            "YORKSHIRE BLUE", "ZAMORANO", "ZANETTI GRANA PADANO", "ZANETTI PARMIGIANO REGGIANO"
    };

    /**
     * the adapter for HTC list view
     */
    private ListViewAdapter mAdapter = null;

    /**
     * The HTC list view
     */
    private HtcListView mHtcListView = null;

    private boolean mIsDelStartCalled = false;

    private boolean mIsDelEndCalled = false;

    private boolean mIsAnimationRunning = false;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then
     *            this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *            Notice: Otherwise it is null.
     * @see onStart() onSaveInstanceState(Bundle) onRestoreInstanceState(Bundle) onPostCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);

        setContentView(R.layout.htclistview_main4);
        mHtcListView = (HtcListView) findViewById(R.id.htc_list);
        if (mHtcListView != null) {
            setAdapter();
            mHtcListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        }
    }

    final static int MENU_DELETE = 0;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mHtcListView!=null){
            mHtcListView.endDelAnimator();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_DELETE, 0, "delete item");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_DELETE:
                deleteItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteItem() {
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
        mAdapter = new ListViewAdapter(this, R.layout.htclistview_demo_htc_listview_item_layout, ENTRIES);
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
         * @param layoutId: the resource ID of the layout Default value:R.layout.htc_listview_item_layout.And
         *            you can choose another one named R.layout.htc_listview_item_multiplechoice_layout.
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
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder holder;
            if (convertView == null) {
                v = mInflater.inflate(m_layoutId, parent, false);
                holder = new ViewHolder();
                holder.checkBox = (HtcCheckBox) v.findViewById(R.id.chkbox);
                holder.checkBox.setFocusable(false);
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
}
