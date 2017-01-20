package org.sitoolkit.wt.domain.operation.selenium;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

/**
 *
 */
public class DbVerifyOperationTest extends SitTesterTestBase {

    @BeforeClass
    public static void runDerby() {
        String derbyDir = System.getProperty("basedir") + "/target/derby/testdb";
        Connection conn = null;
        Boolean result = null;

        try {
            conn = DriverManager.getConnection("jdbc:derby:" + derbyDir + ";create=true");
            Statement state = conn.createStatement();

            String files[] = { "tools/db/01-create-experiment.sql",
                    "tools/db/02-insert-experiment.sql" };
            for (String file : files) {
                String query = Files.lines(Paths.get(file), Charset.forName("UTF-8"))
                        .collect(Collectors.joining(System.getProperty("line.separator")));
                result = state.execute(query.replaceAll(";$", ""));
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Test
    public void test001() {
        test();
    }

    @Test
    public void test002() {
        test();
    }

    @Test
    public void test003() {
        test();
    }

    @Test
    public void test004() {
        test();
    }

    @Test
    public void test005() {
        test();
    }

    @Test
    public void test006() {
        test();
    }

    @Test
    public void test007() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/selenium/DBVerifyOperationTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }
}
