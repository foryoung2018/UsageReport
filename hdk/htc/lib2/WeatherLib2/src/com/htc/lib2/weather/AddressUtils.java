
package com.htc.lib2.weather;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.res.Resources;

/**
 * Address Utilities used to check if input text can match current system
 * language.
 */
public class AddressUtils {
    private ArrayList<UnicodeBlock> mBlockList;
    private ArrayList<UnicodeDef> mDefList;

    private static class UnicodeDef {
        public long high;
        public long low;
    }

    private interface UnicodeProvider {
        ArrayList<UnicodeBlock> getBlockList();

        ArrayList<UnicodeDef> getDefList();
    };

    /**
     * Address Utilities.
     * 
     * @param res Resources
     */
    public AddressUtils(Resources res) {
        String language = res.getConfiguration().locale.getLanguage();
        Map<String, UnicodeProvider> map = generateMap();
        UnicodeProvider provider = map.get(language);
        if (provider != null) {
            mBlockList = provider.getBlockList();
            mDefList = provider.getDefList();
        }
    }

    /**
     * Check if input text can match current system language.
     * 
     * @param text input text
     * @return <code>true</code> if compared result is matched;
     *         <code>false</code> others
     */
    public boolean isMatchedSystemLanguage(String text) {
        if (text == null || text.length() == 0) {
            return false;
        }
        if (mBlockList == null && mDefList == null) {
            return true;
        }
        int i;
        for (i = 0; i < text.length(); i++) {
            int code = Character.codePointAt(text, i);
            if (isInRangeBlock(code) || isInRangeCode(code)) {
                return true;
            }
            int count = Character.charCount(code);
            if (count > 1) {
                i += count - 1; // find next position ..
            }
        }
        return false;
    }

    private boolean isInRangeBlock(int code) {
        if (mBlockList == null || mBlockList.size() == 0) {
            return false;
        }
        UnicodeBlock block = UnicodeBlock.of(code);
        for (UnicodeBlock info : mBlockList) {
            if (block == info) {
                return true;
            }
        }
        return false;
    }

    private boolean isInRangeCode(int code) {
        if (mDefList == null || mDefList.size() == 0) {
            return false;
        }
        for (UnicodeDef def : mDefList) {
            if (code <= def.high && code >= def.low) {
                return true;
            }
        }
        return false;
    }

    private void addBlockArabic(ArrayList<UnicodeBlock> codeList) {
        // U+0600..U+06FF
        codeList.add(UnicodeBlock.ARABIC);
        // U+FB50..U+FDFF
        codeList.add(UnicodeBlock.ARABIC_PRESENTATION_FORMS_A);
        // U+FE70..U+FEFF
        codeList.add(UnicodeBlock.ARABIC_PRESENTATION_FORMS_B);
    }

    private void addBlockArabicUnSupport(ArrayList<UnicodeDef> codeList) {
        // Arabic Supplement
        UnicodeDef supplement = new UnicodeDef();
        supplement.low = 0x0750;
        supplement.high = 0x077F;
        codeList.add(supplement);
        // Arabic Extended-A
        UnicodeDef extendedA = new UnicodeDef();
        extendedA.low = 0x08A0;
        extendedA.high = 0x08FF;
        codeList.add(extendedA);
    }

    private void addBlockLatin(ArrayList<UnicodeBlock> codeList) {
        // U+0000..U+007F
        codeList.add(UnicodeBlock.BASIC_LATIN);
        // U+0080..U+00FF
        codeList.add(UnicodeBlock.LATIN_1_SUPPLEMENT);
        // U+0100..U+017F
        codeList.add(UnicodeBlock.LATIN_EXTENDED_A);
        // U+0180..U+024F
        codeList.add(UnicodeBlock.LATIN_EXTENDED_B);
        // U+0250..U+02AF
        codeList.add(UnicodeBlock.IPA_EXTENSIONS);
        // U+1E00..U+1EFF
        codeList.add(UnicodeBlock.LATIN_EXTENDED_ADDITIONAL);
    }

