package ru.arvrlab.ndkcrashhandler

import android.content.Context
import android.content.Intent
import android.system.Os
import android.util.Log

class SignalHandler {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    private var serviceIntent: Intent? = null

    fun initSignalHandler(
        context: Context,
        cachePath: String,
        activityPackageName: String, activityClassName: String
    ) {
        nativeCreateLogFile(cachePath)
        nativeInitSignalHandler()
        startSignalService(context, activityPackageName, activityClassName, cachePath)
    }

    private fun startSignalService(
        context: Context,
        activityPackageName: String,
        activityClassName: String,cachePath: String
    ) {
        serviceIntent = Intent(context, SignalService::class.java).apply {
            putExtra(EXTRA_ACTIVITY_PID, Os.getpid())
            putExtra(EXTRA_ACTIVITY_PACKAGE, activityPackageName)
            putExtra(EXTRA_ACTIVITY_PACKAGE_CLASS, "$activityPackageName.$activityClassName")
            putExtra(EXTRA_LOG_PATH,"$cachePath/$LOG_FILENAME")
        }
        context.startService(serviceIntent ?: return)
    }

    fun deinitSignalHandler(context: Context) {
        context.stopService(serviceIntent ?: return)
        nativeDeinitSignalHandler()
    }

    /**
     * Initialize native signal handler to catch native crashes.
     */
    private external fun nativeInitSignalHandler()

    /**
     * Deinitialzie native signal handler to leave native crashes alone.
     */
    private external fun nativeDeinitSignalHandler()

    /**
     * @param exception
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application. It will throw a C++ exception
     * and catch it in the signal handler which will be visible in the logs.
     */
    external fun crashAndGetExceptionMessage(exception: Exception?)

    /**
     * @param appId: App PID
     * @param cacheCrashPath: Cache path to log.txt
     */
    external fun nativeCreateLogFile(cacheCrashPath: String?)
}