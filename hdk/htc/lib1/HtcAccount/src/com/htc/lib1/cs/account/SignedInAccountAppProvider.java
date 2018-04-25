package com.htc.lib1.cs.account;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.logging.HtcLoggerFactory;

/**
 * A {@link ContentProvider} to tell whether this app is signed in with HTC Account.
 * Integrated app need to extends and implements {@link SignedInAccountAppProvider#getModuleName()}
 * method, and add custom provider declaration as below in AndroidManifest.xml:
 * <pre class="">
 * {@code
 * <provider
 *      android:name="your provider name"
 *      android:authorities="your provider's authorities"
 *      android:permission="com.htc.cs.identity.permission.SIGNED_IN_APP"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.htc.cs.identity.SIGNED_IN_APP" />
 *          <category android:name="android.intent.category.DEFAULT" />
 *      </intent-filter>
 * </provider>
 * }
 * </pre>
 */
public abstract class SignedInAccountAppProvider extends ContentProvider {
    private HtcLogger mLogger = new HtcLoggerFactory("MyHTC",
            "MyHTC_S", SignedInAccountAppProvider.class).create();

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public final Bundle call(@NonNull String method, String arg, Bundle extras) {
        Bundle result = null;

        if (!callerHasValidSignature()) {
            mLogger.warning("Verify calling package signature failed, calling uid = "
                    + Binder.getCallingUid() + " pid = " + Binder.getCallingPid());
            throw new SecurityException("Calling package not allowed.");
        }
        if (HtcAccountDefs.METHOD_IS_SIGNED_IN.equals(method)) {
            result = new Bundle();
            /*
             * Since the caller package won't have access right for TrayPreferences' ContentProvider
             * (for multi-process SharedPreference, which is not exported), need to temporarily change
             * the identity to integrated app's for accessing isSignedIn().
             */
            long lastIdentity = Binder.clearCallingIdentity();
            result.putBoolean(HtcAccountDefs.KEY_IS_SIGNED_IN, isSignedIn());
            Binder.restoreCallingIdentity(lastIdentity);
        }
        return result;
    }

