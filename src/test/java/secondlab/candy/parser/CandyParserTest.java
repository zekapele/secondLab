package secondlab.candy.parser;

import secondlab.candy.ExpectedCatalog;
import secondlab.candy.Resources;
import secondlab.candy.XmlSnippets;
import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandyParserTest {
    @BeforeAll
    static void enableParserLogs() {
        Logger.getLogger("secondlab.candy").setLevel(Level.FINE);
    }

    @ParameterizedTest
    @EnumSource(ParserKind.class)
    void parsesTheCatalog(ParserKind kind) {
        assertEquals(ExpectedCatalog.candies(), parse(kind, Resources.bytes("xml/candies.xml")));
    }

    @ParameterizedTest
    @EnumSource(ParserKind.class)
    void parsersAgreeOnDocumentOrder(ParserKind kind) {
        List<Candy> parsed = parse(kind, Resources.bytes("xml/candies.xml"));
        assertEquals(List.of("vk", "romashka", "bilochka", "strila", "duchess", "korivka", "ptashyne"),
                parsed.stream().map(Candy::id).toList());
    }

    @Test
    void acceptsBooleanDigitsAndCdata() {
        String xml = XmlSnippets.document("""
                <candy id="digit">
                    <name><![CDATA[Ірис]]></name>
                    <energy>10</energy>
                    <type filling="1">
                        <kind>chocolate</kind>
                    </type>
                    <ingredients>
                        <water>0</water>
                        <sugar>0</sugar>
                        <fructose>0</fructose>
                        <chocolateType></chocolateType>
                        <vanillin>0</vanillin>
                    </ingredients>
                    <value>
                        <proteins>0</proteins>
                        <fats>0.0</fats>
                        <carbohydrates>100</carbohydrates>
                    </value>
                    <production>Plant</production>
                </candy>
                """);
        Candy candy = parse(ParserKind.STAX, xml.getBytes(StandardCharsets.UTF_8)).getFirst();
        assertEquals("Ірис", candy.name());
        assertTrue(candy.type().filling());
        assertNull(candy.ingredients().chocolateType());
    }

    @ParameterizedTest
    @EnumSource(ParserKind.class)
    void rejectsMalformedXml(ParserKind kind) {
        byte[] xml = "<Candy>".getBytes(StandardCharsets.UTF_8);
        assertThrows(CandyXmlException.class, () -> parse(kind, xml));
    }

    @ParameterizedTest
    @EnumSource(ParserKind.class)
    void rejectsIncompleteCandy(ParserKind kind) {
        byte[] xml = XmlSnippets.document(XmlSnippets.candy("id=\"sample\"", "400", "caramel", "")
                        .replace("<energy>400</energy>", ""))
                .getBytes(StandardCharsets.UTF_8);
        CandyXmlException exception = assertThrows(CandyXmlException.class, () -> parse(kind, xml));
        assertTrue(exception.getMessage().contains("energy"));
    }

    @Test
    void rejectsUnknownKindFillingAndNumbers() {
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, document("id=\"sample\"", "400", "gummy", "")));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM, document("id=\"sample\"", "nope", "caramel", "")));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.STAX, xml("id=\"sample\"", "400", "caramel", "")
                .replace("filling=\"false\"", "filling=\"maybe\"").getBytes(StandardCharsets.UTF_8)));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, xml("id=\"sample\"", "400", "caramel", "")
                .replace("<proteins>1.00</proteins>", "<proteins>x</proteins>").getBytes(StandardCharsets.UTF_8)));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM, document("id=\"1bad\"", "400", "caramel", "")));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.STAX, xml("id=\"sample\"", "400", "caramel", "")
                .replace("<sugar>2</sugar>", "<sugar>100001</sugar>").getBytes(StandardCharsets.UTF_8)));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, document("", "400", "caramel", "")));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM,
                XmlSnippets.document("<candy id=\"sample\"><extra>1</extra></candy>").getBytes(StandardCharsets.UTF_8)));
        byte[] nested = XmlSnippets.document(
                "<candy id=\"sample\"><ingredients><extra>1</extra></ingredients></candy>").getBytes(StandardCharsets.UTF_8);
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, nested));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM, nested));
        byte[] outside = XmlSnippets.document("<type filling=\"false\"/><name>Outside</name>").getBytes(StandardCharsets.UTF_8);
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, outside));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.STAX, outside));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM,
                xml("id=\"sample\"", "400", "caramel", "").replace("filling=\"false\"", "filling=\"0\"")
                        .replace("<kind>caramel</kind>", "<kind></kind>")
                        .getBytes(StandardCharsets.UTF_8)));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.SAX, brokenStream()));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.DOM, brokenStream()));
        assertThrows(CandyXmlException.class, () -> parse(ParserKind.STAX, brokenStream()));
    }

    private static InputStream brokenStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("disk");
            }
        };
    }

    private static String xml(String attributes, String energy, String kind, String tail) {
        return XmlSnippets.document(XmlSnippets.candy(attributes, energy, kind, tail));
    }

    private static byte[] document(String attributes, String energy, String kind, String tail) {
        return xml(attributes, energy, kind, tail).getBytes(StandardCharsets.UTF_8);
    }

    private static List<Candy> parse(ParserKind kind, byte[] xml) {
        return parse(kind, new ByteArrayInputStream(xml));
    }

    private static List<Candy> parse(ParserKind kind, InputStream xml) {
        return kind.create().parse(xml);
    }
}
