package com.leozhou.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by zhouchunjie on 2016/12/11.
 */
public class GithubRepoPageProcessor implements PageProcessor {

    //part one: configuration of clawing, including encoding, claw interval, retry time and more.
    private Site site = Site.me().setRetrySleepTime(3).setSleepTime(1000);

    @Override
    // process is interface of custom spider logic, need to write claw logic here
    public void process(Page page) {
        //part two: define how to claw information from page, and save it.
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name") == null){
            //skip the page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        //part three: get next url from page
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new GithubRepoPageProcessor())
                //从https://github.com/code4craft开始抓取
                .addUrl("https://github.com/code4craft")
                //开启5个线程抓取
                .thread(5)
                //启动爬取
                .run();
    }
}
