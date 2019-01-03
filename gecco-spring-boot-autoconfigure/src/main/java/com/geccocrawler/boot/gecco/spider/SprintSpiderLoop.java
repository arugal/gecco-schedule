package com.geccocrawler.boot.gecco.spider;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zhangwei
 * @date: 20:52/2019-01-03
 */
class SprintSpiderLoop extends SingleSpiderExecutor {

    private static final Log logger = LogFactory.getLog(SprintSpiderLoop.class);

    private final GeccoEngine engine;

    private final Spider spider;

    private final SpiderBeanFactory spiderBeanFactory;

    private static final AtomicInteger idx = new AtomicInteger();

    SprintSpiderLoop(SpiderExecutorGroup parent, GeccoEngine engine, ThreadFactory factory) {
        super(parent, factory);
        this.engine = engine;
        this.spider = (Spider) engine.getSpiders().get(0);
        this.spiderBeanFactory = engine.getSpiderBeanFactory();
    }

    @Override
    protected void run() {
        SpiderThreadLocal.set(spider);
        idx.getAndIncrement();
        for(;;){
            try{
                runAllTasks();
            }catch (Throwable t){
                handleLoopException(t);
            }
            try{
                if(isShuttingDown()){
                    return;
                }
            }catch (Throwable t){
                handleLoopException(t);
            }
        }
    }

    @Override
    protected ScheduleTask newScheduleTask(HttpRequest request) {
        return new ScheduleSpiderTask(this, spiderBeanFactory, request);
    }


    @Override
    protected ScheduleTask newScheduleTask(HttpRequest request, long delay) {
        return new ScheduleSpiderTask(this, delay, 0, spiderBeanFactory, request);
    }

    @Override
    protected ScheduleTask newScheduleTask(HttpRequest request, long initialDelay, long period) {
        return new ScheduleSpiderTask(this, initialDelay, period, spiderBeanFactory, request);
    }

    private static void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);

        // Prevent possible consecutive immediate failures that lead to
        // excessive CPU consumption.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore.
        }
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        idx.getAndDecrement();
        if(spider != null){
            spider.stop();
        }
        if(engine != null && idx.get() == 0){
            spiderBeanFactory.getDownloaderFactory().closeAll();
        }
    }
}
