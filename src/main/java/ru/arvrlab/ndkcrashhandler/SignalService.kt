package ru.arvrlab.ndkcrashhandler

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.system.Os
import android.util.Log
import ru.arvrlab.ndkcrashhandler.SignalWatcher.ActionAfterError
import java.lang.IllegalStateException

const val EXTRA_ACTIVITY_PID = "EXTRA_ACTIVITY_PID"
const val EXTRA_ACTIVITY_PPID = "EXTRA_ACTIVITY_PPID"
const val EXTRA_ACTIVITY_PACKAGE = "EXTRA_ACTIVITY_PACKAGE"
const val EXTRA_ACTIVITY_PACKAGE_CLASS = "EXTRA_ACTIVITY_PACKAGE_CLASS"
const val EXTRA_LOG_PATH = "EXTRA_LOG_PATH"
const val LOG_FILENAME = "log.txt"

class SignalService : Service() {
    private val signalWatcher = SignalWatcher()
    private val customIntent: Intent = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    private val actionAfterSignalError = object : ActionAfterError {
        override fun doIt() {
            Log.e(this.javaClass.simpleName, "Try to restart last activity")
            startActivity(customIntent ?: return)
            stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        setupActionForRestart(intent)
        logPids(intent)
        setupAndLaunchSignalWatcher(intent)
        return START_STICKY
    }

    private fun setupActionForRestart(intent: Intent){
        val activityPackage = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE) ?: ""
        val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE_CLASS) ?: ""
        customIntent.setClassName(activityPackage, activityClass)
    }

    private fun logPids(intent: Intent?) {
        if (intent != null) {
            val activityPid = intent.getIntExtra(EXTRA_ACTIVITY_PID, 0)
            val servicePid = Os.getpid()
            Log.e(this.javaClass.simpleName, "Service with PID: " + servicePid + " watch over Activity with PID " + activityPid + " in APP with PID " + Os.getppid())
        }
    }

    private fun setupAndLaunchSignalWatcher(intent: Intent) {
        signalWatcher.startCrashWatcher(intent.getStringExtra(EXTRA_LOG_PATH) ?: throw IllegalStateException("No cache path for signal crashes provided"))
        signalWatcher.actionAfterError = actionAfterSignalError
    }

    fun provideCustomAction(customActionAfterSignalError: ActionAfterError?) {
        signalWatcher.actionAfterError = customActionAfterSignalError ?: actionAfterSignalError
    }
}