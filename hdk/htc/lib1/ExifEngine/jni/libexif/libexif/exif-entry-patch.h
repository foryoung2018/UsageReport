#ifndef __EXIF_ENTRY_PATCH_H__
#define __EXIF_ENTRY_PATCH_H__

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

#include <libexif/exif-entry.h>
#include <libexif/exif-content.h>
#include <libexif/exif-format.h>
#include <libexif/exif-mem.h>


/*! Initialize an empty #ExifEntry with default data in the correct format
 * for the given tag. If the entry is already initialized, this function
 * does nothing.
 * This call allocates memory for the \c data element of the given #ExifEntry.
 * That memory is freed at the same time as the #ExifEntry.
 *
 * \param[out] e entry to initialize
 * \param[in] tag tag number to initialize as
 * \param[in] mem memory alloc functions
 * \param[out] return initialize result state
 */
int exif_entry_patch_initialize(ExifEntry *e, ExifTag tag, ExifMem *mem);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __EXIF_ENTRY_PATCH_H__ */
