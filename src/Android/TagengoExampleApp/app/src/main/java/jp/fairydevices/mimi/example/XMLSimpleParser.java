package jp.fairydevices.mimi.example;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

class XMLSimpleParser {
    private DocumentBuilder dBuilder;
    private XPathExpression expMTSentence;
    private XPathExpression expSRSentence;


    XMLSimpleParser() throws ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dBuilder = dbFactory.newDocumentBuilder();
        XPathFactory xpFactory = XPathFactory.newInstance();
        XPath xpath = xpFactory.newXPath();
        expMTSentence = xpath.compile("/STML/MT_OUT/NBest[@Order='1']/s/text()");
        expSRSentence = xpath.compile("/STML/SR_OUT/NBest[@Order='1']/s/text()");
    }

    String getMT_OUTSentence(String xml) throws XPathExpressionException, IOException, SAXException {
        InputSource is = new InputSource(new StringReader(xml));
        NodeList nodelist = (NodeList)expMTSentence.evaluate(dBuilder.parse(is), XPathConstants.NODESET);
        return nodelist.item(0).getNodeValue();
    }

    String getSR_OUTSentence(String xml) throws XPathExpressionException, IOException, SAXException {
        InputSource is = new InputSource(new StringReader(xml));
        NodeList nodelist = (NodeList)expSRSentence.evaluate(dBuilder.parse(is), XPathConstants.NODESET);
        return nodelist.item(0).getNodeValue();
    }
}
