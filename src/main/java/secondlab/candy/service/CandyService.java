package secondlab.candy.service;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;
import secondlab.candy.parser.ParserKind;
import secondlab.candy.transform.CandyTransformer;
import secondlab.candy.validation.XmlValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public final class CandyService {
    private static final Logger LOG = Logger.getLogger(CandyService.class.getName());

    private final XmlValidator validator = new XmlValidator();
    private final CandyTransformer transformer = new CandyTransformer();

    public List<Candy> load(Path xml, Path xsd, ParserKind parserKind) {
        try {
            byte[] xmlBytes = Files.readAllBytes(xml);
            byte[] xsdBytes = Files.readAllBytes(xsd);
            validator.validate(xmlBytes, xsdBytes, xml.toUri().toString());
            List<Candy> candies = parserKind.create().parse(new ByteArrayInputStream(xmlBytes));
            LOG.info(() -> parserKind + " loaded " + candies.size() + " candies from " + xml.getFileName());
            return candies;
        } catch (IOException exception) {
            throw new CandyXmlException("Cannot read " + xml, exception);
        }
    }

    public void transform(Path xml, Path xsl, Path output) {
        transformer.transform(xml, xsl, output);
    }
}
