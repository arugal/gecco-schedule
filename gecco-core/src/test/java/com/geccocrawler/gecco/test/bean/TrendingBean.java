package com.geccocrawler.gecco.test.bean;

import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.Html;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.RequestParameter;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;

import java.util.List;

/**
 * @author: zhangwei
 * @date: 14:00/2019-01-01
 */
@Gecco(matchUrl = "https://github.com/trending/{languages}?since={time}", pipelines = {"PrintPipline"})
@Data
public class TrendingBean implements HtmlBean {
    private static final long serialVersionUID = 7604223689378270484L;

    @RequestParameter("languages")
    private String languages;

    @RequestParameter("time")
    private String time;

    @Html
    @HtmlField(cssPath = "body > div.application-main > div.explore-pjax-container.container-lg.p-responsive.clearfix > div > div.col-md-9.float-md-left > div.explore-content > ol > li")
    private List<RepositorieBean> repositories;
}
