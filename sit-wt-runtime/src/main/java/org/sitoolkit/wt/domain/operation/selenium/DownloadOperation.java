/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.operation.selenium;

import java.io.File;
import java.net.URL;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceManager;
import org.sitoolkit.wt.domain.evidence.MessagePattern;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.SitRepository;
import org.sitoolkit.wt.infra.TestException;
import org.sitoolkit.wt.util.app.proxysetting.ProxySettingService;
import org.springframework.stereotype.Component;

/**
 *
 * @author takuya.kumakura
 */
@Component("downloadOperation")
public class DownloadOperation extends SeleniumOperation {

    @Resource
    EvidenceManager em;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        ctx.info(MessagePattern.項目をXXします, "ダウンロード");

        try {
            URL targetUrl = (testStep.getLocator().isNa()) ? new URL(seleniumDriver.getCurrentUrl())
                    : new URL(findElement(testStep.getLocator()).getAttribute("href"));

            File tmpDir = new File(SitRepository.getRepositoryPath(), "temporary");
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }

            String fileName = "No" + testStep.getNo() + "_"
                    + StringUtils.substringAfterLast(targetUrl.toString(), "/");
            File tempFile = new File(tmpDir, fileName);

            ProxySettingService proxyService = new ProxySettingService();
            proxyService.loadProxy();
            FileUtils.copyURLToFile(targetUrl, tempFile);

            em.storeDownladEvidence(tempFile);

            String evidencePath = em.getDownloadDir().getAbsolutePath() + "/" + fileName;
            String linkAddedMsg = "<a href=\"" + evidencePath + "\" target=\"evidence\">"
                    + testStep.getItemName() + "</a>をダウンロードしました";
            ctx.getRecords().get(ctx.getRecords().size() - 1).setLog(linkAddedMsg);

        } catch (Exception exp) {
            throw new TestException(exp);
        }
    }
}
