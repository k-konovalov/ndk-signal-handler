package ru.arvrlab.ndkcrashhandler;

public class SignalHandler {
    static {
        System.loadLibrary("ndk-crash-handler");
    }
    /**
     * Initialize native signal handler to catch native crashes.
     */
    public native void initSignalHandler();

    /**
     * Deinitialzie native signal handler to leave native crashes alone.
     */
    public native void deinitSignalHandler();

    /**
     * @param exception
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application. It will throw a C++ exception
     * and catch it in the signal handler which will be visible in the logs.
     */
    public native void crashAndGetExceptionMessage(Exception exception);
    /**
     * @param appId: App PID
     * @param cacheCrashPath: Cache path to log.txt
     * */
    public native void createLogFile(int appId, String cacheCrashPath);
}
