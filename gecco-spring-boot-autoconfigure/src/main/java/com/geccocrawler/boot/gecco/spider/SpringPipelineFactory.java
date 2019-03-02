package com.geccocrawler.boot.gecco.spider;

import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.util.ObjectUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: zhangwei
 * @date: 21:24/2019-01-03
 */
public class SpringPipelineFactory implements PipelineFactory, ApplicationContextAware {

    private ApplicationContext context;

    private final Class<Pipeline> pip = Pipeline.class;

    @Override
    public Pipeline<? extends SpiderBean> getPipeline(String s) {
        ObjectUtil.checkNotNull(s, "pipelineName");
        String [] beanNames = context.getBeanNamesForType(pip);
        for(String name : beanNames){
            if(name.equals(s)){
                return context.getBean(s, pip);
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
