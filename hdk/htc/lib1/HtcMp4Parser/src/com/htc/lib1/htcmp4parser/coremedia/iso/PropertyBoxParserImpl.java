/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.htcmp4parser.coremedia.iso;

import android.content.Context;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Property file based BoxFactory
 *  @hide
 * {@exthide}
 */
public class PropertyBoxParserImpl extends AbstractBoxParser {
    /**
     * @hide
     */
    Properties mapping;
    static private Context sAppContext;
    /**
     * @hide
     */    
    public static void setAppContext(Context context) {
    	sAppContext = context.getApplicationContext();
    }

    private static Properties sMapping = new Properties();
    static {
    	//sMapping.setProperty(name, value);
    	sMapping.setProperty("hint","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackReferenceTypeBox(type)");
    	sMapping.setProperty("cdsc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackReferenceTypeBox(type)");
    	sMapping.setProperty("meta-ilst","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleItemListBox()");
    	sMapping.setProperty("-----name","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleNameBox()");
    	sMapping.setProperty("-----mean","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleMeanBox()");
    	sMapping.setProperty("-----data","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleDataBox()");
    	sMapping.setProperty("rmra","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleReferenceMovieBox()");
    	sMapping.setProperty("rmda","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleReferenceMovieDescriptorBox()");
    	sMapping.setProperty("rmdr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleDataRateBox()");
    	sMapping.setProperty("rdrf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleDataReferenceBox()");
    	sMapping.setProperty("ilst-cprt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleCopyrightBox()");
    	sMapping.setProperty("ilst-\u00A9cmt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleCommentBox()");
    	sMapping.setProperty("ilst-desc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleDescriptionBox()");
    	sMapping.setProperty("ilst-covr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleCoverBox()");
    	sMapping.setProperty("ilst-\u00A9alb","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleAlbumBox()");
    	sMapping.setProperty("ilst-\u00A9gen","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleCustomGenreBox()");
    	sMapping.setProperty("ilst-\u00A9grp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleGroupingBox()");
    	sMapping.setProperty("ilst-\u00A9wrt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTrackAuthorBox()");
    	sMapping.setProperty("ilst-aART","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleAlbumArtistBox()");
    	sMapping.setProperty("ilst-tvsh","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleShowBox()");
    	sMapping.setProperty("ilst-stik","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleMediaTypeBox()");
    	sMapping.setProperty("ilst-pgap","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleGaplessPlaybackBox()");
    	sMapping.setProperty("ilst-tmpo","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTempBox()");
    	sMapping.setProperty("ilst-\u00A9nam","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTrackTitleBox()");
    	sMapping.setProperty("ilst-ldes","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleSynopsisBox()");
    	sMapping.setProperty("ilst-\u00A9ART","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleArtistBox()");
    	sMapping.setProperty("ilst-name","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleNameBox()");
    	sMapping.setProperty("ilst-cpil","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleCompilationBox()");
    	sMapping.setProperty("ilst-purd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.ApplePurchaseDateBox()");
    	sMapping.setProperty("ilst-\u00A9too","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleEncoderBox()");
    	sMapping.setProperty("ilst-sfID","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleStoreCountryCodeBox()");
    	sMapping.setProperty("ilst-gnre","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleStandardGenreBox()");
    	sMapping.setProperty("ilst-tves","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTvEpisodeBox()");
    	sMapping.setProperty("ilst-ilst","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleItemListBox()");
    	sMapping.setProperty("ilst-data","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleDataBox()");
    	sMapping.setProperty("ilst-tvsn","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTvSeasonBox()");
    	sMapping.setProperty("ilst-soal","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleSortAlbumBox()");
    	sMapping.setProperty("ilst-tven","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTvEpisodeNumberBox()");
    	sMapping.setProperty("ilst-trkn","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleTrackNumberBox()");
    	sMapping.setProperty("ilst-\u00A9day","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleRecordingYearBox()");
    	sMapping.setProperty("ilst-----","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleGenericBox()");
    	sMapping.setProperty("ilst-akID","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleStoreAccountTypeBox()");
    	sMapping.setProperty("ilst-rtng","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleRatingBox()");
    	sMapping.setProperty("ilst-tvnn","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleNetworkBox()");
    	sMapping.setProperty("ilst-apID","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleIdBox()");
    	sMapping.setProperty("wave","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleWaveBox()");
    	
    	sMapping.setProperty("udta-ccid","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmContentIdBox()");
    	sMapping.setProperty("udta-yrrc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.RecordingYearBox()");
    	sMapping.setProperty("udta-titl","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TitleBox()");
    	sMapping.setProperty("udta-dscp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DescriptionBox()");
    	sMapping.setProperty("udta-icnu","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmIconUriBox()");
    	sMapping.setProperty("udta-infu","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmInfoUrlBox()");
    	sMapping.setProperty("udta-albm","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.AlbumBox()");
    	sMapping.setProperty("udta-cprt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CopyrightBox()");
    	sMapping.setProperty("udta-gnre","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.GenreBox()");
    	sMapping.setProperty("udta-perf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.PerformerBox()");
    	sMapping.setProperty("udta-auth","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.AuthorBox()");
    	sMapping.setProperty("udta-kywd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.KeywordsBox()");
    	sMapping.setProperty("udta-loci","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.threegpp26244.LocationInformationBox()");
    	sMapping.setProperty("udta-rtng","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.RatingBox()");
    	sMapping.setProperty("udta-clsf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ClassificationBox()");
    	sMapping.setProperty("udta-cdis","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.vodafone.ContentDistributorIdBox()");
    	sMapping.setProperty("udta-albr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.vodafone.AlbumArtistBox()");
    	sMapping.setProperty("udta-cvru","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmCoverUriBox()");
    	sMapping.setProperty("udta-lrcu","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmLyricsUriBox()");
    	
    	sMapping.setProperty("stsd-tx3g","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.TextSampleEntry(type)");
    	sMapping.setProperty("stsd-text","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.QuicktimeTextSampleEntry()");
    	sMapping.setProperty("stsd-enct","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.TextSampleEntry(type)");
    	sMapping.setProperty("stsd-samr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-sawb","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-mp4a","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-drms","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-alac","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-mp4s","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.MpegSampleEntry(type)");
    	sMapping.setProperty("stsd-owma","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-ac-3","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("dac3","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.AC3SpecificBox()");
    	sMapping.setProperty("stsd-ec-3","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("dec3","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.EC3SpecificBox()");
    	sMapping.setProperty("stsd-lpcm","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-dtsc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-dtsh","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-dtsl","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("ddts","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.DTSSpecificBox()");
    	sMapping.setProperty("stsd-dtse","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-mlpa","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("dmlp","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.MLPSpecificBox()");
    	sMapping.setProperty("stsd-enca","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AudioSampleEntry(type)");
    	sMapping.setProperty("stsd-encv","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.VisualSampleEntry(type)");
    	sMapping.setProperty("stsd-mp4v","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.VisualSampleEntry(type)");
    	sMapping.setProperty("stsd-s263","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.VisualSampleEntry(type)");
    	sMapping.setProperty("stsd-avc1","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.VisualSampleEntry(type)");
    	sMapping.setProperty("stsd-ovc1","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.Ovc1VisualSampleEntryImpl()");
    	sMapping.setProperty("stsd-stpp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.SubtitleSampleEntry(type)");
    	
    	// yishung mark
    	sMapping.setProperty("avcC","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.h264.AvcConfigurationBox()");
    	sMapping.setProperty("alac","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleLosslessSpecificBox()");
    	sMapping.setProperty("btrt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.BitRateBox()");
    	sMapping.setProperty("ftyp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.FileTypeBox()");
    	sMapping.setProperty("mdat","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.MediaDataBox()");
    	sMapping.setProperty("moov","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieBox()");
    	sMapping.setProperty("mvhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieHeaderBox()");
    	sMapping.setProperty("trak","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackBox()");
    	sMapping.setProperty("tkhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackHeaderBox()");
    	sMapping.setProperty("edts","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.EditBox()");
    	sMapping.setProperty("elst","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.EditListBox()");
    	sMapping.setProperty("mdia","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaBox()");
    	sMapping.setProperty("mdhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaHeaderBox()");
    	sMapping.setProperty("hdlr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.HandlerBox()");
    	sMapping.setProperty("minf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaInformationBox()");
    	sMapping.setProperty("vmhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.VideoMediaHeaderBox()");
    	sMapping.setProperty("smhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SoundMediaHeaderBox()");
    	sMapping.setProperty("sthd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SubtitleMediaHeaderBox()");
    	sMapping.setProperty("hmhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.HintMediaHeaderBox()");
    	sMapping.setProperty("dinf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataInformationBox()");
    	sMapping.setProperty("dref","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataReferenceBox()");
    	sMapping.setProperty("url ","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataEntryUrlBox()");
    	sMapping.setProperty("urn ","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataEntryUrnBox()");
    	sMapping.setProperty("stbl","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleTableBox()");
    	sMapping.setProperty("ctts","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CompositionTimeToSample()");
    	sMapping.setProperty("stsd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDescriptionBox()");
    	sMapping.setProperty("stts","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TimeToSampleBox()");
    	sMapping.setProperty("stss","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SyncSampleBox()");
    	sMapping.setProperty("stsc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleToChunkBox()");
    	sMapping.setProperty("stsz","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleSizeBox()");
    	sMapping.setProperty("stco","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.StaticChunkOffsetBox()");
    	sMapping.setProperty("subs","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SubSampleInformationBox()");
    	sMapping.setProperty("udta","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UserDataBox()");
    	sMapping.setProperty("skip","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.FreeSpaceBox()");
    	sMapping.setProperty("tref","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackReferenceBox()");
    	sMapping.setProperty("iloc","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ItemLocationBox()");
    	sMapping.setProperty("idat","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ItemDataBox()");
    	sMapping.setProperty("saio","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleAuxiliaryInformationOffsetsBox()");
    	sMapping.setProperty("saiz","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleAuxiliaryInformationSizesBox()");
    	sMapping.setProperty("damr","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.AmrSpecificBox()");
    	sMapping.setProperty("meta","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MetaBox()");
    	sMapping.setProperty("ipro","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ItemProtectionBox()");
    	sMapping.setProperty("sinf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ProtectionSchemeInformationBox()");
    	sMapping.setProperty("frma","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.OriginalFormatBox()");
    	sMapping.setProperty("schi","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SchemeInformationBox()");
    	sMapping.setProperty("odkm","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.odf.OmaDrmKeyManagenentSystemBox()");
    	sMapping.setProperty("odaf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.OmaDrmAccessUnitFormatBox()");
    	sMapping.setProperty("schm","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SchemeTypeBox()");
    	sMapping.setProperty("uuid","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UserBox(userType)");
    	sMapping.setProperty("free","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.FreeBox()");
    	sMapping.setProperty("styp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.SegmentTypeBox()");
    	sMapping.setProperty("mvex","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieExtendsBox()");
    	sMapping.setProperty("mehd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox()");
    	sMapping.setProperty("trex","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackExtendsBox()");
    	
    	sMapping.setProperty("moof","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieFragmentBox()");
    	sMapping.setProperty("mfhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox()");
    	sMapping.setProperty("traf","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackFragmentBox()");
    	sMapping.setProperty("tfhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox()");
    	sMapping.setProperty("trun","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackRunBox()");
    	sMapping.setProperty("sdtp","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDependencyTypeBox()");
    	sMapping.setProperty("mfra","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessBox()");
    	sMapping.setProperty("tfra","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox()");
    	sMapping.setProperty("mfro","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox()");
    	sMapping.setProperty("tfdt","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox()");
    	sMapping.setProperty("nmhd","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.NullMediaHeaderBox()");
    	sMapping.setProperty("gmhd","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.GenericMediaHeaderAtom()");
    	sMapping.setProperty("gmhd-text","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.GenericMediaHeaderTextAtom()");
    	sMapping.setProperty("gmin","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.BaseMediaInfoAtom()");
    	sMapping.setProperty("cslg","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CompositionShiftLeastGreatestAtom()");
    	sMapping.setProperty("pdin","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ProgressiveDownloadInformationBox()");
    	sMapping.setProperty("bloc","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.ultraviolet.BaseLocationBox()");
    	sMapping.setProperty("ftab","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.threegpp26245.FontTableBox()");
    	sMapping.setProperty("co64","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ChunkOffset64BitBox()");
    	sMapping.setProperty("xml ","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.XmlBox()");
    	sMapping.setProperty("avcn","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.basemediaformat.AvcNalUnitStorageBox()");
    	sMapping.setProperty("ainf","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.ultraviolet.AssetInformationBox()");
    	sMapping.setProperty("pssh","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.cenc.ProtectionSystemSpecificHeaderBox()");
    	sMapping.setProperty("trik","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.dece.TrickPlayBox()");
    	sMapping.setProperty("uuid[A2394F525A9B4F14A2446C427C648DF4]","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff.PiffSampleEncryptionBox()");
    	sMapping.setProperty("uuid[8974DBCE7BE74C5184F97148F9882554]","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff.PiffTrackEncryptionBox()");
    	sMapping.setProperty("uuid[D4807EF2CA3946958E5426CB9E46A79F]","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff.TfrfBox()");
    	sMapping.setProperty("uuid[6D1D9B0542D544E680E2141DAFF757B2]","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff.TfxdBox()");
    	sMapping.setProperty("uuid[D08A4F1810F34A82B6C832D8ABA183D3]","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff.UuidBasedProtectionSystemSpecificHeaderBox()");
    	sMapping.setProperty("senc","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.basemediaformat.SampleEncryptionBox()");
    	sMapping.setProperty("tenc","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.basemediaformat.TrackEncryptionBox()");
    	sMapping.setProperty("amf0","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.adobe.ActionMessageFormat0SampleEntryBox()");
    	
    	//iods","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.ObjectDescriptorBox()");
    	sMapping.setProperty("esds","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.ESDescriptorBox()");
    	
    	sMapping.setProperty("tmcd","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.TimeCodeBox()");
    	sMapping.setProperty("sidx","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.threegpp26244.SegmentIndexBox()");
    	
    	sMapping.setProperty("sbgp","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox()");
    	sMapping.setProperty("sgpd","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox()");
    	
    	sMapping.setProperty("default","com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UnknownBox(type)");
    	
    	//for htc slowMotion
    	sMapping.setProperty("_htc","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcSlowMotionBox()");
    	
    	//for zoe
    	sMapping.setProperty("htcb","com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox()");
    	
    	//for geodata
    	sMapping.setProperty("©xyz", "com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.GeoDataBox()");
    }
    /**
     * @hide
     */    
    public PropertyBoxParserImpl(String... customProperties) {
    	mapping = sMapping;
    	
//    	if (sAppContext == null) {
//    		throw new RuntimeException("App Context is null. Call setAppContext(Context context) first");
//    	}
//    	InputStream is = null;
//    	try {
//			is = new BufferedInputStream(sAppContext.getAssets().open("isoparser-default.properties"));
//			mapping = new Properties();
//			mapping.load(is);
//			
//			// TODO: currently ignore "isoparser-custom.properties"
//			
//			// TODO: currently ignore "customProperties"
//			
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
    	
    	// This section is porting to android resource management, which is loaded from assets
//        InputStream is = new BufferedInputStream(getClass().getResourceAsStream("/isoparser-default.properties"));
//        try {
//            mapping = new Properties();
//            try {
//                mapping.load(is);
//                Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources("isoparser-custom.properties");
//
//                while (enumeration.hasMoreElements()) {
//                    URL url = enumeration.nextElement();
//                    InputStream customIS = new BufferedInputStream(url.openStream());
//                    try {
//                        mapping.load(customIS);
//                    } finally {
//                        customIS.close();
//                    }
//                }
//                for (String customProperty : customProperties) {
//                    mapping.load(new BufferedInputStream(getClass().getResourceAsStream(customProperty)));
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                // ignore - I can't help
//            }
//        }
    }
    /**
     * @hide
     */
    public PropertyBoxParserImpl(Properties mapping) {
        this.mapping = mapping;
    }
    /**
     * @hide
     */
    Pattern p = Pattern.compile("(.*)\\((.*?)\\)");
    /**
     * @hide
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Box> getClassForFourCc(String type, byte[] userType, String parent) {
        FourCcToBox fourCcToBox = new FourCcToBox(type, userType, parent).invoke();
        try {
            return (Class<? extends Box>) Class.forName(fourCcToBox.clazzName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @hide
     */
    @Override
    public Box createBox(String type, byte[] userType, String parent) {

        FourCcToBox fourCcToBox = new FourCcToBox(type, userType, parent).invoke();
        String[] param = fourCcToBox.getParam();
        String clazzName = fourCcToBox.getClazzName();
        try {
            if (param[0].trim().length() == 0) {
                param = new String[]{};
            }
            Class clazz = Class.forName(clazzName);

            Class[] constructorArgsClazz = new Class[param.length];
            Object[] constructorArgs = new Object[param.length];
            for (int i = 0; i < param.length; i++) {

                if ("userType".equals(param[i])) {
                    constructorArgs[i] = userType;
                    constructorArgsClazz[i] = byte[].class;
                } else if ("type".equals(param[i])) {
                    constructorArgs[i] = type;
                    constructorArgsClazz[i] = String.class;
                } else if ("parent".equals(param[i])) {
                    constructorArgs[i] = parent;
                    constructorArgsClazz[i] = String.class;
                } else {
                    throw new InternalError("No such param: " + param[i]);
                }


            }
            Constructor<AbstractBox> constructorObject;
            try {
                if (param.length > 0) {
                    constructorObject = clazz.getConstructor(constructorArgsClazz);
                } else {
                    constructorObject = clazz.getConstructor();
                }

                return constructorObject.newInstance(constructorArgs);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private class FourCcToBox {
        private String type;
        private byte[] userType;
        private String parent;
        private String clazzName;
        private String[] param;

        public FourCcToBox(String type, byte[] userType, String parent) {
            this.type = type;
            this.parent = parent;
            this.userType = userType;
        }

        public String getClazzName() {
            return clazzName;
        }

        public String[] getParam() {
            return param;
        }

        public FourCcToBox invoke() {
            String constructor;
            if (userType != null) {
                if (!"uuid".equals((type))) {
                    throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
                }
                constructor = mapping.getProperty((parent) + "-uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
                if (constructor == null) {
                    constructor = mapping.getProperty("uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
                }
                if (constructor == null) {
                    constructor = mapping.getProperty("uuid");
                }
            } else {
                constructor = mapping.getProperty((parent) + "-" + (type));
                if (constructor == null) {
                    constructor = mapping.getProperty((type));
                }
            }
            if (constructor == null) {
                constructor = mapping.getProperty("default");
            }
            if (constructor == null) {
                throw new RuntimeException("No box object found for " + type);
            }
            Matcher m = p.matcher(constructor);
            boolean matches = m.matches();
            if (!matches) {
                throw new RuntimeException("Cannot work with that constructor: " + constructor);
            }
            clazzName = m.group(1);
            param = m.group(2).split(",");
            return this;
        }
    }
}
