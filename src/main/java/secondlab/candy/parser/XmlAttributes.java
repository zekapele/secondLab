package secondlab.candy.parser;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.stream.XMLStreamReader;

final class XmlAttributes {
    private XmlAttributes() {
    }

    static String fromSax(Attributes attributes, String name) {
        for (int index = 0; index < attributes.getLength(); index++) {
            if (name.equals(attributes.getLocalName(index)) || name.equals(attributes.getQName(index))) {
                return attributes.getValue(index);
            }
        }
        return null;
    }

    static String fromDom(Element element, String name) {
        if (!element.hasAttribute(name)) {
            return null;
        }
        return element.getAttribute(name);
    }

    static String fromStax(XMLStreamReader reader, String name) {
        for (int index = 0; index < reader.getAttributeCount(); index++) {
            if (name.equals(reader.getAttributeLocalName(index))) {
                return reader.getAttributeValue(index);
            }
        }
        return null;
    }
}
