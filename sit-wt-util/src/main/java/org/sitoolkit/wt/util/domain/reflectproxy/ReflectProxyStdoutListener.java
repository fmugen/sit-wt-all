package org.sitoolkit.wt.util.domain.reflectproxy;

import org.sitoolkit.wt.util.infra.process.StdoutListener;

public class ReflectProxyStdoutListener implements StdoutListener {

    private UserProxy userProxy = new UserProxy();

    @Override
    public void nextLine(String line) {
        if (line.trim().startsWith("Proxy")) {
            parse(line.trim());
        }
    }

    public UserProxy getUserProxy() {
        return userProxy;
    }

    void parse(String line) {
        String[] details = line.split(" +");
        switch(details[0]) {
        case "ProxyEnable" :
            if ("0x1".equals(details[2])) {
                userProxy.setProxyActive("true");
            }
            break;

        case "ProxyServer" :
            if (details[2].contains(";")) {
                for (String protocolDetail : details[2].split(";")) {
                    String[] protocolDetails = protocolDetail.split("[=:]");
                    String protocol = protocolDetails[0];

                    if ("http".equals(protocol) || "https".equals(protocol)) {
                        userProxy.setProxyHost(protocolDetails[1]);
                        if (protocolDetails.length == 3) {
                            userProxy.setProxyPort(protocolDetails[2]);
                        } else {
                            userProxy.setProxyPort("80");
                        }
                        break;
                    }
                }
            } else {
                String[] settings = details[2].split(":");
                userProxy.setProxyHost(settings[0]);
                if (settings.length == 2) {
                    userProxy.setProxyPort(settings[1]);
                } else {
                    userProxy.setProxyPort("80");
                }
            }
            break;

        case "ProxyOverride" :
            userProxy.setNonProxyHosts(details[2].replaceAll(";", "|"));
            break;
        }
    }
}
