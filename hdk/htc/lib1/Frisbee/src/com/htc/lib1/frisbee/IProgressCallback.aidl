package com.htc.lib1.frisbee;

interface IProgressCallback {
    void updateProgress(int index, int total);
    
    void updateFileProgress(int index, int total, int percent);

    void updateFileProgressBySize(long transfer, long total, int percent);
}