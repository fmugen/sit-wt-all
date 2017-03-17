package org.sitoolkit.wt.util.infra.util;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlUtil {

    public static String readParams(Document document, String expression) {

        String result = "";
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            result = xPath.compile(expression).evaluate(document);
        } catch (Exception exp) {
            //logger
            exp.printStackTrace();
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
            // logger
            exp.printStackTrace();
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
