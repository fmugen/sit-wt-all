package org.sitoolkit.wt.domain.testscript;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.util.tabledata.csv.TableDataDaoCsvImpl;
import org.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestScriptDao {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TEMPLATE_PATH = "classpath:TestScriptTemplate.xlsx";

    @Resource
    ApplicationContext appCtx;

    @Resource
    OperationConverter operationConverter;

    @Resource
    TableDataMapper dm;

    @Resource
    TableDataDaoExcelImpl excelDao;

    @Resource
    TableDataDaoCsvImpl csvDao;

    @Resource
    FileOverwriteChecker fileOverwriteChecker;

    public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);
        TestScriptDao testScriptDao = appCtx.getBean(TestScriptDao.class);

        if (args.length < 1) {
            testScriptDao.log.info("テストスクリプトを指定してください。");
            testScriptDao.log.info(">java {} [scriptPath sheetName]",
                    TestScriptDao.class.getName());
            System.exit(1);
        }

        String scriptPath = args[0];
        String sheetName = args[1];

        TestScript testScript = testScriptDao.load(scriptPath, sheetName, true);

        List<String> caseNoList = new ArrayList<>(testScript.getCaseNoMap().keySet());
        for (String caseNo : caseNoList) {
            System.out.println("Case No:" + caseNo);
        }

        appCtx.close();
        System.exit(0);
    }

    public TestScript load(String scriptPath, String sheetName, boolean loadCaseOnly) {
        return load(new File(scriptPath), sheetName, loadCaseOnly);
    }

    public TestScript load(File scriptFile, String sheetName, boolean loadCaseOnly) {
        TestScript testScript = appCtx.getBean(TestScript.class);

        testScript.setSheetName(sheetName);
        testScript.setScriptFile(scriptFile);
        testScript.setLastModified(scriptFile.lastModified());
        testScript.setName(scriptFile.getName());

        String scriptFileName = testScript.getName();

        if (scriptFileName.endsWith(".xlsx") || scriptFileName.endsWith(".xls")) {
            loadScript(excelDao, testScript, testScript.getSheetName(), loadCaseOnly);
        } else if (scriptFileName.endsWith(".csv")) {
            loadScript(csvDao, testScript, "", loadCaseOnly);
        }

        return testScript;
    }

    private void loadScript(TableDataDao dao, TestScript testScript, String sheetName,
            boolean loadCaseOnly) {
        TableData table = dao.read(testScript.getScriptFile().getAbsolutePath(), sheetName);

        RowData firstRow = table.getRows().iterator().next();
        int testDataIndex = 0;
        for (Map.Entry<String, String> entry : firstRow.getData().entrySet()) {
            if (entry.getKey().startsWith(testScript.getCaseNoPrefix())) {
                String caseNo = StringUtils.substringAfter(entry.getKey(),
                        testScript.getCaseNoPrefix());
                testScript.getCaseNoMap().put(caseNo, testDataIndex++);
            }
        }

        if (loadCaseOnly) {
            return;
        }

        for (RowData row : table.getRows()) {
            TestStep testStep = dm.map("testStep", row, TestStep.class);
            testScript.addTestStep(testStep);
        }
    }

    public String write(String filePath, List<TestStep> testStepList, boolean overwrite) {
        return write(new File(filePath), testStepList, overwrite);
    }

    public String write(File file, List<TestStep> testStepList, boolean overwrite) {
        File dir = file.getParentFile();
        if (dir == null) {
            dir = new File(".");
        } else if (!dir.exists()) {
            dir.mkdirs();
        }

        TableDataCatalog catalog = TestScriptConvertUtils.getTableDataCatalog(testStepList);
        String fileName = sanitizeFileName(file.getName());
        file = new File(file.getParent(), fileName);

        fileOverwriteChecker.setRebuild(overwrite);

        excelDao.write(catalog.get("TestScript"), TEMPLATE_PATH, file.getAbsolutePath(), null);

        return file.getAbsolutePath();
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }

}
