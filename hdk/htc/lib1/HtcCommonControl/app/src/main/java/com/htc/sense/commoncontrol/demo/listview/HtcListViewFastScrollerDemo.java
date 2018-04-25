package com.htc.sense.commoncontrol.demo.listview;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineStamp;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

/**
 * HtcFastScroller Demo with 3 index types (alphabet, priority, date).
 */
public class HtcListViewFastScrollerDemo extends CommonDemoActivityBase {

    HtcListView mListView = null;
    MyAdapter mAdapter = null;
    private static final int ALPHABET_INDEX = 0;
    private static final int PRIORITY_INDEX = 1;
    private static final int DATE_INDEX = 2;
    private static int INDEX_TYPE = ALPHABET_INDEX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        setContentView(R.layout.htclistview_main);
        mListView = (HtcListView) this.findViewById(R.id.list);
        mAdapter = new MyAdapter(this, R.layout.htclistview_demo_htc_fastscroller_item_layout, ENTRIES);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ALPHABET_INDEX, 0, "Alphabet index");
        menu.add(0, PRIORITY_INDEX, 1, "Priority index");
        menu.add(0, DATE_INDEX, 2, "Date index");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case ALPHABET_INDEX:
            INDEX_TYPE = ALPHABET_INDEX;
            mAdapter.notifyDataSetChanged();
            return true;
        case PRIORITY_INDEX:
            INDEX_TYPE = PRIORITY_INDEX;
            mAdapter.notifyDataSetChanged();
            return true;
        case DATE_INDEX:
            INDEX_TYPE = DATE_INDEX;
            mAdapter.notifyDataSetChanged();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    /*
     * You have to implement "SectionIndexer" if you want to setFastScrollEnabled(true)
     */
    class MyAdapter extends BaseAdapter implements SectionIndexer{

        private LayoutInflater mInflater;
        private int m_layoutId = R.layout.htclistview_demo_htc_fastscroller_item_layout;
        private String [] mString;
        private SparseIntArray mAlphaMap; // Store mapping of the section and it's start position
        private java.text.Collator mCollator;
        private final String HIGH_PRIORITY = "Priority " + "高";
        private final String MEDIUM_PRIORITY = "Priority " + "中";
        private final String LOW_PRIORITY = "Priority " + "低";

        public MyAdapter(Context context, int textViewResourceId,
                String[] string) {

            mString = string;
            m_layoutId = textViewResourceId;
            mInflater = (LayoutInflater)(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            mAlphaMap = new SparseIntArray(26 /* Optimize for English */);
            mCollator = java.text.Collator.getInstance();
            mCollator.setStrength(java.text.Collator.PRIMARY);
        }

        /*
         * Performs a binary search or cache lookup to find the first row that matches a given section's starting letter.
         */
        @Override
        public int getPositionForSection(int sectionIndex) {
            switch (INDEX_TYPE) {
            case ALPHABET_INDEX:
                return indexOfAlphabet(sectionIndex);
            case PRIORITY_INDEX:
                return indexOfPriority(sectionIndex);
            case DATE_INDEX:
                return indexOfDate(sectionIndex);
            default:
                return sectionIndex;
            }
        }

        /*
         * Returns the section index for a given position in the list by querying the item and comparing it with all items in the section array.
         */
        @Override
        public int getSectionForPosition(int positionIndex) {
            switch (INDEX_TYPE) {
            case ALPHABET_INDEX:
                String curName = ENTRIES[positionIndex];
                for (int i = 0; i < ENTRIES_ALPHABET.length; i++) {
                    String letter = ENTRIES_ALPHABET[i];
                    if (compare(curName, letter) == 0) {
                        return i;
                    }
                }
                return 0; // Don't recognize the letter - falls under zero'th section
            case PRIORITY_INDEX:
                if (positionIndex <= (mString.length / 3)) {
                    return 0;
                } else if (positionIndex <= ((mString.length * 2) / 3)) {
                    return 1;
                } else {
                    return 2;
                }
            case DATE_INDEX:
                for (int j = 0 ; j < 30 ; j++) {
                    if (positionIndex <= (mString.length * (j + 1)) / 30) {
                        return j;
                    }
                }
            default:
                return 0;
            }
        }

        /**
         * Default implementation compares the first character of word with letter.
         */
        protected int compare(String word, String letter) {
            final String firstLetter;
            if (word.length() == 0) {
                firstLetter = " ";
            } else {
                firstLetter = word.substring(0, 1);
            }

            return mCollator.compare(firstLetter, letter);
        }

        /*
         * Returns the section array constructed from the alphabet provided in the constructor.
         */
        @Override
        public Object[] getSections() {
            switch (INDEX_TYPE) {
            case ALPHABET_INDEX:
                return ENTRIES_ALPHABET;
            case PRIORITY_INDEX:
                return ENTRIES_PRIORITY;
            case DATE_INDEX:
                return ENTRIES_DATE;
            default:
                return null;
            }
        }

        @Override
        public int getCount() {
            return mString.length;
        }

        @Override
        public Object getItem(int position) {
            return mString[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HtcListItem i;
            if (convertView == null) {
                i = (HtcListItem) mInflater.inflate(m_layoutId, parent, false);

            } else {
                i = (HtcListItem) convertView;
            }
            HtcListItem2LineText text;
            text = (HtcListItem2LineText)i.findViewById(R.id.text1);

            HtcListItem2LineStamp stamp;
            stamp = (HtcListItem2LineStamp)i.findViewById(R.id.stamp1);

            // set sentence for item
            String str = mString[position];
            if (str != null) {
                text.setPrimaryText(str);
            }

            // set priority for item
            if (position <= (mString.length / 3)) {
                text.setSecondaryText(HIGH_PRIORITY);
            } else if (position <= ((mString.length * 2) / 3)) {
                text.setSecondaryText(MEDIUM_PRIORITY);
            } else {
                text.setSecondaryText(LOW_PRIORITY);
            }

            // set date for item
            for (int j = 0 ; j < 30 ; j++) {
                if (position <= (mString.length * (j + 1)) / 30) {
                    stamp.setSecondaryText(ENTRIES_DATE[j]);
                    break;
                }
            }

            return i;
        }

        /**
         * Performs a binary search or cache lookup to find the first row that
         * matches a given section's starting letter.
         * @param sectionIndex the section to search for
         * @return the row index of the first occurrence, or the nearest next letter.
         * For instance, if searching for "T" and no "T" is found, then the first
         * row starting with "U" or any higher letter is returned. If there is no
         * data following "T" at all, then the list size is returned.
         */
        private int indexOfAlphabet(int sectionIndex) {
            if (ENTRIES_ALPHABET == null) {
                return 0;
            }
            // Check bounds
            if(sectionIndex <= 0) {
                return 0;
            }
            // Check bounds
            if (sectionIndex >= ENTRIES_ALPHABET.length) {
                sectionIndex = ENTRIES_ALPHABET.length - 1;
            }
            // prepare for Binary search
            int count = getCount();
            int start = 0;
            int end = count;
            int pos;

            String letter = ENTRIES_ALPHABET[sectionIndex];
            letter = letter.toUpperCase();
            // The section character we want to find it's start position
            int key = letter.charAt(0);

            // Get the start position of this key, if not set yet return Integer.MIN_VALUE
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
                    // Not approximate, this is the confirmed start of section, return it
                    return pos;
                }
            }

            // Do we have the position of the previous section?
            // If we have. Set it as the start point of binary search
            if (sectionIndex > 0) {
                int prevLetter =
                        ENTRIES_ALPHABET[sectionIndex - 1].toString().charAt(0);
                int prevLetterPos = mAlphaMap.get(prevLetter, Integer.MIN_VALUE);
                if (prevLetterPos != Integer.MIN_VALUE) {
                    start = Math.abs(prevLetterPos);
                }
            }

            // Now that we have a possibly optimized start and end, let's binary search
            pos = (end + start) / 2;
            while (pos < end) {
                // Get DataItem at pos
                String curData = (String) getItem(pos);
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
                    // Enter approximation in hash if a better solution doesn't exist
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
                    // curLetter and key are the same, but that doesn't mean it's the start position
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

        private int indexOfPriority(int sectionIndex) {
            if (sectionIndex == 0) {
                return 0;
            } else if (sectionIndex == 1) {
                return (mString.length / 3) + 1;
            } else {
                return ((mString.length * 2) / 3) + 1;
            }
        }

        private int indexOfDate(int sectionIndex) {
            if (sectionIndex == 0) {
                return 0;
            } else {
                return ((mString.length * sectionIndex) / 30) + 1;
            }
        }

    }


    /**
     * Our data which will be displayed
     */
    private static final String[] ENTRIES = {

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
            "CHABIS DE GATINE", "CHAOURCE", "CHAROLAIS", "CHAUMES", "CHEDDAR",
            "CHEDDAR CLOTHBOUND", "CHESHIRE", "CHEVRES", "CHEVROTIN DES ARAVIS", "CHONTALENO",
            "CIVRAY", "COEUR DE CAMEMBERT AU CALVADOS", "COEUR DE CHEVRE", "COLBY", "COLD PACK",
            "COMTE", "COOLEA", "COOLENEY", "COQUETDALE", "CORLEGGY", "CORNISH PEPPER",
            "COTHERSTONE", "COTIJA", "COTTAGE CHEESE", "COTTAGE CHEESE (AUSTRALIAN)",
            "COUGAR GOLD", "COULOMMIERS", "COVERDALE", "CRAYEUX DE RONCQ", "CREAM CHEESE",
            "CREAM HAVARTI", "CREMA AGRIA", "CREMA MEXICANA", "CREME FRAICHE", "CRESCENZA",
            "CROGHAN", "CROTTIN DE CHAVIGNOL", "CROTTIN DU CHAVIGNOL", "CROWDIE", "CROWLEY",
            "CUAJADA", "CURD", "CURE NANTAIS", "CURWORTHY", "CWMTAWE PECORINO",
            "CYPRESS GROVE CHEVRE", "DANABLU (DANISH BLUE)", "DANBO", "DANISH FONTINA",
            "DARALAGJAZSKY", "DAUPHIN", "DELICE DES FIOUVES", "DENHANY DORSET DRUM", "DERBY",
            "DESSERTNYJ BELYJ", "DEVON BLUE", "DEVON GARLAND", "DOLCELATTE", "DOOLIN",
            "DOPPELRHAMSTUFEL", "DORSET BLUE VINNEY", "DOUBLE GLOUCESTER", "DOUBLE WORCESTER",
            "DREUX A LA FEUILLE", "DRY JACK", "DUDDLESWELL", "DUNBARRA", "DUNLOP", "DUNSYRE BLUE",
            "DUROBLANDO", "DURRUS", "DUTCH MIMOLETTE (COMMISSIEKAAS)", "EDAM", "EDELPILZ",
            "EMENTAL GRAND CRU", "EMLETT", "EMMENTAL", "EPOISSES DE BOURGOGNE", "ESBAREICH",
            "ESROM", "ETORKI", "EVANSDALE FARMHOUSE BRIE", "EVORA DE L'ALENTEJO", "EXMOOR BLUE",
            "EXPLORATEUR", "FETA", "FETA (AUSTRALIAN)", "FIGUE", "FILETTA", "FIN-DE-SIECLE",
            "FINLANDIA SWISS", "FINN", "FIORE SARDO", "FLEUR DU MAQUIS", "FLOR DE GUIA",
            "FLOWER MARIE", "FOLDED", "FOLDED CHEESE WITH MINT", "FONDANT DE BREBIS",
            "FONTAINEBLEAU", "FONTAL", "FONTINA VAL D'AOSTA", "FORMAGGIO DI CAPRA", "FOUGERUS",
            "FOUR HERB GOUDA", "FOURME D' AMBERT", "FOURME DE HAUTE LOIRE", "FOURME DE MONTBRISON",
            "FRESH JACK", "FRESH MOZZARELLA", "FRESH RICOTTA", "FRESH TRUFFLES", "FRIBOURGEOIS",
            "FRIESEKAAS", "FRIESIAN", "FRIESLA", "FRINAULT", "FROMAGE A RACLETTE", "FROMAGE CORSE",
            "FROMAGE DE MONTAGNE DE SAVOIE", "FROMAGE FRAIS", "FRUIT CREAM CHEESE",
            "FRYING CHEESE", "FYNBO", "GABRIEL", "GALETTE DU PALUDIER", "GALETTE LYONNAISE",
            "GALLOWAY GOAT'S MILK GEMS", "GAMMELOST", "GAPERON A L'AIL", "GARROTXA", "GASTANBERRA",
            "GEITOST", "GIPPSLAND BLUE", "GJETOST", "GLOUCESTER", "GOLDEN CROSS", "GORGONZOLA",
            "GORNYALTAJSKI", "GOSPEL GREEN", "GOUDA", "GOUTU", "GOWRIE", "GRABETTO", "GRADDOST",
            "GRAFTON VILLAGE CHEDDAR", "GRANA", "GRANA PADANO", "GRAND VATEL",
            "GRATARON D' ARECHES", "GRATTE-PAILLE", "GRAVIERA", "GREUILH", "GREVE",
            "GRIS DE LILLE", "GRUYERE", "GUBBEEN", "GUERBIGNY", "HALLOUMI",
            "HALLOUMY (AUSTRALIAN)", "HALOUMI-STYLE CHEESE", "HARBOURNE BLUE", "HAVARTI",
            "HEIDI GRUYERE", "HEREFORD HOP", "HERRGARDSOST", "HERRIOT FARMHOUSE", "HERVE",
            "HIPI ITI", "HUBBARDSTON BLUE COW", "HUSHALLSOST", "IBERICO", "IDAHO GOATSTER",
            "IDIAZABAL", "IL BOSCHETTO AL TARTUFO", "ILE D'YEU", "ISLE OF MULL", "JARLSBERG",
            "JERMI TORTES", "JIBNEH ARABIEH", "JINDI BRIE", "JUBILEE BLUE", "JUUSTOLEIPA",
            "KADCHGALL", "KASERI", "KASHTA", "KEFALOTYRI", "KENAFA", "KERNHEM", "KERVELLA AFFINE",
            "KIKORANGI", "KING ISLAND CAPE WICKHAM BRIE", "KING RIVER GOLD", "KLOSTERKAESE",
            "KNOCKALARA", "KUGELKASE", "L'AVEYRONNAIS", "L'ECIR DE L'AUBRAC", "LA TAUPINIERE",
            "LA VACHE QUI RIT", "LAGUIOLE", "LAIROBELL", "LAJTA", "LANARK BLUE", "LANCASHIRE",
            "LANGRES", "LAPPI", "LARUNS", "LAVISTOWN", "LE BRIN", "LE FIUM ORBO", "LE LACANDOU",
            "LE ROULE", "LEAFIELD", "LEBBENE", "LEERDAMMER", "LEICESTER", "LEYDEN", "LIMBURGER",
            "LINCOLNSHIRE POACHER", "LINGOT SAINT BOUSQUET D'ORB", "LIPTAUER", "LITTLE RYDINGS",
            "LIVAROT", "LLANBOIDY", "LLANGLOFAN FARMHOUSE", "LOCH ARTHUR FARMHOUSE",
            "LODDISWELL AVONDALE", "LONGHORN", "LOU PALOU", "LOU PEVRE", "LYONNAIS", "MAASDAM",
            "MACCONAIS", "MAHOE AGED GOUDA", "MAHON", "MALVERN", "MAMIROLLE", "MANCHEGO",
            "MANOURI", "MANUR", "MARBLE CHEDDAR", "MARBLED CHEESES", "MAREDSOUS", "MARGOTIN",
            "MARIBO", "MAROILLES", "MASCARES", "MASCARPONE", "MASCARPONE (AUSTRALIAN)",
            "MASCARPONE TORTA", "MATOCQ", "MAYTAG BLUE", "MEIRA", "MENALLACK FARMHOUSE",
            "MENONITA", "MEREDITH BLUE", "MESOST", "METTON (CANCOILLOTTE)", "MEYER VINTAGE GOUDA",
            "MIHALIC PEYNIR", "MILLEENS", "MIMOLETTE", "MINE-GABHAR", "MINI BABY BELLS", "MIXTE",
            "MOLBO", "MONASTERY CHEESES", "MONDSEER", "MONT D'OR LYONNAIS", "MONTASIO",
            "MONTEREY JACK", "MONTEREY JACK DRY", "MORBIER", "MORBIER CRU DE MONTAGNE",
            "MOTHAIS A LA FEUILLE", "MOZZARELLA", "MOZZARELLA (AUSTRALIAN)",
            "MOZZARELLA DI BUFALA", "MOZZARELLA FRESH, IN WATER", "MOZZARELLA ROLLS", "MUNSTER",
            "MUROL", "MYCELLA", "MYZITHRA", "NABOULSI", "NANTAIS", "NEUFCHATEL",
            "NEUFCHATEL (AUSTRALIAN)", "NIOLO", "NOKKELOST", "NORTHUMBERLAND", "OAXACA",
            "OLDE YORK", "OLIVET AU FOIN", "OLIVET BLEU", "OLIVET CENDRE",
            "ORKNEY EXTRA MATURE CHEDDAR", "ORLA", "OSCHTJEPKA", "OSSAU FERMIER", "OSSAU-IRATY",
            "OSZCZYPEK", "OXFORD BLUE", "P'TIT BERRICHON", "PALET DE BABLIGNY", "PANEER", "PANELA",
            "PANNERONE", "PANT YS GAWN", "PARMESAN (PARMIGIANO)", "PARMIGIANO REGGIANO",
            "PAS DE L'ESCALETTE", "PASSENDALE", "PASTEURIZED PROCESSED", "PATE DE FROMAGE",
            "PATEFINE FORT", "PAVE D'AFFINOIS", "PAVE D'AUGE", "PAVE DE CHIRAC", "PAVE DU BERRY",
            "PECORINO", "PECORINO IN WALNUT LEAVES", "PECORINO ROMANO", "PEEKSKILL PYRAMID",
            "PELARDON DES CEVENNES", "PELARDON DES CORBIERES", "PENAMELLERA", "PENBRYN",
            "PENCARREG", "PERAIL DE BREBIS", "PETIT MORIN", "PETIT PARDOU", "PETIT-SUISSE",
            "PICODON DE CHEVRE", "PICOS DE EUROPA", "PIORA", "PITHTVIERS AU FOIN",
            "PLATEAU DE HERVE", "PLYMOUTH CHEESE", "PODHALANSKI", "POIVRE D'ANE", "POLKOLBIN",
            "PONT L'EVEQUE", "PORT NICHOLSON", "PORT-SALUT", "POSTEL", "POULIGNY-SAINT-PIERRE",
            "POURLY", "PRASTOST", "PRESSATO", "PRINCE-JEAN", "PROCESSED CHEDDAR", "PROVOLONE",
            "PROVOLONE (AUSTRALIAN)", "PYENGANA CHEDDAR", "PYRAMIDE", "QUARK",
            "QUARK (AUSTRALIAN)", "QUARTIROLO LOMBARDO", "QUATRE-VENTS", "QUERCY PETIT",
            "QUESO BLANCO", "QUESO BLANCO CON FRUTAS --PINA Y MANGO", "QUESO DE MURCIA",
            "QUESO DEL MONTSEC", "QUESO DEL TIETAR", "QUESO FRESCO", "QUESO FRESCO (ADOBERA)",
            "QUESO IBERICO", "QUESO JALAPENO", "QUESO MAJORERO", "QUESO MEDIA LUNA",
            "QUESO PARA FRIER", "QUESO QUESADILLA", "RABACAL", "RACLETTE", "RAGUSANO", "RASCHERA",
            "REBLOCHON", "RED LEICESTER", "REGAL DE LA DOMBES", "REGGIANITO", "REMEDOU",
            "REQUESON", "RICHELIEU", "RICOTTA", "RICOTTA (AUSTRALIAN)", "RICOTTA SALATA", "RIDDER",
            "RIGOTTE", "ROCAMADOUR", "ROLLOT", "ROMANO", "ROMANS PART DIEU", "RONCAL", "ROQUEFORT",
            "ROULE", "ROULEAU DE BEAULIEU", "ROYALP TILSIT", "RUBENS", "RUSTINU", "SAALAND PFARR",
            "SAANENKAESE", "SAGA", "SAGE DERBY", "SAINTE MAURE", "SAINT-MARCELLIN",
            "SAINT-NECTAIRE", "SAINT-PAULIN", "SALERS", "SAMSO", "SAN SIMON", "SANCERRE",
            "SAP SAGO", "SARDO", "SARDO EGYPTIAN", "SBRINZ", "SCAMORZA", "SCHABZIEGER", "SCHLOSS",
            "SELLES SUR CHER", "SELVA", "SERAT", "SERIOUSLY STRONG CHEDDAR", "SERRA DA ESTRELA",
            "SHARPAM", "SHELBURNE CHEDDAR", "SHROPSHIRE BLUE", "SIRAZ", "SIRENE", "SMOKED GOUDA",
            "SOMERSET BRIE", "SONOMA JACK", "SOTTOCENARE AL TARTUFO", "SOUMAINTRAIN",
            "SOURIRE LOZERIEN", "SPENWOOD", "SRAFFORDSHIRE ORGANIC", "ST. AGUR BLUE CHEESE",
            "STILTON", "STINKING BISHOP", "STRING", "SUSSEX SLIPCOTE", "SVECIAOST", "SWALEDALE",
            "SWEET STYLE SWISS", "SWISS", "SYRIAN (ARMENIAN STRING)", "TALA", "TALEGGIO", "TAMIE",
            "TASMANIA HIGHLAND CHEVRE LOG", "TAUPINIERE", "TEIFI", "TELEMEA", "TESTOURI",
            "TETE DE MOINE", "TETILLA", "TEXAS GOAT CHEESE", "TIBET", "TILLAMOOK CHEDDAR",
            "TILSIT", "TIMBOON BRIE", "TOMA", "TOMME BRULEE", "TOMME D'ABONDANCE",
            "TOMME DE CHEVRE", "TOMME DE ROMANS", "TOMME DE SAVOIE", "TOMME DES CHOUANS", "TOMMES",
            "TORTA DEL CASAR", "TOSCANELLO", "TOUREE DE L'AUBIER", "TOURMALET",
            "TRAPPE (VERITABLE)", "TROIS CORNES DE VENDEE", "TRONCHON", "TROU DU CRU", "TRUFFE",
            "TUPI", "TURUNMAA", "TYMSBORO", "TYN GRUG", "TYNING", "UBRIACO", "ULLOA",
            "VACHERIN-FRIBOURGEOIS", "VALENCAY", "VASTERBOTTENOST", "VENACO", "VENDOMOIS",
            "VIEUX CORSE", "VIGNOTTE", "VULSCOMBE", "WAIMATA FARMHOUSE BLUE",
            "WASHED RIND CHEESE (AUSTRALIAN)", "WATERLOO", "WEICHKAESE", "WELLINGTON",
            "WENSLEYDALE", "WHITE STILTON", "WHITESTONE FARMHOUSE", "WIGMORE", "WOODSIDE CABECOU",
            "XANADU", "XYNOTYRO", "YARG CORNISH", "YARRA VALLEY PYRAMID", "YORKSHIRE BLUE",
            "ZAMORANO", "ZANETTI GRANA PADANO", "ZANETTI PARMIGIANO REGGIANO"
            };

    private static final String[] ENTRIES_ALPHABET = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R",    "S", "T",
        "U", "V", "W", "X", "Y", "Z"
    };

    private static final String[] ENTRIES_DATE = {
        "Apr 30, 2012", "Apr 29, 2012", "Apr 28, 2012", "Apr 27, 2012",
        "Apr 26, 2012", "Apr 25, 2012", "Apr 24, 2012", "Apr 23, 2012",
        "Apr 22, 2012", "Apr 21, 2012", "Apr 20, 2012", "Apr 19, 2012",
        "Apr 18, 2012", "Apr 17, 2012", "Apr 16, 2012", "Apr 15, 2012",
        "Apr 14, 2012", "Apr 13, 2012", "Apr 12, 2012", "Apr 11, 2012",
        "Apr 10, 2012", "Apr 9, 2012", "Apr 8, 2012", "Apr 7, 2012",
        "Apr 6, 2012", "Apr 5, 2012", "Apr 4, 2012", "Apr 3, 2012",
        "Apr 2, 2012", "Apr 1, 2012",
    };

    private static final String[] ENTRIES_PRIORITY = {
        "高", "中", "低",
    };
}


