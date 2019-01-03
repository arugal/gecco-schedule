package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.linstener.SpiderExecutorListener;

import java.util.concurrent.TimeUnit;

/**
 * @author: zhangwei
 * @date: 22:06/2019-01-01
 */
public interface SpiderExecutorGroup {

    /**
     * 提交任务, 只执行一次
     * @param url
     */
    void executor(String url);

    /**
     * 提交任务, 只执行一次
     * @param request
     */
    void executor(HttpRequest request);

    /**
     * 提交任务, 只执行一次, 延迟 delay 后执行
     * @param url
     * @param delay
     * @param unit
     */
    void schedule(String url, long delay, TimeUnit unit);

    /**
     * 提交任务, 只执行一次, 延时 delay 后执行
     * @param request
     * @param delay
     * @param unit
     */
    void schedule(HttpRequest request, long delay, TimeUnit unit);

    /**
     * 提交任务, 以 period 为周期循环执行, 延时 initialDelay 后执行
     * @param url
     * @param initialDelay
     * @param period
     * @param unit
     */
    void scheduleAtFixedRate(String url, long initialDelay, long period, TimeUnit unit);

    /**
     * 提交任务, 以 period 为周期循环执行, 延时 initialDelay 后执行
     * @param request
     * @param initialDelay
     * @param period
     * @param unit
     */
    void scheduleAtFixedRate(HttpRequest request, long initialDelay, long period, TimeUnit unit);

    /**
     * 提交任务, 以 period + 本次任务执行时间 为周期循环执行, 延时 initialDelay 后执行
     * @param url
     * @param initalDelay
     * @param delay
     * @param unit
     */
    void scheduleWithFixedDelay(String url, long initalDelay, long delay, TimeUnit unit);

    /**
     * 提交任务, 以 period + 本次任务执行时间 为周期循环执行, 延时 initialDelay 后执行
     * @param request
     * @param initialDelay
     * @param delay
     * @param unit
     */
    void scheduleWithFixedDelay(HttpRequest request, long initialDelay, long delay, TimeUnit unit);

    /**
     * 暂停
     * @return
     */
    void pause();

    /**
     * 恢复
     * @return
     */
    void renew();

    /**
     * 停止
     * @return
     */
    void shutdown();

    /**
     * 是否暂停
     * @return
     */
    boolean isPause();

    /**
     * 是否正在停止中
     * @return
     */
    boolean isShuttingDown();

    /**
     * 是否以停止
     * @return
     */
    boolean isShutdown();

    /**
     * 添加监听器
     * @param listener
     * @return
     */
    boolean addEngicListener(SpiderExecutorListener listener);

    /**
     * 删除监听器
     * @param listener
     * @return
     */
    boolean removeEngicListener(SpiderExecutorListener listener);

    /**
     *
     * @return
     */
    SpiderExecutor next();
}
