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
package com.google.android.exoplayer.extractor.webm;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.ParserException;
import com.google.android.exoplayer.drm.DrmInitData;
import com.google.android.exoplayer.extractor.ChunkIndex;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorInput;
import com.google.android.exoplayer.extractor.ExtractorOutput;
import com.google.android.exoplayer.extractor.PositionHolder;
import com.google.android.exoplayer.extractor.TrackOutput;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.LongArray;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.NalUnitUtil;
import com.google.android.exoplayer.util.ParsableByteArray;
import com.htc.lib1.exo.utilities.LOG;

import android.util.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An extractor to facilitate data retrieval from the WebM container format.
 * <p>
 * WebM is a subset of the EBML elements defined for Matroska. More information about EBML and
 * Matroska is available <a href="http://www.matroska.org/technical/specs/index.html">here</a>.
 * More info about WebM is <a href="http://www.webmproject.org/code/specs/container/">here</a>.
 * RFC on encrypted WebM can be found
 * <a href="http://wiki.webmproject.org/encryption/webm-encryption-rfc">here</a>.
 */
public final class WebmExtractor implements Extractor {
  String TAG = "WebmExtractor";
  private static final int SAMPLE_STATE_START = 0;
  private static final int SAMPLE_STATE_HEADER = 1;
  private static final int SAMPLE_STATE_DATA = 2;

  private static final int CUES_STATE_NOT_BUILT = 0;
  private static final int CUES_STATE_BUILDING = 1;
  private static final int CUES_STATE_BUILT = 2;

  private static final String DOC_TYPE_WEBM = "webm";
  private static final String DOC_TYPE_MATROSKA = "matroska";
  private static final String CODEC_ID_VP8 = "V_VP8";
  private static final String CODEC_ID_VP9 = "V_VP9";
  private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
  private static final String CODEC_ID_VORBIS = "A_VORBIS";
  private static final String CODEC_ID_OPUS = "A_OPUS";
  private static final String CODEC_ID_AAC = "A_AAC";
  private static final String CODEC_ID_AC3 = "A_AC3";
  private static final int VORBIS_MAX_INPUT_SIZE = 8192;
  private static final int OPUS_MAX_INPUT_SIZE = 5760;
  private static final int ENCRYPTION_IV_SIZE = 8;
  private static final int TRACK_TYPE_AUDIO = 2;
  private static final int TRACK_TYPE_VIDEO = 1;
  private static final int UNKNOWN = -1;

  private static final int ID_EBML = 0x1A45DFA3;
  private static final int ID_EBML_READ_VERSION = 0x42F7;
  private static final int ID_DOC_TYPE = 0x4282;
  private static final int ID_DOC_TYPE_READ_VERSION = 0x4285;
  private static final int ID_SEGMENT = 0x18538067;
  private static final int ID_SEEK_HEAD = 0x114D9B74;
  private static final int ID_SEEK = 0x4DBB;
  private static final int ID_SEEK_ID = 0x53AB;
  private static final int ID_SEEK_POSITION = 0x53AC;
  private static final int ID_INFO = 0x1549A966;
  private static final int ID_TIMECODE_SCALE = 0x2AD7B1;
  private static final int ID_DURATION = 0x4489;
  private static final int ID_CLUSTER = 0x1F43B675;
  private static final int ID_TIME_CODE = 0xE7;
  private static final int ID_SIMPLE_BLOCK = 0xA3;
  private static final int ID_BLOCK_GROUP = 0xA0;
  private static final int ID_BLOCK = 0xA1;
  private static final int ID_REFERENCE_BLOCK = 0xFB;
  private static final int ID_TRACKS = 0x1654AE6B;
  private static final int ID_TRACK_ENTRY = 0xAE;
  private static final int ID_TRACK_NUMBER = 0xD7;
  private static final int ID_TRACK_TYPE = 0x83;
  private static final int ID_CODEC_ID = 0x86;
  private static final int ID_CODEC_PRIVATE = 0x63A2;
  private static final int ID_CODEC_DELAY = 0x56AA;
  private static final int ID_SEEK_PRE_ROLL = 0x56BB;
  private static final int ID_VIDEO = 0xE0;
  private static final int ID_PIXEL_WIDTH = 0xB0;
  private static final int ID_PIXEL_HEIGHT = 0xBA;
  private static final int ID_AUDIO = 0xE1;
  private static final int ID_CHANNELS = 0x9F;
  private static final int ID_SAMPLING_FREQUENCY = 0xB5;
  private static final int ID_CONTENT_ENCODINGS = 0x6D80;
  private static final int ID_CONTENT_ENCODING = 0x6240;
  private static final int ID_CONTENT_ENCODING_ORDER = 0x5031;
  private static final int ID_CONTENT_ENCODING_SCOPE = 0x5032;
  private static final int ID_CONTENT_ENCODING_TYPE = 0x5033;
  private static final int ID_CONTENT_ENCRYPTION = 0x5035;
  private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 0x47E1;
  private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 0x47E2;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 0x47E7;
  private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 0x47E8;
  private static final int ID_CUES = 0x1C53BB6B;
  private static final int ID_CUE_POINT = 0xBB;
  private static final int ID_CUE_TIME = 0xB3;
  private static final int ID_CUE_TRACK_POSITIONS = 0xB7;
  private static final int ID_CUE_CLUSTER_POSITION = 0xF1;

