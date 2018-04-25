package com.htc.lib1.exifengine;

import android.annotation.SuppressLint;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * Copyright (C) 2014 HTC Corp. All Rights Reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is a class for reading and writing Exif tags in a JPEG file.
 */
@SuppressLint("NewApi")
public class ExifEngine {

	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_IMAGE_WIDTH = 0x000100;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_IMAGE_HEIGHT = 0x000101;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_BITS_PER_SAMPLE = 0x000102;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_COMPRESSION = 0x000103;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_PHOTOMETRIC_INTERPRETATION = 0x000106;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_IMAGE_DESCRIPTION = 0x00010e;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_MAKE = 0x00010f;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_MODEL = 0x000110;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_STRIP_OFFSETS = 0x000111;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_ORIENTATION = 0x000112;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SAMPLES_PER_PIXEL = 0x000115;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_ROWS_PER_STRIP = 0x000116;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_STRIP_BYTE_COUNTS = 0x000117;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_X_RESOLUTION = 0x00011a;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_Y_RESOLUTION = 0x00011b;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_PLANAR_CONFIGURATION = 0x00011c;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_RESOLUTION_UNIT = 0x000128;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_TRANSFER_FUNCTION = 0x00012d;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_SOFTWARE = 0x000131;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_DATE_TIME = 0x000132;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_ARTIST = 0x00013b;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_WHITE_POINT = 0x00013e;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_PRIMARY_CHROMATICITIES = 0x00013f;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_JPEG_INTERCHANGE_FORMAT = 0x000201;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = 0x000202;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_YCBCR_COEFFICIENTS = 0x000211;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_YCBCR_SUBSAMPLING = 0x000212;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_YCBCR_POSITIONING = 0x000213;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_REFERENCE_BLACK_WHITE = 0x000214;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_COPYRIGHT = 0x008298;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_IFD_POINTER = 0x008769;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_INFO_IFD_POINTER = 0x008825;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_EXPOSURE_TIME = 0x01829a;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_FNUMBER = 0x01829d;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_EXPOSURE_PROGRAM = 0x018822;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_SPECTRAL_SENSITIVITY = 0x018824;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_ISO_SPEED_RATINGS = 0x018827;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_OECF = 0x018828;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_VERSION = 0x019000;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_DATE_TIME_ORIGINAL = 0x019003;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_DATE_TIME_DIGITIZED = 0x019004;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_COMPONENTS_CONFIGURATION = 0x019101;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_COMPRESSED_BITS_PER_PIXEL = 0x019102;
	/**
	 * Type is srational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_SHUTTER_SPEED_VALUE = 0x019201;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_APERTURE_VALUE = 0x019202;
	/**
	 * Type is srational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_BRIGHTNESS_VALUE = 0x019203;
	/**
	 * Type is srational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_EXPOSURE_BIAS_VALUE = 0x019204;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_MAX_APERTURE_VALUE = 0x019205;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_SUBJECT_DISTANCE = 0x019206;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_METERING_MODE = 0x019207;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_LIGHT_SOURCE = 0x019208;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_FLASH = 0x019209;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_FOCAL_LENGTH = 0x01920a;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_SUBJECT_AREA = 0x019214;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_MAKER_NOTE = 0x01927c;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_USER_COMMENT = 0x019286;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_SUBSEC_TIME = 0x019290;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_SUBSEC_TIME_ORIGINAL = 0x019291;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_SUBSEC_TIME_DIGITIZED = 0x019292;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_FLASHPIX_VERSION = 0x01a000;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_COLOR_SPACE = 0x01a001;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_PIXEL_XDIMENSION = 0x01a002;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_PIXEL_YDIMENSION = 0x01a003;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_RELATED_SOUND_FILE = 0x01a004;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_INTEROPERABILITY_IFD_POINTER = 0x01a005;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_FLASH_ENERGY = 0x01a20b;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_SPATIAL_FREQUENCY_RESPONSE = 0x01a20c;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_FOCAL_PLANE_XRESOLUTION = 0x01a20e;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_FOCAL_PLANE_YRESOLUTION = 0x01a20f;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT = 0x01a210;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SUBJECT_LOCATION = 0x01a214;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_EXPOSURE_INDEX = 0x01a215;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SENSING_METHOD = 0x01a217;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_FILE_SOURCE = 0x01a300;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_SCENE_TYPE = 0x01a301;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_CFA_PATTERN = 0x01a302;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_CUSTOM_RENDERED = 0x01a401;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_EXPOSURE_MODE = 0x01a402;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_WHITE_BALANCE = 0x01a403;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_DIGITAL_ZOOM_RATIO = 0x01a404;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_FOCAL_LENGTH_IN35MMFILM = 0x01a405;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SCENE_CAPTURE_TYPE = 0x01a406;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GAIN_CONTROL = 0x01a407;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_CONTRAST = 0x01a408;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SATURATION = 0x01a409;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SHARPNESS = 0x01a40a;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_DEVICE_SETTING_DESCRIPTION = 0x01a40b;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_SUBJECT_DISTANCE_RANGE = 0x01a40c;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_IMAGE_UNIQUE_ID = 0x01a420;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_INTEROPERATILITY_INDEX = 0x020001;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_GPS_VERSION_ID = 0x030000;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_GPS_LATITUDE_REF = 0x030001;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_GPS_LATITUDE = 0x030002;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_GPS_LONGITUDE_REF = 0x030003;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_GPS_LONGITUDE = 0x030004;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_GPS_ALTITUDE_REF = 0x030005;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_GPS_ALTITUDE = 0x030006;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_GPS_TIME_STAMP = 0x030007;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_SATELLITES = 0x030008;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_STATUS = 0x030009;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_MEASURE_MODE = 0x03000a;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DOP = 0x03000b;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_SPEED_REF = 0x03000c;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_SPEED = 0x03000d;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_TRACK_REF = 0x03000e;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_TRACK = 0x03000f;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_IMG_DIRECTION_REF = 0x030010;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_IMG_DIRECTION = 0x030011;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_MAP_DATUM = 0x030012;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_LATITUDE_REF = 0x030013;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_LATITUDE = 0x030014;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_LONGTITUDE_REF = 0x030015;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_LONGTITUDE = 0x030016;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_BEARING_REF = 0x030017;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_BEARING = 0x030018;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_DISTANCE_REF = 0x030019;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DEST_DISTANCE = 0x03001a;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_GPS_PROCESSING_METHOD = 0x03001b;
	/**
	 * Type is String.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_AREA_INFORMATION = 0x03001c;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_GPS_DATE_STAMP = 0x03001d;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_GPS_DIFFERENTIAL = 0x03001e;

	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_IMAGE_WIDTH = 0x040100;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_IMAGE_HEIGHT = 0x040101;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_BITS_PER_SAMPLE = 0x040102;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_COMPRESSION = 0x040103;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_PHOTOMETRIC_INTERPRETATION = 0x040106;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_IMAGE_DESCRIPTION = 0x04010e;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_MAKE = 0x04010f;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_MODEL = 0x040110;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_STRIP_OFFSETS = 0x040111;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_ORIENTATION = 0x040112;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_SAMPLES_PER_PIXEL = 0x040115;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_ROWS_PER_STRIP = 0x040116;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_STRIP_BYTE_COUNTS = 0x040117;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_THUMBNAIL_X_RESOLUTION = 0x04011a;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_THUMBNAIL_Y_RESOLUTION = 0x04011b;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_PLANAR_CONFIGURATION = 0x04011c;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_RESOLUTION_UNIT = 0x040128;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_TRANSFER_FUNCTION = 0x04012d;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_SOFTWARE = 0x040131;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_DATE_TIME = 0x040132;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_ARTIST = 0x04013b;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_THUMBNAIL_WHITE_POINT = 0x04013e;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_THUMBNAIL_PRIMARY_CHROMATICITIES = 0x04013f;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_JPEG_INTERCHANGE_FORMAT = 0x040201;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_JPEG_INTERCHANGE_FORMAT_LENGTH = 0x040202;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_YCBCR_COEFFICIENTS = 0x040211;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_YCBCR_SUBSAMPLING = 0x040212;
	/**
	 * Type is int.
	 */
	public static final int EXIF_TAG_THUMBNAIL_YCBCR_POSITIONING = 0x040213;
	/**
	 * Type is rational, tow int , the first is the numerator and second is
	 * denominator.
	 */
	public static final int EXIF_TAG_THUMBNAIL_REFERENCE_BLACK_WHITE = 0x040214;
	/**
	 * Type is String.
	 */
	public static final int EXIF_TAG_THUMBNAIL_COPYRIGHT = 0x048298;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_EXIF_IFD_POINTER = 0x048769;
	/**
	 * Type is int.
	 *
	 * @deprecated this tag didn't pass the Write-Read test, maybe read is ok,But we cannot prove this point.
	 */
	public static final int EXIF_TAG_THUMBNAIL_GPS_INFO_IFD_POINTER = 0x048825;

	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_DUAL_CAM = 0x100001;
	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_VCM = 0x100002;
	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_FOCUS_AREA = 0x100003;
	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_CALIBRATION = 0x100006;
	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_EX_FLAG = 0x100007;
	/**
	 * Type is int.
	 */
	private static final int EXIF_TAG_HTC_MAKERNOTE_TAG_CAMERAID = 0x100010;

