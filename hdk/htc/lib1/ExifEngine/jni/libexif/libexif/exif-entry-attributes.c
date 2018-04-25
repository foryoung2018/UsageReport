#include <libexif/exif-entry-attributes.h>
#include <stdlib.h>

int addKeyValueString(char** buf, int bufLen, const char* key,
		const char* value) {
	// check to see if buf has enough room to append
	int len = strlen(KEY_VALUE_SPLITOR) + strlen(key) + strlen(KEY_VALUE_LINKER)
			+ strlen(value);
	int newLen = strlen(*buf) + len;
	if (newLen >= bufLen) {
		bufLen = newLen + 100;
		*buf = realloc(*buf, bufLen);
		if (*buf == NULL) {
			return 0;
		}
	}
	// append the new attribute and value
	if (strlen(*buf) > 0) {
		snprintf(*buf + strlen(*buf), bufLen, "%s%s%s%s", KEY_VALUE_SPLITOR,
				key, KEY_VALUE_LINKER, value);
	} else {
		snprintf(*buf, bufLen, "%s%s%s", key, KEY_VALUE_LINKER, value);
	}
	return bufLen;
}

// returns new buffer length
int addKeyValueInt(char** buf, int bufLen, const char* key, int value) {
	char valueStr[20];
	snprintf(valueStr, 20, "%d", value);
	return addKeyValueString(buf, bufLen, key, valueStr);
}

// returns new buffer length
int addKeyValueDouble(char** buf, int bufLen, const char* key, double value,
		const char* format) {
	char valueStr[30];
	snprintf(valueStr, 30, format, value);
	return addKeyValueString(buf, bufLen, key, valueStr);
}
