package secondlab.candy.validation;

import secondlab.candy.Resources;
import secondlab.candy.XmlSnippets;
import secondlab.candy.error.XmlValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlValidatorTest {
    private final XmlValidator validator = new XmlValidator();
    private final byte[] schema = Resources.bytes("xsd/candies.xsd");

    @Test
    void acceptsTheCatalog() {
        assertDoesNotThrow(() -> validator.validate(
                Resources.bytes("xml/candies.xml"),
                schema,
                Resources.path("xml/candies.xml").toUri().toString()));
    }

    @Test
    void acceptsAMinimalCandyWithoutSchemaLocation() {
        byte[] xml = XmlSnippets.document(XmlSnippets.candy("id=\"sample\"", "400", "caramel", ""))
                .getBytes(StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> validator.validate(xml, schema, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1001"})
    void rejectsEnergyOutsideBounds(String energy) {
        assertInvalid(XmlSnippets.candy("id=\"sample\"", energy, "caramel", ""));
    }

    @Test
    void rejectsUnknownKindMissingIdDuplicateIdAndOutOfRangeValues() {
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "gummy", ""));
        assertInvalid(XmlSnippets.candy("", "400", "caramel", ""));
        assertInvalid(XmlSnippets.candy("id=\"same\"", "400", "caramel", "")
                + XmlSnippets.candy("id=\"same\"", "401", "iris", ""));
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "")
                .replace("<sugar>2</sugar>", "<sugar>-1</sugar>"));
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "")
                .replace("<vanillin>4</vanillin>", "<vanillin>1001</vanillin>"));
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "")
                .replace("<proteins>1.00</proteins>", "<proteins>100.001</proteins>"));
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "")
                .replace("filling=\"false\"", ""));
        assertInvalid(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "<chocolateType>ruby</chocolateType>"));
        assertInvalid("");
    }

    @Test
    void rejectsASchemaThatIsNotXsd() {
        byte[] xml = XmlSnippets.document(XmlSnippets.candy("id=\"sample\"", "400", "caramel", ""))
                .getBytes(StandardCharsets.UTF_8);
        assertThrows(XmlValidationException.class,
                () -> validator.validate(xml, "<not-a-schema/>".getBytes(StandardCharsets.UTF_8), null));
    }

    @Test
    void rejectsMalformedDocument() {
        XmlValidationException exception = assertThrows(XmlValidationException.class,
                () -> validator.validate("<Candy>".getBytes(StandardCharsets.UTF_8), schema, null));
        assertTrue(exception.getMessage().contains("XML validation failed") || !exception.getMessage().isBlank());
    }

    private void assertInvalid(String body) {
        byte[] xml = XmlSnippets.document(body).getBytes(StandardCharsets.UTF_8);
        assertThrows(XmlValidationException.class, () -> validator.validate(xml, schema, null));
    }
}
