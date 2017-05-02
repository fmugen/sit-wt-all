package org.sitoolkit.wt.util.infra.process;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConversationProcessTest {

    @Test
    public void startTestAddCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        params.getExitClallbacks().add(exitCode -> {
            // NOP
        });

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        assertThat(testListener.getLines().isEmpty(), is(true));
    }

    @Test
    public void startTestWithoutCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        assertThat(testListener.getLines().isEmpty(), is(true));
    }

    @Test
    public void startWithProcessWaitTestAddCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        params.getExitClallbacks().add(exitCode -> {
            // NOP
        });

        ConversationProcess process = ConversationProcessContainer.create();
        process.startWithProcessWait(params);

        assertLines(testListener.getLines());
    }

    @Test
    public void startWithProcessWaitTestWithoutCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        ConversationProcess process = ConversationProcessContainer.create();
        process.startWithProcessWait(params);

        assertLines(testListener.getLines());
    }

    private List<String> testCommand() {
        File script = new File(getClass().getResource(
                "/org/sitoolkit/wt/util/infra/process").getPath(), "testScript.cmd");

        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add(script.getAbsolutePath());

        return command;
    }

    private void assertLines(List<String> lines) {
        String currentDir = System.getProperty("user.dir");
        assertThat(lines.get(0), is(""));
        assertThat(lines.get(1), is(currentDir + ">echo test script start "));
        assertThat(lines.get(2), is("test script start"));
        assertThat(lines.get(3), is(""));
        assertThat(lines.get(4), is(currentDir + ">echo test script end "));
        assertThat(lines.get(5), is("test script end"));
    }
}
