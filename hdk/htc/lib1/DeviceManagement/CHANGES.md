# Device Management Library Solo (HDK Lib1)

***

## Version: 3.0.26

**Release Date:** Feb 6, 2017

**Release Owner:** YM Chuang

**ChangeList No. (CL#):**

**Build Path:**

### Bug Fixes

### Change List

- Modify retry mechanism.

***

## Version: 3.0.25

**Release Date:** Nov 17, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):**

**Build Path:** https://hichub.htc.com/device-management-qm/dm-lib-solo-build-repository/raw/release/3.0/lib-dm-solo_3.0.25.aar

### Bug Fixes

### Change List

- Change executors.

***

## Version: 3.0.24

**Release Date:** Stp 11, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 622472

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/raw/release/3.0/lib-dm-solo_3.0.24.aar

### Bug Fixes

### Change List

- Remove useless method `DeviceEnv.getMobileEquipmentIdentifier()`.
- Refactor method `DeviceEnv.getMobileEquipmentIdentifierUrn()`.
- Refactor method `RestHelper.getHeaders()`.
- Upgrade Gradle version to 1.2.2.

***

## Version: 3.0.23

**Release Date:** Stp 1, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 613150

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/raw/release/3.0/lib-dm-solo_3.0.23.aar

### Bug Fixes

### Change List

- Fix no device ID return default value.
- No device ID will remove HTTP header `X-HTC-Tel-ID`.
- The targetSdkVersion update to 23.

***

## Version: 3.0.22

**Release Date:** Aug 7, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 598560

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.22.aar

### Bug Fixes

### Change List
- Fix Android API Level 23 (MNC) no `android.net.http.AndroidHttpClient` class issue.
- Add HttpDateTimeUtil.

***

## Version: 3.0.21

**Release Date:** Aug 6, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 594934

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.21.aar

### Bug Fixes

### Change List
- Add gradle.build setting consumerProguardFiles property

***

## Version: 3.0.20

**Release Date:** Aug 3, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 593838

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.20.aar

