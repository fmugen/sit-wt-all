package org.sitoolkit.wt.domain.operation.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

/**
 *
 */
public class DbVerifyOperationTest extends SitTesterTestBase {

    @BeforeClass
    public static void runDerby() throws Exception {
        Connection conn = null;
        Boolean result = null;

        try {
            String derbyDir = "target/derby/testdb";
            File beforeDir = new File(derbyDir);
            if (beforeDir.exists()) {
                if (!rmTree(beforeDir)) {
                    throw new Exception(derbyDir + " delete failed.");
                }
            }

            conn = DriverManager.getConnection("jdbc:derby:" + derbyDir + ";create=true");
            Statement state = conn.createStatement();

            String files[] = { "tools/db/01-create-experiment.sql",
                    "tools/db/02-insert-experiment.sql" };
            for (String file : files) {
                String query = FileUtils.readFileToString(new File(file), Charset.forName("UTF-8"));
                result = state.execute(query.replaceAll(";$", ""));
            }

        } catch (Exception exp) {
            throw exp;
        }
    }

    @Test
    public void test001() {
        test();
    }

    @Test
    public void test002() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.indexOf("Exception") != -1) {
                    expCnt++;
                    assertThat(line, startsWith("\torg.sitoolkit.wt.infra.VerifyException"));
                }
            }
            assertThat(expCnt, is(4));
        }
    }

    @Test
    public void test003() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.startsWith("Caused by:")) {
                    expCnt++;
                    assertThat(line, startsWith(
                            "Caused by: org.springframework.dao.EmptyResultDataAccessException"));
                }
            }
            assertThat(expCnt, is(1));
        }
    }

    @Test
    public void test004() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.startsWith("Caused by:")) {
                    expCnt++;
                    assertThat(line, startsWith(
                            "Caused by: org.springframework.dao.IncorrectResultSizeDataAccessException"));
                }
            }
            assertThat(expCnt, is(1));
        }
    }

    @Test
    public void test005() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.startsWith("Caused by:")) {
                    expCnt++;
                    assertThat(line, startsWith(
                            "Caused by: org.springframework.dao.InvalidDataAccessApiUsageException"));
                }
            }
            assertThat(expCnt, is(1));
        }
    }

    @Test
    public void test006() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.startsWith("Caused by:")) {
                    expCnt++;
                    if (expCnt == 1) {
                        assertThat(line, startsWith(
                                "Caused by: org.springframework.jdbc.BadSqlGrammarException"));
                    } else if (expCnt == 2) {
                        assertThat(line, startsWith("Caused by: java.sql.SQLSyntaxErrorException"));
                    } else if (expCnt == 3) {
                        assertThat(line, startsWith("Caused by: ERROR 42X01"));
                    }
                }
            }
            assertThat(expCnt, is(3));
        }
    }

    @Test
    public void test007() {
        try {
            test();
        } catch (AssertionError err) {
            int expCnt = 0;
            for (String line : err.getMessage().split("[\r\n]")) {
                if (line.startsWith("Caused by:")) {
                    expCnt++;
                    assertThat(line, startsWith("Caused by: java.io.FileNotFoundException"));
                }
            }
            assertThat(expCnt, is(1));
        }
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/selenium/DBVerifyOperationTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

    public static boolean rmTree(File target) {
        boolean result = false;

        if (target.isDirectory()) {
            int delCnt = 0;
            boolean childResult = false;

            for (File child : target.listFiles()) {
                delCnt++;
                childResult = rmTree(child);

                if (!childResult) {
                    result = childResult;
                    break;
                }
            }

            if (childResult || delCnt == 0) {
                result = target.delete();
            }
        } else {
            result = target.delete();
        }

        return result;
    }
}
