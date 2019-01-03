package com.geccocrawler.gecco.spider;

/**
 * @author: zhangwei
 * @date: 22:07/2019-01-01
 */
public interface SpiderExecutor extends SpiderExecutorGroup {

    /**
     * SpiderExecutor self
     * @return
     */
    @Override
    default SpiderExecutor next(){
        return this;
    }

    SpiderExecutorGroup parent();

    /**
     * 开启一个新的工作线程
     */
    void startThread();
}
