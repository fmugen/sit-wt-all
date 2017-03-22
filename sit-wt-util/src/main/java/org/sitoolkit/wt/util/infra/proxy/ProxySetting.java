package org.sitoolkit.wt.util.infra.proxy;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sitoolkit.wt.util.app.proxy.ProxyService;
import org.sitoolkit.wt.util.infra.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProxySetting {
    private static final Logger LOG = Logger.getLogger(ProxySetting.class.getName());

    private File userDir = new File(System.getProperty("user.home"), ".m2");
    private File settingsXml = new File(userDir, "settings.xml");

    private UserProxy userProxy = new UserProxy();


    public void setProxy() {

        try {
            if (settingsXml.exists()) {
                readProxySetting();

            } else {
                readRegistryProxy();

                if ("true".equals(userProxy.getProxyActive())) {
                    Files.copy(ClassLoader.getSystemResourceAsStream("settings.xml"), settingsXml.toPath());
                    writeSettingsXml();
                }
            }

            setProxyProperties();
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "set proxy failed", exp);
        }
    }

    private void readProxySetting() {

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(settingsXml);

            String proxySetting = XmlUtil.readParams(document, "/settings/proxies/proxy");

            if (!proxySetting.trim().isEmpty()) {
                String isActive = XmlUtil.readParams(document, "/settings/proxies/proxy/active");

                if ("true".equals(isActive.trim())) {
                    String host = XmlUtil.readParams(document, "/settings/proxies/proxy/host");
                    String port = XmlUtil.readParams(document, "/settings/proxies/proxy/port");
                    String nonProxyHosts  = XmlUtil.readParams(document, "/settings/proxies/proxy/nonProxyHosts");
                    userProxy.setProxySettings(host, port, nonProxyHosts);

                } else {
                    // proxy settings is defined to disable
                }

            } else {
                readRegistryProxy();

                if ("true".equals(userProxy.getProxyActive())) {
                    writeSettingsXml();
                }
            }

        } catch (Exception exp) {
            LOG.log(Level.WARNING, "settings.xml read failed", exp);
        }
    }

    private void readRegistryProxy() {
        ProxyService proxyService = new ProxyService();
        proxyService.readProxy(registryProxy -> {
            boolean enable = false;
            HashMap<String, String> proxy = new HashMap<>();

            List<String> proxySettings = registryProxy;
            for (String proxySetting : proxySettings) {
                String[] proxyDesc = proxySetting.split(" +");
                switch(proxyDesc[0]) {
                case "ProxyEnable" :
                    if ("0x1".equals(proxyDesc[2])) {
                        enable = true;
                    }
                    break;

                case "ProxyServer" :
                    if (proxyDesc[2].contains(";")) {
                        for (String protocolSetting : proxyDesc[2].split(";")) {
                            String[] settings = protocolSetting.split("[=:]");
                            String protocol = settings[0];

                            if ("http".equals(protocol) || "https".equals(protocol)) {
                                proxy.put("host", settings[1]);
                                if (settings.length == 3) {
                                    proxy.put("port", settings[2]);
                                } else {
                                    proxy.put("port", "80");
                                }
                                break;
                            }
                        }
                    } else {
                        String[] settings = proxyDesc[2].split(":");
                        proxy.put("host", settings[0]);
                        if (settings.length == 2) {
                            proxy.put("port", settings[1]);
                        } else {
                            proxy.put("port", "80");
                        }
                    }
                    break;

                case "ProxyOverride" :
                    proxy.put("nonProxyHosts", proxyDesc[2].replaceAll(";", "|"));
                    break;
                }
            }

            if (enable && proxy.containsKey("host")) {
                userProxy.setRegistryResult(proxy);
            }
        });
    }

    private void writeSettingsXml() {

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(settingsXml);

            Element root = document.getDocumentElement();
            Element proxies = XmlUtil.getChildElement(root, "proxies");
            if (proxies == null) {
                proxies = document.createElement("proxies");
                root.appendChild(proxies);
            }

            Element proxy = document.createElement("proxy");
            Element id = document.createElement("id");
            id.appendChild(document.createTextNode("auto-loaded-by-sit-wt"));
            proxy.appendChild(id);

            Element active = document.createElement("active");
            active.appendChild(document.createTextNode("true"));
            proxy.appendChild(active);

            Element protocol = document.createElement("protocol");
            protocol.appendChild(document.createTextNode("http"));
            proxy.appendChild(protocol);

            Element host = document.createElement("host");
            host.appendChild(document.createTextNode(userProxy.getProxyHost()));
            proxy.appendChild(host);

            Element port = document.createElement("port");
            port.appendChild(document.createTextNode(userProxy.getProxyPort()));
            proxy.appendChild(port);

            if (userProxy.getNonProxyHosts() != null && !userProxy.getNonProxyHosts().isEmpty()) {
                Element nonProxyHosts = document.createElement("nonProxyHosts");
                nonProxyHosts.appendChild(document.createTextNode(userProxy.getNonProxyHosts()));
                proxy.appendChild(nonProxyHosts);
            }

            proxies.appendChild(proxy);
            XmlUtil.createXml(document, settingsXml);

        } catch (Exception exp) {
            LOG.log(Level.WARNING, "settings.xml write failed", exp);
        }
    }

    private void setProxyProperties() {
        System.setProperty("proxySet", userProxy.getProxyActive());

        if ("true".equals(userProxy.getProxyActive())) {
            System.setProperty("proxyHost", userProxy.getProxyHost());
            System.setProperty("proxyPort", userProxy.getProxyPort());

            if (userProxy.getNonProxyHosts() != null && !userProxy.getNonProxyHosts().isEmpty()) {
                System.setProperty("nonProxyHosts", userProxy.getNonProxyHosts());
            }
        }
    }
}