	/**
	 * The jnitdone is ok
	 */
	public static final int EXIF_ENGINE_DONE_OK = 0;
	/**
	 * The jni done is wrong public static final int EXIF_ENGINE_FAILED = -1;
	 * <p/>
	 * private static final int EXIF_ENGINE_EMPTY_HANDLE = -2; /** The done is
	 * wrong
	 */
	private static final int EXIF_ENGINE_INVALID_HANDLE = -3;

	private static final int EXIF_ENGINE_INVALID_PARAMETER = -4;

	private static final int EXIF_ENGINE_ALLOC_FAIL = -5;

	/* libexif section */
	private static final int LIBEXIF_IFD_0 = 0;
	private static final int LIBEXIF_IFD_1 = 1;
	private static final int LIBEXIF_IFD_EXIF = 2;
	private static final int LIBEXIF_IFD_GPS = 3;
	private static final int LIBEXIF_IFD_INTEROPERABILITY = 4;
	private static final int LIBEXIF_IFD_COUNT = 5;

	private long mHandle;
	private String mFileName;
	private ParcelFileDescriptor mFD;
	private Locale mLocale;

	// We use sLock to guarantee thread safety
	private final Object sLock = new Object();

	static {
		System.loadLibrary("exifengine_v0");
	}

	/**
	 * Reads Exif tags from the specified JPEG file.
	 *
	 * @param filename the name of the jpeg.
	 * @param locale   the i18n refer
	 */
	public ExifEngine(String filename, Locale locale) throws IOException {
		if (filename == null) {
			throw new IllegalArgumentException("filename cannot be null");
		}
		mFileName = filename;
		mHandle = readExifData(filename);
		mLocale = locale;
	}

