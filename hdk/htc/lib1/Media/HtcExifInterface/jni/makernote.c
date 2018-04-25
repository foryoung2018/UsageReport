//--------------------------------------------------------------------------
// Parse some maker specific onformation.
// (Very limited right now - add maker specific stuff to this module)
//--------------------------------------------------------------------------
#include "jhead.h"
//add by maopei 0926 begin
extern int MotorolaOrder;
//add by maopei 0926 end
//add by maopei 0925 begin
#define HTC_MARKERNOTE_TAG_NUM          0x0000
#define HTC_MARKERNOTE_TAG_DUAL_CAM     0x0001
#define HTC_MARKERNOTE_TAG_VCM          0X0002
#define HTC_MARKERNOTE_TAG_FOCUS_AREA   0x0003
//add by maopei 0925 end
#define HTC_MARKERNOTE_TAG_CALIBRATION  0x0006  
#define HTC_MARKERNOTE_TAG_EX_FLAG      0x0007 
 
//Fengjen add for detect camera id
#define HTC_MARKERNOTE_TAG_CAMERAID     0x0010

//Harvey add for detect raw image
#define HTC_MAKERNOTE_TAG_RAWIMAGE      0x000A
//--------------------------------------------------------------------------
// Process exif format directory, as used by Cannon maker note
//--------------------------------------------------------------------------
void ProcessCanonMakerNoteDir(unsigned char * DirStart, unsigned char * OffsetBase, 
        unsigned ExifLength)
{
    int de;
    int a;
    int NumDirEntries;

    NumDirEntries = Get16u(DirStart);
    #define DIR_ENTRY_ADDR(Start, Entry) (Start+2+12*(Entry))

    {
        unsigned char * DirEnd;
        DirEnd = DIR_ENTRY_ADDR(DirStart, NumDirEntries);
        if (DirEnd > (OffsetBase+ExifLength)){
            ErrNonfatal("Illegally sized directory",0,0);
            return;
        }

        if (DumpExifMap){
            printf("Map: %05d-%05d: Directory (makernote)\n",DirStart-OffsetBase, DirEnd-OffsetBase);
        }
    }

    if (ShowTags){
        printf("(dir has %d entries)\n",NumDirEntries);
    }

    for (de=0;de<NumDirEntries;de++){
        int Tag, Format, Components;
        unsigned char * ValuePtr;
        int ByteCount;
        unsigned char * DirEntry;
        DirEntry = DIR_ENTRY_ADDR(DirStart, de);

        Tag = Get16u(DirEntry);
        Format = Get16u(DirEntry+2);
        Components = Get32u(DirEntry+4);

        if ((Format-1) >= NUM_FORMATS) {
            // (-1) catches illegal zero case as unsigned underflows to positive large.
            ErrNonfatal("Illegal number format %d for tag %04x", Format, Tag);
            continue;
        }

        if ((unsigned)Components > 0x10000){
            ErrNonfatal("Illegal number of components %d for tag %04x", Components, Tag);
            continue;
        }

        ByteCount = Components * BytesPerFormat[Format];

        if (ByteCount > 4){
            unsigned OffsetVal;
            OffsetVal = Get32u(DirEntry+8);
            // If its bigger than 4 bytes, the dir entry contains an offset.
            if (OffsetVal+ByteCount > ExifLength){
                // Bogus pointer offset and / or bytecount value
                ErrNonfatal("Illegal value pointer for tag %04x", Tag,0);
                continue;
            }
            ValuePtr = OffsetBase+OffsetVal;

            if (DumpExifMap){
                printf("Map: %05d-%05d:   Data for makernote tag %04x\n",OffsetVal, OffsetVal+ByteCount, Tag);
            }
        }else{
            // 4 bytes or less and value is in the dir entry itself
            ValuePtr = DirEntry+8;
        }

        if (ShowTags){
            // Show tag name
            printf("            Canon maker tag %04x Value = ", Tag);
        }

        // Show tag value.
        switch(Format){

            case FMT_UNDEFINED:
                // Undefined is typically an ascii string.

            case FMT_STRING:
                // String arrays printed without function call (different from int arrays)
                if (ShowTags){
                    printf("\"");
                    for (a=0;a<ByteCount;a++){
                        int ZeroSkipped = 0;
                        if (ValuePtr[a] >= 32){
                            if (ZeroSkipped){
                                printf("?");
                                ZeroSkipped = 0;
                            }
                            putchar(ValuePtr[a]);
                        }else{
                            if (ValuePtr[a] == 0){
                                ZeroSkipped = 1;
                            }
                        }
                    }
                    printf("\"\n");
                }
                break;

            default:
                if (ShowTags){
                    PrintFormatNumber(ValuePtr, Format, ByteCount);
                    printf("\n");
                }
        }
        if (Tag == 1 && Components > 16){
            int IsoCode = Get16u(ValuePtr + 16*sizeof(unsigned short));
            if (IsoCode >= 16 && IsoCode <= 24){
                ImageInfo.ISOequivalent = 50 << (IsoCode-16);
            } 
        }

        if (Tag == 4 && Format == FMT_USHORT){
            if (Components > 7){
                int WhiteBalance = Get16u(ValuePtr + 7*sizeof(unsigned short));
                switch(WhiteBalance){
                    // 0=Auto, 6=Custom
                    case 1: ImageInfo.LightSource = 1; break; // Sunny
                    case 2: ImageInfo.LightSource = 1; break; // Cloudy
                    case 3: ImageInfo.LightSource = 3; break; // Thungsten
                    case 4: ImageInfo.LightSource = 2; break; // Fourescent
                    case 5: ImageInfo.LightSource = 4; break; // Flash
                }
            }
            if (Components > 19 && ImageInfo.Distance <= 0) {
                // Inidcates the distance the autofocus camera is focused to.
                // Tends to be less accurate as distance increases.
                int temp_dist = Get16u(ValuePtr + 19*sizeof(unsigned short));
                printf("temp dist=%d\n",temp_dist);
                if (temp_dist != 65535){
                    ImageInfo.Distance = (float)temp_dist/100;
                }else{
                    ImageInfo.Distance = -1 /* infinity */;
                }
            }
        }
    }
}

