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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder.smoothstreaming;


import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;

import java.io.IOException;
/**
 * @hide
 * {@exthide}
 */
public interface ManifestWriter {
    /**
     * @hide
     * @param inputs
     * @return
     * @throws IOException
     */
    String getManifest(Movie inputs) throws IOException;
    /**
     * @hide
     * @param track
     * @return
     */
    long getBitrate(Track track);
    /**
     * @hide
     * @param track
     * @param movie
     * @return
     */
    long[] calculateFragmentDurations(Track track, Movie movie);

}
