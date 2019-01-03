package com.geccocrawler.gecco.spider;

/**
 * @author: zhangwei
 * @date: 14:42/2019-01-02
 */
public interface RejectedExecutionHandler {

    void rejected(Runnable task, SingleSpiderExecutor executor);
}
