/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.htc.lib1.exo.extractor.mp4;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.extractor.mp4.Atom;
import com.google.android.exoplayer.extractor.mp4.AtomParsers;
import com.google.android.exoplayer.extractor.mp4.AtomParsers.StsdDataHolder;
import com.google.android.exoplayer.extractor.mp4.Track;
import com.google.android.exoplayer.util.ParsableByteArray;
import com.google.android.exoplayer.util.Util;

import android.util.Pair;


/** Utility methods for parsing MP4 format atom payloads according to ISO 14496-12. */
public final class HtcAtomParsers{

  /**
   * Parses a trak atom (defined in 14496-12).
   *
   * @param trak Atom to parse.
   * @param mvhd Movie header atom, used to get the timescale.
   * @return A {@link Track} instance, or {@code null} if the track's type isn't supported.
   */
  public static Track parseTrak(Atom.ContainerAtom trak, Atom.LeafAtom mvhd) {
    Atom.ContainerAtom mdia = trak.getContainerAtomOfType(Atom.TYPE_mdia);
    int trackType = AtomParsers.parseHdlr(mdia.getLeafAtomOfType(Atom.TYPE_hdlr).data);
    if (trackType != Track.TYPE_AUDIO && trackType != Track.TYPE_VIDEO
        && trackType != Track.TYPE_TEXT && trackType != Track.TYPE_TIME_CODE) {
      return null;
    }

    //Pair<Integer, Long> header = parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
    //[htc]Add by OOD, for slow motion mp4, start
    int videoRotateDegree = 0;
    Pair<Integer, Long> header;
    if (trackType == Track.TYPE_VIDEO)
    {
        header = AtomParsers.parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        Pair<Integer, Integer> header2;
        header2 = parseTkhdWithVideoRotate(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
        videoRotateDegree = (header2.second);
    } else {
        header = AtomParsers.parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
	if (header.first < 0 ) {
		header = AtomParsers.parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_htka).data);
	}
  
    }
    //[htc]Add by OOD, for slow motion mp4, end
    int id = header.first;
    long duration = header.second;
    long movieTimescale = AtomParsers.parseMvhd(mvhd.data);
    long durationUs;
    if (duration == -1) {
      durationUs = C.UNKNOWN_TIME_US;
    } else {
      durationUs = Util.scaleLargeTimestamp(duration, C.MICROS_PER_SECOND, movieTimescale);
    }
    Atom.ContainerAtom stbl = mdia.getContainerAtomOfType(Atom.TYPE_minf)
        .getContainerAtomOfType(Atom.TYPE_stbl);

