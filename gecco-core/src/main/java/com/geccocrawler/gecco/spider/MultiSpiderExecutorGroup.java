package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.linstener.SpiderExecutorListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zhangwei
 * @date: 22:12/2019-01-01
 */
public abstract class MultiSpiderExecutorGroup implements SpiderExecutorGroup {

    private final SpiderExecutor[] children;
    private final SpiderExecutorChooserFactory.SpiderExecutorChooser chooser;

    protected MultiSpiderExecutorGroup(int nThreads, Object... args){
        this(nThreads, DefaultSpiderExecutorChooserFactory.INSTACE, args);
    }

    protected MultiSpiderExecutorGroup(int nThreads, SpiderExecutorChooserFactory chooserFactory, Object ... agrs){
        if(nThreads < 0){
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
        }

        children = new SpiderExecutor[nThreads];

        for(int i = 0; i < nThreads; i++){
            children[i] = newChild(agrs);
        }

        chooser = chooserFactory.newChooser(children);
    }


    @Override
    public void executor(String url) {
        next().executor(url);
    }

    @Override
    public void executor(HttpRequest request) {
        next().executor(request);
    }

    @Override
    public void schedule(String url, long delay, TimeUnit unit) {
        next().schedule(url, delay, unit);
    }

    @Override
    public void schedule(HttpRequest request, long delay, TimeUnit unit) {
        next().schedule(request, delay, unit);
    }

    @Override
    public void scheduleAtFixedRate(String url, long initialDelay, long period, TimeUnit unit) {
        next().scheduleAtFixedRate(url, initialDelay, period, unit);
    }

    @Override
    public void scheduleAtFixedRate(HttpRequest request, long initialDelay, long period, TimeUnit unit) {
        next().scheduleAtFixedRate(request, initialDelay, period, unit);
    }

    @Override
    public void scheduleWithFixedDelay(String url, long initalDelay, long delay, TimeUnit unit) {
        next().scheduleWithFixedDelay(url, initalDelay, delay, unit);
    }

    @Override
    public void scheduleWithFixedDelay(HttpRequest request, long initialDelay, long period, TimeUnit unit) {
        next().scheduleWithFixedDelay(request, initialDelay, period, unit);
    }

    @Override
    public void pause() {
        for(SpiderExecutor executor : children){
            executor.pause();
        }
    }

    @Override
    public void renew() {
        for(SpiderExecutor executor : children){
            executor.renew();
        }
    }

    @Override
    public void shutdown() {
        for(SpiderExecutor executor : children){
            executor.shutdown();
        }
    }

    @Override
    public boolean isPause() {
        for(SpiderExecutor executor : children){
            if(!executor.isPause()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isShuttingDown() {
        for(SpiderExecutor executor : children){
            if(!executor.isShuttingDown()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isShutdown() {
        for(SpiderExecutor executor : children){
            if(!executor.isShutdown()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addEngicListener(SpiderExecutorListener listener) {
        for(SpiderExecutor executor : children){
           if(!executor.addEngicListener(listener)){
               return false;
           }
        }
        return true;
    }

    @Override
    public boolean removeEngicListener(SpiderExecutorListener listener) {
        for(SpiderExecutor executor : children){
            if(!executor.removeEngicListener(listener)){
                return false;
            }
        }
        return true;
    }

    @Override
    public SpiderExecutor next() {
        return chooser.next();
    }

    public int executorCount(){
        return children.length;
    }

    protected abstract SpiderExecutor newChild(Object ... args);


    private static final class DefaultSpiderExecutorChooserFactory implements SpiderExecutorChooserFactory{

        public static final DefaultSpiderExecutorChooserFactory INSTACE = new DefaultSpiderExecutorChooserFactory();

        private DefaultSpiderExecutorChooserFactory(){

        }


        @Override
        public SpiderExecutorChooser newChooser(SpiderExecutor[] children) {
            if(isPowerOfTwo(children.length)){
                return new PowerOfTwoSpiderExecutorChooser(children);
            }else{
                return new GenericSpiderExecutorChooser(children);
            }
        }

        private static boolean isPowerOfTwo(int val){
            return (val & - val) == val;
        }

        private static final class PowerOfTwoSpiderExecutorChooser implements SpiderExecutorChooser{

            private final AtomicInteger idx = new AtomicInteger();
            private final SpiderExecutor[] children;

            public PowerOfTwoSpiderExecutorChooser(SpiderExecutor[] children) {
                this.children = children;
            }

            @Override
            public SpiderExecutor next() {
                return children[idx.getAndIncrement() & children.length - 1];
            }
        }

        private static final class GenericSpiderExecutorChooser implements SpiderExecutorChooser{

            private final AtomicInteger idx = new AtomicInteger();
            private final SpiderExecutor[] children;

            public GenericSpiderExecutorChooser(SpiderExecutor[] children) {
                this.children = children;
            }

            @Override
            public SpiderExecutor next() {
                return children[Math.abs(idx.getAndIncrement() % children.length)];
            }
        }
    }

}
