package org.sitoolkit.wt.util.app.proxy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.util.domain.proxy.ProxyProcessClient;
import org.sitoolkit.wt.util.domain.proxy.ProxyReadCallback;
import org.sitoolkit.wt.util.domain.proxy.ProxyStdoutListener;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class ProxyService {
    private static final Logger LOG = Logger.getLogger(ProxyService.class.getName());

    ProxyProcessClient client = new ProxyProcessClient();

    public void readProxy(ProxyReadCallback callback) {
        ProcessParams params = new ProcessParams();

        ProxyStdoutListener proxyStdoutListener = new ProxyStdoutListener();
        params.getStdoutListeners().add(proxyStdoutListener);

        params.getExitClallbacks().add(exitCode -> {
            try {
                List<String> proxySettings = proxyStdoutListener.getProxySettings();
                callback.proxyRead(proxySettings);
            } catch (Exception exp) {
                LOG.log(Level.WARNING, "proxy settings read failed", exp);
            }
        });

        client.readProxy(params);
    }
}
