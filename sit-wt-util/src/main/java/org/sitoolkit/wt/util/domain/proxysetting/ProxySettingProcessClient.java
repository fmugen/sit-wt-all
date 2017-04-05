package org.sitoolkit.wt.util.domain.proxysetting;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.util.infra.process.ProcessParams;
import org.sitoolkit.wt.util.infra.proxysetting.ProxySetting;

public class ProxySettingProcessClient {

    public ProxySetting getRegistryProxy(ProcessParams params) {
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

        ProxySetting proxySetting = null;
        for (Object listener : params.getStdoutListeners()) {
            if (listener instanceof ProxySettingStdoutListener) {
                ProxySettingStdoutListener casted = (ProxySettingStdoutListener) listener;
                proxySetting = casted.getProxySetting();
                break;
            }
        }
        return proxySetting;
    }
}
