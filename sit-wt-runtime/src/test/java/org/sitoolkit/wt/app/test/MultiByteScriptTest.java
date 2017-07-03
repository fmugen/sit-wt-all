package org.sitoolkit.wt.app.test;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import org.sitoolkit.wt.infra.ApplicationContextHelper;

public class MultiByteScriptTest extends SitTesterTestBase {

    @Test
    public void testMultiByteCase() {
        TestCase testCase = TestCase.parse(getTestScriptPath());

        String scriptPath = testCase.getScriptPath();
        String sheetName = testCase.getSheetName();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);
        TestScript script = catalog.get(scriptPath, sheetName);

        for (String caseNo : script.getCaseNoMap().keySet()) {
            setUp();
            test(caseNo, null);
        }
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/テスト-スクリプト(サンプル).xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }
}
