package org.sitoolkit.wt.util.domain.proxy;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.util.infra.process.StdoutListener;

public class ProxyStdoutListener implements StdoutListener {

    List<String> proxySettings = new ArrayList<>();

    @Override
    public void nextLine(String line) {
        if (line.trim().startsWith("Proxy")) {
            proxySettings.add(line.trim());
        }
    }

    public List<String> getProxySettings() {
        return proxySettings;
    }
}
