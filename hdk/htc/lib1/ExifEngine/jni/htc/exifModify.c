/**
 *
 * HTC Corporation Proprietary Rights Acknowledgment
 * Copyright (c) 2014 HTC Corporation
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the
 * Authorized User shall not use this work for any purpose other than the purpose
 * agreed by HTC.  Any and all addition or modification to this work shall be
 * unconditionally granted back to HTC and such addition or modification shall be
 * solely owned by HTC.  No right is granted under this statement, including but not
 * limited to, distribution, reproduction, and transmission, except as otherwise
 * provided in this statement.  Any other usage of this work shall be subject to the
 * further written consent of HTC.
 *
 */
//--------------------------------------------------------------------------
// JPEG markers consist of one or more 0xFF bytes, followed by a marker
//--------------------------------------------------------------------------

#include <stdio.h>
#include <time.h>
#include <ctype.h>
#include <unistd.h>
#include <utime.h>
#include <sys/stat.h>
#include <android/log.h>

#include "exifModify.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "IMGLIB", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "IMGLIB", __VA_ARGS__)

#define M_SOF0  0xC0          // Start Of Frame N
#define M_SOF1  0xC1          // N indicates which compression process
#define M_SOF2  0xC2          // Only SOF0-SOF2 are now in common use
#define M_SOF3  0xC3
#define M_SOF5  0xC5          // NB: codes C4 and CC are NOT SOF markers
#define M_SOF6  0xC6
#define M_SOF7  0xC7
#define M_SOF9  0xC9
#define M_SOF10 0xCA
#define M_SOF11 0xCB
#define M_SOF13 0xCD
#define M_SOF14 0xCE
#define M_SOF15 0xCF
#define M_SOI   0xD8          // Start Of Image (beginning of datastream)
#define M_EOI   0xD9          // End Of Image (end of datastream)
#define M_SOS   0xDA          // Start Of Scan (begins compressed data)
#define M_JFIF  0xE0          // Jfif marker
#define M_EXIF  0xE1          // Exif marker.  Also used for XMP data!
#define M_XMP   0x10E1        // Not a real tag (same value in file as Exif!)
#define M_COM   0xFE          // COMment
#define M_DQT   0xDB
#define M_DHT   0xC4
#define M_DRI   0xDD
#define M_IPTC  0xED          // IPTC marker

//--------------------------------------------------------------------------
typedef unsigned char uchar;

#ifndef TRUE
    #define TRUE 1
    #define FALSE 0
#endif

#ifdef _WIN32
    #define PATH_MAX _MAX_PATH
    #define SLASH '\\'
#else
    #define SLASH '/'
#endif

//--------------------------------------------------------------------------
// This structure is used to store jpeg file sections in memory.
typedef struct {
    uchar *  Data;
    int      Type;
    unsigned Offset;
    unsigned Size;
}Section_t;

// jpgfile.c functions
typedef enum {
    READ_METADATA = 1,
    READ_IMAGE = 2,
    READ_ALL = 3
}ReadMode_t;

//--------------------------------------------------------------------------
static Section_t * Sections = NULL;
static int SectionsAllocated;
static int SectionsRead;
static int HaveAll;

/* raw EXIF header data */
static const unsigned char exif_header[] = {
  0xff, 0xd8, 0xff, 0xe1
};
/* length of data in exif_header */
static const unsigned int exif_header_len = sizeof(exif_header);

#define PSEUDO_IMAGE_MARKER 0x123; // Extra value.

//--------------------------------------------------------------------------
// Check sections array to see if it needs to be increased in size.
//--------------------------------------------------------------------------
void CheckSectionsAllocated(void)
{
    if (SectionsRead > SectionsAllocated){
        LOGD("allocation screwup");
    }
    if (SectionsRead >= SectionsAllocated){
        SectionsAllocated += SectionsAllocated/2;
        Sections = (Section_t *)realloc(Sections, sizeof(Section_t)*SectionsAllocated);
		LOGD("sizeof(Section_t)*SectionsAllocated is %d", sizeof(Section_t)*SectionsAllocated);
        if (Sections == NULL){
            LOGE("could not allocate data for entire image");
        }
    }
}

