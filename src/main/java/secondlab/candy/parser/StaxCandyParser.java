package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;
import secondlab.candy.xml.XmlFactories;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class StaxCandyParser implements CandyParser {
    private static final Logger LOG = Logger.getLogger(StaxCandyParser.class.getName());

    @Override
    public List<Candy> parse(InputStream xml) {
        XMLStreamReader reader = null;
        try {
            reader = XmlFactories.xmlInputFactory().createXMLStreamReader(xml);
            List<Candy> candies = new ArrayList<>();
            CandyAssembler assembler = null;
            StringBuilder text = new StringBuilder();
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        text.setLength(0);
                        switch (reader.getLocalName()) {
                            case "candy" -> {
                                assembler = new CandyAssembler();
                                assembler.setId(XmlAttributes.fromStax(reader, "id"));
                            }
                            case "type" -> {
                                if (assembler == null) {
                                    throw new CandyXmlException("Element is outside candy");
                                }
                                assembler.setFilling(XmlAttributes.fromStax(reader, "filling"));
                            }
                            default -> {
                            }
                        }
                    }
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA -> text.append(reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> {
                        String name = reader.getLocalName();
                        switch (name) {
                            case "candy" -> {
                                if (assembler == null) {
                                    throw new CandyXmlException("Element is outside candy");
                                }
                                candies.add(assembler.build());
                                assembler = null;
                            }
                            case "Candy", "type", "ingredients", "value" -> {
                            }
                            default -> {
                                if (!CandyAssembler.isLeaf(name)) {
                                    throw new CandyXmlException("Unexpected element: " + name);
                                }
                                if (assembler == null) {
                                    throw new CandyXmlException("Element is outside candy");
                                }
                                assembler.setField(name, text.toString());
                            }
                        }
                    }
                    default -> {
                    }
                }
            }
            LOG.fine(() -> "StAX parsed " + candies.size() + " candies");
            return candies;
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
