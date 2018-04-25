
package com.htc.lib1.cs.account.restservice;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Direct mapping of REST JSON objects of identity server.
 */
public class IdentityJsonClasses {

    public static class WDeviceToken {
        public String AuthKey;
        public boolean IsEmailVerified;
        public boolean IsHandsetVerified;
        public boolean LegalDocsToSign;
        public boolean FirstLogin;
        public String CountryCode;
        public String Status;
        public Date GracePeriodExpireTime;
        public int CheckGracePeriodInDay;
        public UUID AccountId;
        public String RefreshToken;
    }

    public static class WDeviceTokenPayload {
        public WDeviceToken DeviceToken;
    }

    public static class WDataCenter {
        public UUID Id;
        public String Name;
        public String ServiceUri;
        public String ImcUri;
        public String UpUri;
        public String CmsUri;
        public String WebServiceUri;
        public String WebCmsUri;
    }

    public static class WGracePeriodStatus {
        public boolean IsVerified;
        public boolean IsStarted;
        public boolean IsExpired;
        public int CheckPeriod;
        public int GracePeriodInDay;
        public Date GracePeriodExpireTime;
    }

    public static class WFacebookAccount {
        public String uid;
        public String accessToken;
        public WAccountV2 account;
        public String clientId;
        public String scopes;
    }

    public static class WGoogleAccount {
        public String uid;
        public String accessToken;
        public WAccountV2 account;
        public String clientId;
        public String scopes;
    }

    public static class WAccountV2 {
        public String EmailAddress;
        public String FirstName;
        public String LastName;
        public UUID RegionId;
        public String LanguageCode;
        public boolean SendEmailAboutProducts;
        public String SecurityQuestion;
        public String SecurityAnswer;
        public String NewPassword;
        public List<WVirtualDevice> VirtualDevices;
        public String ClientId;
        public String Scopes;
    }

    public static class WVirtualDevice {
        public enum WPhoneOperationStatus {
            Unknown,
            Off,
            On,
        }

        public enum WResyncStateType {
            ResyncStateInSync,
            ResyncStateBeginHandshake,
            ResyncStatePending,
            ResyncStateInProgress,
            ResyncStateEndHandshake,
        }

        public String AlternateNumber;
        public String AreaCode;
        public UUID BindRequestId;
        public WPhoneOperationStatus CallForwardingState;
        public String CallForwardPhoneNumber;
        public UUID CallForwardRegionId;
        public String CountryCode;
        public Double CurrentLocationLatitude;
        public Double CurrentLocationLongitude;
        public WPhoneOperationStatus ForceRingState;
        public UUID HandsetDeviceId;
        public String HandsetLocale;
        public String IDD;
        public boolean IsLocationTrackingEnabled;
        public boolean IsVerified;
        public Date LastConnectedTimestamp;
        public Date LastKnownLocation;
        public Date LastSeenAtTimestamp;
        public Date LastViewDashboards;
        public Date LastViewFootprints;
        public Date LastViewRecommends;
        public WPhoneOperationStatus LockedHandsetState;
        public String LockHandsetMessage;
        public String LockHandsetPin;
        public String MessageForwardEmailAddress;
        public String MessageForwardPhoneNumber;
        public UUID MessageForwardPhoneRegionId;
        public WPhoneOperationStatus MessageForwardStateEmail;
        public WPhoneOperationStatus MessageForwardStatePhone;
        public int MusicVendor;
        public String NDD;
        public int NewVoicemailCount;
        public String PhoneNumberString;
        public WPhysicalDeviceModel PhysicalDeviceModel;
        public UUID PhysicalDeviceModelId;
        public Date RecommendsAppCommentLastViewDate;
        public Date RecommendsAppLastViewDate;
        public boolean RemoteDataWipeEraseSDState;
        public WPhoneOperationStatus RemoteDataWipeState;
        public UUID ResyncId;
        public WResyncStateType ResyncState;
        public Date ResyncStateDate;
        public long TimeOffsetFromUtc;
        public Date Timestamp;
    }

    public static class WPhysicalDeviceModel {
        public enum WDeviceModelType {
            DeviceUnknown,
            DeviceImei,
            DeviceEsn,
            DeviceMeid,
            DeviceWorld,
        }

        public int CellColumns;
        public int CellRows;
        public int HandsetScreenHeight;
        public int HandsetScreenWidth;
        public int HandsetWallpaperHeight;
        public int HandsetWallpaperWidth;
        public UUID Id;
        public int ImageHeight;
        public int ImageScreenHeight;
        public int ImageScreenOffsetX;
        public int ImageScreenOffsetY;
        public int ImageScreenWidth;
        public int ImageWidth;
        public WDeviceModelType ModelType;
        public String Name;
        public String PartNumber;
        public WPhysicalDeviceModelImage PhysicalDeviceModelImage;
        public String Properties;
    }

    public static class WPhysicalDeviceModelImage {
        public String ContentType;
        public String FileType;
        public UUID Id;
        public String Name;
        public UUID PhysicalDeviceModelId;
        public String Url;
    }

    public static class WProfileImage {
        public UUID AccountId;
        public String ContentType;
        public String FileType;
        public UUID Id;
        public String Name;
        public Date Timestamp;
        public String Url;
    }

    public static class WAccountId {
        public UUID Id;
    }

    public static class WVirtualAccountToken {
        public String uid;
        public String v_token;
        public String a_token;
        public String app_token;
    }

    public static class WBasicSinaProfile {
        public String email;
        public int email_visible;
        public long id;
        public String name;
        public String real_name;
        public int real_name_visible;
        public String screen_name;
    }

    public static class WAccessToken {
        public String access_token;
        public String token_type;
        public Date expired_in;
    }

    public static class WMasterToken {
        public String access_token;
        public String token_type;
        public Date expired_in;
        public String refresh_token;
    }
}
