package com.geccocrawler.boot.gecco;

import com.geccocrawler.gecco.downloader.proxy.Proxys;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.geccocrawler.boot.gecco.util.GeccoUtil.GECCO_PREFIX;
import static com.geccocrawler.gecco.spider.SpiderConfig.*;
/**
 * @author: zhangwei
 * @date: 20:56/2019-01-03
 */
@Component
@ConfigurationProperties(
        prefix = GECCO_PREFIX,
        ignoreInvalidFields = true
)
public class GeccoConfig {

    private String classPath;

    private int nThreand = DEFAULT_SPIDER_LOOP_THREADS;

    private String threadPrefix = DEFAULT_THREAD_PREFIX;

    private boolean mobile = DEFAULT_IS_MOBILE;

    private boolean debug = DEFAULT_IS_DEBUG;

    private int retry = DEFAULT_RETRY;

    private boolean proxy = DEFAULT_IS_PROXY;

    @Autowired
    private PipelineFactory pipelineFactory;

    @Autowired
    private Proxys proxys;

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public int getnThreand() {
        return nThreand;
    }

    public void setnThreand(int nThreand) {
        this.nThreand = nThreand;
    }

    public String getThreadPrefix() {
        return threadPrefix;
    }

    public void setThreadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public boolean isProxy() {
        return proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    public PipelineFactory getPipelineFactory() {
        return pipelineFactory;
    }

    public void setPipelineFactory(PipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    public Proxys getProxys() {
        return proxys;
    }

    public void setProxys(Proxys proxys) {
        this.proxys = proxys;
    }
}
