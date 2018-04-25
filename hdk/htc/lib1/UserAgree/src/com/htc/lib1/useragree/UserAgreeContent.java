package com.htc.lib1.useragree;

public class UserAgreeContent {
	public String title;
    public String message;
    public String checkboxLabel;
    public String positiveLabel;
    public String negativeLabel;
    
    /**
     * the constructor
     * @param title dialog's title shown on HtcUserAgreeDialog
     */
    public UserAgreeContent(String title) {
        this.title = title;
    }
    
    /**
     * the constructor
     * @param title dialog's title shown on HtcUserAgreeDialog
     * @param message dialog's message shown on HtcUserAgreeDialog
     * @param checkboxLabel dialog's checkboxLabel shown on HtcUserAgreeDialog
     * @param positiveLabel dialog's positiveLabel shown on HtcUserAgreeDialog
     * @param negativeLabel dialog's negativeLabel shown on HtcUserAgreeDialog
     */
    public UserAgreeContent(String title, String message, String checkboxLabel, String positiveLabel, String negativeLabel) {
        this.title = title;
        this.message = message;
        this.checkboxLabel = checkboxLabel;
        this.positiveLabel = positiveLabel;
        this.negativeLabel = negativeLabel;
    }
}
