#include "includes/Exception.h"
#include "includes/utils.h"

static void LogSignalInfo(siginfo_t* info) {
    switch (info->si_signo) {
        case SIGILL:
            DEMO_LOG("signal SIGILL caught");
            switch (info->si_code) {
                case ILL_ILLOPC:
                    DEMO_LOG("illegal opcode");
                    break;
                case ILL_ILLOPN:
                    DEMO_LOG("illegal operand");
                    break;
                case ILL_ILLADR:
                    DEMO_LOG("illegal addressing mode");
                    break;
                case ILL_ILLTRP:
                    DEMO_LOG("illegal trap");
                    break;
                case ILL_PRVOPC:
                    DEMO_LOG("privileged opcode");
                    break;
                case ILL_PRVREG:
                    DEMO_LOG("privileged register");
                    break;
                case ILL_COPROC:
                    DEMO_LOG("coprocessor error");
                    break;
                case ILL_BADSTK:
                    DEMO_LOG("internal stack error");
                    break;
                default:
                    DEMO_LOG("code = %d", info->si_code);
                    break;
            }
            break;
        case SIGFPE:
            DEMO_LOG("signal SIGFPE caught");
            switch (info->si_code) {
                case FPE_INTDIV:
                    DEMO_LOG("integer divide by zero");
                    break;
                case FPE_INTOVF:
                    DEMO_LOG("integer overflow");
                    break;
                case FPE_FLTDIV:
                    DEMO_LOG("floating-point divide by zero");
                    break;
                case FPE_FLTOVF:
                    DEMO_LOG("floating-point overflow");
                    break;
                case FPE_FLTUND:
                    DEMO_LOG("floating-point underflow");
                    break;
                case FPE_FLTRES:
                    DEMO_LOG("floating-point inexact result");
                    break;
                case FPE_FLTINV:
                    DEMO_LOG("invalid floating-point operation");
                    break;
                case FPE_FLTSUB:
                    DEMO_LOG("subscript out of range");
                    break;
                default:
                    DEMO_LOG("code = %d", info->si_code);
                    break;
            }
            break;
        case SIGSEGV:
            DEMO_LOG("signal SIGSEGV caught");
            switch (info->si_code) {
                case SEGV_MAPERR:
                    DEMO_LOG("address not mapped to object");
                    break;
                case SEGV_ACCERR:
                    DEMO_LOG("invalid permissions for mapped object");
                    break;
                default:
                    DEMO_LOG("code = %d", info->si_code);
                    break;
            }
            break;
        case SIGBUS:
            DEMO_LOG("signal SIGBUS caught");
            switch (info->si_code) {
                case BUS_ADRALN:
                    DEMO_LOG("invalid address alignment");
                    break;
                case BUS_ADRERR:
                    DEMO_LOG("nonexistent physical address");
                    break;
                case BUS_OBJERR:
                    DEMO_LOG("object-specific hardware error");
                    break;
                default:
                    DEMO_LOG("code = %d", info->si_code);
                    break;
            }
            break;
        case SIGABRT:
            DEMO_LOG("signal SIGABRT caught");
            break;
        case SIGPIPE:
            DEMO_LOG("signal SIGPIPE caught");
            break;
        default:
            DEMO_LOG("signo %d caught", info->si_signo);
            DEMO_LOG("code = %d", info->si_code);
    }
    DEMO_LOG("errno = %d", info->si_errno);
}

const char* createCrashMessage(int signo, siginfo* siginfo) {
    void* current_exception = __cxxabiv1::__cxa_current_primary_exception();
    std::type_info* current_exception_type_info = __cxxabiv1::__cxa_current_exception_type();

    size_t buffer_size = 1024;
    char* abort_message = static_cast<char*>(malloc(buffer_size));

    if (current_exception) {
        try {
            // Check if we can get the message
            if (current_exception_type_info) {
                const char* exception_name = current_exception_type_info->name();

                // Try demangling exception name
                int status = -1;
                char demangled_name[buffer_size];
                __cxxabiv1::__cxa_demangle(exception_name, demangled_name, &buffer_size, &status);

                // Check demangle status
                if (status) {
                    // Couldn't demangle, go with exception_name
                    sprintf(abort_message, "Terminating with uncaught exception of type %s", exception_name);
                } else {
                    if (strstr(demangled_name, "nullptr") || strstr(demangled_name, "NULL")) {
                        // Could demangle, go with demangled_name and state that it was null
                        sprintf(abort_message, "Terminating with uncaught exception of type %s", demangled_name);
                    } else {
                        // Could demangle, go with demangled_name and exception.what() if exists
                        try {
                            __cxxabiv1::__cxa_rethrow_primary_exception(current_exception);
                        } catch (std::exception& e) {
                            // Include message from what() in the abort message
                            sprintf(abort_message, "Terminating with uncaught exception of type %s : %s", demangled_name, e.what());
                        } catch (...) {
                            // Just report the exception type since it is not an std::exception
                            sprintf(abort_message, "Terminating with uncaught exception of type %s", demangled_name);
                        }
                    }
                }

                return abort_message;
            } else {
                // Not a cpp exception, assume a custom crash and act like C
            }
        }
        catch (std::bad_cast& bc) {
            // Not a cpp exception, assume a custom crash and act like C
        }
    }

    // Assume C crash and print signal no and code
    sprintf(abort_message, "Terminating with a C crash %d : %d", signo, siginfo->si_code);
    return abort_message;
}

