package com.geccocrawler.gecco.spider;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zhangwei
 * @date: 19:43/2019-01-02
 */
public class NamedThreadFactory implements ThreadFactory {

    private String prefix;

    private static final AtomicInteger idx = new AtomicInteger();

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(prefix +"-"+ idx.getAndIncrement());
        return thread;
    }
}