//--------------------------------------------------------------------------
// Parse the marker stream until SOS or EOI is seen;
//--------------------------------------------------------------------------
int ReadJpegSections(FILE * infile, ReadMode_t ReadMode)
{
    int a;
    int HaveCom = FALSE;

    a = fgetc(infile);

    if (a != 0xff || fgetc(infile) != M_SOI){
        return FALSE;
    }
    for(;;) {
        int itemlen;
        int marker = 0;
        int ll,lh, got;
        uchar * Data;

        CheckSectionsAllocated();

        for (a=0;a<=16;a++){
            marker = fgetc(infile);
            if (marker != 0xff) break;

            if (a >= 16){
                LOGE("too many padding bytes");
                return FALSE;
            }
        }

        Sections[SectionsRead].Type = marker;
        Sections[SectionsRead].Offset = ftell(infile);
  
        // Read the length of the section.
        lh = fgetc(infile);
        ll = fgetc(infile);

        itemlen = (lh << 8) | ll;

        if (itemlen < 2){

			LOGE("invalid marker");
	        return FALSE;
        }

        Sections[SectionsRead].Size = itemlen;

        Data = (uchar *)malloc(itemlen);

        if (Data == NULL) {
    	    LOGE("Could not allocate memory");
	        return 0;
        }

        Sections[SectionsRead].Data = Data;

        // Store first two pre-read bytes.
        Data[0] = (uchar)lh;
        Data[1] = (uchar)ll;

        got = fread(Data+2, 1, itemlen-2, infile); // Read the whole section.
        if (got != itemlen-2){
            LOGE("Premature end of file?");
	        return FALSE;
        }
        SectionsRead += 1;

        switch (marker) {
            case M_SOS:   // stop before hitting compressed data 
                // If reading entire image is requested, read the rest of the data.
                if (ReadMode & READ_IMAGE) {
                    int cp, ep, size;
                    // Determine how much file is left.
                    cp = ftell(infile);
                    fseek(infile, 0, SEEK_END);
                    ep = ftell(infile);
                    fseek(infile, cp, SEEK_SET);

                    size = ep-cp;
                    Data = (uchar *)malloc(size);
                    if (Data == NULL) {
                        LOGE("could not allocate data for entire image");
    		            return FALSE;
                    }

                    got = fread(Data, 1, size, infile);
                    if (got != size) {
			            LOGE("could not read the rest of the image");
				        return FALSE;
                    }

                    CheckSectionsAllocated();
                    Sections[SectionsRead].Data = Data;
                    Sections[SectionsRead].Offset = cp;
                    Sections[SectionsRead].Size = size;
                    Sections[SectionsRead].Type = PSEUDO_IMAGE_MARKER;
                    SectionsRead ++;
                    HaveAll = 1;
                }
                return TRUE;

            case M_EOI:   // in case it's a tables-only JPEG stream
                LOGE("No image in jpeg!");
                return FALSE;

            case M_COM: // Comment section
                if (HaveCom || ((ReadMode & READ_METADATA) == 0)){
                    // Discard this section.
                    free(Sections[--SectionsRead].Data);
                } else {
                    HaveCom = TRUE;
                }
                break;

            case M_JFIF:
                // Regular jpegs always have this tag, exif images have the exif
                // marker instead, althogh ACDsee will write images with both markers.
                // this program will re-create this marker on absence of exif marker.
                // hence no need to keep the copy from the file.
                free(Sections[--SectionsRead].Data);
                break;

            case M_EXIF:
                // There can be different section using the same marker.
                if (ReadMode & READ_METADATA){
                    if (memcmp(Data+2, "Exif", 4) == 0){
                        break;
                    }else if (memcmp(Data+2, "http:", 5) == 0){
                        Sections[SectionsRead-1].Type = M_XMP; // Change tag for internal purposes.
                        break;
                    }
                }
                // Oterwise, discard this section.
                free(Sections[--SectionsRead].Data);
                break;

            case M_IPTC:
                if (ReadMode & READ_METADATA){
                    // Note: We just store the IPTC section.  Its relatively straightforward
                    // and we don't act on any part of it, so just display it at parse time.
                }else{
                    free(Sections[--SectionsRead].Data);
                }
                break;
           
            case M_SOF0: 
            case M_SOF1: 
            case M_SOF2: 
            case M_SOF3: 
            case M_SOF5: 
            case M_SOF6: 
            case M_SOF7: 
            case M_SOF9: 
            case M_SOF10:
            case M_SOF11:
            case M_SOF13:
            case M_SOF14:
            case M_SOF15:
                //process_SOFn(Data, marker);
                break;
            default:
                // Skip any other sections.
                break;
        }
    }
    return TRUE;
}

