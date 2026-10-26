package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;
import secondlab.candy.xml.XmlConstants;
import secondlab.candy.xml.XmlFactories;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class DomCandyParser implements CandyParser {
    private static final Logger LOG = Logger.getLogger(DomCandyParser.class.getName());

    @Override
    public List<Candy> parse(InputStream xml) {
        try {
            DocumentBuilder builder = XmlFactories.documentBuilderFactory().newDocumentBuilder();
            Element root = builder.parse(xml).getDocumentElement();
            NodeList nodes = root.getElementsByTagNameNS(XmlConstants.NAMESPACE, "candy");
            List<Candy> candies = new ArrayList<>();
            for (int index = 0; index < nodes.getLength(); index++) {
                candies.add(readCandy((Element) nodes.item(index)));
            }
            LOG.fine(() -> "DOM parsed " + candies.size() + " candies");
            return candies;
        } catch (CandyXmlException exception) {
            throw exception;
        } catch (SAXException | IOException | ParserConfigurationException exception) {
            throw new CandyXmlException("DOM parsing failed", exception);
        }
    }

    private static Candy readCandy(Element candy) {
        CandyAssembler assembler = new CandyAssembler();
        assembler.setId(XmlAttributes.fromDom(candy, "id"));
        for (Element child : elements(candy)) {
            readSection(child, assembler);
        }
        return assembler.build();
    }

    private static void readSection(Element element, CandyAssembler assembler) {
        String name = element.getLocalName();
        switch (name) {
            case "type" -> {
                assembler.setFilling(XmlAttributes.fromDom(element, "filling"));
                readLeaves(element, assembler);
            }
            case "ingredients", "value" -> readLeaves(element, assembler);
            default -> {
                if (!CandyAssembler.isLeaf(name)) {
                    throw new CandyXmlException("Unexpected element: " + name);
                }
                assembler.setField(name, element.getTextContent());
            }
        }
    }

    private static void readLeaves(Element parent, CandyAssembler assembler) {
        for (Element child : elements(parent)) {
            if (!CandyAssembler.isLeaf(child.getLocalName())) {
                throw new CandyXmlException("Unexpected element: " + child.getLocalName());
            }
            assembler.setField(child.getLocalName(), child.getTextContent());
        }
    }

    private static List<Element> elements(Element parent) {
        List<Element> children = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node instanceof Element element) {
                children.add(element);
            }
        }
        return children;
    }
}
