module-libauth
==============

Local HTC Account authenticator implementation library which let user signs in through WebView.


How to Unbundle with Google Play service library
-----------------------

HTC Account library users have a choice to determine include Google Play service library or not
since v2.1.16. Note that if you don't inclue Google Play service library, you won't have the
feature of "dynamic security provider"[1]. If your Apps are installed in the ROM without GMS, such
as China ROM, you won't have the feature, too. See the following steps for remove dependency of
Google Play service library.

### Gradle Build:
  Edit "$(SDK)/extraLibraries.gradle" and remove line
```bash
  "com.google.android.gms:play-services-base:6.5.87".
```

### ANT Build:
  Edit "$(SDK)/project.properties" and remove line
```bash
  "android.library.reference.1=../module-google-play-services".
```


Reference
-----------------------

Dynamic security provider
https://developer.android.com/training/articles/security-gms-provider.html

