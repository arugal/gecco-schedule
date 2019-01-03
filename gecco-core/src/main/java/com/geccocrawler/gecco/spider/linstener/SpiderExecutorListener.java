package com.geccocrawler.gecco.spider.linstener;

import com.geccocrawler.gecco.spider.SpiderExecutor;

/**
 * @author: zhangwei
 * @date: 14:45/2019-01-02
 */
public interface SpiderExecutorListener {

    /**
     * 开始启动时，回调
     *
     * @param ge GeccoEngine
     */
    void onStart(SpiderExecutor ge);

    /**
     * 暂停时，回调
     *
     * @param ge GeccoEngine
     */
    void onPause(SpiderExecutor ge);

    /**
     * 重新启动时，回调
     * @param ge
     */
    void onRenew(SpiderExecutor ge);

    /**
     * 引擎停止时，回调
     *
     * @param ge GeccoEngine
     */
    void onShutdown(SpiderExecutor ge);
}
