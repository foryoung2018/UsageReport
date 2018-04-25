package com.htc.lib1.dm.bo;

import android.content.Context;
import android.text.TextUtils;
import com.htc.lib1.dm.env.DeviceEnv;
import com.htc.lib1.dm.env.HepDeviceEnv;
import com.htc.lib1.dm.env.HmsDeviceEnv;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class DeviceProfile {

    private int AndroidAPILevel;

    private String AndroidVersion;

    private String BuildDescription;

    private String BuildDisplayID;

    private String BuildFingerprint;

    private String BuildID;

    private String BuildTags;

    private String BuildType;

    private String CustomerID;

    private String ExtraSenseVersion;

    private String Manufacturer;

    private String MarketingName;

    private String ModelID;

    private String ProductName;

    // HTC project...ROM
    // Ex: ENDEAVOR_U_JB_45_S
    private String ProjectName;

    private Integer RegionID;

    private String ROMVersion;

    private String SenseVersion;

    private Integer SkuID;

    private Integer ScreenWidth;

    private Integer ScreenHeight;

    private Float ScreenDensity;


    public DeviceProfile() {}

    public DeviceProfile(Context context) {

        DeviceEnv deviceEnv = DeviceEnv.get(context);

        this.AndroidVersion= deviceEnv.getAndroidVersion();
        this.AndroidAPILevel = deviceEnv.getAndroidApiLevel();

        this.BuildFingerprint = deviceEnv.getBuildFingerprint();
        this.BuildID = deviceEnv.getBuildId();
        this.BuildDisplayID = deviceEnv.getBuildDisplayId();
        this.BuildType = deviceEnv.getBuildType();
        this.BuildTags = deviceEnv.getBuildTags();

        this.Manufacturer = deviceEnv.getManufacturer();
        this.ProductName = deviceEnv.getProductName();
        this.MarketingName = deviceEnv.getMarketingName();

        // Screen info
        this.ScreenDensity = deviceEnv.getScreenDensity();
        this.ScreenWidth = deviceEnv.getScreenWidth();
        this.ScreenHeight = deviceEnv.getScreenHeight();

        // ACC parameters...

        HmsDeviceEnv hmsDeviceEnv = HmsDeviceEnv.get(context);

        this.SkuID = hmsDeviceEnv.getSkuID();
        this.RegionID = hmsDeviceEnv.getRegionID();

        this.SenseVersion = getStringValueOrNull(hmsDeviceEnv.getSenseVersion());
        this.ExtraSenseVersion = getStringValueOrNull(hmsDeviceEnv.getExtraSenseVersion());

        // HEP parameters...

        HepDeviceEnv hepDeviceEnv = HepDeviceEnv.get(context);

        this.ModelID = getStringValueOrNull(hepDeviceEnv.getDeviceModelId());
        this.CustomerID = getStringValueOrNull(hepDeviceEnv.getCID());
        this.BuildDescription = getStringValueOrNull(hepDeviceEnv.getBuildDescription());
        this.ROMVersion = getStringValueOrNull(hepDeviceEnv.getRomVersion());
        this.ProjectName = getStringValueOrNull(hepDeviceEnv.getProjectName());

    }


    private static String getStringValueOrNull(String value) {
        return TextUtils.isEmpty(value) ? null : value;
    }

}
