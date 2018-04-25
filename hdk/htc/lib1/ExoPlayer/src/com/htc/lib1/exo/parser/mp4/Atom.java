package com.htc.lib1.exo.parser.mp4;

import com.google.android.exoplayer.util.Assertions;

/* package */ abstract class Atom {
  public static final int TYPE_ftyp = getAtomTypeInteger("ftyp");
  public static final int TYPE_moov = getAtomTypeInteger("moov");
  public static final int TYPE_udta = getAtomTypeInteger("udta");
  public static final int TYPE_htcb = getAtomTypeInteger("htcb"); // HTC Defined,
  public static final int TYPE_dtah = getAtomTypeInteger("dtah"); // HTC Defined,
  public static final int TYPE_slmt = getAtomTypeInteger("slmt"); // HTC Defined, Slow motion
  public static final int TYPE_htka = getAtomTypeInteger("htka"); // HTC Defined, Slow motion audio track
  public static final int TYPE__htc = getAtomTypeInteger("_htc"); // HTC Defined, old Slow motion

  private static int getAtomTypeInteger(String typeName) {
      Assertions.checkArgument(typeName.length() == 4);
      int result = 0;
      for (int i = 0; i < 4; i++) {
        result <<= 8;
        result |= typeName.charAt(i);
      }
      return result;
  }
}
