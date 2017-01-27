package org.sitoolkit.wt.gui.domain.script;

import java.util.ArrayList;
import java.util.List;

public class LastCaseId {
    private String filePath = "";

    private List<String> caseIdList = new ArrayList<>();

    private long lastModify = 0L;

    public LastCaseId (String filePath, List<String> caseIdList, long lastModify) {
        this.filePath = filePath;
        this.caseIdList = caseIdList;
        this.lastModify = lastModify;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getCaseIdList() {
        return caseIdList;
    }

    public void setCaseIdList(List<String> caseIdList) {
        this.caseIdList = caseIdList;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

}