    private void addBlockLatinUnSupport(ArrayList<UnicodeDef> codeList) {
        // Latin Extended-C
        UnicodeDef extendC = new UnicodeDef();
        extendC.low = 0x2C60;
        extendC.high = 0x2C7F;
        codeList.add(extendC);
        // Latin Extended-D
        UnicodeDef extendD = new UnicodeDef();
        extendD.low = 0xA720;
        extendD.high = 0xA7FF;
        codeList.add(extendD);
        // Latin Ligatures
        UnicodeDef ligatures = new UnicodeDef();
        ligatures.low = 0xFB00;
        ligatures.high = 0xFB06;
        codeList.add(ligatures);
        // Fullwidth Latin Letters
        UnicodeDef fullwidth = new UnicodeDef();
        fullwidth.low = 0xFF00;
        fullwidth.high = 0xFF5E;
        codeList.add(fullwidth);
    }

    private void addBlockGreek(ArrayList<UnicodeBlock> codeList) {
        // U+0370..U+3FF
        codeList.add(UnicodeBlock.GREEK);
        // U+1F00..U+1FFF
        codeList.add(UnicodeBlock.GREEK_EXTENDED);
    }

    private void addBlockGreekUnSupport(ArrayList<UnicodeDef> codeList) {
        // Coptic
        UnicodeDef coptic = new UnicodeDef();
        coptic.low = 0x2C80;
        coptic.high = 0x2CFF;
        codeList.add(coptic);
    }

    private void addBlockCyrillic(ArrayList<UnicodeBlock> codeList) {
        // U+0400..U+04FF
        codeList.add(UnicodeBlock.CYRILLIC);
        // U+0500..U+052F
        codeList.add(UnicodeBlock.CYRILLIC_SUPPLEMENTARY);
    }

    private void addBlockHebrew(ArrayList<UnicodeBlock> codeList) {
        // U+0590..U+05FF
        codeList.add(UnicodeBlock.HEBREW);
    }

    private void addBlockHebrewUnSupport(ArrayList<UnicodeDef> codeList) {
        // Hebrew Presentation Forms
        UnicodeDef forms = new UnicodeDef();
        forms.low = 0xFB1D;
        forms.high = 0xFB4F;
        codeList.add(forms);
    }

    private void addBlockArmenian(ArrayList<UnicodeBlock> codeList) {
        // U+0530...U+058F
        codeList.add(UnicodeBlock.ARMENIAN);
    }

    private void addBlockArmenianUnSupport(ArrayList<UnicodeDef> codeList) {
        // Armenian Ligatures
        UnicodeDef forms = new UnicodeDef();
        forms.low = 0xFB13;
        forms.high = 0xFB17;
        codeList.add(forms);
    }

    private void addBlockJapanse(ArrayList<UnicodeBlock> codeList) {
        // U+3040..U+309F
        codeList.add(UnicodeBlock.HIRAGANA);
        // U+30A0...U+30FF
        codeList.add(UnicodeBlock.KATAKANA);
        // U+31F0...U+31FF
        codeList.add(UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS);
    }

    private void addBlockJapanseUnSupport(ArrayList<UnicodeDef> codeList) {
        // Halfwidth katakana
        UnicodeDef katakana = new UnicodeDef();
        katakana.low = 0xFF65;
        katakana.high = 0xFF9F;
        codeList.add(katakana);
    }

    private void addBlockKorean(ArrayList<UnicodeBlock> codeList) {
        // U+1100..U+11FF
        codeList.add(UnicodeBlock.HANGUL_JAMO);
        // U+3130..U+318F
        codeList.add(UnicodeBlock.HANGUL_COMPATIBILITY_JAMO);
        // U+AC00..U+D7AF
        codeList.add(UnicodeBlock.HANGUL_SYLLABLES);
    }

    private void addBlockChinese(ArrayList<UnicodeBlock> codeList) {
        // U+2E80..U+2EFF
        codeList.add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
        // U+2F00..U+2FDF
        codeList.add(UnicodeBlock.KANGXI_RADICALS);
        // U+3100..U+312F
        codeList.add(UnicodeBlock.BOPOMOFO);
        // U+3400..U+4DBF
        codeList.add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
        // U+4E00..U+9FFF
        codeList.add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        // U+F900..U+FAFF
        codeList.add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
    }

    private void addBlockMyanmar(ArrayList<UnicodeBlock> codeList) {
        // U+1000..U+109F
        codeList.add(UnicodeBlock.MYANMAR);
    }

    private void addBlockThai(ArrayList<UnicodeBlock> codeList) {
        // U+0E00..U+0E7F
        codeList.add(UnicodeBlock.THAI);
    }

