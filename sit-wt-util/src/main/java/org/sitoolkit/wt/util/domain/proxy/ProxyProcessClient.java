package org.sitoolkit.wt.util.domain.proxy;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class ProxyProcessClient {

    public void readProxy(ProcessParams params) {
        List<String> command = new ArrayList<>();
        command.add("reg");
        command.add("query");
        command.add("\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\"");
        command.add("/v");
        command.add("Proxy*");
        params.setCommand(command);
        params.setProcessWait(true);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }
}
