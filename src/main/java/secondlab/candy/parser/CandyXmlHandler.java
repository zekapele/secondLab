package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CandyXmlHandler {
    private final List<Candy> candies = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private CandyAssembler assembler;

    public void startElement(String name, Map<String, String> attributes) {
        text.setLength(0);
        switch (name) {
            case "candy" -> {
                assembler = new CandyAssembler();
                assembler.setId(attributes.get("id"));
            }
            case "type" -> {
                requireAssembler();
                assembler.setFilling(attributes.get("filling"));
            }
            default -> {
            }
        }
    }

    public void characters(String chunk) {
        if (chunk != null) {
            text.append(chunk);
        }
    }

    public void endElement(String name) {
        switch (name) {
            case "candy" -> {
                requireAssembler();
                candies.add(assembler.build());
                assembler = null;
            }
            case "Candy", "type", "ingredients", "value" -> {
            }
            default -> {
                if (!CandyAssembler.isLeaf(name)) {
                    throw new CandyXmlException("Unexpected element: " + name);
                }
                requireAssembler();
                assembler.setField(name, text.toString());
            }
        }
    }

    public List<Candy> result() {
        return List.copyOf(candies);
    }

    private void requireAssembler() {
        if (assembler == null) {
            throw new CandyXmlException("Element is outside candy");
        }
    }
}
