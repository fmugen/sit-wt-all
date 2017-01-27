package org.sitoolkit.wt.app.ope2script;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.sitoolkit.wt.infra.MultiThreadUtils;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class FirefoxOpener {

    private static final Logger LOG = LoggerFactory.getLogger(FirefoxOpener.class);

    private FirefoxManager ffManager = new FirefoxManager();

    private String[] guidanceResources = new String[] { "css/bootstrap.min.css", "css/style.css" };

    private String guidancePath = "guidance/guidance-ope2script.html";

    public FirefoxOpener() {
    }

    public static void main(String[] args) {
        System.exit(new FirefoxOpener().open());
    }

    /**
     * <ul>
     * <li>Firefoxがインストールされていない場合、インストールする
     * <li>Selenium IDEがインストールされていない場合、インストールする
     * <li>Firefoxを起動する
     * </ul>
     */
    public int open() {
        try {

            File guidanceFile = new File(guidancePath);
            if (!guidanceFile.exists()) {
                LOG.info("ガイダンスファイルを展開します {}", guidanceFile.getAbsolutePath());
                URL resourceUrl = ResourceUtils.getURL("classpath:" + guidancePath);
                FileUtils.copyURLToFile(resourceUrl, guidanceFile);
                for (String guidanceRes : guidanceResources) {
                    URL url = ResourceUtils.getURL("classpath:guidance/" + guidanceRes);
                    File dstFile = new File(guidanceFile.getParent(), guidanceRes);
                    FileUtils.copyURLToFile(url, dstFile);
                }
            }

            FirefoxProfile profile = new FirefoxProfile();
            profile.addExtension(ffManager.getSeleniumIdeUnarchivedDir());

            FirefoxBinary ffBinary = ffManager.getFirefoxBinary();

            LOG.info("Firefoxを起動します {}", ffBinary);

            WebDriver driver = MultiThreadUtils
                    .submitWithProgress(() -> new FirefoxDriver(ffBinary, profile));
            String url = System.getProperty("url");
            if (StringUtils.isNotEmpty(url)) {
                driver.get(url);
            }

            // wait for Firefox window is closed
            ffBinary.waitFor();

            return 0;
        } catch (Exception e) {
            LOG.error("予期しない例外が発生しました", e);
            return 1;
        }
    }

}
