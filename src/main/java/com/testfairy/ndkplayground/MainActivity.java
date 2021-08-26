package com.testfairy.ndkplayground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrashUnwinder;

import static com.testfairy.ndkplayground.CrashLogServiceKt.EXTRA_ACTIVITY_PID;
import static com.testfairy.ndkplayground.CrashLogServiceKt.EXTRA_ACTIVITY_PPID;

public class MainActivity extends Activity {

	// Used to load the 'native-lib' library on application startup.
	static {
		System.loadLibrary("native-lib");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService();

		setContentView(R.layout.activity_main);

		// Example of a call to a native method
		Button crashButton = findViewById(R.id.crash_button);
		crashButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				crashAndGetExceptionMessage(new IllegalAccessException());
			}
		});

		final String reportPath = getCacheDir().getAbsolutePath() + "/crash.txt"; // Example.
		final NDCrashError error = NDCrash.initializeInProcess(reportPath, NDCrashUnwinder.libunwind);
		if (error == NDCrashError.ok) {
			// Initialization is successful.
		} else {
			// Initialization failed, check error value.
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		initSignalHandler();
	}

	@Override
	protected void onPause() {
		super.onPause();

		deinitSignalHandler();
	}

	private void startService(){
		Intent intent = new Intent(this, CrashLogService.class);
		intent.putExtra(EXTRA_ACTIVITY_PPID, Os.getppid());
		intent.putExtra(EXTRA_ACTIVITY_PID, android.os.Process.myPid());
		stopService(intent);
		startService(intent);
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
	 * A native method that is implemented by the 'native-lib' native library,
	 * which is packaged with this application. It will throw a C++ exception
	 * and catch it in the signal handler which will be visible in the logs.
	 */
	public native void crashAndGetExceptionMessage(Exception exception);
}