//--------------------------------------------------------------------------
// Show generic maker note - just hex bytes.
//--------------------------------------------------------------------------
void ShowMakerNoteGeneric(unsigned char * ValuePtr, int ByteCount)
{
    int a;
    for (a=0;a<ByteCount;a++){
        if (a > 10){
            printf("...");
            break;
        }
        printf(" %02x",ValuePtr[a]);
    }
    printf(" (%d bytes)", ByteCount);
    printf("\n");
}

//add by maopei 0925 begin
void ProcessHTCMakerNoteDir(unsigned char * DirStart, unsigned char * OffsetBase, unsigned ExifLength)
{
    int de = 0;
    int a;
    int NumDirEntries;
    int Tag, Format, Components;
    unsigned char * ValuePtr;
    int ByteCount;
    unsigned char * DirEntry;
    unsigned char * DirEnd;

    //#define HTC_DIR_ENTRY_ADDR(Start, Entry) (Start+16*(Entry))

    DirEntry = DirStart;
    Tag = Get32u(DirEntry);
    Format = Get32u(DirEntry+4);
    Components = Get32u(DirEntry+8);

    if ((Format-1) >= NUM_FORMATS) {
        // (-1) catches illegal zero case as unsigned underflows to positive large.
        ErrNonfatal("Illegal number format %d for tag %04x", Format, Tag);
        return;
    }

    if ((unsigned)Components > 0x10000) {
        ErrNonfatal("Illegal number of components %d for tag %04x", Components, Tag);
        return;
    }
     
    ByteCount = Components * BytesPerFormat[Format];

    // 4 bytes or less and value is in the dir entry itself
    ValuePtr = DirEntry+12;
    if (Tag == HTC_MARKERNOTE_TAG_NUM) {
        ImageInfo.MakerNoteTagNum = ConvertAnyFormat(ValuePtr, Format);
        NumDirEntries = ConvertAnyFormat(ValuePtr, Format);
    } else {
        ErrNonfatal("Tag %04x is not htc makernote TAG_NUM (%04x)", Tag, HTC_MARKERNOTE_TAG_NUM);
        return;
    }

    DirEntry += (ByteCount+12);

    /*
    {
        unsigned char * DirEnd;
        //(+1) include HTC_MARKERNOTE_TAG_NUM
        DirEnd = HTC_DIR_ENTRY_ADDR(DirStart, NumDirEntries+1);
        if (DirEnd > (OffsetBase+ExifLength)){
            ErrNonfatal("Illegally sized directory",0,0);
            return;
        }

        if (DumpExifMap){
            printf("Map: %05d-%05d: Directory (makernote)\n",DirStart-OffsetBase, DirEnd-OffsetBase);
        }
    }
    */

    /*
    if (ShowTags){
        printf("(dir has %d entries)\n",NumDirEntries);
    }
    */

    for (de = 1; de < NumDirEntries+1; de++) {

        Tag = Get32u(DirEntry);
        Format = Get32u(DirEntry+4);
        Components = Get32u(DirEntry+8);

        if ((Format-1) >= NUM_FORMATS) {
            // (-1) catches illegal zero case as unsigned underflows to positive large.
            ErrNonfatal("Illegal number format %d for tag %04x", Format, Tag);
            continue;
        }

        if ((unsigned)Components > 0x10000) {
            ErrNonfatal("Illegal number of components %d for tag %04x", Components, Tag);
            continue;
        }

        ByteCount = Components * BytesPerFormat[Format];

        if (Tag == HTC_MARKERNOTE_TAG_CALIBRATION) {
            ByteCount = Components;
            //Workaround --- Camera has wrong design.  
            //It wrote format 4 but the format is "byte" actually.  
        }  

        // 4 bytes or less and value is in the dir entry itself
        ValuePtr = DirEntry+12;

        DirEnd = ValuePtr + ByteCount;  
        if (DirEnd > (OffsetBase + ExifLength)) {
            ErrNonfatal("Illegally sized directory", 0, 0);
            return;  
        }  


        if (ShowTags) {
            // Show tag name
            printf("            htc maker tag %04x Value = ", Tag);
        }

        // Show tag value.
        switch (Format) {

            case FMT_UNDEFINED:
                // Undefined is typically an ascii string.

            case FMT_STRING:
                // String arrays printed without function call (different from int arrays)
                if (ShowTags) {
                    printf("\"");
                    for (a = 0; a < ByteCount; a++) {
                        int ZeroSkipped = 0;
                        if (ValuePtr[a] >= 32) {
                            if (ZeroSkipped) {
                                printf("?");
                                ZeroSkipped = 0;
                            }
                            putchar(ValuePtr[a]);
                        } else {
                            if (ValuePtr[a] == 0) {
                                ZeroSkipped = 1;
                            }
                        }
                    }
                    printf("\"\n");
                }
                break;

            default:
                if (ShowTags) {
                    PrintFormatNumber(ValuePtr, Format, ByteCount);
                    printf("\n");
                }
        }

        // Extract useful components of tag
        switch (Tag) {
            case HTC_MARKERNOTE_TAG_DUAL_CAM:
                ImageInfo.DualCam = ConvertAnyFormat(ValuePtr, Format);
                ImageInfo.isMakerNote = 1;
                break;
            case HTC_MARKERNOTE_TAG_VCM:
                ImageInfo.Vcm = ConvertAnyFormat(ValuePtr, Format);
                break;
            case HTC_MARKERNOTE_TAG_FOCUS_AREA:
                if (Components < 4) {
                    ErrNonfatal("Too little Components (%d) for htc makernote tag %04x", Components, Tag);
                    break;
                }
                ImageInfo.FocusArea.x1 = ConvertAnyFormat(ValuePtr, Format);
                ImageInfo.FocusArea.y1 = ConvertAnyFormat(ValuePtr+4, Format);
                ImageInfo.FocusArea.x2 = ConvertAnyFormat(ValuePtr+8, Format);
                ImageInfo.FocusArea.y2 = ConvertAnyFormat(ValuePtr+12, Format);
                break;
            case HTC_MARKERNOTE_TAG_EX_FLAG:
                ImageInfo.DuoLensExFlag = ConvertAnyFormat(ValuePtr, Format);
                break;
            /* >> Fengjen add for detect camera id */
            case HTC_MARKERNOTE_TAG_CAMERAID:
                ImageInfo.CameraID = ConvertAnyFormat(ValuePtr, Format);
                break;
            /* << Fengjen add for detect camera id */

            /* >> Harvey add for detect raw image */
            case HTC_MAKERNOTE_TAG_RAWIMAGE: {
                ImageInfo.PhotolabExFlag = 0;
                int bit = ConvertAnyFormat(ValuePtr, Format);

                if ((bit & 0x00000001) == 0x00000001) {
                    ImageInfo.PhotolabExFlag |= 0x00000001;
                }
                if ((bit & 0x00000002) == 0x00000002) {
                    ImageInfo.PhotolabExFlag |= 0x00000002;
                }
                break;
            }
            /* << Harvey add for detect raw image */
        }
        DirEntry += (ByteCount + 12);
    }
}
//add by maopei 0925 end