  private static final int LACING_NONE = 0;

  private final EbmlReader reader;
  private final VarintReader varintReader;

  // Temporary arrays.
  private final ParsableByteArray nalStartCode;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray sampleHeaderScratch;
  private final ParsableByteArray vorbisNumPageSamples;
  private final ParsableByteArray seekEntryIdBytes;

  private long segmentContentPosition = UNKNOWN;
  private long segmentContentSize = UNKNOWN;
  private long timecodeScale = 1000000L;
  private long durationUs = C.UNKNOWN_TIME_US;

  private TrackFormat trackFormat;  // Used to store the last seen track.
  private TrackFormat audioTrackFormat;
  private TrackFormat videoTrackFormat;

  private boolean sentDrmInitData;

  // Master seek entry related elements.
  private int seekEntryId;
  private long seekEntryPosition;

  // Cue related elements.
  private boolean seekForCues;
  private long cuesContentPosition = UNKNOWN;
  private long seekPositionAfterBuildingCues = UNKNOWN;
  private int cuesState = CUES_STATE_NOT_BUILT;
  private long clusterTimecodeUs = UNKNOWN;
  private LongArray cueTimesUs;
  private LongArray cueClusterPositions;
  private boolean seenClusterPositionForCurrentCuePoint;

  // Sample reading state.
  private int blockBytesRead;
  private int sampleState;
  private int sampleSize;
  private int sampleCurrentNalBytesRemaining;
  private int sampleTrackNumber;
  private int sampleFlags;
  private long sampleTimeUs;
  private boolean sampleRead;
  private boolean sampleSeenReferenceBlock;

  // Extractor outputs.
  private ExtractorOutput extractorOutput;

  public WebmExtractor() {
    this(new DefaultEbmlReader());
  }

