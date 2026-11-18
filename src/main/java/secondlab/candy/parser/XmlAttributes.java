package secondlab.candy.parser;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.stream.XMLStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

final class XmlAttributes {
    private XmlAttributes() {
    }

    static Map<String, String> fromSax(Attributes attributes) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int index = 0; index < attributes.getLength(); index++) {
            String name = attributes.getLocalName(index);
            if (name == null || name.isEmpty()) {
                name = attributes.getQName(index);
            }
            values.put(name, attributes.getValue(index));
        }
        return values;
    }

    static Map<String, String> fromDom(Element element) {
        Map<String, String> values = new LinkedHashMap<>();
        var nodes = element.getAttributes();
        for (int index = 0; index < nodes.getLength(); index++) {
            var node = nodes.item(index);
            String name = node.getLocalName();
            if (name == null || name.isEmpty()) {
                name = node.getNodeName();
            }
            if (name != null && !name.startsWith("xmlns")) {
                values.put(name, node.getNodeValue());
            }
        }
        return values;
    }

    static Map<String, String> fromStax(XMLStreamReader reader) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int index = 0; index < reader.getAttributeCount(); index++) {
            values.put(reader.getAttributeLocalName(index), reader.getAttributeValue(index));
        }
        return values;
    }
}
