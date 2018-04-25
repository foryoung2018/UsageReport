/*
Copyright (c) 2011 Stanislav Vitvitskiy

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.h264.model;
/**
 * @hide
 */
public class VUIParameters {
	/**
     * @hide
     */
    public static class BitstreamRestriction {

    	/**
         * @hide
         */
        public boolean motion_vectors_over_pic_boundaries_flag;
        
        /**
         * @hide
         */
        public int max_bytes_per_pic_denom;
        
        /**
         * @hide
         */
        public int max_bits_per_mb_denom;
        
        /**
         * @hide
         */
        public int log2_max_mv_length_horizontal;
        
        /**
         * @hide
         */
        public int log2_max_mv_length_vertical;
        
        /**
         * @hide
         */
        public int num_reorder_frames;
        
        /**
         * @hide
         */
        public int max_dec_frame_buffering;

    }

    /**
     * @hide
     */
    public boolean aspect_ratio_info_present_flag;
    
    /**
     * @hide
     */
    public int sar_width;
    
    /**
     * @hide
     */
    public int sar_height;
    
    /**
     * @hide
     */
    public boolean overscan_info_present_flag;
    
    /**
     * @hide
     */
    public boolean overscan_appropriate_flag;
    
    /**
     * @hide
     */
    public boolean video_signal_type_present_flag;
    
    /**
     * @hide
     */
    public int video_format;
    
    /**
     * @hide
     */
    public boolean video_full_range_flag;
    
    /**
     * @hide
     */
    public boolean colour_description_present_flag;
    
    /**
     * @hide
     */
    public int colour_primaries;
    
    /**
     * @hide
     */
    public int transfer_characteristics;
    
    /**
     * @hide
     */
    public int matrix_coefficients;
    
    /**
     * @hide
     */
    public boolean chroma_loc_info_present_flag;
    
    /**
     * @hide
     */
    public int chroma_sample_loc_type_top_field;
    
    /**
     * @hide
     */
    public int chroma_sample_loc_type_bottom_field;
    
    /**
     * @hide
     */
    public boolean timing_info_present_flag;
    
    /**
     * @hide
     */
    public int num_units_in_tick;
    
    /**
     * @hide
     */
    public int time_scale;
    
    /**
     * @hide
     */
    public boolean fixed_frame_rate_flag;
    
    /**
     * @hide
     */
    public boolean low_delay_hrd_flag;
    
    /**
     * @hide
     */
    public boolean pic_struct_present_flag;
    
    /**
     * @hide
     */
    public HRDParameters nalHRDParams;
    
    /**
     * @hide
     */
    public HRDParameters vclHRDParams;

    /**
     * @hide
     */
    public BitstreamRestriction bitstreamRestriction;
    
    /**
     * @hide
     */
    public AspectRatio aspect_ratio;

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "VUIParameters{" + "\n" +
                "aspect_ratio_info_present_flag=" + aspect_ratio_info_present_flag + "\n" +
                ", sar_width=" + sar_width + "\n" +
                ", sar_height=" + sar_height + "\n" +
                ", overscan_info_present_flag=" + overscan_info_present_flag + "\n" +
                ", overscan_appropriate_flag=" + overscan_appropriate_flag + "\n" +
                ", video_signal_type_present_flag=" + video_signal_type_present_flag + "\n" +
                ", video_format=" + video_format + "\n" +
                ", video_full_range_flag=" + video_full_range_flag + "\n" +
                ", colour_description_present_flag=" + colour_description_present_flag + "\n" +
                ", colour_primaries=" + colour_primaries + "\n" +
                ", transfer_characteristics=" + transfer_characteristics + "\n" +
                ", matrix_coefficients=" + matrix_coefficients + "\n" +
                ", chroma_loc_info_present_flag=" + chroma_loc_info_present_flag + "\n" +
                ", chroma_sample_loc_type_top_field=" + chroma_sample_loc_type_top_field + "\n" +
                ", chroma_sample_loc_type_bottom_field=" + chroma_sample_loc_type_bottom_field + "\n" +
                ", timing_info_present_flag=" + timing_info_present_flag + "\n" +
                ", num_units_in_tick=" + num_units_in_tick + "\n" +
                ", time_scale=" + time_scale + "\n" +
                ", fixed_frame_rate_flag=" + fixed_frame_rate_flag + "\n" +
                ", low_delay_hrd_flag=" + low_delay_hrd_flag + "\n" +
                ", pic_struct_present_flag=" + pic_struct_present_flag + "\n" +
                ", nalHRDParams=" + nalHRDParams + "\n" +
                ", vclHRDParams=" + vclHRDParams + "\n" +
                ", bitstreamRestriction=" + bitstreamRestriction + "\n" +
                ", aspect_ratio=" + aspect_ratio + "\n" +
                '}';
    }
}
