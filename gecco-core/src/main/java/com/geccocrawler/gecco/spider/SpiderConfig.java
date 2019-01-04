package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.downloader.proxy.Proxys;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import lombok.NoArgsConstructor;

/**
 * @author: zhangwei
 * @date: 23:37/2019-01-03
 */
@NoArgsConstructor
public class SpiderConfig {

    public static final int DEFAULT_SPIDER_LOOP_THREADS = 1;

    public static final String DEFAULT_THREAD_PREFIX = "spider-thread";

    public static final boolean DEFAULT_IS_MOBILE = false;

    public static final boolean DEFAULT_IS_DEBUG = false;

    public static final int DEFAULT_RETRY = 3;

    public static final boolean DEFAULT_IS_PROXY = false;

    private String classPath;

    private int nThreand = DEFAULT_SPIDER_LOOP_THREADS;

    private String threadPrefix = DEFAULT_THREAD_PREFIX;

    private boolean mobile = DEFAULT_IS_MOBILE;

    private boolean debug = DEFAULT_IS_DEBUG;

    private int retry = DEFAULT_RETRY;

    private boolean proxy = DEFAULT_IS_PROXY;

    private PipelineFactory pipelineFactory;

    private Proxys proxys;

    public SpiderConfig(String classPath){
        this.classPath = classPath;
    }

    public SpiderConfig(String classPath, int nThreand, String threadPrefix, boolean mobile,
                        boolean debug, int retry, boolean proxy, PipelineFactory pipelineFactory, Proxys proxys) {
        this.classPath = classPath;
        this.nThreand = nThreand;
        this.threadPrefix = threadPrefix;
        this.mobile = mobile;
        this.debug = debug;
        this.retry = retry;
        this.proxy = proxy;
        this.pipelineFactory = pipelineFactory;
        this.proxys = proxys;
    }

    public String getClassPath() {
        return classPath;
    }

    public SpiderConfig classPath(String classPath) {
        this.classPath = classPath;
        return this;
    }

    public int getNThreand() {
        return nThreand;
    }

    public SpiderConfig nThreand(int nThreand) {
        this.nThreand = nThreand;
        return this;
    }

    public String getThreadPrefix() {
        return threadPrefix;
    }

    public SpiderConfig threadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
        return this;
    }

    public boolean isMobile() {
        return mobile;
    }

    public SpiderConfig mobile(boolean mobile) {
        this.mobile = mobile;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public SpiderConfig debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public int getRetry() {
        return retry;
    }

    public SpiderConfig retry(int retry) {
        this.retry = retry;
        return this;
    }

    public boolean isProxy() {
        return proxy;
    }

    public SpiderConfig proxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    public PipelineFactory getPipelineFactory() {
        return pipelineFactory;
    }

    public SpiderConfig pipelineFactory(PipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
        return this;
    }

    public Proxys getProxys() {
        return proxys;
    }

    public SpiderConfig proxys(Proxys proxys) {
        this.proxys = proxys;
        return this;
    }

    @Override
    public String toString() {
        return "SpiderConfig{" +
                "classPath='" + classPath + '\'' +
                ", nThreand=" + nThreand +
                ", threadPrefix='" + threadPrefix + '\'' +
                ", mobile=" + mobile +
                ", debug=" + debug +
                ", retry=" + retry +
                ", proxy=" + proxy +
                ", pipelineFactory=" + pipelineFactory +
                ", proxys=" + proxys +
                '}';
    }
}
