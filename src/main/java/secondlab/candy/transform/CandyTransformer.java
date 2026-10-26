package secondlab.candy.transform;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.xml.XmlFactories;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class CandyTransformer {
    private static final Logger LOG = Logger.getLogger(CandyTransformer.class.getName());

    public String transform(InputStream xml, InputStream xsl) {
        try {
            StringWriter writer = new StringWriter();
            newTransformer(xsl).transform(new StreamSource(xml), new StreamResult(writer));
            LOG.info("XML grouped by candy kind");
            return writer.toString();
        } catch (TransformerException exception) {
            throw new CandyXmlException("XSL transformation failed", exception);
        }
    }

    public void transform(Path xml, Path xsl, Path output) {
        try {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            try (InputStream xmlStream = Files.newInputStream(xml);
                 InputStream xslStream = Files.newInputStream(xsl);
                 OutputStream result = Files.newOutputStream(output)) {
                newTransformer(xslStream).transform(new StreamSource(xmlStream), new StreamResult(result));
            }
            LOG.info(() -> "Grouped XML written to " + output.toAbsolutePath());
        } catch (TransformerException exception) {
            throw new CandyXmlException("XSL transformation failed", exception);
        } catch (java.io.IOException exception) {
            throw new CandyXmlException("Cannot write transformed XML", exception);
        }
    }

    private static Transformer newTransformer(InputStream xsl) throws TransformerException {
        Transformer transformer = XmlFactories.transformerFactory().newTransformer(new StreamSource(xsl));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }
}
