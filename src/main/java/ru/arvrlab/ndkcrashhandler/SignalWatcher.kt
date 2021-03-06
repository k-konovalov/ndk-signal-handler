package ru.arvrlab.ndkcrashhandler

import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Watch for Signal crash log. Restart provided activity if exist.
 * */
class SignalWatcher {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var isWatcherEnabled = false
    var actionAfterError: Runnable? = null

    /** Start watcher on new thread only once, during the app lifetime.
     * @param logPath Absolute path to log.txt
     * @param activityClass watch over this
     * */
    fun start(logPath: String, activityClass: String) {
        if (!isWatcherEnabled) executor.execute {
            isWatcherEnabled = true
            waitForError(logPath, activityClass)
        }
    }

    /** Check log.txt for bytes each 2000ms */
    private fun waitForError(logPath: String, activityClass: String) {
        var isErrorMsgExist: Boolean
        var rescueAttempt = 0
        while (isWatcherEnabled) {
            Thread.sleep(2000)
            isErrorMsgExist = isErrorMessageExistInLog(logPath)
            Log.i(this.javaClass.name, "I'm alive.\nWatching over ${activityClass.split(".").last()} in $logPath.\nApp restarted $rescueAttempt times.")
            if (isErrorMsgExist) {
                Log.i(this.javaClass.name, "Crash fired with error:\n${getLastErrorMessage(logPath)}")
                rescueAttempt++
                actionAfterError?.run()
            }
        }
    }

    /** Stop watcher */
    fun stop(){
        isWatcherEnabled = false
        Log.i(this.javaClass.name, "Bye bye")
    }

    /** Check new bytes in log.txt
     * @return true if founded, else false
     */
    private external fun isErrorMessageExistInLog(logPath: String): Boolean
    /** @return last error string from log file
     */
    private external fun getLastErrorMessage(logPath: String): String
}