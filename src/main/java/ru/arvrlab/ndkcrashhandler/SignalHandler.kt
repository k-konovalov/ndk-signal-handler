package ru.arvrlab.ndkcrashhandler

import android.content.Context
import android.content.Intent
import android.system.Os

class SignalHandler {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    fun initSignalHandler(
        context: Context,
        appPid: Int,
        cachePath: String,
        activityPackageName: String, activityClassName: String
    ) {
        nativeCreateLogFile(appPid, cachePath)
        nativeInitSignalHandler()
        startSignalService(context, activityPackageName, activityClassName, cachePath)
        Thread.sleep(5000);
        crashAndGetExceptionMessage(null)
    }

    private fun startSignalService(
        context: Context,
        activityPackageName: String,
        activityClassName: String,cachePath: String
    ) {
        val intent = Intent(context, SignalService::class.java).apply {
            putExtra(EXTRA_ACTIVITY_PPID, Os.getppid())
            putExtra(EXTRA_ACTIVITY_PID, Os.getpid())
            putExtra(EXTRA_ACTIVITY_PACKAGE, activityPackageName)
            putExtra(EXTRA_ACTIVITY_PACKAGE_CLASS, "$activityPackageName.$activityClassName")
            putExtra(EXTRA_LOG_PATH,"$cachePath/$LOG_FILENAME")
        }
        context.startService(intent)
    }

    fun deinitSignalHandler() {
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
    external fun nativeCreateLogFile(appId: Int, cacheCrashPath: String?)
}