void nativeCrashSignalHandler(int signo, siginfo *siginfo, void *ctxvoid) {
    // Restoring an old handler to make built-in Android crash mechanism work.
    sigaction(signo, &crashInContext->old_handlers[signo], nullptr);
    auto *ctx = (sigcontext *) ctxvoid;
    // Get more contexts.
    const ucontext_t* signal_ucontext = (ucontext_t *) ctxvoid;
    assert(signal_ucontext);
    const sigcontext* signal_mcontext = (sigcontext*) &(signal_ucontext->uc_mcontext);
    assert(signal_mcontext);
    LogSignalInfo(siginfo);
    // Log crash message
    auto log_msg = createCrashMessage(signo, siginfo);
    //DEMO_LOG("Log msg: %s", log_msg)
    ofs.open(log_path, std::ios::out);
    ofs << log_msg;
    ofs.close();

    // In some cases we need to re-send a signal to run standard bionic handler.
    if (siginfo->si_code <= 0 || signo == SIGABRT) {
        if (syscall(__NR_tgkill, getpid(), gettid(), signo) < 0) {
            _exit(1);
        }
    }
}

bool registerSignalHandler(CrashSignalHandler handler, struct sigaction old_handlers[NSIG]) {
    struct sigaction sigactionstruct;
    memset(&sigactionstruct, 0, sizeof(sigactionstruct));
    sigactionstruct.sa_flags = SA_SIGINFO;
    sigactionstruct.sa_sigaction = handler;

    // Register new handlers for all signals
    for (int signo : SIGNALS_TO_CATCH) {
        if (sigaction(signo, &sigactionstruct, &old_handlers[signo])) {
            return false;
        }
    }

    return true;
}

void unregisterSignalHandler(struct sigaction old_handlers[NSIG]) {
    // Recover old handler for all signals
    for (int signo = 0; signo < NSIG; ++signo) {
        const struct sigaction* old_handler = &old_handlers[signo];

        if (!old_handler->sa_handler) {
            continue;
        }

        sigaction(signo, old_handler, nullptr);
    }
}

void initializeNativeCrashHandler() {
    // Check if already initialized
    if (crashInContext) {
        __android_log_print(ANDROID_LOG_INFO, "NDK Playground", "%s", "Native crash handler is already initialized.");
        return;
    }

    // Initialize singleton crash handler context
    crashInContext = static_cast<CrashInContext *>(malloc(sizeof(CrashInContext)));
    memset(crashInContext, 0, sizeof(CrashInContext));

    // Trying to register signal handler.
    if (!registerSignalHandler(&nativeCrashSignalHandler, crashInContext->old_handlers)) {
        deinitializeNativeCrashHandler();
        __android_log_print(ANDROID_LOG_ERROR, "NDK Playground", "%s", "Native crash handler initialization failed.");
        return;
    }

    __android_log_print(ANDROID_LOG_ERROR, "NDK Playground", "%s", "Native crash handler successfully initialized.");
}

bool deinitializeNativeCrashHandler() {
    // Check if already deinitialized
    if (!crashInContext) return false;

    // Unregister signal handlers
    unregisterSignalHandler(crashInContext->old_handlers);

    // Free singleton crash handler context
    free(crashInContext);
    crashInContext = nullptr;

    __android_log_print(ANDROID_LOG_ERROR, "NDK Playground", "%s", "Native crash handler successfully deinitialized.");

    return true;
}
/// Jni bindings
extern "C" JNIEXPORT void JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalHandler_nativeInitSignalHandler(
        JNIEnv* env,
        jobject /* this */) {
    initializeNativeCrashHandler();
}

extern "C" JNIEXPORT void JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalHandler_nativeDeinitSignalHandler(
        JNIEnv* env,
        jobject /* this */) {
    deinitializeNativeCrashHandler();
}

extern "C" JNIEXPORT void JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalHandler_crashAndGetExceptionMessage(
        JNIEnv *env,
        jobject thiz, jthrowable exception) {
    //env->ThrowNew(env->GetObjectClass(exception),"Oh my pony");
    int *a = nullptr;
    a[10] = 6;
    env->ExceptionCheck();
    //throw MyException(); // This can be replaced with any foreign function call that throws.
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalWatcher_isErrorMessageExistInLog(JNIEnv *env, jobject thiz) {
    std::string log_from_file = readStringFromLogFile(ifs, log_path);
    if (!log_from_file.empty()) {
        //DEMO_LOG("Intercept crash log: %s", log_from_file.c_str())
        return true;
    } else return false;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalWatcher_getLastErrorMessage(JNIEnv *env, jobject thiz) {
    std::string log_from_file = readStringFromLogFile(ifs, log_path);
    auto remove_status = std::remove(log_path.c_str());
    DEMO_LOG("Remove intercepted log.txt: %i", remove_status)
    DEMO_LOG("Extracted log: %s", log_from_file.c_str())
    jstring log_from_file_jni = env->NewStringUTF(log_from_file.c_str());
    assert(!log_from_file.empty() && "isLogFilled");
    return log_from_file_jni;
}

extern "C"
JNIEXPORT void JNICALL
Java_ru_arvrlab_ndkcrashhandler_SignalHandler_nativeCreateLogFile(JNIEnv *env, jobject thiz, jint app_pid,
                                                                  jstring cache_path) {
    assert(app_pid != 0 && "isCachePathProvided");
    assert(cache_path != nullptr && "isCachePathProvided");
    crash_absolute_path = (char *) env->GetStringUTFChars(cache_path, 0);
    //Remove old and create new empty log.txt
    log_path = crash_absolute_path, log_path += "log.txt";
    auto remove_status = std::remove(log_path.c_str());
    DEMO_LOG("Init log.txt\nin PID %i\nRemove old log.txt: %i\nLog path: %s",
             app_pid, remove_status, log_path.c_str())
}