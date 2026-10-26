package secondlab.candy.transform;

import secondlab.candy.Resources;
import secondlab.candy.error.CandyXmlException;
import secondlab.candy.xml.XmlFactories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CandyTransformerTest {
    private final CandyTransformer transformer = new CandyTransformer();

    @Test
    void groupsCandiesByKind() throws Exception {
        String grouped = transformer.transform(
                new ByteArrayInputStream(Resources.bytes("xml/candies.xml")),
                new ByteArrayInputStream(Resources.bytes("xsl/candies-by-type.xsl")));
        DocumentBuilder builder = XmlFactories.documentBuilderFactory().newDocumentBuilder();
        Element root = builder.parse(new ByteArrayInputStream(grouped.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
        assertEquals("Candy", root.getNodeName());

        List<String> groups = new ArrayList<>();
        NodeList children = root.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            if (children.item(index) instanceof Element element) {
                groups.add(element.getTagName());
                assertEquals(element.getTagName(), kindOf(element));
            }
        }
        assertEquals(List.of("caramel", "chocolate", "iris"), groups);
        assertEquals(List.of("romashka", "duchess"), ids(root, "caramel"));
        assertEquals(List.of("vk", "strila", "ptashyne"), ids(root, "chocolate"));
        assertEquals(List.of("bilochka", "korivka"), ids(root, "iris"));
    }

    @Test
    void writesGroupedFile() throws Exception {
        Path output = Files.createTempFile("candies-by-type", ".xml");
        transformer.transform(Resources.path("xml/candies.xml"), Resources.path("xsl/candies-by-type.xsl"), output);
        String text = Files.readString(output);
        assertEquals(true, text.contains("<caramel") && text.contains("<chocolate") && text.contains("<iris"));
    }

    @Test
    void rejectsBrokenStylesheetAndMissingFiles(@TempDir Path directory) throws Exception {
        Path xml = directory.resolve("candies.xml");
        Path xsl = directory.resolve("broken.xsl");
        Files.writeString(xml, "<Candy/>");
        Files.writeString(xsl, "<xsl:stylesheet/>");
        assertThrows(CandyXmlException.class, () -> transformer.transform(xml, xsl, directory.resolve("out.xml")));
        byte[] catalog = Resources.bytes("xml/candies.xml");
        assertThrows(CandyXmlException.class, () -> transformer.transform(
                new ByteArrayInputStream(catalog),
                new ByteArrayInputStream("<xsl:stylesheet/>".getBytes(StandardCharsets.UTF_8))));
        assertThrows(CandyXmlException.class, () -> transformer.transform(
                Path.of("missing.xml"), Path.of("missing.xsl"), Path.of("target", "out.xml")));
    }

    private static String kindOf(Element group) {
        return group.getElementsByTagName("kind").item(0).getTextContent();
    }

    private static List<String> ids(Element root, String groupName) {
        Element group = null;
        NodeList children = root.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            if (children.item(index) instanceof Element element && groupName.equals(element.getTagName())) {
                group = element;
            }
        }
        NodeList candies = group.getElementsByTagName("candy");
        List<String> ids = new ArrayList<>();
        for (int index = 0; index < candies.getLength(); index++) {
            ids.add(((Element) candies.item(index)).getAttribute("id"));
        }
        return ids;
    }
}
