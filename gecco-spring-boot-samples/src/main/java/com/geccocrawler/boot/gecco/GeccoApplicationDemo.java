package com.geccocrawler.boot.gecco;

import com.geccocrawler.gecco.spider.SpiderLoopGroup;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

/**
 * @author: zhangwei
 * @date: 22:28/2019-01-03
 */
@SpringBootApplication
public class GeccoApplicationDemo {


    private static final String[] urls = {
            "https://github.com/trending/go?since=daily",
            "https://github.com/trending/java?since=daily",
            "https://github.com/trending/python?since=daily",
            "https://github.com/trending/c++?since=daily",
    };

    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(GeccoApplicationDemo.class).run(args);

        SpiderLoopGroup group = context.getBean(SpiderLoopGroup.class);

        for(String url : urls){
            group.executor(url);
        }
    }
}
