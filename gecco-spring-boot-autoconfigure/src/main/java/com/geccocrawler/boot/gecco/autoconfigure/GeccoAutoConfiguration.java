package com.geccocrawler.boot.gecco.autoconfigure;

import com.geccocrawler.boot.gecco.GeccoProperties;
import com.geccocrawler.boot.gecco.spider.SpringPipelineFactory;
import com.geccocrawler.boot.gecco.spider.SpringSpiderLoopGroup;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.downloader.proxy.FileProxys;
import com.geccocrawler.gecco.downloader.proxy.Proxys;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spider.SpiderLoopGroup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
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
@ComponentScan(basePackageClasses = GeccoProperties.class)
public class GeccoAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = GECCO_PREFIX, name = "classpath")
    public SpringSpiderLoopGroup getSpiderLoopGroup(GeccoProperties properties, ApplicationContext context){
        return new SpringSpiderLoopGroup(properties, context);
    }


    @Bean("SpringPipelineFactory")
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
