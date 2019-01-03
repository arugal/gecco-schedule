package com.geccocrawler.boot.gecco.context.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.geccocrawler.boot.gecco.util.GeccoUtil.*;

/**
 * @author: zhangwei
 * @date: 20:12/2019-01-03
 */
@Order(LoggingApplicationListener.DEFAULT_ORDER + 1)
public class WelcomeLogoApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static AtomicBoolean processed = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if(processed.get()){
            return;
        }

        final Log logger = LogFactory.getLog(getClass());

        String bannerText = buildBannerText();

        if(logger.isInfoEnabled()){
            logger.info(bannerText);
        }else{
            System.out.println(bannerText);
        }

        processed.compareAndSet(false, true);
    }

    String buildBannerText(){

        StringBuilder bannerTextBuilder = new StringBuilder();

        bannerTextBuilder
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)
                .append(" :: Gecco Spring Boot (v").append("1.3.1").append(") : ")
                .append(GECCO_SPRINT_BOOT_GITHUB_URL)
                .append(LINE_SEPARATOR)
                .append(" :: Gecco (v").append("1.3.1").append(") : ")
                .append(GECCO_GITHUB_URL)
                .append(LINE_SEPARATOR);

        return bannerTextBuilder.toString();
    }
}
