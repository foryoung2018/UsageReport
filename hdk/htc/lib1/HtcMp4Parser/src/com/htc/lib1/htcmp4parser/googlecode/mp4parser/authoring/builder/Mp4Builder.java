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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;

/**
 * Transforms a <code>Movie</code> object to an IsoFile. Implementations can
 * determine the specific format: Fragmented MP4, MP4, MP4 with Apple Metadata,
 * MP4 with 3GPP Metadata, MOV.
 * @hide
 * {@exthide}
 */
public interface Mp4Builder {
    /**
     * Builds the actual IsoFile from the Movie.
     *
     * @param movie data source
     * @return the freshly built IsoFile
     * @hide
     */
    public IsoFile build(Movie movie);

}
