package com.htc.lib1.home;

public class BadgeInfo {
    private String m_strComponentName;
    private String m_strData;
    private int m_nCount;

    public BadgeInfo(String strComponentName, String strData, int nCount) {
        m_strComponentName = strComponentName;
        m_strData = strData;
        m_nCount = nCount;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(128);
        b.append("BadgeInfo { ");
        b.append("cmp=").append(m_strComponentName);
        b.append(" data=").append(m_strData);
        b.append(" count=").append(m_nCount);
        b.append(" }");
        return b.toString();
    }

    public String getComponentName() {
        return m_strComponentName;
    }

    public String getData() {
        return m_strData;
    }

    public int getCount() {
        return m_nCount;
    }
}
