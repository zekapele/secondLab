package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;
import secondlab.candy.xml.XmlFactories;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class SaxCandyParser implements CandyParser {
    private static final Logger LOG = Logger.getLogger(SaxCandyParser.class.getName());

    @Override
    public List<Candy> parse(InputStream xml) {
        try {
            SAXParser parser = XmlFactories.saxParserFactory().newSAXParser();
            Handler handler = new Handler();
            parser.parse(xml, handler);
            LOG.fine(() -> "SAX parsed " + handler.candies.size() + " candies");
            return handler.candies;
        } catch (CandyXmlException exception) {
            throw exception;
        } catch (SAXException exception) {
            Throwable cause = exception.getException() != null ? exception.getException() : exception.getCause();
            if (cause instanceof CandyXmlException candyXmlException) {
                throw candyXmlException;
            }
            throw new CandyXmlException("SAX parsing failed", exception);
        } catch (IOException | ParserConfigurationException exception) {
            throw new CandyXmlException("SAX parsing failed", exception);
        }
    }

    private static final class Handler extends DefaultHandler {
        private final List<Candy> candies = new ArrayList<>();
        private final StringBuilder text = new StringBuilder();
        private CandyAssembler assembler;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            text.setLength(0);
            switch (elementName(localName, qName)) {
                case "candy" -> {
                    assembler = new CandyAssembler();
                    assembler.setId(XmlAttributes.fromSax(attributes, "id"));
                }
                case "type" -> {
                    requireAssembler();
                    assembler.setFilling(XmlAttributes.fromSax(attributes, "filling"));
                }
                default -> {
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            text.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (elementName(localName, qName)) {
                case "candy" -> {
                    requireAssembler();
                    candies.add(assembler.build());
                    assembler = null;
                }
                case "Candy", "type", "ingredients", "value" -> {
                }
                default -> {
                    String name = elementName(localName, qName);
                    if (!CandyAssembler.isLeaf(name)) {
                        throw new CandyXmlException("Unexpected element: " + name);
                    }
                    requireAssembler();
                    assembler.setField(name, text.toString());
                }
            }
        }

        private void requireAssembler() {
            if (assembler == null) {
                throw new CandyXmlException("Element is outside candy");
            }
        }

        private static String elementName(String localName, String qName) {
            return localName == null || localName.isEmpty() ? qName : localName;
        }
    }
}
