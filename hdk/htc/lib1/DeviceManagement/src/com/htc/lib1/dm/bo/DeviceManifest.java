package com.htc.lib1.dm.bo;

import java.util.List;
import java.util.Map;

/**
 * Created by Joe_Wu on 8/25/14.
 */
public class DeviceManifest {


    // --------------------------------------------------

    // Device manifest type.
    private String type;

    // Device profile.
    private DeviceProfile deviceProfile;

    // The collection of application manifests corresponding to each DM enabled application...
    private List<AppManifest> appManifests;

    private Map<String,Object> meta;
    // --------------------------------------------------

    public DeviceManifest(String type, DeviceProfile deviceProfile, List<AppManifest> appManifests, Map<String,Object> meta) {
        this.type = type;
        this.deviceProfile = deviceProfile;
        this.appManifests = appManifests;
        this.meta = meta;
    }

    // --------------------------------------------------

    public String getType() {
        return type;
    }

    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public List<AppManifest> getAppManifests() {
        return appManifests;
    }

    public Map<String,Object> getMeta() { return meta; }

}