	/**
	 * @see #ExifEngine(String, java.util.Locale)
	 */
	public ExifEngine(String filename) throws IOException {
		this(filename, Locale.getDefault());
	}

	/**
	 * Reads Exif tags from the specified JPEG file.
	 *
	 * @param filename the name of the jpeg.
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	private long readExifData(String filename) {
		return readExifDataNative(mFileName);
	}

	/**
	 * Reads Exif tags from the specified file descriptor.
	 *
	 * @param fd     the fd of the jpeg.
	 * @param locale the i18n refer
	 */
	public ExifEngine(ParcelFileDescriptor fd, Locale locale)
			throws IOException {
		if (fd == null) {
			throw new IllegalArgumentException("fd cannot be null");
		}
		mFD = fd;
		mHandle = readExifDataFromFD(mFD.getFd());
		mLocale = locale;
	}

	/**
	 * @see #ExifEngine(android.os.ParcelFileDescriptor, java.util.Locale)
	 */
	public ExifEngine(ParcelFileDescriptor fd) throws IOException {
		this(fd, Locale.getDefault());
	}

	/**
	 * Reads Exif tags from the specified file descriptor.
	 *
	 * @param fd the fd of the jpeg.
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	private long readExifDataFromFD(int fd) {
		return readExifDataFromFDNative(fd);
	}

	/**
	 * Get the section from the specified exif tag ,and the result is converted
	 * according to libexif.
	 *
	 * @param exiftag the ID of the tag.
	 */
	private int getExifTagSection(int exiftag) {
		int section = LIBEXIF_IFD_COUNT;
		section = exiftag >> 16;
		switch (section) {
			case 0:
				section = LIBEXIF_IFD_0;
				break;
			case 1:
			case 0x10:
				section = LIBEXIF_IFD_EXIF;
				break;
			case 2:
				section = LIBEXIF_IFD_INTEROPERABILITY;
				break;
			case 3:
				section = LIBEXIF_IFD_GPS;
				break;
			case 4:
				section = LIBEXIF_IFD_1;
				break;
			default:
				break;
		}

		return section;
	}

