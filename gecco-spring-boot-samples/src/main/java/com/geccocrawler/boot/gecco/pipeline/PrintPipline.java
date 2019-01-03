package com.geccocrawler.boot.gecco.pipeline;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.spider.SpiderBean;
import org.springframework.stereotype.Component;

/**
 * @author: zhangwei
 * @date: 14:07/2019-01-01
 */
@PipelineName("PrintPipline")
@Component("PrintPipline")
public class PrintPipline implements Pipeline {

    @Override
    public void process(SpiderBean bean) {
        System.out.println(bean.toString());
    }
}
