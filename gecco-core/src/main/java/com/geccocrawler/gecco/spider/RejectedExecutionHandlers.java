package com.geccocrawler.gecco.spider;

import com.sun.org.apache.regexp.internal.RE;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author: zhangwei
 * @date: 15:00/2019-01-02
 */
public final class RejectedExecutionHandlers {

    private static final RejectedExecutionHandler REJECT = new RejectedExecutionHandler() {
        @Override
        public void rejected(Runnable task, SingleSpiderExecutor executor) {
               throw new RejectedExecutionException();
        }
    };

    private RejectedExecutionHandlers(){

    }

    public static RejectedExecutionHandler reject(){
        return REJECT;
    }

}
