package org.sitoolkit.wt.domain.pageload.selenium;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;
import org.sitoolkit.wt.infra.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class SeleniumPageLietener implements PageListener {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumPageLietener.class);

    @Resource
    WebDriver driver;

    @Resource
    PropertyManager pm;

    private String[] guidanceResources = new String[] { "css/bootstrap.min.css", "css/style.css" };

    private String guidanceFilePath = "guidance/guidance-page2script.html";

    @Override
    public void setUpPage(PageContext ctx) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        ctx.setTitle(driver.getTitle());
        ctx.setUrl(driver.getCurrentUrl());
    }

    @Override
    public void tearDownPage(PageContext ctx) {
        driver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void setUp() {

        File guidanceFile = new File(guidanceFilePath);
        if (!guidanceFile.exists()) {
            LOG.info("ガイダンスファイルを展開します {}", guidanceFile.getAbsolutePath());
            try {
                URL resourceUrl = ResourceUtils.getURL("classpath:" + guidanceFilePath);
                FileUtils.copyURLToFile(resourceUrl, guidanceFile);
                for (String guidanceRes : guidanceResources) {
                    URL url = ResourceUtils.getURL("classpath:guidance/" + guidanceRes);
                    File dstFile = new File(guidanceFile.getParent(), guidanceRes);
                    FileUtils.copyURLToFile(url, dstFile);
                }
            } catch (IOException e) {
                LOG.error("ガイダンスファイル展開処理で例外が発生しました", e);
            }
        }

        // touch WebDriver instance to start Browser
        String driverType = driver.toString();
        LOG.info("ブラウザを起動します {}", driverType);

        String url = System.getProperty("url");
        if (StringUtils.isNotEmpty(url)) {
            driver.get(url);
        }
    }

    @Override
    public void tearDown() {
        // driver.quit();
    }

}
