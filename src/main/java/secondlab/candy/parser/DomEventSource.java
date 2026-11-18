package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.xml.XmlFactories;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

final class DomEventSource {
    private DomEventSource() {
    }

    static void read(InputStream xml, CandyXmlHandler handler) {
        try {
            DocumentBuilder builder = XmlFactories.documentBuilderFactory().newDocumentBuilder();
            visit(builder.parse(xml).getDocumentElement(), handler);
        } catch (CandyXmlException exception) {
            throw exception;
        } catch (SAXException | IOException | ParserConfigurationException exception) {
            throw new CandyXmlException("DOM parsing failed", exception);
        }
    }

    private static void visit(Element element, CandyXmlHandler handler) {
        String name = element.getLocalName();
        if (name == null || name.isEmpty()) {
            name = element.getTagName();
        }
        handler.startElement(name, XmlAttributes.fromDom(element));
        StringBuilder text = new StringBuilder();
        NodeList nodes = element.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node instanceof Element child) {
                visit(child, handler);
            } else if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                text.append(node.getNodeValue());
            }
        }
        handler.characters(text.toString());
        handler.endElement(name);
    }
}
