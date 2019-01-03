package com.geccocrawler.gecco.spider.linstener;

import com.geccocrawler.gecco.spider.SpiderExecutor;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: zhangwei
 * @date: 14:47/2019-01-02
 */
public class SpiderExecutorListenerSupport implements SpiderExecutorListener {

    private volatile Set<SpiderExecutorListener> listeners = new CopyOnWriteArraySet<>();

    @Override
    public void onStart(SpiderExecutor ge) {
        for(SpiderExecutorListener listener : listeners){
            listener.onStart(ge);
        }
    }

    @Override
    public void onPause(SpiderExecutor ge) {
        for(SpiderExecutorListener listener : listeners){
            listener.onPause(ge);
        }
    }

    @Override
    public void onRenew(SpiderExecutor ge) {
        for(SpiderExecutorListener listener : listeners){
            listener.onRenew(ge);
        }
    }

    @Override
    public void onShutdown(SpiderExecutor ge) {
        for(SpiderExecutorListener listener : listeners){
            listener.onShutdown(ge);
        }
    }

    public boolean addListener(SpiderExecutorListener listener){
        if(listener instanceof SpiderExecutorListenerSupport){
            return false;
        }
        return listeners.add(listener);
    }

    public boolean removeListaner(SpiderExecutorListener listener){
        return listeners.remove(listener);
    }
}
