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

    /**
     * Initialize signal handler to catch native crashes.
     * @param context put log.txt in cacheDir and launch service
     * @param activityPackageName important info for restarting this activity
     * @param activityClassName important info for restarting this activity
     */
    fun initSignalHandler(
        context: Context,
        activityPackageName: String,
        activityClassName: String
    ) {
        val cachePath = context.cacheDir.absolutePath
        nativeCreateLogFile(cachePath, LOG_FILENAME)
        nativeInitSignalHandler()
        startSignalService(context, activityPackageName, activityClassName, "$cachePath/$LOG_FILENAME")
    }

    /**
     * Start Service with SignalWatcher.
     * @param context for launching service
     * @param activityPackageName important info for restarting this activity
     * @param activityClassName important info for restarting this activity
     * @param cachePath absolute path to log.txt in cache
     * */
    private fun startSignalService(
        context: Context,
        activityPackageName: String,
        activityClassName: String,
        absoluteLogPath: String
    ) {
        serviceIntent = Intent(context, SignalService::class.java).apply {
            putExtra(EXTRA_ACTIVITY_PID, Os.getpid())
            putExtra(EXTRA_ACTIVITY_PACKAGE, activityPackageName)
            putExtra(EXTRA_ACTIVITY_PACKAGE_CLASS, "$activityPackageName.$activityClassName")
            putExtra(EXTRA_LOG_PATH,absoluteLogPath)
        }
        context.startService(serviceIntent ?: return)
    }

    /**
     * Deinitialzie signal handler to leave native crashes alone.
     */
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
     * @param cacheCrashPath Absolute path to log.txt
     * @param logFileName Filename for log file
     */
    private external fun nativeCreateLogFile(cacheCrashPath: String, logFileName: String)
}