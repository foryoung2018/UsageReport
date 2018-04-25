package com.htc.lib1.cc.fontstyle.test;

import android.content.Intent;
import android.widget.TextView;

import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.NoRunOrientation;
import com.htc.test.util.ScreenShotUtil;

public class FontStyleTest extends HtcActivityTestCaseBase {

    private TextView tv;

    public FontStyleTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.fontstyle.activityhelper.MainActivity"));
    }

    private void assertFontStyle(String styleName) {

        assertNotNull("styleName should not be null", styleName);
        int styleId = getInstrumentation().getTargetContext().getResources()
                .getIdentifier(styleName, "style", "com.htc.lib1.cc.fontstyle.activityhelper");
        assertTrue("styleId should not be 0", 0 != styleId);

        Intent intent = new Intent();
        intent.putExtra("styleName", styleName);
        intent.putExtra("styleId", styleId);
        setActivityIntent(intent);
        initActivity();
        tv = (TextView) mSolo.getView("tv");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, tv, this);
    }

    @NoRunOrientation
    public void testFontStyle_title_primary_xs() {
        assertFontStyle("title_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_title_primary_s() {
        assertFontStyle("title_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_title_primary_m() {
        assertFontStyle("title_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_title_primary_l() {
        assertFontStyle("title_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_title_primary_xl() {
        assertFontStyle("title_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_title_secondary_xs() {
        assertFontStyle("title_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_title_secondary_s() {
        assertFontStyle("title_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_title_secondary_m() {
        assertFontStyle("title_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_title_secondary_l() {
        assertFontStyle("title_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_title_secondary_xl() {
        assertFontStyle("title_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_button_primary_xs() {
        assertFontStyle("button_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_b_button_primary_xs() {
        assertFontStyle("b_button_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_button_primary_s() {
        assertFontStyle("button_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_b_button_primary_s() {
        assertFontStyle("b_button_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_button_primary_m() {
        assertFontStyle("button_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_b_button_primary_m() {
        assertFontStyle("b_button_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_button_primary_l() {
        assertFontStyle("button_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_b_button_primary_l() {
        assertFontStyle("b_button_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_button_primary_xl() {
        assertFontStyle("button_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_b_button_primary_xl() {
        assertFontStyle("b_button_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xxs() {
        assertFontStyle("list_primary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xxs() {
        assertFontStyle("darklist_primary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xxs_bold() {
        assertFontStyle("list_primary_xxs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xxs_bold() {
        assertFontStyle("darklist_primary_xxs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xs() {
        assertFontStyle("list_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xs() {
        assertFontStyle("darklist_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xs_bold() {
        assertFontStyle("list_primary_xs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xs_bold() {
        assertFontStyle("darklist_primary_xs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_s() {
        assertFontStyle("list_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_s() {
        assertFontStyle("darklist_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_s_bold() {
        assertFontStyle("list_primary_s_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_s_bold() {
        assertFontStyle("darklist_primary_s_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_m() {
        assertFontStyle("list_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_m() {
        assertFontStyle("darklist_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_m_bold() {
        assertFontStyle("list_primary_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_m_bold() {
        assertFontStyle("darklist_primary_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_l() {
        assertFontStyle("list_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_l() {
        assertFontStyle("darklist_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_l_bold() {
        assertFontStyle("list_primary_l_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_l_bold() {
        assertFontStyle("darklist_primary_l_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xl() {
        assertFontStyle("list_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xl() {
        assertFontStyle("darklist_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_list_primary_xl_bold() {
        assertFontStyle("list_primary_xl_bold");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_primary_xl_bold() {
        assertFontStyle("darklist_primary_xl_bold");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_xxs() {
        assertFontStyle("list_secondary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_xxs() {
        assertFontStyle("darklist_secondary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_xs() {
        assertFontStyle("list_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_xs() {
        assertFontStyle("darklist_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_s() {
        assertFontStyle("list_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_s() {
        assertFontStyle("darklist_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_m() {
        assertFontStyle("list_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_m() {
        assertFontStyle("darklist_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_l() {
        assertFontStyle("list_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_l() {
        assertFontStyle("darklist_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary_xl() {
        assertFontStyle("list_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary_xl() {
        assertFontStyle("darklist_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_primary_xs() {
        assertFontStyle("list_body_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_primary_s() {
        assertFontStyle("list_body_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_primary_m() {
        assertFontStyle("list_body_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_primary_l() {
        assertFontStyle("list_body_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_primary_xl() {
        assertFontStyle("list_body_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_secondary_xs() {
        assertFontStyle("list_body_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_secondary_s() {
        assertFontStyle("list_body_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_secondary_m() {
        assertFontStyle("list_body_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_secondary_l() {
        assertFontStyle("list_body_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_secondary_xl() {
        assertFontStyle("list_body_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_input_default_xs() {
        assertFontStyle("input_default_xs");
    }

    @NoRunOrientation
    public void testFontStyle_input_default_s() {
        assertFontStyle("input_default_s");
    }

    @NoRunOrientation
    public void testFontStyle_input_default_m() {
        assertFontStyle("input_default_m");
    }

    @NoRunOrientation
    public void testFontStyle_input_default_l() {
        assertFontStyle("input_default_l");
    }

    @NoRunOrientation
    public void testFontStyle_input_default_xl() {
        assertFontStyle("input_default_xl");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_xs() {
        assertFontStyle("list_body_xs");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_s() {
        assertFontStyle("list_body_s");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_m() {
        assertFontStyle("list_body_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_l() {
        assertFontStyle("list_body_l");
    }

    @NoRunOrientation
    public void testFontStyle_list_body_xl() {
        assertFontStyle("list_body_xl");
    }

    @NoRunOrientation
    public void testFontStyle_info_primary_xs() {
        assertFontStyle("info_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_info_primary_s() {
        assertFontStyle("info_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_info_primary_m() {
        assertFontStyle("info_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_info_primary_l() {
        assertFontStyle("info_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_info_primary_xl() {
        assertFontStyle("info_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_primary_focused_s() {
        assertFontStyle("primary_focused_s");
    }

    @NoRunOrientation
    public void testFontStyle_primary_focused_m() {
        assertFontStyle("primary_focused_m");
    }

    @NoRunOrientation
    public void testFontStyle_primary_focused_m_bold() {
        assertFontStyle("primary_focused_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_separator_primary_xs() {
        assertFontStyle("separator_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_primary_xs() {
        assertFontStyle("b_separator_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_separator_primary_s() {
        assertFontStyle("separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_primary_s() {
        assertFontStyle("b_separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_separator_primary_m() {
        assertFontStyle("separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_primary_m() {
        assertFontStyle("b_separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_separator_primary_l() {
        assertFontStyle("separator_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_primary_l() {
        assertFontStyle("b_separator_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_separator_primary_xl() {
        assertFontStyle("separator_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_primary_xl() {
        assertFontStyle("b_separator_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_separator_secondary_xs() {
        assertFontStyle("separator_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_secondary_xs() {
        assertFontStyle("b_separator_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_separator_secondary_s() {
        assertFontStyle("separator_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_secondary_s() {
        assertFontStyle("b_separator_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_separator_secondary_m() {
        assertFontStyle("separator_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_secondary_m() {
        assertFontStyle("b_separator_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_separator_secondary_l() {
        assertFontStyle("separator_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_secondary_l() {
        assertFontStyle("b_separator_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_separator_secondary_xl() {
        assertFontStyle("separator_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_b_separator_secondary_xl() {
        assertFontStyle("b_separator_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_notification_info_xs() {
        assertFontStyle("notification_info_xs");
    }

    @NoRunOrientation
    public void testFontStyle_notification_info_s() {
        assertFontStyle("notification_info_s");
    }

    @NoRunOrientation
    public void testFontStyle_notification_info_m() {
        assertFontStyle("notification_info_m");
    }

    @NoRunOrientation
    public void testFontStyle_label_off_m() {
        assertFontStyle("label_off_m");
    }

    @NoRunOrientation
    public void testFontStyle_label_on_m() {
        assertFontStyle("label_on_m");
    }

    @NoRunOrientation
    public void testFontStyle_toggle_primary_m() {
        assertFontStyle("toggle_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_b_toggle_primary_m() {
        assertFontStyle("b_toggle_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_random_access_m() {
        assertFontStyle("random_access_m");
    }

    @NoRunOrientation
    public void testFontStyle_random_access_l() {
        assertFontStyle("random_access_l");
    }

    @NoRunOrientation
    public void testFontStyle_time_info_m() {
        assertFontStyle("time_info_m");
    }

    @NoRunOrientation
    public void testFontStyle_list_secondary() {
        assertFontStyle("list_secondary");
    }

    @NoRunOrientation
    public void testFontStyle_darklist_secondary() {
        assertFontStyle("darklist_secondary");
    }

    @NoRunOrientation
    public void testFontStyle_time_pick_primary_xs() {
        assertFontStyle("time_pick_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_time_pick_primary_s() {
        assertFontStyle("time_pick_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_time_pick_primary_m() {
        assertFontStyle("time_pick_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_shortcut_label_m() {
        assertFontStyle("shortcut_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_shortcut_label_l() {
        assertFontStyle("shortcut_label_l");
    }

    @NoRunOrientation
    public void testFontStyle_source_label_m() {
        assertFontStyle("source_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_dark_source_label_m() {
        assertFontStyle("dark_source_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_title_primary_s() {
        assertFontStyle("automotive_title_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_title_primary_m() {
        assertFontStyle("automotive_title_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_title_secondary_m() {
        assertFontStyle("automotive_title_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_primary_xs() {
        assertFontStyle("automotive_darklist_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_primary_s() {
        assertFontStyle("automotive_darklist_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_primary_m() {
        assertFontStyle("automotive_darklist_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_secondary_xs() {
        assertFontStyle("automotive_darklist_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_secondary_s() {
        assertFontStyle("automotive_darklist_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_secondary_m() {
        assertFontStyle("automotive_darklist_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_darklist_secondary_l() {
        assertFontStyle("automotive_darklist_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_input_default_m() {
        assertFontStyle("automotive_input_default_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_b_separator_primary_m() {
        assertFontStyle("automotive_b_separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_b_separator_primary_s() {
        assertFontStyle("automotive_b_separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_list_body_primary_m() {
        assertFontStyle("automotive_list_body_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_random_access_s() {
        assertFontStyle("automotive_random_access_s");
    }

    @NoRunOrientation
    public void testFontStyle_automotive_random_access_m() {
        assertFontStyle("automotive_random_access_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_primary_xs() {
        assertFontStyle("fixed_title_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_primary_s() {
        assertFontStyle("fixed_title_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_primary_m() {
        assertFontStyle("fixed_title_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_primary_l() {
        assertFontStyle("fixed_title_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_primary_xl() {
        assertFontStyle("fixed_title_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_secondary_xs() {
        assertFontStyle("fixed_title_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_secondary_s() {
        assertFontStyle("fixed_title_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_secondary_m() {
        assertFontStyle("fixed_title_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_secondary_l() {
        assertFontStyle("fixed_title_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_title_secondary_xl() {
        assertFontStyle("fixed_title_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_button_primary_xs() {
        assertFontStyle("fixed_button_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_button_primary_xs() {
        assertFontStyle("fixed_b_button_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_button_primary_s() {
        assertFontStyle("fixed_button_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_button_primary_s() {
        assertFontStyle("fixed_b_button_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_button_primary_m() {
        assertFontStyle("fixed_button_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_button_primary_m() {
        assertFontStyle("fixed_b_button_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_button_primary_l() {
        assertFontStyle("fixed_button_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_button_primary_l() {
        assertFontStyle("fixed_b_button_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_button_primary_xl() {
        assertFontStyle("fixed_button_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_button_primary_xl() {
        assertFontStyle("fixed_b_button_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xxs() {
        assertFontStyle("fixed_list_primary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xxs() {
        assertFontStyle("fixed_darklist_primary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xxs_bold() {
        assertFontStyle("fixed_list_primary_xxs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xxs_bold() {
        assertFontStyle("fixed_darklist_primary_xxs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xs() {
        assertFontStyle("fixed_list_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xs() {
        assertFontStyle("fixed_darklist_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xs_bold() {
        assertFontStyle("fixed_list_primary_xs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xs_bold() {
        assertFontStyle("fixed_darklist_primary_xs_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_s() {
        assertFontStyle("fixed_list_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_s() {
        assertFontStyle("fixed_darklist_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_s_bold() {
        assertFontStyle("fixed_list_primary_s_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_s_bold() {
        assertFontStyle("fixed_darklist_primary_s_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_m() {
        assertFontStyle("fixed_list_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_m() {
        assertFontStyle("fixed_darklist_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_m_bold() {
        assertFontStyle("fixed_list_primary_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_m_bold() {
        assertFontStyle("fixed_darklist_primary_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_l() {
        assertFontStyle("fixed_list_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_l() {
        assertFontStyle("fixed_darklist_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_l_bold() {
        assertFontStyle("fixed_list_primary_l_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_l_bold() {
        assertFontStyle("fixed_darklist_primary_l_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xl() {
        assertFontStyle("fixed_list_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xl() {
        assertFontStyle("fixed_darklist_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_primary_xl_bold() {
        assertFontStyle("fixed_list_primary_xl_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_primary_xl_bold() {
        assertFontStyle("fixed_darklist_primary_xl_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_xxs() {
        assertFontStyle("fixed_list_secondary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_xxs() {
        assertFontStyle("fixed_darklist_secondary_xxs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_xs() {
        assertFontStyle("fixed_list_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_xs() {
        assertFontStyle("fixed_darklist_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_s() {
        assertFontStyle("fixed_list_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_s() {
        assertFontStyle("fixed_darklist_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_m() {
        assertFontStyle("fixed_list_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_m() {
        assertFontStyle("fixed_darklist_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_l() {
        assertFontStyle("fixed_list_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_l() {
        assertFontStyle("fixed_darklist_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary_xl() {
        assertFontStyle("fixed_list_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary_xl() {
        assertFontStyle("fixed_darklist_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_primary_xs() {
        assertFontStyle("fixed_list_body_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_primary_s() {
        assertFontStyle("fixed_list_body_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_primary_m() {
        assertFontStyle("fixed_list_body_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_primary_l() {
        assertFontStyle("fixed_list_body_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_primary_xl() {
        assertFontStyle("fixed_list_body_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_secondary_xs() {
        assertFontStyle("fixed_list_body_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_secondary_s() {
        assertFontStyle("fixed_list_body_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_secondary_m() {
        assertFontStyle("fixed_list_body_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_secondary_l() {
        assertFontStyle("fixed_list_body_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_secondary_xl() {
        assertFontStyle("fixed_list_body_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_input_default_xs() {
        assertFontStyle("fixed_input_default_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_input_default_s() {
        assertFontStyle("fixed_input_default_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_input_default_m() {
        assertFontStyle("fixed_input_default_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_input_default_l() {
        assertFontStyle("fixed_input_default_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_input_default_xl() {
        assertFontStyle("fixed_input_default_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_xs() {
        assertFontStyle("fixed_list_body_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_s() {
        assertFontStyle("fixed_list_body_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_m() {
        assertFontStyle("fixed_list_body_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_l() {
        assertFontStyle("fixed_list_body_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_body_xl() {
        assertFontStyle("fixed_list_body_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_info_primary_xs() {
        assertFontStyle("fixed_info_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_info_primary_s() {
        assertFontStyle("fixed_info_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_info_primary_m() {
        assertFontStyle("fixed_info_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_info_primary_l() {
        assertFontStyle("fixed_info_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_info_primary_xl() {
        assertFontStyle("fixed_info_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_primary_focused_s() {
        assertFontStyle("fixed_primary_focused_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_primary_focused_m() {
        assertFontStyle("fixed_primary_focused_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_primary_focused_m_bold() {
        assertFontStyle("fixed_primary_focused_m_bold");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_primary_xs() {
        assertFontStyle("fixed_separator_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_primary_xs() {
        assertFontStyle("fixed_b_separator_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_primary_s() {
        assertFontStyle("fixed_separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_primary_s() {
        assertFontStyle("fixed_b_separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_primary_m() {
        assertFontStyle("fixed_separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_primary_m() {
        assertFontStyle("fixed_b_separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_primary_l() {
        assertFontStyle("fixed_separator_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_primary_l() {
        assertFontStyle("fixed_b_separator_primary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_primary_xl() {
        assertFontStyle("fixed_separator_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_primary_xl() {
        assertFontStyle("fixed_b_separator_primary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_secondary_xs() {
        assertFontStyle("fixed_separator_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_secondary_xs() {
        assertFontStyle("fixed_b_separator_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_secondary_s() {
        assertFontStyle("fixed_separator_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_secondary_s() {
        assertFontStyle("fixed_b_separator_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_secondary_m() {
        assertFontStyle("fixed_separator_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_secondary_m() {
        assertFontStyle("fixed_b_separator_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_secondary_l() {
        assertFontStyle("fixed_separator_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_secondary_l() {
        assertFontStyle("fixed_b_separator_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_separator_secondary_xl() {
        assertFontStyle("fixed_separator_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_separator_secondary_xl() {
        assertFontStyle("fixed_b_separator_secondary_xl");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_notification_info_xs() {
        assertFontStyle("fixed_notification_info_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_notification_info_s() {
        assertFontStyle("fixed_notification_info_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_notification_info_m() {
        assertFontStyle("fixed_notification_info_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_label_off_m() {
        assertFontStyle("fixed_label_off_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_label_on_m() {
        assertFontStyle("fixed_label_on_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_toggle_primary_m() {
        assertFontStyle("fixed_toggle_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_b_toggle_primary_m() {
        assertFontStyle("fixed_b_toggle_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_random_access_m() {
        assertFontStyle("fixed_random_access_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_random_access_l() {
        assertFontStyle("fixed_random_access_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_time_info_m() {
        assertFontStyle("fixed_time_info_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_list_secondary() {
        assertFontStyle("fixed_list_secondary");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_darklist_secondary() {
        assertFontStyle("fixed_darklist_secondary");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_time_pick_primary_xs() {
        assertFontStyle("fixed_time_pick_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_time_pick_primary_s() {
        assertFontStyle("fixed_time_pick_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_time_pick_primary_m() {
        assertFontStyle("fixed_time_pick_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_shortcut_label_m() {
        assertFontStyle("fixed_shortcut_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_shortcut_label_l() {
        assertFontStyle("fixed_shortcut_label_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_source_label_m() {
        assertFontStyle("fixed_source_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_dark_source_label_m() {
        assertFontStyle("fixed_dark_source_label_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_title_primary_s() {
        assertFontStyle("fixed_automotive_title_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_title_primary_m() {
        assertFontStyle("fixed_automotive_title_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_title_secondary_m() {
        assertFontStyle("fixed_automotive_title_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_primary_xs() {
        assertFontStyle("fixed_automotive_darklist_primary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_primary_s() {
        assertFontStyle("fixed_automotive_darklist_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_primary_m() {
        assertFontStyle("fixed_automotive_darklist_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_secondary_xs() {
        assertFontStyle("fixed_automotive_darklist_secondary_xs");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_secondary_s() {
        assertFontStyle("fixed_automotive_darklist_secondary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_secondary_m() {
        assertFontStyle("fixed_automotive_darklist_secondary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_darklist_secondary_l() {
        assertFontStyle("fixed_automotive_darklist_secondary_l");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_input_default_m() {
        assertFontStyle("fixed_automotive_input_default_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_b_separator_primary_m() {
        assertFontStyle("fixed_automotive_b_separator_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_b_separator_primary_s() {
        assertFontStyle("fixed_automotive_b_separator_primary_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_list_body_primary_m() {
        assertFontStyle("fixed_automotive_list_body_primary_m");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_random_access_s() {
        assertFontStyle("fixed_automotive_random_access_s");
    }

    @NoRunOrientation
    public void testFontStyle_fixed_automotive_random_access_m() {
        assertFontStyle("fixed_automotive_random_access_m");
    }
}
