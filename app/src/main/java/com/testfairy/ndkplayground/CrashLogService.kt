package com.testfairy.ndkplayground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.system.Os
import android.util.Log
import java.util.concurrent.Executors

const val EXTRA_ACTIVITY_PID = "EXTRA_ACTIVITY_PID"
const val EXTRA_ACTIVITY_PPID = "EXTRA_ACTIVITY_PPID"
class CrashLogService : Service() {
    private val executor = Executors.newSingleThreadExecutor()
    init {
        System.loadLibrary("native-lib");
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val activityPid = intent?.getIntExtra(EXTRA_ACTIVITY_PID, 0) ?: 0
        val activityPpid = intent?.getIntExtra(EXTRA_ACTIVITY_PPID, 0) ?: 0
        val servicePid = android.os.Process.myPid()
        Log.e(this.javaClass.simpleName,"Service with PID: $servicePid watch over Activity with PID $activityPid in APP with PPID ${Os.getppid()}")
        startCrashHadler()
        return START_STICKY
    }

    private fun startCrashHadler() {
        executor.execute {
            waitForError()
            Log.e(this.javaClass.simpleName, "Crash fired with ${getErrorMessage()}!")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun waitForError(){
        var isError = 0
        while (isError == 0) {
            Thread.sleep(1000)
            isError = checkForErrorMessage();
        }
    }

    private external fun checkForErrorMessage(): Int
    private external fun getErrorMessage(): String

}