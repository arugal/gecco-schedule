package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.util.ObjectUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author: zhangwei
 * @date: 15:34/2019-01-02
 */
public final class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory){
        ObjectUtil.checkNotNull(threadFactory, "threadFactory");
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
