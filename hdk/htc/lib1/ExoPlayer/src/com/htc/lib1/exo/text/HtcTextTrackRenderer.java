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
package com.htc.lib1.exo.text;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.os.Looper;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.text.TextRenderer;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.SampleSource;

import com.htc.lib1.exo.utilities.LOG;


/**
 * Decodes and renders video using {@link MediaCodec}.
 */
@TargetApi(16)
public class HtcTextTrackRenderer extends TextTrackRenderer {

    private static final String TAG = "HtcMediaCodecVideoTrackRenderer";

    public HtcTextTrackRenderer(SampleSource source, TextRenderer textRenderer,
      Looper textRendererLooper, SubtitleParser... subtitleParsers) {
      super(source, textRenderer, textRendererLooper, subtitleParsers);
    }
    @Override
	public boolean isReady()
    {
        boolean rtn = super.isReady();

        if (rtn == false)
        {
            LOG.I(TAG , "isReady() = " + rtn);
        }
        return rtn;
    }
}