/* >> Fengjen add for detect camera id */
void ProcessHTCMakerNoteDirByOrder(unsigned char * ValuePtr, unsigned char * OffsetBase, unsigned ExifLength, int order){

    static int MotorolaOrderSave;
    MotorolaOrderSave = MotorolaOrder;

    if (order == 0x49) {
        MotorolaOrder = 0; //little endian
    } else if (order == 0x4D) {
        MotorolaOrder = 1; //big endian
    } else {
        ErrNonfatal("htc makernote no little endian (%#0x)/big endian (%#0x) setting", 0x49, 0x4D);
        return;
    }

    ProcessHTCMakerNoteDir(ValuePtr, OffsetBase, ExifLength);
    MotorolaOrder = MotorolaOrderSave;
}
/* << Fengjen add for detect camera id */
//--------------------------------------------------------------------------
// Process maker note - to the limited extent that its supported.
//--------------------------------------------------------------------------
void ProcessMakerNote(unsigned char * ValuePtr, int ByteCount, 
        unsigned char * OffsetBase, unsigned ExifLength)
{
    if (strstr(ImageInfo.CameraMake, "Canon")) {
        ProcessCanonMakerNoteDir(ValuePtr, OffsetBase, ExifLength);
    //add by maopei 0926 begin
    } else if (ByteCount >= 4 && (ValuePtr[0] == 'h' && ValuePtr[1] == 't' && ValuePtr[2] == 'c')) {
    	ProcessHTCMakerNoteDirByOrder((ValuePtr+4), OffsetBase, ExifLength, ValuePtr[3]);
    //add by maopei 0926 end
    /* >> Fengjen add for detect camera id */
    } else if (ByteCount >= 10 && strncmp(ValuePtr,"cameraid_",9) == 0) {
    	ProcessHTCMakerNoteDirByOrder((ValuePtr+10), OffsetBase, ExifLength, ValuePtr[9]);
    /* << Fengjen add for detect camera id */
    } else {
        if (ShowTags) {
            ShowMakerNoteGeneric(ValuePtr, ByteCount);
        }
    }
}

