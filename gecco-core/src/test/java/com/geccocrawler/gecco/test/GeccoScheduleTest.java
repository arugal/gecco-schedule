package com.geccocrawler.gecco.test;

import com.geccocrawler.gecco.spider.SpiderExecutorGroup;
import com.geccocrawler.gecco.spider.SpiderLoopGroup;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangwei
 * @date: 16:36/2019-01-02
 */
public class GeccoScheduleTest {

    private static final String url = "https://github.com/trending/go?since=daily";

    public static void main(String[] args) throws InterruptedException {
        SpiderExecutorGroup group = new SpiderLoopGroup("com.geccocrawler.gecco.test");

        group.scheduleAtFixedRate(url, 0, 20, TimeUnit.SECONDS);

        Scanner scanner = new Scanner(System.in, "UTF-8");

        while (scanner.hasNext()){
            String command = scanner.next();
            System.out.println(">. "+command);
            if(command.equalsIgnoreCase("quit")){
                break;
            }else if(command.equalsIgnoreCase("pause")){
                group.pause();
            }else if(command.equalsIgnoreCase("renew")){
                group.renew();
            }else if(command.equalsIgnoreCase("shutdown")){
                group.shutdown();
            }
        }

        if(!group.isShuttingDown()){
            group.shutdown();
        }
        Thread.sleep(5000L);
        System.out.println("over");
    }
}
