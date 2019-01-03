package com.geccocrawler.boot.gecco.spider;

import com.geccocrawler.boot.gecco.GeccoProperties;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spider.MultiSpiderExecutorGroup;
import com.geccocrawler.gecco.spider.NamedThreadFactory;
import com.geccocrawler.gecco.spider.SpiderExecutor;
import org.springframework.context.ApplicationContext;

/**
 * @author: zhangwei
 * @date: 20:51/2019-01-03
 */
public class SpringSpiderLoopGroup extends MultiSpiderExecutorGroup {

    private GeccoEngine engine;

    public SpringSpiderLoopGroup(GeccoProperties geccoProperties, ApplicationContext context) {
        super(geccoProperties.getnThreand(), geccoProperties, context);
    }

    @Override
    protected SpiderExecutor newChild(Object... args) {
        GeccoProperties properties = (GeccoProperties) args[0];
        ApplicationContext context = (ApplicationContext) args[1];
        if(engine == null){
            engine = newGeccoEngine(properties, context);
        }
        return new SprintSpiderLoop(this, engine, new NamedThreadFactory(properties.getThreadPrefix()));
    }


    private static GeccoEngine newGeccoEngine(GeccoProperties properties, ApplicationContext context){
        GeccoEngine engine;

        if(properties.getPipelineFactory() != null){
            engine = GeccoEngine.create(properties.getClassPath(), properties.getPipelineFactory());
        }else{
            engine = GeccoEngine.create(properties.getClassPath());
            engine.pipelineFactory(context.getBean("SpringPipelineFactory", PipelineFactory.class));
        }

        if(properties.getProxys() != null){
            engine.proxysLoader(properties.getProxys());
        }

        engine.mobile(properties.isMobile())
                .debug(properties.isDebug())
                .proxy(properties.isProxy())
                .retry(properties.getRetry())
                .thread(1)
                .loop(true)
                .monitor(false);

        engine.run();
        engine.engineStop();
        return engine;
    }
}
