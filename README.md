# Android-NDK-Signal-handler-module
Lib that watch for signal crashes (SIGSEGV and e.t.c) and perform actions after it
## Usefull links
### Libs
[ndk-stack](https://developer.android.com/ndk/guides/ndk-stack)
[ivanarh | JNDCrash lib - Signal crash handler](https://github.com/ivanarh/jndcrash)
### Repositories
[TestFairy | NDK Playground](https://github.com/testfairy-blog/NdkPlayground)
[9majka | NDK Overall presentation in Ukraine](https://github.com/9majka/NDK_presentation)
[ivanarh | JNDCrash lib - Signal crash handler](https://github.com/ivanarh/jndcrash)
[liyungui | NDK Signal handler example](https://github.com/liyungui/liyungui.github.io/blob/e7bac51dc082ab4f14b2742db5f5259dda1dbe0b/source/_posts/NDK/Native%E5%B4%A9%E6%BA%83%E6%8D%95%E8%8E%B7.md)
### Papers
[Test Fairy | NDK Crash Handling](https://testfairy.com/blog/ndk-crash-handling/)
[Jekton | Android native crash catching](https://jekton.github.io/2019/04/06/native-crash-catching/)
[Yeconglu - Tencent Bugly | Signal Handler Deeply](https://mp.weixin.qq.com/s/g-WzYF3wWAljok1XjPoo7w?)
[IPC using Pipes](https://medium.com/@jain.sm/ipc-using-pipes-f5daaf27fb44)
### SIG
[signal](https://man7.org/linux/man-pages/man7/signal.7.html)
[siginfo_t - Signal Codes description](https://www.mkssoftware.com/docs/man5/siginfo_t.5.asp#Signal_Codes)
### errno
[errno | Man - Number of last error](https://man7.org/linux/man-pages/man3/errno.3.html)
[errno | Example](https://en.cppreference.com/w/cpp/error/errno)
[errno | RU Descrition](https://ru.wikipedia.org/wiki/Errno.h)
[strerror | Description for error](https://man7.org/linux/man-pages/man3/strerror.3.html)
### Crash handler
[sigaction](https://man7.org/linux/man-pages/man2/sigaction.2.html)
[How to work with signal handler](https://stackoverflow.com/questions/2663456/how-to-write-a-signal-handler-to-catch-sigsegv)
[async-signal-safe functions](https://man7.org/linux/man-pages/man7/signal-safety.7.html)
[StackOverflow | Unwind](https://stackoverflow.com/questions/18017222/android-unwind-backtrace-inside-sigaction/30515756#30515756)
### File Descriptors & Pipe
[Read until pipe is closed](https://unix.stackexchange.com/questions/397553/read-until-pipe-is-closed)
[File Descriptors - Open](https://man7.org/linux/man-pages/man2/open.2.html)
[pipe | man](https://man7.org/linux/man-pages/man2/pipe.2.html)
[read | man](https://man7.org/linux/man-pages/man2/read.2.html)
[How to use pipe between parent and child process](https://stackoverflow.com/questions/54505699/how-to-use-pipe-between-parent-and-child-process-after-call-to-popen)
[Read from a pipe without writing to it](https://stackoverflow.com/questions/54619208/what-happens-when-i-try-to-read-from-a-pipe-without-writing-to-it)
[Pipe is not working correctly in the parent-child process](https://stackoverflow.com/questions/24431553/pipe-is-not-working-correctly-in-the-parent-child-process)
### Services
[Android Developers | Service in process](https://developer.android.com/guide/components/services?hl=ru#java)
[Android Developers | Manifest for Service](https://developer.android.com/guide/topics/manifest/service-element.html#proc)
[StackOverflow | Service in process](https://stackoverflow.com/questions/22514373/start-a-service-in-a-separate-process-android)
### Utils
[How to list processes locking file?](https://unix.stackexchange.com/questions/85994/how-to-list-processes-locking-file)