    long mediaTimescale = AtomParsers.parseMdhd(mdia.getLeafAtomOfType(Atom.TYPE_mdhd).data);
    StsdDataHolder stsdData = AtomParsers.parseStsd(stbl.getLeafAtomOfType(Atom.TYPE_stsd).data, durationUs);
    //[htc]Add by OOD, for slow motion mp4, start
    if (trackType == Track.TYPE_VIDEO)
    {
        stsdData.mediaFormat.rotateDegree = (int) videoRotateDegree;
    }
    //[htc]Add by OOD, for slow motion mp4, end
    return new Track(id, trackType, mediaTimescale, durationUs, stsdData.mediaFormat,
        stsdData.trackEncryptionBoxes, stsdData.nalUnitLengthFieldLength);
  }

  //[htc]Add by OOD, for slow motion mp4, start
  /*
   *   parse geometry matrix in tkhd header.
   *
   *   * 8+ bytes track header atom = long unsigned offset + long ASCII text string 'tkhd'
   *     -> 1 bytes version = byte hex version (current = 0)
   *     -> 3 bytes flags = 24-bit unsigned flags
   *       - sum of TrackEnabled = 1; TrackInMovie = 2; TrackInPreview = 4; TrackInPoster = 8
   *     -> 4 bytes created mac date = long unsigned value in seconds since beginning 1904 to 2040
   *     -> 4 bytes modified mac date = long unsigned value in seconds since beginning 1904 to 2040
   *     -> 4 bytes track id = long integer value (first track = 1)
   *     -> 8 bytes reserved = 2 * long value set to zero
   *     -> 4 bytes duration = long unsigned time length (in time units)
   *     -> 8 bytes reserved = 2 * long value set to zero
   *     -> 2 bytes video layer = short integer positon (middle = 0; negatives are in front)
   *     -> 2 bytes alternate/other = short integer track id (none = 0)
   *     -> 2 bytes track audio volume = short unsigned level (mute = 1; 100% = 256; 200% max = 512)
   *     -> 2 bytes reserved = short value set to zero
   *     -> 4 bytes decimal video geometry matrix value A = long fixed point width scale (normal = 1.0)
   *     -> 4 bytes decimal video geometry matrix value B = long fixed point width rotate (normal = 0.0)
   *     -> 4 bytes decimal video geometry matrix value U = long fixed point width angle (normal = 0.0)
   *     -> 4 bytes decimal video geometry matrix value C = long fixed point height rotate (normal = 0.0)
   *     -> 4 bytes decimal video geometry matrix value D = long fixed point height scale (normal = 1.0)
   *     -> 4 bytes decimal video geometry matrix value V = long fixed point height angle (normal = 0.0)
   *     -> 4 bytes decimal video geometry matrix value X = long fixed point positon (left = 0.0)
   *     -> 4 bytes decimal video geometry matrix value Y = long fixed point positon (top = 0.0)
   *     -> 4 bytes decimal video geometry matrix value W = long fixed point divider scale (normal = 1.0)
   *     -> 8 bytes decimal video frame size = long fixed point width + long fixed point height
   */
  public static Pair<Integer, Integer> parseTkhdWithVideoRotate(ParsableByteArray tkhd) {
    Pair<Integer, Long> header = AtomParsers.parseTkhd(tkhd);
    int position = tkhd.getPosition();
    int degree = 0;
    //parse rotation
    {
        tkhd.skipBytes(4); //reserve
        tkhd.skipBytes(4); //reserve
        tkhd.skipBytes(2); //layer
        tkhd.skipBytes(2); //alternate group
        tkhd.skipBytes(2); //volume
        tkhd.skipBytes(2); //reserve
        int a  = tkhd.readInt();
        int b  = tkhd.readInt();
        int u  = tkhd.readInt();
        int c  = tkhd.readInt();
        int d  = tkhd.readInt();
        int x  = tkhd.readInt();
        int y  = tkhd.readInt();
        int w  = tkhd.readInt();
/*
        LOG.I(TAG, "parseTkhdWithVideo a " + getAtomTypeString(a));
        LOG.I(TAG, "parseTkhdWithVideo b " + getAtomTypeString(b));
        LOG.I(TAG, "parseTkhdWithVideo u " + getAtomTypeString(u));
        LOG.I(TAG, "parseTkhdWithVideo c " + getAtomTypeString(c));
        LOG.I(TAG, "parseTkhdWithVideo d " + getAtomTypeString(d));
        LOG.I(TAG, "parseTkhdWithVideo x " + getAtomTypeString(x));
        LOG.I(TAG, "parseTkhdWithVideo y " + getAtomTypeString(y));
        LOG.I(TAG, "parseTkhdWithVideo w " + getAtomTypeString(w));
*/
        if (a == 0x00010000 && b == 0x00000000 && c == 0x00000000 && d == 0x00010000)
        {
            degree = 0;
        }
        else if (a == 0x00000000 && b == 0x00010000 && c == 0xFFFF0000 && d == 0x00000000)
        {
            degree = 90;
        }
        else if (a == 0xFFFF0000 && b == 0x00000000 && c == 0x00000000 && d == 0xFFFF0000)
        {
            degree = 180;
        }
        else if (a == 0x00000000 && b == 0xFFFF0000 && c == 0x00010000 && d == 0x00000000)
        {
            degree = 270;
        }
        tkhd.setPosition(position);
    }
    return Pair.create(header.first, degree);
  }
  private static String getAtomTypeString(int type) {
	return Integer.toHexString(type);
  }
  //[htc]Add by OOD, for slow motion mp4, end
}
