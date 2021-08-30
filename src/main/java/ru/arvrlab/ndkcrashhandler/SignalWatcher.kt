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

    fun startCrashWatcher() {
        isTaskThrown.set(true)
        executor.execute {
            waitForError()
            Log.e(this.javaClass.name, "Crash fired with error:\n${getLastErrorMessage()}")
            actionAfterError?.doIt()
        }
    }

    private fun waitForError() {
        var isError = false
        while (!isError || !isTaskThrown.get()) {
            Thread.sleep(1000)
            isError = isErrorMessageExistInLog()
        }
        isTaskThrown.set(false)
    }

    /** Check new bytes in log
     * @return true if founded, else false
     */
    private external fun isErrorMessageExistInLog(): Boolean
    /** Read from log file last error
     */
    private external fun getLastErrorMessage(): String

    interface ActionAfterError {
        fun doIt()
    }
}