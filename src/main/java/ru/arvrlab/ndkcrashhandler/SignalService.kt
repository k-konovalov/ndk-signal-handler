package ru.arvrlab.ndkcrashhandler

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.system.Os
import android.util.Log
import java.lang.IllegalStateException

const val EXTRA_ACTIVITY_PID = "EXTRA_ACTIVITY_PID"
const val EXTRA_ACTIVITY_PACKAGE = "EXTRA_ACTIVITY_PACKAGE"
const val EXTRA_ACTIVITY_PACKAGE_CLASS = "EXTRA_ACTIVITY_PACKAGE_CLASS"
const val EXTRA_LOG_PATH = "EXTRA_LOG_PATH"
const val LOG_FILENAME = "log.txt"
const val EXTRA_APP_RESURRECT = "EXTRA_APP_RESURRECT"

class SignalService : Service() {
    private val signalWatcher = SignalWatcher()
    private val customIntent: Intent = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    private val actionAfterSignalError = Runnable {
        customIntent.component?.run {
            Log.i("SignalService", "Trying to restart $className")
        }
        customIntent.extras?.putBoolean(EXTRA_APP_RESURRECT, true)
        startActivity(customIntent)
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

    override fun onDestroy() {
        signalWatcher.stop()
        super.onDestroy()
    }

    private fun setupActionForRestart(intent: Intent){
        val activityPackage = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE) ?: ""
        val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE_CLASS) ?: ""
        customIntent.setClassName(activityPackage, activityClass)
    }

    private fun logPids(intent: Intent?) {
        if (intent != null) {
            val activityPid = intent.getIntExtra(EXTRA_ACTIVITY_PID, 0)
            val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE_CLASS) ?: ""
            val servicePid = Os.getpid()
            Log.i(this.javaClass.simpleName, "Service with PID $servicePid watch over $activityClass with PID $activityPid")
        }
    }

    private fun setupAndLaunchSignalWatcher(intent: Intent) {
        val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_PACKAGE_CLASS) ?: ""
        val logPath = intent.getStringExtra(EXTRA_LOG_PATH) ?: throw IllegalStateException("No cache path for signal crashes provided")
        signalWatcher.start(logPath, activityClass)
        signalWatcher.actionAfterError = actionAfterSignalError
    }

    fun provideCustomAction(customActionAfterSignalError: Runnable?) {
        signalWatcher.actionAfterError = customActionAfterSignalError ?: actionAfterSignalError
    }
}