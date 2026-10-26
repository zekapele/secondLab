package secondlab.candy.validation;

import secondlab.candy.error.XmlValidationException;
import secondlab.candy.xml.XmlFactories;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class XmlValidator {
    private static final Logger LOG = Logger.getLogger(XmlValidator.class.getName());

    public void validate(byte[] xmlBytes, byte[] xsdBytes, String systemId) {
        List<String> errors = new ArrayList<>();
        try {
            Schema schema = XmlFactories.schemaFactory().newSchema(source(xsdBytes, null));
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new CollectingHandler(errors));
            validator.validate(source(xmlBytes, systemId));
        } catch (SAXException | IOException exception) {
            if (errors.isEmpty()) {
                throw new XmlValidationException("XML validation failed: " + exception.getMessage(), exception);
            }
        }
        if (!errors.isEmpty()) {
            throw new XmlValidationException(String.join(System.lineSeparator(), errors));
        }
        LOG.info("XML document is valid");
    }

    private static StreamSource source(byte[] bytes, String systemId) {
        StreamSource source = new StreamSource(new ByteArrayInputStream(bytes));
        if (systemId != null) {
            source.setSystemId(systemId);
        }
        return source;
    }

    private static final class CollectingHandler implements ErrorHandler {
        private final List<String> errors;

        private CollectingHandler(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void warning(SAXParseException exception) {
            LOG.warning(format(exception));
        }

        @Override
        public void error(SAXParseException exception) {
            errors.add(format(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) {
            errors.add(format(exception));
        }

        private static String format(SAXParseException exception) {
            return "line " + exception.getLineNumber() + ": " + exception.getMessage();
        }
    }
}
