package com.htc.lib1.mediamanager;
import com.htc.lib1.mediamanager.ServiceObject;

interface IonServiceStateListener
{
    void onFiltered(int currentShownServices, in Bundle extras);
}