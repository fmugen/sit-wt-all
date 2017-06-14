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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.sitoolkit.wt.domain.evidence.EvidenceManager;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

/**
 *
 * @author takuya.kumakura
 */
public class DownloadOperationTest extends SitTesterTestBase {

    @Resource
    EvidenceManager em;

    @Test
    public void test001() throws FileNotFoundException, IOException {
        test();

        File targetFile = new File("src/main/webapp/pdf/DownloadTest.pdf");
        String targetHash = DigestUtils.md5Hex(new FileInputStream(targetFile));

        File evidenceDir = em.getDownloadDir();
        File firstEvidence = new File(evidenceDir, "No2_DownloadTest.pdf");
        File secondEvidence = new File(evidenceDir, "No5_DownloadTest.pdf");

        assertThat(DigestUtils.md5Hex(new FileInputStream(firstEvidence)), is(targetHash));
        assertThat(DigestUtils.md5Hex(new FileInputStream(secondEvidence)), is(targetHash));

    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/DownloadOperationTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
