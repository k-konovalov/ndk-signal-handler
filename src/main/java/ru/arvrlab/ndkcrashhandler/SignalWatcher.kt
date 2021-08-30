package ru.arvrlab.ndkcrashhandler

import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class SignalWatcher {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    private val executor: Executor = Executors.newSingleThreadExecutor()
    var actionAfterError: ActionAfterError? = null
    private var isTaskThrown = AtomicBoolean(false)

    fun startCrashWatcher(logPath: String) {
        isTaskThrown.set(true)
        executor.execute {
            waitForError(logPath)
            Log.e(this.javaClass.name, "Crash fired with error:\n${getLastErrorMessage(logPath)}")
            actionAfterError?.doIt()
        }
    }

    private fun waitForError(logPath: String) {
        var isError = false
        while (!isError || !isTaskThrown.get()) {
            Thread.sleep(1000)
            isError = isErrorMessageExistInLog(logPath)
        }
        isTaskThrown.set(false)
    }

    /** Check new bytes in log
     * @return true if founded, else false
     */
    private external fun isErrorMessageExistInLog(logPath: String): Boolean
    /** Read from log file last error
     */
    private external fun getLastErrorMessage(logPath: String): String

    interface ActionAfterError {
        fun doIt()
    }
}