    private void addBlockHindi(ArrayList<UnicodeBlock> codeList) {
        // U+0900..U+097F
        codeList.add(UnicodeBlock.DEVANAGARI);
    }

    private void addBlockHindiUnSupport(ArrayList<UnicodeDef> codeList) {
        // U+A8E0..U+A8FF Devanagari Extended
        UnicodeDef extended = new UnicodeDef();
        extended.low = 0xA8E0;
        extended.high = 0xA8FF;
        codeList.add(extended);
    }

    private Map<String, UnicodeProvider> generateMap() {
        Map<String, UnicodeProvider> map = new HashMap<String, UnicodeProvider>();
        // ar:Arabic
        map.put("ar", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockArabic(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockArabicUnSupport(codeList);
                return codeList;
            }
        });
        // bg:Bulgarian
        map.put("bg", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockCyrillic(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // ca:Catalan
        map.put("ca", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // cs:Czech
        map.put("cs", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // da:Danish
        map.put("da", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // de:German
        map.put("de", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // el:Greek
        map.put("el", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockGreek(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockGreekUnSupport(codeList);
                return codeList;
            }
        });
        // es:Spanish
        map.put("es", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // et:Estonian
        map.put("et", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // fa:Persian
        map.put("fa", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockArabic(codeList);
                addBlockCyrillic(codeList);
                addBlockHebrew(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockArabicUnSupport(codeList);
                addBlockHebrewUnSupport(codeList);
                return codeList;
            }
        });
        // fi:Finnish
        map.put("fi", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // fr:French
        map.put("fr", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });

        map.put("hi", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockHindi(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockHindiUnSupport(codeList);
                return codeList;
            }
        });

        // hr:Standard Croatian
        map.put("hr", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // hu:Hungarian
        map.put("hu", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // hy:Armenian
        map.put("hy", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockArmenian(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockArmenianUnSupport(codeList);
                return codeList;
            }
        });
        // in:Indonesian
        map.put("in", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // it:Italian
        map.put("it", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // iw:Hebrew
        map.put("iw", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockHebrew(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockHebrewUnSupport(codeList);
                return codeList;
            }
        });
        // ja:Japanse (Chinese included)
        map.put("ja", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockJapanse(codeList);
                addBlockChinese(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockJapanseUnSupport(codeList);
                return codeList;
            }
        });
        // kk:KazaKh
        map.put("kk", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockCyrillic(codeList);
                addBlockLatin(codeList);
                addBlockArabic(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                addBlockArabicUnSupport(codeList);
                return codeList;
            }
        });
        // ko:Korean
        map.put("ko", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockKorean(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // lt:Lithuanian
        map.put("lt", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // lv:Latvian
        map.put("lv", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });

        // TODO ms:Malay
        map.put("ms", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });

        // my:Burmese
        map.put("my", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockMyanmar(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // nb:Norwegian
        map.put("nb", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // nl:Nederlands
        map.put("nl", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // pl:Polish
        map.put("pl", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // pt:Portuguese
        map.put("pt", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // ro:Romanian
        map.put("ro", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // ru:Russian
        map.put("ru", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockCyrillic(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // sk:Slovak
        map.put("sk", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // sl:Republic of Slovenia
        map.put("sl", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // sr:Standard Serbian
        map.put("sr", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // sv:Swedish
        map.put("sv", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // th:Thai
        map.put("th", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockThai(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // tr:Turkish
        map.put("tr", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // uk:Ukrainian
        map.put("uk", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockCyrillic(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        // vi:Vietnamese
        map.put("vi", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockLatin(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                ArrayList<UnicodeDef> codeList = new ArrayList<UnicodeDef>();
                addBlockLatinUnSupport(codeList);
                return codeList;
            }
        });
        // zh:Chinese
        map.put("zh", new UnicodeProvider() {
            @Override
            public ArrayList<UnicodeBlock> getBlockList() {
                ArrayList<UnicodeBlock> codeList = new ArrayList<UnicodeBlock>();
                addBlockChinese(codeList);
                return codeList;
            }

            @Override
            public ArrayList<UnicodeDef> getDefList() {
                return null;
            }
        });
        return map;
    }
}
