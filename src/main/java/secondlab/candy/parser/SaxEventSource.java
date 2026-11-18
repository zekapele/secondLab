package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.xml.XmlFactories;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;

final class SaxEventSource {
    private SaxEventSource() {
    }

    static void read(InputStream xml, CandyXmlHandler handler) {
        try {
            SAXParser parser = XmlFactories.saxParserFactory().newSAXParser();
            parser.parse(xml, new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    handler.startElement(elementName(localName, qName), XmlAttributes.fromSax(attributes));
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    handler.characters(new String(ch, start, length));
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    handler.endElement(elementName(localName, qName));
                }
            });
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

    private static String elementName(String localName, String qName) {
        return localName == null || localName.isEmpty() ? qName : localName;
    }
}
