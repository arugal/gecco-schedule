package com.geccocrawler.gecco.spider;

/**
 * @author: zhangwei
 * @date: 22:26/2019-01-01
 */
public interface SpiderExecutorChooserFactory {

    SpiderExecutorChooser newChooser(SpiderExecutor[] children);

    interface SpiderExecutorChooser{

        SpiderExecutor next();
    }
}
