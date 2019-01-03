package com.geccocrawler.boot.gecco;

import com.geccocrawler.gecco.downloader.proxy.Proxys;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.geccocrawler.boot.gecco.util.GeccoUtil.GECCO_PREFIX;
/**
 * @author: zhangwei
 * @date: 20:56/2019-01-03
 */
@Component
@ConfigurationProperties(
        prefix = GECCO_PREFIX,
        ignoreInvalidFields = true
)
public class GeccoProperties {

    private String classPath;

    private int nThreand = 1;

    private String threadPrefix = "spider-thread";

    private boolean mobile = false;

    private boolean debug = false;

    private int retry = 3;

    private boolean proxy = false;

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
