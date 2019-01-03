package com.geccocrawler.gecco.test.pipeline;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.spider.SpiderBean;

/**
 * @author: zhangwei
 * @date: 14:07/2019-01-01
 */
@PipelineName("PrintPipline")
public class PrintPipline implements Pipeline {

    @Override
    public void process(SpiderBean bean) {
        System.out.println(bean.toString());
    }
}
