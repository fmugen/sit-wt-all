package org.sitoolkit.wt.gui.app.script;

import org.sitoolkit.wt.gui.domain.script.ScriptProcessClient;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class ScriptService {

    ScriptProcessClient client = new ScriptProcessClient();

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessExitCallback callback) {

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.page2script(driverType, baseUrl, params);
    }

    public ConversationProcess ope2script(String url) {
        return client.ope2script(url);
    }

    public ConversationProcess getCaseNo(String scriptPath, GetCaseNoStdoutListener listener, ProcessExitCallback callback) {
        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);
        params.getStdoutListeners().add(listener);

        return client.getCaseNo(scriptPath, params);

    }
}
