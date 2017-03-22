package org.sitoolkit.wt.util.infra.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.sitoolkit.wt.util.infra.proxy.ProxySetting;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUtil {
    private static final Logger LOG = Logger.getLogger(ProxySetting.class.getName());

    public static String readParams(Document document, String expression) {

        String result = "";
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            result = xPath.compile(expression).evaluate(document);
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "read settings.xml failed", exp);
            return null;
        }

        return result.trim();
    }

    public static void createXml(Document document, File file){

        Transformer transformer = null;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("encoding", "UTF-8");

            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (Exception exp) {
            LOG.log(Level.WARNING, "write settings.xml failed", exp);
        }
    }

    public static Element getChildElement(Element element, String name) {
        NodeList nodes = element.getElementsByTagName("*");

        Element target = null;
        for (int cnt = 0 ; cnt < nodes.getLength() ; cnt++) {
            if ("proxies".equals(nodes.item(cnt).getNodeName())) {
                target = (Element) nodes.item(cnt);
                break;
            }
        }

        return target;
    }
}