### Bug Fixes
- [[ATS][Exception][SST] Device got Exception issue (com.htc.launcher)  during running SST.{{Total failure rate: 2 times/5329 hours}}](http://htceasap1.htc.com.tw/its/jsf/issue/editIssue.jsf?oid=ims.entity.its.HTCIssue:143747172983207083&ticket=ST-1841111-IdwdtZI7TOFg1cPIgFr4-cas)

### Change List
- Enhancement error handling.

***

## Version: 3.0.19

**Release Date:** Jun 2, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 588144

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.19.aar

### Bug Fixes

### Change List
- Change hash algorithm to SHA-256 and change credential version to 2. 

***

## Version: 3.0.18

**Release Date:** Apr 29, 2015

**Release Owner:** Joseph Yen

**Change List No. (CL#):** 538222

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.18.aar

### Bug Fixes
- [531605-001_HIMAULATT_ATT_WWE_L51_Mainline_Sense70_Stable_MR.zip][Log Scan][Always]Process “com.htc.launcher” has PII data in log.(1714 times in test.)

### Change List
- Add prefix NONE_ID_SCHEME when TelephonyManager.getPhoneType() return TelephonyManager.PHONE_TYPE_NONE. 

***

## Version: 3.0.17

**Release Date:** Mar 17, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 509068

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.17.aar

### Bug Fixes

### Change List
- update proguard-project.txt
- show ConfigManager instance fingerprint in log.
- throw DMException in getInstance() method.

***

## Version: 3.0.16

**Release Date:** Mar 10, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.16.aar

### Bug Fixes

### Change List
- handle retry-after null case.

***

## Version: 3.0.15

**Release Date:** Mar 8, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 502674

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.15.aar

### Bug Fixes
- PQCS-4082 [ATS][Auto Ram Dump][RESET][SST]Device reset while device running test case (test case name: SSTv14_SR20_L_Sense70) (no abnormal log pattern found)
- PQCS-4090 [ATS][Auto Ram Dump][RESET][SST]Device reset while device running test case (test case name: SSTv14_SR20_L_Sense70) (Parser issue, Could not get the result from parser)
- PQCS-4098 [ATS][Auto Ram Dump][RESET][SST]Device reset while device running test case (test case name: SSTv14_SR20_L_Sense70) (PC:aee_wdt_atf_info+)
- PQCS-4099 [IQT_ATS][SST][Reset][Ramdump]Device enter ramdump during SST
- PQCS-4109 [ATS][Major][SST] Device got Watchdog Timeout issue (system_server)  during running SST.
- PQCS-4112 [ATS][Major][SST] Device got System Died issue (system_server)  during running SST.

### Change List
- handle unauthorized(config is null) case.

***

## Version: 3.0.14

**Release Date:** Feb 26, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 495956

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.14.aar

### Bug Fixes
- PQCS-940 [EagleEye] com.htc.sense.news Thread Leak

### Change List
- prevent huge threads generated in poor network environment.

***

## Version: 3.0.13

**Release Date:** Jan 9, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 461985

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.13.aar

### Bug Fixes

### Change List
- performance enhancement.
- double check if local latest updated date is incorrect

***

## Version: 3.0.12

**Release Date:** Jan 6, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.12.aar

### Bug Fixes

### Change List
- update persistent cache mechanism by using SharedPreference instead of Robospice cache.

***

## Version: 3.0.11

**Release Date:** Jan 5, 2015

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 457266

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.11.aar

### Bug Fixes

### Change List
- fix unique key base64 format contains “/“ symbol and cannot read cache file correctly.
- enhance the recovery process for OK status but no config in cache.
- add new DMNoKeyInConfigException and will not print error log for this exception

***

## Version: 3.0.10

**Release Date:** Dec 25, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 451327

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.10.aar


### Bug Fixes

### Change List
- align retry-after value when retrieving from server.
- Enhance unique-key which used to detect if client need to re-putprofile or re-getconfig.

***

## Version: 3.0.9

**Release Date:** Dec 22, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.9.aar

### Bug Fixes
- Device Management DM-258 Device UI Test- DM3 HDK 3.0.8- Not using the value of retry-after for the range of retry.

### Change List
- enhance datetime format in log especially for retry-after issue tracking.

***

## Version: 3.0.8

**Release Date:** Dec 22, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.8.aar

### Bug Fixes

### Change List
- add MAX_RETRY_AFTER is 7 days.
- fix Retry-After not working in PutProfile.

***

## Version: 3.0.7

**Release Date:** Dec 19, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.7.aar

### Bug Fixes

### Change List
- support HTTP Service Unavailable (503) with Retry-After header
- Add ScreenDensity, ScreenWidth, ScreenHeight into DeviceProfile

***

## Version: 3.0.6

**Release Date:** Oct 27, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 415858

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.6.aar

### Bug Fixes

### Change List
- fix wrong credential version by using hard coded value “1”.

***

## Version: 3.0.5

**Release Date:** Sep 16, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 396383

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.5.aar

### Bug Fixes
- DM-202 Device UI Test- If PUT Profile success at the first time, it will keep sending request when app calls get configure()

### Change List
- update success status when PutProfile.

***

## Version: 3.0.4

**Release Date:** Sep 9, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 395874

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.4.aar

### Bug Fixes
- DM-199 Device UI test- When the GET Configure fails, it should not keep retry within the time period

### Change List
- fix GetConfig max retry time unit from millisecond to second.

***

## Version: 3.0.3

**Release Date:** Sep 9, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 395018

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.3.aar

### Bug Fixes
- DM-193 Device UI Test- If device retry more than 7 times (the next retry will be an hour later), the retry time is not an hour.
- DM-197 Device UI Test- If Get Config gets the correct value at the first time, and gets wrong value(version key error 1102) at the second time, it will clear the cache and sends default value.
- DM-198 Device UI Test- Non-htc device cannot send request successfully

### Change List
- migrate from Gradle to Ant for HTC prebuild.
- fix wrong PutProfile HTTP read timeout value. (use 20s instead of 5s)
- update http dump debug format.

***

## Version: 3.0.2

**Release Date:** Sep 3, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 393367

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.2.aar

### Bug Fixes

### Change List
- code refactoring.
- create DMStatus for better state management.
- create DMCacheManager.
- use CacheManager of RoboSpice instead of the cache of SpiceService.
- remove RoboSpiceService dependency.
- using Google Http Client.
- remove start() & stop().
- add init() for putProfile checking and getConfig preparation.
- add generic getConfigValue API and varied getXXXValue APIs.

***

## Version: 3.0.1

**Release Date:** Aug 27, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):** 415858

**Build Path:** https://gitlab.dev.sea1.csh.tc/device-management-qm/build-repository/blob/release/3.0/lib-dm-solo_3.0.1.aar

### Bug Fixes

### Change List
- enhance retry mechanism.
- add new exception DMWrongVersionKeyException.
- handle multi-thread invoking issue.

***

## Version: 3.0.0

**Release Date:** Aug 26, 2014

**Release Owner:** JoeC Wu

**Change List No. (CL#):**

### Bug Fixes

### Change List
- init Non-HTC device supported DM library.
- decouple PutProfile / GetConfig API.
- add device profile info into GetConfig request header.
- add new MIME type for new manifest “application/vnd.htc.dm.device-manifest-v2+json”.
- add customised header for dm-lib-solo “X-HTC-DM-Lib-Version: com.htc.lib1.dm;3.0.0”.

***
