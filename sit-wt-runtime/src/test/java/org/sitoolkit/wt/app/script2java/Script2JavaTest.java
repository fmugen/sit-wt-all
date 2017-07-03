package org.sitoolkit.wt.app.script2java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class Script2JavaTest {

    @Test
    public void testLoad() {
        TestClass actual = loadScript("testscript/a/b/c/ABCTestScript.xlsx", "testscript");

        assertThat("スクリプトパス", actual.getScriptPath(), is("testscript/a/b/c/ABCTestScript.xlsx"));
        assertThat("テストクラス物理名", actual.getFileBase(), is("ITABCTestScript"));
        assertThat("テストクラスファイル拡張子", actual.getFileExt(), is("java"));
        assertThat("テストクラス出力ディレクトリ", actual.getOutDir(),
                is(FilenameUtils.separatorsToSystem("target/generated-test-sources/test/a/b/c/")));
        assertThat("テストクラスパッケージ名", actual.getPkg(), is("a.b.c"));
    }

    @Test
    public void testMultiByteScript() {
        TestClass actual = loadScript("src/test/resources/テスト-スクリプト(サンプル).xlsx",
                "src/test/resources");

        assertThat("スクリプトパス", actual.getScriptPath(),
                is("src/test/resources/テスト-スクリプト(サンプル).xlsx"));
        assertThat("テストクラス物理名", actual.getFileBase(), is("ITテスト_スクリプト_サンプル_"));
        assertThat("テストクラスファイル拡張子", actual.getFileExt(), is("java"));
        assertThat("テストクラス出力ディレクトリ", actual.getOutDir(),
                is(FilenameUtils.separatorsToSystem("target/generated-test-sources/test/")));
        assertNull("テストクラスパッケージ名", actual.getPkg());
    }

    private TestClass loadScript(String testscript, String scriptDir) {
        Script2Java gen = new Script2Java();
        TestClass actual = new TestClass();
        gen.load(actual, new File(".", testscript), scriptDir);

        return actual;
    }
}