    private boolean callerHasValidSignature() {
        PackageManager pm = getContext().getPackageManager();
        String[] packages = pm.getPackagesForUid(Binder.getCallingUid());
        try {
            if (packages.length > 0) {
                Signature[] signs = pm.getPackageInfo(packages[0],
                        PackageManager.GET_SIGNATURES).signatures;
                if (signs.length == 1 && verifyKey(signs[0])) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            mLogger.error(e.getMessage());
        }
        return false;
    }

    /**
     * Verify the calling package is signed with HMS production key, and only allow test key if the
     * integrated app is test built.
     */
    private boolean verifyKey(Signature signature) {
        if (productionKey.equals(signature)) {
            return true;
        } else if (testKey.equals(signature) &&
                (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag ||
                        com.htc.lib1.cs.auth.BuildConfig.DEBUG)) {
            mLogger.debug("Test key with test build app.");
            return true;
        }
        return false;
    }

    private Signature productionKey = new Signature("3082048130820369a003020102020900f651268001b" +
            "a1c3d300d06092a864886f70d0101050500308187310b30090603550406130254573110300e06035504" +
            "08130754616f7975616e3110300e0603550407130754616f7975616e3110300e060355040a1307416e6" +
            "4726f69643110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696431" +
            "1e301c06092a864886f70d010901160f616e64726f6964406874632e636f6d301e170d3133313130363" +
            "033353234335a170d3333313130313033353234335a308187310b30090603550406130254573110300e" +
            "0603550408130754616f7975616e3110300e0603550407130754616f7975616e3110300e060355040a1" +
            "307416e64726f69643110300e060355040b1307416e64726f69643110300e06035504031307416e6472" +
            "6f6964311e301c06092a864886f70d010901160f616e64726f6964406874632e636f6d30820120300d0" +
            "6092a864886f70d01010105000382010d00308201080282010100eaf6c3b7f087f30eb65ee1b3ef6dec" +
            "bef6466917f069a1ed48e231cd29d220304cedb9eabbd23867e20fd984e9c284244509481bb84f3f27a" +
            "c58f5c1f5ce6e9ad04a3ac6c4c2f8090284848bb145798fa586883a3b4b2dd52419394cf37fab95d076" +
            "cd57c0c5b432a3bb9170e8266abe06d5956d8030fd90b8735b9d84700e181a834abc4179a5959ec7873" +
            "fbfc89b858b8b940267865189f2d2b941bfc51dabe5272c5c97c558c3c8cc045399c96902888fe5a5d0" +
            "f428dcb76acc958ce3dd97e7661a98311b2dfce77ec4d8980322bda243e92740df76f2835651b3285be" +
            "1775e7acb196d5e6af071f0f611b8874c44c82fba1d5d1254659857940f31423f17020103a381ef3081" +
            "ec301d0603551d0e04160414407f048e06388fe0c03287cda601ac0e102983ef3081bc0603551d23048" +
            "1b43081b18014407f048e06388fe0c03287cda601ac0e102983efa1818da4818a308187310b30090603" +
            "550406130254573110300e0603550408130754616f7975616e3110300e0603550407130754616f79756" +
            "16e3110300e060355040a1307416e64726f69643110300e060355040b1307416e64726f69643110300e" +
            "06035504031307416e64726f6964311e301c06092a864886f70d010901160f616e64726f69644068746" +
            "32e636f6d820900f651268001ba1c3d300c0603551d13040530030101ff300d06092a864886f70d0101" +
            "05050003820101004f179690af096baf1305915e9ddf9b006367d0f85160eab916966538537f32f0a85" +
            "68b4a3d8f3288af0daede100ad92b32f72c2104d8f4dedbdfb5825136dc1bc89958d3a4cb2273a3a4dd" +
            "008b19f424b16e617be33ef3b2c5161fc29ce5e6b20c146b30e9a46cd1365a91e976080dbfa52d8b214" +
            "adc563958de540adfef3ff2f7868cd4f7adf3f8baa509607f1839c5e02146d710e5534b0fd8297ce99d" +
            "7553ca9673213c41109793bf92133098437fa923cfb2775a85f05f2194e339aaf895b61175705f9342b" +
            "8c1ab8224bfa28c1a12ca5b01642b770e447041d98bbf9fdc812a3eb0e4b2b8c381824fd179318afad8" +
            "27bbef1d8331b3b0a76d3b15388565");
    private Signature testKey = new Signature("308204a830820390a003020102020900ae544e4045dcef933" +
            "00d06092a864886f70d0101050500308194310b3009060355040613025553311330110603550408130a" +
            "43616c69666f726e6961311630140603550407130d4d6f756e7461696e20566965773110300e0603550" +
            "40a1307416e64726f69643110300e060355040b1307416e64726f69643110300e06035504031307416e" +
            "64726f69643122302006092a864886f70d0109011613616e64726f696440616e64726f69642e636f6d3" +
            "01e170d3133313131313034303035315a170d3333313130363034303035315a308194310b3009060355" +
            "040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756" +
            "e7461696e20566965773110300e060355040a1307416e64726f69643110300e060355040b1307416e64" +
            "726f69643110300e06035504031307416e64726f69643122302006092a864886f70d0109011613616e6" +
            "4726f696440616e64726f69642e636f6d30820120300d06092a864886f70d01010105000382010d0030" +
            "8201080282010100bab09365da3497540e876d58e89044f3d0481fc7e95b0551b0b164827d56ab89e61" +
            "cc18856e996ee90e2455df31bf916bd57bd3d9d44f0466028e67214249872533e1772c5616578aa3b8f" +
            "000759c0acab13afd3005724a68d826f4d3a99c5bd697059b62a9402acecaab5e70b31535e33a8e0791" +
            "79c2982905e56cc29ae0e0bd9254ccc23f790266d7b498866634b5fe8be56b9d12975e4949c0c1cf383" +
            "9c4b277bc1a4d02a35325179e914b3f8e5960ba95ec4d8cadeac4e0709d7160a751938d73c64e562414" +
            "45d1829d93261dbd9a2b5e2ce690d5e16b933eb14956b9c6cc81c4e0a937fbfee9325db0217189d0164" +
            "4d99ec92a9e7582cc263f08b65e233020103a381fc3081f9301d0603551d0e0416041422362c29ac03e" +
            "ac7a12846c362b347dfe92606df3081c90603551d230481c13081be801422362c29ac03eac7a12846c3" +
            "62b347dfe92606dfa1819aa48197308194310b3009060355040613025553311330110603550408130a4" +
            "3616c69666f726e6961311630140603550407130d4d6f756e7461696e20566965773110300e06035504" +
            "0a1307416e64726f69643110300e060355040b1307416e64726f69643110300e06035504031307416e6" +
            "4726f69643122302006092a864886f70d0109011613616e64726f696440616e64726f69642e636f6d82" +
            "0900ae544e4045dcef93300c0603551d13040530030101ff300d06092a864886f70d010105050003820" +
            "101008e6973bb98f0143878b26e2377a27f36a5c961643d642b585f99771af2e1698cf65cc568950f06" +
            "41e2cea75022255a34b9216f86c9d74d9b23e9511612b5f44ef70398fc2f71571db7ef2f37fa16cfc49" +
            "095a54eb040e25aeea657d228ffa6f030be9520a5b000899ba1db8d19a517dba7250a547a22bf502e7c" +
            "252c5ffc2173fbce1fdff183ab0a08ca8a69d3403bb298efac406a29960c708792a7922cce5d69390ad" +
            "14f0401a925810da6025e6d83c0d3717169bbbd7ca0ce1cf9ec458f11155161c6d5be4511990c816716" +
            "9e841fe0264282b0429e03eec70223e438cc4a8d9ef584213a50299d43896a854760591a4be1c055796" +
            "65a246c39f98f8d0033");

    /**
     * Tell whether the client app is signed in with HTC account.
     *
     * @return whether the client app is signed in with HTC account.
     */
    protected boolean isSignedIn() {
        AppSignInHelper helper = new AppSignInHelper(getContext(), getModuleName());
        return helper.isSignedIn();
    }

    /**
     * Provide the unique name for identify sub-module in integrated app. This should be the same
     * with the {@link AppSignInHelper#AppSignInHelper(Context, String)} second argument.
     *
     * @return the unique name of sign-in app/sub-app.
     */
    protected abstract String getModuleName();
}
