package ru.arvrlab.ndkcrashhandler.test

import android.app.Service
import android.content.Intent
import android.os.Bundle
import org.jetbrains.annotations.TestOnly
import ru.arvrlab.ndkcrashhandler.SignalHandler

class TestService: Service() {
    @TestOnly
    override fun onBind(intent: Intent?) = null

    private val signalHandler = SignalHandler()

    @TestOnly
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        signalHandler.initSignalHandler(
            context = this,
            activityPackageName = packageName,
            activityClassName = "test"
        )
        signalHandler.crashAndGetExceptionMessage(null)
        return super.onStartCommand(intent, flags, startId)
    }

    @TestOnly
    override fun onDestroy() {
        super.onDestroy()
        signalHandler.deinitSignalHandler(this)
    }
}