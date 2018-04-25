/*
 * Copyright 2011 Sebastian Annies, Hamburg
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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.srt;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks.TextTrackImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Parses a .srt file and creates a Track for it.
 * @hide
 */
public class SrtParser {
	
	/**
     * @hide
     */
    public static TextTrackImpl parse(InputStream is) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        TextTrackImpl track = new TextTrackImpl();
        String numberString;
        while ((numberString = r.readLine()) != null) {
            String timeString = r.readLine();
            StringBuilder lineString = new StringBuilder();
            String s;
            while (!((s = r.readLine()) == null || s.trim().equals(""))) {
                lineString.append(s + "\n");
            }            
            final long startTime;
            final long endTime;
            
            if (timeString != null) {
            	startTime = parse(timeString.split("-->")[0]);
                endTime = parse(timeString.split("-->")[1]);
            } else {
            	startTime = 0;
            	endTime = 0;
            	android.util.Log.e(SrtParser.class.getName(), "timeString is null");
            }
            track.getSubs().add(new TextTrackImpl.Line(startTime, endTime, lineString.toString()));

        }
        return track;
    }

    private static long parse(String in) {
        long hours = Long.parseLong(in.split(":")[0].trim());
        long minutes = Long.parseLong(in.split(":")[1].trim());
        long seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim());
        long millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());

        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;

    }
}
