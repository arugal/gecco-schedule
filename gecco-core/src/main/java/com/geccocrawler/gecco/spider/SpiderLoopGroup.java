package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.GeccoEngine;

import static com.geccocrawler.gecco.spider.SpiderConfig.DEFAULT_SPIDER_LOOP_THREADS;

/**
 * @author: zhangwei
 * @date: 22:44/2019-01-01
 */
public class SpiderLoopGroup extends MultiSpiderExecutorGroup{

    private GeccoEngine engine;

    public SpiderLoopGroup(String classPath){
        super(DEFAULT_SPIDER_LOOP_THREADS, new SpiderConfig(classPath));
    }

    public SpiderLoopGroup(SpiderConfig spiderConfig){
        super(spiderConfig.getNThreand(), spiderConfig);
    }

    @Override
    protected SpiderExecutor newChild(Object ... args) {
        SpiderConfig spiderConfig = (SpiderConfig) args[0];
        if(engine == null){
            engine = newGeccoEngic(spiderConfig);
        }
        return new SpiderLoop(this, engine, new NamedThreadFactory(spiderConfig.getThreadPrefix()), spiderConfig);
    }

    private static GeccoEngine newGeccoEngic(SpiderConfig spiderConfig){
        GeccoEngine engine;
        if(spiderConfig.getPipelineFactory() == null){
            engine = GeccoEngine.create(spiderConfig.getClassPath());
        }else{
            engine = GeccoEngine.create(spiderConfig.getClassPath(), spiderConfig.getPipelineFactory());
        }

        if(spiderConfig.getProxys() != null){
            engine.proxysLoader(spiderConfig.getProxys());
        }


//        engine.monitor(spiderConfig.isMobile()) 1.3.0 版本暂时不支持 monitor 设置
                engine.debug(spiderConfig.isDebug())
                .proxy(spiderConfig.isProxy())
                .retry(spiderConfig.getRetry())
                .thread(1)
                .loop(true);
        engine.run();
        engine.engineStop();
        return engine;
    }
}
