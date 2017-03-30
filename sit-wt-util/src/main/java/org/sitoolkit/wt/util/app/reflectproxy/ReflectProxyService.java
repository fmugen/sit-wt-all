package org.sitoolkit.wt.util.app.reflectproxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.util.domain.reflectproxy.ReflectProxyProcessClient;
import org.sitoolkit.wt.util.domain.reflectproxy.ReflectProxyStdoutListener;
import org.sitoolkit.wt.util.domain.reflectproxy.UserProxy;
import org.sitoolkit.wt.util.infra.maven.MavenUtils;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class ReflectProxyService {
    private static final Logger LOG = Logger.getLogger(ReflectProxyService.class.getName());

    public void setProxy() {

        try {
            UserProxy userProxy = MavenUtils.getMavenProxy();

            if (userProxy == null) {
                LOG.log(Level.INFO, "read registry proxy settings");
                userProxy = getOsProxy();

                if (userProxy.isEnabled()) {
                    MavenUtils.writeMavenProxy(userProxy);
                }
            }

            setProperties(userProxy);
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "set proxy failed", exp);
        }
    }

    private UserProxy getOsProxy() {
        ReflectProxyProcessClient client = new ReflectProxyProcessClient();

        ProcessParams params = new ProcessParams();

        ReflectProxyStdoutListener proxyStdoutListener = new ReflectProxyStdoutListener();
        params.getStdoutListeners().add(proxyStdoutListener);

        return client.getRegistryProxy(params);
    }


    private void setProperties(UserProxy userProxy) {
        System.setProperty("proxySet", userProxy.getProxyActive());

        if (userProxy.isEnabled()) {
            LOG.log(Level.INFO, "set proxy properties");
            System.setProperty("proxyHost", userProxy.getProxyHost());
            System.setProperty("proxyPort", userProxy.getProxyPort());

            if (userProxy.getNonProxyHosts() != null && !userProxy.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", userProxy.getNonProxyHosts());
            }
        } else {
            LOG.log(Level.INFO, "proxy settings is disabled");
        }
    }
}