	public static final int MASK_TAG_ID = 0x00FFFF;

	/**
	 * Get the ExifTagID from the specified exif tag ,and the result is
	 * converted according to libexif.
	 *
	 * @param exiftag the ID of the tag.
	 */
	private int getTagID(int exiftag) {
		int tag = 0;
		tag = exiftag & MASK_TAG_ID;

		return tag;
	}

	/**
	 * Returns the value string of the specified exif tag or {@code null} if
	 * there is no such tag in jpeg file.
	 *
	 * @param exiftag the ID of the tag.
	 */
	public String getTagValueString(int exiftag) {
		synchronized (sLock) {
			return onFetchedRawTagValueString(
					exiftag,
					getTagValueStringNative(mHandle,
							getExifTagSection(exiftag), getTagID(exiftag), true));
		}
	}

	/**
	 * Returns the integer value of the specified tag. If there is no such tag
	 * in the JPEG file or the value cannot be parsed as integer, return
	 * <var>defaultValue</var>.
	 *
	 * @param exiftag      the name of the tag.
	 * @param defaultValue the value to return if the tag is not available.
	 */
	public int getTagValueInt(int exiftag, int defaultValue) {
		synchronized (sLock) {
			return getTagValueIntNative(mHandle, getExifTagSection(exiftag),
					getTagID(exiftag), defaultValue);
		}
	}

	// /*
	// * Get the value of the specified tag.
	// *
	// * @param tag the name of the tag.
	// *
	// * @param value the value of the tag. value contains two elements,value[0]
	// * is numerator, value[1] is denominator
	// */
	// @Deprecated
	// public String getTagValueRational(int exiftag) {
	// return getTagValueString(exiftag);
	// }

	/**
	 * Get the value of the specified tag.
	 *
	 * @param exiftag      the name of the tag.
	 * @param defaultValue the value of the tag. value contains two elements,value[0] is
	 *                     numerator, value[1] is denominator
	 */
	public int[] getTagValueRational(int exiftag, int[] defaultValue) {
		synchronized (sLock) {
			return getTagValueRationalNative(mHandle,
					getExifTagSection(exiftag), getTagID(exiftag), defaultValue);
		}
	}

	/**
	 * Set the value of the specified tag.
	 *
	 * @param exiftag the name of the tag.
	 * @param value   the value of the tag.
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	public int setTagValueString(int exiftag, String value) {
		synchronized (sLock) {
			return setTagValueStringNative(mHandle, getExifTagSection(exiftag),
					getTagID(exiftag), value);
		}
	}

	/**
	 * Set the value of the specified tag.
	 *
	 * @param exiftag the name of the tag.
	 * @param value   the value of the tag.
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	public int setTagValueInt(int exiftag, int value) {
		synchronized (sLock) {
			return setTagValueIntNative(mHandle, getExifTagSection(exiftag),
					getTagID(exiftag), value);
		}
	}

	/**
	 * Set the value of the specified tag.
	 *
	 * @param exiftag the name of the tag.
	 * @param value   the value of the tag. value contains two elements,value[0] is
	 *                numerator, value[1] is denominator
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */

