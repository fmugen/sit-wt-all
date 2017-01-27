package org.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.List;

public class TestRunParams {

    private File baseDir;

    private String baseUrl;

    private List<File> scripts;

    private boolean debug;

    private boolean parallel;

    private String driverType;

    private String targetScriptsStr;

    public TestRunParams() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<File> getScripts() {
        return scripts;
    }

    public void setScripts(List<File> scripts) {
        this.scripts = scripts;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public String getTargetScriptsStr() {
        return targetScriptsStr;
    }

    public void setTargetScriptsStr(String targetScriptsStr) {
        this.targetScriptsStr = targetScriptsStr;
    }

}
