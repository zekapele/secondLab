package secondlab.candy.service;

import secondlab.candy.ExpectedCatalog;
import secondlab.candy.Resources;
import secondlab.candy.XmlSnippets;
import secondlab.candy.error.CandyXmlException;
import secondlab.candy.error.XmlValidationException;
import secondlab.candy.parser.ParserKind;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandyServiceTest {
    private final CandyService service = new CandyService();

    @Test
    void loadsValidatedCatalogWithEveryParser() {
        for (ParserKind kind : ParserKind.values()) {
            assertEquals(ExpectedCatalog.candies(), service.load(
                    Resources.path("xml/candies.xml"),
                    Resources.path("xsd/candies.xsd"),
                    kind));
        }
    }

    @Test
    void doesNotParseInvalidXml(@TempDir Path directory) throws Exception {
        Path xml = directory.resolve("bad.xml");
        Files.writeString(xml, XmlSnippets.document(XmlSnippets.candy("id=\"sample\"", "0", "caramel", "")));
        assertThrows(XmlValidationException.class,
                () -> service.load(xml, Resources.path("xsd/candies.xsd"), ParserKind.DOM));
    }

    @Test
    void reportsAMissingFile() {
        assertThrows(CandyXmlException.class,
                () -> service.load(Path.of("missing.xml"), Path.of("missing.xsd"), ParserKind.SAX));
    }

    @Test
    void transformsThroughTheService(@TempDir Path directory) throws Exception {
        Path output = directory.resolve("grouped.xml");
        service.transform(Resources.path("xml/candies.xml"), Resources.path("xsl/candies-by-type.xsl"), output);
        String text = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(text.contains("<chocolate"));
    }
}
