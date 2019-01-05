package com.geccocrawler.gecco.spider.linstener;

import com.geccocrawler.gecco.spider.SpiderExecutor;

/**
 * @author: zhangwei
 * @date: 14:57/2019-01-04
 */
public abstract class SpiderExecutorListenerAdapter implements SpiderExecutorListener{


    @Override
    public void onStart(SpiderExecutor ge) {
        // NOOP
    }

    @Override
    public void onPause(SpiderExecutor ge) {
        // NOOP
    }

    @Override
    public void onRenew(SpiderExecutor ge) {
        // NOOP
    }

    @Override
    public void onShutdown(SpiderExecutor ge) {
        // NOOP
    }
}
