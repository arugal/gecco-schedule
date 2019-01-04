package com.geccocrawler.boot.gecco.autoconfigure;

import com.geccocrawler.boot.gecco.GeccoConfig;
import com.geccocrawler.boot.gecco.spider.SpringPipelineFactory;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.downloader.proxy.FileProxys;
import com.geccocrawler.gecco.downloader.proxy.Proxys;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spider.SpiderConfig;
import com.geccocrawler.gecco.spider.SpiderLoopGroup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.geccocrawler.boot.gecco.util.GeccoUtil.GECCO_PREFIX;

/**
 * @author: zhangwei
 * @date: 19:57/2019-01-03
 */
@Configuration
@ConditionalOnProperty(prefix = GECCO_PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass({GeccoEngine.class, SpiderLoopGroup.class})
@ComponentScan(basePackageClasses = GeccoConfig.class)
public class GeccoAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = GECCO_PREFIX, name = "classpath")
    public SpiderLoopGroup getSpiderLoopGroup(GeccoConfig geccoConfig){
        SpiderConfig spiderConfig = new SpiderConfig()
                .classPath(geccoConfig.getClassPath())
                .nThreand(geccoConfig.getnThreand())
                .threadPrefix(geccoConfig.getThreadPrefix())
                .mobile(geccoConfig.isMobile())
                .debug(geccoConfig.isDebug())
                .retry(geccoConfig.getRetry())
                .proxy(geccoConfig.isProxy())
                .pipelineFactory(geccoConfig.getPipelineFactory())
                .proxys(geccoConfig.getProxys());
        return new SpiderLoopGroup(spiderConfig);
    }


    @Bean("springPipelineFactory")
    @ConditionalOnMissingBean(value = PipelineFactory.class)
    public PipelineFactory getPipelineFactory(){
        return new SpringPipelineFactory();
    }

    @Bean("proxys")
    @ConditionalOnMissingBean(value = Proxys.class)
    public Proxys getProxys(){
        return new FileProxys();
    }
}