  /* package */ WebmExtractor(EbmlReader reader) {
    this.reader = reader;
    this.reader.init(new InnerEbmlReaderOutput());
    varintReader = new VarintReader();
    sampleHeaderScratch = new ParsableByteArray(4);
    vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(-1).array());
    seekEntryIdBytes = new ParsableByteArray(4);
    nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    nalLength = new ParsableByteArray(4);
  }

  @Override
  public void init(ExtractorOutput output) {
    extractorOutput = output;
  }

  @Override
  public void seek() {
    clusterTimecodeUs = UNKNOWN;
    sampleState = SAMPLE_STATE_START;
    reader.reset();
    varintReader.reset();
    sampleCurrentNalBytesRemaining = 0;
  }

  @Override
  public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException,
      InterruptedException {
    sampleRead = false;
    boolean continueReading = true;
    while (continueReading && !sampleRead) {
      continueReading = reader.read(input);
      if (continueReading && maybeSeekForCues(seekPosition, input.getPosition())) {
        return Extractor.RESULT_SEEK;
      }
    }
    return continueReading ? Extractor.RESULT_CONTINUE : Extractor.RESULT_END_OF_INPUT;
  }

  /* package */ int getElementType(int id) {
    switch (id) {
      case ID_EBML:
      case ID_SEGMENT:
      case ID_SEEK_HEAD:
      case ID_SEEK:
      case ID_INFO:
      case ID_CLUSTER:
      case ID_TRACKS:
      case ID_TRACK_ENTRY:
      case ID_AUDIO:
      case ID_VIDEO:
      case ID_CONTENT_ENCODINGS:
      case ID_CONTENT_ENCODING:
      case ID_CONTENT_ENCRYPTION:
      case ID_CONTENT_ENCRYPTION_AES_SETTINGS:
      case ID_CUES:
      case ID_CUE_POINT:
      case ID_CUE_TRACK_POSITIONS:
      case ID_BLOCK_GROUP:
        return EbmlReader.TYPE_MASTER;
      case ID_EBML_READ_VERSION:
      case ID_DOC_TYPE_READ_VERSION:
      case ID_SEEK_POSITION:
      case ID_TIMECODE_SCALE:
      case ID_TIME_CODE:
      case ID_PIXEL_WIDTH:
      case ID_PIXEL_HEIGHT:
      case ID_TRACK_NUMBER:
      case ID_TRACK_TYPE:
      case ID_CODEC_DELAY:
      case ID_SEEK_PRE_ROLL:
      case ID_CHANNELS:
      case ID_CONTENT_ENCODING_ORDER:
      case ID_CONTENT_ENCODING_SCOPE:
      case ID_CONTENT_ENCODING_TYPE:
      case ID_CONTENT_ENCRYPTION_ALGORITHM:
      case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE:
      case ID_CUE_TIME:
      case ID_CUE_CLUSTER_POSITION:
      case ID_REFERENCE_BLOCK:
        return EbmlReader.TYPE_UNSIGNED_INT;
      case ID_DOC_TYPE:
      case ID_CODEC_ID:
        return EbmlReader.TYPE_STRING;
      case ID_SEEK_ID:
      case ID_CONTENT_ENCRYPTION_KEY_ID:
      case ID_SIMPLE_BLOCK:
      case ID_BLOCK:
      case ID_CODEC_PRIVATE:
        return EbmlReader.TYPE_BINARY;
      case ID_DURATION:
      case ID_SAMPLING_FREQUENCY:
        return EbmlReader.TYPE_FLOAT;
      default:
        return EbmlReader.TYPE_UNKNOWN;
    }
  }

  /* package */ void startMasterElement(int id, long contentPosition, long contentSize)
      throws ParserException {
    switch (id) {
      case ID_SEGMENT:
        if (segmentContentPosition != UNKNOWN) {
          throw new ParserException("Multiple Segment elements not supported");
        }
        segmentContentPosition = contentPosition;
        segmentContentSize = contentSize;
        return;
      case ID_SEEK:
        seekEntryId = UNKNOWN;
        seekEntryPosition = UNKNOWN;
        return;
      case ID_CUES:
        cueTimesUs = new LongArray();
        cueClusterPositions = new LongArray();
        return;
      case ID_CUE_POINT:
        seenClusterPositionForCurrentCuePoint = false;
        return;
      case ID_CLUSTER:
        // If we encounter a Cluster before building Cues, then we should try to build cues first
        // before parsing the Cluster.
        if (cuesState == CUES_STATE_NOT_BUILT && cuesContentPosition != UNKNOWN) {
          seekForCues = true;
        }
        return;
      case ID_BLOCK_GROUP:
        sampleSeenReferenceBlock = false;
        return;
      case ID_CONTENT_ENCODING:
        // TODO: check and fail if more than one content encoding is present.
        return;
      case ID_CONTENT_ENCRYPTION:
        trackFormat.hasContentEncryption = true;
        return;
      case ID_TRACK_ENTRY:
        trackFormat = new TrackFormat();
        return;
      default:
        return;
    }
  }

  /* package */ void endMasterElement(int id) throws ParserException {
    switch (id) {
      case ID_SEEK:
        if (seekEntryId == UNKNOWN || seekEntryPosition == UNKNOWN) {
          throw new ParserException("Mandatory element SeekID or SeekPosition not found");
        }
        if (seekEntryId == ID_CUES) {
          cuesContentPosition = seekEntryPosition;
        }
        return;
      case ID_CUES:
        if (cuesState != CUES_STATE_BUILT) {
          extractorOutput.seekMap(buildCues());
          cuesState = CUES_STATE_BUILT;
        } else {
          // We have already built the cues. Ignore.
        }
        return;
      case ID_BLOCK_GROUP:
        if (sampleState != SAMPLE_STATE_DATA) {
          // We've skipped this sample (due to incompatible track number).
          return;
        }
        // If the ReferenceBlock element was not found for this sample, then it is a keyframe.
        if (!sampleSeenReferenceBlock) {
          sampleFlags |= C.SAMPLE_FLAG_SYNC;
        }
        outputSampleMetadata(
            (audioTrackFormat != null && sampleTrackNumber == audioTrackFormat.number)
                ? audioTrackFormat.trackOutput : videoTrackFormat.trackOutput);
        return;
      case ID_CONTENT_ENCODING:
        if (!trackFormat.hasContentEncryption) {
          // We found a ContentEncoding other than Encryption.
          throw new ParserException("Found an unsupported ContentEncoding");
        }
        if (trackFormat.encryptionKeyId == null) {
          throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
        }
        if (!sentDrmInitData) {
          extractorOutput.drmInitData(
              new DrmInitData.Universal(MimeTypes.VIDEO_WEBM, trackFormat.encryptionKeyId));
          sentDrmInitData = true;
        }
        return;
      case ID_TRACK_ENTRY:
        if (trackFormat.number == UNKNOWN || trackFormat.type == UNKNOWN) {
          throw new ParserException("Mandatory element TrackNumber or TrackType not found");
        }
        if ((trackFormat.type == TRACK_TYPE_AUDIO && audioTrackFormat != null)
            || (trackFormat.type == TRACK_TYPE_VIDEO && videoTrackFormat != null)) {
          // There is more than 1 audio/video track. Ignore everything but the first.
          trackFormat = null;
          return;
        }
        if (trackFormat.type == TRACK_TYPE_AUDIO && isCodecSupported(trackFormat.codecId)) {
          audioTrackFormat = trackFormat;
          audioTrackFormat.trackOutput = extractorOutput.track(audioTrackFormat.number);
          audioTrackFormat.trackOutput.format(audioTrackFormat.getMediaFormat(durationUs));
        } else if (trackFormat.type == TRACK_TYPE_VIDEO && isCodecSupported(trackFormat.codecId)) {
          videoTrackFormat = trackFormat;
          videoTrackFormat.trackOutput = extractorOutput.track(videoTrackFormat.number);
          videoTrackFormat.trackOutput.format(videoTrackFormat.getMediaFormat(durationUs));
        } else {
          // Unsupported track type. Do nothing.
        }
        trackFormat = null;
        return;
      case ID_TRACKS:
        if (videoTrackFormat == null && audioTrackFormat == null) {
          throw new ParserException("No valid tracks were found");
        }
        extractorOutput.endTracks();
        return;
      default:
        return;
    }
  }

  /* package */ void integerElement(int id, long value) throws ParserException {
    switch (id) {
      case ID_EBML_READ_VERSION:
        // Validate that EBMLReadVersion is supported. This extractor only supports v1.
        if (value != 1) {
          throw new ParserException("EBMLReadVersion " + value + " not supported");
        }
        return;
      case ID_DOC_TYPE_READ_VERSION:
        // Validate that DocTypeReadVersion is supported. This extractor only supports up to v2.
        if (value < 1 || value > 2) {
          throw new ParserException("DocTypeReadVersion " + value + " not supported");
        }
        return;
      case ID_SEEK_POSITION:
        // Seek Position is the relative offset beginning from the Segment. So to get absolute
        // offset from the beginning of the file, we need to add segmentContentPosition to it.
        seekEntryPosition = value + segmentContentPosition;
        return;
      case ID_TIMECODE_SCALE:
        timecodeScale = value;
        return;
      case ID_PIXEL_WIDTH:
        trackFormat.pixelWidth = (int) value;
        return;
      case ID_PIXEL_HEIGHT:
        trackFormat.pixelHeight = (int) value;
        return;
      case ID_TRACK_NUMBER:
        trackFormat.number = (int) value;
        return;
      case ID_TRACK_TYPE:
        trackFormat.type = (int) value;
        return;
      case ID_CODEC_DELAY:
        trackFormat.codecDelayNs = value;
        return;
      case ID_SEEK_PRE_ROLL:
        trackFormat.seekPreRollNs = value;
        return;
      case ID_CHANNELS:
        trackFormat.channelCount = (int) value;
        return;
      case ID_REFERENCE_BLOCK:
        sampleSeenReferenceBlock = true;
        return;
      case ID_CONTENT_ENCODING_ORDER:
        // This extractor only supports one ContentEncoding element and hence the order has to be 0.
        if (value != 0) {
          throw new ParserException("ContentEncodingOrder " + value + " not supported");
        }
        return;
      case ID_CONTENT_ENCODING_SCOPE:
        // This extractor only supports the scope of all frames (since that's the only scope used
        // for Encryption).
        if (value != 1) {
          throw new ParserException("ContentEncodingScope " + value + " not supported");
        }
        return;
      case ID_CONTENT_ENCODING_TYPE:
        // This extractor only supports Encrypted ContentEncodingType.
        if (value != 1) {
          throw new ParserException("ContentEncodingType " + value + " not supported");
        }
        return;
      case ID_CONTENT_ENCRYPTION_ALGORITHM:
        // Only the value 5 (AES) is allowed according to the WebM specification.
        if (value != 5) {
          throw new ParserException("ContentEncAlgo " + value + " not supported");
        }
        return;
      case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE:
        // Only the value 1 is allowed according to the WebM specification.
        if (value != 1) {
          throw new ParserException("AESSettingsCipherMode " + value + " not supported");
        }
        return;
      case ID_CUE_TIME:
        cueTimesUs.add(scaleTimecodeToUs(value));
        return;
      case ID_CUE_CLUSTER_POSITION:
        if (!seenClusterPositionForCurrentCuePoint) {
          // If there's more than one video/audio track, then there could be more than one
          // CueTrackPositions within a single CuePoint. In such a case, ignore all but the first
          // one (since the cluster position will be quite close for all the tracks).
          cueClusterPositions.add(value);
          seenClusterPositionForCurrentCuePoint = true;
        }
        return;
      case ID_TIME_CODE:
        clusterTimecodeUs = scaleTimecodeToUs(value);
        return;
      default:
        return;
    }
  }

  /* package */ void floatElement(int id, double value) {
    switch (id) {
      case ID_DURATION:
        durationUs = scaleTimecodeToUs((long) value);
        return;
      case ID_SAMPLING_FREQUENCY:
        trackFormat.sampleRate = (int) value;
        return;
      default:
        return;
    }
  }

  /* package */ void stringElement(int id, String value) throws ParserException {
    switch (id) {
      case ID_DOC_TYPE:
        // Validate that DocType is supported.
        if (!DOC_TYPE_WEBM.equals(value) && !DOC_TYPE_MATROSKA.equals(value)) {
          throw new ParserException("DocType " + value + " not supported");
        }
        return;
      case ID_CODEC_ID:
        trackFormat.codecId = value;
        return;
      default:
        return;
    }
  }

  /* package */ void binaryElement(int id, int contentSize, ExtractorInput input)
      throws IOException, InterruptedException {
    switch (id) {
      case ID_SEEK_ID:
        Arrays.fill(seekEntryIdBytes.data, (byte) 0);
        input.readFully(seekEntryIdBytes.data, 4 - contentSize, contentSize);
        seekEntryIdBytes.setPosition(0);
        seekEntryId = (int) seekEntryIdBytes.readUnsignedInt();
        return;
      case ID_CODEC_PRIVATE:
        trackFormat.codecPrivate = new byte[contentSize];
        input.readFully(trackFormat.codecPrivate, 0, contentSize);
        return;
      case ID_CONTENT_ENCRYPTION_KEY_ID:
        trackFormat.encryptionKeyId = new byte[contentSize];
        input.readFully(trackFormat.encryptionKeyId, 0, contentSize);
        return;
      case ID_SIMPLE_BLOCK:
      case ID_BLOCK:
        // Please refer to http://www.matroska.org/technical/specs/index.html#simpleblock_structure
        // and http://matroska.org/technical/specs/index.html#block_structure
        // for info about how data is organized in SimpleBlock and Block elements respectively. They
        // differ only in the way flags are specified.

        if (sampleState == SAMPLE_STATE_START) {
          sampleTrackNumber = (int) varintReader.readUnsignedVarint(input, false, true);
          blockBytesRead = varintReader.getLastLength();
          sampleState = SAMPLE_STATE_HEADER;
        }

        // Ignore the frame if the track number equals neither the audio track nor the video track.
        if ((audioTrackFormat != null && videoTrackFormat != null
                && audioTrackFormat.number != sampleTrackNumber
                && videoTrackFormat.number != sampleTrackNumber)
            || (audioTrackFormat != null && videoTrackFormat == null
                && audioTrackFormat.number != sampleTrackNumber)
            || (audioTrackFormat == null && videoTrackFormat != null
                && videoTrackFormat.number != sampleTrackNumber)) {
          input.skipFully(contentSize - blockBytesRead);
          sampleState = SAMPLE_STATE_START;
          return;
        }

        TrackFormat sampleTrackFormat =
            (audioTrackFormat != null && sampleTrackNumber == audioTrackFormat.number)
                ? audioTrackFormat : videoTrackFormat;
        TrackOutput trackOutput = sampleTrackFormat.trackOutput;

        if (sampleState == SAMPLE_STATE_HEADER) {
          byte[] sampleHeaderScratchData = sampleHeaderScratch.data;
          // Next 3 bytes have timecode and flags. If encrypted, the 4th byte is a signal byte.
          int remainingHeaderLength = sampleTrackFormat.hasContentEncryption ? 4 : 3;
          input.readFully(sampleHeaderScratchData, 0, remainingHeaderLength);
          blockBytesRead += remainingHeaderLength;

          // First two bytes are the relative timecode.
          int timecode = (sampleHeaderScratchData[0] << 8)
              | (sampleHeaderScratchData[1] & 0xFF);
          sampleTimeUs = clusterTimecodeUs + scaleTimecodeToUs(timecode);

          // Third byte contains the lacing value and some flags.
          int lacing = (sampleHeaderScratchData[2] & 0x06) >> 1;
          if (lacing != LACING_NONE) {
            throw new ParserException("Lacing mode not supported: " + lacing);
          }
          boolean isInvisible = (sampleHeaderScratchData[2] & 0x08) == 0x08;
          boolean isKeyframe =
              (id == ID_SIMPLE_BLOCK && (sampleHeaderScratchData[2] & 0x80) == 0x80);
          boolean isEncrypted = false;

          // If encrypted, the fourth byte is an encryption signal byte.
          if (sampleTrackFormat.hasContentEncryption) {
            if ((sampleHeaderScratchData[3] & 0x80) == 0x80) {
              throw new ParserException("Extension bit is set in signal byte");
            }
            isEncrypted = (sampleHeaderScratchData[3] & 0x01) == 0x01;
          }

          sampleFlags = (isKeyframe ? C.SAMPLE_FLAG_SYNC : 0)
              | (isInvisible ? C.SAMPLE_FLAG_DECODE_ONLY : 0)
              | (isEncrypted ? C.SAMPLE_FLAG_ENCRYPTED : 0);
          sampleSize = contentSize - blockBytesRead;
          if (isEncrypted) {
            // Write the vector size.
            sampleHeaderScratch.data[0] = (byte) ENCRYPTION_IV_SIZE;
            sampleHeaderScratch.setPosition(0);
            trackOutput.sampleData(sampleHeaderScratch, 1);
            sampleSize++;
          }
          sampleState = SAMPLE_STATE_DATA;
        }

        if (CODEC_ID_H264.equals(sampleTrackFormat.codecId)) {
          // TODO: Deduplicate with Mp4Extractor.

          // Zero the top three bytes of the array that we'll use to parse nal unit lengths, in case
          // they're only 1 or 2 bytes long.
          byte[] nalLengthData = nalLength.data;
          nalLengthData[0] = 0;
          nalLengthData[1] = 0;
          nalLengthData[2] = 0;
          int nalUnitLengthFieldLength = sampleTrackFormat.nalUnitLengthFieldLength;
          int nalUnitLengthFieldLengthDiff = 4 - sampleTrackFormat.nalUnitLengthFieldLength;
          // NAL units are length delimited, but the decoder requires start code delimited units.
          // Loop until we've written the sample to the track output, replacing length delimiters
          // with start codes as we encounter them.
          while (blockBytesRead < contentSize) {
            if (sampleCurrentNalBytesRemaining == 0) {
              // Read the NAL length so that we know where we find the next one.
              input.readFully(nalLengthData, nalUnitLengthFieldLengthDiff,
                  nalUnitLengthFieldLength);
              blockBytesRead += nalUnitLengthFieldLength;
              nalLength.setPosition(0);
              sampleCurrentNalBytesRemaining = nalLength.readUnsignedIntToInt();
              // Write a start code for the current NAL unit.
              nalStartCode.setPosition(0);
              trackOutput.sampleData(nalStartCode, 4);
            } else {
              // Write the payload of the NAL unit.
              int writtenBytes = trackOutput.sampleData(input, sampleCurrentNalBytesRemaining);
              blockBytesRead += writtenBytes;
              sampleCurrentNalBytesRemaining -= writtenBytes;
            }
          }
        } else {
          while (blockBytesRead < contentSize) {
            blockBytesRead += trackOutput.sampleData(input, contentSize - blockBytesRead);
          }
        }

        if (CODEC_ID_VORBIS.equals(sampleTrackFormat.codecId)) {
          // Vorbis decoder in android MediaCodec [1] expects the last 4 bytes of the sample to be
          // the number of samples in the current page. This definition holds good only for Ogg and
          // irrelevant for WebM. So we always set this to -1 (the decoder will ignore this value if
          // we set it to -1). The android platform media extractor [2] does the same.
          // [1] https://android.googlesource.com/platform/frameworks/av/+/lollipop-release/media/libstagefright/codecs/vorbis/dec/SoftVorbis.cpp#314
          // [2] https://android.googlesource.com/platform/frameworks/av/+/lollipop-release/media/libstagefright/NuMediaExtractor.cpp#474
          vorbisNumPageSamples.setPosition(0);
          trackOutput.sampleData(vorbisNumPageSamples, 4);
          sampleSize += 4;
        }

        // For SimpleBlock, we send the metadata here as we have all the information. For Block, we
        // send the metadata at the end of the BlockGroup element since we'll know if the frame is a
        // keyframe or not only at that point.
        if (id == ID_SIMPLE_BLOCK) {
          outputSampleMetadata(trackOutput);
        }
        return;
      default:
        throw new IllegalStateException("Unexpected id: " + id);
    }
  }

  private void outputSampleMetadata(TrackOutput trackOutput) {
    trackOutput.sampleMetadata(sampleTimeUs, sampleFlags, sampleSize, 0, null);
    sampleState = SAMPLE_STATE_START;
    sampleRead = true;
  }

  /**
   * Builds a {@link ChunkIndex} containing recently gathered Cues information.
   *
   * @return The built {@link ChunkIndex}.
   * @throws ParserException If the index could not be built.
   */
  private ChunkIndex buildCues() throws ParserException {
    if (segmentContentPosition == UNKNOWN) {
      throw new ParserException("Segment start/end offsets unknown");
    } else if (durationUs == C.UNKNOWN_TIME_US) {
      throw new ParserException("Duration unknown");
    } else if (cueTimesUs == null || cueClusterPositions == null
        || cueTimesUs.size() == 0 || cueTimesUs.size() != cueClusterPositions.size()) {
      throw new ParserException("Invalid/missing cue points");
    }
    int cuePointsSize = cueTimesUs.size();
    int[] sizes = new int[cuePointsSize];
    long[] offsets = new long[cuePointsSize];
    long[] durationsUs = new long[cuePointsSize];
    long[] timesUs = new long[cuePointsSize];
    for (int i = 0; i < cuePointsSize; i++) {
      timesUs[i] = cueTimesUs.get(i);
      offsets[i] = segmentContentPosition + cueClusterPositions.get(i);
    }
    for (int i = 0; i < cuePointsSize - 1; i++) {
      sizes[i] = (int) (offsets[i + 1] - offsets[i]);
      durationsUs[i] = timesUs[i + 1] - timesUs[i];
    }
    sizes[cuePointsSize - 1] =
        (int) (segmentContentPosition + segmentContentSize - offsets[cuePointsSize - 1]);
    durationsUs[cuePointsSize - 1] = durationUs - timesUs[cuePointsSize - 1];
    cueTimesUs = null;
    cueClusterPositions = null;
    return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
  }

  /**
   * Updates the position of the holder to Cues element's position if the extractor configuration
   * permits use of master seek entry. After building Cues sets the holder's position back to where
   * it was before.
   *
   * @param seekPosition The holder whose position will be updated.
   * @param currentPosition Current position of the input.
   * @return true if the seek position was updated, false otherwise.
   */
  private boolean maybeSeekForCues(PositionHolder seekPosition, long currentPosition) {
    if (seekForCues) {
      seekPositionAfterBuildingCues = currentPosition;
      seekPosition.position = cuesContentPosition;
      cuesState = CUES_STATE_BUILDING;
      seekForCues = false;
      return true;
    }
    // After parsing Cues, Seek back to original position if available. We will not do this unless
    // we seeked to get to the Cues in the first place.
    if (cuesState == CUES_STATE_BUILT && seekPositionAfterBuildingCues != UNKNOWN) {
      seekPosition.position = seekPositionAfterBuildingCues;
      seekPositionAfterBuildingCues = UNKNOWN;
      return true;
    }
    return false;
  }

  private long scaleTimecodeToUs(long unscaledTimecode) {
    return TimeUnit.NANOSECONDS.toMicros(unscaledTimecode * timecodeScale);
  }

  private boolean isCodecSupported(String codecId) {
    return CODEC_ID_VP8.equals(codecId)
        || CODEC_ID_VP9.equals(codecId)
        || CODEC_ID_H264.equals(codecId)
        || CODEC_ID_OPUS.equals(codecId)
        || CODEC_ID_VORBIS.equals(codecId)
        || CODEC_ID_AAC.equals(codecId)
        || CODEC_ID_AC3.equals(codecId);
  }

  /**
   * Passes events through to the outer {@link WebmExtractor}.
   */
  private final class InnerEbmlReaderOutput implements EbmlReaderOutput {

    @Override
    public int getElementType(int id) {
      return WebmExtractor.this.getElementType(id);
    }

    @Override
    public void startMasterElement(int id, long contentPosition, long contentSize)
        throws ParserException {
      WebmExtractor.this.startMasterElement(id, contentPosition, contentSize);
    }

    @Override
    public void endMasterElement(int id) throws ParserException {
      WebmExtractor.this.endMasterElement(id);
    }

    @Override
    public void integerElement(int id, long value) throws ParserException {
      WebmExtractor.this.integerElement(id, value);
    }

    @Override
    public void floatElement(int id, double value) {
      WebmExtractor.this.floatElement(id, value);
    }

    @Override
    public void stringElement(int id, String value) throws ParserException {
      WebmExtractor.this.stringElement(id, value);
    }

    @Override
    public void binaryElement(int id, int contentsSize, ExtractorInput input)
        throws IOException, InterruptedException {
      WebmExtractor.this.binaryElement(id, contentsSize, input);
    }

  }

  private static final class TrackFormat {

    // Common track elements.
    public String codecId;
    public int number = UNKNOWN;
    public int type = UNKNOWN;
    public boolean hasContentEncryption;
    public byte[] encryptionKeyId;
    public byte[] codecPrivate;

    // Video track related elements.
    public int pixelWidth = UNKNOWN;
    public int pixelHeight = UNKNOWN;
    public int nalUnitLengthFieldLength = UNKNOWN;

    // Audio track related elements.
    public int channelCount = UNKNOWN;
    public int sampleRate = UNKNOWN;
    public long codecDelayNs = UNKNOWN;
    public long seekPreRollNs = UNKNOWN;

    public TrackOutput trackOutput;

    /** Returns a {@link MediaFormat} built using the information in this instance. */
    public MediaFormat getMediaFormat(long durationUs) throws ParserException {
      String mimeType;
      List<byte[]> initializationData = null;
      int maxInputSize = UNKNOWN;
      /*switch (codecId) */{
        if(codecId.equals(CODEC_ID_VP8)) {
          mimeType = MimeTypes.VIDEO_VP8;
          //break;
        } else if (codecId.equals(CODEC_ID_VP9)) {
          mimeType = MimeTypes.VIDEO_VP9;
          //break;
        } else if (codecId.equals(CODEC_ID_H264)) {
          mimeType = MimeTypes.VIDEO_H264;
          Pair<List<byte[]>, Integer> h264Data = parseH264CodecPrivate(
              new ParsableByteArray(codecPrivate));
          initializationData = h264Data.first;
          nalUnitLengthFieldLength = h264Data.second;
          //break;
        } else if (codecId.equals(CODEC_ID_VORBIS)) {
          mimeType = MimeTypes.AUDIO_VORBIS;
          maxInputSize = VORBIS_MAX_INPUT_SIZE;
          initializationData = parseVorbisCodecPrivate(codecPrivate);
          //break;
        } else if (codecId.equals(CODEC_ID_OPUS)) {
          mimeType = MimeTypes.AUDIO_OPUS;
          maxInputSize = OPUS_MAX_INPUT_SIZE;
          initializationData = new ArrayList<byte[]>(3);
          initializationData.add(codecPrivate);
          initializationData.add(ByteBuffer.allocate(Long.SIZE).putLong(codecDelayNs).array());
          initializationData.add(ByteBuffer.allocate(Long.SIZE).putLong(seekPreRollNs).array());
          //break;
        } else if (codecId.equals(CODEC_ID_AAC)) {
          mimeType = MimeTypes.AUDIO_AAC;
          initializationData = Collections.singletonList(codecPrivate);
          //break;
        } else if (codecId.equals(CODEC_ID_AC3)) {
          mimeType = MimeTypes.AUDIO_AC3;
          //break;
        } else /*default:*/ {
          throw new ParserException("Unrecognized codec identifier.");
        }
      }

      if (MimeTypes.isAudio(mimeType)) {
        return MediaFormat.createAudioFormat(mimeType, maxInputSize, durationUs, channelCount,
            sampleRate, initializationData);
      } else if (MimeTypes.isVideo(mimeType)) {
        return MediaFormat.createVideoFormat(mimeType, maxInputSize, durationUs, pixelWidth,
            pixelHeight, initializationData);
      } else {
        throw new ParserException("Unexpected MIME type.");
      }
    }

    /**
     * Builds initialization data for a {@link MediaFormat} from H.264 codec private data.
     *
     * @return The initialization data for the {@link MediaFormat}.
     * @throws ParserException If the initialization data could not be built.
     */
    private static Pair<List<byte[]>, Integer> parseH264CodecPrivate(ParsableByteArray buffer)
        throws ParserException {
      try {
        // TODO: Deduplicate with AtomParsers.parseAvcCFromParent.
        buffer.setPosition(4);
        int nalUnitLengthFieldLength = (buffer.readUnsignedByte() & 0x03) + 1;
        Assertions.checkState(nalUnitLengthFieldLength != 3);
        List<byte[]> initializationData = new ArrayList<byte[]>();
        int numSequenceParameterSets = buffer.readUnsignedByte() & 0x1F;
        for (int i = 0; i < numSequenceParameterSets; i++) {
          initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        int numPictureParameterSets = buffer.readUnsignedByte();
        for (int j = 0; j < numPictureParameterSets; j++) {
          initializationData.add(NalUnitUtil.parseChildNalUnit(buffer));
        }
        return Pair.create(initializationData, nalUnitLengthFieldLength);
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new ParserException("Error parsing vorbis codec private");
      }
    }

    /**
     * Builds initialization data for a {@link MediaFormat} from Vorbis codec private data.
     *
     * @return The initialization data for the {@link MediaFormat}.
     * @throws ParserException If the initialization data could not be built.
     */
    private static List<byte[]> parseVorbisCodecPrivate(byte[] codecPrivate)
        throws ParserException {
      try {
        if (codecPrivate[0] != 0x02) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        int offset = 1;
        int vorbisInfoLength = 0;
        while (codecPrivate[offset] == (byte) 0xFF) {
          vorbisInfoLength += 0xFF;
          offset++;
        }
        vorbisInfoLength += codecPrivate[offset++];

        int vorbisSkipLength = 0;
        while (codecPrivate[offset] == (byte) 0xFF) {
          vorbisSkipLength += 0xFF;
          offset++;
        }
        vorbisSkipLength += codecPrivate[offset++];

        if (codecPrivate[offset] != 0x01) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        byte[] vorbisInfo = new byte[vorbisInfoLength];
        System.arraycopy(codecPrivate, offset, vorbisInfo, 0, vorbisInfoLength);
        offset += vorbisInfoLength;
        if (codecPrivate[offset] != 0x03) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        offset += vorbisSkipLength;
        if (codecPrivate[offset] != 0x05) {
          throw new ParserException("Error parsing vorbis codec private");
        }
        byte[] vorbisBooks = new byte[codecPrivate.length - offset];
        System.arraycopy(codecPrivate, offset, vorbisBooks, 0, codecPrivate.length - offset);
        List<byte[]> initializationData = new ArrayList<byte[]>(2);
        initializationData.add(vorbisInfo);
        initializationData.add(vorbisBooks);
        return initializationData;
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new ParserException("Error parsing vorbis codec private");
      }
    }

  }

}
