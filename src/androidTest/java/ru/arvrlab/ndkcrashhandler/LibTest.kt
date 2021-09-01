package ru.arvrlab.ndkcrashhandler

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.arvrlab.ndkcrashhandler.test.TestService
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

/**
 * Start TestService in other process than tests runner and looking for crash log
 * */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
@LargeTest
class LibTest {
    private lateinit var appContext: Context

    @Before
    fun createContext(){
        appContext = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun startServiceCrash() {
        //After creating the activity, ActivityScenario transitions the activity to the RESUMED state.
        appContext.startService(Intent(appContext, TestService::class.java))
    }

    /** Wait for log
     * */
    @Test
    fun waitAndReadLogs() {
        val waitTimeout = 10 * 1000L
        var time = 0L
        val delay = 500L
        val logFile = File("${appContext.cacheDir.absolutePath}/$LOG_FILENAME")

        Log.w(javaClass.simpleName,"${appContext.cacheDir.absolutePath}/$LOG_FILENAME")
        while(!logFile.exists() || waitTimeout <= time){
            Thread.sleep(delay)
            time += delay
        }
        Assert.assertFalse("isTimeoutPassed", waitTimeout <= time)

        val logStr = try { FileInputStream(logFile).bufferedReader().use { it.readText() } }
        catch (e: Exception){ "" }

        Assert.assertTrue("isLogFilled", logStr.isNotEmpty())
        Log.w(javaClass.simpleName, logStr)

        logFile.delete()
        Assert.assertTrue("isLogDeleted", !logFile.exists())
    }

    @After
    fun stopService(){
        val isServiceStopped = appContext.stopService(Intent(appContext, TestService::class.java))
        Log.w(javaClass.simpleName,"End. isTestServiceAlive: $isServiceStopped")
    }
}