	public int setTagValueRational(int exiftag, int[] value) {
		synchronized (sLock) {
			return setTagValueRationalNative(mHandle,
					getExifTagSection(exiftag), getTagID(exiftag), value);
		}
	}

	/**
	 * Save the tag data into the JPEG file.
	 *
	 * @param filename the name of the new jpeg saved.
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	public int saveExifDataToFile(String filename) throws IOException {
		if (filename == null) {
			throw new IllegalArgumentException("filename cannot be null");
		}
		synchronized (sLock) {
			return saveExifDataToFileNative(mHandle, filename);
		}
	}

	/**
	 * Save the tag data into the old JPEG file.
	 *
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	public int saveExifDataToFile() throws IOException {
		synchronized (sLock) {
			return saveExifDataToFileNative(mHandle, mFileName);
		}
	}

	/**
	 * Save the tag data into the old JPEG file.
	 *
	 * @return will be EXIF_ENGINE_DONE_OK or others.
	 */
	public int saveExifDataToFD() throws IOException {
		synchronized (sLock) {
			return saveExifDataToFileFDNative(mHandle, mFD.getFd());
		}
	}

	/**
	 * Returns true if the JPEG file has a thumbnail.
	 */
	public boolean hasThumbnail() {
		synchronized (sLock) {
			return hasThumbnailNative(mHandle);
		}
	}

	/**
	 * Returns the thumbnail inside the JPEG file, or {@code null} if there is
	 * no thumbnail. The returned data is in JPEG format and can be decoded
	 * using
	 * {@link android.graphics.BitmapFactory#decodeByteArray(byte[], int, int)}
	 */
	public byte[] getThumbnail() {
		synchronized (sLock) {
			return getThumbnailNative(mHandle);
		}
	}

	/**
	 * Modify the thumbnail inside the JPEG file
	 *
	 * @param imgFileRawData JPEG raw file data
	 */
	public int modifyThumbnail(byte[] imgFileRawData) {
		synchronized (sLock) {
			return modifyThumbnailNative(mHandle, imgFileRawData);
		}
	}

	/**
	 * Returns true if the JPEG file has a has3DMacro.
	 */
	public boolean has3DMacro() {
		synchronized (sLock) {
			return has3DMacroNative(mHandle);
		}
	}

	/**
	 * Returns true if the JPEG file has a hasBokeh.
	 */
	public boolean hasBokeh() {
		synchronized (sLock) {
			return hasBokehNative(mHandle);
		}
	}

	/**
	 * Returns true if the JPEG file has a hasFace.
	 */
	public boolean hasFace() {
		synchronized (sLock) {
			return hasFaceNative(mHandle);
		}
	}

	/**
	 * Returns true if the JPEG file has a hasDepth.
	 */
	public boolean hasDepth() {
		synchronized (sLock) {
			return hasDepthNative(mHandle);
		}
	}

	/**
	 * Returns true if the JPEG file has a hasProcessed.
	 */
	public boolean hasProcessed() {
		synchronized (sLock) {
			return hasProcessedNative(mHandle);
		}
	}

	/**
	 * Returns true if the JPEG file has a hasPanorama.
	 */
	public boolean hasPanorama() {
		synchronized (sLock) {
			return hasPanoramaNative(mHandle);
		}
	}

