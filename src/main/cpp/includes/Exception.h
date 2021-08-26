#ifndef FUCK
#define FUCK

// Java
#include <jni.h>

// C++
#include <csignal>
#include <cstdio>
#include <cstring>
#include <exception>
#include <memory>
#include <alloca.h>
#include <cassert>

// C++ ABI
#include <cxxabi.h>

// Android
#include <android/log.h>
#include <unistd.h>
#include <fstream>
////////////////////////////////////////////////////////////////////////////

/// Helper macro to get size of an fixed length array during compile time
#define sizeofa(array) sizeof(array) / sizeof(array[0])

/// tgkill syscall id for backward compatibility (more signals available in many linux kernels)
#define __NR_tgkill 270

//// ---------------  New
#include "Logger.h"
#include <fcntl.h>
#include <sys/stat.h>

std::string crash_absolute_path;
std::string log_path;
std::ofstream ofs;
std::ifstream ifs;

/// Caught signals
const int SIGNALS_TO_CATCH[] = {
        SIGABRT,
        SIGBUS,
        SIGFPE,
        SIGSEGV,
        SIGILL,
        SIGSTKFLT,
        SIGTRAP,
};
/// Signal handler context
struct CrashInContext {
    /// Old handlers of signals that we restore on de-initialization. Keep values for all possible
    /// signals, for unused signals nullptr value is stored.
    struct sigaction old_handlers[NSIG];
};
/// Crash handler function signature
typedef void (*CrashSignalHandler)(int, siginfo*, void*);
/// Global instance of context. Since an app can't crash twice in a single run, we can make this singleton.
CrashInContext* crashInContext = nullptr;
/// Create a crash message using whatever available such as signal, C++ exception etc
const char* createCrashMessage(int signo, siginfo* siginfo);
/** Main signal handling function.
 * @param sig: The number of the signal that caused invocation of the
              handler.
 * @param siginfo: A pointer to a siginfo_t, which is a structure containing further information about the signal
 * @param context: Signal context information that was saved on the user-space stack by the kernel
*/
static void nativeCrashSignalHandler(int signo, siginfo *siginfo, void *ctxvoid);
/** Create crash message in crash handler
 * @param signo The number of the signal that caused invocation of the handler.
   @param siginfo siginfo: A pointer to a siginfo_t, which is a structure containing further information about the signal
 * */
const char* createCrashMessage(int signo, siginfo* siginfo);
/// Register signal handler for crashes
bool registerSignalHandler(CrashSignalHandler handler, struct sigaction old_handlers[NSIG]);
/**
 * Unregister already register signal handler */
void unregisterSignalHandler(struct sigaction old_handlers[NSIG]);

/// like TestFairy.stop() but for crashes
bool deinitializeNativeCrashHandler();
/// like TestFairy.begin() but for crashes
void initializeNativeCrashHandler();

/// Our custom test exception. Anything "publicly" inheriting std::exception will work
class MyException : public std::exception {
public:
    const char* what() const noexcept override {
        return "This is a really important crash message!";
    }
};
#endif