package com.example.printimin.imin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author njmsir
 * Created by njmsir on 2020/6/29.
 */

public class ThreadPoolManager {
    private volatile static ThreadPoolManager INSTANCE;
    private ExecutorService mThreadPool;

    private ThreadPoolManager() {
        mThreadPool = new ThreadPoolExecutor(4,
                4, 100,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("FaceThread");
                        return thread;
                    }
                });
    }

    public static ThreadPoolManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadPoolManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadPoolManager();
                }
            }
        }
        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }

    public void shutdownNow() {
        mThreadPool.shutdownNow();
        INSTANCE = null;
    }
}
