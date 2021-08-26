package com.testfairy.ndkplayground

import android.app.Application
import android.system.Os

class MyApp: Application() {
    init {
        System.loadLibrary("native-lib")
    }
    override fun onCreate() {
        super.onCreate()
        createLogFile(Os.getppid(), cacheDir.absolutePath + "/")
    }

    private external fun createLogFile(appId: Int, cacheCrashPath: String)
}