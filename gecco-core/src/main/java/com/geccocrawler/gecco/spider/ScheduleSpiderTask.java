package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.downloader.*;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.spider.render.Render;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author: zhangwei
 * @date: 14:17/2019-01-02
 */
public class ScheduleSpiderTask extends ScheduleTask {

    private static final Log log = LogFactory.getLog(ScheduleSpiderTask.class);

    private final SpiderBeanFactory spiderBeanFactory;

    private final HttpRequest request;

    private Class<? extends SpiderBean> currSpiderBeanClass;

    private SpiderBeanContext spiderBeanContext;

    private final GeccoEngine engine;

    public ScheduleSpiderTask(SpiderExecutor executor, SpiderBeanFactory spiderBeanFactory, HttpRequest request, GeccoEngine engine) {
        super(executor);
        this.spiderBeanFactory = spiderBeanFactory;
        this.request = request;
        this.engine = engine;
    }

    public ScheduleSpiderTask(SpiderExecutor executor, long nanoTime, long period, SpiderBeanFactory spiderBeanFactory, HttpRequest request, GeccoEngine engine) {
        super(executor, nanoTime, period);
        this.spiderBeanFactory = spiderBeanFactory;
        this.request = request;
        this.engine = engine;
    }

    public ScheduleSpiderTask(SpiderExecutor executor, long nanoTime, SpiderBeanFactory spiderBeanFactory, HttpRequest request, GeccoEngine engine) {
        super(executor, nanoTime);
        this.spiderBeanFactory = spiderBeanFactory;
        this.request = request;
        this.engine = engine;
    }

    @Override
    protected void spider() {
        if(currSpiderBeanClass == null) {
            currSpiderBeanClass = spiderBeanFactory.matchSpider(request);
        }
        if(log.isDebugEnabled()){
            log.debug("match url : "+request.getUrl()+"/"+(currSpiderBeanClass == null ? "null" : currSpiderBeanClass.getName()));
        }

        HttpResponse response = null;
        try {
            if(currSpiderBeanClass == null){
                response = defaultDownload(request);
                if(response != null && (response.getStatus() == 302 || response.getStatus() == 301)){
                    executor.executor(response.getContent());
                }else{
                    log.error("can't match url : "+request.getUrl());
                }
            }else{
                if(spiderBeanContext == null) {
                    // 获取 SpiderBean 的上下文, downloader, beforeDownloader, afterDownloader, render, piplines
                    spiderBeanContext = spiderBeanFactory.getContext(currSpiderBeanClass);
                }
                response = download(spiderBeanContext, request);
                if(response.getStatus() == 200){
                    // render
                    Render render = spiderBeanContext.getRender();

                    SpiderBean spiderBean = null;
                    spiderBean = render.inject(currSpiderBeanClass, request, response);

                    // pipelines
                    pipelines(spiderBean, spiderBeanContext);
                }else if(response.getStatus() == 302 || response.getStatus() == 301){
                    executor.executor(response.getContent());
                }
            }
        }catch (Exception e){
            log.error(String.format("%s ERROR %s/%s", request.getUrl(), e.getClass().getName(), e.getMessage()), e);
            if(e instanceof DownloadTimeoutException){
                // 开启代理，并且获取代理不为 null
                if(engine.isProxy() && engine.getProxysLoader().getProxy() != null){
                    if(log.isDebugEnabled()){
                        log.debug(request.getUrl()+" ERROR : connect time out!");
                    }
                }
            }
        }finally {
            if(response != null){
                response.close();
            }
        }
    }


    /**
     * 默认下载
     *
     * @param request
     * @return
     */
    private HttpResponse defaultDownload(HttpRequest request) throws DownloadException {
        HttpResponse response = download(null, request);
        return response;
    }

    private HttpResponse download(SpiderBeanContext context, HttpRequest request) throws DownloadException {
        Downloader currDownloader = null;
        BeforeDownload before = null;
        AfterDownload after = null;
        int timeout = 1000;
        if(context != null) {
            currDownloader = context.getDownloader();
            before = context.getBeforeDownload();
            after = context.getAfterDownload();
            timeout = context.getTimeout();
        } else {
            currDownloader = spiderBeanFactory.getDownloaderFactory().defaultDownloader();
        }
        if(before != null) {
            before.process(request);
        }
        HttpResponse response = currDownloader.download(request, timeout);
        if(after != null) {
            after.process(request, response);
        }
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void pipelines(SpiderBean spiderBean, SpiderBeanContext context) {
        if(spiderBean == null) {
            return ;
        }
        List<Pipeline> pipelines = context.getPipelines();
        if(pipelines != null) {
            for(Pipeline pipeline : pipelines) {
                pipeline.process(spiderBean);
            }
        }
    }
}
