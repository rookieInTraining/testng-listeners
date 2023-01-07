package com.rookieintraining.browser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserManager {

    private static ThreadLocal<BrowserThread> browserThread;
    private static final List<BrowserThread> browserThreads = Collections.synchronizedList(new ArrayList<>());

    public static void init() {
        browserThread = ThreadLocal.withInitial(() -> {
            BrowserThread thread = new BrowserThread();
            browserThreads.add(thread);
            return thread;
        });
    }

    public static BrowserThread getDriver() {
        return browserThread.get();
    }

    public static void quitDriver() {
        browserThreads.forEach(BrowserThread::quit);
    }
}
