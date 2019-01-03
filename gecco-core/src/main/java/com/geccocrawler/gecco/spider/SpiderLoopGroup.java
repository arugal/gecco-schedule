package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.util.SystemPropertyUtil;

/**
 * @author: zhangwei
 * @date: 22:44/2019-01-01
 */
public class SpiderLoopGroup extends MultiSpiderExecutorGroup{


    private static final int DEFAULT_EXECUTOR_LOOP_THREADS;

    static {
        DEFAULT_EXECUTOR_LOOP_THREADS = Math.max(1 , SystemPropertyUtil.getInt("io.spider.executor.threads", Runtime.getRuntime().availableProcessors()));
    }


    public SpiderLoopGroup(String classPath){
        super(DEFAULT_EXECUTOR_LOOP_THREADS, classPath);
    }


    @Override
    protected SpiderExecutor newChild(Object ... args) {
        return new SpiderLoop(this, (String) args[0]);
    }
}
