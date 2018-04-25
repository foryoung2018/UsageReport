/**
 * HtcBackupAgent module: HtcBackupAgent is a service that acts as a backup agent. 
 * Similar to the android.app.backup.BackupAgent in the Android data backup, 
 * it provides the center interface between an application to Frisbee. 
 * Moreover, HtcBackupAgent provides a scalable architecture and can support large 
 * file transferring for HTC applications. 
 * Application developer should implement this abstract class and declared this service 
 * in the manifest. Afterwards, Frisbee can recognized and support your application.
 */
package com.htc.lib1.frisbee;