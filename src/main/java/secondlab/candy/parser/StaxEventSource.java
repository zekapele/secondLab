package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.xml.XmlFactories;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.logging.Logger;

final class StaxEventSource {
    private static final Logger LOG = Logger.getLogger(StaxEventSource.class.getName());

    private StaxEventSource() {
    }

    static void read(InputStream xml, CandyXmlHandler handler) {
        XMLStreamReader reader = null;
        try {
            reader = XmlFactories.xmlInputFactory().createXMLStreamReader(xml);
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT ->
                            handler.startElement(reader.getLocalName(), XmlAttributes.fromStax(reader));
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA ->
                            handler.characters(reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> handler.endElement(reader.getLocalName());
                    default -> {
                    }
                }
            }
        } catch (CandyXmlException exception) {
            throw exception;
        } catch (XMLStreamException exception) {
            throw new CandyXmlException("StAX parsing failed", exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException exception) {
                    LOG.warning("Failed to close StAX reader: " + exception.getMessage());
                }
            }
        }
    }
}