	/**
	 * get custorm camera id
	 */
	public String getCameraID() {
		synchronized (sLock) {
			return getCameraIdNative(mHandle);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		freeExifDataNative(mHandle);
		mHandle = 0;
		super.finalize();
	}

	// Native Methods
	private native long readExifDataNative(String filename);

	private native long readExifDataFromFDNative(int fd);

	private native int getTagValueIntNative(long handle, int section, int exiftag, int defaultValue);

	/**
	 * @param appendRawData if true ,fetch raw exif tag data and append to the String
	 *                      Result
	 */
	private native String getTagValueStringNative(long handle, int section, int exiftag, boolean appendRawData);

	private native int[] getTagValueRationalNative(long handle, int section, int exiftag, int[] defaultValue);

	private native int setTagValueIntNative(long handle, int section, int exiftag, int value);

	private native int setTagValueStringNative(long handle, int section, int exiftag, String value);

	private native int setTagValueRationalNative(long handle, int section, int exiftag, int[] value);

	private native boolean hasThumbnailNative(long handle);

	private native byte[] getThumbnailNative(long handle);

	private native int saveExifDataToFileNative(long handle, String modifyfile);

	private native int saveExifDataToFileFDNative(long handle, int fd);

	private native int modifyThumbnailNative(long handle, byte[] thumbFileData);

	private native boolean has3DMacroNative(long handle);

	private native boolean hasBokehNative(long handle);

	private native boolean hasFaceNative(long handle);

	private native boolean hasDepthNative(long handle);

	private native boolean hasProcessedNative(long handle);

	private native boolean hasPanoramaNative(long handle);

	private native String getCameraIdNative(long handle);

	private native int freeExifDataNative(long handle);

	// --------add_shenkaige_start
	private final ExifAccessFilter mExifAccessFilter = new ExifAccessFilterBase();

	private final AttrsMap tempMap = new AttrsMap();
	protected final static String ENTRY_ATTRS_KV_PAIR_SPLITOR = "\n";
	protected final static String ENTRY_ATTRS_KV_LINKER = "=";

	private synchronized String onFetchedRawTagValueString(int exifTag,
														   String rawStr) {
		tempMap.reset(null);
		String defValue = null;
		if (rawStr != null && rawStr.length() > 0) {
			int defEndIndex = rawStr.indexOf(ENTRY_ATTRS_KV_PAIR_SPLITOR);
			if (defEndIndex > -1) {
				defValue = rawStr.substring(0, defEndIndex);
				tempMap.reset(rawStr);
			} else {
				defValue = rawStr;
			}
		}
		String newValue = mExifAccessFilter.onFilterExif(exifTag, defValue,
				tempMap, mLocale);
		if (newValue != null) {
			return newValue;
		} else {
			return defValue;
		}
	}

	class AttrsMap {
		private boolean inited = false;
		private String rawData;
		private final Map<String, String> map = new HashMap<String, String>();

		public int size() {
			if (!inited) {
				parseData();
			}
			return map.size();
		}

		public String get(String key) {
			if (!inited) {
				parseData();
			}
			return map.get(key);
		}

		public float optFloat(String key, float def) {
			String str = get(key);
			if (str == null || str.length() == 0) {
				return def;
			}
			try {
				float result = Float.parseFloat(str);
				if (result != Float.NaN) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return def;
		}

		public int optInt(String key, int def) {
			String str = get(key);
			if (str == null || str.length() == 0) {
				return def;
			}
			try {
				return Integer.parseInt(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return def;
		}

		public double optDouble(String key, double def) {
			String str = get(key);
			if (str == null || str.length() == 0) {
				return def;
			}
			try {
				double result = Double.parseDouble(str);
				if (result != Double.NaN) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return def;
		}

		void reset(String rawData) {
			map.clear();
			inited = false;
			this.rawData = rawData;
		}

		private void parseData() {
			map.clear();
			inited = true;
			//
			if (rawData == null || rawData.length() == 0) {
				return;
			}
			//
			String[] array = rawData.split(ENTRY_ATTRS_KV_PAIR_SPLITOR);
			if (array == null || array.length == 0) {
				return;
			}
			//
			for (String s : array) {
				if (s == null || s.length() == 0) {
					continue;
				}
				String[] kv = s.split(ENTRY_ATTRS_KV_LINKER);
				if (kv != null && kv.length >= 2 && kv[0] != null) {
					map.put(kv[0], kv[1]);
				}
			}
		}

		@Override
		public String toString() {
			if (!inited) {
				parseData();
			}
			return map.toString();
		}
	}

	public static final boolean DEBUG = true;
	// --------add_shenkaige_end
}
