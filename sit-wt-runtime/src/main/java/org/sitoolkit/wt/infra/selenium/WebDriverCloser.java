package org.sitoolkit.wt.infra.selenium;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.infra.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverCloser {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverCloser.class);

    private List<WebDriver> driverList = new ArrayList<>();

    @Resource
    PropertyManager pm;

    public void register(WebDriver driver) {
        driverList.add(driver);
    }

    /**
    *
    */
    @PreDestroy
    public void preDestroy() {
        if (pm.isRemoteDriver()) {
            return;
        }

        driverList.parallelStream().forEach(driver -> {
            LOG.debug("WebDriverを停止します {}", driver);
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.trace(e.getMessage());
            }
        });
    }

}
