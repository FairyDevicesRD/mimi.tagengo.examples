package ai.fd.mimi.prism;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

class XMLUtil {
    private static final Set<String> SPACE_SEPARATING_LANGUAGES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("en", "fr", "es", "id", "vi")));

    XMLUtil() {
    }

    RequestData parseXML(String xml) throws IOException, SAXException {
        try {
            //System.out.println("===============================");
            //System.out.println("input: " + xml);
            //System.out.println("===============================");
            RequestData request = new RequestData();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
            Element root = document.getDocumentElement();

            NamedNodeMap rootAttribute = root.getAttributes();
            request.stmlVersion = Float.valueOf(rootAttribute.getNamedItem("Version").getNodeValue());
            request.utteranceID = Integer.valueOf(rootAttribute.getNamedItem("UtteranceID").getNodeValue());

            NodeList nodeList = root.getChildNodes();
            boolean coreElementFound = false;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i); // User, SR_IN,MT_IN,SS_IN が存在
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                //System.out.println("node: " + nodeList.item(i).getNodeName());
                switch (node.getNodeName()) {
                    case "User": {
                        NamedNodeMap userAttribute = node.getAttributes();
                        request.userID = userAttribute.getNamedItem("ID").getNodeValue();
                        //System.out.println("User ID: " + request.userID);
                        break;
                    }
                    case "SR_IN": {
                        if (coreElementFound) {
                            throw new IllegalArgumentException("Extra element found: SR_IN");
                        }
                        coreElementFound = true;
                        //System.out.println("SR_IN");
                        request.type = RequestData.EXEC_TYPE.SR;
                        NamedNodeMap srinAttribute = node.getAttributes();
                        request.language = srinAttribute.getNamedItem("Language").getNodeValue();
                        NodeList nodeList1 = node.getChildNodes();
                        for (int j = 0; j < nodeList1.getLength(); j++) {
                            Node node2 = nodeList1.item(j); // InputAudioFormat,OutputTextFormat,Voice が存在
                            if (node2.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            //System.out.println("node2: " + node2.getNodeName());
                            switch (node2.getNodeName()) {
                                case "InputAudioFormat":
                                    NamedNodeMap iafAttribute = node2.getAttributes();
                                    request.inputAudioFormatAudio = iafAttribute.getNamedItem("Audio").getNodeValue();
                                    break;
                                case "OutputTextFormat":
                                    NamedNodeMap otfAttribute = node2.getAttributes();
                                    request.outputTextFormatForm = otfAttribute.getNamedItem("Form").getNodeValue();
                                    break;
                            }
                        }
                        break;
                    }
                    case "MT_IN": {
                        if (coreElementFound) {
                            throw new IllegalArgumentException("Extra element found: MT_IN");
                        }
                        coreElementFound = true;
                        //System.out.println("MT_IN");
                        request.type = RequestData.EXEC_TYPE.MT;
                        NamedNodeMap mtinAttribute = node.getAttributes();
                        request.sourceLanguage = mtinAttribute.getNamedItem("SourceLanguage").getNodeValue();
                        request.targetLanguage = mtinAttribute.getNamedItem("TargetLanguage").getNodeValue();
                        NodeList nodeList1 = node.getChildNodes();
                        for (int j = 0; j < nodeList1.getLength(); j++) {
                            Node node2 = nodeList1.item(j); // InputTextFormat,OutputTextFormat,s が存在
                            if (node2.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            //System.out.println("node2: " + node2.getNodeName());
                            switch (node2.getNodeName()) {
                                case "InputTextFormat":
                                    NamedNodeMap itfAttribute = node2.getAttributes();
                                    request.inputTextFormatForm = itfAttribute.getNamedItem("Form").getNodeValue();
                                    break;
                                case "OutputTextFormat":
                                    NamedNodeMap otfAttribute = node2.getAttributes();
                                    request.outputTextFormatForm = otfAttribute.getNamedItem("Form").getNodeValue();
                                    break;
                                case "s":
                                    Node node3;
                                    if ((node3 = node2.getAttributes().getNamedItem("Delimiter")) == null) {
                                        request.sentence = node2.getTextContent();
                                    } else {
                                        String delimiter = node3.getNodeValue();
                                        if (delimiter.equals("")) {
                                            request.sentence = node2.getTextContent();
                                        } else {
                                            request.sentence = node2.getTextContent().replace(delimiter, "");
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                    }
                    case "SS_IN": {
                        if (coreElementFound) {
                            throw new IllegalArgumentException("Extra element found: SS_IN");
                        }
                        coreElementFound = true;
                        //System.out.println("SS_IN");
                        request.type = RequestData.EXEC_TYPE.SS;
                        NamedNodeMap ssinAttribute = node.getAttributes();
                        if (ssinAttribute.getNamedItem("Volume") != null) {
                            request.volume = Float.valueOf(ssinAttribute.getNamedItem("Volume").getNodeValue());
                        }
                        if (ssinAttribute.getNamedItem("Rate") != null) {
                            request.rate = Float.valueOf(ssinAttribute.getNamedItem("Rate").getNodeValue());
                        }
                        request.language = ssinAttribute.getNamedItem("Language").getNodeValue();
                        NodeList nodeList1 = node.getChildNodes();
                        for (int j = 0; j < nodeList1.getLength(); j++) {
                            Node node2 = nodeList1.item(j); // InputTextFormat,OutputAudioFormat,Voice,s が存在
                            if (node2.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            switch (node2.getNodeName()) {
                                case "InputTextFormat":
                                    NamedNodeMap itfAttribute = node2.getAttributes();
                                    request.inputTextFormatForm = itfAttribute.getNamedItem("Form").getNodeValue();
                                    break;
                                case "OutputAudioFormat":
                                    NamedNodeMap oafAttribute = node2.getAttributes();
                                    request.outputAudioFormatAudio = oafAttribute.getNamedItem("Audio").getNodeValue();
                                    request.outputAudioFormatEndian = oafAttribute.getNamedItem("Endian").getNodeValue();
                                    break;
                                case "Voice":
                                    NamedNodeMap voiceAttribute = node2.getAttributes();
                                    request.voiceGender = voiceAttribute.getNamedItem("Gender").getNodeValue();
                                    request.voiceAge = voiceAttribute.getNamedItem("Age").getNodeValue();
                                    break;
                                case "s":
                                    Node delimiterNode = node2.getAttributes().getNamedItem("Delimiter");
                                    if (delimiterNode == null || delimiterNode.getNodeValue().equals("")) {
                                        request.sentence = node2.getTextContent();
                                    } else {
                                        request.sentence = node2.getTextContent().replace(delimiterNode.getNodeValue(), " ");
                                    }
                                    break;
                            }
                        }
                        break;
                    }
                }
            }
            return request;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Required node(s) not found", e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Exception thrown in parsing xml", e);
        }
    }

    String createResponseSR(RequestData request, List<String> words) {
        String result;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation dom = documentBuilder.getDOMImplementation();
            Document doc = dom.createDocument("", "STML", null);

            // STML
            Element root = createElement(doc, request);

            // STML/SR_OUT
            Element srOutElement = doc.createElement("SR_OUT");
            srOutElement.setAttribute("Language", request.language); //SR入力音声を返す
            root.appendChild(srOutElement);

            // STML/SR_OUT/NBests
            Element nbestElement = doc.createElement("NBest");
            nbestElement.setAttribute("Order", "1");
            root.appendChild(nbestElement);
            srOutElement.appendChild(nbestElement);

            // STML/SR_OUT/NBests/s
            Element sentenceElement = doc.createElement("s");
            sentenceElement.setAttribute("Duration", "1");
            if (SPACE_SEPARATING_LANGUAGES.contains(request.language)) {
                sentenceElement.setAttribute("Delimiter", " ");
            } else {
                sentenceElement.setAttribute("Delimiter", "");
            }
            StringBuilder resultText = new StringBuilder();
            boolean isFirst = true;
            for (String str : words) {
                String[] st = str.split("\\|", 0);
                if (isFirst) {
                    resultText.append(st[0]);
                    isFirst = false;
                } else {
                    if (SPACE_SEPARATING_LANGUAGES.contains(request.language) && !st[0].equals("") && !st[0].equals(".")) {
                        resultText.append(' ');
                    }
                    resultText.append(st[0]);
                }
            }
            sentenceElement.setTextContent(resultText.toString());

            nbestElement.appendChild(sentenceElement);
            // STML/SR_OUT/NBests/s/Word
            for (String str : words) {
                Element wordElement = doc.createElement("Word");
                wordElement.setTextContent(str);
                sentenceElement.appendChild(wordElement);
            }

            result = convertString(doc);
        } catch (TransformerException | ParserConfigurationException e) {
            throw new RuntimeException("Exception thrown in creating SR response", e);
        }
        return result;
    }

    String createResponseMT(RequestData request, String sentence) {
        String result;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation dom = documentBuilder.getDOMImplementation();
            Document doc = dom.createDocument("", "STML", null);

            // STML
            Element root = createElement(doc, request);

            // STML/MT_OUT
            Element mtOutElement = doc.createElement("MT_OUT");
            mtOutElement.setAttribute("SourceLanguage", request.sourceLanguage);
            mtOutElement.setAttribute("TargetLanguage", request.targetLanguage);
            root.appendChild(mtOutElement);

            // STML/MT_OUT/NBest
            Element nbestElement2 = doc.createElement("NBest");
            nbestElement2.setAttribute("Order", "1");
            mtOutElement.appendChild(nbestElement2);

            // STML/MT_OUT/NBest/s
            Element sentenceElement2 = doc.createElement("s");
            sentenceElement2.setTextContent(sentence);
            nbestElement2.appendChild(sentenceElement2);

            // STML/MT_OUT/NBest/s/Word
            // no-op

            result = convertString(doc);
        } catch (TransformerException | ParserConfigurationException e) {
            throw new RuntimeException("Exception thrown in creating MT response", e);
        }
        return result;
    }

    String createResponseSS(RequestData request) {
        String result;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation dom = documentBuilder.getDOMImplementation();
            Document doc = dom.createDocument("", "STML", null);

            // STML
            Element root = createElement(doc, request);

            // STML/SS_OUT
            Element ssOutElement = doc.createElement("SS_OUT");
            ssOutElement.setAttribute("Language", request.language);
            root.appendChild(ssOutElement);

            // STML/SS_OUT/OutputAudioFormat
            Element outputAudioFormatElement = doc.createElement("OutputAudioFormat");
            outputAudioFormatElement.setAttribute("Audio", request.outputAudioFormatAudio);
            outputAudioFormatElement.setAttribute("Endian", request.outputAudioFormatEndian);
            ssOutElement.appendChild(outputAudioFormatElement);

            result = convertString(doc);
        } catch (TransformerException | ParserConfigurationException e) {
            throw new RuntimeException("Exception thrown in creating SS response", e);
        }
        return result;
    }

    private Element createElement(Document doc, RequestData request) {
        // STML
        Element root = doc.getDocumentElement();
        root.setAttribute("Version", String.valueOf(request.stmlVersion));
        root.setAttribute("UtteranceID", String.valueOf(request.utteranceID));

        // STML/User
        Element userElement = doc.createElement("User");
        userElement.setAttribute("ID", request.userID);
        root.appendChild(userElement);
        return root;
    }

    private String convertString(Document doc) throws TransformerException {
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
        transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

}

class RequestData {

    RequestData() {
    }

    protected enum EXEC_TYPE {
        NONE,
        SR,
        MT,
        SS,
    }

    String xmlins = "";
    float stmlVersion = 0.0f;
    int utteranceID = 0;
    String userID = "";

    String language = "";                 // SR / SS
    String sourceLanguage = "";           // MT
    String targetLanguage = "";           // MT
    String inputAudioFormatAudio = "";    // SR
    String outputAudioFormatAudio = "";   // SS
    String outputAudioFormatEndian = "";  // SS
    String inputTextFormatForm = "";      // MT
    String outputTextFormatForm = "";     // SR / MT
    String sentence = "";                 // MT
    String voiceGender = "";              // SR / SS
    String voiceAge = "";                 // SR / SS
    String voiceNative = "";              // SR

    float volume = 0.0f;                  // SS
    float rate = 0.0f;                    // SS


    RequestData.EXEC_TYPE type = RequestData.EXEC_TYPE.NONE;

}
