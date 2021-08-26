package ru.arvrlab.ndkcrashhandler;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SignalWatcher {
    static {
        System.loadLibrary("ndk-crash-handler");
    }

    private final Executor executor = Executors.newSingleThreadExecutor();
    public ActionAfterError actionAfterError = ()->{};

    public void startCrashHadler() {
        executor.execute(
                () -> {
                    waitForError();
                    Log.e(this.getClass().getName(), "Crash fired with error:\n" + getLastErrorMessage());
                    actionAfterError.doIt();
                }
        );
    }

    private void waitForError()  {
        boolean isError = false;
        while (!isError) {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) { }
            isError = isErrorMessageExistInLog();
        }
    }

    /** Check new bytes in log
     * @return true if founded, else false
     * */
    public native boolean isErrorMessageExistInLog();
    /** Read from log file last error
     * */
    public native String getLastErrorMessage();

    public interface ActionAfterError {
        void doIt();
    }
}
