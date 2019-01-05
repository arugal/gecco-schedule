package com.geccocrawler.boot.gecco.bean;

import com.geccocrawler.gecco.annotation.Html;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Text;
import com.geccocrawler.gecco.spider.HtmlBean;
import lombok.Data;

/**
 * @author: zhangwei
 * @date: 14:29/2019-01-01
 */
@Data
public class RepositorieBean implements HtmlBean {

    private static final long serialVersionUID = 2970147343550076493L;

    @Text
    @HtmlField(cssPath = "div.d-inline-block.col-9.mb-1 > h3 > a > span")
    private String owner;

    @Text
    @HtmlField(cssPath = "div.d-inline-block.col-9.mb-1 > h3 > a")
    private String name;

    @Text
    @HtmlField(cssPath = "div.py-1 > p")
    private String describe;

    @Text
    @HtmlField(cssPath = "div.f6.text-gray.mt-2 > a:nth-child(2)")
    private String star;

    @Text
    @HtmlField(cssPath = "div.f6.text-gray.mt-2 > a:nth-child(3)")
    private String fork;

    @Text
    @HtmlField(cssPath = "div.f6.text-gray.mt-2 > span.d-inline-block.float-sm-right")
    private String newStar;
}
