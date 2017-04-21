package org.sitoolkit.wt.util.app.proxysetting;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.util.domain.proxysetting.ProxySettingProcessClient;
import org.sitoolkit.wt.util.domain.proxysetting.ProxySettingStdoutListener;
import org.sitoolkit.wt.util.infra.maven.MavenUtils;
import org.sitoolkit.wt.util.infra.process.ProcessParams;
import org.sitoolkit.wt.util.infra.proxysetting.ProxySetting;

public class ProxySettingService {
    private static final Logger LOG = Logger.getLogger(ProxySettingService.class.getName());

    public void loadProxy() {

        try {
            ProxySetting proxySetting = MavenUtils.readProxySetting();

            if (proxySetting == null) {
                LOG.log(Level.INFO, "read registry proxy settings");
                proxySetting = getOsProxy();

                if (proxySetting.isEnabled()) {
                    if (!MavenUtils.writeProxySetting(proxySetting))
                        return;
                }
            }

            setProperties(proxySetting);
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "set proxy failed", exp);
        }
    }

    private ProxySetting getOsProxy() {
        ProxySettingProcessClient client = new ProxySettingProcessClient();

        ProcessParams params = new ProcessParams();

        ProxySettingStdoutListener proxyStdoutListener = new ProxySettingStdoutListener();
        params.getStdoutListeners().add(proxyStdoutListener);
        params.getExitClallbacks().add(exitCode -> {
            int timeout = 3000;
            int counter = 0;
            while (!proxyStdoutListener.isDone()) {
                int wait = 100;
                try {
                    Thread.sleep(wait);
                    counter += wait;
                    if (counter > timeout) throw new Exception("STDOUT read timeout.");
                } catch (Exception exp) {
                    LOG.log(Level.WARNING, "get OS proxy failed : ", exp);
                    break;
                }
            }
        });

        return client.getRegistryProxy(params);
    }


    private void setProperties(ProxySetting proxySetting) {
        System.setProperty("proxySet", proxySetting.getProxyActive());

        if (proxySetting.isEnabled()) {
            LOG.log(Level.INFO, "set proxy properties");
            System.setProperty("proxyHost", proxySetting.getProxyHost());
            System.setProperty("proxyPort", proxySetting.getProxyPort());

            if (proxySetting.getNonProxyHosts() != null && !proxySetting.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", proxySetting.getNonProxyHosts());
            }
        } else {
            LOG.log(Level.INFO, "proxy settings is disabled");
        }
    }
}