//--------------------------------------------------------------------------
// Discard read data.
//--------------------------------------------------------------------------
void DiscardData(void)
{
    int a;

    for (a=0;a<SectionsRead;a++){
        free(Sections[a].Data);
    }

    SectionsRead = 0;
    HaveAll = 0;
}

//--------------------------------------------------------------------------
// Read image data.
//--------------------------------------------------------------------------
int ReadJpegFileFD(const int fd, ReadMode_t ReadMode)
{
    FILE * infile;
    int ret;

    infile = fdopen(fd, "rb"); // Unix ignores 'b', windows needs it.

    if (infile == NULL) {
        LOGD("can't open %d", fd);
        return FALSE;
    }

    // Scan the JPEG headers.
    ret = ReadJpegSections(infile, ReadMode);
    if (!ret){
        LOGD("Cannot parse JPEG sections for file: %d", fd);
    }

    fclose(infile);

    if (ret == FALSE){
        DiscardData();
    }
    return ret;
}

//--------------------------------------------------------------------------
// Read image data.
//--------------------------------------------------------------------------
int ReadJpegFile(const char * FileName, ReadMode_t ReadMode)
{
    FILE * infile;
    int ret;

    infile = fopen(FileName, "rb"); // Unix ignores 'b', windows needs it.

    if (infile == NULL) {
        LOGD("can't open '%s'", FileName);
        return FALSE;
    }

    // Scan the JPEG headers.
    ret = ReadJpegSections(infile, ReadMode);
    if (!ret){
        LOGD("Cannot parse JPEG sections for file: %s", FileName);
    }

    fclose(infile);

    if (ret == FALSE){
        DiscardData();
    }
    return ret;
}

void saveJPGFile(const char* filename, const char* desfile, const ExifData* ed) {
    char backupName[400];
    struct stat buf;

    if (!desfile){
        strncpy(backupName, filename, 395);
        strcat(backupName, ".t");

        // Remove any .old file name that may pre-exist
        unlink(backupName);

        // Rename the old file.
        rename(filename, backupName);

        // Write the new file.
         if (WriteJpegFile(filename,ed)) {
            // Copy the access rights from original file
        	if (stat(backupName, &buf) == 0) {
            	// set Unix access rights and time to new file
        	    struct utimbuf mtime;
        	    chmod(filename, buf.st_mode);

        	    mtime.actime = buf.st_mtime;
        	    mtime.modtime = buf.st_mtime;

        	    utime(filename, &mtime);
    	}

        // Now that we are done, remove original file.
    	unlink(backupName);

        } else {
		    LOGE("WriteJpegFile failed, restoring from backup file");
        	// move back the backup file
        	rename(backupName, filename);
        }
    } else {
	    WriteJpegFile(desfile,ed);
    }
}

void saveJPGFD(const int fd, const ExifData *ed){
	WriteJpegFileFD(fd,ed);
}

//--------------------------------------------------------------------------
// Write image data back to fd.
//--------------------------------------------------------------------------
int WriteJpegFileFD(const int fd, const ExifData *ed)
{
	int ret = FALSE;
	FILE * outfile;
    outfile = fdopen(fd,"wb");
    if (outfile == NULL){
        LOGE("Could not open file for write");
        return FALSE;
    }
    ret = WriteJpegFileInternal(outfile,ed);
    fclose(outfile);
    return ret;
}

