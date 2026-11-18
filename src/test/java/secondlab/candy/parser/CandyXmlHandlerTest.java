package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.CandyKind;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CandyXmlHandlerTest {
    @Test
    void buildsACandyFromElementEvents() {
        CandyXmlHandler handler = new CandyXmlHandler();
        handler.startElement("Candy", Map.of());
        handler.startElement("candy", Map.of("id", "sample"));
        leaf(handler, "name", "Ірис");
        leaf(handler, "energy", "10");
        handler.startElement("type", Map.of("filling", "false"));
        leaf(handler, "kind", "iris");
        handler.endElement("type");
        handler.startElement("ingredients", Map.of());
        leaf(handler, "water", "1");
        leaf(handler, "sugar", "2");
        leaf(handler, "fructose", "3");
        leaf(handler, "vanillin", "4");
        handler.endElement("ingredients");
        handler.startElement("value", Map.of());
        leaf(handler, "proteins", "1.00");
        leaf(handler, "fats", "2.00");
        leaf(handler, "carbohydrates", "3.00");
        handler.endElement("value");
        leaf(handler, "production", "Plant");
        handler.endElement("candy");
        handler.endElement("Candy");

        var candy = handler.result().getFirst();
        assertEquals("sample", candy.id());
        assertEquals(CandyKind.IRIS, candy.type().kind());
    }

    @Test
    void rejectsEventsOutsideCandy() {
        CandyXmlHandler handler = new CandyXmlHandler();
        assertThrows(CandyXmlException.class, () -> handler.endElement("name"));
    }

    private static void leaf(CandyXmlHandler handler, String name, String value) {
        handler.startElement(name, Map.of());
        handler.characters(value);
        handler.endElement(name);
    }
}
