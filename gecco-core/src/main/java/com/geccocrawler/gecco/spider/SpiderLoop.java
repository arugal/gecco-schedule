package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.request.HttpRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author: zhangwei
 * @date: 16:04/2019-01-02
 */
class SpiderLoop extends SingleSpiderExecutor {

    private static final Log logger = LogFactory.getLog(SpiderLoop.class);

    private volatile static GeccoEngine engine;

    private volatile static Spider spider;

    private static final Object GECCO_LOCK = new Object();

    private final SpiderBeanFactory spiderBeanFactory;

    SpiderLoop(SpiderExecutorGroup parent, String classPath){
        super(parent, new NamedThreadFactory(SpiderLoop.class.getSimpleName()));

        if(engine == null && spider == null) {
            synchronized (GECCO_LOCK) {
                if(engine == null && spider == null) {
                    engine = GeccoEngine.create(classPath).loop(true).thread(1).mobile(false).monitor(false);
                    engine.run();
                    engine.engineStop();
                    spider = (Spider) engine.getSpiders().get(0);
                }
            }
        }
        spiderBeanFactory = engine.getSpiderBeanFactory();
    }


    @Override
    protected void run() {
        SpiderThreadLocal.set(spider);
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
    protected ScheduleTask newScheduleTask(HttpRequest request){
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
        if(spider != null){
            spider.stop();
        }
        if(engine != null){
            spiderBeanFactory.getDownloaderFactory().closeAll();
        }
    }
}