//--------------------------------------------------------------------------
// Write image data back to disk.
//--------------------------------------------------------------------------
int WriteJpegFile(const char * FileName, const ExifData *ed)
{
	int ret = 0;
	FILE * outfile;
    outfile = fopen(FileName,"wb");
    if (outfile == NULL){
        LOGE("Could not open file for write");
        return FALSE;
    }
    ret = WriteJpegFileInternal(outfile,ed);
    fclose(outfile);
    return ret;
}
//--------------------------------------------------------------------------
// Write image data back to disk.
//--------------------------------------------------------------------------
int WriteJpegFileInternal(const FILE * outfile, const ExifData *ed)
{
/*    FILE * outfile;*/
    int a;
    unsigned char *exif_data;
    unsigned int exif_data_len;

    if (!HaveAll){
        LOGE("Can't write back - didn't read all");
        return FALSE;
    }

    if (outfile == NULL){
        LOGE("Could not open file for write");
        return FALSE;
    }

	exif_data_save_data(ed, &exif_data, &exif_data_len);

	/* Write EXIF header */
	if (fwrite(exif_header, exif_header_len, 1, outfile) != 1) {
		LOGE("Error writing to file ");
		goto errout;
	}

	/* Write EXIF block length */
	if (fputc((exif_data_len+2) >> 8, outfile) < 0) {
		LOGE("Error writing to file \n");
		goto errout;
	}

	if (fputc((exif_data_len+2) & 0xff, outfile) < 0) {
		LOGE("Error writing to file \n");
		goto errout;
	}

	/* Write EXIF data block */
	if (fwrite(exif_data, exif_data_len, 1, outfile) != 1) {
		LOGE("Error writing to file \n");
		goto errout;
	}

    if (Sections[0].Type != M_EXIF && Sections[0].Type != M_JFIF) {
		a = 0;
    } else {
		a = 1;
    }

    int writeOk = FALSE;
    int nWrite = 0;
    // Write all the misc sections
    for (;a<SectionsRead-1;a++) {
        fputc(0xff,outfile);
        fputc((unsigned char)Sections[a].Type, outfile);
	    nWrite = fwrite(Sections[a].Data, 1, Sections[a].Size, outfile);
        writeOk = (nWrite == Sections[a].Size);
        if(!writeOk){
            LOGD("write section %d failed expect %d actual %d",a,Sections[a].Size,nWrite);
            break;
        }
    }

    // Write the remaining image data.
    if (writeOk){
        nWrite = fwrite(Sections[a].Data, 1,Sections[a].Size, outfile);
		writeOk = (nWrite == Sections[a].Size);
        if (!writeOk) {
            LOGD("write section %d failed expect %d actual %d",a,Sections[a].Size,nWrite);
        }
    }
       
    return writeOk;

errout:
        exif_data_unref(ed);
        return FALSE;
}

void ResetJpgfile(void)
{
    if (Sections == NULL){
        Sections = (Section_t *)malloc(sizeof(Section_t)*5);
        SectionsAllocated = 5;
    }

    SectionsRead = 0;
    HaveAll = 0;
}

int ModifyExifData(const char * filePath, const ExifData * ed)
{
    int ret = FALSE;
    if (filePath) {
	    ReadMode_t ReadMode = READ_METADATA;
	    ReadMode |= READ_IMAGE;

        ResetJpgfile();
	    if (ReadJpegFile(filePath, ReadMode)) {
            saveJPGFile(filePath, 0, ed);
            DiscardData();
	        ret = TRUE;
	    }
    }

    return ret;
}

int ModifyExifDataFD(const int fd, const ExifData * ed)
{
    int ret = FALSE;
    if (fd>0) {
	    ReadMode_t ReadMode = READ_METADATA;
	    ReadMode |= READ_IMAGE;

        ResetJpgfile();
	    if (ReadJpegFileFD(fd, ReadMode)) {
	    	saveJPGFD(fd, ed);
            DiscardData();
	        ret = TRUE;
	    }
    }

    return ret;
}



