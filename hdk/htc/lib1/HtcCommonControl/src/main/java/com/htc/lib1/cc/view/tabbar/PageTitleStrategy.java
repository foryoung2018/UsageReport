package com.htc.lib1.cc.view.tabbar;

public interface PageTitleStrategy {
    public CharSequence getPageTitle(int position);

    public int getPageCount(int position);

    public boolean isAutomotiveMode();

    public boolean isCNMode();
}
