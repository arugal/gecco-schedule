package com.geccocrawler.boot.gecco.listener;

import com.geccocrawler.gecco.spider.SpiderExecutor;
import com.geccocrawler.gecco.spider.linstener.SpiderExecutorListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @author: zhangwei
 * @date: 14:56/2019-01-04
 */
@Component
public class ListannerSamples extends SpiderExecutorListenerAdapter {

    @Override
    public void onStart(SpiderExecutor ge) {
        System.out.println("Spider start");
    }

    @Override
    public void onShutdown(SpiderExecutor ge) {
        System.out.println("Spider shutdown");
    }
}
